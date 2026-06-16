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
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.result.contract.ActivityResultContracts
import android.content.pm.PackageManager
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import android.Manifest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Locale
import androidx.core.net.toUri

class CustomersActivity : AppCompatActivity() {

    private lateinit var adapter: CustomersAdapter
    private val allCustomers = mutableListOf<Customer>()

    private var etNameRef: TextInputEditText? = null
    private var etPhoneRef: TextInputEditText? = null
    private var etEmailRef: TextInputEditText? = null

    private val contactPickerLauncher = registerForActivityResult(ActivityResultContracts.PickContact()) { uri ->
        uri?.let { extractContactInfo(it) }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            contactPickerLauncher.launch(null)
        } else {
            Toast.makeText(this, "Permission denied to access contacts", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_customers)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.customers_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupToolbar()
        setupCustomersList()
        setupSearch()
        setupAddButton()
        setupNavigation()

        // Load data
        loadCustomers()
        if (allCustomers.isEmpty()) {
            loadMockCustomers()
        }
    }

    private fun saveCustomers() {
        val prefs = getSharedPreferences("sales_prefs", MODE_PRIVATE)
        val json = Gson().toJson(allCustomers)
        prefs.edit().putString("customers_list", json).apply()
    }

    private fun loadCustomers() {
        val prefs = getSharedPreferences("sales_prefs", MODE_PRIVATE)
        val json = prefs.getString("customers_list", null)
        if (json != null) {
            val type = object : TypeToken<List<Customer>>() {}.type
            val list: List<Customer> = Gson().fromJson(json, type)
            allCustomers.clear()
            allCustomers.addAll(list)
            adapter.updateList(allCustomers)
        }
    }

    private fun setupNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navRail = findViewById<NavigationRailView>(R.id.navigation_rail)

        bottomNav?.selectedItemId = R.id.nav_customers
        navRail?.selectedItemId = R.id.nav_customers

