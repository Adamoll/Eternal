package com.eternalsrv.fcm;

import android.util.Log;

import com.eternalsrv.R;
import com.eternalsrv.ui.SplashActivity;
import com.eternalsrv.utils.ActivityLifecycle;
import com.eternalsrv.utils.NotificationUtils;
import com.eternalsrv.utils.ResourceUtils;
import com.google.firebase.messaging.RemoteMessage;
import com.quickblox.messages.services.fcm.QBFcmPushListenerService;

import java.util.Map;

public class FcmPushListenerService extends QBFcmPushListenerService {
    private static final String TAG = FcmPushListenerService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.v(TAG, "From: " + remoteMessage.getFrom());

        if (ActivityLifecycle.getInstance().isBackground()) {
            showNotification((String) remoteMessage.getData().values().toArray()[0]);
        }
    }

    @Override
    protected void sendPushMessage(Map data, String from, String message) {
        super.sendPushMessage(data, from, message);
    }

    protected void showNotification(String message) {
        NotificationUtils.showNotification(this, SplashActivity.class,
                ResourceUtils.getString(R.string.notification_title), message, R.mipmap.ic_launcher);
    }
}
