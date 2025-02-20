package com.ToDoCompass.database

import androidx.room.DatabaseView
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ToDoCompass.LogicAndData.StringConstants.Companion.ALARM_TABLE
import com.ToDoCompass.LogicAndData.StringConstants.Companion.DEFAULT_NOTIF_TYPE_TABLE
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NOTE_TABLE
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NOTIF_TYPE_TABLE
import com.ToDoCompass.LogicAndData.StringConstants.Companion.PROFILE_TABLE
import com.ToDoCompass.LogicAndData.StringConstants.Companion.TASK_TABLE

@Entity(tableName = PROFILE_TABLE, indices = [Index(
    value = ["profileOrder"],
    unique = true)])
data class TaskProfile(
    @PrimaryKey(autoGenerate = true)
    val idProfile: Int = 0,
    val profileOrder: Int = 0,
    val profileTitle: String,
)

@Entity(tableName = DEFAULT_NOTIF_TYPE_TABLE, indices = [Index(
    value = ["idProfile", "groupNumber"],
    unique = true)])
data class DefaultNotifType(
    @PrimaryKey(autoGenerate = true)
    val idForThis : Int = 0,
    val idProfile: Int = 0,
    val groupNumber: Int = 0, //0, 1, 2 or 3
    val notifTypeId : Int = 0,
)

//@Parcelize
@Entity(tableName = TASK_TABLE,
    
    indices = [Index(
                    value = ["profile", "groupId", "ord"]
    )]
    
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val profile: Int,
    val groupId : Int = 0, //means number of list
    //val defaultAlarmType : AlarmType = AlarmType.SILENT,
    val ord: Int = 0,
    val title: String,
    val isChild : Boolean = false,
    val idOfParent : Int = -1,
    val childOrd : Int = 0,
    val taskDone : Boolean = false,
    //val taskSize : TaskSize = TaskSize.S //0 = S, 1 = M, 2 = L
) //: Parcelable


@Entity(
    tableName = NOTE_TABLE,
    indices = [Index(
        value = ["parentId"])]
)
data class TaskNote(
    @PrimaryKey
    val idChild: Int? = null, //TaskNote column identifier
    val parentId : Int,
    val note: String
)

@Entity(
    tableName = ALARM_TABLE,
    indices = [Index(
        value = ["parentId"])]
)
data class TaskAlarm(
    @PrimaryKey(autoGenerate = true)
    val alarmId: Int = 0,
    val parentId : Int,
    val notifTypeId : Int,
    val repeatIntervalMin : Int = 0,
    val date : String,
    val time : String,
    val active : Boolean,
    val note : String
)

@Entity(
    tableName = NOTIF_TYPE_TABLE,
    indices = [Index(
        value = ["notifTypeOrder"],
        unique = true)
    ]
)
data class NotifType(
    @PrimaryKey(autoGenerate = true)
    val notifTypeId: Int = 0,
    val notifTypeOrder : Int = 0,
    val name : String,
    val soundUriString : String,
    val soundFileName : String = "",
    val vibrationPatternString: String,
    val persistentLength : Int,
    val rampUp : Boolean,
    val respectSystem : Boolean = false
)

@DatabaseView()
data class TaskAlarmFront(
    val idChild : Int,
    val parentId : Int,
    val date : String,
    val time : String,
    val active : Boolean,
    val note : String,
    val title : String,
)

data class NoteOfDate(
    val date : String,
    val note : Int
)

data class StringOfDate(
    val date : String,
    val dispNote : String
)


data class TotalOfTask(
    val id : Int?,
    val total: Float?
)

data class LastDoneDate(
    val id : Int?,
    val lastDone: String?
)


/*@DatabaseView(
    "SELECT idTC, taskId, date, note FROM task_notes "
)
data class TaskNoteView(
    val idTC: Int,
    val taskId : Int,
    val date: String,
    val note: Int
)*/



data class DateNoteWithProfileId(
    val date: String,
    val profileId: Int,
    val sum: Float
)


// TODO make Room return tasks directly in this type? Get the alarm data directly in room?
data class ListItem (
    val uniqueId : Int = 0,
    val profile : Int = 0,
    val group : Int = 0,
    val ord : Int,
    val title : String,
    //val isDrawn : Boolean = true,
    val isChild : Boolean = false,
    val idOfParent : Int = -1,
    val childOrd : Int = 0,
    val taskDone : Boolean = false,
    val alarmString : String = "",
)

fun ListItem.toTaskNoId() : Task =
    Task(
        profile = profile,
        groupId = group,
        ord = ord,
        title = title,
        isChild = isChild,
        idOfParent = idOfParent,
        childOrd = childOrd,
        taskDone = taskDone
    )

fun ListItem.toTask() : Task =
    Task(
        id = uniqueId,
        profile = profile,
        groupId = group,
        ord = ord,
        title = title,
        isChild = isChild,
        idOfParent = idOfParent,
        childOrd = childOrd,
        taskDone = taskDone
    )



fun Task.toListItem(alarmString : String = "") : ListItem =
    ListItem(
        uniqueId = id,
        profile = profile,
        group = groupId,
        ord = ord,
        title = title,
        isChild = isChild,
        idOfParent = idOfParent,
        childOrd = childOrd,
        taskDone = taskDone,
        alarmString = alarmString
    )



/*

@DatabaseView(
    value = "SELECT uniqueId, profile, groupId, ord, title, isChild, idOfParent FROM $TASK_TABLE",
    viewName = LIST_ITEM_VIEW
)

data class ListItemFront (
    @ColumnInfo("uniqueId")
    val uniqueId : Int = 0,
    @ColumnInfo("profile")
    val profile : Int = 0,
    @ColumnInfo("groupId")
    val group : Int = 0,
    @ColumnInfo("ord")
    val ord : Int,
    @ColumnInfo("title")
    val title : String,
    val isDrawn : Boolean = true,
    @ColumnInfo("isChild")
    val isChild : Boolean = false,
    @ColumnInfo("idOfParent")
    val idOfParent : Int = -1
)
*/

enum class AlarmType{
    SILENT,
    GENTLE,
    MAX
}

data class AlarmStatus(
    val alarmId : Int,
    val active : Boolean,
    val date : String,
    val time : String
)

enum class TaskSize{
    S,
    M,
    L,
}


//TODO make maybe Room return this type when sending notification??
data class AlarmTotalData(
    val alarm : TaskAlarm,
    val taskParent : Task,
    val notifType : NotifType
)
