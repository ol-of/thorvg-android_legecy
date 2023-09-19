package com.thorvg.android.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.thorvg.android.graphics.drawable.LottieDrawable;

public class LottieView extends View {
    private LottieDrawable mLottieDrawable;
    private String mFilePath = null;

    public LottieView(Context context) {
        super(context);
    }

    public LottieView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LottieView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LottieView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setFilePath(String filePath) {
        if (filePath == null || filePath.isEmpty() || filePath.equals(mFilePath)) {
            return;
        }
        mFilePath = filePath;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mLottieDrawable == null && mFilePath != null && !mFilePath.isEmpty()) {
            mLottieDrawable = new LottieDrawable(getContext(), mFilePath, getMeasuredWidth(), getMeasuredHeight());
            mLottieDrawable.setCallback(this);
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