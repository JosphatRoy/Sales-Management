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

class CustomersActivity : AppCompatActivity() {

    private lateinit var adapter: CustomersAdapter
    private val allCustomers = mutableListOf<Customer>()

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

        // Load mock data
        loadMockCustomers()
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

    private fun setupCustomersList() {
        val rv = findViewById<RecyclerView>(R.id.rvCustomers)
        
        // Responsive columns using the integer resource
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = GridLayoutManager(this, columns)
        
        adapter = CustomersAdapter(allCustomers)
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
        val filteredList = allCustomers.filter {
            it.name.contains(query, ignoreCase = true) || 
            it.phone.contains(query, ignoreCase = true) ||
            it.email.contains(query, ignoreCase = true)
        }
        adapter.updateList(filteredList)
    }

    private fun setupAddButton() {
        findViewById<FloatingActionButton>(R.id.fabAddCustomer).setOnClickListener {
            Toast.makeText(this, "Add Customer Clicked", Toast.LENGTH_SHORT).show()
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