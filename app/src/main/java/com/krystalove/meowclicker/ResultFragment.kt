package com.krystalove.meowclicker

import android.R.attr.defaultValue
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView


class ResultFragment : Fragment() {

    private var score: Int = 0
    private val SCORE_TAG = "SCORE"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_result, container, false)

        val bundle = this.arguments
        score = bundle!!.getInt(SCORE_TAG, defaultValue)

        val scoreResultFragment = view.findViewById(R.id.scoreResultFragment) as TextView
        val rating = view.findViewById(R.id.rating) as RatingBar
        val resetButton = view.findViewById(R.id.resetButton) as Button

        scoreResultFragment.text = score.toString()

        rating.rating = score.toFloat() / 100

        resetButton.setOnClickListener {
            val intent = Intent(activity, GameActivity::class.java)
            startActivity(intent)
            activity!!.finish()
        }
        return view
    }
}
