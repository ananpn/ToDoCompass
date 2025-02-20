package com.ToDoCompass.uiComponents.Modals

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.ToDoCompass.LogicAndData.StringConstants
import com.ToDoCompass.LogicAndData.StringConstants.Companion.ACTIVATE_ALARM_BUTTON
import com.ToDoCompass.LogicAndData.StringConstants.Companion.ALARM_NOTE_LABEL
import com.ToDoCompass.LogicAndData.StringConstants.Companion.BACK_BUTTON
import com.ToDoCompass.LogicAndData.StringConstants.Companion.DISABLE_ALARM_BUTTON
import com.ToDoCompass.LogicAndData.StringConstants.Companion.EDIT_ALARM_TITLE
import com.ToDoCompass.LogicAndData.StringConstants.Companion.PICK_NOTIF_TYPE
import com.ToDoCompass.LogicAndData.StringConstants.Companion.SAVE_BUTTON
import com.ToDoCompass.R
import com.ToDoCompass.ViewModels.MainViewModel
import com.ToDoCompass.database.TaskAlarm
import com.ToDoCompass.uiComponents.TaskCards.isAlarmTimeAfterPresent
import com.ToDoCompass.uiComponents.smallComponents.ChooseNotificationTypeDropDownSuper
import com.ToDoCompass.uiComponents.smallComponents.DeleteButton
import com.ToDoCompass.uiComponents.smallComponents.StringInputField
import com.marosseleng.compose.material3.datetimepickers.date.ui.dialog.DatePickerDialog
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditAlarmDialog(
    vm : MainViewModel,
    onDismiss : () -> Unit = {},
    alarmToUpdate : TaskAlarm,
    isAddingTask : Boolean = false,
) {
    //val context = LocalContext.current
    val scope = rememberCoroutineScope()
    //val focusRequester = FocusRequester()
    val context = LocalContext.current

    var datePick by rememberSaveable { mutableStateOf(false) }
    var timePick by rememberSaveable { mutableStateOf(false) }

    
    var notifTypeId by remember{ mutableStateOf(alarmToUpdate.notifTypeId) }
    var active by remember{ mutableStateOf(alarmToUpdate.active) }
    var pickedDate by rememberSaveable{ mutableStateOf(alarmToUpdate.date) }
    var pickedTime by rememberSaveable{ mutableStateOf(alarmToUpdate.time) }
    var message by remember { mutableStateOf(alarmToUpdate.note) }
    
    val mainLabel by remember{ mutableStateOf(
        when (isAddingTask){
            true -> EDIT_ALARM_TITLE
            false -> EDIT_ALARM_TITLE+"of task ${vm.uiState.taskCardListItem.title}"
        }
    ) }
    
    DialogWrapper(
        mainLabel = mainLabel,
        confirmButtonText = SAVE_BUTTON,
        dismissButtonText = BACK_BUTTON,
        onConfirm = { scope.launch {
            try {
                alarmToUpdate.copy(
                    notifTypeId = notifTypeId,
                    active = active,
                    time = pickedTime,
                    date = pickedDate,
                    note = message
                ).let{
                    vm.updateAndScheduleAlarm(
                        alarm = it,
                        active = it.active,
                        date = it.date,
                        time = it.time,
                    )
                }
            }
            catch(e :Exception){
                val toast =
                    Toast.makeText(context, StringConstants.ALARM_ADD_FAIL, Toast.LENGTH_LONG) // in Activity
                toast.show()
            }
            delay(30)
            vm.updateEveryThingFromDB()
            onDismiss()
        } },
        onDismiss = {
            onDismiss()
        },
        
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
                    Text(text = pickedDate.toString())
                }
                Button(
                    modifier = Modifier,
                    onClick = {
                        timePick = true
                    }
                ){
                    Icon(
                        ImageVector.vectorResource(R.drawable.baseline_access_time_24),
                        "Clock"
                    )
                    Text(text = pickedTime.toString())
                }
            }
            Spacer(
                modifier = Modifier.height(10.dp)
            )
            Text(PICK_NOTIF_TYPE)
            ChooseNotificationTypeDropDownSuper(
                vm = vm,
                chosenId = notifTypeId,
                parentTaskId = alarmToUpdate.parentId,
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
            Row(){
                if(isAlarmTimeAfterPresent(alarmToUpdate)) {
                    when (active) {
                        true -> Button(
                            modifier = Modifier,
                            onClick = {
                                active = false
                            }
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                "Disable"
                            )
                            Text(text = DISABLE_ALARM_BUTTON)
                        }
                        
                        false -> Button(
                            modifier = Modifier,
                            onClick = {
                                active = true
                            }
                        ) {
                            Icon(
                                Icons.Outlined.Refresh,
                                "Activate"
                            )
                            Text(text = ACTIVATE_ALARM_BUTTON)
                        }
                    }
                }
                DeleteButton(
                    onDeletePressed = {
                        vm.openDeleteDialog(alarmToUpdate)
                    }
                )
            }
        }
    }
    if (datePick) {
        DatePickerDialog(
            title = {
                Text(StringConstants.DATE_PICK_TITLE,
                     style = MaterialTheme.typography.headlineSmall)
            },
            initialDate = LocalDate.now(),
            onDismissRequest = {
                datePick = false
            },
            onDateChange = {
                pickedDate = it.toString()
                datePick = false

            }
        )
    }
    if (timePick) {
        TimePickerDialog(
            title = {
                Text(StringConstants.TIME_PICK_TITLE,
                     style = MaterialTheme.typography.headlineSmall)
            },
            onDismissRequest = {
                timePick = false
            },
            onTimeChange = {
                pickedTime = it.toString()
                timePick = false
            }

        )
    }
}
