package com.eternalsrv.utils.asynctasks;

import org.json.JSONException;
import org.json.JSONObject;

public class AsyncTaskParams {
    JSONObject jsonObject;

    public AsyncTaskParams() {
        jsonObject = new JSONObject();
    }

    public void put(String key, int value) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void put(String key, double value) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void put(String key, long value) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void put(String key, String value) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void put(String key, boolean value) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void put(String key, Object value) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String toString() {
        return jsonObject.toString();
    }
}
