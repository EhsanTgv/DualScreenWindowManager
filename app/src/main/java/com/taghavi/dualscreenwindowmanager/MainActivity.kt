package com.taghavi.dualscreenwindowmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowMetricsCalculator
import com.taghavi.dualscreenwindowmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var windowInfoTracker: WindowInfoTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        windowInfoTracker = WindowInfoTracker.getOrCreate(this@MainActivity)

        obtainWindowMetrics()
    }

    private fun obtainWindowMetrics() {
        val wmc = WindowMetricsCalculator.getOrCreate()
        val currentWM = wmc.computeCurrentWindowMetrics(this).bounds.flattenToString()
        val maximumWM = wmc.computeMaximumWindowMetrics(this).bounds.flattenToString()
        binding.windowMetrics.text =
            "CurrentWindowMetrics: ${currentWM}\nMaximumWindowMetrics: $maximumWM"
    }
}