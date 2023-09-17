package com.thorvg.android.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import com.thorvg.android.graphics.drawable.LottieDrawable;

public class LottieView extends View {
    private LottieDrawable mLottieDrawable;
    private String mJsonFilePath = null;

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

    public void setJsonFilePath(String jsonFilePath) {
        mJsonFilePath = jsonFilePath;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mJsonFilePath != null) {
            mLottieDrawable = new LottieDrawable(getContext(), mJsonFilePath, getMeasuredWidth(), getMeasuredHeight());
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
}