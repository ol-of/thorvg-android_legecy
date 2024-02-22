package com.thorvg.android.graphics.drawable;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SVGDrawable extends Drawable {
    private final Context mContext;

    private final AssetManager mAssetManager;

    private static final String TAG = "SVGDrawable";

    private String mFilePath;

    private SVGDrawable(Context context, String filePath) {
        mContext = context;
        mAssetManager = context.getAssets();
        mFilePath = filePath;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public static SVGDrawable create(Context context, String filePath) {
        return new SVGDrawable(context, filePath);
    }
}
