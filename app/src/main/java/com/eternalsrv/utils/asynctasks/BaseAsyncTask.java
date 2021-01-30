package com.eternalsrv.utils.asynctasks;

import android.os.AsyncTask;

import com.eternalsrv.App;
import com.eternalsrv.utils.Config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class BaseAsyncTask<T> extends AsyncTask<String, Void, String> {
    private String httpMethod;
    private T requestParams;
    private HttpURLConnection connection;

    public BaseAsyncTask(String urn, T params) {
        try {
            connection = (HttpURLConnection) new URL(Config.getServer() + urn).openConnection();
            requestParams = params;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String[] params) {
        try {
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoInput(true);
            if ("POST".equals(httpMethod) || "DELETE".equals(httpMethod)) {
                connection.setDoOutput(true);
                connection.setRequestMethod(httpMethod);

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(App.getGson().toJson(requestParams));
                writer.flush();
                writer.close();
                os.close();
            }

            connection.connect();

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                return sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }
}

