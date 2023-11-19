package com.example.motologr.ui.logging.maint

import CustomAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.motologr.R
import com.example.motologr.databinding.FragmentMaintLoggingBinding
import com.example.motologr.ui.logging.ItemsViewModel

class MaintLoggingFragment : Fragment() {

    private var _binding: FragmentMaintLoggingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val maintLoggingViewModel =
            ViewModelProvider(this).get(MaintLoggingViewModel::class.java)

        _binding = FragmentMaintLoggingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // getting the recyclerview by its id
        val recyclerview = binding.recyclerViewMaintLogging

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(root.context)

        // ArrayList of class ItemsViewModel
        val data = ArrayList<ItemsViewModel>()

        // This loop will create 20 Views containing
        // the image with the count of view
        for (i in 1..20) {
            data.add(ItemsViewModel(R.drawable.ic_menu_arrow_16, "Item " + i))
        }

        // This will pass the ArrayList to our Adapter
        val adapter = CustomAdapter(data)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}