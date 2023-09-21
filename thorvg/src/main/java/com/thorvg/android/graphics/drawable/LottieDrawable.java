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
import com.thorvg.android.LottieNative;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class LottieDrawable extends Drawable implements Animatable {
    private static final String LOGTAG = LottieDrawable.class.getSimpleName();

    private static final int LOTTIE_INFO_FRAME_COUNT = 0;
    private static final int LOTTIE_INFO_COUNT = 1;

    private static final int FRAMES_PER_UPDATES = 1;

    private final AssetManager mAssetManager;

    private final long mNativePtr;

    private Bitmap mBuffer;

    private int mFrame;
    private final int mStartFrame;
    private final int mEndFrame;

    private boolean mIsRunning;

    public LottieDrawable(Context context, String filePath, int width, int height) {
        mAssetManager = context.getAssets();
        mBuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        String contentStr = loadJSONFromAsset(filePath);
        final int[] outValues = new int[LOTTIE_INFO_COUNT];
        mNativePtr = LottieNative.nCreateLottie(mBuffer, contentStr, contentStr.length(),
                width, height, outValues);
        mStartFrame = 0;
        mEndFrame = outValues[LOTTIE_INFO_FRAME_COUNT];
    }

    public void release() {
        LottieNative.nDestroyLottie(mNativePtr);
        if (mBuffer != null) {
            mBuffer.recycle();
            mBuffer = null;
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

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mNativePtr == 0) {
            return;
        }
        LottieNative.nDrawLottieFrame(mNativePtr, mBuffer, mFrame);
        canvas.drawBitmap(mBuffer, 0, 0, new Paint());

        // Increase frame count.
        mFrame += FRAMES_PER_UPDATES;
        if (mFrame > mEndFrame) {
            mFrame = mStartFrame;
        } else if (mFrame < mStartFrame) {
            mFrame = mEndFrame;
        }

        invalidateSelf();
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