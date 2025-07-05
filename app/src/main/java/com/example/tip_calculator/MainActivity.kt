package com.example.tip_calculator

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
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

        // Handle Keyboard Dismissal for Bill Amount Input
        binding.baseAmountInput.setOnEditorActionListener { v, actionId, event ->
            if(actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                updateTipAndTotal()
                // Hide Keyboard
                binding.baseAmountInput.clearFocus()
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.baseAmountInput.windowToken, 0)
                true
            } else {
                false
            }
        }

        // Set SeekBar range: 0% to 35%
        binding.seekBar.max = 35    // range from 0% to 35%
        binding.seekBar.progress = 15 // default to 15%

        // Set People Dropdown Options
        // Load people options array from strings.xml
        val peopleOptions = resources.getStringArray(R.array.people_options)
        val adapter = ArrayAdapter.createFromResource(this, R.array.people_options, android.R.layout.simple_spinner_item)

        // Set layout for dropdown items
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Bind adapter to dropdown
        binding.peopleDropdown.adapter = adapter


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

        // Set Number of People Input Listener
        binding.peopleDropdown.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                updatePerPersonAmount()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }

    }

    private fun updateTipAndTotal() {
        val baseAmount = binding.baseAmountInput.text.toString().toDoubleOrNull() ?: 0.0
        val tipPercentage = binding.seekBar.progress / 100.0
        val shoppingTaxRate = 0.0625

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
        val taxAmount = baseAmount * shoppingTaxRate
        val totalAmount = baseAmount + tipAmount + taxAmount

        // Display tip and total amount
        binding.tipAmountResult.text = "%.2f".format(tipAmount)
        binding.taxAmountResult.text = "%.2f".format(taxAmount)
        binding.totalAmountResult.text = "%.2f".format(totalAmount)

        // Update per person amount
        updatePerPersonAmount()
    }

    private fun updatePerPersonAmount() {
        val totalAmount = binding.totalAmountResult.text.toString().toDoubleOrNull() ?: 0.0
        val numPeople = binding.peopleDropdown.selectedItem.toString().toIntOrNull() ?: 1

        if(numPeople == null) {
            binding.perPersonAmountResult.text = ""
            return
        }

        // Calculate per person amount
        val perPersonAmount = totalAmount / numPeople

        // Display per person amount
        binding.perPersonAmountResult.text = "%.2f".format(perPersonAmount)
    }
}