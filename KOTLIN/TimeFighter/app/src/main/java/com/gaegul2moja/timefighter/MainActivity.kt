package com.gaegul2moja.timefighter

import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {

    internal var score : Int = 0

    internal lateinit var tapMeButton : Button
    internal lateinit var gameScoreTextview : TextView
    internal lateinit var timeLeftTextView: TextView

    internal var gameStarted = false
    internal lateinit var countDownTimer: CountDownTimer
    internal val  initialCountdown : Long = 6000
    internal val countDownInterval : Long = 1000
    internal var timeLeftOnTimer : Long = 6000

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY" //STRING TYPE 이니까 const를 붙인다.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate called. Score is : $score") //$는 string value가 어디서 오는지(여기서는 score 변수로부터 온다)를 알린다.

        tapMeButton = findViewById(R.id.tapMeButton)
        gameScoreTextview = findViewById(R.id.yourScoreTextView)
        timeLeftTextView = findViewById(R.id.timeLeftTextView)

        gameScoreTextview.text = getString(R.string.yourScore, score)

        tapMeButton.setOnClickListener { view ->
            incrementScore()
        }

        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeftOnTimer = savedInstanceState.getLong(TIME_LEFT_KEY)
            restoreGame()
        }
        else {
            resetGame()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //return super.onOptionsItemSelected(item)
        if(item.itemId == R.id.actionAbout)
            showInfo()
        return true
    }

    private fun showInfo () {
        val dialogTitle = getString(R.string.aboutTitle) + BuildConfig.VERSION_NAME
        val dialogMessage = getString(R.string.aboutMessage)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.create().show()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SCORE_KEY, score)
        outState.putLong(TIME_LEFT_KEY, timeLeftOnTimer)

        countDownTimer.cancel()
        Log.d(TAG, "onSaveInstanceState : Saving Score : $score & Time Left : $timeLeftOnTimer")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called.")
    }

    private fun resetGame () {
        score = 0
        gameScoreTextview.text = getString(R.string.yourScore, score)
        val initialTimeLeft = initialCountdown / 1000

        timeLeftTextView.text = getString(R.string.timeLeft, initialTimeLeft)

        countDownTimer = object  : CountDownTimer(initialCountdown, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished / 1000
                timeLeftTextView.text = getString(R.string.timeLeft, timeLeft)
            }

            override fun onFinish() {
                endGame()
            }
        }
        gameStarted = false
    }

    private fun incrementScore () {
        if(!gameStarted) {
            startGame()
        }
        score ++
        val newScore = getString(R.string.yourScore, score)
        gameScoreTextview.text = newScore
    }

    private fun restoreGame () {
        gameScoreTextview.text = getString(R.string.yourScore, score)

        val restoredTime = timeLeftOnTimer / 1000
        timeLeftTextView.text = getString(R.string.timeLeft, restoredTime)

        countDownTimer = object : CountDownTimer(timeLeftOnTimer, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished / 1000
                timeLeftTextView.text = getString(R.string.timeLeft, timeLeft)
            }

            override fun onFinish() {
                endGame()
            }
        }
        countDownTimer.start()
        gameStarted = true
    }

    private fun startGame () {
        countDownTimer.start()
        gameStarted = true
    }
    private fun endGame () {
        Toast.makeText(this, getString(R.string.gameOver, score), Toast.LENGTH_LONG).show()
        resetGame()
    }

}
