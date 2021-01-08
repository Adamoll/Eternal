package com.eternalsrv.utils.holders;

import android.util.SparseArray;

import com.eternalsrv.ui.swipe.UserProfileInfo;
import java.util.List;

public class UserProfileInfoHolder {

    private static UserProfileInfoHolder instance;

    private SparseArray<UserProfileInfo> userProfileInfoSparseArray;

    public static synchronized UserProfileInfoHolder getInstance() {
        if (instance == null) {
            instance = new UserProfileInfoHolder();
        }

        return instance;
    }

    private UserProfileInfoHolder() {
        userProfileInfoSparseArray = new SparseArray<>();
    }

    public void putProfilesInfo(List<UserProfileInfo> profilesInfo) {
        for (UserProfileInfo profileInfo : profilesInfo) {
            putProfileInfo(profileInfo);
        }
    }

    public void putProfileInfo(UserProfileInfo profileInfo) {
        userProfileInfoSparseArray.put(profileInfo.getUserQbId(), profileInfo);
    }

    public UserProfileInfo getProfileInfo(int id) {
        return userProfileInfoSparseArray.get(id);
    }

}
