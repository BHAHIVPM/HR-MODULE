package com.bhahi.hrmodule.databasemapping;


public class DatabaseContextHolder {

    private DatabaseContextHolder(){}

    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    public static void set(String databaseKey) {
        CONTEXT.set(databaseKey);
    }

    public static String get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}