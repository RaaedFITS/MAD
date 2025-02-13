package com.example.madproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madproject.R

class CategoryAdapter(
    private val categoryList: List<CategoryItem>,
    private val onCategoryClicked: (CategoryItem) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.bind(category)
        holder.itemView.setOnClickListener {
            onCategoryClicked(category)
        }
    }

    override fun getItemCount() = categoryList.size

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Define references to views
        private val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
        private val ivCategoryIcon: ImageView = itemView.findViewById(R.id.ivCategoryIcon)

        fun bind(category: CategoryItem) {
            tvCategoryName.text = category.name
            ivCategoryIcon.setImageResource(category.iconResId)
        }
    }
}
