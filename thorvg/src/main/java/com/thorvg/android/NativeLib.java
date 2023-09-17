package com.thorvg.android;

import android.graphics.Bitmap;

public class NativeLib {
    static {
        System.loadLibrary("thorvg-android");
    }

    public static native void nRenderLottie(String jsonContent, int contentSize, Bitmap bitmap,
            float width, float height);
}