package com.thorvg.sample;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.thorvg.android.widget.LottieAnimationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final LottieAnimationView lottieView = findViewById(R.id.lottie_view);
        lottieView.setLottieDrawable(R.drawable.lottie_swinging);

        findViewById(R.id.anim_state).setOnClickListener(v -> {
            TextView button = (TextView) v;
            if ("Pause".contentEquals(button.getText())) {
                lottieView.pauseAnimation();
                button.setText("Resume");
            } else {
                lottieView.resumeAnimation();
                button.setText("Pause");
            }
        });
    }
}
