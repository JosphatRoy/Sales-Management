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

class OilBrandActivity : AppCompatActivity() {

    private lateinit var brandName: String
    private lateinit var adapter: InventoryAdapter
    private val brandProducts = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_oil_brand)

        brandName = intent.getStringExtra("BRAND_NAME") ?: "Oils & Fats"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.oil_brand_root)) { v, insets ->
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
        
        adapter = InventoryAdapter(brandProducts) { _ -> }
        rv.adapter = adapter
        
        loadMockBrandData()
    }

    private fun loadMockBrandData() {
        val mock = listOf(
            Product("oil1", "$brandName 500ml", "Vegetable Oils & Fats", 110.0, 40),
            Product("oil2", "$brandName 1L", "Vegetable Oils & Fats", 215.0, 25),
            Product("oil3", "$brandName 2L", "Vegetable Oils & Fats", 420.0, 15),
            Product("oil4", "$brandName 3L", "Vegetable Oils & Fats", 610.0, 10),
            Product("oil5", "$brandName 5L", "Vegetable Oils & Fats", 1050.0, 5)
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
