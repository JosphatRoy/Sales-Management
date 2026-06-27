package com.example.salesmanagment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ProgressBar
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
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class Transactionscreen : AppCompatActivity() {

    private lateinit var mpesaService: MpesaService
    private lateinit var paypalService: PayPalService

    private lateinit var etProductName: TextInputEditText
    private lateinit var etQuantity: TextInputEditText
    private lateinit var etUnitPrice: TextInputEditText
    private lateinit var etCustomerName: TextInputEditText
    private lateinit var actvPaymentMode: AutoCompleteTextView
    private lateinit var etMpesaPhone: TextInputEditText
    private lateinit var mpesaPhoneLayout: TextInputLayout
    private lateinit var tvTotalAmount: TextView
    private lateinit var btnSubmit: Button
    private lateinit var btnClear: Button
    private lateinit var rvRecentSales: RecyclerView
    private lateinit var paymentProgress: ProgressBar
    
    private lateinit var salesAdapter: SalesAdapter
    private val recentSales = mutableListOf<Sale>()
    
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private val paymentModes = arrayOf("M-Pesa", "Debit Mastercard", "PayPal")

    private fun initRetrofit() {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder().addInterceptor(logging).build()

        val mpesaRetrofit = Retrofit.Builder()
            .baseUrl(PaymentConfig.MPESA_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        mpesaService = mpesaRetrofit.create(MpesaService::class.java)

        val paypalRetrofit = Retrofit.Builder()
            .baseUrl(PaymentConfig.PAYPAL_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        paypalService = paypalRetrofit.create(PayPalService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transactionscreen)

        initRetrofit()
        setupSystemUI()
        initViews()
        setupRecyclerView()
        setupListeners()
        setupNavigation()
        setupPaymentModeDropdown()

        syncWithFirebase()
    }

    private fun syncWithFirebase() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            loadSales() // Fallback to local
            if (recentSales.isEmpty()) loadMockHistory()
            return
        }

        db.collection("users").document(currentUser.uid).collection("sales")
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener

                if (snapshots != null) {
                    val list = snapshots.toObjects(Sale::class.java)
                    recentSales.clear()
                    recentSales.addAll(list.sortedByDescending { it.timestamp }) // Use timestamp for sorting
                    salesAdapter.updateList(recentSales)
                    if (recentSales.isEmpty() && snapshots.metadata.isFromCache.not()) {
                        loadMockHistory()
                    }
                }
            }
    }

    private fun setupPaymentModeDropdown() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, paymentModes)
        actvPaymentMode.setAdapter(adapter)
        actvPaymentMode.setText(paymentModes[0], false)
        
        // Initial check
        updateMpesaFieldVisibility(paymentModes[0])

        actvPaymentMode.setOnItemClickListener { parent, _, position, _ ->
            val selected = parent.getItemAtPosition(position) as String
            updateMpesaFieldVisibility(selected)
        }
    }

    private fun updateMpesaFieldVisibility(mode: String) {
        if (mode == "M-Pesa") {
            mpesaPhoneLayout.visibility = View.VISIBLE
        } else {
            mpesaPhoneLayout.visibility = View.GONE
        }
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

        bottomNav?.selectedItemId = R.id.nav_sales
        navRail?.selectedItemId = R.id.nav_sales
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
        if (itemId == R.id.nav_sales) return true

        return when (itemId) {
            R.id.nav_home -> { navigateTo(HomeActivity::class.java); true }
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
        etMpesaPhone = findViewById(R.id.etMpesaPhone)
        mpesaPhoneLayout = findViewById(R.id.mpesaPhoneLayout)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        btnSubmit = findViewById(R.id.btnSaveTransaction)
        btnClear = findViewById(R.id.btnClear)
        rvRecentSales = findViewById(R.id.rvRecentSales)
        paymentProgress = findViewById(R.id.paymentProgress)
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
                    val updatedSale = sale.copy(
                        productName = etProdName.text.toString(),
                        quantity = newQty,
                        unitPrice = newPrice,
                        totalAmount = newQty * newPrice
                    )
                    recentSales[index] = updatedSale
                    salesAdapter.updateList(recentSales)
                    saveSaleToFirestore(updatedSale)
                    saveSales()
                }
            }
            .setNeutralButton("Delete") { _, _ ->
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    db.collection("users").document(currentUser.uid).collection("sales")
                        .document(sale.id).delete()
                }
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
        tvTotalAmount.text = String.format(Locale.getDefault(), "Ksh %.2f", total)
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
            paymentMode == "M-Pesa" && etMpesaPhone.text.toString().isEmpty() -> {
                etMpesaPhone.error = "Enter M-Pesa phone number"
                etMpesaPhone.requestFocus()
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
        
        // Save to Firestore and Local
        saveSaleToFirestore(newSale)
        saveSales()

        Toast.makeText(this, "Sale Recorded Successfully", Toast.LENGTH_SHORT).show()
        
        // Initiate Payment
        val phone = etMpesaPhone.text.toString().trim()
        initiatePayment(total, paymentMode, phone)
        
        clearForm()
    }

    private fun saveSaleToFirestore(sale: Sale) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("users").document(currentUser.uid).collection("sales")
                .document(sale.id)
                .set(sale)
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to sync sale to cloud", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun initiatePayment(amount: Double, mode: String, phone: String) {
        when (mode) {
            "M-Pesa" -> executeMpesaStkPush(amount, phone)
            "PayPal" -> executePayPalOrder(amount)
            "Debit Mastercard" -> executeMastercardPayment(amount)
            else -> showToast("Unsupported payment method")
        }
    }

    private fun executeMpesaStkPush(amount: Double, phone: String) {
        if (PaymentConfig.MPESA_CONSUMER_KEY == "YOUR_CONSUMER_KEY") {
            showToast("M-Pesa is not configured yet. Please update PaymentConfig.kt")
            return
        }

        showLoadingSpinner()
        val authHeader = "Basic " + Base64.encodeToString(
            "${PaymentConfig.MPESA_CONSUMER_KEY}:${PaymentConfig.MPESA_CONSUMER_SECRET}".toByteArray(),
            Base64.NO_WRAP
        )

        mpesaService.getAccessToken(authHeader).enqueue(object : Callback<MpesaTokenResponse> {
            override fun onResponse(call: Call<MpesaTokenResponse>, response: Response<MpesaTokenResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.accessToken
                    if (token != null) {
                        performSTKPush(token, amount, phone)
                    } else {
                        hideLoadingSpinner()
                        showToast("Failed to retrieve M-Pesa token")
                    }
                } else {
                    hideLoadingSpinner()
                    showToast("M-Pesa Auth Failed: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<MpesaTokenResponse>, t: Throwable) {
                hideLoadingSpinner()
                showToast("Network Error: ${t.message}")
            }
        })
    }

    private fun performSTKPush(token: String, amount: Double, phone: String) {
        val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        val password = Base64.encodeToString(
            "${PaymentConfig.MPESA_BUSINESS_SHORT_CODE}${PaymentConfig.MPESA_PASSKEY}$timestamp".toByteArray(),
            Base64.NO_WRAP
        )

        // Sanitize phone number (remove +, ensures starts with 254)
        var cleanPhone = phone.replace("+", "")
        if (cleanPhone.startsWith("0")) {
            cleanPhone = "254" + cleanPhone.substring(1)
        }

        val request = STKPushRequest(
            businessShortCode = PaymentConfig.MPESA_BUSINESS_SHORT_CODE,
            password = password,
            timestamp = timestamp,
            amount = amount.toInt().toString(),
            partyA = cleanPhone,
            businessShortCodeB = PaymentConfig.MPESA_BUSINESS_SHORT_CODE,
            phoneNumber = cleanPhone,
            callBackUrl = PaymentConfig.MPESA_CALLBACK_URL,
            accountReference = "SalesAppOrder",
            transactionDesc = "Payment for Sale"
        )

        mpesaService.initiateSTKPush("Bearer $token", request).enqueue(object : Callback<STKPushResponse> {
            override fun onResponse(call: Call<STKPushResponse>, response: Response<STKPushResponse>) {
                hideLoadingSpinner()
                if (response.isSuccessful) {
                    showToast("STK Push initiated! Please enter your PIN on your phone.")
                } else {
                    showToast("STK Push failed: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<STKPushResponse>, t: Throwable) {
                hideLoadingSpinner()
                showToast("STK Push error: ${t.message}")
            }
        })
    }

    private fun executePayPalOrder(amount: Double) {
        showToast("Initiating PayPal payment for Ksh $amount...")
    }

    private fun executeMastercardPayment(amount: Double) {
        showToast("Opening Mastercard secure payment for Ksh $amount...")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoadingSpinner() {
        paymentProgress.visibility = View.VISIBLE
        btnSubmit.isEnabled = false
    }

    private fun hideLoadingSpinner() {
        paymentProgress.visibility = View.GONE
        btnSubmit.isEnabled = true
    }

    private fun clearForm() {
        etProductName.text?.clear()
        etQuantity.text?.clear()
        etUnitPrice.text?.clear()
        etCustomerName.text?.clear()
        etMpesaPhone.text?.clear()
        actvPaymentMode.setText(paymentModes[0], false)
        updateMpesaFieldVisibility(paymentModes[0])
        tvTotalAmount.text = "Ksh 0.00"
        etProductName.requestFocus()
    }
}
