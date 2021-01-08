package com.eternalsrv.utils.configs;

import com.eternalsrv.models.SampleConfigs;
import com.google.gson.Gson;

import java.io.IOException;

public class ConfigUtils extends CoreConfigUtils {

    public static SampleConfigs getSampleConfigs(String fileName) throws IOException {
        ConfigParser configParser = new ConfigParser();
        Gson gson = new Gson();
        return gson.fromJson(configParser.getConfigsAsJsonString(fileName), SampleConfigs.class);
    }
}
