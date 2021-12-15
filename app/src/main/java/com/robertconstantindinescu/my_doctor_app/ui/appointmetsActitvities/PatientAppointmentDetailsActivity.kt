package com.robertconstantindinescu.my_doctor_app.ui.appointmetsActitvities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.navigation.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.adapters.viewPagerAdapters.ViewPagerAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.ActivityPatientAppointmentDetailsBinding
import com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.doctorFragments.appointmentDetailsFragments.DoctorAppointmentNotes
import com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.doctorFragments.appointmentDetailsFragments.PatientAppointmentDetailsFragment
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.PENDING_DOCTOR_APPOINTMENT_MODEL
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_patient_appointment_details.*

@AndroidEntryPoint
class PatientAppointmentDetailsActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityPatientAppointmentDetailsBinding
    private val args by navArgs<PatientAppointmentDetailsActivityArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPatientAppointmentDetailsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val resultBundle = Bundle()
        resultBundle.putParcelable(PENDING_DOCTOR_APPOINTMENT_MODEL, args.pendingDoctorModel)

        //get acces to viewPager
        val viewPager = mBinding.viewPager
        val adapter = ViewPagerAdapter(resultBundle, supportFragmentManager, lifecycle)

        adapter.addFragment(
            PatientAppointmentDetailsFragment(),
            resources.getString(R.string.viewPager_patientDetails_title)
        )
        adapter.addFragment(
            DoctorAppointmentNotes(),
            resources.getString(R.string.viewPager_doctorNotes_title)
        )

        viewPager.adapter = adapter
        TabLayoutMediator(mBinding.tabLayout, viewPager){ tab, position ->
            tab.text = adapter.getPageTitle(position)

        }.attach()



    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()


        return super.onOptionsItemSelected(item)


    }
}