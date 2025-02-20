package com.ToDoCompass.database

import com.ToDoCompass.LogicAndData.StringConstants.Companion.ALARM_TABLE
import com.ToDoCompass.uiComponents.smallComponents.GroupDefaultNotifType
import kotlinx.coroutines.flow.Flow

class OfflineRepository(private val taskDao: TaskDao) : AppRepository {
    
    override suspend fun updateEntity(entity : Any) {
        //Log.v("OfflineRepository updateEntity", "entity = $entity")
        when (entity::class){
            Task::class -> {
                (entity as? Task)?.let{
                    taskDao.updateTask(it)
                }
            }
            TaskProfile::class -> {
                (entity as? TaskProfile)?.let{
                    taskDao.updateProfile(it)
                }
            }
            NotifType::class -> {
                (entity as? NotifType)?.let{
                    taskDao.updateNotifType(it)
                }
            }
            TaskAlarm::class -> {
                
                (entity as? TaskAlarm)?.let{
                    taskDao.updateAlarmDao(it)
                }
            }
            
            DefaultNotifType::class -> {
                (entity as? DefaultNotifType)?.let{
                    taskDao.updateDefaultNotifType(it)
                }
            }
            else -> throw Exception("Error in updateEntity: entity type invalid")
        }
    }
    override suspend fun insertEntity(entity : Any) {
        when (entity::class){
            Task::class -> {
                (entity as? Task)?.let{
                    taskDao.insertTask(it)
                }
            }
            TaskProfile::class -> {
                (entity as? TaskProfile)?.let{
                    taskDao.insertProfile(it)
                }
            }
            NotifType::class -> {
                (entity as? NotifType)?.let{
                    taskDao.insertNotifType(it)
                }
            }
            TaskAlarm::class -> {
                (entity as? TaskAlarm)?.let{
                    taskDao.insertAlarmDao(it)
                }
            }
            DefaultNotifType::class -> {
                (entity as? DefaultNotifType)?.let{
                    taskDao.insertDefaultNotifType(it)
                }
            }
            else -> throw Exception("Error in updateEntity: entity type invalid")
        }
    }
    
    //This deletes the entity totally, including dependents if applicaple
    override suspend fun deleteEntityTotally(entity : Any) {
        when (entity::class){
            Task::class -> {
                (entity as? Task)?.let{
                    taskDao.deleteTaskTotally(it)
                }
            }
            TaskProfile::class -> {
                (entity as? TaskProfile)?.let{
                    taskDao.deleteProfileTotally(it)
                }
            }
            NotifType::class -> {
                (entity as? NotifType)?.let{
                    taskDao.deleteNotifType(it)
                }
            }
            TaskAlarm::class -> {
                (entity as? TaskAlarm)?.let{
                    taskDao.deleteAlarmDao(it)
                }
            }
            DefaultNotifType::class -> {
                (entity as? DefaultNotifType)?.let{
                    throw Exception("Error in deleteEntity: can't delete DefaultNotifType")
                }
            }
            else -> throw Exception("Error in deleteEntity: entity type invalid")
        }
    }
    
    override suspend fun deleteFromTableWithId(id : Int, table : String) {
        when (table){
            ALARM_TABLE -> taskDao.deleteAlarmWithId(id)
            else -> null
        }
    }
    // Task methods ************************************************************
    
    override suspend fun insertNewTask(task : Task, alarm : TaskAlarm?) : Int? = taskDao.insertNewTask(task, alarm)

    override suspend fun getAllTasksProfileRep(profileId : Int): List<Task>
        = taskDao.getAllTasksProfileDao(profileId)
    
    override suspend fun getAllTasksOfProfileAsListItem(profileId : Int) : List<ListItem>
        = taskDao.getAllTasksOfProfileAsListItem(profileId)
    
    override fun getTaskWithId(id: Int): Task? = taskDao.getTaskWithId(id)

    override suspend fun updateTasksSequentially(direction : Int, id : Int, profileId : Int) : Int
        = taskDao.updateTasksSequentially(direction, id, profileId)

    override suspend fun deleteTask(task : Task) = taskDao.deleteTask(task)
    
    override suspend fun updateTaskDone(task : Task, done : Boolean) = taskDao.updateTaskDone(task, done)
    
    //override fun getTaskAlarmType(id : Int) : AlarmType = taskDao.getTaskAlarmType(id)

    override fun getAllTasks() = taskDao.getAllTasksDao()

    override suspend fun updateTasks(tasks : List<Task>)
        = taskDao.updateTasks(tasks)

