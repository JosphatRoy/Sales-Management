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

class BreakfastCerealsEggsActivity : AppCompatActivity() {

    private val items = listOf(
        // Breakfast Cereals
        CategoryData("Weetabix", "Includes Weetos and Oatibix.", R.drawable.ic_shopping_cart, "#B71C1C"),
        CategoryData("Kellogg's", "Corn Flakes, Crunchy Nut, Frosties, Coco Pops.", R.drawable.ic_shopping_cart, "#D32F2F"),
        CategoryData("Proctor & Allan", "Quality breakfast cereals.", R.drawable.ic_shopping_cart, "#C62828"),
        CategoryData("Morning Harvest", "Morning Harvest cereal range.", R.drawable.ic_shopping_cart, "#F44336"),
        CategoryData("Temmy's", "Temmy's breakfast cereals.", R.drawable.ic_shopping_cart, "#EF5350"),
        CategoryData("Sante", "Health-focused cereals.", R.drawable.ic_shopping_cart, "#E57373"),
        CategoryData("Naturalli", "Natural breakfast options.", R.drawable.ic_shopping_cart, "#FF8A80"),
        CategoryData("Nuvita", "Nuvita Cereos and more.", R.drawable.ic_shopping_cart, "#FF5252"),
        CategoryData("Shapies / Elbbins", "Fun shaped cereals.", R.drawable.ic_shopping_cart, "#FF1744"),
        CategoryData("Fit", "Fit brand breakfast cereals.", R.drawable.ic_shopping_cart, "#D50000"),
        CategoryData("Alpen", "Muesli and breakfast bars.", R.drawable.ic_shopping_cart, "#F44336"),
        CategoryData("Bokomo", "Bokomo cereal varieties.", R.drawable.ic_shopping_cart, "#E53935"),
        CategoryData("Nestle Cereals", "Cheerios and Nestle breakfast range.", R.drawable.ic_shopping_cart, "#D81B60"),
        
        // Eggs
        CategoryData("Isinya Eggs", "Fresh Isinya farm eggs.", R.drawable.ic_shopping_cart, "#FFD600"),
        CategoryData("Green Farm Eggs", "Yellow Yolk farm eggs.", R.drawable.ic_shopping_cart, "#FFEA00"),
        CategoryData("Kenegg", "Golden Yolk premium eggs.", R.drawable.ic_shopping_cart, "#FFEE58"),
        CategoryData("Jandee Eggs", "Fresh Jandee farm eggs.", R.drawable.ic_shopping_cart, "#FFF176"),
        CategoryData("Jupiters Eggs", "Jupiters Yellow Yolk eggs.", R.drawable.ic_shopping_cart, "#FFF59D")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_breakfast_cereals_eggs)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.breakfast_root)) { v, insets ->
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
        val rv = findViewById<RecyclerView>(R.id.rvBreakfastCerealsEggs)
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = GridLayoutManager(this, columns)
        
        rv.adapter = CategoryAdapter(items) { item ->
            val intent = Intent(this, CerealsEggsBrandActivity::class.java)
            intent.putExtra("BRAND_NAME", item.name)
            startActivity(intent)
        }
    }

    private fun setupNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.setOnItemSelectedListener { item -> handleNavigation(item.itemId) }
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
