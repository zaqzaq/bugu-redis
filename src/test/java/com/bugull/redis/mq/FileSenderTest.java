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

import com.bugull.redis.Connection;
import com.bugull.redis.listener.FileListener;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class FileSenderTest {
    
    @Test
    public void testSendFile() throws Exception {
        Connection conn = Connection.getInstance();
        conn.setHost("192.168.0.200");
        conn.setPassword("foobared");
        conn.setClientId("sender");
        conn.connect();
        
        FileClient client = conn.getFileClient();
        client.setFileListener(new SendFileListener());
        
        Map<String, String> extras = new HashMap<String, String>();
        extras.put("filePath", "/Users/frankwen/send/redis.pdf");
        client.requestSendFile("receiver", extras);
        
        Thread.sleep(30 * 1000);
    }
    
    class SendFileListener extends FileListener{
        @Override
        public void onRequest(String fromClientId, String fileId, Map<String,String> extras){
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void onAccept(String fromClientId, String fileId, Map<String,String> extras) {
            String filePath = extras.get("filePath");
            try{
                FileClient client = Connection.getInstance().getFileClient();
                FileInputStream fis = new FileInputStream(filePath);
                int chunkSize = 16 * 1024;
                byte[] chunkData = new byte[chunkSize];
                int bytesRead = fis.read(chunkData, 0, chunkSize);
                while(bytesRead > 0){
                    if(bytesRead == chunkSize){
                        client.sendFileData(fileId, chunkData);
                    }else{
                        client.sendFileData(fileId, Arrays.copyOfRange(chunkData, 0, bytesRead));
                    }
                    bytesRead = fis.read(chunkData, 0, chunkSize);
                }
                client.sendEndOfFile(fileId);
            }catch(Exception ex){
                
            }
        }

        @Override
        public void onReject(String fromClientId, String fileId, Map<String,String> extras) {
            System.out.println(fromClientId + " refuse to received file");
        }

        @Override
        public void onFileData(String fileId, byte[] data) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void onFileEnd(String fileId) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
        @Override
        public void onError(String fileId){
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }

}
