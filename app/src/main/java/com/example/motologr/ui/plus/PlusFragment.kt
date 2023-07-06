package com.example.motologr.ui.plus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.motologr.databinding.FragmentPlusBinding

class PlusFragment : Fragment() {

    private var _binding: FragmentPlusBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(PlusViewModel::class.java)

        _binding = FragmentPlusBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textPlus
        slideshowViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val inputRegDue: View = binding.editTextRegExpireInput

        val button: View = binding.buttonConfirm
        button.setOnClickListener() {
            textView.text = "WOW!"
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}