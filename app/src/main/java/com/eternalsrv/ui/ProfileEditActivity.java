package com.eternalsrv.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.text.Html;
import android.view.MenuItem;
import android.widget.EditText;

import com.eternalsrv.App;
import com.eternalsrv.R;
import com.eternalsrv.models.QBUserCustomData;
import com.eternalsrv.ui.interfaces.OnStartDragListener;
import com.eternalsrv.ui.editprofile.PhotosRecyclerViewAdapter;
import com.eternalsrv.ui.editprofile.ProfilePhotoData;
import com.eternalsrv.ui.editprofile.SimpleItemTouchHelperCallback;
import com.eternalsrv.ui.editprofile.photoupload.PhotoUploadService;
import com.eternalsrv.utils.Config;
import com.eternalsrv.utils.MyPreferences;
import com.eternalsrv.utils.PreferencesManager;
import com.eternalsrv.utils.SharedPrefsHelper;
import com.eternalsrv.utils.asynctasks.AsyncTaskParams;
import com.eternalsrv.utils.asynctasks.BaseAsyncTask;
import com.eternalsrv.utils.constant.ServerMethodsConsts;
import com.eternalsrv.utils.imagepick.GetFilePathFromUri;
import com.google.gson.Gson;
import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileEditActivity extends AppCompatActivity implements OnStartDragListener {
    public static final int PICK_PHOTO_FOR_AVATAR = 652;
    public static final int REQUEST_READ_EXTERNAL_STORAGE = 99;

    private EditText descriptionEditText;

    private PreferencesManager preferencesManager;
    private MyPreferences myPreferences;

    private PhotosRecyclerViewAdapter photosAdapter;
    private ItemTouchHelper mItemTouchHelper;

    private PhotoUploadService photoUploadService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        preferencesManager = App.getPreferencesManager();
        myPreferences = App.getPreferences();
        setActionBarSettings();

        descriptionEditText = findViewById(R.id.profile_edit_description_edit_text);
        descriptionEditText.setText(myPreferences.getDescription());

        photosAdapter = new PhotosRecyclerViewAdapter(this, this);
        photosAdapter.setHasStableIds(true);
        RecyclerView photosRecyclerView = findViewById(R.id.edit_proile_photos_recyclerview);
        photosRecyclerView.setHasFixedSize(true);
        photosRecyclerView.setAdapter(photosAdapter);

        final int spanCount = 3;
        final GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), spanCount);
        photosRecyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(photosAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(photosRecyclerView);
    }

    private void setActionBarSettings() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Html.fromHtml("<font color='#555555' align='right'>Edit profile</font>"));
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.primary_purple), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            saveProfile();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        saveProfile();
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            GetFilePathFromUri getFilePathFromUri = new GetFilePathFromUri(getApplicationContext());
            String path = getFilePathFromUri.getPath(data.getData());
            uploadPhoto(path, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    photosAdapter.pickImage();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    private void saveProfile() {
        myPreferences.setDescription(descriptionEditText.getText().toString());
        preferencesManager.savePreferences();

        Gson gson = new Gson();
        final QBUser currentUser = SharedPrefsHelper.getInstance().getQbUser();
        currentUser.setOldPassword(currentUser.getPassword());
        QBUserCustomData customData = new QBUserCustomData();
        List<ProfilePhotoData> photoDataList = photosAdapter.getProfilePhotos();
        customData.getProfilePhotoData().addAll(photoDataList);
        currentUser.setCustomData(gson.toJson(customData));
        QBUsers.updateUser(currentUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle args) {
                SharedPrefsHelper.getInstance().saveQbUser(currentUser);
            }

            @Override
            public void onError(QBResponseException errors) {
                System.out.print("error update user");
            }
        });
        StringBuilder builder = new StringBuilder();
        String delimeter = "";
        for (int i = 0; i < photoDataList.size(); i++) {
            if (photoDataList.get(i).getBlobId() != null) {
                builder.append(delimeter);
                delimeter = ";";
                builder.append(photoDataList.get(i).getLink());
            }
        }

        AsyncTaskParams params = new AsyncTaskParams();
        params.put("description", myPreferences.getDescription());
        params.put("user_id", myPreferences.getUserId());
        params.put("photo_links", builder.toString());
        BaseAsyncTask saveSettingsTask = new BaseAsyncTask(ServerMethodsConsts.USERPROFILE, params);
        saveSettingsTask.setHttpMethod("POST");
        saveSettingsTask.execute();
    }

    private void uploadPhoto(final String filePath, int position) {
        if (photoUploadService == null) {
            OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
            builder.readTimeout(20, TimeUnit.SECONDS);
            builder.connectTimeout(15, TimeUnit.SECONDS);
            builder.writeTimeout(30, TimeUnit.SECONDS);
            photoUploadService = new Retrofit.Builder().baseUrl(Config.getServer() + ServerMethodsConsts.PHOTO + "/")
                    .client(builder.build())
                    .build()
                    .create(PhotoUploadService.class);
        }
        File file = new File(filePath);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), reqFile);
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), myPreferences.getUserId().toString());
        RequestBody imagePosition = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(position));
        RequestBody qbSessionToken = RequestBody.create(MediaType.parse("text/plain"), QBSessionManager.getInstance().getToken());
        Call<ResponseBody> call = photoUploadService.upload(body, userId, imagePosition, qbSessionToken);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    if (json.getString("status").equals("ok")) {
                        String photoLink = json.getString("photo_link");
                        Integer blobId = json.getInt("blob_id");
                        updateUserCustomData(new ProfilePhotoData(photoLink, blobId), filePath);
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    public void deletePhoto(final ProfilePhotoData photoData) {
        AsyncTaskParams params = new AsyncTaskParams();
        params.put("user_id", myPreferences.getUserId());
        params.put("qb_token", QBSessionManager.getInstance().getToken());
        params.put("photo_link", photoData.getLink());
        params.put("blob_id", photoData.getBlobId());
        BaseAsyncTask deletePhotoTask = new BaseAsyncTask(ServerMethodsConsts.PHOTO + "/", params);
        deletePhotoTask.setHttpMethod("DELETE");
        deletePhotoTask.execute();

        final QBUser currentUser = SharedPrefsHelper.getInstance().getQbUser();
        if (currentUser.getCustomData() != null) {
            Gson gson = new Gson();
            QBUserCustomData customData = gson.fromJson(currentUser.getCustomData(), QBUserCustomData.class);
            if (customData != null) {
                customData.getProfilePhotoData().remove(photoData);
                currentUser.setCustomData(gson.toJson(customData));
                currentUser.setOldPassword(currentUser.getPassword());
                QBUsers.updateUser(currentUser).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser user, Bundle args) {
                        SharedPrefsHelper.getInstance().saveQbUser(currentUser);
                        photosAdapter.remove(photoData);
                    }

                    @Override
                    public void onError(QBResponseException errors) {
                        System.out.print("error update user");
                    }
                });
            }
        }
    }

    private void updateUserCustomData(final ProfilePhotoData photoData, final String filePath) {
        Gson gson = new Gson();
        final QBUser currentUser = SharedPrefsHelper.getInstance().getQbUser();
        currentUser.setOldPassword(currentUser.getPassword());
        QBUserCustomData customData = gson.fromJson(currentUser.getCustomData(), QBUserCustomData.class);
        if (customData == null)
            customData = new QBUserCustomData();
        customData.getProfilePhotoData().add(photoData);
        currentUser.setCustomData(gson.toJson(customData));
        QBUsers.updateUser(currentUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle args) {
                SharedPrefsHelper.getInstance().saveQbUser(currentUser);
                photosAdapter.update(photoData, filePath);
            }

            @Override
            public void onError(QBResponseException errors) {
                System.out.print("error update user");
            }
        });
    }
}
