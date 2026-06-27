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

class PopcornsGrainsActivity : AppCompatActivity() {

    private val popcornItems = listOf(
        CategoryData("Butterfly Grains Popcorn Kernels", "Premium butterfly grains popcorn.", R.drawable.ic_shopping_cart, "#FFD700"),
        CategoryData("Farm Popcorn", "Fresh quality farm popcorn kernels.", R.drawable.ic_shopping_cart, "#FBC02D"),
        CategoryData("American Garden Microwave", "Quick and easy microwave popcorn.", R.drawable.ic_shopping_cart, "#FFA000"),
        CategoryData("Kamili Imported Popcorn", "Kamili brand imported popcorn kernels.", R.drawable.ic_shopping_cart, "#FF8F00"),
        CategoryData("Yankee Raw Popcorn", "Yankee brand raw popcorn kernels.", R.drawable.ic_shopping_cart, "#F57C00"),
        CategoryData("Sunset Delight Raw Popcorn", "Sunset Delight brand raw popcorn.", R.drawable.ic_shopping_cart, "#EF6C00"),
        CategoryData("Argus Raw Popcorn", "Argus brand quality raw popcorn.", R.drawable.ic_shopping_cart, "#E65100")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_popcorns_grains)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.popcorns_root)) { v, insets ->
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
        val rv = findViewById<RecyclerView>(R.id.rvPopcornsGrains)
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = GridLayoutManager(this, columns)
        
        rv.adapter = CategoryAdapter(popcornItems) { item ->
            val intent = Intent(this, PopcornBrandActivity::class.java)
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
