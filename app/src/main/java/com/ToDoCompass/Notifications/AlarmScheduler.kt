package com.ToDoCompass.Notifications

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NOTIFICATION
import com.ToDoCompass.MainActivity
import com.ToDoCompass.R
import com.ToDoCompass.database.TaskAlarmFront

//@SuppressLint("UnspecifiedImmutableFlag")
fun scheduleAlarm(channelId: String, context: Context, taskAlarm: TaskAlarmFront) {
    //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // API 26 TODO create a function that lower APIs
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    val (hours, min) = //listOf(12,4)
        taskAlarm.time!!.split(":").map { it.toInt() }
    val (year, month, day) =
        //listOf(2024,1,19)
        taskAlarm.date!!.split("-")
            .map { it.toInt() }
    Log.v("scheduleAlarm", "$year, $month, $day, $hours, $min")
    // if the date is saved as null, app always crushes
    val calendar = Calendar.getInstance()
    //calendar.set(Calendar.AM_PM, Calendar.AM);

    /*calendar.timeInMillis = System.currentTimeMillis()
    calendar.clear()*/
    val notificationIntent = Intent(context, MainActivity::class.java)
    notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    /*
    val pIntent= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API 31
        PendingIntent.getBroadcast( // every pending intent must be unique to show different notifications.
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_MUTABLE
        )
    } else {
        PendingIntent.getBroadcast(
            context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
    */
    //val iconDrawable: Drawable = ContextCompat.getDrawable(context, R.drawable.vc_done)!!
    //val iconBitmap: Bitmap = iconDrawable.toBitmap()
    /*  val contentIntent = PendingIntent.getActivity(
          context,
          0, notificationIntent,
          PendingIntent.FLAG_CANCEL_CURRENT
      )*/

    val contentIntent= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // start activity from notification
        PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_MUTABLE
        )
    } else {
        PendingIntent.getActivity(
            context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    val nm: NotificationManager = context
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    calendar.set(year, month - 1, day, hours, min,0)

    val notificationTime: Long = System.currentTimeMillis() + (5 * 1000)

    if (calendar.timeInMillis > System.currentTimeMillis() || true) { // set alarm if the time is in future

/*
        val sb: Spannable = SpannableString(taskAlarm.title)
        sb.setSpan(StyleSpan(MaterialTheme), 0, sb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)*/

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setChannelId(channelId)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setWhen(notificationTime)
            //.setWhen(calendar.timeInMillis)
            .setContentIntent(contentIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(
                NotificationCompat
                .InboxStyle()
                .addLine(taskAlarm.title)
                .addLine(taskAlarm.time)
                //.addLine(taskAlarm.description)
                //.addLine("another line")
                //.setBigContentTitle(sb)
                .setSummaryText("Reminder"))
            .build()
        val intent = Intent(context, AlarmReceiver::class.java) // Create an intent to launch the notification
        intent.putExtra(NOTIFICATION, notificationBuilder)

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API 31
            PendingIntent.getBroadcast( // every pending intent must be unique to show different notifications.
                context,
                taskAlarm.parentId,
                intent,
                PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                context, taskAlarm.parentId.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        Log.v("createChannel", "setting alarm")

        alarmManager?.setExact(
            AlarmManager.RTC_WAKEUP,
            notificationTime,//calendar.timeInMillis,
            pendingIntent
        )
    }
    //}
}