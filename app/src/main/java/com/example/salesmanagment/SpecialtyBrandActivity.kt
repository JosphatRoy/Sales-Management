package com.example.salesmanagment

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class SpecialtyBrandActivity : AppCompatActivity() {

    private lateinit var brandName: String
    private lateinit var adapter: InventoryAdapter
    private val brandProducts = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_specialty_brand)

        brandName = intent.getStringExtra("BRAND_NAME") ?: "Specialty Flour"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.specialty_brand_root)) { v, insets ->
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
            // Handle product click
        }
        rv.adapter = adapter
        
        loadMockBrandData()
    }

    private fun loadMockBrandData() {
        val mock = listOf(
            Product("s1", "$brandName 500g", "Specialty Flour", 150.0, 20),
            Product("s2", "$brandName 1kg", "Specialty Flour", 280.0, 15),
            Product("s3", "$brandName Organic 1kg", "Specialty Flour", 350.0, 8)
        )
        brandProducts.addAll(mock)
        adapter.updateList(brandProducts)
    }

    private fun setupNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.setOnItemSelectedListener { _ ->
            finish()
            true
        }
    }
}
