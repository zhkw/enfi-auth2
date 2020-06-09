package com.cisdi.cpm.auth.utils;

import com.cisdi.cpm.auth.helper.cachehelper.MapCacheOper;

import java.util.HashMap;
import java.util.Map;

public class TableUtils {
    //对Map<String, String>格式的
    public static void buildTableColumn(Map<String, String> table, String prefix) {
        for (Map.Entry<String, String> entity : table.entrySet()) {
            if (entity.getValue() != null && !"".equals(entity.getValue())) {
                if (!"tableName".equals(entity.getKey())) {
                    if (prefix != null) {
                        table.put(entity.getKey(), "," + prefix + "." + entity.getValue());
                    } else {
                        table.put(entity.getKey(), "," + entity.getValue());
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> getTableFromCache(String tableName) {
        Map<String, Map<String, String>> tables = (Map<String, Map<String, String>>) MapCacheOper.getValueFromKey("tables");
        //person表
        Map<String, String> personTable = null;
        if (tables != null) {
            personTable = tables.get(tableName);
        }

        return (Map<String, String>) ((HashMap<String, String>) personTable).clone();

    }
}