    override suspend fun insertTasks(tasks : List<Task>)
        = taskDao.insertTasks(tasks)

    override suspend fun upsertTasks(tasks : List<Task>)
            = taskDao.upsertTasks(tasks)

    override suspend fun deleteGroup4()
        = taskDao.deleteTasksFromGroup4()
    
    override fun getTaskOfAlarm(alarmId: Int)
        = taskDao.getTaskOfAlarm(alarmId = alarmId)
/*
    override fun lastDone(profileId : Int, currentDate : String) : Flow<List<LastDoneDate>> =
        taskDao.lastDone(profileId, currentDate)
    */


    //TaskProfile methods **************************************************************

    override suspend fun getFirstProfileId(): Int? = taskDao.getFirstProfile()

    override fun getProfileTitle(profileId : Int) : Flow<String> = taskDao.getProfileTitle(profileId)

    override fun getAllProfiles() : Flow<List<TaskProfile>> = taskDao.getAllProfiles()

    override suspend fun updateProfilesSequentially(direction : Int, profileId : Int) : Int
        = taskDao.updateProfilesSequentially(direction, profileId)

    override suspend fun neatifyProfileOrders() =
        taskDao.neatifyProfileOrders()
    
    
    override suspend fun switchProfiles(first: Int, second: Int) = taskDao.switchProfiles(first, second)
    
    override suspend fun insertProfiles(profiles : List<TaskProfile>) = taskDao.insertProfiles(profiles)

    
    // TaskAlarm methods ************************************************************
    
    override suspend fun getAlarmWithId(alarmId : Int) = taskDao.getAlarmWithId(alarmId)
    
    override fun getAlarmWithIdFlow(alarmId : Int) = taskDao.getAlarmWithIdFlow(alarmId)
    
    override suspend fun insertAlarmAndReturnId(alarm: TaskAlarm) : Long? = taskDao.insertAlarmAndReturnId(alarm)

    override suspend fun insertAlarmForNewTask(alarm: TaskAlarm) = taskDao.insertAlarmForNewTaskDao(alarm)

    override suspend fun deleteAlarm(alarm : TaskAlarm) = taskDao.deleteAlarmDao(alarm)

    override fun getAllAlarms() = taskDao.getAllAlarmsDao()
    
    override fun getAllAlarmsOfProfile(profile : Int) : Flow<List<TaskAlarm>> {
        return taskDao.getAllAlarmsOfProfile(profile)
    }
    
    override suspend fun getAllAlarmStatusesAsList() = taskDao.getAllAlarmStatusesAsList()

    override fun getAlarmsOfTask(idOfTask : Int) = taskDao.getAlarmsOfTaskDao(idOfTask)

    override suspend fun updateAlarms(alarms : List<TaskAlarm>)
            = taskDao.updateAlarms(alarms)

    override suspend fun insertAlarms(alarms : List<TaskAlarm>)
            = taskDao.insertAlarms(alarms)
    
    override suspend fun makeAllAlarmsInactive()
        = taskDao.makeAllAlarmsInactive()
    
    
    //NOTIF TYPE METHODS *************************************************
    
    override suspend fun getNotifTypeWithId(notifTypeId: Int): NotifType?
        = taskDao.getNotifTypeWithId(notifTypeId)
    
    override suspend fun insertNotifType(notifType: NotifType)
        = taskDao.insertNotifType(notifType)
    
    override suspend fun insertNotifTypeExactly(notifType: NotifType)
        = taskDao.insertNotifTypeDao(notifType)
    
    override fun getAllNotifTypes(): Flow<List<NotifType>>
        = taskDao.getAllNotifTypesDao()
    
    
    override suspend fun switchNotifTypes(first: Int, second: Int) = taskDao.switchNotifTypes(first, second)
    
    override suspend fun insertNotifTypes(notifTypes : List<NotifType>) = taskDao.insertNotifTypes(notifTypes)
    
    override fun getDefaultNotifTypeOfGroupInProfileAsFlow(
        idProfile : Int, group : Int,
    ) : Flow<GroupDefaultNotifType>
        = taskDao.getDefaultNotifTypeOfGroupInProfileAsFlow(idProfile = idProfile, group = group)
    
    override suspend fun obtainGroupDefaultNotifTypeForTask(id : Int) : NotifType
        = taskDao.obtainGroupDefaultNotifTypeForTask(id)
    
    override suspend fun getAllDefaultNotifTypes(): List<DefaultNotifType>
        = taskDao.getAllDefaultNotifTypes()
    
}