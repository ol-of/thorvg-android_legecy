package com.thorvg.android;

import android.graphics.Bitmap;

public class LottieNative {
    static {
        System.loadLibrary("thorvg-android");
    }

    public static native long nCreateLottie(Bitmap bitmap, String contentString, int length,
            float width, float height, int[] outValues);

    public static native void nDrawLottieFrame(long lottiePtr, Bitmap bitmap, int frame);

    public static native void nDestroyLottie(long lottiePtr);
}