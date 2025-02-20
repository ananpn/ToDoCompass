package com.ToDoCompass.uiComponents.Modals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ToDoCompass.LogicAndData.LogicConstants.Companion.TASK_NOT_ADDED
import com.ToDoCompass.LogicAndData.LogicConstants.Companion.USE_GROUP_DEFAULT
import com.ToDoCompass.LogicAndData.StringConstants.Companion.ADD_BUTTON
import com.ToDoCompass.LogicAndData.StringConstants.Companion.ADD_TASK
import com.ToDoCompass.LogicAndData.StringConstants.Companion.EMPTY_STRING
import com.ToDoCompass.ViewModels.MainViewModel
import com.ToDoCompass.database.ListItem
import com.ToDoCompass.database.TaskAlarm
import com.ToDoCompass.uiComponents.TaskCards.AlarmBoxContent
import com.ToDoCompass.uiComponents.smallComponents.StringInputField
import kotlinx.coroutines.launch


@Composable
fun AddTaskDialog(
    vm : MainViewModel,
    label : String = ADD_TASK,
    onSave : (ListItem, TaskAlarm?) -> Unit,
    onDismiss : () -> Unit
) {
    //if (vm.openAddNewDialog) doesnt work here since we use this also for adding subtasks
    //  and it happens in MainScreen.kt where it would also open the dialog to add task
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf(EMPTY_STRING) }
    //val alarmsOfTask = remember{ mutableStateListOf<TaskAlarm>()}
    var alarmOfTask by remember{ mutableStateOf<TaskAlarm?>(null) }
    
    DialogWrapper(
        mainLabel = label,
        confirmButtonText = ADD_BUTTON,
        onConfirm = {
            val item = ListItem(
                title = title,
                profile = vm.uiState.dispProfileId,
                group = 4,
                ord = 0,
            )
            onSave(item, alarmOfTask)
        },
        onDismiss = {
            onDismiss()
        },
    ){
        Column{
            StringInputField(
                inVal = title,
                label = ADD_TASK,
                shouldFocus = true,
                onInput = {title = it}
            )
            Spacer(
                modifier = Modifier.height(8.dp)
            )
            //add alarm
            AlarmBoxContent(
                vm = vm,
                parentId = TASK_NOT_ADDED,
                alarms = listOf(alarmOfTask),
                defaultNotifTypeId = USE_GROUP_DEFAULT,
                onSave = {scope.launch{
                    alarmOfTask = it
                    //alarmsOfTask.add(it)
                }},
                updateAlarmClicked = {
                    vm.openUpdateDialog(it)
                }
                
            )
            /*
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
                Text("Add \nreminder",
                     style = MaterialTheme.typography.bodySmall.copy(
                         fontSize = 9.5.sp,
                         lineHeight = 11.sp
                     ),
                     minLines = 2,
                     maxLines = 2,
                     textAlign = TextAlign.Center
                )
                
            }
            */
        }
    }
/*
    if (addAlarm){
        AddAlarmDialog(
            vm = vm,
            parentId = TASK_NOT_ADDED,
            defaultNotifTypeId = NOTIF_TYPE_PARENT_NOT_ADDED,
            label = StringConstants.ADD_ALARM_TITLE,
            confirmButtonText = DONE_BUTTON,
            onDismiss = {
                addAlarm = false
            },
            onSave = {
                alarmOfTask = it
                addAlarm = false
            }
        )
    }
    */
}
