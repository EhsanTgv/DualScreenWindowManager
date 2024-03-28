package com.taghavi.dualscreenwindowmanager

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import androidx.window.layout.WindowMetricsCalculator
import com.taghavi.dualscreenwindowmanager.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var windowInfoTracker: WindowInfoTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        windowInfoTracker = WindowInfoTracker.getOrCreate(this@MainActivity)

        obtainWindowMetrics()
        onWindowLayoutInfoChange()
    }

    private fun obtainWindowMetrics() {
        val wmc = WindowMetricsCalculator.getOrCreate()
        val currentWM = wmc.computeCurrentWindowMetrics(this).bounds.flattenToString()
        val maximumWM = wmc.computeMaximumWindowMetrics(this).bounds.flattenToString()
        binding.windowMetrics.text =
            "CurrentWindowMetrics: ${currentWM}\nMaximumWindowMetrics: $maximumWM"
    }

    private fun onWindowLayoutInfoChange() {
        lifecycleScope.launch(Dispatchers.Main) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                windowInfoTracker.windowLayoutInfo(this@MainActivity)
                    .collect { value ->
                        updateUI(value)
                    }
            }
        }
    }

    private fun updateUI(newLayoutInfo: WindowLayoutInfo) {
        binding.layoutChange.text = newLayoutInfo.toString()
        if (newLayoutInfo.displayFeatures.isNotEmpty()) {
            binding.configurationChanged.text = "Spanned across displays"
            alignViewToFoldingFeatureBounds(newLayoutInfo)
        } else {
            binding.configurationChanged.text = "One logic/physical display - unspanned"
        }
    }

    private fun alignViewToFoldingFeatureBounds(newLayoutInfo: WindowLayoutInfo) {
        val constraintLayout = binding.constraintLayout
        val set = ConstraintSet()
        set.clone(constraintLayout)

        val foldingFeature = newLayoutInfo.displayFeatures[0] as FoldingFeature
        val bounds = getFeatureBoundsInWindow(foldingFeature, binding.root)

        bounds?.let { rect ->
            val horizontalFoldingFeatureHeight = (rect.bottom - rect.top).coerceAtLeast(1)
            val verticalFoldingFeatureWidth = (rect.right - rect.left).coerceAtLeast(1)

            set.constrainHeight(
                R.id.folding_feature,
                horizontalFoldingFeatureHeight,
            )
            set.constrainWidth(
                R.id.folding_feature,
                verticalFoldingFeatureWidth,
            )

            set.connect(
                R.id.folding_feature,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START,
                0,
            )
            set.connect(
                R.id.folding_feature,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP,
                0,
            )

            if (foldingFeature.orientation == FoldingFeature.Orientation.VERTICAL) {
                set.setMargin(R.id.folding_feature, ConstraintSet.START, rect.left)
                set.connect(
                    R.id.layout_change,
                    ConstraintSet.END,
                    R.id.folding_feature,
                    ConstraintSet.START,
                    0,
                )
            } else {
                set.setMargin(R.id.folding_feature, ConstraintSet.TOP, rect.top)
                set.connect(
                    R.id.layout_change,
                    ConstraintSet.TOP,
                    R.id.folding_feature,
                    ConstraintSet.BOTTOM,
                    0
                )
            }

            set.setVisibility(R.id.folding_feature, View.VISIBLE)
            set.applyTo(constraintLayout)
        }
    }

    private fun getFeatureBoundsInWindow(
        displayFeature: DisplayFeature,
        view: View,
        includePadding: Boolean = true,
    ): Rect? {
        val viewLocationInWindow = IntArray(2)
        view.getLocationInWindow(viewLocationInWindow)

        val viewRect = Rect(
            viewLocationInWindow[0],
            viewLocationInWindow[1],
            viewLocationInWindow[0] + view.width,
            viewLocationInWindow[1] + view.height,
        )

        if (includePadding) {
            viewRect.left += view.paddingLeft
            viewRect.top += view.paddingTop
            viewRect.right += view.paddingRight
            viewRect.bottom += view.paddingBottom
        }

        val featureRectInView = Rect(displayFeature.bounds)
        val intersects = featureRectInView.intersect(viewRect)

        if ((featureRectInView.width() == 0 && featureRectInView.height() == 0) || !intersects) {
            return null
        }

        featureRectInView.offset(-viewLocationInWindow[0], -viewLocationInWindow[1])

        return featureRectInView
    }
}