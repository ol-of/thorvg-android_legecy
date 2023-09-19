package com.thorvg.android.graphics.drawable;

import android.content.Context;
import android.content.res.AssetManager;
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

    private final AssetManager mAssetManager;

    private long mNativePtr;
    private int mWidth;
    private int mHeight;

    private Bitmap mCurrentBuffer;
    private Bitmap mNextBuffer;
    private Bitmap mTempBuffer;

    private int mFrame;
    private int mStartFrame;
    private int mEndFrame;
    private int mFramesPerUpdates;
    private boolean mShouldLimitFps = false;

    private boolean mIsRunning;

    private final Runnable mDrawFrameRunnable = () -> {
        if (mNativePtr == 0 || mWidth == 0 || mHeight == 0) {
            return;
        }

        if (mCurrentBuffer == null) {
            mCurrentBuffer = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        }

        NativeLib.nDrawLottieFrame(mNativePtr, mCurrentBuffer, mFrame);

        mTempBuffer = mCurrentBuffer;

        // Increase frame count.
        mFrame += mFramesPerUpdates;
        if (mFrame > mEndFrame) {
            mFrame = mEndFrame;
        } else if (mFrame < mStartFrame) {
            mFrame = mStartFrame;
        }
    };

    public LottieDrawable(Context context, String filePath, int width, int height) {
        mAssetManager = context.getAssets();
        mWidth = width;
        mHeight = height;
        String contentString = loadJSONFromAsset(filePath);
        mNativePtr = NativeLib.nCreateLottie(contentString, contentString.length(),
                mWidth, mHeight);
        mFramesPerUpdates = mShouldLimitFps ? 2 : 1;
    }

    public void release() {
        NativeLib.nDestroyLottie(mNativePtr);
        if (mCurrentBuffer != null) {
            mCurrentBuffer.recycle();
            mCurrentBuffer = null;
        }
        if (mNextBuffer != null) {
            mNextBuffer.recycle();
            mNextBuffer = null;
        }
        if (mTempBuffer != null) {
            mTempBuffer.recycle();
            mTempBuffer = null;
        }
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean isRunning() {
        return mIsRunning;
    }

    private void switchBuffer() {
        mCurrentBuffer = mNextBuffer;
        mNextBuffer = mTempBuffer;
        mTempBuffer = null;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mNativePtr == 0) {
            return;
        }
        if (mIsRunning) {
            switchBuffer();
        }
        if (mCurrentBuffer == null) {
            mCurrentBuffer = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        }
        NativeLib.nDrawLottieFrame(mNativePtr, mCurrentBuffer, mFrame);
        canvas.drawBitmap(mCurrentBuffer, 0, 0, new Paint());
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

    private String loadJSONFromAsset(String fileName) {
        String json = null;
        try {
            InputStream inputStream = mAssetManager.open(fileName);

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
}