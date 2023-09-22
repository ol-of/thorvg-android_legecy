package com.thorvg.android.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.thorvg.android.graphics.drawable.LottieDrawable;

public class LottieAnimationView extends View {
    private LottieDrawable mLottieDrawable;

    public LottieAnimationView(Context context) {
        this(context, null);
    }

    public LottieAnimationView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LottieAnimationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public LottieAnimationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setLottieDrawable(@DrawableRes int resId) {
        mLottieDrawable = LottieDrawable.create(getContext(), resId);
        if (mLottieDrawable != null) {
            mLottieDrawable.setCallback(this);
        }
    }

    public void startAnimation() {
        if (mLottieDrawable != null) {
            mLottieDrawable.start();
        }
    }

    public void stopAnimation() {
        if (mLottieDrawable != null) {
            mLottieDrawable.stop();
        }
    }

    public void pauseAnimation() {
        if (mLottieDrawable != null) {
            mLottieDrawable.pause();
        }
    }

    public void resumeAnimation() {
        if (mLottieDrawable != null) {
            mLottieDrawable.resume();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (mLottieDrawable != null && (width != mLottieDrawable.getIntrinsicWidth()
                || height != mLottieDrawable.getIntrinsicHeight())) {
            mLottieDrawable.setSize(width, height);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mLottieDrawable != null) {
            mLottieDrawable.release();
            mLottieDrawable = null;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        final LottieDrawable lottieDrawable = mLottieDrawable;
        if (lottieDrawable == null) {
            return;
        }
        lottieDrawable.draw(canvas);
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        super.invalidateDrawable(drawable);
        invalidate();
    }
}