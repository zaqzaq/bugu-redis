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

package com.bugull.redis.listener;

import com.bugull.redis.message.FileMessage;
import com.bugull.redis.utils.Constant;
import com.bugull.redis.utils.JedisUtil;
import java.util.Map;

/**
 * A QueueListener used for transfer files.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class FileQueueListener extends QueueListener {
    
    private FileListener fileListener;
    
    public FileQueueListener(FileListener fileListener){
        this.fileListener = fileListener;
    }
    
    @Override
    public void onQueueMessage(String queue, byte[] message) {
        if(JedisUtil.isEmpty(message)){
            return;
        }
        String s = new String(message);
        FileMessage fm = FileMessage.parse(s);
        String fromClientId = fm.getFromClientId();
        String fileId = fm.getFileId();
        Map<String,String> extras = fm.getExtras();
        int type = fm.getType();
        switch(type){
            case Constant.FILE_REQUEST:
                fileListener.onRequest(fromClientId, fileId, extras);
                break;
            case Constant.FILE_ACCEPT:
                fileListener.onAccept(fromClientId, fileId, extras);
                break;
            case Constant.FILE_REJECT:
                fileListener.onReject(fromClientId, fileId, extras);
                break;
            default:
                break;
        }
    }

}
