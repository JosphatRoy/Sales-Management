package com.example.salesmanagment

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView

class WheatFlourActivity : AppCompatActivity() {

    private val wheatFlourBrands = listOf(
        CategoryData("Exe", "All-Purpose, Self-Raising, Mandazi, Chapati, Atta.", R.drawable.ic_shopping_cart, "#FFA726"),
        CategoryData("Ajab Wheat", "All-Purpose, Self Raising, Mandazi, Atta Mark 1.", R.drawable.ic_shopping_cart, "#FF9800"),
        CategoryData("Soko Wheat", "All-Purpose, Atta Mark 1.", R.drawable.ic_shopping_cart, "#FB8C00"),
        CategoryData("Pembe Wheat", "All-Purpose, Self Raising, Atta.", R.drawable.ic_shopping_cart, "#F57C00"),
        CategoryData("Dola Wheat", "High quality wheat flour.", R.drawable.ic_shopping_cart, "#EF6C00"),
        CategoryData("Masterchef Wheat", "Masterchef quality wheat flour.", R.drawable.ic_shopping_cart, "#E65100"),
        CategoryData("Ndovu", "Standard wheat flour brands.", R.drawable.ic_shopping_cart, "#D84315"),
        CategoryData("210 Wheat", "Sifted wheat flour.", R.drawable.ic_shopping_cart, "#BF360C"),
        CategoryData("Lea Wheat", "Quality wheat meal.", R.drawable.ic_shopping_cart, "#8D6E63"),
        CategoryData("Umoja", "Umoja wheat flour brands.", R.drawable.ic_shopping_cart, "#795548"),
        CategoryData("Elliots", "All-Purpose, Atta Mark 1.", R.drawable.ic_shopping_cart, "#6D4C41"),
        CategoryData("Lotus", "Specialty wheat flour.", R.drawable.ic_shopping_cart, "#5D4037"),
        CategoryData("Pendo", "Quality wheat flour.", R.drawable.ic_shopping_cart, "#4E342E"),
        CategoryData("Phulka Atta", "Phulka Atta Mark 1.", R.drawable.ic_shopping_cart, "#3E2723"),
        CategoryData("Butterfly Atta", "Butterfly Atta Mark.", R.drawable.ic_shopping_cart, "#FFA000"),
        CategoryData("Chapati Afya", "Winnie's Pure Health Chapati Afya.", R.drawable.ic_shopping_cart, "#4CAF50")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_wheat_flour)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.wheat_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupToolbar()
        setupList()
        setupNavigation()
    }

    private fun setupToolbar() {
        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupList() {
        val rv = findViewById<RecyclerView>(R.id.rvWheatFlour)
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = GridLayoutManager(this, columns)
        
        rv.adapter = CategoryAdapter(wheatFlourBrands) { item ->
            val intent = Intent(this, WheatBrandActivity::class.java)
            intent.putExtra("BRAND_NAME", item.name)
            startActivity(intent)
        }
    }

    private fun setupNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navRail = findViewById<NavigationRailView>(R.id.navigation_rail)

        bottomNav?.setOnItemSelectedListener { item -> handleNavigation(item.itemId) }
        navRail?.setOnItemSelectedListener { item -> handleNavigation(item.itemId) }
    }

    private fun handleNavigation(itemId: Int): Boolean {
        return when (itemId) {
            R.id.nav_home -> { navigateTo(HomeActivity::class.java); true }
            R.id.nav_sales -> { navigateTo(Transactionscreen::class.java); true }
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
}
