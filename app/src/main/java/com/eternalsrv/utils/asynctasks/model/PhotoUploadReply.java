package com.eternalsrv.utils.asynctasks.model;

import com.google.gson.annotations.SerializedName;

public class PhotoUploadReply extends BaseReply{
    @SerializedName("photo_link")
    private String photoLink;
    @SerializedName("blob_id")
    private int blobId;

    public String getPhotoLink() {
        return photoLink;
    }

    public int getBlobId() {
        return blobId;
    }
}
