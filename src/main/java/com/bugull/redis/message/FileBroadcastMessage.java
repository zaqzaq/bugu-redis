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

package com.bugull.redis.message;

import com.bugull.redis.utils.Constant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Message used for boradcast files.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class FileBroadcastMessage {
    
    private byte type;
    private String fileId;
    private Map<String, String> extras;
    private byte[] fileData;

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Map<String, String> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, String> extras) {
        this.extras = extras;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }
    
    public static FileBroadcastMessage parse(byte[] message){
        FileBroadcastMessage fbm = new FileBroadcastMessage();
        byte theType = message[0];
        fbm.setType(theType);
        String theFileId = new String(Arrays.copyOfRange(message, 1, 37)); //fileId is 36 chars UUID
        fbm.setFileId(theFileId);
        int len = message.length;
        if(len>37){
            byte[] exData = Arrays.copyOfRange(message, 37, len);
            if(theType==Constant.BROADCAST_DATA){
                fbm.setFileData(exData);
            }
            else if(theType==Constant.BROADCAST_START){
                Map<String, String> map = new HashMap<String, String>();
                String exStr = new String(exData);
                String[] arr = exStr.split(Constant.SPLIT_MESSAGE);
                for(String s : arr){
                    String[] kv = s.split(Constant.SPLIT_EXTRA);
                    map.put(kv[0], kv[1]);
                }
                fbm.setExtras(map);
            }
        } 
        return fbm;
    }
    
    public byte[] toBytes(){
        byte[] fileIdData = fileId.getBytes();
        byte[] exData = null;
        if(type==Constant.BROADCAST_DATA){
            exData = fileData;
        }
        else if(type==Constant.BROADCAST_START && extras!=null){
            StringBuilder sb = new StringBuilder();
            Set<Map.Entry<String, String>> set = extras.entrySet();
            for(Map.Entry<String, String> entry : set){
                sb.append(entry.getKey()).append(Constant.SPLIT_EXTRA).append(entry.getValue());
                sb.append(Constant.SPLIT_MESSAGE);
            }
            String s = sb.toString();
            String data = s.substring(0, s.length() - Constant.SPLIT_MESSAGE.length());
            exData = data.getBytes();
        }
        byte[] result = null;
        if(exData == null){
            result = new byte[37];
        }else{
            int len = 37 + exData.length;
            result = new byte[len];
        }
        result[0] = type;
        System.arraycopy(fileIdData, 0, result, 1, 36);
        if(exData != null){
            System.arraycopy(exData, 0, result, 37, exData.length);
        }
        return result;
    }

}
