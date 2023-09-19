package com.thorvg.android;

import android.graphics.Bitmap;

public class NativeLib {
    static {
        System.loadLibrary("thorvg-android");
    }

    public static native long nCreateLottie(String contentString, int length, float width, float height);

    public static native void nDrawLottieFrame(long lottiePtr, Bitmap bitmap, int frame);

    public static native void nDestroyLottie(long lottiePtr);
}