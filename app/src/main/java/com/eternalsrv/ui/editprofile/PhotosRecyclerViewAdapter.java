package com.eternalsrv.ui.editprofile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.eternalsrv.ui.interfaces.ItemTouchHelperAdapter;
import com.eternalsrv.ui.interfaces.ItemTouchHelperViewHolder;
import com.eternalsrv.ui.interfaces.OnStartDragListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.eternalsrv.App;
import com.eternalsrv.ui.ProfileEditActivity;
import com.eternalsrv.R;
import com.eternalsrv.models.QBUserCustomData;
import com.eternalsrv.utils.ImageLoader;
import com.eternalsrv.utils.SharedPrefsHelper;
import com.google.gson.Gson;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.eternalsrv.ui.ProfileEditActivity.REQUEST_READ_EXTERNAL_STORAGE;

public class PhotosRecyclerViewAdapter extends RecyclerView.Adapter<PhotosRecyclerViewAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private final List<ProfilePhotoData> profilePhotos = new ArrayList<>();
    private final OnStartDragListener mDragStartListener;
    private final Activity parentActivity;
    private final ImageLoader imageLoader;

    public PhotosRecyclerViewAdapter(Activity activity, OnStartDragListener dragStartListener) {
        parentActivity = activity;
        mDragStartListener = dragStartListener;
        imageLoader = App.getImageLoader();
        QBUser user = SharedPrefsHelper.getInstance().getQbUser();
        QBUserCustomData customData;
        if (user.getCustomData() != null) {
            customData = new Gson().fromJson(user.getCustomData(), QBUserCustomData.class);
            if (customData != null)
                profilePhotos.addAll(customData.getProfilePhotoData());
        }
        for(int i = 0; profilePhotos.size() < 6; i++)
            profilePhotos.add(new ProfilePhotoData("E" + System.nanoTime(), null));
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_profile_photo_item, parent, false);
        return new ItemViewHolder(view);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        final int pos = holder.getAdapterPosition();
        final String photoLink = profilePhotos.get(pos).getLink();
        if (profilePhotos.get(pos).getBlobId() != null)
            imageLoader.downloadImage(photoLink, holder.photoView);
        else
            holder.photoView.setImageBitmap(null);
        holder.photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mDragStartListener.onStartDrag(holder);
                return true;
            }
        });

        if (profilePhotos.get(pos).getBlobId() == null) {
            holder.fabAdd.setVisibility(View.VISIBLE);
            holder.fabDelete.setVisibility(View.GONE);
            holder.photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkPermissionAndPickImage();
                }
            });
        } else {
            holder.fabDelete.setVisibility(View.VISIBLE);
            holder.fabAdd.setVisibility(View.GONE);
            holder.fabDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ProfileEditActivity) parentActivity).deletePhoto(getProfileDataByLink(photoLink));
                }
            });
        }
    }

    @Override
    public void onItemDismiss(int position) {
        profilePhotos.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(profilePhotos, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    private ProfilePhotoData getProfileDataByLink(String link) {
        for (int i = 0; i < profilePhotos.size(); i++) {
            if (profilePhotos.get(i).getLink().equals(link)) {
                return profilePhotos.get(i);
            }
        }
        return null;
    }

    public void update(ProfilePhotoData photoData, String filePath) {
        Bitmap uploadedImage = BitmapFactory.decodeFile(filePath);
        imageLoader.addBitmapToMemoryCache(photoData.getLink(), uploadedImage);
        int firstEmpty = findFirstEmpty();
        QBUser user = SharedPrefsHelper.getInstance().getQbUser();
        if (user.getCustomData() != null) {
            profilePhotos.set(firstEmpty, photoData);
            notifyItemChanged(firstEmpty);
        }
    }

    public void remove(ProfilePhotoData photoData) {
        for (int i = 0; i < profilePhotos.size(); i++) {
            if (profilePhotos.get(i).getBlobId().equals(photoData.getBlobId())) {
                profilePhotos.remove(i);
                imageLoader.removeBitmapFromMemoryCache(photoData.getLink());
                profilePhotos.add(new ProfilePhotoData("E" + System.nanoTime(), null));
                notifyDataSetChanged();
                break;
            }
        }
    }

    private int findFirstEmpty() {
        for (int i = 0; i < profilePhotos.size(); i++) {
            if (profilePhotos.get(i).getBlobId() == null) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return profilePhotos.size();
    }

    @Override
    public long getItemId(int position) {
        return profilePhotos.get(position).getLink().hashCode();
    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        parentActivity.startActivityForResult(intent, ProfileEditActivity.PICK_PHOTO_FOR_AVATAR);
    }

    private void checkPermissionAndPickImage(){
        int permissionCheck = ContextCompat.checkSelfPermission(parentActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    parentActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            pickImage();
        }
    }

    public List<ProfilePhotoData> getProfilePhotos() {
        List<ProfilePhotoData> result = new ArrayList<>();
        for (int i = 0; i < profilePhotos.size(); i++) {
            if (profilePhotos.get(i).getBlobId() != null)
                result.add(profilePhotos.get(i));
        }
        return result;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {
        public ImageView photoView;
        public FloatingActionButton fabAdd;
        public FloatingActionButton fabDelete;

        public ItemViewHolder(View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.edit_profile_photo_image);
            fabAdd = itemView.findViewById(R.id.edit_profile_photo_add_fab);
            fabDelete = itemView.findViewById(R.id.edit_profile_photo_delete_fab);
        }

        @Override
        public void onItemSelected() {
        }

        @Override
        public void onItemClear() {
        }
    }
}
