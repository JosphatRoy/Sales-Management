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

class BabyKidsActivity : AppCompatActivity() {

    private val babyKidsItems = listOf(
        // Baby Transport & Safety
        CategoryData("Baby Transport & Safety", "Baby & Toddler Car Seats and safety gear.", R.drawable.ic_people, "#FF4081"),
        
        // Feeding & Nutrition
        CategoryData("Feeding & Nutrition", "Baby & Toddler Food, Baby Bottles and accessories.", R.drawable.ic_people, "#F06292"),
        
        // Hygiene & Diapering
        CategoryData("Hygiene & Diapering", "Baby Diapers, Baby Pants, and Baby Wipes.", R.drawable.ic_people, "#EC407A"),
        
        // Skincare & Grooming
        CategoryData("Skincare & Grooming", "Creams, Lotions, Oils, Jellies, Powders, Soaps, Shampoo.", R.drawable.ic_people, "#E91E63"),
        
        // Gifting
        CategoryData("Gifting", "Curated Baby Gift Packs and essentials.", R.drawable.ic_people, "#D81B60")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_baby_kids)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.baby_kids_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupToolbar()
        setupBabyKidsList()
        setupNavigation()
    }

    private fun setupToolbar() {
        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupBabyKidsList() {
        val rv = findViewById<RecyclerView>(R.id.rvBabyKids)
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = GridLayoutManager(this, columns)
        
        rv.adapter = CategoryAdapter(babyKidsItems) { item ->
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
