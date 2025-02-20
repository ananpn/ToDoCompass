package com.ToDoCompass.uiComponents.Modals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.ToDoCompass.LogicAndData.LogicConstants.Companion.TASK_NOT_ADDED
import com.ToDoCompass.LogicAndData.StringConstants
import com.ToDoCompass.LogicAndData.StringConstants.Companion.ALARM_NOTE_LABEL
import com.ToDoCompass.LogicAndData.StringConstants.Companion.CANCEL_BUTTON
import com.ToDoCompass.LogicAndData.StringConstants.Companion.EMPTY_STRING
import com.ToDoCompass.R
import com.ToDoCompass.ViewModels.MainViewModel
import com.ToDoCompass.database.TaskAlarm
import com.ToDoCompass.uiComponents.smallComponents.ChooseNotificationTypeDropDownSuper
import com.ToDoCompass.uiComponents.smallComponents.StringInputField
import com.marosseleng.compose.material3.datetimepickers.date.ui.dialog.DatePickerDialog
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import java.time.LocalDate
import java.time.LocalTime


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddAlarmDialog(
    vm : MainViewModel,
    parentId : Int = TASK_NOT_ADDED,
    defaultNotifTypeId : Int = -1,
    label : String = "",
    confirmButtonText : String = "",
    onDismiss : () -> Unit,
    onSave : (TaskAlarm) -> Unit,
) {
    var message by remember { mutableStateOf(EMPTY_STRING) }

    var firstLaunch by rememberSaveable { mutableStateOf(true) }
    var datePick by rememberSaveable { mutableStateOf(true) }
    var timePick by rememberSaveable { mutableStateOf(false) }
    

    var pickedDate by rememberSaveable{ mutableStateOf(LocalDate.now()) }
    var pickedTime by rememberSaveable{ mutableStateOf(LocalTime.now()) }
    
    //get this from vm
    //val defaultNotifTypeId by rememberSaveable{ mutableStateOf(defaultNotifTypeId) }
    var notifTypeId by rememberSaveable{ mutableStateOf(defaultNotifTypeId) }

    if (!datePick && !timePick){
        DialogWrapper(
            mainLabel = label,
            confirmButtonText = confirmButtonText,
            dismissButtonText = CANCEL_BUTTON,
            onConfirm = {
                onSave(
                    TaskAlarm(
                        parentId = parentId,
                        date = pickedDate.toString(),
                        time = pickedTime.toString().take(5),
                        active = true,
                        note = message,
                        notifTypeId = notifTypeId,
                    )
                )
            },
            onDismiss = {
                onDismiss()
            }
        ){
            Column() {
                Row(){
                    Button(
                        modifier = Modifier,
                        onClick = {
                            datePick = true
                        }
                    ){
                        Icon(Icons.Filled.DateRange, "Calendar")
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = pickedDate.toString())
                    }
                    Button(
                        modifier = Modifier,
                        onClick = {
                            timePick = true
                        }
                    ){
                        Icon(imageVector = ImageVector.vectorResource(R.drawable.baseline_access_time_24),
                             ""
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = pickedTime.toString())
                    }
                }
                Spacer(
                    modifier = Modifier.height(10.dp)
                )
                Text(StringConstants.PICK_NOTIF_TYPE)
                ChooseNotificationTypeDropDownSuper(
                    vm = vm,
                    chosenId = notifTypeId,
                    parentTaskId = parentId,
                    onChoose = {
                        notifTypeId = it
                    }
                )
                Spacer(
                    modifier = Modifier.height(10.dp)
                )
                StringInputField(
                    inVal = message,
                    label = ALARM_NOTE_LABEL,
                    shouldFocus = false,
                    onInput = {message = it}
                )
                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }
        }
    }

    if (datePick) {
        DatePickerDialog(
            title = {
                Text(StringConstants.DATE_PICK_TITLE,
                     style = MaterialTheme.typography.headlineSmall
                )
            },
            initialDate = LocalDate.now(),
            onDismissRequest = {
                datePick = false
                if (firstLaunch){
                    onDismiss()
                }
            },
            onDateChange = {
                pickedDate = it
                if (firstLaunch){
                    timePick = true
                }
                datePick = false

            }
        )
    }
    if (timePick) {
        TimePickerDialog(
            title = {
                Text(StringConstants.TIME_PICK_TITLE,
                     style = MaterialTheme.typography.headlineSmall
                )
            },
            onDismissRequest = {
                if (firstLaunch){
                    datePick = true
                }
                timePick = false
            },
            onTimeChange = {
                pickedTime = it
                timePick = false
                firstLaunch = false
            }

        )
    }


}
