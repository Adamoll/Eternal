package com.eternalsrv.utils;

import android.os.Bundle;

import com.eternalsrv.R;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;
import com.quickblox.messages.model.QBPushType;
import com.quickblox.users.model.QBUser;


public class PushUtils {

    public static void sendPushAboutMessage(QBChatDialog qbChatDialog, String message) {
        StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
        userIds.add(qbChatDialog.getRecipientId());

        QBUser user = SharedPrefsHelper.getInstance().getQbUser();

        QBEvent event = new QBEvent();
        event.setUserIds(userIds);
        event.setEnvironment(QBEnvironment.DEVELOPMENT);
        event.setNotificationType(QBNotificationType.PUSH);

        event.setMessage(qbChatDialog.getDialogId() + "^" + SharedPrefsHelper.getInstance().getQbUser().getId() +
                "^" + user.getFullName().split(" ")[0] + ": " + message);

        QBPushNotifications.createEvent(event).performAsync(new QBEntityCallback<QBEvent>() {
            @Override
            public void onSuccess(QBEvent qbEvent, Bundle args) {
                // sent
            }

            @Override
            public void onError(QBResponseException errors) {

            }
        });

    }

    public static void sendPushAboutNewPair(int recipientId) {
        StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
        userIds.add(recipientId);

        QBEvent event = new QBEvent();
        event.setUserIds(userIds);
        event.setEnvironment(QBEnvironment.DEVELOPMENT);
        event.setNotificationType(QBNotificationType.PUSH);
        event.setMessage(ResourceUtils.getString(R.string.new_pair));

        QBPushNotifications.createEvent(event).performAsync(new QBEntityCallback<QBEvent>() {
            @Override
            public void onSuccess(QBEvent qbEvent, Bundle args) {
                // sent
            }

            @Override
            public void onError(QBResponseException errors) {

            }
        });

    }
}
