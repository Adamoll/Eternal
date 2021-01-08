package com.eternalsrv.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.eternalsrv.utils.constant.GcmConsts;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainActivity.class);

        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString(GcmConsts.EXTRA_GCM_DIALOG_ID) != null) {
                intent.putExtra(GcmConsts.EXTRA_GCM_DIALOG_ID, getIntent().getExtras().getString(GcmConsts.EXTRA_GCM_DIALOG_ID));
                intent.putExtra(GcmConsts.EXTRA_GCM_RECIPIENT_ID, getIntent().getExtras().getString(GcmConsts.EXTRA_GCM_RECIPIENT_ID));
            }
            else if (getIntent().getExtras().getString(GcmConsts.EXTRA_GCM_NEW_PAIR) != null)
                intent.putExtra(GcmConsts.EXTRA_GCM_NEW_PAIR, getIntent().getExtras().getString(GcmConsts.EXTRA_GCM_NEW_PAIR));
        }
        startActivity(intent);
        finish();
    }
}