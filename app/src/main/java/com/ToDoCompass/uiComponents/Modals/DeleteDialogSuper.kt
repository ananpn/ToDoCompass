package com.ToDoCompass.uiComponents.Modals

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.ToDoCompass.LogicAndData.StringConstants.Companion.CANCEL_BUTTON
import com.ToDoCompass.LogicAndData.StringConstants.Companion.CONFIRM_DELETE
import com.ToDoCompass.LogicAndData.StringConstants.Companion.CONFIRM_DELETE_ALARM
import com.ToDoCompass.LogicAndData.StringConstants.Companion.CONFIRM_DELETE_NOTIF_TYPE
import com.ToDoCompass.LogicAndData.StringConstants.Companion.CONFIRM_DELETE_PROFILE
import com.ToDoCompass.LogicAndData.StringConstants.Companion.CONFIRM_DELETE_SUBTASK
import com.ToDoCompass.LogicAndData.StringConstants.Companion.CONFIRM_DELETE_TASK
import com.ToDoCompass.LogicAndData.StringConstants.Companion.DELETE_BUTTON
import com.ToDoCompass.LogicAndData.StringConstants.Companion.INFO_DELETE_SUBTASK
import com.ToDoCompass.LogicAndData.StringConstants.Companion.INFO_DELETE_TASK
import com.ToDoCompass.ViewModels.MainViewModel
import com.ToDoCompass.database.NotifType
import com.ToDoCompass.database.Task
import com.ToDoCompass.database.TaskAlarm
import com.ToDoCompass.database.TaskProfile
import com.ToDoCompass.ui.theme.getTextButtonColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DeleteDialogSuper(
    vm : MainViewModel,
    //item : ListItem,
    //label : String,
    onDelete : () -> Unit = {}
) {
    if (vm.openDeleteDialog){
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        fun operationOnDelete(it : Any) { scope.launch{
            vm.deleteEntityInDBTotally(it)
            delay(50)
            vm.updateEveryThingFromDB()
            vm.closeDeleteDialog()
            onDelete()
        }}
        
        when (vm.entityToDelete::class){
            Task::class ->
                ConfirmDeleteDialog(
                    vm = vm,
                    titleText = when(vm.deleteInfo){
                        INFO_DELETE_TASK -> CONFIRM_DELETE_TASK
                        INFO_DELETE_SUBTASK -> CONFIRM_DELETE_SUBTASK
                        else -> ""
                    },
                        onDelete = {
                            operationOnDelete(vm.entityToDelete)
                        },
                )
            TaskProfile::class ->
                ConfirmDeleteDialog(
                    vm = vm,
                    titleText = CONFIRM_DELETE_PROFILE,
                    onDelete = {
                        operationOnDelete(vm.entityToDelete)
                        vm.closeUpdateDialog()
                    },
                )
            NotifType::class ->
                ConfirmDeleteDialog(
                    vm = vm,
                    titleText = CONFIRM_DELETE_NOTIF_TYPE,
                    onDelete = {
                        operationOnDelete(vm.entityToDelete)
                        vm.closeUpdateDialog()
                    },
                )
            TaskAlarm::class ->
                ConfirmDeleteDialog(
                    vm = vm,
                    titleText = CONFIRM_DELETE_ALARM,
                    onDelete = {
                        operationOnDelete(vm.entityToDelete)
                        vm.closeUpdateDialog()
                    },
                )
            
            else -> throw Exception("Error in EditDialogSuper: entity type invalid")
        }
    }
}

@Composable
fun ConfirmDeleteDialog(
    vm: MainViewModel,
    onDelete: () -> Unit,
    onDismiss: () -> Unit = {vm.closeDeleteDialog()},
    titleText : String = CONFIRM_DELETE,
    deleteButtonText : String = DELETE_BUTTON,
    dismissButtonText : String = CANCEL_BUTTON
    
) {
    if (vm.openDeleteDialog) {
        val coroutineScope = rememberCoroutineScope()
        AlertDialog(
            onDismissRequest = {
                vm.closeDeleteDialog()
            },
            title = {
                Text(
                    text = titleText
                )
            },
            confirmButton = {
                TextButton(
                    colors = getTextButtonColors(),
                    onClick = {
                        onDelete()
                    }
                ) {
                    Text(
                        text = deleteButtonText
                    )
                }
            },
            dismissButton = {
                TextButton(
                    colors = getTextButtonColors(),
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text(
                        text = dismissButtonText
                    )
                }
            }
        )
    }
}