package com.thorvg.sample;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.thorvg.android.widget.LottieView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LottieView lottieView = findViewById(R.id.lottieView);
        lottieView.setFilePath("swinging.json");
    }
}
