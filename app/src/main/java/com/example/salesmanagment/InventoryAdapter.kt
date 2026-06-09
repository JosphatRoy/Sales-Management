package com.example.salesmanagment

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class InventoryAdapter(private var products: List<Product>) :
    RecyclerView.Adapter<InventoryAdapter.ProductViewHolder>() {

    fun updateList(newList: List<Product>) {
        products = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = products.size

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvProductCategory)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        private val tvStock: TextView = itemView.findViewById(R.id.tvStockLevel)
        private val stockIndicator: View = itemView.findViewById(R.id.stockIndicator)

        fun bind(product: Product) {
            tvName.text = product.name
            tvCategory.text = product.category
            tvPrice.text = String.format(Locale.getDefault(), "$ %.2f", product.price)
            tvStock.text = "Stock: ${product.stock}"

            val color = when {
                product.stock <= 5 -> Color.RED
                product.stock <= 15 -> Color.YELLOW
                else -> Color.GREEN
            }
            stockIndicator.setBackgroundColor(color)
        }
    }
}