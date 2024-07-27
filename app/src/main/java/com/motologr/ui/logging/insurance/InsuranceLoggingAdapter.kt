package com.motologr.ui.logging.insurance
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.motologr.R

class InsuranceLoggingAdapter(private val mList: List<InsuranceLoggingItemsViewModel>) : RecyclerView.Adapter<InsuranceLoggingAdapter.ViewHolder>() {

    // create new views 
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view  
        // that is used to hold list item 
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_insurance_policies, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view 
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        // sets the image to the imageview from our itemHolder class 
        holder.imageView.setImageResource(ItemsViewModel.image)

        // sets the text to the textview from our itemHolder class 
        holder.startDt.text = ItemsViewModel.startDt
        holder.endDt.text = ItemsViewModel.endDt
        holder.policyPrice.text = ItemsViewModel.policyPrice
        holder.policyFrequency.text = ItemsViewModel.policyFrequency

        holder.itemView.setOnClickListener { v ->
            val bundle = Bundle()
            bundle.putInt("position", position)

            holder.itemView.findNavController().navigate(R.id.nav_insurance_policy, bundle)
        }
    }

    // return the number of the items in the list 
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text 
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.insurance_policies_card_image)
        val startDt: TextView = itemView.findViewById(R.id.insurance_policies_card_date)
        val endDt: TextView = itemView.findViewById(R.id.insurance_policies_card_end_date)
        val policyPrice: TextView = itemView.findViewById(R.id.insurance_policies_card_price)
        val policyFrequency: TextView = itemView.findViewById(R.id.insurance_policies_card_frequency)
    }
}
