package com.example.motologr.ui.motorcycle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.motologr.databinding.FragmentMotorcycleBinding

class MotorcycleFragment : Fragment() {

    private var _binding: FragmentMotorcycleBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(MotorcycleViewModel::class.java)

        _binding = FragmentMotorcycleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textMotorcycle
        galleryViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}