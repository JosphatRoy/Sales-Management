package com.example.salesmanagment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.navigationrail.NavigationRailView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupGreeting()
        setupSummary()
        setupQuickActions()
        setupNavigation()
    }

    private fun setupGreeting() {
        val passedUsername = intent.getStringExtra("EXTRA_USERNAME")
        val tvWelcome = findViewById<TextView>(R.id.tvHomeWelcome)

        if (!passedUsername.isNullOrEmpty()) {
            tvWelcome.text = "Welcome, $passedUsername"
            return
        }

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val displayName = currentUser.displayName
            if (!displayName.isNullOrEmpty()) {
                tvWelcome.text = "Welcome, $displayName"
            } else {
                db.collection("user_accounts").document(currentUser.email ?: "")
                    .get()
                    .addOnSuccessListener { doc ->
                        val name = doc.getString("username") ?: "Owner"
                        tvWelcome.text = "Welcome, $name"
                    }
            }
        }
    }

    private fun setupSummary() {
        val currentUser = auth.currentUser ?: return
        
        // 1. Fetch Today's Sales
        db.collection("users").document(currentUser.uid).collection("sales")
            .get()
            .addOnSuccessListener { result ->
                val sales = result.toObjects(Sale::class.java)
                val total = sales.sumOf { it.totalAmount }
                findViewById<TextView>(R.id.tvHomeTodaySales).text = String.format(Locale.getDefault(), "Ksh %.2f", total)
            }

        // 2. Fetch Total Customers
        db.collection("users").document(currentUser.uid).collection("customers")
            .get()
            .addOnSuccessListener { result ->
                findViewById<TextView>(R.id.tvHomeTotalCustomers).text = result.size().toString()
            }
    }

    private fun setupQuickActions() {
        findViewById<MaterialCardView>(R.id.actionNewSale).setOnClickListener { navigateTo(Transactionscreen::class.java) }
        findViewById<MaterialCardView>(R.id.actionInventory).setOnClickListener { navigateTo(InventoryActivity::class.java) }
        findViewById<MaterialCardView>(R.id.actionCustomers).setOnClickListener { navigateTo(CustomersActivity::class.java) }
        findViewById<MaterialCardView>(R.id.actionReports).setOnClickListener { navigateTo(ReportsActivity::class.java) }
        findViewById<MaterialCardView>(R.id.actionCategories).setOnClickListener { navigateTo(CategoryActivity::class.java) }
    }

    private fun setupNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navRail = findViewById<NavigationRailView>(R.id.navigation_rail)

        setupAdaptiveNavigation(bottomNav, navRail)
        
        bottomNav?.selectedItemId = R.id.nav_home
        navRail?.selectedItemId = R.id.nav_home
    }

    private fun setupAdaptiveNavigation(bottomNav: BottomNavigationView?, navRail: NavigationRailView?) {
        val isWide = resources.configuration.screenWidthDp >= 600
        val navGuideline = findViewById<androidx.constraintlayout.widget.Guideline>(R.id.nav_guideline)

        if (isWide) {
            navRail?.visibility = View.VISIBLE
            bottomNav?.visibility = View.GONE
            val railWidth = (80 * resources.displayMetrics.density).toInt()
            navGuideline?.setGuidelineBegin(railWidth)
        } else {
            navRail?.visibility = View.GONE
            bottomNav?.visibility = View.VISIBLE
            navGuideline?.setGuidelineBegin(0)
        }

        bottomNav?.setOnItemSelectedListener { item -> handleNavigation(item.itemId) }
        navRail?.setOnItemSelectedListener { item -> handleNavigation(item.itemId) }
    }

    private fun handleNavigation(itemId: Int): Boolean {
        if (itemId == R.id.nav_home) return true

        return when (itemId) {
            R.id.nav_sales -> { navigateTo(Transactionscreen::class.java); true }
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
