package com.thorvg.android;

public class SVGNative {
    static {
        System.loadLibrary("thorvg-android");
    }

    public static native long nCreateSVG(String content);
    public static native void nDestroySVG(long svgPtr);
}
