package com.eternalsrv.utils.configs;

import android.content.Context;

import com.eternalsrv.App;
import com.eternalsrv.utils.AssetsUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ConfigParser {

    private Context context;

    public ConfigParser() {
        context = App.getInstance().getApplicationContext();
    }

    public String getConfigsAsJsonString(String fileName) throws IOException {
        return AssetsUtils.getJsonAsString(fileName, context);
    }

    public JSONObject getConfigsAsJson(String fileName) throws IOException, JSONException {
        return new JSONObject(getConfigsAsJsonString(fileName));
    }

    public String getConfigByName(JSONObject jsonObject, String fieldName) throws JSONException {
        return jsonObject.getString(fieldName);
    }
}
