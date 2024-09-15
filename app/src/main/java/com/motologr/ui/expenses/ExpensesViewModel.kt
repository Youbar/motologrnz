package com.motologr.ui.expenses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.motologr.data.DataManager
import com.motologr.data.logging.Loggable
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Calendar

class ExpensesViewModel : ViewModel() {

    //region Properties

    private val _textExpensesTitle = MutableLiveData<String>().apply {
        value = returnFinancialYear()
    }

    val textExpensesTitle: LiveData<String> = _textExpensesTitle

    private val _textExpensesRepairs = "Repairs"

    val textExpensesRepairs: String = _textExpensesRepairs

    private val _textExpensesRepairsValue = MutableLiveData<String>().apply {
        value = formatExpense(repairCost)
    }

    val textExpensesRepairsValue: LiveData<String> = _textExpensesRepairsValue

    private val _textExpensesServices = "Services:"

    val textExpensesServices: String = _textExpensesServices

    private val _textExpensesServicesValue = MutableLiveData<String>().apply {
        value = formatExpense(serviceCost)
    }

    val textExpensesServicesValue: LiveData<String> = _textExpensesServicesValue

    private val _textExpensesFuel = "Fuel:"

    val textExpensesFuel: String = _textExpensesFuel

    private val _textExpensesFuelValue = MutableLiveData<String>().apply {
        value = formatExpense(fuelCost)
    }

    val textExpensesFuelValue: LiveData<String> = _textExpensesFuelValue

    private val _textExpensesReg = "Reg:"

    val textExpensesReg: String = _textExpensesReg

    private val _textExpensesRegValue = MutableLiveData<String>().apply {
        value = formatExpense(regCost)
    }

    val textExpensesRegValue: LiveData<String> = _textExpensesRegValue

    private val _textExpensesRuc = "RUC:"

    val textExpensesRuc: String = _textExpensesRuc

    private val _textExpensesRucValue = MutableLiveData<String>().apply {
        value = formatExpense(rucCost)
    }

    val textExpensesRucValue: LiveData<String> = _textExpensesRucValue


    private val _textExpensesWOF = "WOF:"

    val textExpensesWOF: String = _textExpensesWOF

    private val _textExpensesWOFValue = MutableLiveData<String>().apply {
        value = formatExpense(wofCost)
    }

    val textExpensesWOFValue: LiveData<String> = _textExpensesWOFValue

    private val _textExpensesInsurance = "Insurance:"

    val textExpensesInsurance: String = _textExpensesInsurance

    private val _textExpensesInsuranceValue = MutableLiveData<String>().apply {
        value = formatExpense(insuranceCost)
    }

    val textExpensesInsuranceValue: LiveData<String> = _textExpensesInsuranceValue

    private val _textExpensesTotal = "Total:"

    val textExpensesTotal: String = _textExpensesTotal

    private val _textExpensesTotalValue = MutableLiveData<String>().apply {
        value = formatExpense(totalCost)
    }

    val textExpensesTotalValue: LiveData<String> = _textExpensesTotalValue

    private val _buttonExpensesExport = "Export"

    val buttonExpensesExport: String = _buttonExpensesExport

    //endregion

    private fun returnFinancialYear() : String {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)

        val financialYearString = "Expenses for FY"

        return if (month < 3) {
            financialYearString + calendar.get(Calendar.YEAR).toString().takeLast(2)
        } else {
            financialYearString + (calendar.get(Calendar.YEAR) + 1).toString().takeLast(2)
        }
    }

    private lateinit var expensesLogs : List<Loggable>
    private lateinit var repairLogs : List<Loggable>
    private lateinit var serviceLogs : List<Loggable>
    private lateinit var wofLogs : List<Loggable>
    private lateinit var regLogs : List<Loggable>
    private lateinit var rucLogs : List<Loggable>
    private lateinit var fuelLogs : List<Loggable>
    private lateinit var insuranceLogs : List<Loggable>

    private val repairCost : BigDecimal
        get() {
            return if (this::repairLogs.isInitialized)
                calculateTotalExpenseForLoggables(repairLogs)
            else
                BigDecimal(0)
        }

    private val serviceCost : BigDecimal
        get() {
            return if (this::serviceLogs.isInitialized)
                calculateTotalExpenseForLoggables(serviceLogs)
            else
                BigDecimal(0)
        }

    private val wofCost : BigDecimal
        get() {
            return if (this::wofLogs.isInitialized)
                calculateTotalExpenseForLoggables(wofLogs)
            else
                BigDecimal(0)
        }

    private val regCost : BigDecimal
        get() {
            return if (this::regLogs.isInitialized)
                calculateTotalExpenseForLoggables(regLogs)
            else
                BigDecimal(0)
        }

    private val rucCost : BigDecimal
        get() {
            return if (this::rucLogs.isInitialized)
                calculateTotalExpenseForLoggables(rucLogs)
            else
                BigDecimal(0)
        }

    private val fuelCost : BigDecimal
        get() {
            return if (this::fuelLogs.isInitialized)
                calculateTotalExpenseForLoggables(fuelLogs)
            else
                BigDecimal(0)
        }

    private val insuranceCost : BigDecimal
        get() {
            return if (this::insuranceLogs.isInitialized)
                calculateTotalExpenseForLoggables(insuranceLogs)
            else
                BigDecimal(0)
        }

    private val totalCost : BigDecimal
        get() {
            return if (this::expensesLogs.isInitialized)
                calculateTotalExpenseForLoggables(expensesLogs)
            else
                BigDecimal(0)
        }

    init {
        calculateExpensesForFinancialYear()
    }

    private fun calculateExpensesForFinancialYear(){
        expensesLogs = DataManager.returnActiveVehicle()!!.returnExpensesLogsWithinFinancialYear()

        // Repair = 0, Service = 1, WOF = 2, Reg = 3, Ruc = 4, Fuel = 100, BillLog = 201
        repairLogs = expensesLogs.filter { loggable -> loggable.classId == 0 }
        serviceLogs = expensesLogs.filter { loggable -> loggable.classId == 1 }
        wofLogs = expensesLogs.filter { loggable -> loggable.classId == 2 }
        regLogs = expensesLogs.filter { loggable -> loggable.classId == 3 }
        rucLogs = expensesLogs.filter { loggable -> loggable.classId == 4 }
        fuelLogs = expensesLogs.filter { loggable -> loggable.classId == 100 }
        insuranceLogs = expensesLogs.filter { loggable -> loggable.classId == 201}

        _textExpensesRepairsValue.value = formatExpense(repairCost)
        _textExpensesServicesValue.value = formatExpense(serviceCost)
        _textExpensesWOFValue.value = formatExpense(wofCost)
        _textExpensesRegValue.value = formatExpense(regCost)
        _textExpensesRucValue.value = formatExpense(rucCost)
        _textExpensesFuelValue.value = formatExpense(fuelCost)
        _textExpensesInsuranceValue.value = formatExpense(insuranceCost)
        _textExpensesTotalValue.value = formatExpense(totalCost)
    }

    private fun calculateTotalExpenseForLoggables(expensesLogs : List<Loggable>) : BigDecimal {
        var total : BigDecimal = 0.0.toBigDecimal()
        for (expenseLog in expensesLogs) {
            total += expenseLog.unitPrice
        }

        return total
    }

    private fun formatExpense(expense: BigDecimal) : String {
        val df = DecimalFormat("0.00")
        df.roundingMode = RoundingMode.HALF_EVEN

        return "$" + df.format(expense).toString()
    }

    fun getExpensesLogs(): List<Loggable> {
        return expensesLogs
    }
}