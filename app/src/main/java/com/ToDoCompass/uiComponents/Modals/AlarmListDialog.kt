package com.ToDoCompass.uiComponents.Modals

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.ToDoCompass.LogicAndData.StringConstants.Companion.DONE_BUTTON
import com.ToDoCompass.R
import com.ToDoCompass.ViewModels.MainViewModel
import com.ToDoCompass.database.TaskAlarm
import com.ToDoCompass.ui.theme.getOffsetColor
import com.ToDoCompass.uiComponents.TaskCards.isAlarmValid


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AlarmListDialog(
    vm : MainViewModel,
    parentId : Int,
    alarms : List<TaskAlarm>,
    label : String,
    onDismiss : () -> Unit
) {
    var showEditAlarmDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var alarmToEdit by remember{ mutableStateOf<TaskAlarm?>(null) }
    
    val validAlarms = alarms.filter { isAlarmValid(it) }.sortedBy { it.time }.sortedBy { it.date }
    val inValidAlarms = alarms.filterNot { it in validAlarms }.sortedBy { it.time }.sortedBy { it.date }
    DialogWrapper(
        mainLabel = label,
        confirmButtonText = DONE_BUTTON,
        dismissButtonText = null,
        onConfirm = { onDismiss()}
    ){
        LazyColumn(
        
        ) {
            items(
                validAlarms, {it.alarmId}
            ){alarm ->
                alarmListRow(
                    alarm = alarm,
                    onClicked = {
                        vm.openUpdateDialog(alarm)
                        //showEditAlarmDialog = true
                        //alarmToEdit = alarm
                    },
                    bgColor = getOffsetColor().copy()
                )
            }
            items(
                inValidAlarms, {it.alarmId}
            ){alarm ->
                
                alarmListRow(
                    alarm = alarm,
                    onClicked = {
                        vm.openUpdateDialog(alarm)
                        /*
                        showEditAlarmDialog = true
                        alarmToEdit = alarm
                        */
                        
                    },
                    bgColor = getOffsetColor().copy(alpha = 0.10f)
                )
            }
        }
    }
    /*
    if (showEditAlarmDialog){
        alarmToEdit?.let {
            vm.openUpdateDialog(it)
            EditAlarmDialog(
                vm = vm,
                alarmToUpdate = it,
                onDismiss = {
                    showEditAlarmDialog = false
                },
                onConfirm = {
                    showEditAlarmDialog = false
                }
            )
        }
    }
    */
}

@Composable
fun alarmListRow(
    alarm: TaskAlarm,
    onClicked : () -> Unit,
    bgColor: Color,
){
    
    Row(modifier = Modifier
        .height(35.dp)
        .fillMaxWidth(0.8f)
        .clip(RoundedCornerShape(20))
        .background(color = bgColor)
        .clickable(
            onClick = {
                onClicked()
            }
        )
        .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.baseline_alarm_24),
            "",
            modifier = Modifier.offset(x = 4.dp)
        )
        Spacer(Modifier.width(15.dp))
        Text(
            alarm.date
        )
        Spacer(Modifier.width(15.dp))
        Text(
            alarm.time.take(5)
        )
    }
    Spacer(modifier = Modifier.height(2.dp))
    
}
