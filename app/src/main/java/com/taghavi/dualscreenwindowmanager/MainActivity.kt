package com.taghavi.dualscreenwindowmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.window.layout.WindowInfoTracker
import com.taghavi.dualscreenwindowmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var windowInfoTracker: WindowInfoTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        windowInfoTracker = WindowInfoTracker.getOrCreate(this@MainActivity)
    }
}