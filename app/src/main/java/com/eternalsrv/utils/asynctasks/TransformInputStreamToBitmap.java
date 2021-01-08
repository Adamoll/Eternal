package com.eternalsrv.utils.asynctasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

public class TransformInputStreamToBitmap extends AsyncTask<InputStream, Void, Bitmap> {
    private ImageView imageView;
    private Integer avatarId;

    @Override
    protected Bitmap doInBackground(InputStream... inputStreams) {
        Bitmap bitmap = BitmapFactory.decodeStream(inputStreams[0]);
       // App.getImageLoader().addBitmapToMemoryCache(avatarId, bitmap);
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        imageView.setImageBitmap(bitmap);
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public void setAvatarId(Integer avatarId) {
        this.avatarId = avatarId;
    }
}
