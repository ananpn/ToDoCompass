package com.ToDoCompass.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ToDoCompass.LogicAndData.Constants
import com.ToDoCompass.LogicAndData.Constants.Companion.defaultAlarmNotificationType
import com.ToDoCompass.LogicAndData.Constants.Companion.emptyListItem
import com.ToDoCompass.LogicAndData.Constants.Companion.emptyProfile
import com.ToDoCompass.LogicAndData.Constants.Companion.emptyTask
import com.ToDoCompass.LogicAndData.Constants.Companion.silentNotificationType
import com.ToDoCompass.LogicAndData.Constants.Companion.useGroupDefaultNotificationType
import com.ToDoCompass.LogicAndData.LogicConstants.Companion.DEFAULT_NOTIF_TYPE_NOT_FOUND
import com.ToDoCompass.LogicAndData.LogicConstants.Companion.NOTIF_TYPE_PARENT_NOT_ADDED
import com.ToDoCompass.LogicAndData.LogicConstants.Companion.NOTIF_TYPE_PARENT_NOT_FOUND
import com.ToDoCompass.LogicAndData.StringConstants.Companion.EMPTY_STRING
import com.ToDoCompass.LogicAndData.VibrationPatternData
import com.ToDoCompass.LogicAndData.swapList
import com.ToDoCompass.LogicAndData.toStringForStore
import com.ToDoCompass.LogicAndData.toVibrationPatternData
import com.ToDoCompass.database.AppRepository
import com.ToDoCompass.database.DefaultNotifType
import com.ToDoCompass.database.ListItem
import com.ToDoCompass.database.NotifType
import com.ToDoCompass.database.Task
import com.ToDoCompass.database.TaskAlarm
import com.ToDoCompass.database.TaskProfile
import com.ToDoCompass.database.toTask
import com.ToDoCompass.di.PrefsImpl
import com.ToDoCompass.uiComponents.smallComponents.GroupDefaultNotifType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: AppRepository,
    private val prefs: PrefsImpl,
    private val worDep: WorkerDependency
) : ViewModel() {
    
    
    val player = worDep.player
    
    //Notification Types *********************************************************************
    
    suspend fun updateEntityInDB(entity : Any){
        //Log.v("vm updateentity inDB", "entity = $entity")
        repo.updateEntity(entity)
    }
    
    suspend fun insertEntityToDB(entity : Any){
        repo.insertEntity(entity)
    }
    
    suspend fun deleteEntityInDBTotally(entity : Any){
        repo.deleteEntityTotally(entity)
    }
    
    val _dbNotifTypes = repo.getAllNotifTypes()
    var notifTypes : SnapshotStateList<NotifType> = SnapshotStateList()
    
    suspend fun saveNotifTypesToDB() {
        repo.insertNotifTypes(notifTypes.toList())
    }
    
    suspend fun updateNotifTypesFromDB() {
        notifTypes.swapList(
            _dbNotifTypes.firstOrNull() ?:listOf()
        )
    }
    
    fun getNotifTypesFromRepo() : Flow<List<NotifType>> {
        return repo.getAllNotifTypes()
    }
    
    fun switchNotifTypes(first : Int, second : Int) = viewModelScope.launch{
        val newNotifTypes = notifTypes.map{item ->
            when (item.notifTypeOrder) {
                first -> item.copy(notifTypeOrder = second)
                second -> item.copy(notifTypeOrder = first)
                else -> item
            }
        }
        notifTypes.swapList(newNotifTypes)
    }
    
    fun getDefaultNotifTypeOfGroupInProfile(idProfile : Int, group : Int) : Flow<GroupDefaultNotifType>{
        return repo.getDefaultNotifTypeOfGroupInProfileAsFlow(idProfile = idProfile, group = group)
    }
    
    suspend fun getNotifTypeIdDefaultOfTask(taskId : Int) : Int {
        if (taskId == -1){
            return NOTIF_TYPE_PARENT_NOT_ADDED
        }
        val task = data.filter { it.uniqueId == taskId }.firstOrNull()
            ?:subData.filter { it.uniqueId == taskId }.firstOrNull()
        if (task==null){
            return NOTIF_TYPE_PARENT_NOT_FOUND
        }
        else {
            val defNotifTypeFlow = getDefaultNotifTypeOfGroupInProfile(task.profile, task.group)
            var output : Int? = null
            output = defNotifTypeFlow.firstOrNull()?.notifTypeId
            var count = 0
            while (output == null && count < 30){
                count++
                delay(20)
            }
            output?.let {
                return it
            }
            return DEFAULT_NOTIF_TYPE_NOT_FOUND
        }
    }
    
    fun checkDefaultNotifTypes() = viewModelScope.launch{
        val allDefaultNotifTypes = repo.getAllDefaultNotifTypes()
        profiles.forEach{profile ->
            //Log.v("checkDefaultNotifTypes", "profile = $profile")
            for (groupNumber in 0..3){
                allDefaultNotifTypes.firstOrNull {
                    it.idProfile == profile.idProfile
                            && it.groupNumber == groupNumber
                } ?:repo.insertEntity(
                    DefaultNotifType(
                        idProfile = profile.idProfile,
                        groupNumber = groupNumber,
                        notifTypeId = -1,
                    )
                )
            }
        }
        if (notifTypes.none{it.notifTypeOrder == -2})
            repo.insertNotifTypeExactly(silentNotificationType)
        if (notifTypes.none { it.notifTypeOrder == -1 })
            repo.insertNotifTypeExactly(defaultAlarmNotificationType)
        if (notifTypes.none { it.notifTypeOrder == -3 })
            repo.insertNotifTypeExactly(useGroupDefaultNotificationType)
    }
    
    
    
    //UI State ***************************************************
    var uiState by mutableStateOf(UiState())

    fun setUiState(
        dispProfileId : Int = uiState.dispProfileId,
        //dispDates : List<LocalDate> = uiState.dispDates,
        taskCardListItem : ListItem = uiState.taskCardListItem,
        upProfile : TaskProfile = uiState.upProfile,
        selectProfile : Boolean = uiState.selectProfile,
    ) {
        uiState = uiState.copy(
            dispProfileId = dispProfileId,
            //dispDates =  dispDates,
            taskCardListItem = taskCardListItem,
            upProfile = upProfile,
            selectProfile = selectProfile,
        )
    }

    var allData : SnapshotStateList<ListItem> = SnapshotStateList()
    var data : SnapshotStateList<ListItem> = SnapshotStateList()
    var subData : SnapshotStateList<ListItem> = SnapshotStateList()
    //var taskData : SnapshotStateList<Task> = SnapshotStateList()

    //val _dbData = repo.getAllTasksProfileRep(uiState.dispProfileId)//ProfileRep(uiState.dispProfileId)

    suspend fun updateDataFromDB() {
        //Log.v("vm", "updateDataFromDB")
        /*
        taskData.swapList(
            _dbData.firstOrNull()
                ?.filter{it.profile == uiState.dispProfileId} ?:listOf()
        )
        val allData = taskData.map{task ->
            task.toListItem()
        }
        */
        /*
        val allData = repo.getAllTasksProfileRep(uiState.dispProfileId).map{ task ->
            val alarmsOfTask = alarms.firstOrNull()?.filter{ isAlarmValid(it) && it.parentId == task.id} ?:listOf()
            val alarmString = constructAlarmString(alarmsOfTask.sortedBy { it.date+it.time })
            task.toListItem(alarmString)
        } ?:listOf()*/
        // TODO is it better to have val allData here or have it as a variable? What if variable is changed to snapshotstatelist or nulled afterwards or smth
        allData.swapList(repo.getAllTasksOfProfileAsListItem(uiState.dispProfileId))
        data.swapList(allData.filter{!it.isChild && !it.taskDone})
        subData.swapList(allData.filter{it.isChild})
    }

    fun saveDataToDB() = viewModelScope.launch{
        //Log.v("vm", "saveDataToDB")
        data.removeIf{it.group == 4}
        repo.insertTasks(data.toList().map{item -> item.toTask()})
    }




    var isItemMovingGroup : Boolean = false
    var groupMoveState = GroupMoveState(
        false,
        false,
        movedItem = emptyListItem, 0,0,0
    )

    fun findItemFromData(itemKey : Int) : ListItem {
        return data.filter{item -> item.uniqueId == itemKey}.firstOrNull() ?:emptyListItem
    }

    fun itemMoveStage1(movedItem : ListItem,
                       targetGroup : Int,
                       index :Int
    ) = viewModelScope.launch {
        //Log.v("vm itemmovestage 1", "launched")
        val newItem = addItemToDataAtIndex(
            group = targetGroup,
            new = movedItem,
            index = index
        )
        //sortData()
        groupMoveState = GroupMoveState(
            moveStage1Done = true,
            moveStage2Done = false,
            movedItem = newItem,
            itemKey = newItem.uniqueId,
            fromGroup =movedItem.group,
            toGroup = targetGroup
        )
    }

    fun itemMoveFinalize(index : Int = 0){
        groupMoveState = groupMoveState.copy(moveStage1Done = false)
    }

    fun switchItemsInDataByIndex(group : Int, first : Int, second : Int){
        val newdata = data.map{item ->
            if (item.ord == first && item.group == group)
                item.copy(ord = second)
            else if (item.ord == second && item.group == group)
                item.copy(ord = first)
            else item
        }
        data.swapList(newdata)
    }

    private fun addItemToDataAtIndex(group : Int, index : Int, new : ListItem) : ListItem {
        data.removeIf{item -> item.uniqueId == new.uniqueId}
        data.add(new.copy(group = group))
        val newList = data.map{item ->
            if (item.group == group) {
                if (item.uniqueId != new.uniqueId) {
                    if (item.ord >= index) {
                        item.copy(ord = item.ord + 1)

                    }
                    else item
                }
                else {
                    item.copy(ord = index)
                }
            }
            else item
        }
        data.swapList(newList)
        return data.firstOrNull()
            {it.uniqueId == groupMoveState.movedItem.uniqueId}
                ?:groupMoveState.movedItem
    }

    fun sortData(){
        var newData = mutableListOf<ListItem>()
        for (group in 0..4){
            val dataInGroup = data.filter { it.group == group }.sortedBy { it.ord }
            dataInGroup.forEachIndexed{ index, item ->
                newData.add(item.copy(ord = index))
            }
        }
        data.swapList(newData)
    }

    fun cancelAdding(){
        data.removeIf{it.group == 4}
        deleteGroup4()
    }

    fun addItem(item : ListItem) = viewModelScope.launch {
        data.removeIf{it.group == 4}
        insertEntityToDB(item.toTask())
        updateDataFromDB()
    }
    
    

    //Displayed profile *********************************************************************

    fun setDispProfile(dispProfileId: Int) {
        viewModelScope.launch {
            prefs.saveDispProfileId(dispProfileId)
            setUiState(dispProfileId = dispProfileId)
        }
    }

    
    val dispProfileId = prefs.dispProfileId
    
    //val alarms = dispProfileId.flatMapLatest { repo.getAllAlarmsOfProfile(it). }
    //val _alarms = repo.getAllAlarms()
    //val alarms = _alarms.value

    fun updateDispProfileId() = viewModelScope.launch {
        try {
            setUiState(dispProfileId = dispProfileId.firstOrNull() ?:1)
        } catch (e: Exception) {
            setUiState(dispProfileId = 1)
        }
        updateDataFromDB()
    }
    
    // Task methods ************************************************************
    
    suspend fun insertNewTask(
        task: Task? = null,
        item: ListItem? = null,
        alarm: TaskAlarm? = null
    ) {
        data.removeIf{it.group == 4}
        if (task != null || item != null) {
            var taskToAdd = emptyTask
            if (task != null) {
                taskToAdd = task
            } else if (item != null) {
                taskToAdd = item.toTask()
            }
            if (taskToAdd.isChild){
                taskToAdd = taskToAdd.copy(
                    childOrd = subTasksLastIndex+1
                )
            }
            val alarmId = repo.insertNewTask(
                task = taskToAdd,
                alarm = alarm
            )
            alarm?.let {_alarm ->
                alarmId?.let {_alarmId ->
                    worDep.scheduleAlarm(_alarm.copy(alarmId = _alarmId))
                }
            }
        }
        delay(10)
        updateDataAndSubTasksFromDB()
    }
    
    fun updateTaskAndData(task: Task) = viewModelScope.launch {
        updateEntityInDB(task).also{
            updateDataAndSubTasksFromDB()
        }
    }
    
    suspend fun updateTasksSequentially(direction: Int, id: Int, profile: Int) : Int {
        var output = -1
        viewModelScope.launch {
            output = repo.updateTasksSequentially(direction, id, profile)
        }
        while(output<0){
            delay(30)
        }
        return output
    }
    
    fun deleteTask(task: Task) = viewModelScope.launch {
        repo.deleteTask(task)
    }
    
    
    fun updateTaskDone(task : Task, done : Boolean = true) = viewModelScope.launch {
        //This also modifies subtasks and alarms accordingly
        repo.updateTaskDone(task, done)
    }
    
    fun deleteGroup4() = viewModelScope.launch {
        repo.deleteGroup4()
    }
    

    //SubTasks ***************************************************************************

    val subTasks : SnapshotStateList<ListItem> = SnapshotStateList()
    var subTasksLastIndex = subTasks.maxOfOrNull { it.childOrd } ?:-1
    /*
    fun moveSubTask(from: ItemPosition, to: ItemPosition) {
        subTasks = subTasks.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }
    */

    fun switchSubTasks(first : Int, second : Int){
        val newdata = subTasks.map{item ->
            if (item.childOrd == first)
                item.copy(childOrd = second)
            else if (item.childOrd == second)
                item.copy(childOrd = first)
            else item
        }
        subTasks.swapList(newdata)
        //updateSubTasks()
    }

    fun addSubTask(item : ListItem) = viewModelScope.launch {
        insertEntityToDB(item.copy(
            childOrd = subTasksLastIndex+1
        ).toTask())
        
    }

    fun updateSubTasks(){
        subTasks.swapList(subData.filter{it.idOfParent == uiState.taskCardListItem.uniqueId})
        sortSubTasks()
        subTasksLastIndex = subTasks.maxOfOrNull { it.childOrd } ?:-1
    }

    suspend fun updateDataAndSubTasksFromDB() {
        updateDataFromDB()
        updateSubTasks()
    }
    
    fun updateEveryThingFromDB() = viewModelScope.launch {
        updateProfilesFromDB()
        updateDataAndSubTasksFromDB()
    }

    fun sortSubTasks(){
        val newData = subTasks.sortedBy { it.childOrd }
            .mapIndexed{ index,it,->
                it.copy(childOrd = index)
            }
        subTasks.swapList(newData)
    }

    fun saveSubTasksToDB()= viewModelScope.launch{
        //Log.v("vm savesubtaskstodb", "launch")
        //Log.v("vm savesubtaskstodb", "subTasks = ${subTasks.toList().map{it -> it.uniqueId}}")
        repo.insertTasks(subTasks.toList().map{item -> item.toTask()})
    }

    //Alarms ********************************************************************************
    
    

    suspend fun insertAndScheduleAlarm(alarm: TaskAlarm) {
        val newAlarmId = repo.insertAlarmAndReturnId(alarm)
        if (newAlarmId != null){
            worDep.scheduleAlarm(alarm.copy(
                alarmId = newAlarmId.toInt()
            ))
        }
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
            worDep.scheduleAlarmWithCheck(it)
            
        }
        
    }
    
    fun getTaskOfAlarm(alarmId : Int) : Flow<Task> {
        return repo.getTaskOfAlarm(alarmId)
    }
    
    fun getAlarmWithIdFlow(alarmId : Int) : Flow<TaskAlarm> {
        return repo.getAlarmWithIdFlow(alarmId)
    }

    suspend fun insertAlarmForNewTask(alarm: TaskAlarm) {
        repo.insertAlarmForNewTask(alarm)
    }

    fun getAlarmsOfTask(idOfTask : Int) : Flow<List<TaskAlarm>>{
        return repo.getAlarmsOfTask(idOfTask)
    }

    suspend fun deleteAlarm(alarm: TaskAlarm) = viewModelScope.launch {
        repo.deleteAlarm(alarm)
    }
    
    fun checkAlarms() = viewModelScope.launch{
        worDep.checkAlarms()
    }
    
    fun scheduleAlarm(alarm : TaskAlarm) {
        worDep.scheduleAlarm(alarm)
    }


    //Flows from DB ********************************************************************************

    //val tasks = dispProfileId.flatMapLatest { dispProfileId -> repo.getAllTasksProfileRep(dispProfileId) }

    val profileTitleFlow = dispProfileId.flatMapLatest { dispProfileId -> repo.getProfileTitle(dispProfileId) }
    





    /*
    fun lastDone(groupId : Int, currentDate : String) : Flow<List<LastDoneDate>> {
        return repo.lastDone(groupId, currentDate)
    }
    */

    //Profile methods **************************
    
    val _dbProfiles = repo.getAllProfiles()
    var profiles : SnapshotStateList<TaskProfile> = SnapshotStateList()
    
    suspend fun saveProfilesToDB() {
        //Log.v("vm", "saveDataToDB")
        repo.insertProfiles(profiles.toList())
    }
    
    suspend fun updateProfilesFromDB() {
        //Log.v("vm", "updateDataFromDB")
        profiles.swapList(
            _dbProfiles.firstOrNull() ?:listOf()
        )
    }
    
    fun setFirstProfileId() = viewModelScope.launch {
        val firstProfileId: Int? = repo.getFirstProfileId()
        if (firstProfileId != null) {
            setDispProfile(firstProfileId)
        } else setDispProfile(0)
    }
    
    fun switchProfiles(first : Int, second : Int) = viewModelScope.launch{
        val newProfiles = profiles.map{item ->
            if (item.profileOrder == first)
                item.copy(profileOrder = second)
            else if (item.profileOrder == second)
                item.copy(profileOrder = first)
            else item
        }
        profiles.swapList(newProfiles)
    }

    suspend fun updateProfilesSequentially(direction: Int, profileId: Int): Int {
        var output = -1
        viewModelScope.launch{
            output = repo.updateProfilesSequentially(direction, profileId)
        }
        while(output<0){
            delay(30)
        }
        return output
    }

    fun getProfileTitle(profileId : Int) : Flow<String> {
        return repo.getProfileTitle(profileId)
    }



    //Global booleans******************************************************************************

    var openAddNewDialog by mutableStateOf(false)
    fun openAddNewDialog() { openAddNewDialog = true }
    fun closeAddNewDialog() { openAddNewDialog = false }
    
    var openNewProfileDialog by mutableStateOf(false)
    fun openNewProfileDia() { openNewProfileDialog = true }
    fun closeNewProfileDia() { openNewProfileDialog = false }


    var openUpdateDialog by mutableStateOf(false)
    var entityToUpdate : Any = mutableStateOf(null)
    
    fun openUpdateDialog(entity : Any) {
        fun operationOnEntity(it : Any){
            run{entityToUpdate = it}.also{
                //Opens com/ToDoCompass/uiComponents/Modals/EditDialogSuper.kt
                openUpdateDialog = true
            }
        }
        when (entity::class){
            Task::class -> {
                (entity as? Task)?.let{
                    operationOnEntity(it)
                }
            }
            TaskProfile::class -> {
                (entity as? TaskProfile)?.let{
                    operationOnEntity(it)
                }
            }
            NotifType::class -> {
                (entity as? NotifType)?.let{
                    operationOnEntity(it)
                }
            }
            TaskAlarm::class -> {
                (entity as? TaskAlarm)?.let{
                    operationOnEntity(it)
                }
            }
            else -> throw Exception("Error in mainViewModel.openUpdateDialog: entity type invalid")
        }
    }
    
    fun closeUpdateDialog() { openUpdateDialog = false }
    
    var openDeleteDialog by mutableStateOf(false)
    var entityToDelete : Any = mutableStateOf(null)
    var deleteInfo : String = ""
    
    fun openDeleteDialog(entity : Any, info : String = "") {
        fun operationOnEntity(it : Any){
            run{entityToDelete = it}.also{
                deleteInfo = info
                //Opens com/ToDoCompass/uiComponents/Modals/EditDialogSuper.kt
                openDeleteDialog = true
            }
        }
        when (entity::class){
            Task::class -> {
                (entity as? Task)?.let{
                    operationOnEntity(it)
                }
            }
            TaskProfile::class -> {
                (entity as? TaskProfile)?.let{
                    operationOnEntity(it)
                }
            }
            NotifType::class -> {
                (entity as? NotifType)?.let{
                    operationOnEntity(it)
                }
            }
            TaskAlarm::class -> {
                (entity as? TaskAlarm)?.let{
                    operationOnEntity(it)
                }
            }
            else -> throw Exception("Error in mainViewModel.openDeleteDialog: entity type invalid")
        }
    }
    
    fun closeDeleteDialog() { openDeleteDialog = false }

    
    var showingTaskCard by mutableStateOf(false)
    fun openTaskCard(item : ListItem){
        if (item != emptyListItem){
            setUiState(taskCardListItem = item)
            showingTaskCard = true
        }
    }
    fun closeTaskCard() = viewModelScope.launch{
        //save subtasks to DB
        //repo.insertTasks(subTasks.toList().map{item -> item.toTask()})
        //delay(50) //delay probably not needed
        showingTaskCard = false
        showingSubTaskCard = false
        subTasks.swapList(listOf())
    }
    
    var showingSubTaskCard by mutableStateOf(false)
    
    var showingGroupEditCard by mutableStateOf(false)
    var groupToEdit by mutableStateOf(0)
    fun openGroupEditCard(groupClicked : Int){
        groupToEdit = groupClicked
        showingGroupEditCard = true
    }
    fun closeGroupEditCard() = viewModelScope.launch{
        //save subGroupEdits to DB
        //repo.insertGroupEdits(subGroupEdits.toList().map{item -> item.toGroupEdit()})
        //delay(50) //delay probably not needed
        showingGroupEditCard = false
    }
    
    

    //Theme and colors ****************************************************************************
    
    val appSettingsFlow = prefs.appSettingsDataFlow
    
    var defaultBGColor = Color.Transparent
    var modifiedBGColor = Color.Transparent

    fun setBGColors(newDef : Color, newMod : Color){
        defaultBGColor = newDef
        modifiedBGColor = newMod
    }

    fun setDarkMode(isNightMode: Boolean) =viewModelScope.launch {
        prefs.saveDarkTheme(isNightMode)
    }
    
    fun setSeedColor(newSliderFloat : Float) = viewModelScope.launch {
        prefs.saveSeedColorData(newSliderFloat)
    }

    fun setPalette(newPaletteIn : Int) = viewModelScope.launch {
        prefs.savePaletteData(newPaletteIn)
    }
    
    //Other Settings *********************************************************************
    
    //val dataFlow = worDep.dataFlow
    val customVibrationPatternFlow = prefs.customVibPattern.map{
        it.toVibrationPatternData()
    }
    
    
    fun saveCustomVibrationPattern(new : VibrationPatternData) = viewModelScope.launch {
        prefs.saveCustomVibPattern(new.toStringForStore())
    }
    

    
    

    //Global variables *********************************************************************

    var screenWidthDp = 0.dp
    var screenHeightDp = 0.dp
    var boxWidth8 = (screenWidthDp/(Constants.firstColumnWeight+7f))
    var screenWidthPx = 0f

    fun updateScreenSize(screenWidthInput : Dp, screenWidthInputPx : Float, screenHeightInput : Dp){
        screenWidthDp = screenWidthInput
        screenHeightDp = screenHeightInput
        screenWidthPx = screenWidthInputPx
        boxWidth8 = (screenWidthDp/(Constants.firstColumnWeight +7f))
    }

    //Preferences *********************************************************************
