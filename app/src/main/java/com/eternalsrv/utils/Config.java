package com.eternalsrv.utils;

public class Config {
    private static final String server;

    static
    {
        server = "http://192.168.1.15:5000/";
    }
    public static String getServer() {
        return server;
    }
}
