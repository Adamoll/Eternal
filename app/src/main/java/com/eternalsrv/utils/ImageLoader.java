package com.eternalsrv.utils;

import android.graphics.Bitmap;
import android.util.LruCache;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.eternalsrv.App;

public class ImageLoader {

    private LruCache<String, Bitmap> mMemoryCache;

    public ImageLoader() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 4;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }
    public void removeBitmapFromMemoryCache(String key) {
        mMemoryCache.remove(key);
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }


    public void downloadImage(final String photoUrl, final ImageView imageView) {
        Bitmap bitmap = getBitmapFromMemCache(photoUrl);

        if (bitmap == null) {
            Glide.with(App.getAppContext()).load(photoUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    imageView.setImageBitmap(resource);
                    addBitmapToMemoryCache(photoUrl, resource);
                }
            });
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }
}
