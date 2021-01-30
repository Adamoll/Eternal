package com.eternalsrv.utils.asynctasks.model;

import com.google.gson.annotations.SerializedName;

public class UserProfileInfoModel {
    @SerializedName("user_id")
    private int userId;
    @SerializedName("qb_id")
    private int quickbloxId;
    private String name;
    private String description ;
    private int age;
    private int distance;
    @SerializedName("photo_links")
    private String photoLinks;

    public UserProfileInfoModel(int userId, int quickbloxId, String name, String description,
                                int age, int distance, String photoLinks) {
        this.userId = userId;
        this.quickbloxId = quickbloxId;
        this.name = name;
        this.description = description;
        this.age = age;
        this.distance = distance;
        this.photoLinks = photoLinks;
    }

    public int getUserId() {
        return userId;
    }

    public int getQuickbloxId() {
        return quickbloxId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getAge() {
        return age;
    }

    public int getDistance() {
        return distance;
    }

    public String getPhotoLinks() {
        return photoLinks;
    }
}
