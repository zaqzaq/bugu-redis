/*
 * Copyright (c) www.bugull.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bugull.redis.mq;

import com.bugull.redis.RedisConnection;
import com.bugull.redis.utils.Constant;
import com.bugull.redis.exception.RedisException;
import com.bugull.redis.listener.FileBroadcastListener;
import com.bugull.redis.listener.FileQueueListener;
import com.bugull.redis.listener.FileListener;
import com.bugull.redis.message.FileBroadcastMessage;
import com.bugull.redis.message.FileMessage;
import com.bugull.redis.task.GetFileDataTask;
import com.bugull.redis.task.SubscribeTopicTask;
import com.bugull.redis.utils.JedisUtil;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * MQ client to transfer files.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class FileClient extends AbstractClient {
    
    private FileListener fileListener;
    private FileBroadcastListener broadcastListener;
    
    public FileClient(JedisPool pool){
        this.pool = pool;
    }
    
    public void setFileListener(FileListener fileListener){
        this.fileListener = fileListener;
        MQClient client = RedisConnection.getInstance().getMQClient();
        client.consume(new FileQueueListener(fileListener), Constant.FILE_CLIENT + RedisConnection.getInstance().getClientId());
    }
    
    public String requestSendFile(String toClientId, Map<String, String> extras) throws RedisException {
        String fileId = UUID.randomUUID().toString();
        FileMessage fm = new FileMessage();
        fm.setFromClientId(RedisConnection.getInstance().getClientId());
        fm.setType(Constant.FILE_REQUEST);
        fm.setFileId(fileId);
        fm.setExtras(extras);
        MQClient client = RedisConnection.getInstance().getMQClient();
        client.produce(Constant.FILE_CLIENT + toClientId, Constant.FILE_MSG_TIMEOUT, fm.toString().getBytes());
        return fileId;
    }
    
    public void sendFileData(String fileId, byte[] data) throws RedisException {
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            byte[] queue = (Constant.FILE_CHUNKS + fileId).getBytes();
            jedis.lpush(queue, data);
        }catch(Exception ex){
            throw new RedisException(ex.getMessage(), ex);
        }finally{
            JedisUtil.returnToPool(pool, jedis);
        }
    }
    
    public void sendEndOfFile(String fileId) throws RedisException {
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            byte[] queue = (Constant.FILE_CHUNKS + fileId).getBytes();
            jedis.lpush(queue, Constant.EOF_MESSAGE.getBytes());
        }catch(Exception ex){
            throw new RedisException(ex.getMessage(), ex);
        }finally{
            JedisUtil.returnToPool(pool, jedis);
        }
    }
    
    public void acceptReceiveFile(String toClientId, String fileId, Map<String, String> extras) throws RedisException {
        //send accept message
        FileMessage fm = new FileMessage();
        fm.setFromClientId(RedisConnection.getInstance().getClientId());
        fm.setType(Constant.FILE_ACCEPT);
        fm.setFileId(fileId);
        fm.setExtras(extras);
        MQClient client = RedisConnection.getInstance().getMQClient();
        client.produce(Constant.FILE_CLIENT + toClientId, Constant.FILE_MSG_TIMEOUT, fm.toString().getBytes());

        //start a thread to receive file data
        GetFileDataTask task = new GetFileDataTask(fileListener, pool, fileId);
        new Thread(task).start();
    }
    
    public void rejectReceiveFile(String toClientId, String fileId, Map<String, String> extras) throws RedisException {
        //send reject message;
        FileMessage fm = new FileMessage();
        fm.setFromClientId(RedisConnection.getInstance().getClientId());
        fm.setType(Constant.FILE_REJECT);
        fm.setFileId(fileId);
        fm.setExtras(extras);
        MQClient client = RedisConnection.getInstance().getMQClient();
        client.produce(Constant.FILE_CLIENT + toClientId, Constant.FILE_MSG_TIMEOUT, fm.toString().getBytes());
    }
    
    public void setFileBroadcastListener(FileBroadcastListener broadcastListener){
        this.broadcastListener = broadcastListener;
    }
    
    public void subscribeFileBroadcast(String... topics) {
        for(String topic : topics){
            ExecutorService es = topicServices.get(topic);
            if(es == null){
                //use single thread executor to make sure the thread never crash
                es = Executors.newSingleThreadExecutor();
                ExecutorService temp = topicServices.putIfAbsent(topic, es);
                if(temp == null){
                    SubscribeTopicTask task = new SubscribeTopicTask(broadcastListener, pool, topic.getBytes());
                    es.execute(task);
                    blockedTasks.putIfAbsent(topic, task);
                }
            }
            broadcastListener.addTimer(topic);
        }
    }
    
    public void unsubscribeFileBroadcast(String... topics) throws RedisException {
        for(String topic : topics){
            try{
                broadcastListener.unsubscribe(topic.getBytes());
            }catch(Exception ex){
                throw new RedisException(ex.getMessage(), ex);
            }
            broadcastListener.removeTimer(topic);
            super.stopTopicTask(topic);
        }
    }
    
    public String startBroadcastFile(String topic, Map<String, String> extras) throws RedisException {
        String fileId = UUID.randomUUID().toString();
        FileBroadcastMessage fbm = new FileBroadcastMessage();
        fbm.setType(Constant.BROADCAST_START);
        fbm.setFileId(fileId);
        fbm.setExtras(extras);
        byte[] message = fbm.toBytes();
        MQClient client = RedisConnection.getInstance().getMQClient();
        client.publish(topic, message);
        return fileId;
    }
    
    public void endBroadcastFile(String topic, String fileId) throws RedisException {
        FileBroadcastMessage fbm = new FileBroadcastMessage();
        fbm.setType(Constant.BROADCAST_END);
        fbm.setFileId(fileId);
        byte[] message = fbm.toBytes();
        MQClient client = RedisConnection.getInstance().getMQClient();
        client.publish(topic, message);
    }
    
    public void broadcastFileData(String topic, String fileId, byte[] data) throws RedisException {
        FileBroadcastMessage fbm = new FileBroadcastMessage();
        fbm.setType(Constant.BROADCAST_DATA);
        fbm.setFileId(fileId);
        fbm.setFileData(data);
        byte[] message = fbm.toBytes();
        MQClient client = RedisConnection.getInstance().getMQClient();
        client.publish(topic, message);
    }
    
    public void stopAllFileBroadcastTask(){
        super.stopAllTopicTask();
        if(broadcastListener != null){
            broadcastListener.closeAllTimer();
        }
    }

}
