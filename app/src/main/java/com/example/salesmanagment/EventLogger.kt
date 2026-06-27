package com.example.salesmanagment

import android.util.Log

class EventLogger(private val tag: String = "InteractionDemo") {
    fun log(message: String) {
        Log.d(tag, message)
    }
}
