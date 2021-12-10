package com.robertconstantindinescu.my_doctor_app.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.robertconstantindinescu.my_doctor_app.adapters.RequestAppointmentAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.ActivityRequestAppointmentBinding
import com.robertconstantindinescu.my_doctor_app.databinding.ToolbarLayoutBinding
import com.robertconstantindinescu.my_doctor_app.utils.DatePicker
import com.robertconstantindinescu.my_doctor_app.utils.TimePicker
import com.robertconstantindinescu.my_doctor_app.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_request_appointment.*

@AndroidEntryPoint
class RequestAppointmentActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityRequestAppointmentBinding
    private val args by navArgs<RequestAppointmentActivityArgs>()

    private val mainViewModel: MainViewModel by viewModels()

    private val mAdapter: RequestAppointmentAdapter by lazy {
        RequestAppointmentAdapter(this@RequestAppointmentActivity, mainViewModel)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRequestAppointmentBinding.inflate(layoutInflater)
        setContentView(mBinding.root)


        mBinding.lifecycleOwner = this
        mBinding.mainViewModel = mainViewModel
        mBinding.mAdapter = mAdapter
        setUpRecyclerView(mBinding.recyclerViewCancerData)

        with(mBinding){
            btnBack.setOnClickListener { onBackPressed() }
            editTextDate.setOnClickListener {
                showDatePickerDialog()
            }
            editTextTime.setOnClickListener {
                showTimePickerDialog()
            }
        }






        with(mBinding) {
            img_doctor.load(args.doctorModel.image)
            txtView_doctorName.text = args.doctorModel.name.toString()
            txtView_doctorEmail.text = args.doctorModel.email.toString()
            txtView_doctorLiscence.text = args.doctorModel.doctorLiscence.toString()
        }


    }

    private fun showTimePickerDialog() {
        val timePicker = TimePicker { onTimeSelected(it) }
        timePicker.show(supportFragmentManager, "timePicker")
    }

    private fun onTimeSelected(time: String) {
        mBinding.editTextTime.setText(time)
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePicker { day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(supportFragmentManager, "datePicker")

    }

    private fun setUpRecyclerView(recyclerViewCancerData: RecyclerView) {
        recyclerViewCancerData.adapter = mAdapter
        recyclerViewCancerData.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    @SuppressLint("SetTextI18n")
    private fun onDateSelected(day: Int, month: Int, year: Int) {
        mBinding.editTextDate.setText("$day/$month/$year")

    }
}