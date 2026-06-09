package com.example.salesmanagment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView
import android.content.Intent
import java.util.Locale
import java.util.UUID

class Transactionscreen : AppCompatActivity() {

    private lateinit var etProductName: TextInputEditText
    private lateinit var etQuantity: TextInputEditText
    private lateinit var etUnitPrice: TextInputEditText
    private lateinit var etCustomerName: TextInputEditText
    private lateinit var tvTotalAmount: TextView
    private lateinit var btnSubmit: Button
    private lateinit var btnClear: Button
    private lateinit var rvRecentSales: RecyclerView
    
    private lateinit var salesAdapter: SalesAdapter
    private val recentSales = mutableListOf<Sale>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transactionscreen)

        setupSystemUI()
        initViews()
        setupRecyclerView()
        setupListeners()
        setupNavigation()
        
        // Load some initial history
        loadMockHistory()
    }

    private fun setupNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navRail = findViewById<NavigationRailView>(R.id.navigation_rail)

        bottomNav?.selectedItemId = R.id.nav_home
        navRail?.selectedItemId = R.id.nav_home

        bottomNav?.setOnItemSelectedListener { item -> handleNavigation(item.itemId) }
        navRail?.setOnItemSelectedListener { item -> handleNavigation(item.itemId) }
    }

    private fun handleNavigation(itemId: Int): Boolean {
        if (itemId == R.id.nav_home) return true

        return when (itemId) {
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

    private fun loadMockHistory() {
        recentSales.addAll(listOf(
            Sale(UUID.randomUUID().toString(), "Sugar", 2.0, 2.50, 5.00, "John Doe"),
            Sale(UUID.randomUUID().toString(), "Milk", 3.0, 1.20, 3.60, null),
            Sale(UUID.randomUUID().toString(), "Soap", 10.0, 0.80, 8.00, "Jane Smith")
        ))
        salesAdapter.updateList(recentSales)
    }

    private fun setupSystemUI() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.transaction_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initViews() {
        etProductName = findViewById(R.id.etProductName)
        etQuantity = findViewById(R.id.etQuantity)
        etUnitPrice = findViewById(R.id.etUnitPrice)
        etCustomerName = findViewById(R.id.etCustomerName)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        btnSubmit = findViewById(R.id.btnSaveTransaction)
        btnClear = findViewById(R.id.btnClear)
        rvRecentSales = findViewById(R.id.rvRecentSales)
    }

    private fun setupRecyclerView() {
        // Responsive: use 2 columns on tablets (w600dp)
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rvRecentSales.layoutManager = if (columns > 2) {
            GridLayoutManager(this, 2)
        } else {
            LinearLayoutManager(this)
        }
        
        salesAdapter = SalesAdapter(recentSales)
        rvRecentSales.adapter = salesAdapter
    }

    private fun setupListeners() {
        // Real-time calculation watcher
        val calculationWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateTotalDisplay()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        etQuantity.addTextChangedListener(calculationWatcher)
        etUnitPrice.addTextChangedListener(calculationWatcher)

        btnSubmit.setOnClickListener {
            validateAndSubmit()
        }

        btnClear.setOnClickListener {
            clearForm()
        }
    }

    private fun updateTotalDisplay() {
        val qty = etQuantity.text.toString().toDoubleOrNull() ?: 0.0
        val price = etUnitPrice.text.toString().toDoubleOrNull() ?: 0.0
        val total = qty * price

        // Authentic currency formatting
        tvTotalAmount.text = String.format(Locale.getDefault(), "$ %.2f", total)
    }

    private fun validateAndSubmit() {
        val name = etProductName.text.toString().trim()
        val qtyStr = etQuantity.text.toString().trim()
        val priceStr = etUnitPrice.text.toString().trim()
        val customer = etCustomerName.text.toString().trim()

        // Validation logic
        when {
            name.isEmpty() -> {
                etProductName.error = "Enter product name"
                etProductName.requestFocus()
            }
            qtyStr.isEmpty() || (qtyStr.toDoubleOrNull() ?: 0.0) <= 0 -> {
                etQuantity.error = "Enter valid quantity"
                etQuantity.requestFocus()
            }
            priceStr.isEmpty() || (priceStr.toDoubleOrNull() ?: 0.0) <= 0 -> {
                etUnitPrice.error = "Enter valid unit price"
                etUnitPrice.requestFocus()
            }
            else -> {
                processTransaction(name, qtyStr.toDouble(), priceStr.toDouble(), customer)
            }
        }
    }

    private fun processTransaction(name: String, qty: Double, price: Double, customer: String) {
        val total = qty * price
        
        // Create new Sale object
        val newSale = Sale(
            id = UUID.randomUUID().toString(),
            productName = name,
            quantity = qty,
            unitPrice = price,
            totalAmount = total,
            customerName = if (customer.isEmpty()) null else customer
        )

        // Add to history (at the top)
        recentSales.add(0, newSale)
        salesAdapter.updateList(recentSales)

        Toast.makeText(this, "Sale Recorded Successfully", Toast.LENGTH_SHORT).show()

        // Reset the form for the next entry
        clearForm()
    }

    private fun clearForm() {
        etProductName.text?.clear()
        etQuantity.text?.clear()
        etUnitPrice.text?.clear()
        etCustomerName.text?.clear()
        tvTotalAmount.text = "$ 0.00"
        etProductName.requestFocus()
    }
}