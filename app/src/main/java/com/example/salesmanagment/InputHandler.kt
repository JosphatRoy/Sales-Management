package com.example.salesmanagment

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView

class InputHandler(
    private val logger: EventLogger,
    private val display: MessageDisplay
) {
    fun setupInputListener(textView: TextView) {
        textView.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || 
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val input = v.text.toString()
                val logMsg = "Keyboard Input: $input"
                logger.log(logMsg)
                display.showMessage("Captured: $input")
                true
            } else {
                false
            }
        }
    }
}
