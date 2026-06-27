package com.example.salesmanagment

import android.content.Context
import android.widget.Toast

interface MessageDisplay {
    fun showMessage(text: String)
}

class ToastDisplay(private val context: Context) : MessageDisplay {
    override fun showMessage(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}
