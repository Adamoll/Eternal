package com.eternalsrv.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;

public class SettingsSeekbar extends CrystalSeekbar {
    private static final int THUMB_SIZE = 32;

    public SettingsSeekbar(Context context) {
        super(context);
    }

    public SettingsSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SettingsSeekbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
