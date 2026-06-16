package com.example.salesmanagment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigationrail.NavigationRailView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ReportsActivity : AppCompatActivity() {

    private lateinit var topProductsAdapter: InventoryAdapter
    private val topProducts = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reports)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.reports_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupNavigation()
        setupTopProductsList()
        setupAddReportButton()
        
        loadReportData()
        if (topProducts.isEmpty()) {
            loadMockReportData()
        }
    }

    private fun saveReportProducts() {
        val prefs = getSharedPreferences("sales_prefs", MODE_PRIVATE)
        val json = Gson().toJson(topProducts)
        prefs.edit().putString("report_products_list", json).apply()
    }

    private fun loadReportData() {
        // Sales Summary (Static for now)
        findViewById<TextView>(R.id.tvTotalRevenue).text = "$ 12,450.00"
        findViewById<TextView>(R.id.tvTotalTransactions).text = "156 Transactions"
        findViewById<TextView>(R.id.tvInventoryValue).text = "$ 45,200.00"
        findViewById<TextView>(R.id.tvLowStockCount).text = "5 Low Stock Items"

        val prefs = getSharedPreferences("sales_prefs", MODE_PRIVATE)
        val json = prefs.getString("report_products_list", null)
        if (json != null) {
            val type = object : TypeToken<List<Product>>() {}.type
            val list: List<Product> = Gson().fromJson(json, type)
            topProducts.clear()
            topProducts.addAll(list)
            topProductsAdapter.updateList(topProducts)
        }
    }

    private fun loadMockReportData() {
        topProducts.addAll(listOf(
            Product("1", "Sugar", "Groceries", 2.50, 450),
            Product("5", "Rice", "Groceries", 10.00, 320),
            Product("2", "Milk", "Dairy", 1.20, 280),
            Product("4", "Eggs", "Dairy", 3.00, 150)
        ))
        topProductsAdapter.updateList(topProducts)
        saveReportProducts()
    }

    private fun setupAddReportButton() {
        findViewById<FloatingActionButton>(R.id.fabAddReport).setOnClickListener {
            showAddReportDialog()
        }
    }

    private fun showAddReportDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_inventory, null)
        val etName = dialogView.findViewById<TextInputEditText>(R.id.etProductName)
        
        AlertDialog.Builder(this)
            .setTitle("Add to Top Products")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = etName.text.toString()
                if (name.isNotEmpty()) {
                    topProducts.add(0, Product(System.currentTimeMillis().toString(), name, "Reported", 0.0, 0))
                    topProductsAdapter.updateList(topProducts)
                    saveReportProducts()
                    Toast.makeText(this, "Report item added", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupTopProductsList() {
        val rv = findViewById<RecyclerView>(R.id.rvTopProducts)
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = if (columns > 2) GridLayoutManager(this, 2) else LinearLayoutManager(this)
        
        topProductsAdapter = InventoryAdapter(topProducts) { product ->
            showEditProductDialog(product)
        }
        rv.adapter = topProductsAdapter
    }

    private fun showEditProductDialog(product: Product) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_inventory, null)
        val etName = dialogView.findViewById<TextInputEditText>(R.id.etProductName)
        val etCategory = dialogView.findViewById<TextInputEditText>(R.id.etCategory)
        val etPrice = dialogView.findViewById<TextInputEditText>(R.id.etPrice)
        val etStock = dialogView.findViewById<TextInputEditText>(R.id.etStock)

        etName.setText(product.name)
        etCategory.setText(product.category)
        etPrice.setText(product.price.toString())
        etStock.setText(product.stock.toString())

        AlertDialog.Builder(this)
            .setTitle("Edit Product")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val index = topProducts.indexOf(product)
                if (index != -1) {
                    topProducts[index] = product.copy(
                        name = etName.text.toString(),
                        category = etCategory.text.toString(),
                        price = etPrice.text.toString().toDoubleOrNull() ?: 0.0,
                        stock = etStock.text.toString().toIntOrNull() ?: 0
                    )
                    topProductsAdapter.updateList(topProducts)
                    saveReportProducts()
                }
            }
            .setNeutralButton("Delete") { _, _ ->
                topProducts.remove(product)
                topProductsAdapter.updateList(topProducts)
                saveReportProducts()
            }
            .show()
    }

    private fun setupNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navRail = findViewById<NavigationRailView>(R.id.navigation_rail)

        bottomNav?.selectedItemId = R.id.nav_reports
        navRail?.selectedItemId = R.id.nav_reports

        bottomNav?.setOnItemSelectedListener { item -> handleNavigation(item.itemId) }
        navRail?.setOnItemSelectedListener { item -> handleNavigation(item.itemId) }
    }

    private fun handleNavigation(itemId: Int): Boolean {
        if (itemId == R.id.nav_reports) return true

        return when (itemId) {
            R.id.nav_home -> { navigateTo(Transactionscreen::class.java); true }
            R.id.nav_inventory -> { navigateTo(InventoryActivity::class.java); true }
            R.id.nav_customers -> { navigateTo(CustomersActivity::class.java); true }
            else -> false
        }
    }

    private fun navigateTo(cls: Class<*>) {
        val intent = Intent(this, cls)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
    }
}
