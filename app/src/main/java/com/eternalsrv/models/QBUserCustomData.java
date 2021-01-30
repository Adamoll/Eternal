package com.eternalsrv.models;

import com.eternalsrv.ui.editprofile.ProfilePhotoData;

import java.util.ArrayList;
import java.util.List;

public class QBUserCustomData {
    private List<ProfilePhotoData> profilePhotoData;

    public QBUserCustomData() {
        profilePhotoData = new ArrayList<>();
    }

    public List<ProfilePhotoData> getProfilePhotoData() {
        return profilePhotoData;
    }

    public void setProfilePhotoData(List<ProfilePhotoData> profilePhotoData) {
        this.profilePhotoData = profilePhotoData;
    }
}