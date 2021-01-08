package com.eternalsrv.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.eternalsrv.App;
import com.eternalsrv.models.ChatDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChatDialogManager {
    private static final String DIALOGS_PREFS_NAME = "dialogs";
    private static final String DIALOG_ID_LIST_NAME = "dialog-id-list";
    private static ChatDialogManager instance;
    private static Set<String> dialogList;
    private static Gson gson;
    private SharedPreferences sharedPreferences;

    public static ChatDialogManager getInstance() {
        if (instance == null) {
            instance = new ChatDialogManager();
        }
        return instance;
    }

    private ChatDialogManager() {
        instance = this;
        gson = new Gson();
        sharedPreferences = App.getInstance().getSharedPreferences(DIALOGS_PREFS_NAME, Context.MODE_PRIVATE);
        String dialogListJson = sharedPreferences.getString(DIALOG_ID_LIST_NAME, null);
        if (dialogListJson != null) {
            Type type = new TypeToken<List<String>>(){}.getType();
            dialogList = new Gson().fromJson(dialogListJson, type);
        } else {
            List<String> dialogList = new ArrayList<>();
            getEditor().putString(DIALOG_ID_LIST_NAME, gson.toJson(dialogList));
            getEditor().commit();
        }
    }

    public void clearAllData(){
        SharedPreferences.Editor editor = getEditor();
        editor.clear().commit();
    }

    public void addDialog(ChatDialog chatDialog) {
        String dialogString = gson.toJson(chatDialog);
        SharedPreferences.Editor editor = getEditor();
        dialogList.add(chatDialog.getDialogId());
        editor.putString(DIALOG_ID_LIST_NAME, gson.toJson(dialogList));
        editor.putString(chatDialog.getDialogId(), dialogString);

        editor.commit();
    }

    public void deleteDialog(String dialogId) {
        SharedPreferences.Editor editor = getEditor();
        dialogList.remove(dialogId);
        editor.putString(DIALOG_ID_LIST_NAME, gson.toJson(dialogList));

        editor.remove(dialogId);
        editor.commit();
    }

    public boolean containsDialog(String dialogId) {
        String dialogListJson = sharedPreferences.getString(DIALOG_ID_LIST_NAME, null);
        if (dialogList.contains(dialogId))
            return true;
        return false;
    }

    public ChatDialog getDialog(String dialogId) {
        if (dialogList.contains(dialogId)) {
            sharedPreferences.getString(dialogId, null);
        }
        return null;
    }

    public List<ChatDialog> getDialogs() {
        Type type = new TypeToken<ChatDialog>(){}.getType();

        List<ChatDialog> chatDialogs = new ArrayList<>();
        for (String dialogId: dialogList) {
            String chatDialogJson = sharedPreferences.getString(dialogId, null);
            if (chatDialogJson != null) {
                chatDialogs.add((ChatDialog)new Gson().fromJson(chatDialogJson, type));
            }
        }
        return chatDialogs;
    }

    public void updateDialog(ChatDialog chatDialog) {
        addDialog(chatDialog);
    }

    private SharedPreferences.Editor getEditor() {
        return sharedPreferences.edit();
    }
}
