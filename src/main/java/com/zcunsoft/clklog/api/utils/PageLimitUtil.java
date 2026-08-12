package com.zcunsoft.clklog.api.utils;

/**
 * 分页 limit 安全拼接，防止超大 pageSize。
 */
public final class PageLimitUtil {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 500;

    private PageLimitUtil() {
    }

    public static int safePageSize(int pageSize) {
        if (pageSize < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }

    public static int safePageNum(int pageNum) {
        return pageNum < 1 ? 1 : pageNum;
    }

    /**
     * @return 形如 {@code  limit 0,10}（含前导空格）
     */
    public static String limitClause(int pageNum, int pageSize) {
        int size = safePageSize(pageSize);
        int num = safePageNum(pageNum);
        return " limit " + (num - 1) * size + "," + size;
    }
}
