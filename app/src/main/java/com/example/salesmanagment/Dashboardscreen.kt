package com.example.salesmanagment

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView

class Dashboardscreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge support
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboardscreen)

        // Handle window insets (Status Bar and Navigation Bar padding)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashboard_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0) // Bottom padding handled by Nav
            insets
        }

        // 1. Initialize views
        val cardSales = findViewById<MaterialCardView>(R.id.cardSales)
        val cardInventory = findViewById<MaterialCardView>(R.id.cardInventory)
        val cardCustomers = findViewById<MaterialCardView>(R.id.cardCustomers)
        val cardReports = findViewById<MaterialCardView>(R.id.cardReports)
        
        // Navigation components (could be null depending on layout-w600dp vs layout)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navRail = findViewById<NavigationRailView>(R.id.navigation_rail)

        setupAdaptiveNavigation(bottomNav, navRail)

        // 2. Set Click Listeners for Cards
        cardSales.setOnClickListener { navigateTo(Transactionscreen::class.java) }
        cardInventory.setOnClickListener { navigateTo(InventoryActivity::class.java) }
        cardCustomers.setOnClickListener { navigateTo(CustomersActivity::class.java) }
        cardReports.setOnClickListener { navigateTo(ReportsActivity::class.java) }

        // 3. Handle Navigation "Tabs" (Responsive)
        bottomNav?.setOnItemSelectedListener { item -> handleNavigation(item.itemId) }
        navRail?.setOnItemSelectedListener { item -> handleNavigation(item.itemId) }
    }

    private fun setupAdaptiveNavigation(bottomNav: BottomNavigationView?, navRail: NavigationRailView?) {
        val isWide = resources.configuration.screenWidthDp >= 600
        val navGuideline = findViewById<androidx.constraintlayout.widget.Guideline>(R.id.nav_guideline)

        if (isWide) {
            navRail?.visibility = android.view.View.VISIBLE
            bottomNav?.visibility = android.view.View.GONE
            // Typical Nav Rail width is 80dp
            val railWidth = (80 * resources.displayMetrics.density).toInt()
            navGuideline?.setGuidelineBegin(railWidth)
        } else {
            navRail?.visibility = android.view.View.GONE
            bottomNav?.visibility = android.view.View.VISIBLE
            navGuideline?.setGuidelineBegin(0)
        }

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
}