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

class MaizeFlourActivity : AppCompatActivity() {

    private val maizeFlourBrands = listOf(
        CategoryData("Amaize", "Premium sifted maize flour.", R.drawable.ic_shopping_cart, "#FFB74D"),
        CategoryData("Pembe", "Quality sifted maize flour.", R.drawable.ic_shopping_cart, "#FFA726"),
        CategoryData("Jogoo", "The traditional choice for Ugali.", R.drawable.ic_shopping_cart, "#FF9800"),
        CategoryData("Soko", "Sifted maize meal.", R.drawable.ic_shopping_cart, "#FB8C00"),
        CategoryData("Ajab", "Fortified sifted maize flour.", R.drawable.ic_shopping_cart, "#F57C00"),
        CategoryData("Dola", "Premium sifted maize meal.", R.drawable.ic_shopping_cart, "#EF6C00"),
        CategoryData("Raha Premium", "High quality maize flour.", R.drawable.ic_shopping_cart, "#E65100"),
        CategoryData("Lea Premium", "Quality sifted maize meal.", R.drawable.ic_shopping_cart, "#D84315"),
        CategoryData("Masterchef", "Sifted maize flour.", R.drawable.ic_shopping_cart, "#BF360C"),
        CategoryData("Tupike", "Value for money maize flour.", R.drawable.ic_shopping_cart, "#FFB74D"),
        CategoryData("210", "Sifted maize meal.", R.drawable.ic_shopping_cart, "#FFA726"),
        CategoryData("Mama", "Nutritious sifted maize flour.", R.drawable.ic_shopping_cart, "#FF9800"),
        CategoryData("Spenza", "Quality maize meal.", R.drawable.ic_shopping_cart, "#FB8C00"),
        CategoryData("Nice Extra", "Nice Extra sifted maize flour.", R.drawable.ic_shopping_cart, "#F57C00"),
        CategoryData("Soko Nutri Wholegrain", "Wholegrain maize meal for health.", R.drawable.ic_shopping_cart, "#4CAF50"),
        CategoryData("Ugali Afya (Winnie's)", "Winnie's Pure Health Ugali Afya.", R.drawable.ic_shopping_cart, "#388E3C"),
        CategoryData("Ugali Afya (Natures)", "Natures Equatorial Ugali Afya.", R.drawable.ic_shopping_cart, "#2E7D32")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_maize_flour)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.maize_root)) { v, insets ->
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
        val rv = findViewById<RecyclerView>(R.id.rvMaizeFlour)
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = GridLayoutManager(this, columns)
        
        rv.adapter = CategoryAdapter(maizeFlourBrands) { item ->
            val intent = Intent(this, MaizeBrandActivity::class.java)
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
