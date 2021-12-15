package com.robertconstantindinescu.my_doctor_app.ui.appointmetsActitvities

import android.annotation.SuppressLint
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters.RequestAppointmentAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.ActivityRequestAppointmentBinding
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.CancerDataEntity
import com.robertconstantindinescu.my_doctor_app.utils.DatePicker
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import com.robertconstantindinescu.my_doctor_app.utils.State
import com.robertconstantindinescu.my_doctor_app.utils.TimePicker
import com.robertconstantindinescu.my_doctor_app.viewmodels.MainViewModel
import com.robertconstantindinescu.my_doctor_app.viewmodels.RequestAppointmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_request_appointment.*
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class RequestAppointmentActivity : AppCompatActivity(),
    RequestAppointmentAdapter.OnItemClickListener {

    private lateinit var mBinding: ActivityRequestAppointmentBinding
    private val args by navArgs<RequestAppointmentActivityArgs>()
    private lateinit var loadingDialog: LoadingDialog

    private val mainViewModel: MainViewModel by viewModels()
    private val requestAppointmentViewModel: RequestAppointmentViewModel by viewModels()
    private var cancerDataList = mutableListOf<CancerDataEntity /*CancerDataFirebaseModel*/>()

    private val mAdapter: RequestAppointmentAdapter by lazy {
        RequestAppointmentAdapter(this@RequestAppointmentActivity, mainViewModel, this)

    }

    private lateinit var description: String
    private lateinit var date: String
    private lateinit var time: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRequestAppointmentBinding.inflate(layoutInflater)
        setContentView(mBinding.root)



        loadingDialog = LoadingDialog(this)
        mBinding.lifecycleOwner = this
        mBinding.mainViewModel = mainViewModel
        mBinding.mAdapter = mAdapter
        setUpRecyclerView(mBinding.recyclerViewCancerData)

        with(mBinding) {
            btnBack.setOnClickListener { onBackPressed() }
            editTextDate.setOnClickListener {
                showDatePickerDialog()
            }
            editTextTime.setOnClickListener {
                showTimePickerDialog()
            }
            buttonSendRequest.setOnClickListener {
                // TODO: 10/12/21 check for valid fields
                if (fieldsAreReady()) {
                    val alertDialog = AlertDialog.Builder(this@RequestAppointmentActivity)
                    alertDialog.setTitle("Confirmation")
                        .setMessage(
                            "Are you sure you want set an appointment with Dr.${args.doctorModel.name} on ${
                                editTextDate.text.trim()
                            } at ${editTextTime.text.trim()} ?"
                        )
                    alertDialog.setPositiveButton(
                        "Yes",
                        DialogInterface.OnClickListener { _, _ ->
                            lifecycleScope.launchWhenStarted {
                                requestAppointmentViewModel.createPendingDoctorPatientAppointment(
                                    args.doctorModel,
                                    cancerDataList, description!!, date!!, time!!
                                ).collect {
                                    when (it) {
                                        is State.Loading -> {
                                            if (it.flag == true) {
                                                loadingDialog.startLoading()
                                            }
                                        }
                                        is State.Succes -> {
                                            loadingDialog.stopLoading()
                                            // TODO: 11/12/21 here we can make a toast
                                            Snackbar.make(
                                                mBinding.root,
                                                it.data.toString(),
                                                Snackbar.LENGTH_LONG
                                            ).show()
                                            // TODO: 11/12/21 here we can use the scroll event in the snackbar so that it finishes go to the fargment by using onbackpress. we have used that when delete a location.  


                                        }
                                        is State.Failed -> {
                                            loadingDialog.stopLoading()
                                            Snackbar.make(
                                                mBinding.root,
                                                it.error,
                                                Snackbar.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }

                        })
                    alertDialog.setNegativeButton("No", null)
                    alertDialog.setCancelable(false).show()

                }


            }
        }






        with(mBinding) {
            img_doctor.load(args.doctorModel.image)
            txtView_doctorName.text = args.doctorModel.name.toString()
            txtView_doctorEmail.text = args.doctorModel.email.toString()
            txtView_doctorLiscence.text = args.doctorModel.doctorLiscence.toString()
        }


    }

    private fun fieldsAreReady(): Boolean {
        with(mBinding) {
            description = edtDescription.text?.trim().toString()
            date = editTextDate.text?.trim().toString()
            time = editTextTime.text?.trim().toString()
        }
        val view: View? = null
        val flag = false

        when {
            description.isEmpty() -> {
                mBinding.edtDescription.error = resources.getString(R.string.field_required)
            }
            date.isEmpty() -> {
                mBinding.editTextDate.error = resources.getString(R.string.field_required)
            }
            time.isEmpty() -> {
                mBinding.editTextTime.error = resources.getString(R.string.field_required)
            }
        }

        return if (flag) {
            view?.requestFocus()
            false
        } else true

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

    override fun onItemClickListener(cancerDataEntity: CancerDataEntity) {

        // TODO: 11/12/21 we have discovered a nice feature. every time we click an result to be sended we can display a litle image of the cancer that will be sended.
        //mBinding.imgtest.load(cancerDataEntity.img)
        cancerDataList.add(cancerDataEntity)



        // TODO: 14/12/21 Lo que podemos hacer es usar el objeto cancerDataEntity par coger la imagen y convertirla a uri. Entonces lo que haremos es que nos vamos a crear otra clase cancerDataModel donde como imagen tndra un string con ese uri par ala iamgen y lo demas igual. y eso es lo que añadiremos al array



//        img.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
//        val path: String = MediaStore.Images.Media.insertImage(
//            this.contentResolver,
//            img,
//            "dsfsdfsf",
//            "SDFSD"
//        )
//        val path2 = Uri.parse(path)
//
//        val cancerDataFirebaseModel = CancerDataFirebaseModel(
//            cancerImg = path2,
//            date = cancerDataEntity.date,
//            outcome = cancerDataEntity.outcome
//        )
//
//        cancerDataList.add(cancerDataFirebaseModel)
//        Log.d("cancerImg", cancerDataFirebaseModel.cancerImg.toString())


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
//            lifecycleScope.launch(Dispatchers.Main) {
//                saveImageInQ(cancerDataEntity.cancerImg!!)
//            }
//
//        }
//        else{
//            lifecycleScope.launch(Dispatchers.Main) {
//                saveImageInLegacy(cancerDataEntity.cancerImg!!)
//            }
//
//        }


        Log.d("onItemClickListener", cancerDataList.size.toString())

    }
//    private fun saveImageInQ(bitmap: Bitmap):Uri {
//        val filename = "IMG_${System.currentTimeMillis()}.jpg"
//        var fos: OutputStream? = null
//        var imageUri: Uri? = null
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
//            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
//            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
//            put(MediaStore.Video.Media.IS_PENDING, 1)
//        }
//
//        //use application context to get contentResolver
//        val contentResolver = application.contentResolver
//
//        contentResolver.also { resolver ->
//            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//            fos = imageUri?.let { resolver.openOutputStream(it) }
//        }
//
//        fos?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 70, it) }
//
//        contentValues.clear()
//        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
//        contentResolver.update(imageUri!!, contentValues, null, null)
//
//        return imageUri as Uri
//    }
//    fun saveImageInLegacy(bitmap:Bitmap){
//        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//        val image = File(imagesDir, filename)
//        fos = FileOutputStream(image)
//        fos?.use {bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)}
//    }
}