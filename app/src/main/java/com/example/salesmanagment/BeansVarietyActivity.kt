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

class BeansVarietyActivity : AppCompatActivity() {

    private lateinit var varietyName: String
    private lateinit var adapter: InventoryAdapter
    private val varietyProducts = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_beans_variety)

        varietyName = intent.getStringExtra("VARIETY_NAME") ?: "Beans"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.beans_variety_root)) { v, insets ->
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
        toolbar.title = varietyName
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupProductList() {
        val rv = findViewById<RecyclerView>(R.id.rvVarietyProducts)
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = GridLayoutManager(this, columns)
        
        adapter = InventoryAdapter(varietyProducts) { product ->
            // Handle click
        }
        rv.adapter = adapter
        
        loadMockVarietyData()
    }

    private fun loadMockVarietyData() {
        val mock = listOf(
            Product("b1", "$varietyName 500g", "Beans", 90.0, 25),
            Product("b2", "$varietyName 1kg", "Beans", 175.0, 15),
            Product("b3", "$varietyName Pre-boiled 400g", "Beans", 120.0, 10)
        )
        varietyProducts.addAll(mock)
        adapter.updateList(varietyProducts)
    }

    private fun setupNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.setOnItemSelectedListener { _ ->
            finish()
            true
        }
    }
}
