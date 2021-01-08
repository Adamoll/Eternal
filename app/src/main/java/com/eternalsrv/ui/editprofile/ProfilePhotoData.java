package com.eternalsrv.ui.editprofile;

/**
 * Created by Adam on 2018-10-02..
 */

public class ProfilePhotoData {
    private String link;
    private Integer blobId;

    public ProfilePhotoData() {
    }

    public ProfilePhotoData(String link, Integer blobId) {
        this.link = link;
        this.blobId = blobId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Integer getBlobId() {
        return blobId;
    }

    public void setBlobId(Integer blobId) {
        this.blobId = blobId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProfilePhotoData that = (ProfilePhotoData) o;

        return link != null ? link.equals(that.link) : that.link == null;
    }

    @Override
    public int hashCode() {
        return link != null ? link.hashCode() : 0;
    }
}
