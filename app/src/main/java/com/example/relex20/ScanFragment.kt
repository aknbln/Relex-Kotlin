package com.example.relex20

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.relex20.databinding.FragmentScanBinding
import com.example.relex20.model.TransactionViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ScanFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScanFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val sharedViewModel: TransactionViewModel by activityViewModels()

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Connects fragment to view model
        binding.apply {
            viewModel = sharedViewModel //right click on viewModel to see in XML file
            lifecycleOwner = viewLifecycleOwner
        }

        // INPUT BOX POP UP
        binding.scan.setOnClickListener{
            showBottomSheet()
        }

        // CAMERA
        binding.camera.setOnClickListener{
            if (ContextCompat.checkSelfPermission(
                    requireContext(), /*Check this*/
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
            else {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            }
            Toast.makeText(context,
                "Clicked!", Toast.LENGTH_SHORT).show()
            // TODO: Need to upload picture somewhere? What did Johns say about us having to save pictures
        }
    }

    private fun showBottomSheet() {
        // Set up bottomSheet and view
        val bottomSheet = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate( R.layout.scanned_bottom_sheet, null)

        // MANUAL INPUT SUBMIT BUTTON
        val addExpenseBut: Button = view.findViewById<Button>(R.id.addExpense)
        addExpenseBut.setOnClickListener {
            val input = view.findViewById<EditText>(R.id.expenseName)

            // If text present, we allow data to update
            if (!TextUtils.isEmpty(input.text.toString())) {
                val expenseString: String = input.text.toString()

                // ADD TO VIEW MODEL TOTAL
                val expenseNum = expenseString.toDouble()
                println("ExpenseNum is $expenseNum------------")
                sharedViewModel.updateTotal(expenseNum);

                // notify user
                Toast.makeText(context, "Added: $expenseString", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Empty Expense", Toast.LENGTH_SHORT).show()
            }
        }

        // Show bottomSheet
        bottomSheet.setContentView(view)
        bottomSheet.show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ScanFragment.
         */
        private const val CAMERA_PERMISSION_CODE = 1
        private const val CAMERA_REQUEST_CODE = 2

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ScanFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    // Displays BottomSheet fragment with input box, allowing user to manually input transaction
    // Used in onViewCreated()

    /**
     * This fragment lifecycle method is called when the view hierarchy associated with the fragment
     * is being removed. As a result, clear out the binding object.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            }
            else {
                Toast.makeText(context,
                    "Need Permission!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                // paste image here
                val thumbNail: Bitmap = data!!.extras!!.get("data") as Bitmap
                binding.scannable.setImageBitmap(thumbNail);
            }
        }
    }
}