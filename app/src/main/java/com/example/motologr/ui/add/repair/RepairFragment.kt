package com.example.motologr.ui.add.repair

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.motologr.R

class RepairFragment : Fragment() {

    companion object {
        fun newInstance() = RepairFragment()
    }

    private lateinit var viewModel: RepairViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_repair, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RepairViewModel::class.java)
        // TODO: Use the ViewModel
    }

}