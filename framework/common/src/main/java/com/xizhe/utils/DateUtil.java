package com.xizhe.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author admin
 * @version 1.0
 * @description: TODO
 * @date 2023/9/8 18:15
 */

public class DateUtil {
    public static Date get(String pattern) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date parse = sdf.parse(pattern);
            return parse;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
