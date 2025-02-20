package com.ToDoCompass.database

import com.ToDoCompass.uiComponents.smallComponents.GroupDefaultNotifType
import kotlinx.coroutines.flow.Flow


interface AppRepository {
    suspend fun updateEntity(entity : Any)
    
    suspend fun insertEntity(entity : Any)
    
    suspend fun deleteEntityTotally(entity : Any)
    
    suspend fun deleteFromTableWithId(id : Int, table : String)
    
    // Task methods ******************************
    
    suspend fun insertNewTask(task : Task, alarm : TaskAlarm?) : Int?
    
    suspend fun getAllTasksProfileRep(profileId : Int): List<Task>
    
    suspend fun getAllTasksOfProfileAsListItem(profileId : Int) : List<ListItem>
    
    fun getTaskWithId(id : Int) : Task?

    suspend fun updateTasksSequentially(direction : Int, id : Int, profileId : Int) : Int

    suspend fun deleteTask(task : Task)
    
    suspend fun updateTaskDone(task : Task, done : Boolean)

    fun getAllTasks(): Flow<List<Task>>

    suspend fun updateTasks(tasks : List<Task>)

    suspend fun insertTasks(tasks : List<Task>)

    suspend fun upsertTasks(tasks : List<Task>)

    suspend fun deleteGroup4()
    
    fun getTaskOfAlarm(alarmId : Int) : Flow<Task>

    //TaskProfile methods ********************************

    suspend fun getFirstProfileId() : Int?

    fun getProfileTitle(profileId : Int) : Flow<String>
    
    suspend fun insertProfiles(profiles : List<TaskProfile>)

    fun getAllProfiles() : Flow<List<TaskProfile>>

    suspend fun updateProfilesSequentially(direction : Int, profileId : Int) : Int

    suspend fun neatifyProfileOrders()
    
    suspend fun switchProfiles(first : Int, second : Int)

    // Alarm methods ******************************
    
    suspend fun getAlarmWithId(alarmId : Int) : TaskAlarm?
    
    fun getAlarmWithIdFlow(alarmId : Int) : Flow<TaskAlarm>
    
    suspend fun insertAlarmAndReturnId(alarm: TaskAlarm) : Long?

    suspend fun insertAlarmForNewTask(alarm: TaskAlarm)

    suspend fun deleteAlarm(alarm : TaskAlarm)

    fun getAllAlarms(): Flow<List<TaskAlarm>>
    
    fun getAllAlarmsOfProfile(profile : Int): Flow<List<TaskAlarm>>
    
    suspend fun getAllAlarmStatusesAsList(): List<AlarmStatus>

    fun getAlarmsOfTask(idOfTask : Int) : Flow<List<TaskAlarm>>

    suspend fun updateAlarms(alarms : List<TaskAlarm>)

    suspend fun insertAlarms(alarms : List<TaskAlarm>)
    
    suspend fun makeAllAlarmsInactive()
    
    //NOTIF TYPE METHODS *************************************************
    
    suspend fun insertNotifType(notifType: NotifType)
    
    suspend fun insertNotifTypeExactly(notifType: NotifType)
    
    suspend fun getNotifTypeWithId(notifTypeId : Int): NotifType?
    
    fun getAllNotifTypes() : Flow<List<NotifType>>
    
    suspend fun switchNotifTypes(first : Int, second : Int)
    
    suspend fun insertNotifTypes(notifTypes : List<NotifType>)
    
    fun getDefaultNotifTypeOfGroupInProfileAsFlow(idProfile : Int, group : Int, ) : Flow<GroupDefaultNotifType>
    
    suspend fun obtainGroupDefaultNotifTypeForTask(id : Int) : NotifType
    
    suspend fun getAllDefaultNotifTypes() : List<DefaultNotifType>
    
    
}