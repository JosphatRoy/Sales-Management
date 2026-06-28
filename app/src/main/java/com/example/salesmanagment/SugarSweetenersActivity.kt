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

class SugarSweetenersActivity : AppCompatActivity() {

    private val items = listOf(
        // Sugar Brands
        CategoryData("House Brands", "Quality white sugar.", R.drawable.ic_shopping_cart, "#F5F5F5"),
        CategoryData("Nutrameal Sugar", "Nutrameal brand sugar.", R.drawable.ic_shopping_cart, "#EEEEEE"),
        CategoryData("Kabras Sugar", "White and Brown Kabras sugar.", R.drawable.ic_shopping_cart, "#E0E0E0"),
        CategoryData("Mara Sugar", "Mara brand sugar.", R.drawable.ic_shopping_cart, "#F5F5F5"),
        CategoryData("Clovers", "Brown sugar with molasses.", R.drawable.ic_shopping_cart, "#8D6E63"),
        CategoryData("Fairleigh", "Premium brown sugar.", R.drawable.ic_shopping_cart, "#795548"),
        CategoryData("Zesta", "Icing sugar for baking.", R.drawable.ic_shopping_cart, "#FFFFFF"),
        
        // Sweetener Brands
        CategoryData("Tropicana Slim", "Low calorie sweeteners.", R.drawable.ic_shopping_cart, "#4CAF50"),
        CategoryData("Lakanto", "Monkfruit sweeteners.", R.drawable.ic_shopping_cart, "#81C784"),
        CategoryData("Aspa Sweet", "Artificial sweeteners.", R.drawable.ic_shopping_cart, "#A5D6A7")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sugar_sweeteners)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.sugar_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupToolbar()
        setupList()
        setupNavigation()
    }

    private fun setupToolbar() {
        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupList() {
        val rv = findViewById<RecyclerView>(R.id.rvSugarSweeteners)
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = GridLayoutManager(this, columns)
        
        rv.adapter = CategoryAdapter(items) { item ->
            val intent = Intent(this, SugarBrandActivity::class.java)
            intent.putExtra("BRAND_NAME", item.name)
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
