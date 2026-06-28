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

class VegetableOilsFatsActivity : AppCompatActivity() {

    private val items = listOf(
        // Vegetable & Specialty Oils
        CategoryData("Fresh Fri", "Premium vegetable cooking oil.", R.drawable.ic_shopping_cart, "#FFC107"),
        CategoryData("Rina", "Quality vegetable oil.", R.drawable.ic_shopping_cart, "#FFB300"),
        CategoryData("Dola Oil", "Dola brand cooking oil.", R.drawable.ic_shopping_cart, "#FFA000"),
        CategoryData("Golden Drop / Fry", "Pure vegetable oils.", R.drawable.ic_shopping_cart, "#FF8F00"),
        CategoryData("Avena", "Fortified vegetable oil.", R.drawable.ic_shopping_cart, "#FF6F00"),
        CategoryData("Salit", "Salit brand cooking oil.", R.drawable.ic_shopping_cart, "#F57C00"),
        CategoryData("Ufuta", "Pure sunflower/sesame oils.", R.drawable.ic_shopping_cart, "#EF6C00"),
        CategoryData("Masterchef Oil", "Masterchef vegetable oil.", R.drawable.ic_shopping_cart, "#E65100"),
        CategoryData("Pika", "Pika brand vegetable oil.", R.drawable.ic_shopping_cart, "#FB8C00"),
        CategoryData("Elianto", "Pure corn oil.", R.drawable.ic_shopping_cart, "#FFD600"),
        CategoryData("Rinsun / SunGold", "Sunflower cooking oils.", R.drawable.ic_shopping_cart, "#FFEE58"),
        CategoryData("Olive & Specialty", "Santa Maria, Borges, Pietro, Terra Delyssa.", R.drawable.ic_shopping_cart, "#4CAF50"),
        
        // Cooking Fats
        CategoryData("Kimbo", "The traditional cooking fat.", R.drawable.ic_shopping_cart, "#FFFFFF"),
        CategoryData("Cowboy", "Classic cooking fat.", R.drawable.ic_shopping_cart, "#F5F5F5"),
        CategoryData("Kasuku", "Kasuku brand cooking fat.", R.drawable.ic_shopping_cart, "#EEEEEE"),
        CategoryData("Chipsy", "Ideal for deep frying.", R.drawable.ic_shopping_cart, "#E0E0E0"),
        CategoryData("Mpishi Poa", "Quality cooking fat.", R.drawable.ic_shopping_cart, "#BDBDBD"),
        CategoryData("Tily", "Tily brand cooking fat.", R.drawable.ic_shopping_cart, "#9E9E9E"),
        CategoryData("Fry Mate", "Fry Mate cooking fat.", R.drawable.ic_shopping_cart, "#757575"),
        CategoryData("Mallo / Veebol", "Mallo, Veebol, Yello Gold fats.", R.drawable.ic_shopping_cart, "#FFEE58")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vegetable_oils_fats)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.oil_root)) { v, insets ->
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
        val rv = findViewById<RecyclerView>(R.id.rvVegetableOilsFats)
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = GridLayoutManager(this, columns)
        
        rv.adapter = CategoryAdapter(items) { item ->
            val intent = Intent(this, OilBrandActivity::class.java)
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
