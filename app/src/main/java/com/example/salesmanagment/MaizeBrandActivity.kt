package com.example.salesmanagment

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView
import com.google.android.material.textfield.TextInputEditText

class MaizeBrandActivity : AppCompatActivity() {

    private lateinit var brandName: String
    private lateinit var adapter: InventoryAdapter
    private val brandProducts = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_maize_brand)

        brandName = intent.getStringExtra("BRAND_NAME") ?: "Maize Flour"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.maize_brand_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupToolbar()
        setupProductList()
        setupNavigation()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = brandName
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupProductList() {
        val rv = findViewById<RecyclerView>(R.id.rvBrandProducts)
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = GridLayoutManager(this, columns)
        
        adapter = InventoryAdapter(brandProducts) { product ->
            // Handle product click if needed
        }
        rv.adapter = adapter
        
        // In a real app, you'd fetch from Firebase here
        // For now, we'll show a "Filtered View" message or mock data
        loadMockBrandData()
    }

    private fun loadMockBrandData() {
        // This would filter the central repository in a real app
        val mock = listOf(
            Product("m1", "$brandName 1kg", "Maize Flour", 95.0, 50),
            Product("m2", "$brandName 2kg", "Maize Flour", 185.0, 30),
            Product("m3", "$brandName Premium 2kg", "Maize Flour", 210.0, 15)
        )
        brandProducts.addAll(mock)
        adapter.updateList(brandProducts)
    }

    private fun setupNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.setOnItemSelectedListener { item ->
            // Reuse standard navigation logic
            finish()
            true
        }
    }
}
