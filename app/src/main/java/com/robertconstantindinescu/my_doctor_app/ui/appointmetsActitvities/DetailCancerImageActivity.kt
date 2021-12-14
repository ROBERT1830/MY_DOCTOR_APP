package com.robertconstantindinescu.my_doctor_app.ui.appointmetsActitvities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import coil.load
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.databinding.ActivityDetailCancerImageBinding
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.CancerDataFirebaseModel
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.CANCER_DATA
import kotlinx.android.synthetic.main.activity_patient_appointment_details.*

class DetailCancerImageActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityDetailCancerImageBinding
    private lateinit var cancerDataFirebaseModel: CancerDataFirebaseModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityDetailCancerImageBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        intent.apply {
            cancerDataFirebaseModel = intent.getParcelableExtra<CancerDataFirebaseModel>(CANCER_DATA)!!

        }

        with(mBinding){
            imgViewCancerPhoto.load(cancerDataFirebaseModel.cancerImg)
            txtViewCancerResult.text = cancerDataFirebaseModel.outcome.toString()
            txtViewCancerDate.text = cancerDataFirebaseModel.date.toString()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()


        return super.onOptionsItemSelected(item)


    }
}