package com.motologr.ui.logging.fuel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.motologr.MainActivity
import com.motologr.R
import com.motologr.data.DataManager

class FuelLoggingAdapter(private val mList: List<FuelLoggingItemsViewModel>) : RecyclerView.Adapter<FuelLoggingAdapter.ViewHolder>() {

    // create new views 
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view  
        // that is used to hold list item 
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_fuel_logging, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view 
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        // sets the image to the imageview from our itemHolder class 
        holder.imageView.setImageResource(ItemsViewModel.image)

        // sets the text to the textview from our itemHolder class 
        holder.fuelDt.text = ItemsViewModel.fuelDt
        holder.fuelPrice.text = ItemsViewModel.fuelPrice
        holder.fuelVolume.text = ItemsViewModel.fuelVolume

        if (ItemsViewModel.fuelVolume == "-1.0 L") {
            holder.fuelVolume.isVisible = false
        }

        holder.itemView.setOnClickListener { v ->
            val bundle: Bundle = Bundle()
            bundle.putInt("position", position)

            holder.itemView.findNavController().navigate(R.id.nav_fuel, bundle)
        }
    }

    // return the number of the items in the list 
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text 
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.fuel_logging_card_image)
        val fuelDt: TextView = itemView.findViewById(R.id.fuel_logging_card_date)
        val fuelPrice: TextView = itemView.findViewById(R.id.fuel_logging_card_price)
        val fuelVolume: TextView = itemView.findViewById(R.id.fuel_logging_card_volume)
    }
}
