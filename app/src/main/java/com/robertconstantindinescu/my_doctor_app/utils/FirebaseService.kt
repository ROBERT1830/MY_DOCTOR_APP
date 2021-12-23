package com.robertconstantindinescu.my_doctor_app.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.ui.DoctorVideoCallActivity
import com.robertconstantindinescu.my_doctor_app.ui.PatientVideoCallActivity
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.ROOM_CODE
import kotlin.random.Random

private const val CHANNEL_ID = "my_channel"

class FirebaseService:FirebaseMessagingService() {

    companion object{
        var sharedPref: SharedPreferences? = null

        var token: String?
            get() {
                return sharedPref?.getString("token", "")
        }
        set(value) {
            sharedPref?.edit()?.putString("token", value)?.apply()
        }

    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken


//        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
//            if (task.isSuccessful){
//                Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
//                return@addOnCompleteListener
//            }
//            token = task.result
//            Log.d("token", token.toString())
//            CoroutineScope(Dispatchers.IO).launch {
//                saveTokenApp(token!!)
//            }
//
//        }

    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        var notificationMessage: String? = null
        var roomCode = ""

        //get data from the notification message
        if (message.data.isNotEmpty()){
            notificationMessage = message.data["message"]
            roomCode = notificationMessage!!.takeLast(10)

        }


        val intent = Intent(this, PatientVideoCallActivity::class.java)
        intent.putExtra(ROOM_CODE, roomCode)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)


        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            //chose the icon for the notification
            .setSmallIcon(R.drawable.ic_cancer)
            //this is to delete the notificaiton when click on it
            .setAutoCancel(true)
            //the contentn notification is od the pending intent.
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationID, notification)


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
            description = "My channel description"
            //enable the light blink when the notitification cames up
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }



//    private suspend fun saveTokenApp(token: String) {
//
//        val auth = Firebase.auth
//        val map: MutableMap<String, Any> = HashMap()
//        map["appToken"] = token
//
//        Firebase.database.getReference("Users")//.child("Patients")
//            .child(auth.uid!!).updateChildren(map).await()
//
//    }
}