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

class FlourRiceCerealsActivity : AppCompatActivity() {

    private val items = listOf(
        CategoryData("Maize Flour", "Amaize, Pembe, Jogoo, Soko, Ajab, Dola, Raha, Lea, Mama, Spenza...", R.drawable.ic_shopping_cart, "#FFB74D"),
        CategoryData("Wheat Flour", "Exe, Ajab, Soko, Pembe, Dola, Ndovu, Elliots, Pendo, Atta Mark 1...", R.drawable.ic_shopping_cart, "#FFA726"),
        CategoryData("Porridge (Uji) Flour", "Famila, Soko, Pembe, Jogoo, Winnie's Pure Health, Rimwabi...", R.drawable.ic_shopping_cart, "#FF9800"),
        CategoryData("Specialty Flour", "Gram Flour, Rice Flour, Oat Flour, Cassava, Rye, Coconut Flour.", R.drawable.ic_shopping_cart, "#FB8C00"),
        CategoryData("Basmati Rice", "Sunrice, Daawat, Pearl Super Basmati, Ranee Premium.", R.drawable.ic_shopping_cart, "#F57C00"),
        CategoryData("Mwea Pishori Rice", "Jamii, Kings M.P.S, Cil, Pearl, Farm Pishori.", R.drawable.ic_shopping_cart, "#EF6C00"),
        CategoryData("Long Grain Rice", "Daawat, Farm Pakistani/Thailand, White Long Grain.", R.drawable.ic_shopping_cart, "#E65100"),
        CategoryData("Brown Rice", "Cil Aromatic Brown Rice.", R.drawable.ic_shopping_cart, "#D84315"),
        CategoryData("Beans", "Wairimu, Njahi, Yellow Beans, Kamande, Nyayo, Rosecoco...", R.drawable.ic_shopping_cart, "#A1887F"),
        CategoryData("Grams & Lentils", "Green Grams, Red Lentils, Toor Dal, Chickpeas, Pigeon Peas.", R.drawable.ic_shopping_cart, "#8D6E63"),
        CategoryData("Popcorns & Grains", "Butterfly Grains, Farm Popcorn, American Garden Microwave.", R.drawable.ic_shopping_cart, "#795548")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_flour_rice_cereals)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.flour_root)) { v, insets ->
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
        val rv = findViewById<RecyclerView>(R.id.rvFlourRiceCereals)
        val columns = resources.getInteger(R.integer.dashboard_columns)
        rv.layoutManager = GridLayoutManager(this, columns)
        
        rv.adapter = CategoryAdapter(items) { item ->
            when (item.name) {
                "Maize Flour" -> startActivity(Intent(this, MaizeFlourActivity::class.java))
                "Wheat Flour" -> startActivity(Intent(this, WheatFlourActivity::class.java))
                "Porridge (Uji) Flour" -> startActivity(Intent(this, PorridgeFlourActivity::class.java))
                "Specialty Flour" -> startActivity(Intent(this, SpecialtyFlourActivity::class.java))
                "Basmati Rice", "Mwea Pishori Rice", "Long Grain Rice", "Brown Rice" -> startActivity(Intent(this, RiceActivity::class.java))
                "Beans", "Grams & Lentils", "Popcorns & Grains" -> startActivity(Intent(this, CerealsPulsesActivity::class.java))
                else -> {
                    val intent = Intent(this, InventoryActivity::class.java)
                    intent.putExtra("FILTER_CATEGORY", item.name)
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
