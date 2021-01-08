package com.eternalsrv.utils.asynctasks;

import android.os.AsyncTask;

import com.eternalsrv.utils.Config;
import com.eternalsrv.utils.constant.ServerMethodsConsts;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class UpdateQbIdToDB extends AsyncTask<String, Void, String> {
    Config config = new Config();
    JSONObject json = new JSONObject();

    @Override
    protected String doInBackground(String... strings) {
        // 0 - idfb
        // 1 - idqb
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(config.getServer() + ServerMethodsConsts.UPDATEQBIDTODB);
        HttpResponse response = null;

        try {
            json.put("idFb", strings[0]);
            json.put("idQb", strings[1]);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("data", json.toString()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
            response = httpclient.execute(httppost);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
