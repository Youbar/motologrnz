package com.motologr.ui.expenses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.motologr.databinding.FragmentExpensesBinding

class ExpensesFragment : Fragment() {

    private var _binding: FragmentExpensesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val expensesViewModel =
            ViewModelProvider(this).get(ExpensesViewModel::class.java)

        _binding = FragmentExpensesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textExpensesTitle: TextView = binding.textExpensesTitle
        val textExpensesRepairs: TextView = binding.textExpensesRepairs
        val textExpensesRepairsValue: TextView = binding.textExpensesRepairsValue
        val textExpensesServices: TextView = binding.textExpensesServices
        val textExpensesServicesValue: TextView = binding.textExpensesServicesValue
        val textExpensesFuel: TextView = binding.textExpensesFuel
        val textExpensesFuelValue: TextView = binding.textExpensesFuelValue
        val textExpensesReg: TextView = binding.textExpensesReg
        val textExpensesRegValue: TextView = binding.textExpensesRegValue
        val textExpensesWOF: TextView = binding.textExpensesWof
        val textExpensesWOFValue: TextView = binding.textExpensesWofValue

        expensesViewModel.textExpensesTitle.observe(viewLifecycleOwner) {
            textExpensesTitle.text = it
        }
        expensesViewModel.textExpensesRepairs.observe(viewLifecycleOwner) {
            textExpensesRepairs.text = it
        }
        expensesViewModel.textExpensesRepairsValue.observe(viewLifecycleOwner) {
            textExpensesRepairsValue.text = it
        }
        expensesViewModel.textExpensesServices.observe(viewLifecycleOwner) {
            textExpensesServices.text = it
        }
        expensesViewModel.textExpensesServicesValue.observe(viewLifecycleOwner) {
            textExpensesServicesValue.text = it
        }
        expensesViewModel.textExpensesFuel.observe(viewLifecycleOwner) {
            textExpensesFuel.text = it
        }
        expensesViewModel.textExpensesFuelValue.observe(viewLifecycleOwner) {
            textExpensesFuelValue.text = it
        }
        expensesViewModel.textExpensesReg.observe(viewLifecycleOwner) {
            textExpensesReg.text = it
        }
        expensesViewModel.textExpensesRegValue.observe(viewLifecycleOwner) {
            textExpensesRegValue.text = it
        }
        expensesViewModel.textExpensesWOF.observe(viewLifecycleOwner) {
            textExpensesWOF.text = it
        }
        expensesViewModel.textExpensesWOFValue.observe(viewLifecycleOwner) {
            textExpensesWOFValue.text = it
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}