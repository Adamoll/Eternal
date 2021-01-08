package com.eternalsrv.utils.asynctasks;

import android.os.AsyncTask;

import com.eternalsrv.utils.QBUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class QBSession extends AsyncTask<String, Void, String> {

    /**
     * strings[0] - FBToken
     * @param strings
     * @return
     */
    @Override
    protected String doInBackground(String... strings) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("https://api.quickblox.com/session.json");
        HttpResponse response = null;

        try {
            httppost.setHeader("Content-Type", "application/json");
            httppost.setHeader("QuickBlox-REST-API-Version", "0.1.0");
            HttpEntity httpEntity = new StringEntity(QBUtils.getSignatureRequestData(strings[0]));
            httppost.setEntity(httpEntity);

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

