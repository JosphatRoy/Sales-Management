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

class LiquorActivity : AppCompatActivity() {

    private val liquorItems = listOf(
        // Beer
        CategoryData("Craft Beer", "Small-batch and artisanal beers.", R.drawable.ic_shopping_cart, "#8D6E63"),
        CategoryData("Cider & Cocktails", "Refreshing ciders and pre-mixed cocktails.", R.drawable.ic_shopping_cart, "#A1887F"),
        CategoryData("Lager Beer", "Classic and crisp lager beers.", R.drawable.ic_shopping_cart, "#795548"),
        CategoryData("Malt & Stout Beer", "Rich malt and dark stout beers.", R.drawable.ic_shopping_cart, "#5D4037"),
        
        // Spirits
        CategoryData("Whisky", "Premium aged whiskies.", R.drawable.ic_shopping_cart, "#B71C1C"),
        CategoryData("Gin", "Botanical and classic gins.", R.drawable.ic_shopping_cart, "#C62828"),
        CategoryData("Brandy", "Smooth and rich brandies.", R.drawable.ic_shopping_cart, "#D32F2F"),
        CategoryData("Liqueur", "Sweet and flavored spirits.", R.drawable.ic_shopping_cart, "#E53935"),
        CategoryData("Rum", "Spiced, dark, and white rums.", R.drawable.ic_shopping_cart, "#F44336"),
        CategoryData("Tequila", "Agave-based spirits.", R.drawable.ic_shopping_cart, "#EF5350"),
        CategoryData("Vodka", "Clear and flavored vodkas.", R.drawable.ic_shopping_cart, "#E57373"),
        
        // Wine
        CategoryData("Red Wine", "Full-bodied and light red wines.", R.drawable.ic_shopping_cart, "#880E4F"),
        CategoryData("White Wine", "Crisp and dry white wines.", R.drawable.ic_shopping_cart, "#AD1457"),
        CategoryData("Rose Wine", "Light and fruity rose wines.", R.drawable.ic_shopping_cart, "#C2185B"),
        CategoryData("Sparkling Wine", "Bubbles for every celebration.", R.drawable.ic_shopping_cart, "#D81B60"),
        
        // Intimate
        CategoryData("Intimacy Lubes", "Personal lubricants and essentials.", R.drawable.ic_people, "#311B92")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_liquor)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.liquor_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupToolbar()
        setupLiquorList()
        setupNavigation()
    }

    private fun setupToolbar() {
        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupLiquorList() {
        val rv = findViewById<RecyclerView>(R.id.rvLiquor)
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = GridLayoutManager(this, columns)
        
        rv.adapter = CategoryAdapter(liquorItems) { item ->
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
