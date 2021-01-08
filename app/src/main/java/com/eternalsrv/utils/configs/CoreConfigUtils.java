package com.eternalsrv.utils.configs;


import com.eternalsrv.models.QbConfigs;
import com.google.gson.Gson;

import java.io.IOException;

public class CoreConfigUtils {

    public static final String USER_LOGIN_FIELD_NAME = "user_login";
    public static final String USER_PASSWORD_FIELD_NAME = "user_password";

    public static QbConfigs getCoreConfigs(String fileName) throws IOException {
        ConfigParser configParser = new ConfigParser();
        Gson gson = new Gson();
        return gson.fromJson(configParser.getConfigsAsJsonString(fileName), QbConfigs.class);
    }

    public static QbConfigs getCoreConfigsOrNull(String fileName){
        QbConfigs qbConfigs = null;

        try {
            qbConfigs = getCoreConfigs(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return qbConfigs;
    }
}
