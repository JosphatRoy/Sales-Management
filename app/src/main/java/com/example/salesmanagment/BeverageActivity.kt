package com.example.salesmanagment

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView

class BeverageActivity : AppCompatActivity() {

    private val beverageItems = listOf(
        // Cold Beverages
        CategoryData("Water", "Pure, Mineral, Flavoured, and Sparkling Water.", R.drawable.ic_shopping_cart, "#2196F3"),
        CategoryData("Juices & Syrups", "Ready-to-Drink, Sparkling Juices, and Concentrates.", R.drawable.ic_shopping_cart, "#03A9F4"),
        CategoryData("Soft & Energy Drinks", "Soda, Energy, and Sports Drinks.", R.drawable.ic_shopping_cart, "#00BCD4"),
        
        // Hot Beverages
        CategoryData("Coffee & Tea", "Grounded Coffee, Tea Bags, and specialty teas.", R.drawable.ic_shopping_cart, "#795548"),
        CategoryData("Chocolates & Cocoas", "Pure Cocoa and Drinking Chocolate.", R.drawable.ic_shopping_cart, "#5D4037"),
        CategoryData("Plant-Based Powders", "Soya Drinks and nutritious powders.", R.drawable.ic_shopping_cart, "#8D6E63")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_beverage)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.beverage_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupToolbar()
        setupBeverageList()
        setupNavigation()
    }

    private fun setupToolbar() {
        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupBeverageList() {
        val rv = findViewById<RecyclerView>(R.id.rvBeverages)
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = GridLayoutManager(this, columns)
        
        rv.adapter = CategoryAdapter(beverageItems) { item ->
            val intent = Intent(this, InventoryActivity::class.java)
            intent.putExtra("FILTER_CATEGORY", item.name)
            startActivity(intent)
        }
    }

    private fun setupNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navRail = findViewById<NavigationRailView>(R.id.navigation_rail)

        bottomNav?.setOnItemSelectedListener { item -> handleNavigation(item.itemId) }
        navRail?.setOnItemSelectedListener { item -> handleNavigation(item.itemId) }
    }

    private fun handleNavigation(itemId: Int): Boolean {
        return when (itemId) {
            R.id.nav_home -> { navigateTo(HomeActivity::class.java); true }
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
