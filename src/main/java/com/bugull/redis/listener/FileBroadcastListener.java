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

import com.bugull.redis.message.FileBroadcastMessage;
import com.bugull.redis.utils.Constant;
import java.util.Map;

/**
 *
 * @author Frank Wen(xbwen@hotmail.com)
 */
public abstract class FileBroadcastListener extends TopicListener {
    
    public abstract void onFileStart(String topic, String fileId, Map<String,String> extras);
    
    public abstract void onFileEnd(String topic, String fileId);
    
    public abstract void onFileData(String topic, String fileId, byte[] data);

    @Override
    public void onTopicMessage(String topic, byte[] message) {
        if(message.length <37){  //1 byte for type, 36 bytes for fileId(UUID)
            return;
        }
        FileBroadcastMessage fbm = FileBroadcastMessage.parse(message);
        byte type = fbm.getType();
        String fileId = fbm.getFileId();
        if(type==Constant.BROADCAST_START){
            onFileStart(topic, fileId, fbm.getExtras());
        }
        else if(type==Constant.BROADCAST_END){
            onFileEnd(topic, fileId);
        }
        else if(type==Constant.BROADCAST_DATA){
            onFileData(topic, fileId, fbm.getFileData());
        }
    }

}
