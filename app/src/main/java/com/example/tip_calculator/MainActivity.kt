package com.example.tip_calculator

import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tip_calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // View Binding object
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set SeekBar range: 0% to 35%
        binding.seekBar.max = 35    // range from 0% to 35%
        binding.seekBar.progress = 15 // default to 15%

        // Set SeekBar Listener
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateTipAndTotal()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Set Base Amount Input Listener
        binding.baseAmountInput.setOnKeyListener { _, _, _ ->
            updateTipAndTotal()
            false
        }

    }

    private fun updateTipAndTotal() {
        val baseAmount = binding.baseAmountInput.text.toString().toDoubleOrNull() ?: 0.0
        val tipPercentage = binding.seekBar.progress / 100.0

        // Tip percent from SeekBar (actual = 10% + progress)
        val tipPercent = binding.seekBar.progress
        binding.tipAmountValue.text = "$tipPercent%"

        if(baseAmount == null) {
            binding.tipAmountResult.text = ""
            binding.totalAmountResult.text = ""
            return
        }

        // Update tip comment
        if(tipPercentage <= 0.05) {
            binding.tipComment.text = "Poor"
            binding.tipComment.setTextColor(getColor(R.color.comment_color_poor))
        } else if(tipPercentage <= 0.12) {
            binding.tipComment.text = "Acceptable"
            binding.tipComment.setTextColor(getColor(R.color.comment_color_acceptable))
        } else if(tipPercentage <= 0.18) {
            binding.tipComment.text = "Good"
            binding.tipComment.setTextColor(getColor(R.color.comment_color_good))
        } else {
            binding.tipComment.text = "Amazing!"
            binding.tipComment.setTextColor(getColor(R.color.comment_color_amazing))
        }

        val tipAmount = baseAmount * tipPercentage
        val totalAmount = baseAmount + tipAmount

        // Display tip and total amount
        binding.tipAmountResult.text = "%.2f".format(tipAmount)
        binding.totalAmountResult.text = "%.2f".format(totalAmount)
    }
}