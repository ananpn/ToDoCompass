package com.ToDoCompass.ViewModels

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.ToDoCompass.LogicAndData.Constants.Companion.defaultAlarmNotificationType
import com.ToDoCompass.LogicAndData.Constants.Companion.defaultNotificationType
import com.ToDoCompass.LogicAndData.Constants.Companion.silentNotificationType
import com.ToDoCompass.LogicAndData.Constants.Companion.testNotificationType
import com.ToDoCompass.LogicAndData.LogicConstants.Companion.NOTIF_TYPE_USE_DEFAULT_ALARM
import com.ToDoCompass.LogicAndData.LogicConstants.Companion.SILENT_NOTIF_TYPE
import com.ToDoCompass.LogicAndData.LogicConstants.Companion.USE_GROUP_DEFAULT
import com.ToDoCompass.LogicAndData.TimeFunctions.Companion.epochMillisFromDateAndTime
import com.ToDoCompass.LogicAndData.TimeFunctions.Companion.isTimeBeforeCurrentFull
import com.ToDoCompass.Notifications.AppNotificationInfa
import com.ToDoCompass.Notifications.NotificationProperties
import com.ToDoCompass.Notifications.SoundAndVibrationPlayer
import com.ToDoCompass.database.AppRepository
import com.ToDoCompass.database.NotifType
import com.ToDoCompass.database.TaskAlarm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkerDependency @Inject constructor(
    private val repo: AppRepository,
    private val notifMan : AppNotificationInfa,
    //private val prefs: PrefsImpl,
    playerIn : SoundAndVibrationPlayer,
    context : Context

) {
    val context = context
    private val scope = CoroutineScope(Dispatchers.IO)
    val player = playerIn

    suspend fun checkAlarms(){
        val alarmStatuses = repo.getAllAlarmStatusesAsList()
        alarmStatuses.forEach{
            if (!isTimeBeforeCurrentFull(it.date+","+it.time, format = "yyyy-MM-dd,HH:mm")) {
                val alarmSet = notifMan.isAlarmSetWithCode(it.alarmId)
                if (!alarmSet && it.active) {
                    val activeAlarmNotSet = repo.getAlarmWithId(it.alarmId)
                    if (activeAlarmNotSet != null) {
                        notifMan.scheduleAndroidAlarm(
                            requestCode = it.alarmId,
                            millisEpochOfAlarm = epochMillisFromDateAndTime(
                                activeAlarmNotSet.date,
                                activeAlarmNotSet.time
                            )
                        )
                    }
                }
                if (alarmSet && !it.active) {
                    notifMan.cancelAlarm(it.alarmId)
                }
            }
            else {
                notifMan.cancelAlarm(it.alarmId)
                val alarm = repo.getAlarmWithId(alarmId = it.alarmId)
                alarm?.let{
                    repo.updateEntity(alarm.copy(active = false))
                }
            }
        }
    }

    //This is a Dumb function, doesn't check if alarm active just schedules
    fun scheduleAlarm(alarm : TaskAlarm){
        notifMan.scheduleAndroidAlarm(
            requestCode = alarm.alarmId,
            millisEpochOfAlarm = epochMillisFromDateAndTime(
                alarm.date,
                alarm.time
            )
        )
    }
    
    //This needs the alarm to be correct
    fun scheduleAlarmWithCheck(alarm : TaskAlarm)
    {
        if (alarm.active){
            notifMan.scheduleAndroidAlarm(
                requestCode = alarm.alarmId,
                millisEpochOfAlarm = epochMillisFromDateAndTime(
                    alarm.date,
                    alarm.time
                )
            )
        }
        else {
            notifMan.cancelAlarm(alarm.alarmId)
        }
        
    }
    
    suspend fun insertAndScheduleAlarm(alarm: TaskAlarm) {
        val alarmId = repo.insertAlarmAndReturnId(alarm)
        if (alarmId != null)
            notifMan.scheduleAndroidAlarm(
                requestCode = alarmId.toInt(),
                millisEpochOfAlarm = epochMillisFromDateAndTime(
                    date = alarm.date,
                    time = alarm.time
                )
            )
    }
    
    suspend fun updateAndScheduleAlarm(
        alarm : TaskAlarm?,
        active : Boolean,
        date : String,
        time : String,
    ) {
        alarm?.copy(
            date = date,
            time = time.take(5),
            active = active
        )?.let{
            repo.updateEntity(it)
            when (active){
                true -> notifMan.scheduleAndroidAlarm(
                    requestCode = it.alarmId,
                    millisEpochOfAlarm = epochMillisFromDateAndTime(
                        date = it.date,
                        time = it.time
                    )
                )
                false -> notifMan.cancelAlarm(requestCode = it.alarmId)
            }
        }
    }
    
    fun receivedAlarmWork(
        requestCode : Int
    ) = scope.launch{
        Log.v("workerdep receivedAlarmWork", "launch requestCode = $requestCode")
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return
        }
        val alarm = repo.getAlarmWithId(requestCode)
        alarm?.let{alarmThis ->
            val task = repo.getTaskWithId(alarmThis.parentId)
            task?.let{taskThis ->
                val notifType = when{
                    alarmThis.notifTypeId == USE_GROUP_DEFAULT -> repo.obtainGroupDefaultNotifTypeForTask(alarm.parentId)
                    alarmThis.notifTypeId == NOTIF_TYPE_USE_DEFAULT_ALARM -> defaultAlarmNotificationType
                    alarmThis.notifTypeId == SILENT_NOTIF_TYPE -> silentNotificationType
                    alarmThis.notifTypeId >= 0 -> repo.getNotifTypeWithId(alarmThis.notifTypeId)
                    else -> defaultAlarmNotificationType
                } ?:defaultAlarmNotificationType
                val notifProp = NotificationProperties(
                    alarmId = requestCode,
                    title = taskThis.title,
                    note = alarmThis.note,
                    notifType = notifType
                //notifSettings = getNotificationSettingsFromPrefs(alarm.alarmType),
                )
                sendNotifAndPlaySound(notifProp)
            } ?:repo.deleteAlarm(alarmThis)
        }
    }
    
    fun askAgainWork(requestCode : Int){
        Log.v("workerdep askagainwórk", "launch requestCode = $requestCode")
        player.stopPlayback()
        //notifMan.scheduleAskAgain()
    
    }
    
    fun notifDeleteWork(){
        Log.v("workerdep notifDeleteWork", "launch")
        player.stopPlayback()
    }
    
    fun notifDismissWork(requestCode: Int){
        notifMan.dismissNotification(requestCode = requestCode)
        player.stopPlayback()
        
    }
    
    fun notifSnoozeWork(){
        player.stopPlayback()
    }
    
    fun notifSilenceWork(){
        player.stopPlayback()
    }
    
    fun notifDoneWork(requestCode : Int) = scope.launch{
        val alarm = repo.getAlarmWithId(requestCode)
        if (alarm != null){
            repo.updateEntity(
                alarm.copy(active = false)
            )
            val task = repo.getTaskWithId(alarm.parentId)
            if (task != null) {
                repo.updateEntity(
                    task.copy(taskDone = true)
                )
            }
        }
        player.stopPlayback()
        notifMan.dismissNotification(requestCode)
    }
    
    fun sendNotifAndPlaySound(notifProp : NotificationProperties){
        notifMan.sendNotification(
            notifProp
        )
        player.playNotificationSound(notifProp.notifType ?: defaultNotificationType)
    }
    
    fun checkWorkerWork() = scope.launch{
        checkAlarms()
    }
    
    fun startCheckWorker() = scope.launch{
        notifMan.StartCheckWorker(true)
    }

    
    
    //TESTING ****************************************************************************
    
    
    fun testReceivedAlarmWork() = scope.launch{
        val notifProp = NotificationProperties(
            alarmId = 0,
            title = "title test",
            note = "note test",
            notifType = testNotificationType,
        )
        sendNotifAndPlaySound(
            notifProp
        )
        
        
    }
    
    fun testScheduleAlarmWork() = scope.launch{
        notifMan.scheduleAndroidAlarm(
            requestCode = 1,
            millisEpochOfAlarm = System.currentTimeMillis()+6000
            
        )
        
        
    }
    
    fun cancelAllAlarms() = scope.launch{
        val alarms = repo.getAllAlarmStatusesAsList()
        alarms.forEach {
            notifMan.cancelAlarm(it.alarmId)
        }
    }
    
    fun getNotifTypesFromRepo() : Flow<List<NotifType>> {
        return repo.getAllNotifTypes()
    }
    
    
    


    //CHECKS ****************************************************************************
/*
    suspend fun checkBackGroundProcessState() : Boolean {
        var output = false
        try {
            if (tempUpdateMan.isWorkRunning()) output = true
        }
        catch (e : Exception){
        }
        try {
            if (isAlarmSet(context)) output = true
        }
        catch (e : Exception){
        }

        return output

    }

    fun initCheckWorker(start : Boolean) = coroutineScope.launch{
        tempUpdateMan.StartCheckWorker(start)
    }

    fun checkWorkerWork() = coroutineScope.launch{
        val isRunning = checkBackGroundProcessState()
        if (!isRunning && storedData.updateInterval != "Disabled"){
            initializeTemperatureUpdater()
        }
        if (storedData.updateInterval == "Disabled"){
            tempUpdateMan.StartCheckWorker(false)
        }
    }

    suspend fun readPrefs() {
        storedData = dataFlow.firstOrNull() ?: storedData
    }
    */
}

