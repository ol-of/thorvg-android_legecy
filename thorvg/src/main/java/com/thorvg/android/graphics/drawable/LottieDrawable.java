package com.thorvg.android.graphics.drawable;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import androidx.annotation.FloatRange;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import com.thorvg.android.LottieNative;
import com.thorvg.android.R;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.StandardCharsets;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class LottieDrawable extends Drawable implements Animatable {
    private static final String TAG = "LottieDrawable";

    private static final int UNDEFINED_SIZE_IN_DIP = 100;

    /**
     * Internal variables
     */

    /**
     * Additional playing state to indicate whether an animator has been start()'d. There is
     * some lag between a call to start() and the first animation frame. We should still note
     * that the animation has been started, even if it's first animation frame has not yet
     * happened, and reflect that state in isRunning().
     * Note that delayed animations are different: they are not started until their first
     * animation frame, which occurs after their delay elapses.
     */
    private boolean mRunning = false;

    private boolean mPaused;

    private int mWidth = 0;

    private int mHeight = 0;

    /**
     * Backing variables
     */

    private int mRemainingPlayCount;

    private int mFrame;

    /**
     * Animation handler used to schedule updates for this animation.
     */
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private final Runnable mNextFrameRunnable = new Runnable() {
        @Override
        public void run() {
            if (mLottieState.mRepeatCount == INFINITE || mRemainingPlayCount > -1) {
                invalidateSelf();
            }
        }
    };

    private Paint mTmpPaint = new Paint();

    /**
     * Public constants
     */

    @RestrictTo(LIBRARY_GROUP_PREFIX)
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

    private LottieDrawableState mLottieState;

    private LottieDrawable(Context context) {
        mLottieState = new LottieDrawableState(context);
    }

    private LottieDrawable(@NonNull LottieDrawableState state) {
        mLottieState = state;
    }

    public void release() {
        mLottieState.releaseLottie();
    }

//    @NonNull
//    @Override
//    public Drawable mutate() {
//        if (!mMutated && super.mutate() == this) {
//            mLottieState = new LottieDrawableState(mLottieState);
//            mMutated = true;
//        }
//        return this;
//    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mLottieState.valid() && (mLottieState.mAutoPlay || mRunning)) {
            long startTime = System.nanoTime();

            canvas.drawBitmap(getFrame(mFrame), 0, 0, mTmpPaint);

            // Increase frame count.
            mFrame += mLottieState.mFramesPerUpdate;
            if (mFrame > mLottieState.mLastFrame) {
                mFrame = mLottieState.mFirstFrame;
                mRemainingPlayCount--;
            } else if (mFrame < mLottieState.mFirstFrame) {
                mFrame = mLottieState.mLastFrame;
                mRemainingPlayCount--;
            }

            long endTime = System.nanoTime();

            if (mPaused) {
                return;
            }

            mHandler.postDelayed(mNextFrameRunnable, mLottieState.mFrameInterval
                    - ((endTime - startTime) / 1000000));
        }
    }

    @Nullable
    public Bitmap getFrame(int frame) {
        return mLottieState.getLottieBuffer(frame);
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

    @Override
    public int getIntrinsicWidth() {
        return mLottieState.mWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mLottieState.mHeight;
    }

    /**
     * Sets how many times the animation should be repeated. If the repeat
     * count is 0, the animation is never repeated. If the repeat count is
     * greater than 0 or {@link #INFINITE}, the repeat mode will be taken
     * into account. The repeat count is 0 by default.
     *
     * @param count the number of times the animation should be repeated
     */
    public void setRepeatCount(int count) {
        mLottieState.mRepeatCount = count;
        mRemainingPlayCount = count;
    }

    /**
     * Defines how many times the animation should repeat. The default value
     * is 0.
     *
     * @return the number of times the animation should repeat, or {@link #INFINITE}
     */
    public int getRepeatCount() {
        return mLottieState.mRepeatCount;
    }

    /**
     * Defines what this animation should do when it reaches the end. This
     * setting is applied only when the repeat count is either greater than
     * 0 or {@link #INFINITE}. Defaults to {@link #RESTART}.
     *
     * @param mode {@link #RESTART} or {@link #REVERSE}
     */
    public void setRepeatMode(@RepeatMode int mode) {
        mLottieState.setRepeatMode(mode);
    }

    /**
     * Defines what this animation should do when it reaches the end.
     *
     * @return either one of {@link #REVERSE} or {@link #RESTART}
     */
    @RepeatMode
    public int getRepeatMode() {
        return mLottieState.mRepeatMode;
    }

    public int getFirstFrame() {
        return mLottieState.mFirstFrame;
    }

    public void setFirstFrame(int frame) {
        mLottieState.setFirstFrame(frame);
    }

    public int getLastFrame() {
        return mLottieState.mLastFrame;
    }

    public void setLastFrame(int frame) {
        mLottieState.setLastFrame(frame);
    }

    /**
     * Gets the length of the animation. The default duration is 300 milliseconds.
     *
     * @return The length of the animation, in milliseconds.
     */
    public long getDuration() {
        return mLottieState.mLottie.mDuration;
    }

    public void setSpeed(@FloatRange(from = 0) float speed) {
        mLottieState.setSpeed(speed);
    }

    @FloatRange(from = 0)
    public float getSpeed() {
        return mLottieState.mSpeed;
    }

    public void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
        if (mWidth <= 0) {
            throw new IllegalArgumentException("<vector> tag requires width > 0");
        } else if (mHeight <= 0) {
            throw new IllegalArgumentException("<vector> tag requires height > 0");
        }
        mLottieState.setLottieSize(mWidth, mHeight);
    }

    @Override
    public boolean isRunning() {
        return mRunning;
    }

    @Override
    public void start() {
        mRunning = true;
        mFrame = mLottieState.mFirstFrame;
        mRemainingPlayCount = mLottieState.mRepeatCount;
        invalidateSelf();
    }

    @Override
    public void stop() {
        mRunning = false;
        mHandler.removeCallbacks(mNextFrameRunnable);
    }

    public void pause() {
        mPaused = true;
        mHandler.removeCallbacks(mNextFrameRunnable);
    }

    public void resume() {
        mPaused = false;
        invalidateSelf();
    }

    @Nullable
    public static LottieDrawable create(Context context, int resId) {
        try {
            final Resources res = context.getResources();
            @SuppressLint("ResourceType") final XmlPullParser parser = res.getXml(resId);
            final AttributeSet attrs = Xml.asAttributeSet(parser);
            int type;
            //noinspection StatementWithEmptyBody
            while ((type = parser.next()) != XmlPullParser.START_TAG
                    && type != XmlPullParser.END_DOCUMENT) {
                // Empty loop
            }
            if (type != XmlPullParser.START_TAG) {
                throw new XmlPullParserException("No start tag found");
            }
            return createFromXmlInner(context, attrs);
        } catch (XmlPullParserException e) {
            Log.e(TAG, "parser error", e);
        } catch (IOException e) {
            Log.e(TAG, "parser error", e);
        }
        return null;
    }

    @NonNull
    public static LottieDrawable createFromXmlInner(@NonNull Context context,
            @NonNull AttributeSet attrs) {
        final LottieDrawable drawable = new LottieDrawable(context);
        drawable.inflate(context, attrs);
        return drawable;
    }

    private void inflate(@NonNull Context context, @NonNull AttributeSet attrs) {
        final LottieDrawableState state = mLottieState;

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LottieDrawable,
                0, 0);

        state.mLottie = new Lottie(context, a.getString(R.styleable.LottieDrawable_assetFilePath));

        setLastFrame(a.getInt(R.styleable.LottieDrawable_frameTo, state.mLottie.mFrameCount));
        setFirstFrame(a.getInt(R.styleable.LottieDrawable_frameFrom, 0));
        setSpeed(a.getFloat(R.styleable.LottieDrawable_speed, 1f));

        setRepeatMode(a.getInt(R.styleable.LottieDrawable_android_repeatMode, RESTART));
        setRepeatCount(a.getInt(R.styleable.LottieDrawable_android_repeatCount, INFINITE));

        state.mAutoPlay = a.getBoolean(R.styleable.LottieDrawable_android_autoStart, true);

        int defaultSize = (int) context.getResources().getDisplayMetrics().density
                * UNDEFINED_SIZE_IN_DIP;
        state.mBaseWidth = a.getDimensionPixelOffset(R.styleable.LottieDrawable_android_width,
                defaultSize);
        state.mBaseHeight = a.getDimensionPixelOffset(R.styleable.LottieDrawable_android_height,
                defaultSize);

        a.recycle();

        state.setLottieSize((int) state.mBaseWidth, (int) state.mBaseHeight);
    }

    private static class LottieDrawableState extends ConstantState {
        Lottie mLottie;

        float mBaseWidth = 0;
        float mBaseHeight = 0;

        private int mWidth = 0;
        private int mHeight = 0;

        /**
         * The type of repetition that will occur when repeatMode is nonzero. RESTART means the
         * animation will start from the beginning on every new cycle. REVERSE means the animation
         * will reverse directions on each iteration.
         */
        int mRepeatMode = RESTART;

        int mRepeatCount = 1;

        float mSpeed = 1f;

        int mFirstFrame;
        int mLastFrame;

        long mFrameInterval;

        int mFramesPerUpdate = 1;

        boolean mAutoPlay;

        float mAlpha = 1f;

        LottieDrawableState(LottieDrawableState copy) {
            if (copy != null) {
                mLottie = new Lottie(copy.mLottie);
                mBaseWidth = copy.mBaseWidth;
                mBaseHeight = copy.mBaseHeight;
                mRepeatCount = copy.mRepeatCount;
                mRepeatMode = copy.mRepeatMode;
                mFramesPerUpdate = copy.mFramesPerUpdate;
                mAutoPlay = copy.mAutoPlay;
                mSpeed = copy.mSpeed;
                mAlpha = copy.mAlpha;
            }
        }

        private void releaseLottie() {
            mLottie.destroy();
            mLottie = null;
        }

        private boolean valid() {
            return mLottie != null && mLottie.mNativePtr != 0;
        }

        private void setLottieSize(int width, int height) {
            if (width != mWidth || height != mHeight) {
                mWidth = width;
                mHeight = height;
                mLottie.setBufferSize(width, height);
            }
        }

        Bitmap getLottieBuffer(int frame) {
            return mLottie.getBuffer(frame);
        }

        void setRepeatMode(@RepeatMode int mode) {
            mRepeatMode = mode;
            mFramesPerUpdate = mRepeatMode == RESTART ? 1 : -1;
        }

        void setSpeed(@FloatRange(from = 0) float speed) {
            mSpeed = speed;
            updateFrameInterval();
        }

        void setFirstFrame(int frame) {
            mFirstFrame = Math.min(frame, mLastFrame);
            updateFrameInterval();
        }

        void setLastFrame(int frame) {
            mLastFrame = Math.min(frame, mLottie.mFrameCount);
            updateFrameInterval();
        }

        void updateFrameInterval() {
            int frameCount = mLastFrame - mFirstFrame;
            mFrameInterval = (long) (mLottie.mDuration / frameCount / mSpeed);
        }

        LottieDrawableState(@NonNull Context context) {
        }

        @NonNull
        @Override
        public Drawable newDrawable() {
            return new LottieDrawable(this);
        }

        @Override
        public int getChangingConfigurations() {
            return 0;
        }
    }

    private static class Lottie {
        private static final int LOTTIE_INFO_FRAME_COUNT = 0;
        private static final int LOTTIE_INFO_DURATION = 1;
        private static final int LOTTIE_INFO_COUNT = 2;

        private final String mJsonContent;

        private final long mNativePtr;

        private int mFrameCount;

        // How long the animation should last in ms
        private long mDuration;

        private Bitmap mBuffer;

        Lottie(@NonNull Lottie copy) {
            mJsonContent = copy.mJsonContent;
            mNativePtr = create(copy.mJsonContent);
            mFrameCount = copy.mFrameCount;
            mDuration = copy.mDuration;
        }

        Lottie(@NonNull Context context, @NonNull String filePath) {
            mJsonContent = loadJsonFromAsset(context, filePath);
            mNativePtr = create(mJsonContent);
        }

        long create(@NonNull String jsonContent) {
            final int[] outValues = new int[LOTTIE_INFO_COUNT];
            long nativePtr = LottieNative.nCreateLottie(jsonContent, jsonContent.length(), outValues);
            mFrameCount = outValues[LOTTIE_INFO_FRAME_COUNT];
            mDuration = outValues[LOTTIE_INFO_DURATION] * 1000L;
            return nativePtr;
        }

        void destroy() {
            if (mBuffer != null) {
                mBuffer.recycle();
                mBuffer = null;
            }
            LottieNative.nDestroyLottie(mNativePtr);
        }

        void setBufferSize(int width, int height) {
            mBuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            LottieNative.nSetLottieBufferSize(mNativePtr, mBuffer, width, height);
        }

        Bitmap getBuffer(int frame) {
            LottieNative.nDrawLottieFrame(mNativePtr, mBuffer, frame);
            return mBuffer;
        }

        String loadJsonFromAsset(Context context, String fileName) {
            String json = null;
            try {
                InputStream inputStream = context.getAssets().open(fileName);

                int size = inputStream.available();
                byte[] buffer = new byte[size];

                // Read JSON data from InputStream.
                inputStream.read(buffer);
                inputStream.close();

                // Convert a byte array to a string.
                json = new String(buffer, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new IllegalStateException("");
            }
            return json;
        }
    }
}