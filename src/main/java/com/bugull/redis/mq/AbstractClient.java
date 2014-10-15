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

import com.bugull.redis.utils.Constant;
import com.bugull.redis.exception.RedisException;
import com.bugull.redis.task.BlockedTask;
import com.bugull.redis.utils.JedisUtil;
import com.bugull.redis.utils.ThreadUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public abstract class AbstractClient {
    
    protected JedisPool pool;
    
    //store the blocked tasks, in order to stop it and close the jedis client.
    protected final ConcurrentMap<String, BlockedTask> blockedTasks = new ConcurrentHashMap<String, BlockedTask>();
    
    protected final ConcurrentMap<String, ExecutorService> topicServices = new ConcurrentHashMap<String, ExecutorService>();
    
    public boolean isOnline(String clientId) throws RedisException {
        boolean result = false;
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            result = jedis.exists(Constant.ONLINE + clientId);
        }catch(Exception ex){
            throw new RedisException(ex.getMessage(), ex);
        }finally{
            JedisUtil.returnToPool(pool, jedis);
        }
        return result;
    }
    
    public List<Boolean> isOnline(List<String> clientList) throws RedisException {
        List<Boolean> results = new ArrayList<Boolean>();
        List<Response<Boolean>> responseList = new ArrayList<Response<Boolean>>();
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            Pipeline p = jedis.pipelined();
            for(String clientId : clientList){
                responseList.add(p.exists(Constant.ONLINE + clientId));
            }
            p.sync();
        }catch(Exception ex){
            throw new RedisException(ex.getMessage(), ex);
        }finally{
            JedisUtil.returnToPool(pool, jedis);
        }
        for(Response<Boolean> response : responseList){
            results.add(response.get());
        }
        return results;
    }
    
    protected void stopTopicTask(String topic){
        BlockedTask task = blockedTasks.get(topic);
        if(task != null){
            task.setStopped(true);
            try{
                task.getJedis().disconnect();
            }catch(Exception ex){
                //ignore ex
            }
            blockedTasks.remove(topic);
        }
        ExecutorService es = topicServices.get(topic);
        if(es != null){
            topicServices.remove(topic);
            ThreadUtil.safeClose(es);
        }
    }
    
    protected void stopAllTopicTask(){
        Set<String> set = topicServices.keySet();
        for(String topic : set){
            stopTopicTask(topic);
        }
    }

}
