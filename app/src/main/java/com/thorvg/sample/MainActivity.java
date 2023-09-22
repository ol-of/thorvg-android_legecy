package com.thorvg.sample;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.thorvg.android.widget.LottieAnimationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LottieAnimationView lottieView = findViewById(R.id.lottieView);
        lottieView.setLottieDrawable(R.drawable.lottie_swinging);
    }
}