/*

    private val _rowTapMode = MutableStateFlow(prefs.rowTapMode)
    val rowTapMode = _rowTapMode.value
    fun updateRowTapMode(){
        _rowTapMode.value = prefs.rowTapMode
    }

    fun setRowTapMode(isNightMode: Boolean) =viewModelScope.launch {
        prefs.saveRowTapMode(isNightMode)
    }

    fun rowTapModeGet() : Boolean {
        updateRowTapMode()
        var output = false
        viewModelScope.launch {
            val rowTapMode2 = _rowTapMode.value
            try {
                output = rowTapMode2.first()
            }
            catch (e : Exception){
            }
        }
        return output
    }
*/


    fun testNotification(){
        worDep.testReceivedAlarmWork()
    }
    
    fun testScheduleAlarmWork(){
        worDep.testScheduleAlarmWork()
        
    }
    
    fun dismissNotification(requestCode: Int?){
        requestCode?.let{worDep.notifDismissWork(it)}
    }
    
    fun notifDeleteWork(){
        worDep.notifDeleteWork()
    }
    
    fun makeAllAlarmsInactive() = viewModelScope.launch{
        repo.makeAllAlarmsInactive()
    }
    
    fun cancelAllAlarms() {
        worDep.cancelAllAlarms()
    }
    
    fun startCheckWorker(){
        worDep.startCheckWorker()
    }



