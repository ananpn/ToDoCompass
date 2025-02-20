package com.ToDoCompass.uiComponents.Modals

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import com.ToDoCompass.ViewModels.MainViewModel
import com.ToDoCompass.database.NotifType
import com.ToDoCompass.database.Task
import com.ToDoCompass.database.TaskAlarm
import com.ToDoCompass.database.TaskProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditDialogSuper(
    vm : MainViewModel,
    //item : ListItem,
    //label : String,
    //onDismiss : () -> Unit
) {
    if (vm.openUpdateDialog){
        val scope = rememberCoroutineScope()
        when (vm.entityToUpdate::class){
            Task::class -> {
            
            }
            TaskProfile::class -> {
                EditProfileDialog(
                    vm = vm,
                    onDismiss = {scope.launch {
                        vm.updateProfilesFromDB()
                        vm.closeUpdateDialog()
                    }},
                    onDelete = {scope.launch{
                        vm.deleteEntityInDBTotally(vm.entityToUpdate as TaskProfile)
                        delay(150)
                        vm.updateDataFromDB()
                        vm.updateProfilesFromDB()
                        delay(20)
                        (vm.entityToUpdate as? TaskProfile)?.also{
                            if (vm.uiState.dispProfileId == it.idProfile){
                                vm.setDispProfile(vm.profiles.firstOrNull()?.idProfile ?:1)
                            }
                        }
                        vm.closeUpdateDialog()
                    }
                    }
                )
            }
            NotifType::class -> {
                EditNotifTypeDialog(
                    vm = vm,
                    onDismiss = {
                    
                    },
                    onSave = {
                    
                    }
                )
            }
            TaskAlarm::class -> {
                (vm.entityToUpdate as? TaskAlarm)?.also{alarmToUpdate ->
                    EditAlarmDialog(
                        vm = vm,
                        onDismiss = {
                            vm.closeUpdateDialog()
                        },
                        alarmToUpdate = alarmToUpdate
                    )
                }
            }
            else -> throw Exception("Error in EditDialogSuper: entity type invalid")
        }
}
}
