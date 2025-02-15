package com.example.madproject

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TravelLocationAdapter(
    private val locationList: List<TravelLocation>
) : RecyclerView.Adapter<TravelLocationAdapter.TravelLocationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TravelLocationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_travel_location, parent, false)
        return TravelLocationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TravelLocationViewHolder, position: Int) {
        val currentLocation = locationList[position]
        holder.bind(currentLocation)
        holder.itemView.setOnClickListener {
            // Create an Intent to start the detail activity
            val context = holder.itemView.context
            val intent = Intent(context, v_scroll_dash::class.java)
            // Pass all needed details as extras
            intent.putExtra("placeId", currentLocation.id)
            intent.putExtra("category", currentLocation.category)
            intent.putExtra("locationName", currentLocation.locationName)
            intent.putExtra("description", currentLocation.description)
            intent.putExtra("price", currentLocation.price) // Make sure TravelLocation has a price field
            intent.putExtra("imageRes", currentLocation.imageRes)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = locationList.size

    class TravelLocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Find views by ID in the item layout
        private val tvLocationName: TextView = itemView.findViewById(R.id.tvLocationName)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        private val ivPlaceImage: ImageView = itemView.findViewById(R.id.ivPlaceImage)

        fun bind(travelLocation: TravelLocation) {
            tvLocationName.text = travelLocation.locationName
            tvDescription.text = travelLocation.description
            tvRating.text = "Ratings: ${travelLocation.rating}"
            // Optionally, set an image if your layout has an ImageView (ivPlaceImage)
            if (travelLocation.imageRes != 0) {
                ivPlaceImage.setImageResource(travelLocation.imageRes)
            }
        }
    }
}
