package com.example.salesmanagment

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth

class Loginscreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loginscreen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnSignInWithLink = findViewById<Button>(R.id.btnSignInWithLink)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty()) {
                etEmail.error = "Email is required"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "Password is required"
                return@setOnClickListener
            }

            // Real Firebase authentication
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Dashboardscreen::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        btnSignInWithLink.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                sendSignInLink(email)
            } else {
                etEmail.error = "Email is required for link sign in"
                Toast.makeText(this, "Enter email to receive sign in link", Toast.LENGTH_SHORT).show()
            }
        }

        tvForgotPassword.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Reset email sent", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Enter email first", Toast.LENGTH_SHORT).show()
            }
        }

        tvSignUp.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and Password required to sign up", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Dashboardscreen::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun sendSignInLink(email: String) {
        val actionCodeSettings = ActionCodeSettings.newBuilder()
            // Using a verified domain instead of deprecated *.page.link
            .setUrl("https://auth.example.com/finishSignUp?email=$email")
            .setHandleCodeInApp(true)
            .setAndroidPackageName(
                "com.example.salesmanagment",
                true, // installIfNotAvailable
                "24"  // minimumVersion
            )
            .build()

        FirebaseAuth.getInstance().sendSignInLinkToEmail(email, actionCodeSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Verification email sent", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}