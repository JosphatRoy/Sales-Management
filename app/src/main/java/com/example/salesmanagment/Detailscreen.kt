package com.example.salesmanagment

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.bottomnavigation.BottomNavigationView

class Detailscreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detailscreen)

        // Handle System Bar Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0) // Bottom handled by Nav
            insets
        }

        setupToolbar()
        displayData()
        setupActionButtons()
        setupNavigation()
    }

    private fun setupNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navRail = findViewById<com.google.android.material.navigationrail.NavigationRailView>(R.id.navigation_rail)

        // Set no selected item as this is a detail screen
        bottomNav?.selectedItemId = -1 
        navRail?.selectedItemId = -1
        
        bottomNav?.setOnItemSelectedListener { item -> handleNavigation(item.itemId) }
        navRail?.setOnItemSelectedListener { item -> handleNavigation(item.itemId) }
    }

    private fun handleNavigation(itemId: Int): Boolean {
        return when (itemId) {
            R.id.nav_home -> { navigateTo(Transactionscreen::class.java); true }
            R.id.nav_inventory -> { navigateTo(InventoryActivity::class.java); true }
            R.id.nav_customers -> { navigateTo(CustomersActivity::class.java); true }
            R.id.nav_reports -> { navigateTo(ReportsActivity::class.java); true }
            else -> false
        }
    }

    private fun navigateTo(cls: Class<*>) {
        val intent = Intent(this, cls)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun displayData() {
        // References to views (Already populated by XML placeholders, but can be set here)
        val tvProductName = findViewById<TextView>(R.id.tvDetailProductName)
        val tvTotalAmount = findViewById<TextView>(R.id.tvDetailTotalAmount)
        
        // Mock: You could use intent.getStringExtra() here
    }

    private fun setupActionButtons() {
        // Floating Action Button - Edit
        findViewById<FloatingActionButton>(R.id.fabEdit).setOnClickListener {
            Toast.makeText(this, "Edit functionality triggered", Toast.LENGTH_SHORT).show()
        }

        // Share Receipt Button
        findViewById<MaterialButton>(R.id.btnShareReceipt).setOnClickListener {
            val shareText = "Transaction Details: ${findViewById<TextView>(R.id.tvDetailProductName).text} - Total: ${findViewById<TextView>(R.id.tvDetailTotalAmount).text}"
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(intent, "Share via"))
        }
    }
}