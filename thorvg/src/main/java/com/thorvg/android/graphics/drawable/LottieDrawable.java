package com.thorvg.android.graphics.drawable;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.thorvg.android.LottieNative;
import com.thorvg.android.R;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.StandardCharsets;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

// https://android.googlesource.com/platform/frameworks/support/+/0402748/graphics/drawable/static/src/android/support/graphics/drawable/VectorDrawableCompat.java
// https://android.googlesource.com/platform/frameworks/support/+/f185f10/compat/java/android/support/v4/graphics/drawable/DrawableCompat.java
// https://android.googlesource.com/platform/frameworks/base/+/53a3ed7c46c12c2e578d1b1df8b039c6db690eaa/core/java/android/view/LayoutInflater.java
// https://android.googlesource.com/platform/frameworks/base/+/HEAD/core/java/android/animation/ValueAnimator.java

public class LottieDrawable extends Drawable implements Animatable {
    private static final String TAG = LottieDrawable.class.getSimpleName();

    /**
     * Internal constants
     */

    private static final int LOTTIE_INFO_FRAME_COUNT = 0;
    private static final int LOTTIE_INFO_COUNT = 1;

    /**
     * Internal variables
     * NOTE: This object implements the clone() method, making a deep copy of any referenced
     * objects. As other non-trivial fields are added to this class, make sure to add logic
     * to clone() to make deep copies of them.
     */

    private final Context mContext;

    private final AssetManager mAssetManager;

    /**
     * Additional playing state to indicate whether an animator has been start()'d. There is
     * some lag between a call to start() and the first animation frame. We should still note
     * that the animation has been started, even if it's first animation frame has not yet
     * happened, and reflect that state in isRunning().
     * Note that delayed animations are different: they are not started until their first
     * animation frame, which occurs after their delay elapses.
     */
    private boolean mRunning = false;

    // The number of times the animation will repeat. The default is 0, which means the animation
    // will play only once
    private int mRepeatCount = 0;
    /**
     * The type of repetition that will occur when repeatMode is nonzero. RESTART means the
     * animation will start from the beginning on every new cycle. REVERSE means the animation
     * will reverse directions on each iteration.
     */
    private int mRepeatMode = RESTART;


    private int mFramesPerUpdates = 1;

    private String mFilePath;

    private Bitmap mBuffer;
    private int mWidth;
    private int mHeight;
    private long mNativePtr;
    private int mFrame;
    private int mStartFrame;
    private int mEndFrame;

    /**
     * Public constants
     */

    /** @hide */
    @IntDef({RESTART, REVERSE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RepeatMode {}

    /**
     * When the animation reaches the end and <code>repeatCount</code> is INFINITE
     * or a positive value, the animation restarts from the beginning.
     */
    public static final int RESTART = 1;
    /**
     * When the animation reaches the end and <code>repeatCount</code> is INFINITE
     * or a positive value, the animation reverses direction on every iteration.
     */
    public static final int REVERSE = 2;
    /**
     * This value used used with the {@link #setRepeatCount(int)} property to repeat
     * the animation indefinitely.
     */
    public static final int INFINITE = -1;

    public LottieDrawable(Context context) {
        mContext = context;
        mAssetManager = context.getAssets();
    }

    public void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
        mBuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        String contentStr = loadJSONFromAsset(mFilePath);
        final int[] outValues = new int[LOTTIE_INFO_COUNT];
        mNativePtr = LottieNative.nCreateLottie(mBuffer, contentStr, contentStr.length(),
                width, height, outValues);
        mStartFrame = 0;
        mEndFrame = outValues[LOTTIE_INFO_FRAME_COUNT];
    }

    /**
     * Sets how many times the animation should be repeated. If the repeat
     * count is 0, the animation is never repeated. If the repeat count is
     * greater than 0 or {@link #INFINITE}, the repeat mode will be taken
     * into account. The repeat count is 0 by default.
     *
     * @param value the number of times the animation should be repeated
     */
    public void setRepeatCount(int value) {
        mRepeatCount = value;
    }
    /**
     * Defines how many times the animation should repeat. The default value
     * is 0.
     *
     * @return the number of times the animation should repeat, or {@link #INFINITE}
     */
    public int getRepeatCount() {
        return mRepeatCount;
    }


    /**
     * Defines what this animation should do when it reaches the end. This
     * setting is applied only when the repeat count is either greater than
     * 0 or {@link #INFINITE}. Defaults to {@link #RESTART}.
     *
     * @param value {@link #RESTART} or {@link #REVERSE}
     */
    public void setRepeatMode(@RepeatMode int value) {
        mRepeatMode = value;
        mFramesPerUpdates = mRepeatMode == RESTART ? 1 : -1;
    }
    /**
     * Defines what this animation should do when it reaches the end.
     *
     * @return either one of {@link #REVERSE} or {@link #RESTART}
     */
    @RepeatMode
    public int getRepeatMode() {
        return mRepeatMode;
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

    public void release() {
        LottieNative.nDestroyLottie(mNativePtr);
        if (mBuffer != null) {
            mBuffer.recycle();
            mBuffer = null;
        }
    }

    public static LottieDrawable create(Context context, int rid) {
        try {
            final Resources resources = context.getResources();
            final XmlPullParser parser = resources.getXml(rid);
            final AttributeSet attrs = Xml.asAttributeSet(parser);
            int type;
            while ((type = parser.next()) != XmlPullParser.START_TAG &&
                    type != XmlPullParser.END_DOCUMENT) {
                // Empty loop
            }
            if (type != XmlPullParser.START_TAG) {
                throw new XmlPullParserException("No start tag found");
            }
            final LottieDrawable drawable = new LottieDrawable(context);
            drawable.inflate(resources, parser, attrs);
            return drawable;
        } catch (XmlPullParserException e) {
            Log.e(TAG, "parser error", e);
        } catch (IOException e) {
            Log.e(TAG, "parser error", e);
        }
        return null;
    }

    @Override
    public void inflate(@NonNull Resources r, @NonNull XmlPullParser parser,
            @NonNull AttributeSet attrs, @Nullable Theme theme) {
        final TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.LottieDrawable, 0, 0);
        mFilePath = a.getString(R.styleable.LottieDrawable_assetFilePath);
        setRepeatMode(a.getInt(R.styleable.LottieDrawable_repeatMode, RESTART));
        setRepeatCount(a.getInt(R.styleable.LottieDrawable_repeatCount, INFINITE));
        a.recycle();
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean isRunning() {
        return mRunning;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mNativePtr == 0) {
            return;
        }
        LottieNative.nDrawLottieFrame(mNativePtr, mBuffer, mFrame);
        canvas.drawBitmap(mBuffer, 0, 0, new Paint());

        // Increase frame count.
        mFrame += mFramesPerUpdates;
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
}