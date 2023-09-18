package com.thorvg.android;

import android.graphics.Bitmap;

public class NativeLib {
    static {
        System.loadLibrary("thorvg-android");
    }

    public static native long nCreateLottie(Bitmap bitmap, String contentString, int length, float width, float height);

    public static native void nDrawLottie(long lottiePtr);

    public static native void nDestroyLottie(long lottiePtr);
}