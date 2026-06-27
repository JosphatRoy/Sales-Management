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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class ReportsActivity : AppCompatActivity() {

    private lateinit var topProductsAdapter: InventoryAdapter
    private val topProducts = mutableListOf<Product>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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
        
        syncWithFirebase()
    }

    private fun syncWithFirebase() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            loadReportData() // Load static/local
            if (topProducts.isEmpty()) loadMockReportData()
            return
        }

        // Fetch sales for summary with real-time/offline support
        db.collection("users").document(currentUser.uid).collection("sales")
            .addSnapshotListener { snapshots, e ->
                if (snapshots != null) {
                    val sales = snapshots.toObjects(Sale::class.java)
                    updateSummary(sales)
                }
            }

        // Fetch top products
        db.collection("users").document(currentUser.uid).collection("top_products")
            .addSnapshotListener { snapshots, e ->
                if (snapshots != null) {
                    val list = snapshots.toObjects(Product::class.java)
                    topProducts.clear()
                    topProducts.addAll(list)
                    topProductsAdapter.updateList(topProducts)
                    if (topProducts.isEmpty() && snapshots.metadata.isFromCache.not()) {
                        loadMockReportData()
                    }
                }
            }
    }

    private fun updateSummary(sales: List<Sale>) {
        val totalRevenue = sales.sumOf { it.totalAmount }
        val transactionCount = sales.size
        
        findViewById<TextView>(R.id.tvTotalRevenue).text = String.format(Locale.getDefault(), "Ksh %.2f", totalRevenue)
        findViewById<TextView>(R.id.tvTotalTransactions).text = "$transactionCount Transactions"
        
        // Inventory value could be fetched similarly from "products"
        val currentUser = auth.currentUser ?: return
        db.collection("users").document(currentUser.uid).collection("products")
            .addSnapshotListener { snapshots, e ->
                if (snapshots != null) {
                    val products = snapshots.toObjects(Product::class.java)
                    val totalValue = products.sumOf { it.price * it.stock }
                    val lowStock = products.count { it.stock < 10 }
                    
                    findViewById<TextView>(R.id.tvInventoryValue).text = String.format(Locale.getDefault(), "Ksh %.2f", totalValue)
                    findViewById<TextView>(R.id.tvLowStockCount).text = "$lowStock Low Stock Items"
                }
            }
    }

    private fun saveReportProductToFirestore(product: Product) {
        val currentUser = auth.currentUser ?: return
        db.collection("users").document(currentUser.uid).collection("top_products")
            .document(product.id)
            .set(product)
    }

    private fun deleteReportProductFromFirestore(productId: String) {
        val currentUser = auth.currentUser ?: return
        db.collection("users").document(currentUser.uid).collection("top_products")
            .document(productId)
            .delete()
    }

    private fun saveReportProducts() {
        val prefs = getSharedPreferences("sales_prefs", MODE_PRIVATE)
        val json = Gson().toJson(topProducts)
        prefs.edit().putString("report_products_list", json).apply()
    }

    private fun loadReportData() {
        // Default summary values
        findViewById<TextView>(R.id.tvTotalRevenue).text = "Ksh 0.00"
        findViewById<TextView>(R.id.tvTotalTransactions).text = "0 Transactions"
        findViewById<TextView>(R.id.tvInventoryValue).text = "Ksh 0.00"
        findViewById<TextView>(R.id.tvLowStockCount).text = "0 Low Stock Items"

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
                    val newProduct = Product(System.currentTimeMillis().toString(), name, "Reported", 0.0, 0)
                    topProducts.add(0, newProduct)
                    topProductsAdapter.updateList(topProducts)
                    saveReportProductToFirestore(newProduct)
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
                    val updatedProduct = product.copy(
                        name = etName.text.toString(),
                        category = etCategory.text.toString(),
                        price = etPrice.text.toString().toDoubleOrNull() ?: 0.0,
                        stock = etStock.text.toString().toIntOrNull() ?: 0
                    )
                    topProducts[index] = updatedProduct
                    topProductsAdapter.updateList(topProducts)
                    saveReportProductToFirestore(updatedProduct)
                    saveReportProducts()
                }
            }
            .setNeutralButton("Delete") { _, _ ->
                deleteReportProductFromFirestore(product.id)
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
            R.id.nav_home -> { navigateTo(HomeActivity::class.java); true }
            R.id.nav_sales -> { navigateTo(Transactionscreen::class.java); true }
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
