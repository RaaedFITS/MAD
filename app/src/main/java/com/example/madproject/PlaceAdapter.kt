package com.example.madproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlaceAdapter(private var placeList: List<Place>) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeTypeText: TextView = itemView.findViewById(R.id.textViewPlaceType)
        val locationText: TextView = itemView.findViewById(R.id.textViewLocation)
        val descriptionText: TextView = itemView.findViewById(R.id.textViewDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeTypeText.text = place.placeType
        holder.locationText.text = place.location
        holder.descriptionText.text = place.description
    }

    override fun getItemCount(): Int = placeList.size

    fun updateData(newData: List<Place>) {
        placeList = newData
        notifyDataSetChanged()
    }
}
