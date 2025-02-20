package com.ToDoCompass.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ToDoCompass.LogicAndData.Constants.Companion.defaultNotificationType
import com.ToDoCompass.LogicAndData.StringConstants.Companion.ALARM_TABLE
import com.ToDoCompass.LogicAndData.StringConstants.Companion.DEFAULT_NOTIF_TYPE_TABLE
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NOTIF_TYPE_TABLE
import com.ToDoCompass.LogicAndData.StringConstants.Companion.PROFILE_TABLE
import com.ToDoCompass.LogicAndData.StringConstants.Companion.TASK_TABLE
import com.ToDoCompass.LogicAndData.constructAlarmString
import com.ToDoCompass.uiComponents.TaskCards.isAlarmValid
import com.ToDoCompass.uiComponents.smallComponents.GroupDefaultNotifType
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    // Task methods ******************************

    @Query("SELECT * FROM $TASK_TABLE ORDER BY ord ASC")
    fun getAllTasksDao(): Flow<List<Task>>

    @Query("SELECT * FROM $TASK_TABLE")
    fun getAllTasksAsList(): List<Task>

    @Insert(entity = Task::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskDao(task: Task) : Long
    
    @Transaction
    suspend fun insertNewTask(task : Task, alarm : TaskAlarm?) : Int?{
        val newId = insertTaskDao(task)
        if (alarm != null){
            return insertAlarmAndReturnId(alarm.copy(
                parentId = newId.toInt()
            ))?.toInt()
            
        }
        else return null
    }

    @Transaction
    suspend fun insertTask(task: Task) {
        //val lastOrder = neatifyTaskOrdersSize(task.profile)
        //insertTaskDao(task.copy(ord = lastOrder))
        insertTaskDao(task)
    }

    @Query("SELECT ord FROM $TASK_TABLE WHERE id = :id LIMIT 1")
    fun getTaskOrder(id : Int) : Int
    
/*    @Query("SELECT defaultAlarmType FROM $TASK_TABLE WHERE id = :id LIMIT 1")
    fun getTaskAlarmType(id : Int) : AlarmType*/

    @Query("SELECT * FROM $TASK_TABLE WHERE profile = :profile ORDER BY groupId ASC, ord ASC")
    suspend fun getAllTasksProfileDao(profile: Int): List<Task>
    
    @Transaction
    suspend fun getAllTasksOfProfileAsListItem(profile : Int) : List<ListItem>{
        
        val tasks = getAllTasksProfileDao(profile)
        val alarms = getAllAlarmsOfProfileAsList(profile).filter{
            isAlarmValid(it)
        }
        val output = tasks.map{task ->
            val alarmString = constructAlarmString(
                alarms.filter{it.parentId == task.id}.sortedBy { it.date + it.time }
            )
            task.toListItem(alarmString = alarmString)
        }
        return output
    }

    @Update(entity = Task::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTaskDao(task : Task)

    @Transaction
    suspend fun updateTask(task : Task){
        updateTaskDao(task)
        neatifyTaskOrders(task.profile, task.groupId)
    }

    @Delete(entity = Task::class)
    suspend fun deleteTaskDao(task : Task)

    @Query("DELETE FROM $TASK_TABLE WHERE isChild = 1 AND idOfParent = :idOfTask")
    fun deleteSubTasksOfTask(idOfTask : Int)

    @Transaction
    suspend fun deleteTaskTotally(task : Task){
        deleteAlarmsOfTask(task.id)
        deleteSubTasksOfTask(task.id)
        deleteTask(task)
    }

    @Transaction
    suspend fun deleteTask(task : Task){
        deleteTaskDao(task)
        neatifyTaskOrders(task.profile, task.groupId)
    }
    
    @Transaction
    suspend fun updateTaskDone(task : Task, done : Boolean) {
        updateTask(task.copy(taskDone = done))
        activateAlarmsOfTask(idOfTask = task.id, newActive = !done)
        updateSubTaskDone(idOfParent = task.id, done = done)
    }
    @Query("UPDATE $TASK_TABLE SET taskDone = :done WHERE idOfParent = :idOfParent")
    fun updateSubTaskDone(idOfParent : Int, done : Boolean)
    

    @Query("UPDATE $TASK_TABLE " +
            "SET ord = CASE ord " +
            "WHEN :order THEN -:order-:direction-1 " +
            "WHEN :order+:direction THEN -:order-1 END " +
            "WHERE ord IN (:order+:direction, :order) AND profile = :profile"
    )
    fun updateOrderIds(direction : Int, order : Int, profile : Int)

    @Query("UPDATE $TASK_TABLE " +
            "SET ord = -ord-1 " +
            "WHERE ord < 0"
    )
    fun updateNegativeOrderIds()

    @Transaction
    suspend fun updateTasksSequentially(direction : Int, id : Int, profile : Int) : Int {
        val lastOrder = neatifyTaskOrdersSize(profile)
        val order = getTaskOrder(id)
        if (order+direction in 0..lastOrder) {
            updateOrderIds(direction, order, profile)
            updateNegativeOrderIds()
            return order+direction
        }
        else return order
    }

    @Query("SELECT * FROM $TASK_TABLE WHERE profile = :profile ORDER BY ord")
    fun getTasksByProfile(profile: Int): List<Task>
    
    @Query("SELECT * FROM $TASK_TABLE WHERE id = :id LIMIT 1")
    fun getTaskWithId(id : Int) : Task
    
    @Query("SELECT * FROM $TASK_TABLE WHERE groupId = :group ORDER BY ord")
    fun getTasksByGroup(group: Int): List<Task>
    
    @Query("SELECT * FROM $TASK_TABLE WHERE profile = :profile AND groupId = :group ORDER BY ord")
    fun getTasksByGroupAndProfile(profile : Int, group: Int): List<Task>

    @Update(entity = Task::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTasks(tasks: List<Task>)

    @Transaction
    suspend fun neatifyTaskOrdersSize(profile: Int) : Int {
        val tasks = getTasksByProfile(profile)
        val output = mutableListOf<Task>()
        tasks.forEachIndexed { index : Int, task : Task ->
            output.add(task.copy(ord = index))
        }
        updateTasks(output)
        return output.size
    }

    @Transaction
    suspend fun neatifyTaskOrders(profile : Int, group: Int) {
        val tasks = getTasksByGroupAndProfile(profile, group)
        val output = mutableListOf<Task>()
        tasks.forEachIndexed { index : Int, task : Task ->
            output.add(task.copy(ord = index))
        }
        updateTasks(output)
    }

    @Insert(entity = Task::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<Task>)

    @Transaction
    suspend fun upsertTasks(tasks: List<Task>) {
        val allTasks = getAllTasksAsList()
        for (task in tasks){
            if (task.profile == (allTasks.firstOrNull { it -> it.id == task.id }?.profile
                    ?: false)
            ){
                //Log.v("taskdao upsert", "updating task $task")
                updateTaskDao(task)
            }
            else {
                //Log.v("taskdao upsert", "inserting task $task")
                insertTaskDao(task)
            }
        }
    }

    @Query("DELETE FROM $TASK_TABLE WHERE groupId = 4")
    suspend fun deleteTasksFromGroup4()
    
    @Query("SELECT * FROM $TASK_TABLE " +
               "INNER JOIN $ALARM_TABLE ON $TASK_TABLE.id = $ALARM_TABLE.parentId " +
               "WHERE alarmId = :alarmId LIMIT 1")
    fun getTaskOfAlarm(alarmId : Int) : Flow<Task>


/*
    @Query(
        "SELECT parentId as id, MAX(note) as lastDone FROM $NOTE_TABLE " +
            "INNER JOIN $TASK_TABLE ON $NOTE_TABLE.parentId = $TASK_TABLE.id " +
                "WHERE parentId = $TASK_TABLE.id AND $TASK_TABLE.profile = :profile AND note<=:currentDate AND note>0 PROFILE BY parentId"
    )
    fun lastDone(profile : Int, currentDate : String) : Flow<List<LastDoneDate>>
    */

    // TaskNote methods ******************************
/*
    @Query("UPDATE $NOTE_TABLE " +
            "SET note = (note*:newClickStep/:gcd) " +
            "WHERE parentId = :id")
    suspend fun updateTaskNotesClickStep(newClickStep : Int,
                                          gcd : Int,
                                          id :Int)

    @Query("UPDATE $NOTE_TABLE " +
            "SET note = (note+:increment) " +
            "WHERE parentId = :parentId AND note = :date"
    )
    suspend fun plusOneNoteDao(parentId : Int, increment : Int, date : String)
    */

    @Insert(entity = TaskNote::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNew(taskNote: TaskNote)

    /*
    @Transaction
    suspend fun plusOneNote(parentId : Int, increment : Int, date : String){
        insertNew(TaskNote(parentId = parentId, note = date, note = 0))
        plusOneNoteDao(parentId, increment, date)
    }
    */
/*
    @Update(entity = TaskNote::class, onConflict = OnConflictStrategy.REPLACE)
    fun updateTaskNoteDao(taskNote : TaskNote)


    @Insert(entity = TaskNote::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskNote(taskNote: TaskNote)

    @Query("SELECT note FROM $NOTE_TABLE WHERE parentId = :parentId AND note = :date LIMIT 1")
    fun getNote(parentId : Int, date : String): Flow<Int>

    @Query("SELECT note FROM $NOTE_TABLE WHERE parentId = :parentId AND note = :date LIMIT 1")
    fun getNoteStatic(parentId : Int, date : String): Int?

    @Query("SELECT note, note FROM $NOTE_TABLE WHERE parentId = :parentId AND note IN (:dispDates) PROFILE BY note")
    fun getNoteWeek(parentId : Int, dispDates : List<String>) : Flow<List<NoteOfDate>>

    @Query("SELECT parentId as id, SUM(CAST (note as float) / CAST( $TASK_TABLE.denominator as float)) as total FROM $NOTE_TABLE " +
            "INNER JOIN $TASK_TABLE ON $NOTE_TABLE.parentId = $TASK_TABLE.id " +
            "WHERE note IN (:dispDatesStrings) " +
            "AND parentId = $TASK_TABLE.id AND profile = :profile PROFILE BY parentId")
    fun getAllRowTotals(profile : Int,
                    dispDatesStrings: List<String>) : Flow<List<TotalOfTask>>


    @Query(
        "SELECT SUM(CAST (note as float) / CAST( $TASK_TABLE.denominator as float)) FROM $NOTE_TABLE " +
            "INNER JOIN $TASK_TABLE ON $NOTE_TABLE.parentId = $TASK_TABLE.id " +
                "WHERE note = :date " +
            "AND parentId = $TASK_TABLE.id AND profile = :profile " +
            "LIMIT 1")
    fun getColumnTotal(profile : Int, date: String): Flow<Float>
    */


    //Profile Methods*************************************

    @Query("SELECT idProfile FROM $PROFILE_TABLE ORDER BY profileOrder LIMIT 1")
    suspend fun getFirstProfile() : Int?

    @Query("SELECT profileTitle FROM $PROFILE_TABLE WHERE idProfile = :idG LIMIT 1")
    fun getProfileTitle(idG : Int) : Flow<String>

    @Query("SELECT profileOrder FROM $PROFILE_TABLE WHERE idProfile = :idG LIMIT 1")
    fun getProfileOrder(idG : Int) : Int
    
    @Query("SELECT * FROM $PROFILE_TABLE WHERE profileOrder = :order LIMIT 1")
    fun getProfileFromOrder(order : Int) : TaskProfile

    @Insert(entity = TaskProfile::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfileDao(profile : TaskProfile)
    
    @Insert(entity = TaskProfile::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfiles(profile : List<TaskProfile>)

    @Transaction
    suspend fun insertProfile(profile : TaskProfile) {
        val lastOrder = neatifyProfileOrdersSize()
        insertProfileDao(profile.copy(profileOrder = lastOrder))
    }

    @Delete(entity = TaskProfile::class)
    suspend fun deleteProfile(profile : TaskProfile)

    @Query("SELECT * FROM $PROFILE_TABLE ORDER BY profileOrder ASC")
    suspend fun getAllProfilesAsList() : List<TaskProfile>

    @Query("SELECT * FROM $PROFILE_TABLE ORDER BY profileOrder ASC")
    fun getAllProfiles() : Flow<List<TaskProfile>>

    @Update
    suspend fun updateProfileDao(profile : TaskProfile)

    @Transaction
    suspend fun updateProfile(profile : TaskProfile) {
        updateProfileDao(profile)
        neatifyProfileOrders()
    }

    @Query("UPDATE $PROFILE_TABLE " +
            "SET profileOrder = CASE profileOrder " +
            "WHEN :order THEN -:order-:direction-1 WHEN :order+:direction THEN -:order-1 END " +
            "WHERE profileOrder IN (:order+:direction, :order)")
    fun updateProfileOrders(order : Int, direction : Int)

    @Query("UPDATE $PROFILE_TABLE " +
            "SET profileOrder = -profileOrder-1 " +
            "WHERE profileOrder < 0")
    fun updateNegativeprofileOrders()

    @Transaction
    suspend fun updateProfilesSequentially(direction : Int, profile : Int) : Int {
        val lastOrder = neatifyProfileOrdersSize()
        val profileOrder = getProfileOrder(profile)
        if (profileOrder+direction in 0..lastOrder) {
            updateProfileOrders(profileOrder, direction)
            updateNegativeprofileOrders()
            return profileOrder+direction
        }
        else return profileOrder
    }

    @Query("DELETE FROM $TASK_TABLE WHERE profile = :profile")
    suspend fun deleteTasksOfProfile(profile : Int)
/*


    @Query("DELETE FROM $NOTE_TABLE WHERE parentId IN " +
            "(SELECT id FROM $TASK_TABLE WHERE profile = :profile)")
    suspend fun deleteTaskNotesOfProfile(profile : Int)
*/

    
    @Transaction
    suspend fun deleteProfileTotally(profile : TaskProfile)  {
        //Log.v("taskodao deleteprofiletotally", "deleting profile $profile")
        deleteProfile(profile)
        if (profile.idProfile != null) {
            deleteTasksOfProfile(profile.idProfile)
        }
        neatifyProfileOrders()
    }

    @Update(entity = TaskProfile::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateProfiles(profiles: List<TaskProfile>)

    @Transaction
    suspend fun neatifyProfileOrdersSize() : Int {
        val profiles = getAllProfilesAsList()
        val output = mutableListOf<TaskProfile>()
        profiles.forEachIndexed { index : Int, profile : TaskProfile ->
            output.add(profile.copy(profileOrder = index))
        }
        updateProfiles(output)
        return output.size
    }

    @Transaction
    suspend fun neatifyProfileOrders() {
        val profiles = getAllProfilesAsList()
        val output = mutableListOf<TaskProfile>()
        profiles.forEachIndexed { index : Int, profile : TaskProfile ->
            output.add(profile.copy(profileOrder = index))
        }
        updateProfiles(output)
    }
    
    @Transaction
    suspend fun switchProfiles(first : Int, second : Int){
        val firstProfile = getProfileFromOrder(first)
        val secondProfile = getProfileFromOrder(second)
        insertProfiles(listOf(
            firstProfile.copy(profileOrder = second),
            secondProfile.copy(profileOrder = first)
        ))
    }


    //Alarm Methods*************************************
    
    @Query("SELECT * FROM $ALARM_TABLE WHERE alarmId = :alarmId LIMIT 1")
    suspend fun getAlarmWithId(alarmId : Int): TaskAlarm?
    
    @Query("SELECT * FROM $ALARM_TABLE WHERE alarmId = :alarmId LIMIT 1")
    fun getAlarmWithIdFlow(alarmId : Int): Flow<TaskAlarm>
    
    @Query("SELECT * FROM $ALARM_TABLE")
    fun getAllAlarmsDao(): Flow<List<TaskAlarm>>
    
    @Query("SELECT * FROM $ALARM_TABLE " +
                   "INNER JOIN $TASK_TABLE ON $ALARM_TABLE.parentId = $TASK_TABLE.id " +
                   "WHERE $TASK_TABLE.profile = :profile AND $ALARM_TABLE.parentId == $TASK_TABLE.id")
    fun getAllAlarmsOfProfile(profile : Int): Flow<List<TaskAlarm>>
    
    @Query("SELECT * FROM $ALARM_TABLE " +
                   "INNER JOIN $TASK_TABLE ON $ALARM_TABLE.parentId = $TASK_TABLE.id " +
                   "WHERE $TASK_TABLE.profile = :profile AND $ALARM_TABLE.parentId == $TASK_TABLE.id")
    suspend fun getAllAlarmsOfProfileAsList(profile : Int): List<TaskAlarm>
    
    @Query("SELECT alarmid, active, date, time FROM $ALARM_TABLE")
    suspend fun getAllAlarmStatusesAsList(): List<AlarmStatus>
    
    @Query("SELECT * FROM $ALARM_TABLE")
    suspend fun getAllAlarmsAsList(): List<TaskAlarm>
    
    @Query("SELECT * FROM $ALARM_TABLE WHERE parentId = :idOfTask")
    fun getAlarmsOfTaskDao(idOfTask : Int): Flow<List<TaskAlarm>>

    @Insert(entity = TaskAlarm::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarmAndReturnId(alarm: TaskAlarm) : Long?
    
    @Insert(entity = TaskAlarm::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarmDao(alarm: TaskAlarm)

    @Transaction
    suspend fun insertAlarmForNewTaskDao(alarm: TaskAlarm){
        val tasksInGroup4 = getTasksByGroup(group = 4)
        val newTask = tasksInGroup4.firstOrNull()
        if (newTask != null){
            insertAlarmDao(
                alarm.copy(
                    parentId = newTask.id
                )
            )
        }

    }

    @Update(entity = TaskAlarm::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAlarmDao(alarm : TaskAlarm)

    @Delete(entity = TaskAlarm::class)
    suspend fun deleteAlarmDao(alarm : TaskAlarm)

    @Update
    suspend fun updateAlarms(alarms: List<TaskAlarm>)

    @Insert(entity = TaskAlarm::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarms(alarms: List<TaskAlarm>)

    @Query("DELETE FROM $ALARM_TABLE WHERE parentId = :idOfTask")
    fun deleteAlarmsOfTask(idOfTask : Int)
    
    @Query("UPDATE $ALARM_TABLE SET active = :newActive WHERE parentId = :idOfTask")
    fun activateAlarmsOfTask(idOfTask : Int, newActive : Boolean)
    
    
    //NOTIF TYPE METHODS *************************************************
    
    @Insert(entity = NotifType::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifTypeDao(notifType: NotifType)
    
    @Delete(entity = NotifType::class)
    suspend fun deleteNotifType(notifType: NotifType)
    
    @Query("SELECT * FROM $NOTIF_TYPE_TABLE WHERE notifTypeId = :notifTypeId LIMIT 1")
    suspend fun getNotifTypeWithId(notifTypeId : Int): NotifType?
    
    @Query("SELECT * FROM $NOTIF_TYPE_TABLE")
    fun getAllNotifTypesDao(): Flow<List<NotifType>>
    
    @Insert(entity = NotifType::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifTypes(notifType : List<NotifType>)
    
    
    @Query("SELECT * FROM $NOTIF_TYPE_TABLE WHERE notifTypeOrder = :order LIMIT 1")
    fun getNotifTypeFromOrder(order : Int) : NotifType
    
    @Transaction
    suspend fun switchNotifTypes(first : Int, second : Int){
        val firstNotifType = getNotifTypeFromOrder(first)
        val secondNotifType = getNotifTypeFromOrder(second)
        insertNotifTypes(listOf(
            firstNotifType.copy(notifTypeOrder = second),
            secondNotifType.copy(notifTypeOrder = first)
        ))
    }
    
    @Transaction
    suspend fun insertNotifType(notifType : NotifType) {
        val lastOrder = neatifyNotifTypeOrdersSize()
        insertNotifTypeDao(notifType.copy(notifTypeOrder = lastOrder))
    }
    
    @Transaction
    suspend fun neatifyNotifTypeOrdersSize() : Int {
        val notifTypes = getUserNotifTypesAsList()
        val output = mutableListOf<NotifType>()
        notifTypes.forEachIndexed { index : Int, notifType : NotifType ->
            output.add(notifType.copy(notifTypeOrder = index))
        }
        updateNotifTypes(output)
        return output.size
    }
    
    @Update(entity = NotifType::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNotifTypes(notifTypes: List<NotifType>)
    
    @Update(entity = NotifType::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNotifType(notifType: NotifType)
    
    @Query("SELECT * FROM $NOTIF_TYPE_TABLE WHERE notifTypeId >= 0 ORDER BY notifTypeOrder ASC")
    suspend fun getUserNotifTypesAsList() : List<NotifType>
    
    
    @Insert(entity = DefaultNotifType::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDefaultNotifType(item : DefaultNotifType)
    
    @Update(entity = DefaultNotifType::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateDefaultNotifType(item : DefaultNotifType)
    
    
    @Query("SELECT name, $NOTIF_TYPE_TABLE.notifTypeId as notifTypeId, $DEFAULT_NOTIF_TYPE_TABLE.idForThis as groupDefaultNotifTypeId FROM $NOTIF_TYPE_TABLE " +
                   "INNER JOIN $DEFAULT_NOTIF_TYPE_TABLE " +
                   "ON $NOTIF_TYPE_TABLE.notifTypeId = $DEFAULT_NOTIF_TYPE_TABLE.notifTypeId " +
                   "WHERE $DEFAULT_NOTIF_TYPE_TABLE.idProfile = :idProfile " +
                   "AND $DEFAULT_NOTIF_TYPE_TABLE.groupNumber = :group LIMIT 1"
    )
    fun getDefaultNotifTypeOfGroupInProfileAsFlow(idProfile : Int, group : Int, ) : Flow<GroupDefaultNotifType>
    
    @Query("SELECT name, $NOTIF_TYPE_TABLE.notifTypeId as notifTypeId, $DEFAULT_NOTIF_TYPE_TABLE.idForThis as groupDefaultNotifTypeId FROM $NOTIF_TYPE_TABLE " +
                   "INNER JOIN $DEFAULT_NOTIF_TYPE_TABLE " +
                   "ON $NOTIF_TYPE_TABLE.notifTypeId = $DEFAULT_NOTIF_TYPE_TABLE.notifTypeId " +
                   "WHERE $DEFAULT_NOTIF_TYPE_TABLE.idProfile = :idProfile " +
                   "AND $DEFAULT_NOTIF_TYPE_TABLE.groupNumber = :group LIMIT 1"
    )
    suspend fun getDefaultNotifTypeOfGroupInProfile(idProfile : Int, group : Int, ) : GroupDefaultNotifType
    
    @Transaction
    suspend fun obtainGroupDefaultNotifTypeForTask(id : Int) : NotifType {
        val task = getTaskWithId(id)
        val notifTypeId =  getDefaultNotifTypeOfGroupInProfile(idProfile = task.profile, group = task.groupId).notifTypeId
        var output : NotifType? = null
        notifTypeId?.let{
            output = getNotifTypeWithId(notifTypeId = it)
        }
        output?.let{return it}
        return defaultNotificationType
    }
    
    @Query("SELECT * FROM $DEFAULT_NOTIF_TYPE_TABLE")
    suspend fun getAllDefaultNotifTypes() : List<DefaultNotifType>
    
    
    
    
    //DELETE BY ID **********************
    
    @Query("DELETE FROM $ALARM_TABLE WHERE alarmId = :id")
    suspend fun deleteAlarmWithId(id : Int)
    
    
    
    //TESTING *************************************************
    
    @Query("UPDATE $ALARM_TABLE SET active = 0")
    suspend fun makeAllAlarmsInactive()
    
}