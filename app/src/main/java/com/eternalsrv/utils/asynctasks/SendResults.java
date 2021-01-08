package com.eternalsrv.utils.asynctasks;

import android.os.AsyncTask;

import com.eternalsrv.models.PersonalityTrait;
import com.eternalsrv.utils.Config;
import com.eternalsrv.utils.constant.ServerMethodsConsts;

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
import java.util.ArrayList;
import java.util.List;

public class SendResults extends AsyncTask<PersonalityTrait, Void, String> {
    Config config = new Config();
    JSONObject json = new JSONObject();
    String userID;

    public void setData(String id)
    {
        this.userID = id;
    }

    @Override
    protected String doInBackground(PersonalityTrait... params) {
        //params[0] - E
        //params[1] - I
        //params[2] - N
        //params[3] - S
        //params[4] - T
        //params[5] - J
        //params[6] - F
        //params[7] - P
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(config.getServer() + ServerMethodsConsts.TEST);
        HttpResponse response = null;

        try {
            json.put("id", userID);
            json.put("numberOfQuestions", 24);
            for (PersonalityTrait tr : params) {
                for (int i = 0; i < tr.getNumbersOfQuestions().length; i++) {
                    json.put("q" + tr.getNumbersOfQuestions()[i], tr.getAnswerPoints()[i]);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("data", json.toString()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));

            response = httpclient.execute(httppost);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {}
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
    }
}
