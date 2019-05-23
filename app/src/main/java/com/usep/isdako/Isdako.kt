package com.usep.isdako

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.google.firebase.database.FirebaseDatabase

class Isdako : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificatioChannels()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

    private fun createNotificatioChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            var eventChannel = NotificationChannel(
                CHANNEL_GEOFENCE_EVENT_ID,
                "Enter and exit events",
                NotificationManager.IMPORTANCE_HIGH
            )
            eventChannel.enableLights(true)
            eventChannel.enableVibration(true)
            eventChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            val manager = this.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(eventChannel)
        }
    }
    companion object {
        const val CHANNEL_GEOFENCE_EVENT_ID = "geofenceEventNotifChannel"
    }
}
