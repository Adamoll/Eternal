package com.eternalsrv.utils.holders;

import android.content.Context;
import android.content.SharedPreferences;

import com.eternalsrv.App;
import com.eternalsrv.models.ChatDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.users.model.QBUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class QbDialogHolder {
    private static final String DIALOGS_PREFS_NAME = "dialogs";
    private static final String DIALOG_ID_LIST_NAME = "dialog-id-list";

    private static QbDialogHolder instance;
    private final Map<String, QBChatDialog> dialogsMap;
    private final SharedPreferences sharedPreferences;

    public static synchronized QbDialogHolder getInstance() {
        if (instance == null) {
            instance = new QbDialogHolder();
        }
        return instance;
    }

    private QbDialogHolder() {
        Gson gson = new Gson();
        sharedPreferences = App.getInstance().getSharedPreferences(DIALOGS_PREFS_NAME, Context.MODE_PRIVATE);
        dialogsMap = new TreeMap<>();
        String dialogListJson = sharedPreferences.getString(DIALOG_ID_LIST_NAME, null);
        if (dialogListJson != null) {
            Type type = new TypeToken<Set<String>>(){}.getType();
            Set<String> dialogIdsSet = gson.fromJson(dialogListJson, type);
            for (String dialogId : dialogIdsSet) {
                String chatDialogJson = sharedPreferences.getString(dialogId, null);
                ChatDialog chatDialog = gson.fromJson(chatDialogJson, ChatDialog.class);
                dialogsMap.put(dialogId, chatDialog);
            }
        } else {
            Set<String> dialogSet = new HashSet<>();
            getEditor().putString(DIALOG_ID_LIST_NAME, gson.toJson(dialogSet));
            getEditor().commit();
        }
    }

    public Map<String, QBChatDialog> getDialogs() {
        return getSortedMap(dialogsMap);
    }

    public QBChatDialog getChatDialogById(String dialogId){
        return dialogsMap.get(dialogId);
    }

    public void clear() {
        dialogsMap.clear();
    }

    public void addDialog(QBChatDialog dialog) {
        if (dialog != null) {
            dialogsMap.put(dialog.getDialogId(), dialog);
        }
    }

    public void addDialogs(List<QBChatDialog> dialogs) {
        for (QBChatDialog dialog : dialogs) {
            addDialog(dialog);
        }
    }

    public void deleteDialogs(Collection<QBChatDialog> dialogs) {
        for (QBChatDialog dialog : dialogs) {
            deleteDialog(dialog);
        }
    }

    public void deleteDialogs(ArrayList<String> dialogsIds) {
        for (String dialogId : dialogsIds) {
            deleteDialog(dialogId);
        }
    }

    public void deleteDialog(QBChatDialog chatDialog){
        dialogsMap.remove(chatDialog.getDialogId());
    }

    public void deleteDialog(String dialogId){
        dialogsMap.remove(dialogId);
    }

    public boolean hasDialogWithId(String dialogId){
        return dialogsMap.containsKey(dialogId);
    }

    public boolean hasPrivateDialogWithUser(QBUser user){
        return getPrivateDialogWithUser(user) != null;
    }

    public QBChatDialog getPrivateDialogWithUser(QBUser user){
        for (QBChatDialog chatDialog : dialogsMap.values()){
            if (QBDialogType.PRIVATE.equals(chatDialog.getType())
                    && chatDialog.getOccupants().contains(user.getId())){
                return chatDialog;
            }
        }
        return null;
    }

    public QBChatDialog getDialogWithRecipientId(Integer recipientId) {
        for (QBChatDialog chatDialog : dialogsMap.values()){
            if (chatDialog.getOccupants().contains(recipientId)){
                return chatDialog;
            }
        }
        return null;
    }

    private Map<String, QBChatDialog> getSortedMap(Map <String, QBChatDialog> unsortedMap){
        Map <String, QBChatDialog> sortedMap = new TreeMap(new LastMessageDateSentComparator(unsortedMap));
        sortedMap.putAll(unsortedMap);
        return sortedMap;
    }

    public void updateDialog(String dialogId, QBChatMessage qbChatMessage){
        QBChatDialog updatedDialog = getChatDialogById(dialogId);
        updatedDialog.setLastMessage(qbChatMessage.getBody());
        updatedDialog.setLastMessageDateSent(qbChatMessage.getDateSent());
        updatedDialog.setUnreadMessageCount(updatedDialog.getUnreadMessageCount() != null
                ? updatedDialog.getUnreadMessageCount() + 1 : 1);
        updatedDialog.setLastMessageUserId(qbChatMessage.getSenderId());

        dialogsMap.put(updatedDialog.getDialogId(), updatedDialog);
    }

    public void size() {
        try{
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            ObjectOutputStream oos=new ObjectOutputStream(baos);
            oos.writeObject(dialogsMap);
            oos.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private SharedPreferences.Editor getEditor() {
        return sharedPreferences.edit();
    }

    public static class LastMessageDateSentComparator implements Comparator<String> {
        Map <String, QBChatDialog> map;

        public LastMessageDateSentComparator(Map <String, QBChatDialog> map) {

            this.map = map;
        }

        public int compare(String keyA, String keyB) {

            long valueA = map.get(keyA).getLastMessageDateSent();
            long valueB = map.get(keyB).getLastMessageDateSent();

            if (valueB < valueA){
                return -1;
            } else {
                return 1;
            }
        }
    }
}
