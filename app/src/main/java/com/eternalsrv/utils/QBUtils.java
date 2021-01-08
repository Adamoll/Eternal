package com.eternalsrv.utils;

import com.eternalsrv.App;
import com.eternalsrv.models.QbConfigs;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class QBUtils {
    private static String getSignature(QbConfigs qbConfigs, String FBToken, String nonce, Long timestamp) {
        StringBuilder builder = new StringBuilder();
        builder.append("application_id=");
        builder.append(qbConfigs.getAppId());
        builder.append("&");
        builder.append("auth_key=");
        builder.append(qbConfigs.getAuthKey());
        builder.append("&");
        if(FBToken != null) {
            builder.append("keys[token]=");
            builder.append(FBToken);
            builder.append("&");
            builder.append("nonce=");
            builder.append(nonce);
            builder.append("&");
            builder.append("provider=facebook");

        } else {
            builder.append("nonce=");
            builder.append(nonce);
        }

        builder.append("&");
        builder.append("timestamp=");
        builder.append(timestamp);

        return calculateRFC2104HMAC(qbConfigs.getAuthSecret(), builder.toString());
    }

    /**
     * If FBToken is not null, then signature for obtaining user QBToken is created
     * else created is new token to register new user in Qb
     * @param FBToken
     * @return
     */
    public static String getSignatureRequestData(String FBToken) {
        QbConfigs qbConfigs = App.getInstance().getQbConfigs();
        String nonce = getNonce();
        Long timestamp = getUnixTimestamp();
        JSONObject json = new JSONObject();
        try {
            json.put("application_id", qbConfigs.getAppId());
            json.put("auth_key", qbConfigs.getAuthKey());
            json.put("timestamp", timestamp);
            json.put("nonce", nonce);
            json.put("signature", getSignature(qbConfigs, FBToken, nonce, timestamp));
            if(FBToken != null) {
                json.put("provider", "facebook");
                JSONObject keys = new JSONObject();
                keys.put("token", FBToken);
                json.put("keys", keys);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }

    private static String calculateRFC2104HMAC(String key, String data) {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return toHexString(mac.doFinal(data.getBytes()));
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        int j = bytes.length;
        for (int i = 0; i < j; i++) {
            byte b = bytes[i];
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    private static String getNonce() {
        List<Integer> nonce;
        Integer[] arr = new Integer[8];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i + 1;
        }
        Collections.shuffle(Arrays.asList(arr));

        nonce = new ArrayList<>(Arrays.asList(arr));
        StringBuilder builder = new StringBuilder();
        for (Integer i : nonce)
            builder.append(i.intValue());

        return builder.toString().substring(1);
    }

    private static Long getUnixTimestamp() {
        return System.currentTimeMillis() / 1000L;
    }
}
