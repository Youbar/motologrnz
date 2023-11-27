package com.motologr.ui.logging.maint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.motologr.R
import com.motologr.ui.data.DataManager

class MaintLoggingAdapter(private val mList: List<MaintLoggingItemsViewModel>) : RecyclerView.Adapter<MaintLoggingAdapter.ViewHolder>() {

    // create new views 
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view  
        // that is used to hold list item 
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_maint_logging, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view 
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        // sets the image to the imageview from our itemHolder class 
        holder.imageView.setImageResource(ItemsViewModel.image)

        // sets the text to the textview from our itemHolder class 
        holder.maintType.text = ItemsViewModel.maintType
        holder.maintDt.text = ItemsViewModel.maintDt
        holder.maintPrice.text = ItemsViewModel.maintPrice

        holder.itemView.setOnClickListener { v ->
            val bundle: Bundle = Bundle()
            bundle.putInt("position", position)

            // Need a function to collect all logs, sort by date, and then find relative within that collection of logs
            // and then absolute within that subset of the logs
            if (holder.maintType.text == "Repair") {
                holder.itemView.findNavController().navigate(R.id.nav_repair, bundle)
            } else if (holder.maintType.text == "Service") {
                holder.itemView.findNavController().navigate(R.id.nav_service, bundle)
            } else if (holder.maintType.text == "WOF") {
                holder.itemView.findNavController().navigate(R.id.nav_wof, bundle)
            }
        }
    }

    // return the number of the items in the list 
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text 
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.maint_logging_card_image)
        val maintType: TextView = itemView.findViewById(R.id.maint_logging_card_type)
        val maintDt: TextView = itemView.findViewById(R.id.maint_logging_card_date)
        val maintPrice: TextView = itemView.findViewById(R.id.maint_logging_card_price)
    }
}
