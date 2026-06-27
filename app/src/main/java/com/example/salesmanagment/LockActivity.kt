package com.example.salesmanagment

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class LockActivity : AppCompatActivity() {

    private lateinit var biometricHelper: BiometricHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lock)

        biometricHelper = BiometricHelper(this)

        findViewById<Button>(R.id.btnUnlock).setOnClickListener {
            tryUnlock()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Prevent going back without unlocking, move app to background
                moveTaskToBack(true)
            }
        })

        // Auto-show prompt on start
        tryUnlock()
    }

    private fun tryUnlock() {
        if (biometricHelper.isBiometricAvailable()) {
            biometricHelper.showBiometricPrompt(
                onSuccess = {
                    AppLockManager.unlock()
                    finish()
                },
                onError = { error ->
                    Toast.makeText(this, "Unlock failed: $error", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            // Fallback: If no biometrics, just unlock for now or require PIN/Password
            // In a production app, you'd require the device PIN here.
            AppLockManager.unlock()
            finish()
        }
    }
}
