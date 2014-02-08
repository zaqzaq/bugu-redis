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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import org.junit.Test;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class FileReceiverTest {
    
    @Test
    public void testReceive() throws Exception {
        Connection conn = Connection.getInstance();
        conn.setHost("192.168.0.200");
        conn.setPassword("foobared");
        conn.setClientId("receiver");
        conn.connect();
        
        FileClient client = conn.getFileClient();
        
        client.setFileListener(new ReceiveFileListener());
        
        Thread.sleep(30 * 1000);
    }
    
    class ReceiveFileListener extends FileListener{
        
        private FileOutputStream fos;
        
        @Override
        public void onRequest(String fromClientId, String fileId, Map<String,String> extras){
            FileClient client = Connection.getInstance().getFileClient();
            try{
                client.acceptReceiveFile(fromClientId, fileId, extras);
                File file = new File("/Users/frankwen/receive/new_redis.pdf");
                file.createNewFile();
                fos = new FileOutputStream(file);
            }catch(Exception ex){
                
            }
        }

        @Override
        public void onAccept(String fromClientId, String fileId, Map<String,String> extras) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void onReject(String fromClientId, String fileId, Map<String,String> extras) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void onFileData(String fileId, byte[] data) {
            try{
                fos.write(data);
            }catch(IOException ex){
                
            }
        }

        @Override
        public void onFileEnd(String fileId) {
            try{
                fos.flush();
                fos.close();
            }catch(IOException ex){
                
            }
        }
        
        @Override
        public void onError(String fileId){
            try{
                fos.close();
            }catch(IOException ex){
                
            }
        }
        
    }

}
