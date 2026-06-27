package com.example.salesmanagment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView

class CategoryActivity : AppCompatActivity() {

    private val categories = listOf(
        CategoryData("Promos", "Check out today's best deals and offers.", R.drawable.ic_assessment, "#F44336"),
        CategoryData("Food Cupboard", "Cereals, oils, flour, spices, and canned goods.", R.drawable.ic_shopping_cart, "#FF9800"),
        CategoryData("Fresh Food", "Fresh produce, meats, deli, and bakery items.", R.drawable.ic_shopping_cart, "#4CAF50"),
        CategoryData("Baby & Kids", "Infant formula, diapers, toys, and baby care.", R.drawable.ic_people, "#E91E63"),
        CategoryData("Beverage", "Hot drinks, juices, sodas, and water.", R.drawable.ic_shopping_cart, "#2196F3"),
        CategoryData("Electronics", "TVs, audio, appliances, and kitchen gadgets.", R.drawable.ic_inventory, "#009688"),
        CategoryData("Cleaning", "Detergents, soaps, and household essentials.", R.drawable.ic_inventory, "#607D8B"),
        CategoryData("Beauty & Cosmetics", "Skin care, hygiene, and makeup products.", R.drawable.ic_people, "#9C27B0"),
        CategoryData("Liqour", "Fine wines, spirits, and beers.", R.drawable.ic_shopping_cart, "#B71C1C"),
        CategoryData("More Categories>>>", "Explore all other store departments.", R.drawable.ic_inventory, "#757575")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_category)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.category_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupToolbar()
        setupCategoryList()
        setupNavigation()
    }

    private fun setupToolbar() {
        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupCategoryList() {
        val rv = findViewById<RecyclerView>(R.id.rvCategories)
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = GridLayoutManager(this, columns)
        
        rv.adapter = CategoryAdapter(categories) { category ->
            when (category.name) {
                "Promos" -> startActivity(Intent(this, PromosActivity::class.java))
                "Food Cupboard" -> startActivity(Intent(this, FoodCupboardActivity::class.java))
                "Fresh Food" -> startActivity(Intent(this, FreshFoodActivity::class.java))
                "Baby & Kids" -> startActivity(Intent(this, BabyKidsActivity::class.java))
                "Beverage" -> startActivity(Intent(this, BeverageActivity::class.java))
                "Electronics" -> startActivity(Intent(this, ElectronicsActivity::class.java))
                "Cleaning" -> startActivity(Intent(this, CleaningActivity::class.java))
                "Beauty & Cosmetics" -> startActivity(Intent(this, BeautyCosmeticsActivity::class.java))
                "Liqour" -> startActivity(Intent(this, LiquorActivity::class.java))
                "More Categories>>>" -> startActivity(Intent(this, MoreCategoriesActivity::class.java))
                else -> {
                    val intent = Intent(this, InventoryActivity::class.java)
                    intent.putExtra("FILTER_CATEGORY", category.name)
                    startActivity(intent)
                }
            }
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

data class CategoryData(
    val name: String,
    val description: String,
    val iconRes: Int,
    val colorHex: String
)
