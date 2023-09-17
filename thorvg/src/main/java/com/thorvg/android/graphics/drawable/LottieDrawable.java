package com.thorvg.android.graphics.drawable;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;

import com.thorvg.android.NativeLib;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class LottieDrawable extends BitmapDrawable implements Animatable {
    private final Context mContext;
    private final Bitmap mBitmap;
    private final String mJsonFilePath;
    private int mWidth;
    private int mHeight;

    public LottieDrawable(Context context, String jsonFilePath, int width, int height) {
        mContext = context;
        mJsonFilePath = jsonFilePath;
        mWidth = width;
        mHeight = height;
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
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

    @Override
    public void draw(Canvas canvas) {
        String jsonContent = loadJSONFromAsset(mContext, mJsonFilePath);
        NativeLib.nRenderLottie(jsonContent, jsonContent.length(), mBitmap, mWidth, mHeight);
        canvas.drawBitmap(mBitmap, 0, 0, getPaint());
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
}