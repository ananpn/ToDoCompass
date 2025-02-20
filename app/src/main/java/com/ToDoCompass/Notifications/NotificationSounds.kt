package com.ToDoCompass.Notifications

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.ToDoCompass.LogicAndData.obtainSystemDefaultAlarmRingtone

// Function to retrieve a list of available notification sounds
@Composable
fun getNotificationSounds(): List<NotificationSound> {
    val context = LocalContext.current
    val defaultAlarmUriString = Settings
        .System
        .getString(context.contentResolver, Settings.System.ALARM_ALERT)
    val defaultNotificationUriString = Settings
        .System
        .getString(context.contentResolver, Settings.System.NOTIFICATION_SOUND)
    val systemDefaultAlarmUri = defaultAlarmUriString
        .toRingtoneUri(defaultType = RingtoneManager.TYPE_ALARM)
    val systemDefaultNotificationUri = defaultNotificationUriString
        .toRingtoneUri(defaultType = RingtoneManager.TYPE_NOTIFICATION)
    //Log.v("getNotificationSound", "systemDefaultAlarmUri = $systemDefaultAlarmUri, systemDefaultNotificationUri = $systemDefaultNotificationUri")
    val silentNotification = NotificationSound(
        title = "Silent",
        uri = null
    )
    val systemDefaultAlarmTitle = defaultAlarmUriString.toRingToneTitle()
    val systemDefaultNotificationTitle = defaultNotificationUriString.toRingToneTitle()
    val notificationSounds = mutableListOf<NotificationSound>(silentNotification)
    notificationSounds.add(NotificationSound("Use alarm default ($systemDefaultAlarmTitle)", obtainSystemDefaultAlarmRingtone()))
    notificationSounds.add(NotificationSound("Use notification default ($systemDefaultNotificationTitle)", obtainSystemDefaultAlarmRingtone()))
    notificationSounds.add(NotificationSound("--Alarms--", null))
    val ringtoneManager = RingtoneManager(LocalContext.current)
    ringtoneManager.setType(RingtoneManager.TYPE_ALARM)
    val cursor = ringtoneManager.cursor
    while (cursor.moveToNext()) {
        var title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
        val uri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX)
        val id = cursor.getString(RingtoneManager.ID_COLUMN_INDEX)
        val soundUri = Uri.parse("$uri/$id")
        //Log.v("getNotificationSound", "soundUri = $soundUri")
        if (soundUri == systemDefaultAlarmUri){
            title += " (Default)"
        }
        notificationSounds.add(NotificationSound(title, soundUri))
    }
    cursor.close()
    notificationSounds.add(NotificationSound("--Notifications--", null))
    val ringtoneManager2 = RingtoneManager(LocalContext.current)
    ringtoneManager2.setType(RingtoneManager.TYPE_NOTIFICATION)
    val cursor2 = ringtoneManager2.cursor
    while (cursor2.moveToNext()) {
        var title = cursor2.getString(RingtoneManager.TITLE_COLUMN_INDEX)
        val uri = cursor2.getString(RingtoneManager.URI_COLUMN_INDEX)
        val id = cursor2.getString(RingtoneManager.ID_COLUMN_INDEX)
        val soundUri = Uri.parse("$uri/$id")
        if (soundUri == systemDefaultNotificationUri){
            title += " (Default)"
        }
        notificationSounds.add(NotificationSound(title, soundUri))
    }
    cursor2.close()
    return notificationSounds
}



// Data class to represent a notification sound
data class NotificationSound(val title: String, val uri: Uri?)

// ViewModel to manage the selection of notification sound
class NotificationSoundViewModel(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    private val defaultSoundKey = "default_notification_sound"
    // Get the currently selected default notification sound
    fun getDefaultNotificationSound(): NotificationSound? {
        val soundUriString = sharedPreferences.getString(defaultSoundKey, null)
        return soundUriString?.let {
            val title = "Custom Sound" // You can also provide a default title
            val uri = Uri.parse(soundUriString)
            NotificationSound(title, uri)
        }
    }
    
    // Set the default notification sound
    fun setDefaultNotificationSound(soundUri: Uri) {
        sharedPreferences.edit().putString(defaultSoundKey, soundUri.toString()).apply()
    }
}

fun getActualDefaultAlarmRingtoneUriString(context: Context): String {
    return Settings.System.getString(context.contentResolver, Settings.System.ALARM_ALERT)
    /*
    return if (defaultAlarmUriString != null) {
        Uri.parse(defaultAlarmUriString.split("?").first())
    } else {
        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
    }
    */
}

fun getActualDefaultNotificationRingtoneUri(context: Context): Uri {
    val defaultAlarmUriString = Settings.System.getString(context.contentResolver, Settings.System.NOTIFICATION_SOUND)
    return if (defaultAlarmUriString != null) {
        Uri.parse(defaultAlarmUriString.split("?").first())
    } else {
        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    }
}

private fun String.toRingtoneUri(defaultType : Int) : Uri{
    return if (this != null){
        Uri.parse(this.split("?").first())
    } else {
        RingtoneManager.getDefaultUri(defaultType)
    }
}

private fun String.toNotificationRingtoneUri() : Uri{
    return if (this != null){
        Uri.parse(this.split("?").first())
    } else {
        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    }
}

private fun String.toRingToneTitle() : String{
    return this.substringAfter("title=").substringBefore("&").replace("%20", " ")
}

