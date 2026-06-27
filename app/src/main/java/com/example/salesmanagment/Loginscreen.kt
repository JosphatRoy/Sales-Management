package com.example.salesmanagment

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Random

class Loginscreen : AppCompatActivity() {

    private lateinit var etUsername: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var cbRememberMe: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loginscreen)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        cbRememberMe = findViewById(R.id.cbRememberMe)
        
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnSignInWithLink = findViewById<Button>(R.id.btnSignInWithLink)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)
        val tvRandomize = findViewById<TextView>(R.id.tvRandomPassword)

        // Load remembered credentials
        loadRememberedCredentials()

        // Start form animations
        animateForm()

        tvRandomize.setOnClickListener {
            etPassword.setText(generateRandomPassword())
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val username = etUsername.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                if (email.isEmpty()) etEmail.error = "Email is required"
                if (password.isEmpty()) etPassword.error = "Password is required"
                return@setOnClickListener
            }

            if (isNetworkAvailable()) {
                // Real Firebase authentication (Online)
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            handleSuccessfulLogin(username, email, password)
                        } else {
                            Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                // Offline Login Attempt
                handleOfflineLogin(email, password)
            }
        }

        btnSignInWithLink.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                if (isNetworkAvailable()) {
                    sendSignInLink(email)
                } else {
                    Toast.makeText(this, "Internet required for email link sign in", Toast.LENGTH_SHORT).show()
                }
            } else {
                etEmail.error = "Email is required for link sign in"
                Toast.makeText(this, "Enter email to receive sign in link", Toast.LENGTH_SHORT).show()
            }
        }

        tvForgotPassword.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                if (isNetworkAvailable()) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Reset email sent", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Internet required to reset password", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Enter email first", Toast.LENGTH_SHORT).show()
            }
        }

        tvSignUp.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty()) {
                etUsername.error = "Username is required for registration"
                return@setOnClickListener
            }

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and Password required to sign up", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isNetworkAvailable()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = task.result?.user
                            
                            // Set display name in Firebase Auth
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build()
                                
                            user?.updateProfile(profileUpdates)
                            
                            saveUserCredentialsToFirestore(username, email, password)
                            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, HomeActivity::class.java).apply {
                                putExtra("EXTRA_USERNAME", username)
                            }
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Internet required for registration", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun animateForm() {
        val logo = findViewById<View>(R.id.login_logo)
        val title = findViewById<View>(R.id.login_title)
        val username = findViewById<View>(R.id.usernameLayout)
        val email = findViewById<View>(R.id.emailLayout)
        val password = findViewById<View>(R.id.passwordLayout)
        val options = findViewById<View>(R.id.login_options)
        val loginBtn = findViewById<View>(R.id.btnLogin)
        val linkBtn = findViewById<View>(R.id.btnSignInWithLink)
        val forgot = findViewById<View>(R.id.tvForgotPassword)
        val signup = findViewById<View>(R.id.tvSignUp)

        val views = listOf(logo, title, username, email, password, options, loginBtn, linkBtn, forgot, signup)

        // Initial state: invisible and shifted down
        views.forEach {
            it.alpha = 0f
            it.translationY = 50f
        }

        // Animate them one by one
        views.forEachIndexed { index, view ->
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay(100L * index)
                .setInterpolator(android.view.animation.DecelerateInterpolator())
                .start()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    private fun handleOfflineLogin(email: String, pass: String) {
        val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
        val savedEmail = prefs.getString("remembered_email", "")
        val savedPass = prefs.getString("remembered_password", "")

        if (email == savedEmail && pass == savedPass && savedEmail != "") {
            Toast.makeText(this, "Logged in (Offline Mode)", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Internet required for first-time login or non-remembered accounts", Toast.LENGTH_LONG).show()
        }
    }

    private fun handleSuccessfulLogin(username: String, email: String, pass: String) {
        if (cbRememberMe.isChecked) {
            val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
            prefs.edit().apply {
                putString("remembered_username", username)
                putString("remembered_email", email)
                putString("remembered_password", pass)
                putBoolean("is_remembered", true)
                apply()
            }
        } else {
            getSharedPreferences("login_prefs", MODE_PRIVATE).edit().clear().apply()
        }

        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("EXTRA_USERNAME", username)
        }
        startActivity(intent)
        finish()
    }

    private fun loadRememberedCredentials() {
        val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
        if (prefs.getBoolean("is_remembered", false)) {
            etUsername.setText(prefs.getString("remembered_username", ""))
            etEmail.setText(prefs.getString("remembered_email", ""))
            etPassword.setText(prefs.getString("remembered_password", ""))
            cbRememberMe.isChecked = true
        }
    }

    private fun generateRandomPassword(): String {
        val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*"
        val sb = StringBuilder()
        val random = Random()
        for (i in 0 until 10) {
            sb.append(chars[random.nextInt(chars.length)])
        }
        return sb.toString()
    }

    private fun saveUserCredentialsToFirestore(username: String, email: String, pass: String) {
        val userData = hashMapOf(
            "username" to username,
            "email" to email,
            "password" to pass,
            "type" to "standard",
            "timestamp" to com.google.firebase.Timestamp.now()
        )
        FirebaseFirestore.getInstance().collection("user_accounts")
            .document(email)
            .set(userData)
    }

    private fun sendSignInLink(email: String) {
        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setUrl("https://auth.example.com/finishSignUp?email=$email")
            .setHandleCodeInApp(true)
            .setAndroidPackageName("com.example.salesmanagment", true, "24")
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
