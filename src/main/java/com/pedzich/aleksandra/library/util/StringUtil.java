package com.pedzich.aleksandra.library.util;

public class StringUtil {

    public static String getEntityNotFoundExceptionMessage(String entityName, Integer id) {
        return entityName + " with id: " + id + " doesn't exist.";
    }
}
