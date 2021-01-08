package com.eternalsrv.ui.interpolator;

import android.view.animation.Interpolator;

public class SmoothInterpolator implements Interpolator {
    // easeInOutQuint
    public float getInterpolation(float t) {
        float x = t*2.0f;
        if (t<0.5f) return 0.5f*x*x*x*x*x;
        x = (t-0.5f)*2-1;
        return 0.5f*x*x*x*x*x+1;
    }
}