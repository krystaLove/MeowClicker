package com.krystalove.meowclicker

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_game.*
import pl.droidsonroids.gif.GifDrawable


class GameActivity : AppCompatActivity() {

    val TO_MILLIS: Long = 1000

    private var score = 0

    private var prepareDuration: Long = 0
    private var gameDuration: Long = 0

    private var imageChooser = true

    private lateinit var prepareTimer: CountDownTimer
    private lateinit var gameTimer: CountDownTimer

    private lateinit var prepareAnimViewDrawable: GifDrawable

    private val SCORE_TAG = "SCORE"

    var instance: GameActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        clickButton.isEnabled = false

        prepareDuration = resources.getInteger(R.integer.prepareTime).toLong()
        gameDuration = resources.getInteger(R.integer.gameTime).toLong()

        prepareAnimViewDrawable = prepareAnimView.drawable as GifDrawable

        val myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        hud.startAnimation(myFadeInAnimation)

        prepareTimer = MyPrepareCounter(prepareDuration * TO_MILLIS, 1 * TO_MILLIS).start()
        gameTimer = MyGameCounter(gameDuration * TO_MILLIS, 1 * TO_MILLIS)
    }

    inner class MyPrepareCounter(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onFinish() {
            timerTextView.text = "0"

            catView.setImageResource(R.drawable.prepare_anim_end)
            prepareAnimViewDrawable.stop()

            gameTimer.start()
            clickButton.isClickable = true
            clickButton.isEnabled = true
        }

        override fun onTick(millisUntilFinished: Long) {
            timerTextView.text = (millisUntilFinished / 1000).toString()
        }
    }

    inner class MyGameCounter(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onFinish() {
            timerTextView.text = "0"
            progressBar.progress = 0
            if (instance != null)
                gameFinish()
        }

        override fun onTick(millisUntilFinished: Long) {
            timerTextView.text = (millisUntilFinished / TO_MILLIS).toString()

            val progress = ((millisUntilFinished / gameDuration) * 100 / TO_MILLIS).toInt()
            //Log.d("progress", ((millisUntilFinished/gameDuration) *100/TO_MILLIS).toString())
            progressBar.progress = progress
        }
    }

    private fun updateScore() {
        scoreTextView.text = score.toString()
    }

    private fun gameFinish() {
        clickButton.isClickable = false
        clickButton.isEnabled = false
        hud.isEnabled = false

        val fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        fadeOutAnimation.duration = resources.getInteger(R.integer.transitionDuration).toLong() * TO_MILLIS

        hud.startAnimation(fadeOutAnimation)
        hud.visibility = View.GONE

        Handler().postDelayed({
            val endFragment = ResultFragment()

            val transaction = supportFragmentManager.beginTransaction()

            val bundle = Bundle()
            bundle.putInt(SCORE_TAG, score)
            endFragment.arguments = bundle

            transaction.replace(R.id.resultFragment, endFragment)
            transaction.commit()
            resultFragment.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right))
        }, 2000)
    }

    fun doClick(view: View) {
        if (imageChooser) {
            imageChooser = false
            catView.setImageResource(R.drawable.first_clap)
        } else {
            imageChooser = true
            catView.setImageResource(R.drawable.second_clap)
        }
        score++
        updateScore()
    }

    public override fun onResume() {
        super.onResume()
        instance = this
    }

    public override fun onPause() {
        super.onPause()
        instance = null
    }

}

