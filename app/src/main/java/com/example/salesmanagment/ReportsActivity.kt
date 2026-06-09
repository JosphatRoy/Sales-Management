package com.example.salesmanagment

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView

class ReportsActivity : AppCompatActivity() {

    private lateinit var topProductsAdapter: InventoryAdapter
    private val topProducts = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reports)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.reports_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupNavigation()
        setupTopProductsList()
        
        // Mock data for report
        loadReportData()
    }

    private fun setupTopProductsList() {
        val rv = findViewById<RecyclerView>(R.id.rvTopProducts)
        
        // Use 2 columns for the top products on tablets too if it makes sense
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = if (columns > 2) GridLayoutManager(this, 2) else LinearLayoutManager(this)
        
        topProductsAdapter = InventoryAdapter(topProducts)
        rv.adapter = topProductsAdapter
    }

    private fun loadReportData() {
        // Sales Summary
        findViewById<TextView>(R.id.tvTotalRevenue).text = "$ 12,450.00"
        findViewById<TextView>(R.id.tvTotalTransactions).text = "156 Transactions"
        
        // Inventory Summary
        findViewById<TextView>(R.id.tvInventoryValue).text = "$ 45,200.00"
        findViewById<TextView>(R.id.tvLowStockCount).text = "5 Low Stock Items"

        // Mocking top selling items
        topProducts.addAll(listOf(
            Product("1", "Sugar", "Groceries", 2.50, 450),
            Product("5", "Rice", "Groceries", 10.00, 320),
            Product("2", "Milk", "Dairy", 1.20, 280),
            Product("4", "Eggs", "Dairy", 3.00, 150)
        ))
        topProductsAdapter.updateList(topProducts)
    }

    private fun setupNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navRail = findViewById<NavigationRailView>(R.id.navigation_rail)

        bottomNav?.selectedItemId = R.id.nav_reports
        navRail?.selectedItemId = R.id.nav_reports

        bottomNav?.setOnItemSelectedListener { item -> handleNavigation(item.itemId) }
        navRail?.setOnItemSelectedListener { item -> handleNavigation(item.itemId) }
    }

    private fun handleNavigation(itemId: Int): Boolean {
        if (itemId == R.id.nav_reports) return true

        return when (itemId) {
            R.id.nav_home -> { navigateTo(Transactionscreen::class.java); true }
            R.id.nav_inventory -> { navigateTo(InventoryActivity::class.java); true }
            R.id.nav_customers -> { navigateTo(CustomersActivity::class.java); true }
            else -> false
        }
    }

    private fun navigateTo(cls: Class<*>) {
        val intent = Intent(this, cls)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
    }
}