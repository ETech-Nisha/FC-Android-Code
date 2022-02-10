package com.fractal.chaoss.util

import android.app.Activity
import android.util.DisplayMetrics


class Screen(private val activity: Activity) : ScreenInterface {
    private fun getScreenDimension(activity: Activity): DisplayMetrics {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics


    }

    private fun getScreenDensity(activity: Activity): Float {
        return activity.resources.displayMetrics.density
    }

    override fun getWidth(): Float {
        val displayMetrics = getScreenDimension(activity)
        //return displayMetrics.widthPixels / getScreenDensity(activity)
        return displayMetrics.widthPixels.toFloat()
    }

    override fun getHeight(): Float {
        val displayMetrics = getScreenDimension(activity)
      //  return displayMetrics.heightPixels / getScreenDensity(activity)
        return displayMetrics.heightPixels.toFloat()
    }

}

