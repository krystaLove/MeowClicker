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

        prepareDuration = resources.getInteger(R.integer.prepareTime).toLong()
        gameDuration = resources.getInteger(R.integer.gameTime).toLong()

        prepareAnimViewDrawable = prepareAnimView.drawable as GifDrawable

        val myFadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        hud.startAnimation(myFadeOutAnimation)

        prepareTimer = MyPrepareCounter(prepareDuration * TO_MILLIS, 1 * TO_MILLIS).start()
        gameTimer = MyGameCounter(gameDuration * TO_MILLIS, 1 * TO_MILLIS)
    }

    inner class MyPrepareCounter(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onFinish() {
            timerTextView.text = "0"

            clicker.setImageResource(R.drawable.prepare_anim_end)
            prepareAnimViewDrawable.stop()

            gameTimer.start()
            clicker.setOnClickListener {
                if (imageChooser) {
                    imageChooser = false
                    clicker.setImageResource(R.drawable.first_clap)
                } else {
                    imageChooser = true
                    clicker.setImageResource(R.drawable.second_clap)
                }
                score++
                updateScore()
            }
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

    fun updateScore() {
        scoreTextView.text = score.toString()
    }

    fun gameFinish() {
        clicker.setOnClickListener(null)
        hud.isEnabled = false
        hud.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
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

    public override fun onResume() {
        super.onResume()
        instance = this
    }

    public override fun onPause() {
        super.onPause()
        instance = null
    }
}

