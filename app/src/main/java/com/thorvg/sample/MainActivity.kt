package com.thorvg.sample

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.thorvg.android.widget.LottieAnimationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val lottieView = findViewById<LottieAnimationView>(R.id.lottie_view)
        lottieView.setLottieDrawable(R.drawable.lottie_swinging)

        findViewById<View>(R.id.anim_state).setOnClickListener { v: View ->
            val button = v as TextView
            if ("Pause".contentEquals(button.text)) {
                lottieView.pauseAnimation()
                button.text = "Resume"
            } else {
                lottieView.resumeAnimation()
                button.text = "Pause"
            }
        }
    }
}