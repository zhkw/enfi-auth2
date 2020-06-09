package com.cisdi.cpm.auth.helper.cachehelper;

import java.util.HashMap;
import java.util.Map;

public class MapCacheOper {
    //缓存实体
    private static Map<String, Object> cache = new HashMap<String, Object>();

    /**
     * 添加值（key-value；key-List；key-set; key-map）
     */
    public static void setKeyValue(String key, Object value) {
        cache.put(key, value);
    }

    /**
     * 从缓存中获取Key的值
     * @param key
     * @return
     */
    public static Object getValueFromKey(String key) {
        return cache.get(key);
    }
}
