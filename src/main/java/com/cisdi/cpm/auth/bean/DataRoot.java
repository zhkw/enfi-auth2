package com.cisdi.cpm.auth.bean;

import java.util.HashMap;
import java.util.Map;

public class DataRoot {
    protected Map<String, Object> dataMap = new HashMap<String, Object>();

    public void put(String key, Object value) {
        dataMap.put(key, value);
    }

    public Object get(String key) {
        return dataMap.get(key);
    }

    public Map<String, Object> getHashAttributes() {
        return this.dataMap;
    }

    public void setHashAttributes(Map<String, Object> hashAttributes) {
        this.dataMap = hashAttributes;
    }
}
