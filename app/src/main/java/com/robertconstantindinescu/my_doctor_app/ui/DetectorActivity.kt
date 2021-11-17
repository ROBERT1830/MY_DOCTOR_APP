package com.robertconstantindinescu.my_doctor_app.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.robertconstantindinescu.my_doctor_app.mlmodel.Classifier
import com.robertconstantindinescu.my_doctor_app.databinding.ActivityDetectorBinding
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.CancerDataEntity
import com.robertconstantindinescu.my_doctor_app.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.text.SimpleDateFormat

// TODO: 16/11/21 Tenemos que opne rqeu sera un entry point pq el viewmodel está inyectado.
@AndroidEntryPoint
class DetectorActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel

    private lateinit var mClassifier: Classifier
    private lateinit var mBitmap: Bitmap
    private lateinit var mBinding: ActivityDetectorBinding

    private val mCameraRequestCode = 0
    private val mGalleryRequestCode = 2

    private val mInputSize = 224
    private val mModelPath = "model.tflite"
    private val mLabelPath = "labels.txt"
    private val mSamplePath = "skin-icon.jpg"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        mBinding = ActivityDetectorBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mClassifier = Classifier(assets, mModelPath, mLabelPath, mInputSize)

        resources.assets.open(mSamplePath).use {
            //con esto vamos a decodificar ese inputStream(que es una succesion de bit) de esa imagen
            //y formamos un bitmap con la imagen.
            mBitmap = BitmapFactory.decodeStream(it)
            //ahora lo que hacemos con ese mBitmap que obtenemos, es escalarlo a nuestro gusto. Transformamos la imagen
            //El meotodo createScaledBitmap() acepta los siguientes parametros.
            //la fente bitmap que es el mBitmap
            //la nueva altura y anchura en pixeles que sera 224
            //filtros par amayor calidad que en este caso lo denamos a falso porque bajaria el rendimietno.
            mBitmap = Bitmap.createScaledBitmap(mBitmap, mInputSize, mInputSize, true)
            //simplemente vamos a asignar a la iamgeview ese bitmap transofmrado.
            mBinding.mPhotoImageView.setImageBitmap(mBitmap)


            mBinding.mCameraButton.setOnClickListener {
                //llamamos a la camara con un codigo de respuesta en caso de qeu se haya accedido.
                val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                //si se vuevle a la atividad princiapl desde el intentn camara, lo que hace este metodo es devolver
                //ese request code en elonActivityResult. cuando la actividad exista, es decir volvemos a ella
                startActivityForResult(callCameraIntent, mCameraRequestCode) //cuadno haces la staarr activity for resutl pasas ese codigo que te sera devievleto en el onActityresutl
            }

            mBinding.mGalleryButton.setOnClickListener {
                val callGalleryIntent = Intent(Intent.ACTION_PICK)
                callGalleryIntent.type = "image/*"
                startActivityForResult(callGalleryIntent, mGalleryRequestCode)
            }
            mBinding.mDetectButton.setOnClickListener {
                /*Cuadno pulsamos el boton de detectar llamamos a la calse mClassifier. que ya la habíamos
                * inicializado. y llamamso al metodo recognizeImage. */
                val results = mClassifier.recognizeImage(mBitmap).firstOrNull()
                mBinding.mResultTextView.text= results?.title+"\n Confidence:"+results?.confidence

            }

            mBinding.saveButton.setOnClickListener {
                // TODO: 16/11/21 crear fecha directamente, coger la actual
                // TODO: 16/11/21 Crar objeto cancerentity
                // TODO: 16/11/21 guardar objeto usando el viewmodel que hay que inyectarlo
                val date: String = generateDate()
                val cancerDataEntity = CancerDataEntity(
                    date, mBitmap, mBinding.mResultTextView.text.toString()
                )

                saveCancerRecord(cancerDataEntity)
            }
        }




    }

    private fun saveCancerRecord(cancerDataEntity: CancerDataEntity) {
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mainViewModel.insertCancerRecord(cancerDataEntity)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val currentDate = sdf.format(java.util.Date())

        return currentDate



    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //si el request code
        if(requestCode == mCameraRequestCode){
            //si el result code es OK es decir el ususario ha pulsado ok despues de tomar la foto
            //y además los datos que se reciben del intent camera no son nulos

            if(resultCode == Activity.RESULT_OK && data != null) {
                /*Entonces a nuestro Bitmap vamos a asociarle esa foto que viene de la camara
                * o mejor dicho ese bitmap porque lo casteamos. Y ademas cuando obtenemos ese bitmap
                * le vamos a asociar una clave, en este caso data. */
                mBitmap = data.extras!!.get("data") as Bitmap
                //escalamos la imagen en de acuerdo a una fucnion que hemos definido abajo.
                mBitmap = scaleImage(mBitmap)
                //indicamos un toast con las dimensiones de la imagen
                val toast = Toast.makeText(this, ("Image crop to: w= ${mBitmap.width} h= ${mBitmap.height}"), Toast.LENGTH_LONG)
                toast.setGravity(Gravity.BOTTOM, 0, 20)
                toast.show()
                //ahora vamos a coger y setear la iamgen en el imageview.
                mBinding.mPhotoImageView.setImageBitmap(mBitmap)
                mBinding.mResultTextView.text= "Your photo image set now."
            } else {
                //si resulta que el codigo no es ok cuando volvemos de la camara pues nada, un mensaje de que se canceló la camara.
                Toast.makeText(this, "Camera cancel..", Toast.LENGTH_LONG).show()
            }
            /*Si el reqeustcode es el de la camara*/
        } else if(requestCode == mGalleryRequestCode) {
            //si los datos no son nulos
            if (data != null) {
                //Ahora vamos a alamcenarnos la dirección de la imagen de la galeria. Es decir la uri
                //con el data hacemos como un get y obtenemos esa uri de la iamgen de al galeria.
                //devuelve //The URI of the data this intent is targeting

                val uri = data.data

                //ahora con esa uri te creas el bitmap.
                try {
                    mBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                } catch (e: IOException) {
                    e.printStackTrace()
                }


                println("Success!!!")
                //lo escalamos
                mBitmap = scaleImage(mBitmap)
                //y ahora vamos a meterlo en el iamgeview.
                mBinding.mPhotoImageView.setImageBitmap(mBitmap)

            }
        } else {
            Toast.makeText(this, "Unrecognized request code", Toast.LENGTH_LONG).show()

        }
    }


    fun scaleImage(bitmap: Bitmap?): Bitmap {
        //obtenemos la anchura del bitmap original qeu nos llega a de la cámara.
        val orignalWidth = bitmap!!.width
        //obtenemos tambien la altura.
        val originalHeight = bitmap!!.height
        //reeescalamos la imagen  dividientdo 224.0 entre el woth original ---> podemos meter un puto de rutpura pra ver que size tiene.
        val scaleWidth = mInputSize.toFloat() / orignalWidth
        val scaleHeight = mInputSize.toFloat() / originalHeight
        //Nos creamos una matriz.
        val matrix = Matrix()
        //escalamos la matriz de acuerdo a la altura y anchira en pixeles.
        matrix.postScale(scaleWidth, scaleHeight)
        //devolvemos un bitmap tranformado y escalado según la estrucutra de la matriz que hemos defido
        //le pasamos el botmap original de la camapara, le decimos que el primer pixel de la posicion x
        /*es el cero al igual que el pixel del eje y. Leugo definimos la altura y anchura en pixeles
        * que sera 224 ¡, la matriz y el filtro. */
        return Bitmap.createBitmap(bitmap, 0, 0, orignalWidth, originalHeight, matrix, true)
    }

}