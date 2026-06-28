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

class WeighedSpicesActivity : AppCompatActivity() {

    private val spiceBrands = listOf(
        CategoryData("Tropical Heat", "Premium spices and herbs.", R.drawable.ic_shopping_cart, "#E65100"),
        CategoryData("Nature’S Own", "Natural and pure seasonings.", R.drawable.ic_shopping_cart, "#EF6C00"),
        CategoryData("Orleys", "Quality spice blends.", R.drawable.ic_shopping_cart, "#F57C00"),
        CategoryData("Shalimar", "Traditional Shalimar spices.", R.drawable.ic_shopping_cart, "#FB8C00"),
        CategoryData("Simba Mbili", "The famous Curry Powder and more.", R.drawable.ic_shopping_cart, "#FF9800"),
        CategoryData("Royco", "Curry powder and specialized spice mixes.", R.drawable.ic_shopping_cart, "#FFA726"),
        CategoryData("Sunset Delight", "Sunset Delight brand spices.", R.drawable.ic_shopping_cart, "#FFB74D"),
        CategoryData("Jomu", "Quality seasonings and spices.", R.drawable.ic_shopping_cart, "#FFCC80"),
        CategoryData("Jumbo", "Flavorful spices and cubes.", R.drawable.ic_shopping_cart, "#FFE0B2")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_weighed_spices)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.spices_root)) { v, insets ->
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
        val rv = findViewById<RecyclerView>(R.id.rvWeighedSpices)
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = GridLayoutManager(this, columns)
        
        rv.adapter = CategoryAdapter(spiceBrands) { item ->
            val intent = Intent(this, SpiceBrandActivity::class.java)
            intent.putExtra("BRAND_NAME", item.name)
            startActivity(intent)
        }
    }

    private fun setupNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.setOnItemSelectedListener { item -> handleNavigation(item.itemId) }
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
