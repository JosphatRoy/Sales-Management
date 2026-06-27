package com.example.salesmanagment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splash)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Check if user is already logged in (Firebase persists sessions offline)
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Delay for 2 seconds and then decide where to go
        Handler(Looper.getMainLooper()).postDelayed({
            if (currentUser != null) {
                // User is already authenticated (session exists locally)
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                // No session found, go to Login
                startActivity(Intent(this, Loginscreen::class.java))
            }
            finish()
        }, 2000)
    }
}
