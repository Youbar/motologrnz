package com.motologr.ui.expenses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ExpensesViewModel : ViewModel() {

    private val _textExpensesTitle = MutableLiveData<String>().apply {
        value = "Expenses FY 23-24"
    }
    private val _textExpensesRepairs = MutableLiveData<String>().apply {
        value = "Repairs:"
    }
    private val _textExpensesRepairsValue = MutableLiveData<String>().apply {
        value = ""
    }
    private val _textExpensesServices = MutableLiveData<String>().apply {
        value = "Services:"
    }
    private val _textExpensesServicesValue = MutableLiveData<String>().apply {
        value = ""
    }
    private val _textExpensesFuel = MutableLiveData<String>().apply {
        value = "Fuel:"
    }
    private val _textExpensesFuelValue = MutableLiveData<String>().apply {
        value = ""
    }
    private val _textExpensesReg = MutableLiveData<String>().apply {
        value = "Reg:"
    }
    private val _textExpensesRegValue = MutableLiveData<String>().apply {
        value = ""
    }
    private val _textExpensesWOF = MutableLiveData<String>().apply {
        value = "WOF:"
    }
    private val _textExpensesWOFValue = MutableLiveData<String>().apply {
        value = ""
    }

    val textExpensesTitle: LiveData<String> = _textExpensesTitle
    val textExpensesRepairs: LiveData<String> = _textExpensesRepairs
    val textExpensesRepairsValue: LiveData<String> = _textExpensesRepairsValue
    val textExpensesServices: LiveData<String> = _textExpensesServices
    val textExpensesServicesValue: LiveData<String> = _textExpensesServicesValue
    val textExpensesFuel: LiveData<String> = _textExpensesFuel
    val textExpensesFuelValue: LiveData<String> = _textExpensesFuelValue
    val textExpensesReg: LiveData<String> = _textExpensesReg
    val textExpensesRegValue: LiveData<String> = _textExpensesRegValue
    val textExpensesWOF: LiveData<String> = _textExpensesWOF
    val textExpensesWOFValue: LiveData<String> = _textExpensesWOFValue
}