package com.example.salesmanagment

import android.os.Bundle
import android.widget.EditText
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class InteractionHandlerActivity : AppCompatActivity() {

    private lateinit var logger: EventLogger
    private lateinit var display: MessageDisplay
    private lateinit var inputHandler: InputHandler
    private lateinit var gestureHandler: GestureHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_interaction_handler)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.interaction_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize core components according to OOP principles
        logger = EventLogger()
        display = ToastDisplay(this)
        
        // Setup specialized handlers
        inputHandler = InputHandler(logger, display)
        gestureHandler = GestureHandler(this, logger, display)

        val etInput = findViewById<EditText>(R.id.etInteractionInput)
        val gestureArea = findViewById<FrameLayout>(R.id.gestureArea)

        // Connect handlers to UI components
        inputHandler.setupInputListener(etInput)
        gestureHandler.attachToView(gestureArea)

        logger.log("InteractionHandlerActivity created and initialized.")
    }
}
