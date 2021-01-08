package com.eternalsrv.utils.asynctasks;

import android.os.AsyncTask;

import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.server.Performer;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class QBGetUserByFbId extends AsyncTask<String, Void, QBUser> {

    @Override
    protected QBUser doInBackground(String... strings) {
        Performer<QBUser> performer = null;
        performer = QBUsers.getUserByFacebookId(strings[0]);
        QBUser user = null;
        try {
            user = performer.perform();
        } catch (QBResponseException e) {
            e.printStackTrace();
        }

        return user;
    }

}
