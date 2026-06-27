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

class FoodCupboardActivity : AppCompatActivity() {

    private val foodCupboardItems = listOf(
        // 1. Pantry Staples & Commodities
        CategoryData("Flour, Rice & Cereals", "Pantry staples and basic commodities.", R.drawable.ic_shopping_cart, "#FF9800"),
        CategoryData("Sugar & Sweeteners", "Sweetening agents and sugars.", R.drawable.ic_shopping_cart, "#FFB74D"),
        CategoryData("Pasta & Noodles", "Dried pasta, noodles, and spaghetti.", R.drawable.ic_shopping_cart, "#FFA726"),
        CategoryData("Vegetable Oils & Fats", "Cooking oils, fats, and margarines.", R.drawable.ic_shopping_cart, "#FB8C00"),
        CategoryData("Weighed Spices", "Food additives and seasonings.", R.drawable.ic_shopping_cart, "#F57C00"),
        
        // 2. Breakfast & Spreads
        CategoryData("Breakfast Cereals & Eggs", "Start your day with cereals and fresh eggs.", R.drawable.ic_shopping_cart, "#EF6C00"),
        CategoryData("Honey & Syrup", "Natural honey and sweet syrups.", R.drawable.ic_shopping_cart, "#E65100"),
        
        // 3. Snacks & Confectionery
        CategoryData("Snacks", "Crisps, biscuits, cookies, and nuts.", R.drawable.ic_shopping_cart, "#FF9800"),
        CategoryData("Confectionery", "Sweets, chocolates, and candies.", R.drawable.ic_shopping_cart, "#FFCC80"),
        
        // 4. Dried Foods, Nuts & Seeds
        CategoryData("Dried Fruits & Seeds", "Healthy dried nuts, seeds, and fruits.", R.drawable.ic_shopping_cart, "#FFE0B2"),
        CategoryData("Dry Cereals", "Unprocessed dried cereals.", R.drawable.ic_shopping_cart, "#FFF3E0"),
        
        // 5. Meals, Canned & Frozen Foods
        CategoryData("Soups", "Canned and instant soup varieties.", R.drawable.ic_shopping_cart, "#FF9800"),
        CategoryData("Frozen Foods", "Ready-to-cook frozen meals and sides.", R.drawable.ic_inventory, "#2196F3"),
        CategoryData("Canned Vegetables & Meat", "Long-shelf-life canned produce and meat.", R.drawable.ic_inventory, "#607D8B"),
        CategoryData("Sardines & Tuna", "Canned fish and seafood.", R.drawable.ic_inventory, "#0288D1")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_food_cupboard)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.food_cupboard_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupToolbar()
        setupFoodList()
        setupNavigation()
    }

    private fun setupToolbar() {
        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupFoodList() {
        val rv = findViewById<RecyclerView>(R.id.rvFoodCupboard)
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = GridLayoutManager(this, columns)
        
        rv.adapter = CategoryAdapter(foodCupboardItems) { item ->
            if (item.name == "Flour, Rice & Cereals") {
                startActivity(Intent(this, FlourRiceCerealsActivity::class.java))
            } else {
                val intent = Intent(this, InventoryActivity::class.java)
                intent.putExtra("FILTER_CATEGORY", item.name)
                startActivity(intent)
            }
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
