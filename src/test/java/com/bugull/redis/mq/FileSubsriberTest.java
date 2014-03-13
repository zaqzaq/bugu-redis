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
import com.bugull.redis.listener.FileBroadcastListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class FileSubsriberTest {
    
    @Test
    public void testSubscribe() throws Exception {
        RedisConnection conn = RedisConnection.getInstance();
        conn.setHost("192.168.0.200");
        conn.setPassword("foobared");
        conn.setClientId("subscriber");
        conn.connect();
        
        FileClient client = conn.getFileClient();
        
        FileBroadcastListener listener = new MyFileBroadcastListener();
        
        client.setFileBroadcastListener(listener);
        
        client.subscribeFileBroadcast("file_broadcast");
        
        Thread.sleep(60L * 1000L);
        
        conn.disconnect();
    }
    
    class MyFileBroadcastListener extends FileBroadcastListener{
        
        private FileOutputStream fos;
        
        @Override
        public void onFileStart(String topic, String fileId, Map<String, String> extras) {
            try{
                File file = new File("/Users/frankwen/receive/new_file.pdf");
                file.createNewFile();
                fos = new FileOutputStream(file);
            }catch(IOException ex){
                
            }
        }

        @Override
        public void onFileEnd(String topic, String fileId) {
            try{
                fos.flush();
                fos.close();
            }catch(IOException ex){
                
            }
        }

        @Override
        public void onFileData(String topic, String fileId, byte[] data) {
            try{
                fos.write(data);
            }catch(IOException ex){
                
            }
        }
        
    }

}
