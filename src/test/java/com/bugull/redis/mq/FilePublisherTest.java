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
import java.io.FileInputStream;
import java.util.Arrays;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class FilePublisherTest {
    
    @Test
    public void testPublish() throws Exception {
        RedisConnection conn = RedisConnection.getInstance();
        conn.setHost("192.168.0.200");
        conn.setPassword("foobared");
        conn.setClientId("publisher");
        conn.connect();
        
        FileClient client = conn.getFileClient();
        
        String topic = "file_broadcast";
        String fileId = client.startBroadcastFile(topic, null);
        String filePath = "/Users/frankwen/send/redis.pdf";
        FileInputStream fis = new FileInputStream(filePath);
        int chunkSize = 16 * 1024;
        byte[] chunkData = new byte[chunkSize];
        int bytesRead = fis.read(chunkData, 0, chunkSize);
        while(bytesRead > 0){
            if(bytesRead == chunkSize){
                client.broadcastFileData(topic, fileId, chunkData);
            }else{
                client.broadcastFileData(topic, fileId, Arrays.copyOfRange(chunkData, 0, bytesRead));
            }
            bytesRead = fis.read(chunkData, 0, chunkSize);
        }
        client.endBroadcastFile(topic, fileId);
        
        Thread.sleep(10L * 1000L);
        
        conn.disconnect();
    }
    

}
