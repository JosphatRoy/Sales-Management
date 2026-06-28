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

class SugarBrandActivity : AppCompatActivity() {

    private lateinit var brandName: String
    private lateinit var adapter: InventoryAdapter
    private val brandProducts = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sugar_brand)

        brandName = intent.getStringExtra("BRAND_NAME") ?: "Sugar"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.sugar_brand_root)) { v, insets ->
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
            Product("sugar1", "$brandName 1kg", "Sugar & Sweeteners", 150.0, 30),
            Product("sugar2", "$brandName 2kg", "Sugar & Sweeteners", 290.0, 20),
            Product("sugar3", "$brandName 5kg", "Sugar & Sweeteners", 720.0, 10)
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
