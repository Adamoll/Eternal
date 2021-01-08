package com.eternalsrv.utils.chat;

import android.os.Bundle;

import com.eternalsrv.utils.holders.QbDialogHolder;
import com.eternalsrv.utils.qb.QbDialogUtils;
import com.eternalsrv.utils.qb.callback.QbEntityCallbackImpl;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogCustomData;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class DialogsManager {

    public static final String PROPERTY_OCCUPANTS_IDS = "occupants_ids";
    public static final String PROPERTY_DIALOG_TYPE = "dialog_type";
    public static final String PROPERTY_DIALOG_NAME = "dialog_name";
    public static final String PROPERTY_NOTIFICATION_TYPE = "notification_type";
    public static final String PROPERTY_MATCH_VALUE = "match_value";
    public static final String CREATING_DIALOG = "creating_dialog";
    public static final String DELETING_DIALOG = "deleting_dialog";

    private final Set<ManagingDialogsCallbacks> managingDialogsCallbackListener = new CopyOnWriteArraySet<>();

    private boolean isMessageCreatingDialog(QBChatMessage systemMessage){
        return CREATING_DIALOG.equals(systemMessage.getProperty(PROPERTY_NOTIFICATION_TYPE));
    }

    private boolean isMessageDeletingDialog(QBChatMessage systemMessage) {
        return DELETING_DIALOG.equals(systemMessage.getProperty(PROPERTY_NOTIFICATION_TYPE));
    }

    private QBChatMessage buildSystemMessageAboutCreatingDialog(QBChatDialog dialog){
        QBChatMessage qbChatMessage = new QBChatMessage();
        qbChatMessage.setDialogId(dialog.getDialogId());
        qbChatMessage.setProperty(PROPERTY_OCCUPANTS_IDS, QbDialogUtils.getOccupantsIdsStringFromList(dialog.getOccupants()));
        qbChatMessage.setProperty(PROPERTY_DIALOG_TYPE, String.valueOf(dialog.getType().getCode()));
        qbChatMessage.setProperty(PROPERTY_DIALOG_NAME, String.valueOf(dialog.getName()));
        qbChatMessage.setProperty(PROPERTY_NOTIFICATION_TYPE, CREATING_DIALOG);
        qbChatMessage.setProperty(PROPERTY_MATCH_VALUE, String.valueOf(dialog.getCustomData().getInteger("matchValue")));
        return qbChatMessage;
    }


    private QBChatMessage buildSystemMessageAboutDeletingDialog(QBChatDialog dialog) {
        QBChatMessage qbChatMessage = new QBChatMessage();
        qbChatMessage.setDialogId(dialog.getDialogId());
        qbChatMessage.setProperty(PROPERTY_OCCUPANTS_IDS, QbDialogUtils.getOccupantsIdsStringFromList(dialog.getOccupants()));
        qbChatMessage.setProperty(PROPERTY_DIALOG_TYPE, String.valueOf(dialog.getType().getCode()));
        qbChatMessage.setProperty(PROPERTY_NOTIFICATION_TYPE, DELETING_DIALOG);

        return qbChatMessage;
    }

    private QBChatDialog buildChatDialogFromSystemMessage(QBChatMessage qbChatMessage){
        QBChatDialog chatDialog = new QBChatDialog();
        chatDialog.setDialogId(qbChatMessage.getDialogId());
        chatDialog.setOccupantsIds(QbDialogUtils.getOccupantsIdsListFromString((String) qbChatMessage.getProperty(PROPERTY_OCCUPANTS_IDS)));
        chatDialog.setType(QBDialogType.parseByCode(Integer.parseInt(qbChatMessage.getProperty(PROPERTY_DIALOG_TYPE).toString())));
        chatDialog.setName(qbChatMessage.getProperty(PROPERTY_DIALOG_NAME).toString());
        chatDialog.setUnreadMessageCount(0);
        QBDialogCustomData customData = new QBDialogCustomData("DialogMatchValue");
        customData.putInteger("matchValue", Integer.parseInt(qbChatMessage.getProperty(PROPERTY_MATCH_VALUE).toString()));
        chatDialog.setCustomData(customData);
        return chatDialog;
    }

    public void sendSystemMessageAboutCreatingDialog(QBSystemMessagesManager systemMessagesManager, QBChatDialog dialog) {
        QBChatMessage systemMessageCreatingDialog = buildSystemMessageAboutCreatingDialog(dialog);
        try {
            for (Integer recipientId : dialog.getOccupants()) {
                if (!recipientId.equals(QBChatService.getInstance().getUser().getId())) {
                    systemMessageCreatingDialog.setRecipientId(recipientId);
                    systemMessagesManager.sendSystemMessage(systemMessageCreatingDialog);
                }
            }
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    public void sendSystemMessageAboutDeletingDialog(QBSystemMessagesManager systemMessagesManager, QBChatDialog dialog) {
        QBChatMessage systemMessageDeletingDialog = buildSystemMessageAboutDeletingDialog(dialog);
        try {
            for(Integer recipientId: dialog.getOccupants()) {
                if (!recipientId.equals(QBChatService.getInstance().getUser().getId())) {
                    systemMessageDeletingDialog.setRecipientId(recipientId);
                    systemMessagesManager.sendSystemMessage(systemMessageDeletingDialog);
                }
            }
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    private void loadUsersFromDialog(QBChatDialog chatDialog){
        ChatHelper.getInstance().getUsersFromDialog(chatDialog, new QbEntityCallbackImpl<ArrayList<QBUser>>());
    }

    public void onGlobalMessageReceived(String dialogId, QBChatMessage chatMessage){
        if (chatMessage.getBody() != null && chatMessage.isMarkable()) {
            if (QbDialogHolder.getInstance().hasDialogWithId(dialogId)) {
                QbDialogHolder.getInstance().updateDialog(dialogId, chatMessage);
                notifyListenersDialogUpdated(dialogId);
            } else {
                ChatHelper.getInstance().getDialogById(dialogId, new QbEntityCallbackImpl<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog chatDialog, Bundle bundle) {
                        loadUsersFromDialog(chatDialog);
                        QbDialogHolder.getInstance().addDialog(chatDialog);
                        notifyListenersNewDialogLoaded(chatDialog);
                    }
                });
            }
        }
    }

    public void onSystemMessageReceived(QBChatMessage systemMessage){
        if (isMessageCreatingDialog(systemMessage)) {
            QBChatDialog chatDialog = buildChatDialogFromSystemMessage(systemMessage);
            chatDialog.initForChat(QBChatService.getInstance());
            QbDialogHolder.getInstance().addDialog(chatDialog);
            notifyListenersDialogCreated(chatDialog);
        } else if (isMessageDeletingDialog(systemMessage)) {
            QbDialogHolder.getInstance().deleteDialog(systemMessage.getDialogId());
            notifyListenersDialogDeleted(systemMessage.getDialogId());
        }
    }

    private void notifyListenersDialogCreated(final QBChatDialog chatDialog) {
        for (ManagingDialogsCallbacks listener : getManagingDialogsCallbackListeners()) {
            listener.onDialogCreated(chatDialog);
        }
    }

    private void notifyListenersDialogUpdated(final String dialogId) {
        for (ManagingDialogsCallbacks listener : getManagingDialogsCallbackListeners()) {
            listener.onDialogUpdated(dialogId);
        }
    }

    private void notifyListenersNewDialogLoaded(final QBChatDialog chatDialog) {
        for (ManagingDialogsCallbacks listener : getManagingDialogsCallbackListeners()) {
            listener.onNewDialogLoaded(chatDialog);
        }
    }

    private void notifyListenersDialogDeleted(final String chatDialog) {
        for (ManagingDialogsCallbacks listener : getManagingDialogsCallbackListeners()) {
            listener.onDialogDeleted(chatDialog);
        }
    }

    public void addManagingDialogsCallbackListener(ManagingDialogsCallbacks listener){
        if (listener != null){
            managingDialogsCallbackListener.add(listener);
        }
    }

    public void removeManagingDialogsCallbackListener(ManagingDialogsCallbacks listener) {
        managingDialogsCallbackListener.remove(listener);
    }

    public Collection<ManagingDialogsCallbacks> getManagingDialogsCallbackListeners() {
        return Collections.unmodifiableCollection(managingDialogsCallbackListener);
    }

    public interface ManagingDialogsCallbacks{

        void onDialogCreated(QBChatDialog chatDialog);

        void onDialogUpdated(String chatDialog);

        void onNewDialogLoaded(QBChatDialog chatDialog);

        void onDialogDeleted(String chatDialog);
    }
}
