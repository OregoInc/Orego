package com.orego.corporation.orego.utils

import android.annotation.SuppressLint
import android.content.Context
import android.support.design.widget.BottomSheetBehavior
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener

class SwipeListener(c: Context, bottomSheetBehavior: BottomSheetBehavior<*>) : OnTouchListener {
    val SWIPE_THRESHOLD = 100
    val SWIPE_VELOCITY_THRESHOLD = 100
    var mBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private val gestureDetector: GestureDetector

    init {
        mBottomSheetBehavior = bottomSheetBehavior
        gestureDetector = GestureDetector(c, GestureListener())
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(motionEvent)
    }

    private inner class GestureListener : SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        // Determines the fling velocity and then fires the appropriate swipe event accordingly
        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            val result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (Math.abs(diffX) < Math.abs(diffY)) {
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY <= 0) {
                            onSwipeUp()
                        }
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            return result
        }
    }

    fun onSwipeUp() {
        mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
    }
}