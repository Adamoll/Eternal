package com.eternalsrv.utils.asynctasks.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserProfileInfoReply extends BaseReply {
    @SerializedName("users_profile_info")
    private List<UserProfileInfoModel> usersProfileInfo;

    public List<UserProfileInfoModel> getUsersProfileInfo() {
        return usersProfileInfo;
    }

}
