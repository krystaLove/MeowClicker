package com.krystalove.meowclicker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val transitionTime: Long = resources.getInteger(R.integer.transitionDuration).toLong() * 1000

        val myFadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        myFadeInAnim.duration = transitionTime
        mainLayout.startAnimation(myFadeInAnim)

        val myButtonAnim = AnimationUtils.loadAnimation(this, R.anim.bounce)

        val interpolator: Interpolator = MyBounceInterpolator(0.2, 20.0)
        myButtonAnim!!.interpolator = interpolator
        startButton.animation = myButtonAnim

        startButton.setOnClickListener {

            startButton.isEnabled = false

            val myFadeOutAnim = AnimationUtils.loadAnimation(this, R.anim.fade_out)
            myFadeOutAnim.duration = transitionTime
            mainLayout.visibility = View.GONE
            mainLayout.startAnimation(myFadeOutAnim)

            Handler().postDelayed({
                val intent = Intent(this, GameActivity::class.java)

                startActivity(intent)
                finish()

            }, transitionTime)

        }
    }

    internal class MyBounceInterpolator(amplitude: Double, frequency: Double) : android.view.animation.Interpolator {
        private var mAmplitude = 1.0
        private var mFrequency = 10.0

        init {
            mAmplitude = amplitude
            mFrequency = frequency
        }

        override fun getInterpolation(time: Float): Float {
            return (-1.0 * Math.pow(Math.E, -time / mAmplitude) *
                    Math.cos(mFrequency * time) + 1).toFloat()
        }
    }


}
