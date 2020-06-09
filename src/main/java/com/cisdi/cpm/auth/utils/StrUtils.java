package com.cisdi.cpm.auth.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

import java.io.Reader;
import java.sql.Clob;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrUtils {
    private static final Logger logger = LoggerFactory.getLogger(StrUtils.class);

    /**
     * Get formatted date string
     *
     * @param format
     * @return
     */
    public static String getDateStr(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    /**
     * Get date string like YYYYMMddHHmmss
     *
     * @return
     * @throws BaseException
     */
    public static String getDateStr() {
        return getDateStr("YYYYMMddHHmmss");
    }

    /**
     * Get UUID
     *
     * @return
     * 		GUID
     * @throws BaseException
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().toUpperCase();
    }

    /**
     * Byte conversion, return K,M,G.
     *
     * @param size
     * @return
     */
    public static String getUnitSize(long size) {
        long k = size / 1024;
        if (k < 1) {
            return size + "Byte";
        }
        else {
            long m = k / 1024;
            if (m < 1) {
                return k + "KB";
            }
            else {
                long g = m / 1024;
                if (g < 1) {
                    return m + "MB";
                }
                else {
                    return g + "GB";
                }
            }
        }
    }

    /**
     * Clob type to string
     *
     * @param clob
     * @return
     * @throws BaseException
     */
    public static String clob2Str(Clob clob) {
        if (clob == null) {
            return "";
        }
        Reader stream = null;
        String str = "";
        try {
            stream = clob.getCharacterStream();
            char[] c = new char[(int) clob.length()];
            stream.read(c);
            str = new String(c);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("clob conversion error!");
        }

        return str;
    }

    /**
     * Encode byte array with Base64
     *
     * @param bytes
     * 		byte array
     * @return
     * 		base64 string
     * @throws BaseException
     */
    public static String Base64Encode(byte[] bytes) {
        BASE64Encoder be = new BASE64Encoder();
        return be.encode(bytes);
    }

    /**
     * Regular expressions validation
     *
     * @param regex
     * 	regular expression
     * @param str
     * @return
     */
    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        return matcher.matches();
    }

    /**
     * Positive number validation
     *
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        String regex = "^([1-9]\\d*\\.\\d*|0\\.\\d+|[1-9]\\d*|0)$";

        return match(regex,str);
    }

    public static boolean isEmpty(String str) {
        Pattern pat = Pattern.compile("\\s*");
        return pat.matcher(str).matches();
    }

    /**
     * Generate random number
     * @param size
     * @return
     * @throws BaseException
     */
    public static String generateRandom(int size) {
        String randomStr = "";
        for (int i = 0; i < size; i++) {
            Double d = Math.random() * 10;
            randomStr += d.intValue();
        }

        return randomStr;
    }

    /**
     * ��Listת����SQL��in�Ӿ������
     * Convert List into string, format like "in (?,?)"
     *
     * @param dataList
     * @return
     * @throws BaseException
     */
    public static String list2SQLCondition(List<String> dataList) {
        if (dataList == null || dataList.size() == 0) {
            return "('')";
        }
        String condition = "(";
        int num = 0;
        int size = dataList.size();
        for (String data : dataList) {
            num++;
            condition = condition + "'" + data + "',";
            if (num == size) {
                condition = condition.substring(0, condition.length() - 1);
                condition += ")";
                break;
            }
        }

        return condition;
    }

}

