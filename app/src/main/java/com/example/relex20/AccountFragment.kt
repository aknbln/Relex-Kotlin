package com.example.relex20

import android.app.ProgressDialog.show
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.relex20.databinding.FragmentAccountBinding
import com.example.relex20.databinding.FragmentMapsBinding
import com.example.relex20.model.TransactionViewModel
import kotlin.math.roundToInt


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var auth : FirebaseAuth
    private val sharedViewModel: TransactionViewModel by activityViewModels()
    private var _binding: FragmentAccountBinding? = null
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
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = sharedViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        // -----SIGN OUT------
        val signOutButton : Button = view.findViewById<Button>(R.id.signOutBtn)
        signOutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(activity, MainActivity::class.java))
        }

        // -----REQUEST COMPENSATION-----
        val compensationButton : Button = view.findViewById<Button>(R.id.transferBtn)
        compensationButton.setOnClickListener {
            var currTotal = sharedViewModel._total.value
            // Round to two decimals
            if (currTotal != null)
                currTotal = (currTotal.times(100.0)).roundToInt() / 100.0

            // Show message center of screen
            var toast : Toast = Toast.makeText(activity, "Request Submitted for $currTotal", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER, 0, -10)
            toast.show()

            // Reset all variables in recyclerview
            sharedViewModel.resetOrder()
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}