//UiState ***************************************************************************************



/*
fun nextWeek(){
    setUiState(dispDates = generateWeek(uiState.dispDates[0].plusDays(7)))
}

fun prevWeek(){
    setUiState(dispDates = generateWeek(uiState.dispDates[0].minusDays(7)))
}
   */

data class UiState(
    //val dispDates : List<LocalDate> = Constants.currentWeek,
    val dispProfileId : Int = 1,
    val profileTitle : String = EMPTY_STRING,
    val taskCardListItem : ListItem = emptyListItem,
    val upProfile : TaskProfile = emptyProfile,
    val selectProfile : Boolean = false,
)

data class GroupMoveState(
    val moveStage1Done : Boolean = false,
    val moveStage2Done : Boolean = false,
    val movedItem : ListItem = emptyListItem,
    val itemKey : Int = movedItem.uniqueId,
    val fromGroup : Int = movedItem.group,
    val toGroup : Int
    )

fun setGroupMoveState(
    moveStage1Done : Boolean = groupMoveState.moveStage1Done,
    moveStage2Done : Boolean = groupMoveState.moveStage2Done,
    movedItem : ListItem = groupMoveState.movedItem,
    itemKey : Int = groupMoveState.movedItem.uniqueId,
    fromGroup : Int = groupMoveState.movedItem.group,
    toGroup : Int = groupMoveState.toGroup
){
        groupMoveState = groupMoveState.copy(
            moveStage1Done = moveStage1Done,
            moveStage2Done = moveStage2Done,
            movedItem = movedItem,
            itemKey = itemKey,
            fromGroup =fromGroup,
            toGroup = toGroup
        )
    }

data class ColumnBoundaries(
    val xLeft : Int = 0,
    val xCenter : Int = 0,
    val xRight : Int = 2 * xCenter,
    val yTop : Int = 0,
    val yCenter : Int = 0,
    val yBottom : Int = 2 * yCenter,
    )
}


/*

    fun addItemToData(group : Int, index : Int, new : ListItem) : ListItem{
        setGroupMoveState(
            movedItem = new.copy(group = group)
        )
        return addItemToDataAtIndex(
            new = new,
            group = group,
            index = index
        )
        sortData()

    }

    fun addItemToDB(item : ListItem) = viewModelScope.launch{
        insertTask(item.toTaskNoId()) //no id set to make Room generate it
        delay(20)
        updateDataFromDB()
    }
    */




