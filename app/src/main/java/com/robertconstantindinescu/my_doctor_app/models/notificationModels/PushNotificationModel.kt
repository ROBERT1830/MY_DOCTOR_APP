package com.robertconstantindinescu.my_doctor_app.models.notificationModels

data class PushNotificationModel(
    val data: NotificationDataModel,
    val to: String
)
