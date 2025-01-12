package com.example.lab6

import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var moleButtons: List<Button>
    private lateinit var scoreTextView: TextView
    private var score = 0
    private var misses = 0
    private val handler = Handler()
    private var currentActiveMoleIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scoreTextView = findViewById(R.id.scoreTextView)
        moleButtons = listOf(
            findViewById(R.id.button1),
            findViewById(R.id.button2),
            findViewById(R.id.button3),
            findViewById(R.id.button4),
            findViewById(R.id.button5),
            findViewById(R.id.button6)
        )

        moleButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (index == currentActiveMoleIndex) {
                    score++
                    updateScore()
                    deactivateMole()
                } else {
                    misses++
                    checkGameOver()
                    Toast.makeText(this, "Pudło!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        startGame()
    }

    private fun startGame() {
        score = 0
        misses = 0
        updateScore()
        handler.postDelayed(object : Runnable {
            override fun run() {
                deactivateMole()
                activateRandomMole()
                handler.postDelayed(this, 1000) // Tempo zmiany kreta (1 sekunda)
            }
        }, 1000)
    }

    private fun activateRandomMole() {
        currentActiveMoleIndex = Random.nextInt(moleButtons.size)
        moleButtons[currentActiveMoleIndex].background = ContextCompat.getDrawable(this, R.drawable.mole_active)
    }

    private fun deactivateMole() {
        if (currentActiveMoleIndex != -1) {
            moleButtons[currentActiveMoleIndex].background = ContextCompat.getDrawable(this, R.drawable.mole_inactive)
        }
    }

    private fun updateScore() {
        scoreTextView.text = "Wynik: $score"
    }

    private fun checkGameOver() {
        if (misses >= 3) {
            showGameOverDialog()
        }
    }

    private fun showGameOverDialog() {
        handler.removeCallbacksAndMessages(null) // Zatrzymaj grę
        AlertDialog.Builder(this)
            .setTitle("Koniec gry")
            .setMessage("Przegrałeś! Wynik końcowy: $score")
            .setPositiveButton("Zagraj ponownie") { _, _ ->
                startGame()
            }
            .setCancelable(false)
            .show()
    }
}
