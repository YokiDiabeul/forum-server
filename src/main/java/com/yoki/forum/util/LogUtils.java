package com.yoki.forum.util;

import org.slf4j.Logger;

public class LogUtils {

    private static final String START = "start";

    private LogUtils() {}

    public static void start(StringBuilder sb, String nom, Logger l){
        sb.append(StringUtils.inBracket(nom));
        l.info(sb + StringUtils.SPACE + START);
    }

    public static StringBuilder reset(String prefix) {
        return new StringBuilder(prefix);
    }

    public static void info(String nom, Logger l) {
        l.info(StringUtils.inBracket(nom) + StringUtils.SPACE + START);
    }

}