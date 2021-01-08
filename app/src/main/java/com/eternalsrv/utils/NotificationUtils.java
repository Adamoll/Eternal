package com.eternalsrv.utils;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.DrawableRes;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.eternalsrv.App;
import com.eternalsrv.R;
import com.eternalsrv.utils.constant.GcmConsts;


public class NotificationUtils {

    public static void showNotification(Context context, Class<? extends Activity> activityClass,
                                        String title, String message, @DrawableRes int icon) {
        String[] elements = message.split("\\^", 3);
        String messageBody = null;
        String qbDialogId = null;
        String recipientId = null;
        Intent intent = new Intent(context, activityClass);
        if(message.equals(ResourceUtils.getString(R.string.new_pair))) {
            intent.putExtra(GcmConsts.EXTRA_GCM_NEW_PAIR, GcmConsts.EXTRA_GCM_NEW_PAIR);
        } else if (elements.length > 2) {
            qbDialogId = elements[0];
            recipientId = elements[1];
            messageBody = elements[2];
            intent.putExtra(GcmConsts.EXTRA_GCM_MESSAGE, messageBody);
            intent.putExtra(GcmConsts.EXTRA_GCM_DIALOG_ID, qbDialogId);
            intent.putExtra(GcmConsts.EXTRA_GCM_RECIPIENT_ID, recipientId);
        } else {
            intent.putExtra(GcmConsts.EXTRA_GCM_MESSAGE, message);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String CHANNEL_ID = "channel";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(messageBody == null? message : messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setChannelId(CHANNEL_ID)
                .setContentIntent(pendingIntent);

        NotificationChannel channel = null;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID, "FCM Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);

        }
        int notificationId = getNotificationId();

        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    private static int getNotificationId() {
        SharedPreferences prefs = App.getAppContext().getSharedPreferences(PushUtils.class.getSimpleName(), Context.MODE_PRIVATE);
        int notificationNumber = prefs.getInt("notificationNumber", 0);
        SharedPreferences.Editor editor = prefs.edit();
        notificationNumber++;
        editor.putInt("notificationNumber", notificationNumber);
        editor.commit();
        return notificationNumber;
    }

}
