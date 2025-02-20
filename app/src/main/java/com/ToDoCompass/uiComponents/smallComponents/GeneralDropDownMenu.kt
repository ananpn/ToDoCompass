package com.ToDoCompass.uiComponents.smallComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ToDoCompass.LogicAndData.Constants.Companion.defaultNotificationType
import com.ToDoCompass.LogicAndData.Constants.Companion.useGroupDefaultNotificationType
import com.ToDoCompass.LogicAndData.LogicConstants.Companion.TASK_NOT_ADDED
import com.ToDoCompass.LogicAndData.LogicConstants.Companion.USE_GROUP_DEFAULT
import com.ToDoCompass.ViewModels.MainViewModel
import com.ToDoCompass.database.NotifType

@Composable
fun <T> GeneralDropDownMenu(
    items : List<T>,
    itemContent : @Composable (T) -> Unit,
    onChosen : (T) -> Unit,
    chosenItemIn : T,
){
    var chosenItem by remember(chosenItemIn) {
        mutableStateOf(chosenItemIn)
    }
    var expanded by rememberSaveable{ mutableStateOf(false) }
    Box(modifier = Modifier.clickable(
        onClick = {
            expanded = true
        }
    )){
        itemContent(chosenItem)
        DropdownMenu(
            modifier = Modifier,
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        )
        {
            items.forEach { item ->
                DropdownMenuItem(
                    modifier = Modifier.background(
                        color = when(item == chosenItem){
                            false -> MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                            true -> MaterialTheme.colorScheme.surfaceColorAtElevation(15.dp)
                        }
                    ),
                    text = {itemContent(item)},
                    onClick = {
                        onChosen(item)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun ChooseNotificationTypeDropDown(
    vm : MainViewModel,
    label : String? = null,
    chosen : NotifType,
    onChoose : (NotifType) -> Unit = {}

){
    
    GeneralDropDownMenu(
       items = vm.notifTypes.toList(),
       itemContent = {
           Box(modifier = Modifier.padding(5.dp), contentAlignment = Alignment.Center){
               it?.let{
                   Text(
                       text = it.name,
                       style = MaterialTheme.typography.bodyLarge,
                   )
               //NotifTypeRow(notifType = it)
               }
           }
       },
       onChosen = {
           it?.let{onChoose(it)}
        },
       chosenItemIn = chosen
    )
    
}

@Composable
fun ChooseNotificationTypeDropDownSuper(
    vm : MainViewModel,
    parentTaskId : Int,
    chosenId : Int,
    onChoose : (Int) -> Unit = {},
){
    var groupDefaultNotificationType by remember{ mutableStateOf(
        defaultNotificationType
    ) }
    //The fuck is this?
    LaunchedEffect(Unit){
        if (parentTaskId != TASK_NOT_ADDED){
            val groupDefaultNotifTypeId = vm.getNotifTypeIdDefaultOfTask(parentTaskId)
            groupDefaultNotificationType = vm.notifTypes.filter {
                it.notifTypeId == groupDefaultNotifTypeId
            }.firstOrNull() ?: defaultNotificationType
        }
    }
    val items = remember(groupDefaultNotificationType.name){vm.notifTypes.map{
        if (it.notifTypeId == USE_GROUP_DEFAULT && parentTaskId != TASK_NOT_ADDED){
            it.copy(
                name = useGroupDefaultNotificationType.name
                    .drop(1).dropLast(1)+" ("+groupDefaultNotificationType.name+")"
            )
        }
        else it
    }}
    val chosen = remember(chosenId, groupDefaultNotificationType.name){
        items.filter {
            it.notifTypeId == chosenId
        }.firstOrNull()
        /*
        when (chosenId) {
            TASK_NOT_ADDED ->
                useGroupDefaultNotificationType
            USE_GROUP_DEFAULT ->
                useGroupDefaultNotificationType.copy(
                    name = useGroupDefaultNotificationType.name
                        .drop(1).dropLast(1)+" ("+groupDefaultNotificationType.name+")"
                )
            else -> items.filter {
                it.notifTypeId == chosenId
            }.firstOrNull()
        }*/
    }
    
    
    
    GeneralDropDownMenu(
        items = items,
        itemContent = {
            Box(modifier = Modifier.padding(5.dp), contentAlignment = Alignment.Center){
                it?.let{
                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    //NotifTypeRow(notifType = it)
                }
            }
        },
        onChosen = {
            it?.let{onChoose(it.notifTypeId)}
        },
        chosenItemIn = when (chosen == null){
            true -> useGroupDefaultNotificationType.copy()
            false -> chosen
        },
    )
    
    /*
    ChooseNotificationTypeDropDown(
        vm = vm,
        chosen = when (chosen == null){
           true -> useGroupDefaultNotificationType.copy()
           false -> chosen
        },
        onChoose = {
            onChoose(it.notifTypeId)
        }
    )
    */
}
