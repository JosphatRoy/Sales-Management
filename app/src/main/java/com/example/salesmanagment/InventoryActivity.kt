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
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class InventoryActivity : AppCompatActivity() {

    private lateinit var adapter: InventoryAdapter
    private val allProducts = mutableListOf<Product>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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

        // Sync data with Firebase
        syncWithFirebase()
    }

    private fun syncWithFirebase() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Not logged in. Using local data.", Toast.LENGTH_SHORT).show()
            loadLocalProducts()
            return
        }

        // 1. Check if we have local data to migrate
        val prefs = getSharedPreferences("sales_prefs", MODE_PRIVATE)
        val json = prefs.getString("products_list", null)
        
        if (json != null) {
            // Local data exists, migrate it to Firestore if Firestore is empty for this user
            val type = object : TypeToken<List<Product>>() {}.type
            val localList: List<Product> = Gson().fromJson(json, type)
            
            db.collection("users").document(currentUser.uid).collection("products")
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        // Migrate local to remote
                        migrateToFirestore(currentUser.uid, localList)
                    } else {
                        // Remote data exists, load it
                        loadFromFirestore(currentUser.uid)
                        // Clear local cache to prevent future migration conflicts
                        prefs.edit().remove("products_list").apply()
                    }
                }
        } else {
            // No local data, just load from Firestore
            loadFromFirestore(currentUser.uid)
        }
    }

    private fun migrateToFirestore(userId: String, products: List<Product>) {
        val batch = db.batch()
        products.forEach { product ->
            val docRef = db.collection("users").document(userId).collection("products").document(product.id)
            batch.set(docRef, product)
        }
        batch.commit().addOnSuccessListener {
            Toast.makeText(this, "Data migrated to cloud", Toast.LENGTH_SHORT).show()
            getSharedPreferences("sales_prefs", MODE_PRIVATE).edit().remove("products_list").apply()
            loadFromFirestore(userId)
        }
    }

    private fun loadFromFirestore(userId: String) {
        db.collection("users").document(userId).collection("products")
            .get()
            .addOnSuccessListener { result ->
                val list = result.toObjects(Product::class.java)
                allProducts.clear()
                allProducts.addAll(list)
                adapter.updateList(allProducts)
                if (allProducts.isEmpty()) {
                    loadMockProducts()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error loading cloud data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveProducts(product: Product) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("users").document(currentUser.uid).collection("products")
                .document(product.id)
                .set(product)
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to sync with cloud", Toast.LENGTH_SHORT).show()
                }
        }
        
        // Also keep local backup for offline access (optional but good practice)
        val prefs = getSharedPreferences("sales_prefs", MODE_PRIVATE)
        val json = Gson().toJson(allProducts)
        prefs.edit().putString("products_list", json).apply()
    }

    private fun deleteProduct(product: Product) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("users").document(currentUser.uid).collection("products")
                .document(product.id)
                .delete()
        }
        allProducts.remove(product)
        adapter.updateList(allProducts)
        // Update local backup
        val json = Gson().toJson(allProducts)
        getSharedPreferences("sales_prefs", MODE_PRIVATE).edit().putString("products_list", json).apply()
    }

    private fun loadLocalProducts() {
        val prefs = getSharedPreferences("sales_prefs", MODE_PRIVATE)
        val json = prefs.getString("products_list", null)
        if (json != null) {
            val type = object : TypeToken<List<Product>>() {}.type
            val list: List<Product> = Gson().fromJson(json, type)
            allProducts.clear()
            allProducts.addAll(list)
            adapter.updateList(allProducts)
        }
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
        
        adapter = InventoryAdapter(allProducts) { product ->
            showEditProductDialog(product)
        }
        rv.adapter = adapter
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

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Edit Product")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val index = allProducts.indexOf(product)
                if (index != -1) {
                    val updatedProduct = product.copy(
                        name = etName.text.toString(),
                        category = etCategory.text.toString(),
                        price = etPrice.text.toString().toDoubleOrNull() ?: 0.0,
                        stock = etStock.text.toString().toIntOrNull() ?: 0
                    )
                    allProducts[index] = updatedProduct
                    adapter.updateList(allProducts)
                    saveProducts(updatedProduct)
                    Toast.makeText(this, "Product updated", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Delete") { _, _ ->
                deleteProduct(product)
                Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton("Cancel", null)
            .show()
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
            showAddProductDialog()
        }
    }

    private fun showAddProductDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_inventory, null)
        val etName = dialogView.findViewById<TextInputEditText>(R.id.etProductName)
        val etCategory = dialogView.findViewById<TextInputEditText>(R.id.etCategory)
        val etPrice = dialogView.findViewById<TextInputEditText>(R.id.etPrice)
        val etStock = dialogView.findViewById<TextInputEditText>(R.id.etStock)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = etName.text.toString()
                val category = etCategory.text.toString()
                val priceStr = etPrice.text.toString()
                val stockStr = etStock.text.toString()

                if (name.isNotEmpty() && category.isNotEmpty() && priceStr.isNotEmpty() && stockStr.isNotEmpty()) {
                    val newProduct = Product(
                        id = System.currentTimeMillis().toString(),
                        name = name,
                        category = category,
                        price = priceStr.toDoubleOrNull() ?: 0.0,
                        stock = stockStr.toIntOrNull() ?: 0
                    )
                    allProducts.add(0, newProduct) // Add to top
                    adapter.updateList(allProducts)
                    saveProducts(newProduct)
                    Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
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