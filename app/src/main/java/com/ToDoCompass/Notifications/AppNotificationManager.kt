package com.ToDoCompass.Notifications

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.os.Build
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.core.app.NotificationCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ToDoCompass.IntentsActivity
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NOTIFICATION_BUTTON_TEXT_1
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NOTIFICATION_BUTTON_TEXT_2
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NOTIFICATION_BUTTON_TEXT_3
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NOTIF_ALARM
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NOTIF_ALARM_ID
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NOTIF_BUTTON1_INTENT
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NOTIF_BUTTON2_INTENT
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NOTIF_BUTTON3_INTENT
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NOTIF_DELETE_INTENT
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NOTIF_FULL_SCREEN_INTENT
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NOTIF_REQUEST_CODE
import com.ToDoCompass.R
import java.util.concurrent.TimeUnit

class AppNotificationManager(context : Context) : AppNotificationInfa{
    val context = context
    val channelId = "ToDoCompass_channel1"
    val channelName = "ToDoCompass Channel1"
    val checkWorkId = "check_work_ToDoCompass"
    val notificationManager =
        context
            .getSystemService(
                ComponentActivity.NOTIFICATION_SERVICE
            ) as NotificationManager
    
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    

    override fun scheduleAndroidAlarm(
        alarmId : Int,
        millisEpochOfAlarm : Long,
        millisFromNow : Long,
    ){
        val pendingIntent = createIntentForNotification(
            alarmId = alarmId,
        )
        
        val systemMillisElapsed = SystemClock.elapsedRealtime()
        val alarmTime : Long = millisFromNow+systemMillisElapsed

        alarmManager?.setExact(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            alarmTime,//calendar.timeInMillis,
            pendingIntent
        )
    }
    
    
    
    private fun createIntentForNotification(
        alarmId : Int,
        requestCode : Int = alarmId,
    ) : PendingIntent{
        val intent = Intent(context, AlarmReceiver::class.java) // Create an intent to launch the notification
        intent.setAction(NOTIF_ALARM)
        intent.putExtra(
            NOTIF_ALARM_ID,
            alarmId
        )
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) // API 31
        {
            PendingIntent.getBroadcast( // every pending intent must be unique to show different notifications.
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        return pendingIntent
    }
    
    
    override fun cancelAlarm(
        requestCode: Int
    ){
        val pendingIntent = createIntentForNotification(alarmId = requestCode)
        alarmManager?.cancel(pendingIntent)
    }
    
    
    override fun sendNotification(
        notifProp : NotificationProperties
    ) {
        
        val notification = buildNotification(notifProp)
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        
        
        channel.setSound(
            null,
            AudioAttributes.Builder()
                .build()
        )
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(
            notifProp.alarmId,
            notification
        )
    }
    
    
    
    
    fun buildNotification(
        notifProp : NotificationProperties
    ) : Notification{

        val icon = R.drawable.sharp_window_24
        
        //Silence
        val button1PendingIntent = createPendingIntent(
            intentClass = AlarmReceiver::class.java,
            alarmId = notifProp.alarmId,
            requestCode = -100*notifProp.alarmId,
            action = NOTIF_BUTTON1_INTENT,
            type = "broadcast"
        )
        
        //Done
        val button2PendingIntent = createPendingIntent(
            intentClass = AlarmReceiver::class.java,
            alarmId = notifProp.alarmId,
            requestCode = -1000*notifProp.alarmId-1,
            action = NOTIF_BUTTON2_INTENT,
            type = "broadcast"
        )
        
        //Reschedule
        val button3PendingIntent = createPendingIntent(
            intentClass = IntentsActivity::class.java,
            alarmId = notifProp.alarmId,
            requestCode = -1000*notifProp.alarmId-2,
            action = NOTIF_BUTTON3_INTENT,
            type = "activity"
        )
        
        val deleteIntent = createPendingIntent(
            intentClass = AlarmReceiver::class.java,
            alarmId = notifProp.alarmId,
            requestCode = -1000*notifProp.alarmId-3,
            action = NOTIF_DELETE_INTENT,
            type = "broadcast"
        )
        /*
        val contentIntent = createPendingIntent(
            IntentsActivity::class.java,
            notifProp.requestCode,
            NOTIF_CONTENT_INTENT,
            "activity"
        )
        */
        
        val dummyFullScreenIntent = createPendingIntent(
            intentClass = IntentsActivity::class.java,
            alarmId = notifProp.alarmId,
            requestCode = -1000*notifProp.alarmId-4,
            action = NOTIF_FULL_SCREEN_INTENT,
            type = "activity"
        )
        
        val output = NotificationCompat.Builder(context, channelId)
            .setCategory(Notification.CATEGORY_ALARM)
            .setSmallIcon(icon)
            .setChannelId(channelId)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            
            //.setSilent(true)
            //.setWhen(notificationTime)
            //.setWhen(calendar.timeInMillis)
            //.setContentIntent(contentIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(
                NotificationCompat
                    .InboxStyle()
                    .addLine("Reminder: ${notifProp.title}")
                    .addLine(notifProp.note)
                    //.addLine(taskAlarm.description)
                    //.addLine("another line")
                    //.setBigContentTitle(sb)
                    .setSummaryText("${notifProp.title}")
            )
            //.setContentIntent(contentIntent)
            .setDeleteIntent(deleteIntent)
            .setFullScreenIntent(dummyFullScreenIntent, true)
            .addAction(R.drawable.sharp_window_24, NOTIFICATION_BUTTON_TEXT_1, button1PendingIntent)
            .addAction(R.drawable.sharp_window_24, NOTIFICATION_BUTTON_TEXT_2, button2PendingIntent)
            .addAction(R.drawable.sharp_window_24, NOTIFICATION_BUTTON_TEXT_3, button3PendingIntent)
        return output.build()
    }
    