        bottomNav?.setOnItemSelectedListener { item -> handleNavigation(item.itemId) }
        navRail?.setOnItemSelectedListener { item -> handleNavigation(item.itemId) }
    }

    private fun handleNavigation(itemId: Int): Boolean {
        if (itemId == R.id.nav_customers) return true

        return when (itemId) {
            R.id.nav_home -> { navigateTo(Transactionscreen::class.java); true }
            R.id.nav_inventory -> { navigateTo(InventoryActivity::class.java); true }
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

//    private fun setupCustomersList() {
//        val rv = findViewById<RecyclerView>(R.id.rvCustomers)
//
//        // Responsive columns using the integer resource
//        val columns = resources.getInteger(R.integer.dashboard_columns)
//        rv.layoutManager = GridLayoutManager(this, columns)
//
//        adapter = CustomersAdapter(allCustomers)
//        rv.adapter = adapter
//    }

    private fun setupCustomersList() {
        val rv = findViewById<RecyclerView>(R.id.rvCustomers)

        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = GridLayoutManager(this, columns)

        adapter = CustomersAdapter(allCustomers) { customer ->
            showCustomerDetailsModal(customer)
        }
        rv.adapter = adapter
    }

    private fun showEditCustomerDialog(customer: Customer) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_customer, null)
        val etName = dialogView.findViewById<TextInputEditText>(R.id.etName)
        val etPhone = dialogView.findViewById<TextInputEditText>(R.id.etPhone)
        val etEmail = dialogView.findViewById<TextInputEditText>(R.id.etEmail)
        val btnImport = dialogView.findViewById<Button>(R.id.btnImportContact)
        
        btnImport.visibility = android.view.View.GONE

        etName.setText(customer.name)
        etPhone.setText(customer.phone)
        etEmail.setText(customer.email)

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Edit Customer")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val index = allCustomers.indexOf(customer)
                if (index != -1) {
                    allCustomers[index] = customer.copy(
                        name = etName.text.toString(),
                        phone = etPhone.text.toString(),
                        email = etEmail.text.toString()
                    )
                    adapter.updateList(allCustomers)
                    saveCustomers()
                    Toast.makeText(this, "Customer updated", Toast.LENGTH_SHORT).show()
                }
            }
            .setNeutralButton("Delete") { _, _ ->
                allCustomers.remove(customer)
                adapter.updateList(allCustomers)
                saveCustomers()
                Toast.makeText(this, "Customer deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCustomerDetailsModal(customer: Customer) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_customer_details, null)
        
        dialogView.findViewById<TextView>(R.id.tvDetName).text = customer.name
        dialogView.findViewById<TextView>(R.id.tvDetPhone).text = customer.phone
        dialogView.findViewById<TextView>(R.id.tvDetEmail).text = customer.email
        dialogView.findViewById<TextView>(R.id.tvDetTotal).text = String.format(Locale.getDefault(), "Total Purchases: $%.2f", customer.totalPurchases)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Edit") { _, _ -> showEditCustomerDialog(customer) }
            .setNegativeButton("Close", null)
            .create()

        dialogView.findViewById<Button>(R.id.btnCall).setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = "tel:${customer.phone}".toUri()
            }
            startActivity(intent)
        }

        dialogView.findViewById<Button>(R.id.btnEmail).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:${customer.email}".toUri()
            }
            try {
                startActivity(intent)
            } catch (_: Exception) {
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
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
        val filteredList = allCustomers.filter {
            it.name.contains(query, ignoreCase = true) || 
            it.phone.contains(query, ignoreCase = true) ||
            it.email.contains(query, ignoreCase = true)
        }
        adapter.updateList(filteredList)
    }

    private fun setupAddButton() {
        findViewById<FloatingActionButton>(R.id.fabAddCustomer).setOnClickListener {
            showAddCustomerDialog()
        }
    }

    private fun showAddCustomerDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_customer, null)
        val etName = dialogView.findViewById<TextInputEditText>(R.id.etName)
        val etPhone = dialogView.findViewById<TextInputEditText>(R.id.etPhone)
        val etEmail = dialogView.findViewById<TextInputEditText>(R.id.etEmail)
        val btnImport = dialogView.findViewById<Button>(R.id.btnImportContact)

        etNameRef = etName
        etPhoneRef = etPhone
        etEmailRef = etEmail

        btnImport.setOnClickListener {
            checkPermissionAndPickContact()
        }

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = etName.text.toString()
                val phone = etPhone.text.toString()
                val email = etEmail.text.toString()

                if (name.isNotEmpty() && phone.isNotEmpty()) {
                    val newCustomer = Customer(
                        id = System.currentTimeMillis().toString(),
                        name = name,
                        phone = phone,
                        email = email,
                        totalPurchases = 0.0
                    )
                    allCustomers.add(0, newCustomer) // Add to top of list
                    adapter.updateList(allCustomers)
                    saveCustomers()
                    Toast.makeText(this, "Customer added successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please enter name and phone", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun checkPermissionAndPickContact() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            contactPickerLauncher.launch(null)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    private fun extractContactInfo(uri: Uri) {
        val contentResolver = contentResolver
        val cursor = contentResolver.query(uri, null, null, null, null)

        if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
            etNameRef?.setText(name)

            // Get Phone
            val hasPhoneNumber = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER))
            if (hasPhoneNumber > 0) {
                val phoneCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    arrayOf(id),
                    null
                )
                if (phoneCursor != null && phoneCursor.moveToFirst()) {
                    val phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    etPhoneRef?.setText(phoneNumber)
                    phoneCursor.close()
                }
            }

            // Get Email
            val emailCursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                arrayOf(id),
                null
            )
            if (emailCursor != null && emailCursor.moveToFirst()) {
                val email = emailCursor.getString(emailCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS))
                etEmailRef?.setText(email)
                emailCursor.close()
            }
            cursor.close()
        }
    }

    private fun loadMockCustomers() {
        allCustomers.addAll(listOf(
            Customer("1", "John Doe", "+254 711 222 333", "john@.....com", 1500.50),
            Customer("2", "Jane Smith", "+254 722 333 444", "jane@....com", 2400.00),
            Customer("3", "David Kimani", "+254 733 444 555", "david@....com", 500.00),
            Customer("4", "Sarah Omari", "+254 744 555 666", "sarah@....com", 3200.75),
            Customer("5", "Michael Brown", "+254 755 666 777", "michael@....com", 120.00),
            Customer("6", "Alice Cooper", "+254 766 777 888", "alice@.....com", 4500.00)



        ))
        adapter.updateList(allCustomers)
    }
}