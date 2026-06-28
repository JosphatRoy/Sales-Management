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

class PastaNoodlesActivity : AppCompatActivity() {

    private val items = listOf(
        // Pasta Brands
        CategoryData("Santa Lucia", "Premium Italian pasta.", R.drawable.ic_shopping_cart, "#FFD54F"),
        CategoryData("Santa Maria Pasta", "Quality pasta varieties.", R.drawable.ic_shopping_cart, "#FFCA28"),
        CategoryData("Ranee Pasta", "Ranee brand pasta products.", R.drawable.ic_shopping_cart, "#FFB300"),
        CategoryData("Primavera", "Authentic Primavera pasta.", R.drawable.ic_shopping_cart, "#FFA000"),
        CategoryData("Riscossa", "Traditional Italian Riscossa pasta.", R.drawable.ic_shopping_cart, "#FF8F00"),
        CategoryData("Divella", "Quality Divella pasta selection.", R.drawable.ic_shopping_cart, "#F57C00"),
        CategoryData("Pasta Zara", "Pasta Zara premium range.", R.drawable.ic_shopping_cart, "#E65100"),
        
        // Noodle Brands
        CategoryData("Indomie", "Popular instant noodles.", R.drawable.ic_shopping_cart, "#4CAF50"),
        CategoryData("Noodies", "Quick and tasty Noodies.", R.drawable.ic_shopping_cart, "#66BB6A"),
        CategoryData("Nala", "Nala brand noodles.", R.drawable.ic_shopping_cart, "#81C784"),
        CategoryData("Chen Ke Ming", "Authentic oriental noodles.", R.drawable.ic_shopping_cart, "#A5D6A7")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pasta_noodles)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pasta_noodles_root)) { v, insets ->
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
        val rv = findViewById<RecyclerView>(R.id.rvPastaNoodles)
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = GridLayoutManager(this, columns)
        
        rv.adapter = CategoryAdapter(items) { item ->
            val intent = Intent(this, PastaNoodlesBrandActivity::class.java)
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
