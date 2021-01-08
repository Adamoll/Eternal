package com.eternalsrv.utils.asynctasks;

import android.os.AsyncTask;

import com.eternalsrv.utils.MyPreferences;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class QBRegisterUser extends AsyncTask<String, Void, String> {

    private String QBToken;
    private String login;
    private String password;
    private String email;
    private String externalUserId;
    private String facebookId;
    private String fullName;
    private String tagList;

    MyPreferences myPreferences;
    @Override
    protected String doInBackground(String... strings) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("https://api.quickblox.com/users.json");
        HttpResponse response = null;

        JSONObject user_node = new JSONObject();
        JSONObject params = new JSONObject();
        try {
            params.put("login", login);
            params.put("password", password);
            params.put("email", myPreferences.getEmail());
            params.put("facebook_id", facebookId);
            params.put("full_name", myPreferences.getFirstName() + " " + myPreferences.getLastName());
            params.put("tag_list", tagList);
            user_node.put("user", params);

            httppost.setHeader("Content-Type", "application/json");
            httppost.setHeader("QuickBlox-REST-API-Version", "0.1.0");
            httppost.setHeader("QB-Token", myPreferences.getQBToken());
            HttpEntity httpEntity = new StringEntity(user_node.toString(),"UTF-8");
            httppost.setEntity(httpEntity);

            response = httpclient.execute(httppost);
            return EntityUtils.toString(response.getEntity());

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getExternalUserId() {
        return externalUserId;
    }

    public void setExternalUserId(String externalUserId) {
        this.externalUserId = externalUserId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTagList() {
        return tagList;
    }

    public void setTagList(String tagList) {
        this.tagList = tagList;
    }

    public String getQBToken() {
        return QBToken;
    }

    public void setQBToken(String QBToken) {
        this.QBToken = QBToken;
    }

    public MyPreferences getMyPreferences() {
        return myPreferences;
    }

    public void setMyPreferences(MyPreferences myPreferences) {
        this.myPreferences = myPreferences;
    }
}
