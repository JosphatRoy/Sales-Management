package com.example.salesmanagment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView
import android.content.Intent

class InventoryActivity : AppCompatActivity() {

    private lateinit var adapter: InventoryAdapter
    private val allProducts = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inventory)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.inventory_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupToolbar()
        setupInventoryList()
        setupSearch()
        setupAddButton()
        setupNavigation()

        // Load mock data
        loadMockProducts()
    }

    private fun setupNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navRail = findViewById<NavigationRailView>(R.id.navigation_rail)

        bottomNav?.selectedItemId = R.id.nav_inventory
        navRail?.selectedItemId = R.id.nav_inventory

        bottomNav?.setOnItemSelectedListener { item -> handleNavigation(item.itemId) }
        navRail?.setOnItemSelectedListener { item -> handleNavigation(item.itemId) }
    }

    private fun handleNavigation(itemId: Int): Boolean {
        if (itemId == R.id.nav_inventory) return true
        
        return when (itemId) {
            R.id.nav_home -> { navigateTo(Transactionscreen::class.java); true }
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

    private fun setupToolbar() {
        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupInventoryList() {
        val rv = findViewById<RecyclerView>(R.id.rvInventory)
        
        // Responsive columns using the integer resource we created earlier
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = GridLayoutManager(this, columns)
        
        adapter = InventoryAdapter(allProducts)
        rv.adapter = adapter
    }

    private fun setupSearch() {
        val etSearch = findViewById<TextInputEditText>(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filter(query: String) {
        val filteredList = allProducts.filter {
            it.name.contains(query, ignoreCase = true) || 
            it.category.contains(query, ignoreCase = true)
        }
        adapter.updateList(filteredList)
    }

    private fun setupAddButton() {
        findViewById<FloatingActionButton>(R.id.fabAddProduct).setOnClickListener {
            Toast.makeText(this, "Add Product Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadMockProducts() {
        allProducts.addAll(listOf(
            Product("1", "Sugar", "Groceries", 2.50, 50),
            Product("2", "Milk", "Dairy", 1.20, 10),
            Product("3", "Bread", "Bakery", 1.50, 5),
            Product("4", "Eggs", "Dairy", 3.00, 20),
            Product("5", "Rice", "Groceries", 10.00, 100),
            Product("6", "Oil", "Groceries", 5.00, 15),
            Product("7", "Soap", "Toiletries", 0.80, 40),
            Product("8", "Salt", "Groceries", 0.50, 60)
        ))
        adapter.updateList(allProducts)
    }
}