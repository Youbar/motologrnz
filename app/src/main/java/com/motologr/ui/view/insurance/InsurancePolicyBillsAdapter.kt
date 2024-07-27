package com.motologr.ui.view.insurance
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.motologr.R

class InsurancePolicyBillsAdapter(private val mList: List<InsurancePolicyBillsViewModel>) : RecyclerView.Adapter<InsurancePolicyBillsAdapter.ViewHolder>() {

    // create new views 
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view  
        // that is used to hold list item 
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_insurance_bill, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view 
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        // sets the image to the imageview from our itemHolder class 
        holder.imageView.setImageResource(ItemsViewModel.image)

        // sets the text to the textview from our itemHolder class 
        holder.billDt.text = ItemsViewModel.billingDt
        holder.billPrice.text = ItemsViewModel.policyPrice

        if (ItemsViewModel.paid) {
            holder.billPaid.setImageResource(R.drawable.ic_log_check_16)
            holder.billPaid.isVisible = true
        }
        else
            holder.billPaid.isVisible = false

/*        holder.itemView.setOnClickListener { v ->
            val bundle: Bundle = Bundle()
            bundle.putInt("position", position)

            holder.itemView.findNavController().navigate(R.id.nav_fuel, bundle)
        }*/
    }

    // return the number of the items in the list 
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text 
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.insurance_bill_card_image)
        val billDt: TextView = itemView.findViewById(R.id.insurance_bill_card_date)
        val billPrice: TextView = itemView.findViewById(R.id.insurance_bill_card_price)
        val billPaid: ImageView = itemView.findViewById(R.id.insurance_bill_card_image_paid)
    }
}
