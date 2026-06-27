package com.example.salesmanagment

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class CategoryAdapter(
    private val categories: List<CategoryData>,
    private val onItemClick: (CategoryData) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position], onItemClick)
    }

    override fun getItemCount(): Int = categories.size

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: MaterialCardView = itemView as MaterialCardView
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivCategoryIcon)
        private val tvName: TextView = itemView.findViewById(R.id.tvCategoryName)
        private val tvDesc: TextView = itemView.findViewById(R.id.tvCategoryDesc)

        fun bind(category: CategoryData, onItemClick: (CategoryData) -> Unit) {
            tvName.text = category.name
            tvDesc.text = category.description
            ivIcon.setImageResource(category.iconRes)
            
            val color = Color.parseColor(category.colorHex)
            cardView.strokeColor = color
            
            // Subtle tinted background
            val alphaColor = Color.argb(40, Color.red(color), Color.green(color), Color.blue(color))
            cardView.setCardBackgroundColor(alphaColor)
            ivIcon.setColorFilter(color)

            itemView.setOnClickListener { onItemClick(category) }
        }
    }
}
