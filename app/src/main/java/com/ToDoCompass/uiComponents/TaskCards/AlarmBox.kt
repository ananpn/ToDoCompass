package com.ToDoCompass.uiComponents.TaskCards

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ToDoCompass.LogicAndData.Constants.Companion.alarmTimeFormat
import com.ToDoCompass.LogicAndData.LogicConstants.Companion.TASK_NOT_ADDED
import com.ToDoCompass.LogicAndData.LogicConstants.Companion.USE_GROUP_DEFAULT
import com.ToDoCompass.LogicAndData.StringConstants
import com.ToDoCompass.LogicAndData.StringConstants.Companion.ALARM_LIST_TITLE
import com.ToDoCompass.LogicAndData.StringConstants.Companion.CREATE_BUTTON
import com.ToDoCompass.LogicAndData.StringConstants.Companion.SET_REMINDER_BUTTON
import com.ToDoCompass.LogicAndData.TimeFunctions
import com.ToDoCompass.R
import com.ToDoCompass.ViewModels.MainViewModel
import com.ToDoCompass.database.ListItem
import com.ToDoCompass.database.TaskAlarm
import com.ToDoCompass.ui.theme.getOffsetColor
import com.ToDoCompass.uiComponents.Modals.AddAlarmDialog
import com.ToDoCompass.uiComponents.Modals.AlarmListDialog
import kotlinx.coroutines.launch


@Composable
fun  AlarmBox(
    vm : MainViewModel,
    item : ListItem,
)
{
    val alarms by vm.getAlarmsOfTask(item.uniqueId).collectAsState(listOf())
    var defaultNotifTypeId by remember {
        mutableStateOf(USE_GROUP_DEFAULT)
    }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    AlarmBoxContent(
        vm = vm,
        parentId = item.uniqueId,
        alarms = alarms.sortedBy { it -> it.date+it.time },
        defaultNotifTypeId = defaultNotifTypeId,
        parentName = item.title,
        onSave = {
            scope.launch {
                try {
                    vm.insertAndScheduleAlarm(it)
                }
                catch(e : Exception){
                    val toast =
                        Toast.makeText(context, StringConstants.ALARM_ADD_FAIL, Toast.LENGTH_LONG) // in Activity
                    toast.show()
                }
            }
        },
        updateAlarmClicked = {
            vm.openUpdateDialog(entity = it)
        }
    )
}




@Composable
fun AlarmBoxContent(
    vm : MainViewModel,
    parentId : Int,
    parentName : String = "",
    alarms : List<TaskAlarm?>,
    defaultNotifTypeId : Int,
    onSave : (TaskAlarm) -> Unit = {},
    updateAlarmClicked : (TaskAlarm) -> Unit = {}
){
    var addAlarm by rememberSaveable { mutableStateOf(false) }
    var showAlarmList by rememberSaveable { mutableStateOf(false) }
    val validAlarms = alarms.filter { isAlarmValid(it) }
    
    var setReminderText by remember { mutableStateOf(SET_REMINDER_BUTTON) }
    
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(35.dp)
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Row(modifier = Modifier
            .height(35.dp)
            
            .clip(RoundedCornerShape(40))
            .background(color = getOffsetColor().copy(red = 0.98f))
            .clickable(
                onClick = {
                    addAlarm = true
                }
            )
            .padding(horizontal = 7.dp)
            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.baseline_add_alarm_24),
                "",
                modifier = Modifier.offset(x = 0.dp)
            )
            Text(text = setReminderText,
                 style = MaterialTheme.typography.bodySmall.copy(
                     fontSize = 9.5.sp,
                     lineHeight = 11.sp
                 ),
                 minLines = 2,
                 maxLines = 2,
                 textAlign = TextAlign.Center
             )
        }
        Spacer(Modifier.width(5.dp))
        val nextAlarm = validAlarms.firstOrNull()
        Row(modifier = Modifier
            .height(35.dp)
            .clip(RoundedCornerShape(40))
            .background(color = getOffsetColor().copy())
            .clickable(
                onClick = {
                    nextAlarm?.let {
                        updateAlarmClicked(it)
                    }
                }
            )
            .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (nextAlarm == null) {
                true ->
                    Text(
                        text ="No reminders"
                    )
                false -> {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.baseline_alarm_24),
                        "",
                        modifier = Modifier.offset(x = 4.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = formatAlarmTimeToDisplay(nextAlarm.date, nextAlarm.time)
                    )
                }
            }
            
        }
        Spacer(Modifier.width(5.dp))
        if (parentId != TASK_NOT_ADDED){
            Row(modifier = Modifier
                .height(35.dp)
                .clip(RoundedCornerShape(40))
                .background(color = getOffsetColor().copy())
                .clickable(
                    onClick = {
                        showAlarmList = true
                        //
                    }
                )
                .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.MoreVert, "")
                Text(
                    text = " ${validAlarms.filterNotNull().size}"
                )
            }
        }
    }
    
    if (addAlarm){
        AddAlarmDialog(
            vm = vm,
            parentId = parentId,
            defaultNotifTypeId = defaultNotifTypeId,
            label = StringConstants.ADD_ALARM_TITLE+" for task "+parentName,
            confirmButtonText = CREATE_BUTTON,
            onDismiss = {
                addAlarm = false
            },
            onSave = {
                onSave(it)
                addAlarm=false
            }
        )
    }
    
    if (showAlarmList){
        AlarmListDialog(
            vm = vm,
            parentId = parentId,
            label = ALARM_LIST_TITLE+parentName,
            alarms = alarms.filterNotNull(),
            onDismiss = {
                showAlarmList = false
            },
            
            
            )
        
    }

}

fun isAlarmValid(alarm : TaskAlarm?) : Boolean {
    if (alarm == null) return false
    if (!alarm.active) return false
    val time = alarm.date+alarm.time
    return !TimeFunctions.isTimeBeforeCurrentFull(time, alarmTimeFormat)
}

fun isAlarmTimeAfterPresent(alarm : TaskAlarm?) : Boolean {
    if (alarm == null) return false
    val time = alarm.date+alarm.time
    return !TimeFunctions.isTimeBeforeCurrentFull(time, alarmTimeFormat)
}

fun formatAlarmTimeToDisplay(date : String, time : String, separator : String = ", ") : String{
    val MM = date.takeLast(5).take(2).formatMMddDisplay()
    val dd = date.takeLast(2).trim().formatMMddDisplay()
    return dd+"."+MM+separator+time

}

fun String.formatMMddDisplay() : String{
    if (this.substring(0,1) == "0") return (this.replace("0", "")).take(1)
    else return this
}

fun calculateTimeRemaining() {

}