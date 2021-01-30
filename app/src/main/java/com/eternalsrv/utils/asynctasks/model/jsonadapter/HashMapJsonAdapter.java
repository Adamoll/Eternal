package com.eternalsrv.utils.asynctasks.model.jsonadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HashMapJsonAdapter extends TypeAdapter<HashMap<String, String>> {
    @Override
    public void write(JsonWriter out, HashMap<String, String> value) throws IOException {
        if (value == null)
            return;
        out.beginArray();
        for (Map.Entry<String, String> entry: value.entrySet()) {
            out.beginObject();
            out.name(entry.getKey()).value(entry.getValue());
            out.endObject();
        }
        out.endArray();
    }

    @Override
    public HashMap<String, String> read(JsonReader in) throws IOException {
        HashMap<String, String> hashMap = new HashMap<>();
        in.beginArray();
        while (in.hasNext()) {
            in.beginObject();
            String key = null;
            String value = null;
            while (in.hasNext()) {
                key = in.nextName();
                value = in.nextString();
            }
            hashMap.put(key, value);
            in.endObject();
        }
        in.endArray();
        return hashMap;
    }
}
