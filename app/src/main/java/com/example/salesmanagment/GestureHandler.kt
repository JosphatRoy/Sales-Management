package com.example.salesmanagment

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class GestureHandler(
    context: Context,
    private val logger: EventLogger,
    private val display: MessageDisplay
) : GestureDetector.SimpleOnGestureListener() {

    private val detector = GestureDetector(context, this)

    fun attachToView(view: View) {
        view.setOnTouchListener { _, event ->
            detector.onTouchEvent(event)
            true
        }
    }

    override fun onDown(e: MotionEvent): Boolean {
        logger.log("Gesture: onDown")
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        val msg = "Gesture: Single Tap detected"
        logger.log(msg)
        display.showMessage(msg)
        return true
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        val msg = "Gesture: Double Tap detected"
        logger.log(msg)
        display.showMessage(msg)
        return true
    }

    override fun onLongPress(e: MotionEvent) {
        val msg = "Gesture: Long Press detected"
        logger.log(msg)
        display.showMessage(msg)
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val msg = "Gesture: Fling (Swipe) detected"
        logger.log(msg)
        display.showMessage(msg)
        return true
    }
}
