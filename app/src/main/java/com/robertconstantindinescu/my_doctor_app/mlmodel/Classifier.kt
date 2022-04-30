package com.robertconstantindinescu.my_doctor_app.mlmodel

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*

class Classifier(assetManager: AssetManager, modelPath: String, labelPath: String, inputSize: Int) {
    private var INTERPRETER: Interpreter
    private var LABEL_LIST: List<String>
    private val INPUT_SIZE: Int = inputSize
    private val PIXEL_SIZE: Int = 3
    private val IMAGE_MEAN = 0
    private val IMAGE_STD = 255.0f
    private val MAX_RESULTS = 3
    private val THRESHOLD = 0.4f

    data class Recognition(
        var id: String = "",
        var title: String = "",
        var confidence: Float = 0F
    )  {
        override fun toString(): String {
            return "Title = $title, Confidence = $confidence)"
        }
    }

    init {
        INTERPRETER = Interpreter(loadModelFile(assetManager, modelPath))
        LABEL_LIST = loadLabelList(assetManager, labelPath)
    }

    /**
     * con esto vamos a cargar el modelo de ML. Enotnces le pasamos el archivo y la ruta de ese aricho donde temos
     * el modelo.
     */
    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        //de lo que tenmos en asset cogemos su descripcion. Y como sabemos de que asset coger la descripcion
        //porque tenemos varios archivos, pues cuando pasamos la ruta.  cuando instanciamos la clase
        //pasamos la ruta tanto del label.txt como del modelo.
        val fileDescriptor = assetManager.openFd(modelPath)
        //Ahora nos vamos a hacer una conexion o un flujo a ese modelo usando la descripcion.
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        //Ahora obtenemos el canal del archivo para leer ese archivo
        val fileChannel = inputStream.channel
        //obtenemos los bytes primero par aempezar a leer.
        val startOffset = fileDescriptor.startOffset
        //y vamos a determinar la longitud.
        val declaredLength = fileDescriptor.declaredLength
        //ahora mapeamos ese canal en la memoria para poder leer el fichero. posicion a apartir de la cual leeremos y la logitud que ocupara en memoria ese mapa.
        //devolvemos ese mapa.
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun loadLabelList(assetManager: AssetManager, labelPath: String): List<String> {
        //leemos el lable.zt y lo pasamos a una lista.
        return assetManager.open(labelPath).bufferedReader().useLines { it.toList() }

    }

    /**
     * A este método le pasamos un bitmap ya escalado para crear internamente una imagen escalada
     * @return --> dataclass Recognition con el diagnostico.
     */
    fun recognizeImage(bitmap: Bitmap): List<Recognition> {
        //El bitmap que obtenemos, y que está en el iamgeview lo escalamos
        // y no le pasamos filtro de alta resolucion
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false)
        //Ahora vamos a convertir ese bytmap escalado a bites.
        val byteBuffer = convertBitmapToByteBuffer(scaledBitmap)
        //definimos cual sera el output que nos devuevle el interpreter
        val result = Array(1) { FloatArray(LABEL_LIST.size) }
        //Corremos el algoritmo en la iamgen o conjunto de byte que hemos pasado
        // y le definimos la longidud del out put porque la del input es el chorizo de bites del buffer
        INTERPRETER.run(byteBuffer, result)
        return getSortedResult(result)
    }


    /**
     * Este metodo se encarga de pasar el bitmap a bytes. Y nos devuvle un objeto bytebuffer
     */
    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        //para crearnos bytes a partir del bitmap vamos a uasar un bytebuffer. Y pondremos todos los
        /*bits del bitmap en el bytebuffer. */
        val byteBuffer = ByteBuffer.allocateDirect(4 * INPUT_SIZE * INPUT_SIZE * PIXEL_SIZE)
        byteBuffer.order(ByteOrder.nativeOrder())
        //Creamos un array con un determiando tamaño.
        val intValues = IntArray(INPUT_SIZE * INPUT_SIZE)

        /*Cogemos los pixeles del bitmap.
        * getPixels ---> Returns in pixels[] a copy of the data in the bitmap. Each value is a packed int representing a Color.
        * le pasamos como parametro el array que contendra esos pixeles 224*224 y luego definimos a aprtir de
        * donde se leeran los pixeles que es de la posicon 0 0 para x e y, y el numero de pixeles qeu se leeran
        * por cada fila y el numero de filas que es iguala  ala altura. Eso lo obtenemos
        * cogiendo con un get los calroes del bitmap. */
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var pixel = 0

        for (i in 0 until INPUT_SIZE) {
            for (j in 0 until INPUT_SIZE) {
                val `val` = intValues[pixel++]

                byteBuffer.putFloat((((`val`.shr(16)  and 0xFF) - IMAGE_MEAN) / IMAGE_STD))
                byteBuffer.putFloat((((`val`.shr(8) and 0xFF) - IMAGE_MEAN) / IMAGE_STD))
                byteBuffer.putFloat((((`val` and 0xFF) - IMAGE_MEAN) / IMAGE_STD))
            }
        }
        return byteBuffer
    }


    private fun getSortedResult(labelProbArray: Array<FloatArray>): List<Recognition> {
        Log.d("Classifier", "List Size:(%d, %d, %d)".format(labelProbArray.size,labelProbArray[0].size,LABEL_LIST.size))

        val pq = PriorityQueue(
            MAX_RESULTS,
            Comparator<Recognition> {
                    (_, _, confidence1), (_, _, confidence2)
                -> java.lang.Float.compare(confidence1, confidence2) * -1
            })

        for (i in LABEL_LIST.indices) {
            val confidence = labelProbArray[0][i]
            if (confidence >= THRESHOLD) {
                pq.add(
                    Recognition("" + i,
                    if (LABEL_LIST.size > i) LABEL_LIST[i] else "Unknown", confidence)
                )
            }
        }
        Log.d("Classifier", "pqsize:(%d)".format(pq.size))

        val recognitions = ArrayList<Recognition>()
        val recognitionsSize = Math.min(pq.size, MAX_RESULTS)
        for (i in 0 until recognitionsSize) {
            recognitions.add(pq.poll())
        }
        return recognitions
    }

}