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

class GramsLentilsActivity : AppCompatActivity() {

    private val items = listOf(
        CategoryData("Nutrameal Green Grams", "Polished and high quality green grams.", R.drawable.ic_shopping_cart, "#4CAF50"),
        CategoryData("Farm Green Grams", "Farm fresh green grams (Mung beans).", R.drawable.ic_shopping_cart, "#388E3C"),
        CategoryData("Yankee Doodle Green Gram", "Yankee Doodle premium green grams.", R.drawable.ic_shopping_cart, "#2E7D32"),
        CategoryData("Green Grams", "Standard quality green gram varieties.", R.drawable.ic_shopping_cart, "#1B5E20"),
        CategoryData("Red Lentils (Masoor)", "Kamili Nutrameal Red Lentils (Masoor Whole).", R.drawable.ic_shopping_cart, "#E57373"),
        CategoryData("Toor Dal", "Nutrameal Toor Dal (Pigeon Peas Split).", R.drawable.ic_shopping_cart, "#FFB74D"),
        CategoryData("Butterfly Green Lentils", "Butterfly Pulses brand Green Lentils.", R.drawable.ic_shopping_cart, "#8BC34A"),
        CategoryData("Yankee Masoor Whole", "Yankee Doodle brand Masoor Whole.", R.drawable.ic_shopping_cart, "#EF5350"),
        CategoryData("Yankee Masoor Dall Washed", "Yankee brand Masoor Dall Washed.", R.drawable.ic_shopping_cart, "#F44336"),
        CategoryData("Butterfly Masoor Dal", "Butterfly Lentils brand Masoor Dal.", R.drawable.ic_shopping_cart, "#D32F2F"),
        CategoryData("Pigeon Peas", "Kamili Nutrameal Pigeon Peas.", R.drawable.ic_shopping_cart, "#FFA726"),
        CategoryData("Nutrameal Chickpeas", "Nutritious and quality chickpeas.", R.drawable.ic_shopping_cart, "#FF9800")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_grams_lentils)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.grams_lentils_root)) { v, insets ->
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
        val rv = findViewById<RecyclerView>(R.id.rvGramsLentils)
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = GridLayoutManager(this, columns)
        
        rv.adapter = CategoryAdapter(items) { item ->
            val intent = Intent(this, GramsLentilsBrandActivity::class.java)
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
