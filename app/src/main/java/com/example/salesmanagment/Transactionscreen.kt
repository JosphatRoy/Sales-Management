package com.example.salesmanagment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
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
import com.google.android.material.navigationrail.NavigationRailView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Locale
import java.util.UUID

class Transactionscreen : AppCompatActivity() {

    private lateinit var etProductName: TextInputEditText
    private lateinit var etQuantity: TextInputEditText
    private lateinit var etUnitPrice: TextInputEditText
    private lateinit var etCustomerName: TextInputEditText
    private lateinit var actvPaymentMode: AutoCompleteTextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var btnSubmit: Button
    private lateinit var btnClear: Button
    private lateinit var rvRecentSales: RecyclerView
    
    private lateinit var salesAdapter: SalesAdapter
    private val recentSales = mutableListOf<Sale>()
    
    private val paymentModes = arrayOf("M-Pesa", "Debit Mastercard", "PayPal")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transactionscreen)

        setupSystemUI()
        initViews()
        setupRecyclerView()
        setupListeners()
        setupNavigation()
        setupPaymentModeDropdown()

        loadSales()
        if (recentSales.isEmpty()) {
            loadMockHistory()
        }
    }

    private fun setupPaymentModeDropdown() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, paymentModes)
        actvPaymentMode.setAdapter(adapter)
        actvPaymentMode.setText(paymentModes[0], false)
    }

    private fun saveSales() {
        val prefs = getSharedPreferences("sales_prefs", MODE_PRIVATE)
        val json = Gson().toJson(recentSales)
        prefs.edit().putString("sales_list", json).apply()
    }

    private fun loadSales() {
        val prefs = getSharedPreferences("sales_prefs", MODE_PRIVATE)
        val json = prefs.getString("sales_list", null)
        if (json != null) {
            val type = object : TypeToken<List<Sale>>() {}.type
            val list: List<Sale> = Gson().fromJson(json, type)
            recentSales.clear()
            recentSales.addAll(list)
            salesAdapter.updateList(recentSales)
        }
    }

    private fun setupNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navRail = findViewById<NavigationRailView>(R.id.navigation_rail)

        setupAdaptiveNavigation(bottomNav, navRail)

        bottomNav?.selectedItemId = R.id.nav_home
        navRail?.selectedItemId = R.id.nav_home
    }

    private fun setupAdaptiveNavigation(bottomNav: BottomNavigationView?, navRail: NavigationRailView?) {
        val isWide = resources.configuration.screenWidthDp >= 600
        val navGuideline = findViewById<androidx.constraintlayout.widget.Guideline>(R.id.nav_guideline)

        if (isWide) {
            navRail?.visibility = android.view.View.VISIBLE
            bottomNav?.visibility = android.view.View.GONE
            val railWidth = (80 * resources.displayMetrics.density).toInt()
            navGuideline?.setGuidelineBegin(railWidth)
        } else {
            navRail?.visibility = android.view.View.GONE
            bottomNav?.visibility = android.view.View.VISIBLE
            navGuideline?.setGuidelineBegin(0)
        }

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
            Sale(UUID.randomUUID().toString(), "Sugar", 2.0, 2.50, 5.00, "John Doe", "M-Pesa"),
            Sale(UUID.randomUUID().toString(), "Milk", 3.0, 1.20, 3.60, null, "PayPal"),
            Sale(UUID.randomUUID().toString(), "Soap", 10.0, 0.80, 8.00, "Jane Smith", "Debit Mastercard")
        ))
        salesAdapter.updateList(recentSales)
        saveSales()
    }

    private fun setupSystemUI() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.transaction_root_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
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
        actvPaymentMode = findViewById(R.id.actvPaymentMode)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        btnSubmit = findViewById(R.id.btnSaveTransaction)
        btnClear = findViewById(R.id.btnClear)
        rvRecentSales = findViewById(R.id.rvRecentSales)
    }

    private fun setupRecyclerView() {
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rvRecentSales.layoutManager = if (columns > 2) {
            GridLayoutManager(this, 2)
        } else {
            LinearLayoutManager(this)
        }
        
        salesAdapter = SalesAdapter(recentSales) { sale ->
            showEditSaleDialog(sale)
        }
        rvRecentSales.adapter = salesAdapter
    }

    private fun showEditSaleDialog(sale: Sale) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_inventory, null)
        val etProdName = dialogView.findViewById<TextInputEditText>(R.id.etProductName)
        val etQty = dialogView.findViewById<TextInputEditText>(R.id.etStock) 
        val etPrice = dialogView.findViewById<TextInputEditText>(R.id.etPrice)
        
        etProdName.setText(sale.productName)
        etQty.setText(sale.quantity.toString())
        etPrice.setText(sale.unitPrice.toString())

        AlertDialog.Builder(this)
            .setTitle("Edit Sale")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val index = recentSales.indexOf(sale)
                if (index != -1) {
                    val newQty = etQty.text.toString().toDoubleOrNull() ?: 0.0
                    val newPrice = etPrice.text.toString().toDoubleOrNull() ?: 0.0
                    recentSales[index] = sale.copy(
                        productName = etProdName.text.toString(),
                        quantity = newQty,
                        unitPrice = newPrice,
                        totalAmount = newQty * newPrice
                    )
                    salesAdapter.updateList(recentSales)
                    saveSales()
                }
            }
            .setNeutralButton("Delete") { _, _ ->
                recentSales.remove(sale)
                salesAdapter.updateList(recentSales)
                saveSales()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupListeners() {
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
        tvTotalAmount.text = String.format(Locale.getDefault(), "$ %.2f", total)
    }

    private fun validateAndSubmit() {
        val name = etProductName.text.toString().trim()
        val qtyStr = etQuantity.text.toString().trim()
        val priceStr = etUnitPrice.text.toString().trim()
        val customer = etCustomerName.text.toString().trim()
        val paymentMode = actvPaymentMode.text.toString()

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
            paymentMode.isEmpty() -> {
                Toast.makeText(this, "Select payment mode", Toast.LENGTH_SHORT).show()
            }
            else -> {
                processTransaction(name, qtyStr.toDouble(), priceStr.toDouble(), customer, paymentMode)
            }
        }
    }

    private fun processTransaction(name: String, qty: Double, price: Double, customer: String, paymentMode: String) {
        val total = qty * price
        val newSale = Sale(
            id = UUID.randomUUID().toString(),
            productName = name,
            quantity = qty,
            unitPrice = price,
            totalAmount = total,
            customerName = if (customer.isEmpty()) null else customer,
            paymentMode = paymentMode
        )

        recentSales.add(0, newSale)
        salesAdapter.updateList(recentSales)
        saveSales()

        Toast.makeText(this, "Sale Recorded Successfully", Toast.LENGTH_SHORT).show()
        
        // Direct to Payment Gateway
        initiateDirectPayment(name, total, paymentMode)
        
        clearForm()
    }

    // A data class to represent your payment request payload
    data class PaymentRequest(
        val product: String,
        val amount: Double,
        val paymentMode: String
    )

    private fun initiateDirectPayment(product: String, amount: Double, mode: String) {
        val paymentRequest = PaymentRequest(product, amount, mode)

        // Highlight: Instead of building URLs for a browser, we pass the data to an API handler
        when (mode) {
            "M-Pesa" -> executeMpesaStkPush(paymentRequest)
            "PayPal" -> executePayPalDirectCharge(paymentRequest)
            "Debit Mastercard" -> executeMastercardDirectCharge(paymentRequest)
            else -> showToast("Unsupported payment method")
        }
    }

    private fun executeMpesaStkPush(request: PaymentRequest) {
        // TODO: Make a network call to your backend to trigger Daraja API (STK Push / NIPI)
        // This will prompt the user to enter their M-Pesa PIN directly on their phone screen in real-time.
        showLoadingSpinner()

        // Placeholder implementation to fix "Unresolved reference: myBackendApi"
        // In a real app, you would use Retrofit or another networking library here.
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            hideLoadingSpinner()
            showToast("STK Push initiated! Please check your phone for the PIN prompt.")
        }, 1000)

        /*
        myBackendApi.triggerMpesaPush(request).enqueue(object : Callback<PaymentResponse> {
            override fun onResponse(call: Call<PaymentResponse>, response: Response<PaymentResponse>) {
                hideLoadingSpinner()
                if (response.isSuccessful) {
                    showToast("STK Push sent! Please check your phone to enter your PIN.")
                }
            }
            override fun onFailure(call: Call<PaymentResponse>, t: Throwable) {
                hideLoadingSpinner()
                showToast("Payment failed: ${t.message}")
            }
        })
        */
    }

    private fun executePayPalDirectCharge(request: PaymentRequest) {
        // TODO: For direct app-based PayPal, use the official PayPal Android SDK
        // or pass a setup token generated from your backend.
        showToast("Initiating real-time PayPal Native Checkout...")
    }

    private fun executeMastercardDirectCharge(request: PaymentRequest) {
        // TODO: Use tokenization (like Mastercard Payment Gateway Services - MPGS SDK)
        // Never pass raw card details directly through your own API unless you are PCI-DSS certified.
        showToast("Opening secure Mastercard native field...")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoadingSpinner() {
        // Implementation for showing a loading spinner
    }

    private fun hideLoadingSpinner() {
        // Implementation for hiding the loading spinner
    }

    private fun clearForm() {
        etProductName.text?.clear()
        etQuantity.text?.clear()
        etUnitPrice.text?.clear()
        etCustomerName.text?.clear()
        actvPaymentMode.setText(paymentModes[0], false)
        tvTotalAmount.text = "$ 0.00"
        etProductName.requestFocus()
    }
}
