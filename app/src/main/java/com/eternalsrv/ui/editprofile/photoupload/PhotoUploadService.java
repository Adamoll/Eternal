package com.eternalsrv.ui.editprofile.photoupload;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Adam on 2018-09-28..
 */

public interface PhotoUploadService {
     @Multipart
     @POST(".")
     Call<ResponseBody> upload(
             @Part MultipartBody.Part image,
             @Part("user_id") RequestBody userId,
             @Part("image_position") RequestBody position,
             @Part("qb_session_token") RequestBody qbSessionToken
             );

}
