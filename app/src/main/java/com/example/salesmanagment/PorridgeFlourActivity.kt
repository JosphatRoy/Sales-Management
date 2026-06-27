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

class PorridgeFlourActivity : AppCompatActivity() {

    private val porridgeFlourBrands = listOf(
        CategoryData("Famila", "Pure Wimbi, Uji Mix, Baby Weaning.", R.drawable.ic_shopping_cart, "#795548"),
        CategoryData("Soko Wimbi", "Pure Wimbi, Wimbi Mix.", R.drawable.ic_shopping_cart, "#8D6E63"),
        CategoryData("Pembe Wimbi", "Pembe Pure Wimbi.", R.drawable.ic_shopping_cart, "#A1887F"),
        CategoryData("Jogoo Wimbi", "Jogoo traditional Wimbi.", R.drawable.ic_shopping_cart, "#BCAAA4"),
        CategoryData("Winnie's (Uji)", "Pure Wimbi, Uji Afya, Toto Afya, Terere Afya.", R.drawable.ic_shopping_cart, "#4CAF50"),
        CategoryData("Rimwabi", "Rimwabi Omena Uji Mix.", R.drawable.ic_shopping_cart, "#388E3C"),
        CategoryData("Afya Wimbi", "Natures Equatorial Afya Wimbi.", R.drawable.ic_shopping_cart, "#2E7D32"),
        CategoryData("Mican", "Amaranth Baby Porridge, Omena Mix & Amaranth.", R.drawable.ic_shopping_cart, "#1B5E20")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_porridge_flour)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.porridge_root)) { v, insets ->
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
        val rv = findViewById<RecyclerView>(R.id.rvPorridgeFlour)
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = GridLayoutManager(this, columns)
        
        rv.adapter = CategoryAdapter(porridgeFlourBrands) { item ->
            val intent = Intent(this, PorridgeBrandActivity::class.java)
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
