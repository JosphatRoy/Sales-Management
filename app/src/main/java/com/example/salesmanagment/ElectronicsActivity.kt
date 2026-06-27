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

class ElectronicsActivity : AppCompatActivity() {

    private val electronicItems = listOf(
        // Kitchen Appliances
        CategoryData("Food & Drink Prep", "Blenders, Juicers, Sandwich Makers, Toasters.", R.drawable.ic_inventory, "#F44336"),
        CategoryData("Cooking & Heating", "Microwaves, Ovens, Kettles, Air Fryers.", R.drawable.ic_inventory, "#E91E63"),
        CategoryData("Refrigeration", "Fridges and Freezers.", R.drawable.ic_inventory, "#9C27B0"),
        CategoryData("Cookers", "Stand Alone, Table Top, and Electric Cookers.", R.drawable.ic_inventory, "#673AB7"),
        CategoryData("Water Dispensers", "Beverage Care and Water Dispensers.", R.drawable.ic_inventory, "#3F51B5"),
        
        // Home Comfort & Cleaning
        CategoryData("Air Conditioning", "Stand Fans and Room Heaters.", R.drawable.ic_inventory, "#2196F3"),
        CategoryData("Garment Care", "Steam and Dry Iron Boxes.", R.drawable.ic_inventory, "#03A9F4"),
        CategoryData("Floor Care", "Vacuum Cleaners.", R.drawable.ic_inventory, "#00BCD4"),
        CategoryData("Washing Machines", "Front Load and Top Load Washing Machines.", R.drawable.ic_inventory, "#009688"),
        
        // Entertainment & Media
        CategoryData("TVs & Receivers", "Digital TVs, Smart TVs, and Decoders.", R.drawable.ic_inventory, "#4CAF50"),
        CategoryData("Sound Systems", "Home Theaters, Woofers, and Speakers.", R.drawable.ic_inventory, "#8BC34A"),
        
        // Power & Electrical Supplies
        CategoryData("Wiring & Components", "Wires, Cables, Switches, and Plugs.", R.drawable.ic_inventory, "#CDDC39"),
        CategoryData("Distribution & Accessories", "Extension Cords and Lighting accessories.", R.drawable.ic_inventory, "#FFEB3B")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_electronics)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.electronics_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupToolbar()
        setupElectronicsList()
        setupNavigation()
    }

    private fun setupToolbar() {
        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupElectronicsList() {
        val rv = findViewById<RecyclerView>(R.id.rvElectronics)
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = GridLayoutManager(this, columns)
        
        rv.adapter = CategoryAdapter(electronicItems) { item ->
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
