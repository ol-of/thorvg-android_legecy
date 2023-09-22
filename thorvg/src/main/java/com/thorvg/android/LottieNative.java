package com.thorvg.android;

import android.graphics.Bitmap;

public class LottieNative {
    static {
        System.loadLibrary("thorvg-android");
    }

    public static native long nCreateLottie(String content, int length, int[] outValues);

    public static native void nSetLottieBufferSize(long lottiePtr, Bitmap bitmap, float width, float height);

    public static native void nDrawLottieFrame(long lottiePtr, Bitmap bitmap, int frame);

    public static native void nDestroyLottie(long lottiePtr);
}