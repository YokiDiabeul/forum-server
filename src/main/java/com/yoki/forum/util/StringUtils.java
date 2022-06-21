package com.yoki.forum.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringUtils {

    public static final String DELIMITER = "/";
    public static final String SPACE = " ";

    private static final String OPEN_BRACKET = "[";
    private static final String CLOSE_BRACKET = "]";
    private static final String COMMA = ",";
    public static final String TIRET = "-";

    private StringUtils(){}

    public static String makePath(Object... objects) {
        return Arrays.stream(objects).map(Object::toString).collect(Collectors.joining(DELIMITER));
    }

    public static String inBracket(Object o) {
        return wrap(OPEN_BRACKET, CLOSE_BRACKET, o);
    }

    public static String inBracket(Object... os) {
        return wrap(OPEN_BRACKET, CLOSE_BRACKET, os);
    }

    public static String wrap(String w, Object... os) {
        return Arrays.stream(os).map(o -> wrap(w, o)).collect(Collectors.joining());
    }

    public static String wrap(String wo, String we, Object... os) {
        return Arrays.stream(os).map(o -> wrap(wo, o, we)).collect(Collectors.joining());
    }

    public static String wrap(String wrapper, Object o) {
        return new StringBuilder(wrapper).append(o).append(wrapper).toString();
    }

    public static String wrap(String wo, Object o, String we) {
        return new StringBuilder(wo).append(o).append(we).toString();
    }

    public static String toString(List<? extends Object> list) {
        return toString(list, COMMA + SPACE);
    }

    public static String toString(List<? extends Object> list, String delimiter) {
        return list.stream().map(o -> o.toString()).collect(Collectors.joining(delimiter));
    }

}
