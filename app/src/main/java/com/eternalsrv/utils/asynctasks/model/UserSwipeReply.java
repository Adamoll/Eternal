package com.eternalsrv.utils.asynctasks.model;

import com.google.gson.annotations.SerializedName;

public class UserSwipeReply extends BaseReply {
    @SerializedName("user_id")
    private int userId;
    @SerializedName("qb_id")
    private int recipientQuickBloxId;
    private int match;
    @SerializedName("match_value")
    private int matchValue;

    public int getUserId() {
        return userId;
    }

    public int getRecipientQuickBloxId() {
        return recipientQuickBloxId;
    }

    public int getMatch() {
        return match;
    }

    public boolean isMatch() {
        return match == 1;
    }

    public int getMatchValue() {
        return matchValue;
    }
}
