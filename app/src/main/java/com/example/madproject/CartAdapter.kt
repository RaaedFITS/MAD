package com.example.madproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(private var orderList: List<Order>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPlaceId: TextView = itemView.findViewById(R.id.tvPlaceId)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val order = orderList[position]
        holder.tvPlaceId.text = "Place ID: ${order.placeId}"
        holder.tvPrice.text = "Price: $${order.price}"
        // Optionally, format the timestamp into a readable date
        holder.tvTimestamp.text = "Time: ${order.timestamp}"
        holder.tvStatus.text = "Status: ${order.status}"
    }

    override fun getItemCount(): Int = orderList.size

    fun updateData(newData: List<Order>) {
        orderList = newData
        notifyDataSetChanged()
    }
}