    override fun dismissNotification(requestCode: Int){
        notificationManager.cancel(requestCode)
        notificationManager.deleteNotificationChannel(channelId)
    }
    
    override fun dismissNotifications(){
        notificationManager.cancelAll()
    }
    
    
    fun buildNotification2(
        notifProp : NotificationProperties
    ) : Notification{
        val icon = R.drawable.ic_launcher_foreground
        
        val output = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(icon)
            .setChannelId(channelId)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            
            //.setWhen(notificationTime)
            //.setWhen(calendar.timeInMillis)
            //.setContentIntent(contentIntent)
            //.setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(
                NotificationCompat
                    .BigPictureStyle()
                    .setBigContentTitle(notifProp.title)
                    .setContentDescription(notifProp.note)
                    //.addLine(taskAlarm.description)
                    //.addLine("another line")
                    //.setBigContentTitle(sb)
                    .setSummaryText("Reminder: ${notifProp.title}")
            )
        
        return output.build()
        
    }
    
    // This starts CheckWorker which does workerDependency.checkWorkerWork().
    // It is not related to createCheckPendingIntent below
    override suspend fun StartCheckWorker(start : Boolean) {
        if (start) {
            val request = PeriodicWorkRequestBuilder<CheckWorker>(24, TimeUnit.HOURS)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                checkWorkId,
                ExistingPeriodicWorkPolicy.REPLACE,
                request
            )
        }
    }
    
    override fun isAlarmSetWithCode(
        requestCode : Int,
    ) : Boolean {
        val checkIntent = createCheckPendingIntent(requestCode)
        if (checkIntent != null) {
            //Log.v("notifmanager", "alarm is SET with alarmId = $requestCode")
            return true
        }
        else {
            //Log.v("notifmanager", "alarm is NOT SET with alarmId = $requestCode")
            return false
        }
        //checkIntent.setAction(INTENT_ACTION)
        //checkIntent.putExtra(INTENT_NAME, INTENT_MESSAGE)
    }
    
    
    //
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createCheckPendingIntent(requestCode: Int) : PendingIntent?{
        val intent = Intent(context, AlarmReceiver::class.java) // Create an intent to launch the notification
        intent.setAction(NOTIF_ALARM)
        intent.putExtra(
            NOTIF_REQUEST_CODE,
            requestCode
        )
        val checkIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) // API 31
        {
            PendingIntent.getBroadcast( // every pending intent must be unique to show different notifications.
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
            )
        } else {
            PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE
            )
        }
        return checkIntent
        
    }
    
    
    fun <T : Any> createPendingIntent(
        intentClass: Class<T>,
        alarmId : Int,
        requestCode: Int,
        action: String,
        type : String
    ) : PendingIntent{
        
        val thisIntent = Intent(context, intentClass)
        //thisIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        thisIntent.setAction(action)
        thisIntent.putExtra(
            NOTIF_ALARM_ID,
            alarmId
        )
        val pendingIntent = createMutablePendingIntentFromIntent(
            context = context,
            intent = thisIntent,
            type = type,
            requestCode = requestCode
        )
        return pendingIntent
    }
}


fun createMutablePendingIntentFromIntent(
    context: Context,
    intent: Intent,
    type: String,
    requestCode: Int,
) : PendingIntent {
    val pendingIntent= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // start activity from notification
        when (type) {
            "activity" -> PendingIntent.getActivity(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_ONE_SHOT
            )
            else -> PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_ONE_SHOT
            )
        }
    } else
        when (type) {
            "activity" -> PendingIntent.getActivity(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT
            )
            else -> PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT
            )
    }
    return pendingIntent
}