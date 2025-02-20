package com.ToDoCompass.Notifications

import javax.inject.Singleton

@Singleton
interface AppNotificationInfa {
    fun scheduleAndroidAlarm(
        requestCode : Int,
        millisEpochOfAlarm : Long = 0,
        millisFromNow : Long = millisEpochOfAlarm-System.currentTimeMillis(),
    )
    
    fun sendNotification(
        notifProp : NotificationProperties
    )
    
    fun dismissNotification(requestCode: Int)
    fun dismissNotifications()
    
    fun isAlarmSetWithCode(
        requestCode : Int,
    ) : Boolean
    
    
    fun cancelAlarm(
        requestCode: Int
    )
    
    suspend fun StartCheckWorker(start : Boolean)
    
}