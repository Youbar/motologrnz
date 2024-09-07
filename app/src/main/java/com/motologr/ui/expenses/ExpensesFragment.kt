package com.motologr.ui.expenses

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.motologr.databinding.FragmentExpensesBinding
import com.motologr.data.DataManager

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
            ViewModelProvider(this)[ExpensesViewModel::class.java]

        _binding = FragmentExpensesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initViewModel(expensesViewModel)
        DataManager.updateTitle(activity, expensesViewModel.textExpensesTitle.value.toString())

        binding.buttonExpensesExport.setOnClickListener {
            // At SDK 29 and above, writing PDF to external storage is automatically given
            var permissionGranted = true

            if (android.os.Build.VERSION.SDK_INT < 29) {
                permissionGranted = ContextCompat
                    .checkSelfPermission(requireContext(), WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            }

            if (!permissionGranted)
                createExternalStorageDialog()
            else {
                val generatePDF = GeneratePDF(requireContext(), expensesViewModel.getExpensesLogs())
                generatePDF.generatePDF()
            }
        }

        return root
    }

    private fun createExternalStorageDialog() {
        MaterialAlertDialogBuilder(requireContext()).setTitle("Storage Access Required")
            .setMessage("In order to export your expenses data, access is required to external storage")
            .setNeutralButton("Cancel") { _, _ ->

            }
            .setNegativeButton("Decline") { _, _ ->
                Toast.makeText(activity, "External storage access required to export data", Toast.LENGTH_LONG).show()
            }
            .setPositiveButton("Accept") { _, _ ->
                requestPermission.launch(WRITE_EXTERNAL_STORAGE)
            }
            .show()
    }

    var requestPermission = registerForActivityResult(RequestPermission()) {
            isGranted ->
        // Do something if permission granted
        if (isGranted) {
            Log.i("DEBUG", "permission granted")
        } else {
            Log.i("DEBUG", "permission denied")
        }
    }

    private fun initViewModel(expensesViewModel: ExpensesViewModel) {
        expensesViewModel.textExpensesTitle.observe(viewLifecycleOwner) {
            binding.textExpensesTitle.text = it
        }

        binding.textExpensesRepairs.text = expensesViewModel.textExpensesRepairs

        expensesViewModel.textExpensesRepairsValue.observe(viewLifecycleOwner) {
            binding.textExpensesRepairsValue.text = it
        }

        binding.textExpensesServices.text = expensesViewModel.textExpensesServices

        expensesViewModel.textExpensesServicesValue.observe(viewLifecycleOwner) {
            binding.textExpensesServicesValue.text = it
        }
        binding.textExpensesFuel.text = expensesViewModel.textExpensesFuel

        expensesViewModel.textExpensesFuelValue.observe(viewLifecycleOwner) {
            binding.textExpensesFuelValue.text = it
        }

        binding.textExpensesReg.text = expensesViewModel.textExpensesReg

        expensesViewModel.textExpensesRegValue.observe(viewLifecycleOwner) {
            binding.textExpensesRegValue.text = it
        }

        binding.textExpensesWof.text = expensesViewModel.textExpensesWOF

        expensesViewModel.textExpensesWOFValue.observe(viewLifecycleOwner) {
            binding.textExpensesWofValue.text = it
        }

        binding.textExpensesInsurance.text = expensesViewModel.textExpensesInsurance

        expensesViewModel.textExpensesInsuranceValue.observe(viewLifecycleOwner) {
            binding.textExpensesInsuranceValue.text = it
        }

        binding.textExpensesTotal.text = expensesViewModel.textExpensesTotal

        expensesViewModel.textExpensesTotalValue.observe(viewLifecycleOwner) {
            binding.textExpensesTotalValue.text = it
        }

        binding.buttonExpensesExport.text = expensesViewModel.buttonExpensesExport
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}