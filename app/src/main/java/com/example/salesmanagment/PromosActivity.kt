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

class PromosActivity : AppCompatActivity() {

    private val promoDeals = listOf(
        CategoryData("Online Exclusives", "Exclusive deals only available on our online platform.", R.drawable.ic_shopping_cart, "#FF5722"),
        CategoryData("Private Label", "High quality products from our own brand at lower prices.", R.drawable.ic_inventory, "#795548"),
        CategoryData("Electronics Deals", "Save big on the latest gadgets and appliances.", R.drawable.ic_inventory, "#F44336"),
        CategoryData("Fruits & Vegetables Deals", "Freshly picked produce at discounted rates.", R.drawable.ic_shopping_cart, "#4CAF50"),
        CategoryData("Food cupboard deals", "Stock up your pantry with these amazing offers.", R.drawable.ic_shopping_cart, "#FF9800"),
        CategoryData("Fresh Deals", "Daily discounts on meats, dairy, and bakery items.", R.drawable.ic_shopping_cart, "#8BC34A"),
        CategoryData("Dairy Product Deals", "Great prices on milk, cheese, and yogurt.", R.drawable.ic_shopping_cart, "#2196F3"),
        CategoryData("Beauty & Cosmetics Deals", "Revitalize your routine with our beauty offers.", R.drawable.ic_people, "#E91E63"),
        CategoryData("Beverage Deals", "Refreshing drinks and sodas on sale.", R.drawable.ic_shopping_cart, "#03A9F4"),
        CategoryData("Liquor Deals", "Selected wines and spirits at promotional prices.", R.drawable.ic_shopping_cart, "#B71C1C"),
        CategoryData("Stationery Deals", "Essential school and office supplies on offer.", R.drawable.ic_inventory, "#FFD700"),
        CategoryData("Cleaning Deals", "Keep your home spotless for less.", R.drawable.ic_inventory, "#607D8B"),
        CategoryData("Snacks Deals", "Delicious treats and bites at reduced costs.", R.drawable.ic_shopping_cart, "#FFC107"),
        CategoryData("Baby & Kids Deals", "Best for your little ones at the best prices.", R.drawable.ic_people, "#9C27B0")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_promos)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.promos_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupToolbar()
        setupPromoList()
        setupNavigation()
    }

    private fun setupToolbar() {
        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupPromoList() {
        val rv = findViewById<RecyclerView>(R.id.rvPromos)
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = GridLayoutManager(this, columns)
        
        rv.adapter = CategoryAdapter(promoDeals) { deal ->
            val intent = Intent(this, InventoryActivity::class.java)
            intent.putExtra("FILTER_CATEGORY", deal.name)
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
