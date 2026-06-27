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

class FreshFoodActivity : AppCompatActivity() {

    private val freshFoodItems = listOf(
        // 1. Meat & Seafood
        CategoryData("Fresh Meat Cuts", "Beef, Fish, Chicken, Pork, Goat, Rabbit.", R.drawable.ic_shopping_cart, "#F44336"),
        CategoryData("Processed Meat", "Bacon, Sausages, Smokies, Hams, Brawns.", R.drawable.ic_shopping_cart, "#D32F2F"),
        CategoryData("Fish (Omena) & Seafood", "Fresh and dried fish options.", R.drawable.ic_shopping_cart, "#C62828"),
        CategoryData("Meat Substitutes", "Plant-based meat alternatives.", R.drawable.ic_shopping_cart, "#B71C1C"),
        
        // 2. Bakery & Deli
        CategoryData("Bread & Rolls", "Freshly baked bread, rolls, buns, scones.", R.drawable.ic_shopping_cart, "#8D6E63"),
        CategoryData("Cakes & Pastries", "Cakes, muffins, and sweet pastries.", R.drawable.ic_shopping_cart, "#795548"),
        CategoryData("Deli & Snacks", "Deli snacks and cold deli items.", R.drawable.ic_shopping_cart, "#6D4C41"),
        
        // 3. Produce
        CategoryData("Fruits", "Seasonal and exotic fresh fruits.", R.drawable.ic_shopping_cart, "#4CAF50"),
        CategoryData("Vegetables", "Fresh green vegetables and roots.", R.drawable.ic_shopping_cart, "#388E3C"),
        CategoryData("Fresh Salads", "Ready-to-eat garden and fruit salads.", R.drawable.ic_shopping_cart, "#2E7D32"),
        
        // 4. Dairy & Essentials
        CategoryData("Milk & Substitutes", "Fresh, Longlife, Evaporated, and plant milk.", R.drawable.ic_shopping_cart, "#2196F3"),
        CategoryData("Yoghurt & Cream", "Flavored yoghurts and cooking creams.", R.drawable.ic_shopping_cart, "#1976D2"),
        CategoryData("Butter, Ghee & Cheese", "Dairy spreads and various cheeses.", R.drawable.ic_shopping_cart, "#1565C0"),
        CategoryData("Ice Cream", "Frozen treats and desserts.", R.drawable.ic_shopping_cart, "#0D47A1")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_fresh_food)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fresh_food_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupToolbar()
        setupFreshFoodList()
        setupNavigation()
    }

    private fun setupToolbar() {
        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupFreshFoodList() {
        val rv = findViewById<RecyclerView>(R.id.rvFreshFood)
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = GridLayoutManager(this, columns)
        
        rv.adapter = CategoryAdapter(freshFoodItems) { item ->
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
