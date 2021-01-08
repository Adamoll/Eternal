package com.eternalsrv.utils.holders;

import android.util.SparseArray;

import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

public class QbUsersHolder {

    private static QbUsersHolder instance;

    private SparseArray<QBUser> qbUserSparseArray;

    public static synchronized QbUsersHolder getInstance() {
        if (instance == null) {
            instance = new QbUsersHolder();
        }

        return instance;
    }

    private QbUsersHolder() {
        qbUserSparseArray = new SparseArray<>();
    }

    public void putUsers(List<QBUser> users) {
        for (QBUser user : users) {
            putUser(user);
        }
    }

    public void putUser(QBUser user) {
        qbUserSparseArray.put(user.getId(), user);
    }

    public QBUser getUserById(int id) {
        return qbUserSparseArray.get(id);
    }

    public List<QBUser> getUsersByIds(List<Integer> ids) {
        List<QBUser> users = new ArrayList<>();
        for (Integer id : ids) {
            QBUser user = getUserById(id);
            if (user != null) {
                users.add(user);
            }
        }
        return users;
    }
}
