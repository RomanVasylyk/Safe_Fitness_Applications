package com.example.miband3

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class HeartRateActivity : AppCompatActivity() {

    private lateinit var heartRateTextView: TextView
    private lateinit var stepsTextView: TextView
    private lateinit var distanceTextView: TextView
    private lateinit var caloriesTextView: TextView

    private val heartRateUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val heartRate = intent.getIntExtra("heartRate", 0)
            updateHeartRateData(heartRate)
        }
    }

    private val stepsUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val steps = intent.getIntExtra("steps", 0)
            val meters = intent.getIntExtra("meters", 0)
            val calories = intent.getIntExtra("calories", 0)
            updateStepsData(steps, meters, calories)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heart_rate)

        heartRateTextView = findViewById(R.id.heart_rate_text_view)
        stepsTextView = findViewById(R.id.steps_text_view)
        distanceTextView = findViewById(R.id.distance_text_view)
        caloriesTextView = findViewById(R.id.calories_text_view)

        val heartRate = intent.getIntExtra("heartRate", 0)
        val steps = intent.getIntExtra("steps", 0)
        val meters = intent.getIntExtra("meters", 0)
        val calories = intent.getIntExtra("calories", 0)

        updateHeartRateData(heartRate)
        updateStepsData(steps, meters, calories)

        LocalBroadcastManager.getInstance(this).registerReceiver(heartRateUpdateReceiver, IntentFilter("HEART_RATE_UPDATED"))
        LocalBroadcastManager.getInstance(this).registerReceiver(stepsUpdateReceiver, IntentFilter("STEPS_UPDATED"))
    }

    fun updateHeartRateData(heartRate: Int) {
        heartRateTextView.text = "Srdcový rytmus: $heartRate bpm"
    }

    fun updateStepsData(steps: Int, meters: Int, calories: Int) {
        stepsTextView.text = "Kroky: $steps"
        distanceTextView.text = "Vzdialenosť: $meters м"
        caloriesTextView.text = "Kalórie: $calories"
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(heartRateUpdateReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(stepsUpdateReceiver)
    }
}
