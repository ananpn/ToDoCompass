package com.ToDoCompass.Notifications

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ToDoCompass.IntentsActivity
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NOTIF_ALARM
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NOTIF_ALARM_ID
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NOTIF_ASK_AGAIN
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NOTIF_BUTTON1_INTENT
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NOTIF_BUTTON2_INTENT
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NOTIF_DELETE_INTENT
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NOTIF_FULL_SCREEN_INTENT
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NOTIF_SNOOZE_INTENT
import com.ToDoCompass.ViewModels.WorkerDependency
import com.ToDoCompass.database.NotifType
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/*
Android13: We have full control when we want to ask the user for permission
Android 12L or lower: The system will show the permission dialog when the app creates its first notification channel*/

// the notification and channel can be created here
@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject lateinit var workerDependency : WorkerDependency
    override fun onReceive(context: Context?, intent: Intent?) {
        val alarmId = intent?.getIntExtra(NOTIF_ALARM_ID, -1)
        //val notifProp : NotificationProperties? = intent.getParcelableExtra(NOTIF_PROPERTIES)
        val action = intent?.action
        when (action){
            NOTIF_ALARM -> {
                alarmId?.let{
                    workerDependency.receivedAlarmWork(it)
                }
            }
            NOTIF_ASK_AGAIN -> {
                alarmId?.let{
                    workerDependency.askAgainWork(it)
                }
                
            }
            NOTIF_BUTTON2_INTENT -> {
                alarmId?.let {
                    workerDependency.notifDoneWork(it)
                }
            }
            NOTIF_DELETE_INTENT -> {
                workerDependency.notifDeleteWork()
            }
            NOTIF_BUTTON1_INTENT -> {
                workerDependency.notifSilenceWork()
            }
            NOTIF_FULL_SCREEN_INTENT -> {
                workerDependency.notifDeleteWork()
            }
            NOTIF_SNOOZE_INTENT -> {
                
                context?.let{
                    val intent = Intent(it, IntentsActivity::class.java).apply {
                        this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    it.startActivity(intent)
                }
                workerDependency.notifSnoozeWork()
            }
        }
    }
}

@SuppressLint("UnspecifiedImmutableFlag")
fun NotificationManager.sendReminderNotification(
    applicationContext: Context,
    channelId: String,
) {}



//@Parcelize
data class NotificationProperties(
    val alarmId : Int,
    val title : String,
    val iconPath : String = "",
    val note : String,
    val notifType : NotifType
    //val notifSettings : NotificationSettings,
    ) //: Parcelable

