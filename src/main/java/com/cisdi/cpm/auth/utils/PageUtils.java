package com.cisdi.cpm.auth.utils;

public class PageUtils {
    public static long getTotalPage(long totalCount, long pageSize) {
        return totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
    }
}
