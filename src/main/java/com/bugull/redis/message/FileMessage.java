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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Message used for transfer files.
 * 
 * @author Frank Wen(xbwen@hotmail.com)
 */
public class FileMessage {
    
    private String fromClientId;
    private int type;
    private String fileId;
    private Map<String, String> extras;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFromClientId() {
        return fromClientId;
    }

    public void setFromClientId(String fromClientId) {
        this.fromClientId = fromClientId;
    }

    public Map<String, String> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, String> extras) {
        this.extras = extras;
    }
    
    public static FileMessage parse(String s){
        String[] arr = s.split(Constant.SPLIT_MESSAGE);
        FileMessage fm = new FileMessage();
        fm.setFromClientId(arr[0]);
        fm.setType(Integer.parseInt(arr[1]));
        fm.setFileId(arr[2]);
        Map<String, String> map = new HashMap<String, String>();
        for(int i=3; i<arr.length; i++){
            String extra = arr[i];
            String[] kv = extra.split(Constant.SPLIT_EXTRA);
            map.put(kv[0], kv[1]);
        }
        fm.setExtras(map);
        return fm;
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(fromClientId);
        sb.append(Constant.SPLIT_MESSAGE);
        sb.append(type);
        sb.append(Constant.SPLIT_MESSAGE);
        sb.append(fileId);
        sb.append(Constant.SPLIT_MESSAGE);
        if(extras != null){
            Set<Entry<String, String>> set = extras.entrySet();
            for(Entry<String, String> entry : set){
                sb.append(entry.getKey()).append(Constant.SPLIT_EXTRA).append(entry.getValue());
                sb.append(Constant.SPLIT_MESSAGE);
            }
        }
        String s = sb.toString();
        return s.substring(0, s.length() - Constant.SPLIT_MESSAGE.length());
    }

}
