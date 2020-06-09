package com.cisdi.cpm.auth.common.data;

import java.util.HashMap;
import java.util.Map;

public class SessionInfo {
    private static final ThreadLocal<Map<String, Object>> SESSION_MAP =
            new ThreadLocal<Map<String, Object>>();

    /** * �������protected���췽��. */
    protected SessionInfo() {
    }

    /**
     * ����߳��б��������.
     *
     * @param attribute
     *            ��������
     * @return ����ֵ
     */
    public static Object get(String attribute) {
        Map<String, Object> map = (Map<String, Object>) SESSION_MAP.get();
        if (map == null) {
            return "";
        }

        return map.get(attribute);
    }

    /**
     * �����ƶ���������ֵ.
     *
     * @param attribute
     *            ��������
     * @param value
     *            ����ֵ
     */
    public static void set(String attribute, Object value) {
        Map<String, Object> map = (Map<String, Object>) SESSION_MAP.get();
        if (map == null) {
            map = new HashMap<String, Object>();
            SESSION_MAP.set(map);
        }

        map.put(attribute, value);
    }

    public static String getUserName() {
        return (String)get("userName");
    }

    public static String getUserId() {
        return (String)get("userId");
    }


}
