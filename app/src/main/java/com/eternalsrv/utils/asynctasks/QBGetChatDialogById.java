package com.eternalsrv.utils.asynctasks;

import android.os.AsyncTask;

import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class QBGetChatDialogById extends AsyncTask<String, Void, Object[]> {
    @Override
    protected Object[] doInBackground(String... strings) {
        QBChatDialog chatDialog = null;
        QBUser qbUser = null;
        try {
            chatDialog = QBRestChatService.getChatDialogById(strings[0]).perform();
            qbUser = QBUsers.getUser(Integer.parseInt(strings[1])).perform();
        } catch (QBResponseException e) {
            e.printStackTrace();
        }
        return new Object[]{chatDialog, qbUser};
    }
}
