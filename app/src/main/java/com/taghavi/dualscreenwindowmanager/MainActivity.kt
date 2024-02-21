package com.taghavi.dualscreenwindowmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.window.layout.WindowInfoTracker

class MainActivity : AppCompatActivity() {
    private lateinit var windowInfoTracker: WindowInfoTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        windowInfoTracker = WindowInfoTracker.getOrCreate(this@MainActivity)
    }
}