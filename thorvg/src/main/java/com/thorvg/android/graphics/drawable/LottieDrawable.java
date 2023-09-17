package com.thorvg.android.graphics.drawable;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.thorvg.android.NativeLib;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class LottieDrawable extends Drawable implements Drawable.Callback, Animatable {
    private static final String LOGTAG = LottieDrawable.class.getSimpleName();

    private LottieDrawableState mLottieState;
    private Context mContext;
    private Bitmap mBitmap;
    private String mJsonFilePath;
    private int mWidth;
    private int mHeight;

    public LottieDrawable(Context context, String jsonFilePath, int width, int height) {
        mContext = context;
        mJsonFilePath = jsonFilePath;
        mWidth = width;
        mHeight = height;
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
    }


    /**
     * The one constructor to rule them all. This is called by all public
     * constructors to set the state and initialize local properties.
     */
    private LottieDrawable(@Nullable LottieDrawableState state, @Nullable Resources res) {
        // As the mutable, not-thread-safe native instance is stored in LottieDrawableState, we
        // need to always do a defensive copy even if mutate() isn't called. Otherwise
        // draw() being called on 2 different LottieDrawable instances could still hit the same
        // underlying native object.
        mLottieState = new LottieDrawableState(state);
//        updateLocalState(res);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

//    @Override
//    public ConstantState getConstantState() {
//        mLottieState.mChangingConfigurations = getChangingConfigurations();
//        return mLottieState;
//    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        String jsonContent = loadJSONFromAsset(mContext, mJsonFilePath);
        NativeLib.nRenderLottie(jsonContent, jsonContent.length(), mBitmap, mWidth, mHeight);
        canvas.drawBitmap(mBitmap, 0, 0, new Paint());
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        // We can't tell whether the drawable is fully opaque unless we examine all the pixels,
        // but we could tell it is transparent if the root alpha is 0.
        return getAlpha() == 0 ? PixelFormat.TRANSPARENT : PixelFormat.TRANSLUCENT;
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable who) {

    }

    @Override
    public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {

    }

    @Override
    public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {

    }

    private static String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(fileName);

            int size = inputStream.available();
            byte[] buffer = new byte[size];

            // Read JSON data from InputStream.
            inputStream.read(buffer);
            inputStream.close();

            // Convert a byte array to a string.
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    static class LottieDrawableState extends ConstantState {
        int mChangingConfigurations;

        public LottieDrawableState(LottieDrawableState copy) {
            if (copy != null) {
                mChangingConfigurations = copy.mChangingConfigurations;
            }
        }

        @NonNull
        @Override
        public Drawable newDrawable() {
            return new LottieDrawable(this, null);
        }

        @NonNull
        @Override
        public Drawable newDrawable(Resources res) {
            return new LottieDrawable(this, res);
        }

        @Override
        public int getChangingConfigurations() {
            return mChangingConfigurations;
        }
    }
}