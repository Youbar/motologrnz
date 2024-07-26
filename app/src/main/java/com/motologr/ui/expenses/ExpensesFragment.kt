package com.motologr.ui.expenses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.motologr.databinding.FragmentExpensesBinding
import com.motologr.data.DataManager
import com.motologr.data.logging.Loggable
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

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

        DataManager.updateTitle(activity, "Expenses for FY25")

        val expensesList = calculateExpensesForFinancialYear()
        setFragmentValues(expensesList, expensesViewModel)

        return root
    }

    private fun setFragmentValues(expensesList: ArrayList<BigDecimal>, expensesViewModel: ExpensesViewModel) {
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
        val textExpensesInsurance: TextView = binding.textExpensesInsurance
        val textExpensesInsuranceValue: TextView = binding.textExpensesInsuranceValue
        val textExpensesTotal: TextView = binding.textExpensesTotal
        val textExpensesTotalValue: TextView = binding.textExpensesTotalValue
        val buttonExpensesOK: Button = binding.buttonExpensesOk
        val buttonExpensesExport: Button = binding.buttonExpensesExport

        // Repair = 0, Service = 1, WOF = 2, Reg = 3, Fuel = 4, Insurance = 5, Total = 6
        expensesViewModel.textExpensesTitle.observe(viewLifecycleOwner) {
            textExpensesTitle.text = it + returnFinancialYear()
        }
        expensesViewModel.textExpensesRepairs.observe(viewLifecycleOwner) {
            textExpensesRepairs.text = it
        }
        expensesViewModel.textExpensesRepairsValue.observe(viewLifecycleOwner) {
            textExpensesRepairsValue.text = expensesList[0].toString()
        }
        expensesViewModel.textExpensesServices.observe(viewLifecycleOwner) {
            textExpensesServices.text = it
        }
        expensesViewModel.textExpensesServicesValue.observe(viewLifecycleOwner) {
            textExpensesServicesValue.text = expensesList[1].toString()
        }
        expensesViewModel.textExpensesFuel.observe(viewLifecycleOwner) {
            textExpensesFuel.text = it
        }
        expensesViewModel.textExpensesFuelValue.observe(viewLifecycleOwner) {
            textExpensesFuelValue.text = expensesList[4].toString()
        }
        expensesViewModel.textExpensesReg.observe(viewLifecycleOwner) {
            textExpensesReg.text = it
        }
        expensesViewModel.textExpensesRegValue.observe(viewLifecycleOwner) {
            textExpensesRegValue.text = expensesList[3].toString()
        }
        expensesViewModel.textExpensesWOF.observe(viewLifecycleOwner) {
            textExpensesWOF.text = it
        }
        expensesViewModel.textExpensesWOFValue.observe(viewLifecycleOwner) {
            textExpensesWOFValue.text = expensesList[2].toString()
        }
        expensesViewModel.textExpensesInsurance.observe(viewLifecycleOwner) {
            textExpensesInsurance.text = it
        }
        expensesViewModel.textExpensesInsuranceValue.observe(viewLifecycleOwner) {
            textExpensesInsuranceValue.text = expensesList[5].toString()
        }
        expensesViewModel.textExpensesTotal.observe(viewLifecycleOwner) {
            textExpensesTotal.text = it
        }
        expensesViewModel.textExpensesTotalValue.observe(viewLifecycleOwner) {
            textExpensesTotalValue.text = expensesList[6].toString()
        }
        expensesViewModel.buttonExpensesOK.observe(viewLifecycleOwner) {
            buttonExpensesOK.text = it
        }
        expensesViewModel.buttonExpensesExport.observe(viewLifecycleOwner) {
            buttonExpensesExport.text = it
        }
    }

    private fun calculateExpensesForFinancialYear() : ArrayList<BigDecimal> {
        val expensesLogs : ArrayList<Loggable> = DataManager.ReturnActiveVehicle()!!.returnExpensesLogsWithinFinancialYear()

        // Repair = 0, Service = 1, WOF = 2, Reg = 3, Fuel = 100
        val repairsLogs = expensesLogs.filter { loggable -> loggable.classId == 0 }
        val servicesLogs = expensesLogs.filter { loggable -> loggable.classId == 1 }
        val wofLogs = expensesLogs.filter { loggable -> loggable.classId == 2 }
        val regLogs = expensesLogs.filter { loggable -> loggable.classId == 3 }
        val fuelLogs = expensesLogs.filter { loggable -> loggable.classId == 100 }
        val insuranceLogs = expensesLogs.filter { loggable -> loggable.classId == 201}

        var repairCost = calculateTotalExpenseForLoggables(ArrayList(repairsLogs));
        var serviceCost = calculateTotalExpenseForLoggables(ArrayList(servicesLogs));
        var wofCost = calculateTotalExpenseForLoggables(ArrayList(wofLogs));
        var regCost = calculateTotalExpenseForLoggables(ArrayList(regLogs));
        var fuelCost = calculateTotalExpenseForLoggables(ArrayList(fuelLogs));
        var insuranceCost = calculateTotalExpenseForLoggables(ArrayList(insuranceLogs));

        var total = calculateTotalExpenseForLoggables(expensesLogs);


        return arrayListOf(repairCost, serviceCost, wofCost, regCost, fuelCost, insuranceCost, total)
    }

    private fun returnFinancialYear() : String {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)

        return if (month < 3) {
            calendar.get(Calendar.YEAR).toString().takeLast(2)
        } else {
            (calendar.get(Calendar.YEAR) + 1).toString().takeLast(2)
        }
    }

    private fun calculateTotalExpenseForLoggables(expensesLogs : ArrayList<Loggable>) : BigDecimal {
        var total : BigDecimal = 0.0.toBigDecimal()
        for (expenseLog in expensesLogs) {
            total += expenseLog.unitPrice;
        }

        return total
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}