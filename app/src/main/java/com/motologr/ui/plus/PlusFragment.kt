package com.motologr.ui.plus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.motologr.R
import com.motologr.databinding.FragmentPlusBinding
import com.motologr.ui.data.DataManager
import com.motologr.ui.data.Vehicle
import java.text.SimpleDateFormat
import java.util.Date

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
        val plusViewModel =
            ViewModelProvider(this).get(PlusViewModel::class.java)

        _binding = FragmentPlusBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textNewVehicle: TextView = binding.textPlus
        val textModel: TextView = binding.textModelPrompt
        val editTextModel: EditText = binding.editTextModelInput
        val textYear: TextView = binding.textYearPrompt
        val editTextYear: EditText = binding.editTextYearInput
        val textLastWOF: TextView = binding.textWofLastPrompt
        val editTextLastWOF: EditText = binding.editTextWofLastInput
        val textCurrentReg: TextView = binding.textRegExpirePrompt
        val editTextCurrentReg: EditText = binding.editTextRegExpireInput
        val textCurrentOdo: TextView = binding.textOdoPrompt
        val editTextCurrentOdo: EditText = binding.editTextOdoInput

        plusViewModel.textNewVehicle.observe(viewLifecycleOwner) {
            textNewVehicle.text = it
        }
        plusViewModel.textModel.observe(viewLifecycleOwner) {
            textModel.text = it
        }
        plusViewModel.editTextModel.observe(viewLifecycleOwner) {
            editTextModel.hint = it
        }
        plusViewModel.textYear.observe(viewLifecycleOwner) {
            textYear.text = it
        }
        plusViewModel.editTextYear.observe(viewLifecycleOwner) {
            editTextYear.hint = it
        }
        plusViewModel.textLastWOF.observe(viewLifecycleOwner) {
            textLastWOF.text = it
        }
        plusViewModel.editTextLastWOF.observe(viewLifecycleOwner) {
            editTextLastWOF.hint = it
        }
        plusViewModel.textCurrentReg.observe(viewLifecycleOwner) {
            textCurrentReg.text = it
        }
        plusViewModel.editTextCurrentReg.observe(viewLifecycleOwner) {
            editTextCurrentReg.hint = it
        }
        plusViewModel.textCurrOdo.observe(viewLifecycleOwner) {
            textCurrentOdo.text = it
        }
        plusViewModel.editTextCurrOdo.observe(viewLifecycleOwner) {
            editTextCurrentOdo.hint = it
        }

        val button: View = binding.buttonConfirm
        button.setOnClickListener() {

            // Logic to check inputs here

            val format: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

            val modelName: String = binding.editTextModelInput.text.toString()
            val modelYear: Int = Integer.parseInt(binding.editTextYearInput.text.toString())
            val modelWOF: Date = format.parse(binding.editTextWofLastInput.text.toString())
            val modelReg: Date = format.parse(binding.editTextRegExpireInput.text.toString())
            val odometer: Int = Integer.parseInt(binding.editTextOdoInput.text.toString())

            val vehicle = Vehicle(modelName, modelYear, modelWOF, modelReg, odometer)

            DataManager.CreateNewVehicle(vehicle)

            findNavController().navigate(R.id.action_nav_plus_to_nav_vehicle_1)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}