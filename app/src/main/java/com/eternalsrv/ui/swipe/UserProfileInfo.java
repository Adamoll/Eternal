package com.eternalsrv.ui.swipe;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class UserProfileInfo implements Parcelable {
    private int userId;
    private int userQbId;
    private String name;
    private List<String> photoLinks;
    private int age;
    private int matchValue;
    private int distance;
    private String description;

    public UserProfileInfo() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserQbId() {
        return userQbId;
    }

    public void setUserQbId(int userQbId) {
        this.userQbId = userQbId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getMatchValue() {
        return matchValue;
    }

    public void setMatchValue(int matchValue) {
        this.matchValue = matchValue;
    }

    public List<String> getPhotoLinks() {
        return photoLinks;
    }

    public void setPhotoLinks(List<String> photoLinks) {
        this.photoLinks = photoLinks;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    protected UserProfileInfo(Parcel in) {
        userId = in.readInt();
        userQbId = in.readInt();
        name = in.readString();
        if (in.readByte() == 0x01) {
            photoLinks = new ArrayList<String>();
            in.readList(photoLinks, String.class.getClassLoader());
        } else {
            photoLinks = null;
        }
        age = in.readInt();
        matchValue = in.readInt();
        distance = in.readInt();
        description = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userId);
        dest.writeInt(userQbId);
        dest.writeString(name);
        if (photoLinks == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(photoLinks);
        }
        dest.writeInt(age);
        dest.writeInt(matchValue);
        dest.writeInt(distance);
        dest.writeString(description);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserProfileInfo> CREATOR = new Parcelable.Creator<UserProfileInfo>() {
        @Override
        public UserProfileInfo createFromParcel(Parcel in) {
            return new UserProfileInfo(in);
        }

        @Override
        public UserProfileInfo[] newArray(int size) {
            return new UserProfileInfo[size];
        }
    };
}