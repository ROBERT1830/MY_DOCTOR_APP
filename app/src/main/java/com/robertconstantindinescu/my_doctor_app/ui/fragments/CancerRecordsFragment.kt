package com.robertconstantindinescu.my_doctor_app.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.adapters.CancerRecordsAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentCancerRecordsBinding
import com.robertconstantindinescu.my_doctor_app.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CancerRecordsFragment : Fragment() {
//    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null

    //private lateinit var mBinding: FragmentCancerRecordsBinding


    private val mainViewModel: MainViewModel by viewModels()
    private val mAdapter: CancerRecordsAdapter by lazy {
        CancerRecordsAdapter(
            requireActivity(),
            mainViewModel
        )
    }

    private var _binding: FragmentCancerRecordsBinding? = null
    private val binding get () = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //mBinding = FragmentCancerRecordsBinding.inflate(inflater, container, false)

        _binding = FragmentCancerRecordsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel
        binding.mAdapter = mAdapter

        setHasOptionsMenu(true)
        setUpRecyclerView(binding.recyclerViewCancerData)

        binding.newDetectionFab.setOnClickListener {
            //navegamos al detector activity
            findNavController().navigate(R.id.action_cancerRecordsFragment_to_detectorActivity)
        }

        return binding.root
    }

    private fun setUpRecyclerView(recyclerViewCancerData: RecyclerView) {
        recyclerViewCancerData.adapter = mAdapter
        recyclerViewCancerData.layoutManager = LinearLayoutManager(requireContext())
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.cancer_records_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.delete_all_cancer_records){
            mainViewModel.deleteAllCancerRecords()
            showSnackbar()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showSnackbar(){
        Snackbar.make(
            binding.root,
            "All recipes removed",
            Snackbar.LENGTH_SHORT
        ).setAction("Ok"){}.show()
    }


//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment UserCancerDataFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            CancerRecordsFragment().apply {
//                arguments = Bundle().apply {
//
//                }
//            }
//    }
}