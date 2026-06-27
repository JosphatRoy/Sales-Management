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

class RiceActivity : AppCompatActivity() {

    private val riceBrands = listOf(
        CategoryData("Basmati Rice", "Sunrice, Daawat, Pearl Super, Ranee.", R.drawable.ic_shopping_cart, "#F5F5F5"),
        CategoryData("Mwea Pishori", "Jamii, Kings M.P.S, Cil, Pearl, Farm Pishori.", R.drawable.ic_shopping_cart, "#EEEEEE"),
        CategoryData("Long Grain & Import", "Daawat, Farm Pakistani, Farm Thailand, White Long Grain.", R.drawable.ic_shopping_cart, "#E0E0E0"),
        CategoryData("Brown Rice", "Cil Aromatic Brown Rice.", R.drawable.ic_shopping_cart, "#D7CCC8")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rice)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rice_root)) { v, insets ->
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
        val rv = findViewById<RecyclerView>(R.id.rvRice)
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = GridLayoutManager(this, columns)
        
        rv.adapter = CategoryAdapter(riceBrands) { item ->
            when (item.name) {
                "Basmati Rice" -> startActivity(Intent(this, BasmatiRiceActivity::class.java))
                "Mwea Pishori" -> startActivity(Intent(this, MweaPishoriActivity::class.java))
                "Long Grain & Import" -> startActivity(Intent(this, LongGrainRiceActivity::class.java))
                "Brown Rice" -> startActivity(Intent(this, BrownRiceActivity::class.java))
                else -> {
                    val intent = Intent(this, InventoryActivity::class.java)
                    intent.putExtra("FILTER_CATEGORY", item.name)
                    startActivity(intent)
                }
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
