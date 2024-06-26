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
import com.motologr.ui.data.DataManager
import com.motologr.ui.data.logging.Loggable
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

        val expensesList = calculateExpensesForFinancialYear()
        setFragmentValues(expensesList, expensesViewModel)

        return root
    }

    private fun setFragmentValues(expensesList: ArrayList<Double>, expensesViewModel: ExpensesViewModel) {
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
        val textExpensesTotal: TextView = binding.textExpensesTotal
        val textExpensesTotalValue: TextView = binding.textExpensesTotalValue
        val buttonExpensesOK: Button = binding.buttonExpensesOk
        val buttonExpensesExport: Button = binding.buttonExpensesExport

        // Repair = 0, Service = 1, WOF = 2, Reg = 3, Fuel = 4, Total = 5
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
        expensesViewModel.textExpensesTotal.observe(viewLifecycleOwner) {
            textExpensesTotal.text = it
        }
        expensesViewModel.textExpensesTotalValue.observe(viewLifecycleOwner) {
            textExpensesTotalValue.text = expensesList[5].toString()
        }
        expensesViewModel.buttonExpensesOK.observe(viewLifecycleOwner) {
            buttonExpensesOK.text = it
        }
        expensesViewModel.buttonExpensesExport.observe(viewLifecycleOwner) {
            buttonExpensesExport.text = it
        }
    }

    private fun isWithinFinancialYear(loggableDate : Date) : Boolean {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        var maxDate: Date
        var minDate: Date

        val format: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

        if (month < 3) {
            maxDate = format.parse("01/04/" + year)
            minDate = format.parse("31/03/" + (year - 1))
        } else {
            maxDate = format.parse("01/04/" + (year + 1))
            minDate = format.parse("31/03/" + year)
        }

        return loggableDate.before(maxDate) and loggableDate.after(minDate)
    }

    private fun calculateExpensesForFinancialYear() : ArrayList<Double> {
        var expensesLogs : ArrayList<Loggable> = DataManager.ReturnActiveVehicle()!!.returnExpensesLogs()

        // Need to add a filter to only take YTD values
        expensesLogs = ArrayList(expensesLogs.filter { loggable -> isWithinFinancialYear(loggable.sortableDate) })

        // Repair = 0, Service = 1, WOF = 2, Reg = 3, Fuel = 100
        val repairsLogs = expensesLogs.filter { loggable -> loggable.classId == 0 }
        val servicesLogs = expensesLogs.filter { loggable -> loggable.classId == 1 }
        val wofLogs = expensesLogs.filter { loggable -> loggable.classId == 2 }
        val regLogs = expensesLogs.filter { loggable -> loggable.classId == 3 }
        val fuelLogs = expensesLogs.filter { loggable -> loggable.classId == 100 }

        var repairCost = calculateTotalExpenseForLoggables(ArrayList(repairsLogs));
        var serviceCost = calculateTotalExpenseForLoggables(ArrayList(servicesLogs));
        var wofCost = calculateTotalExpenseForLoggables(ArrayList(wofLogs));
        var regCost = calculateTotalExpenseForLoggables(ArrayList(regLogs));
        var fuelCost = calculateTotalExpenseForLoggables(ArrayList(fuelLogs));

        var total = calculateTotalExpenseForLoggables(expensesLogs);


        return arrayListOf(repairCost, serviceCost, wofCost, regCost, fuelCost, total)
    }

    private fun returnFinancialYear() : String {

        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)

        val format: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

        if (month < 3) {
            return calendar.get(Calendar.YEAR).toString().takeLast(2)
        } else {
            return (calendar.get(Calendar.YEAR) + 1).toString().takeLast(2)
        }
    }

    private fun calculateTotalExpenseForLoggables(expensesLogs : ArrayList<Loggable>) : Double {
        var total : Double = 0.0
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