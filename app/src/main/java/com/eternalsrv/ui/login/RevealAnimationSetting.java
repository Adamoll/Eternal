package com.eternalsrv.ui.login;

public class RevealAnimationSetting {
    private int centerX;
    private int centerY;
    private int width;
    private int height;

    public RevealAnimationSetting(int centerX, int centerY, int width, int height) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = width;
        this.height = height;
    }

    public static RevealAnimationSetting with(int centerX, int centerY, int width, int height) {
        return new RevealAnimationSetting(centerX, centerY, width, height);
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
