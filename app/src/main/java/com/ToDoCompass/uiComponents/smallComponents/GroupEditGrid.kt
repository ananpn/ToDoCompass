package com.ToDoCompass.uiComponents.smallComponents

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ToDoCompass.LogicAndData.StringConstants.Companion.INFO_DELETE_TASK
import com.ToDoCompass.LogicAndData.getGroupColor
import com.ToDoCompass.ViewModels.MainViewModel
import com.ToDoCompass.database.DefaultNotifType
import com.ToDoCompass.database.toTask
import com.ToDoCompass.uiComponents.TaskCards.DoneTaskRowContent
import com.ToDoCompass.uiComponents.TaskCards.TaskRow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GroupEditGrid(
    vm : MainViewModel
){
    var editCardOpen by remember{mutableStateOf(false)}
    var groupToEdit by remember{mutableStateOf(0)}
    val onClickAction : (group : Int) -> Unit = {group ->
        Log.v("GroupEditGrid onClickAction", "launch")
        editCardOpen = true
        groupToEdit = group
    }
    
    Column(modifier = Modifier
        .fillMaxHeight()){
        Row(
            modifier = Modifier
                .weight(1f)
        
        )
        {
            GroupEditGridBox(
                group = 0,
                vm = vm,
            )
            GroupEditGridBox(
                group = 1,
                vm = vm,
            )
        }
        Row(
            modifier = Modifier
                .weight(1f)
        ){
            GroupEditGridBox(
                group = 2,
                vm = vm,
            )
            GroupEditGridBox(
                group = 3,
                vm = vm,
            )
        }
        
    }
}

@Composable
fun RowScope.GroupEditGridBox(
    group : Int,
    vm : MainViewModel,
    onClick : (group : Int) -> Unit = {}
){
    val boxModifier = Modifier
        .height(300.dp)
        .padding(5.dp)
        .clip(shape = RoundedCornerShape(15))
        .clickable(
            onClick = {
                vm.openGroupEditCard(group)
            }
        )
    Box(
        contentAlignment = Alignment.Center,
        modifier = boxModifier
            .weight(1f)
            .background(color = getGroupColor(group))
    ) {
        Text("Edit", color = MaterialTheme.colorScheme.outline)
    }
}

@Composable
fun GroupManageCard(
    vm : MainViewModel,
    onDismiss : () -> Unit = {}
){ if (vm.showingGroupEditCard){
    val profileToEdit = vm.dispProfileId.collectAsState(initial = 0)
    val group = vm.groupToEdit
    val scope = rememberCoroutineScope()
    
    val doneTasks = vm.allData.toList().filter{
        it.taskDone && it.group == vm.groupToEdit && !it.isChild
    }
    Log.v("groupmanagecard", "donetasks = $doneTasks")
    
    Box(modifier = Modifier
        .background(color = Color.Black.copy(alpha = 0.3f))
        .fillMaxSize()
        .zIndex(11f)
        .clickable(onClick = {
            onDismiss()
        })
    ){
    
    }
    Column(
        modifier = Modifier
            .background(color = Color.Transparent)
            .fillMaxSize()
            .zIndex(11f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        
        Card(
            modifier = Modifier
                .width(330.dp)
                .height(360.dp),
            colors = CardDefaults.cardColors(
                containerColor = getGroupColor(group = group).let{
                    it.copy(
                        red = (it.red*0.80f).coerceAtMost(1f),
                        blue = (it.blue*0.80f).coerceAtMost(1f),
                        green = (it.green*0.80f).coerceAtMost(1f)
                    )
                },
                //containerColor = getGroupColorCard(group = item.group),
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            shape = RoundedCornerShape(5),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                GroupDefaultNotifTypeBox(vm)
            }
            
            LazyColumn(
            
            ){
                doneTasks.forEach { doneTask -> 
                    item{
                        TaskRow(){
                            DoneTaskRowContent(
                                item = doneTask,
                                activateTaskUp = { scope.launch {
                                    vm.updateTaskDone(doneTask.toTask(), false)
                                    vm.updateDataFromDB()
                                    // TODO vm.activateTaskTotally(doneTask.toTask()) activate subtasks and alarms too
                                }},
                                deleteTaskFinally = {
                                    vm.openDeleteDialog(
                                        entity = doneTask.toTask(),
                                        info = INFO_DELETE_TASK
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
fun GroupDefaultNotifTypeBox(
    vm : MainViewModel,
){
    val scope = rememberCoroutineScope()
    val dispProfileId = vm.dispProfileId.collectAsState(initial = 1).value
    var selectedDefaultNotifType =
        vm.getDefaultNotifTypeOfGroupInProfile(
            dispProfileId, group = vm.groupToEdit
        ).collectAsState(initial = null)
    
    var notifTypeLazyColumnExpanded by remember{ mutableStateOf(false) }
    
    val notifTypeList = vm.notifTypes.filter{it.notifTypeId != -3}//vm.getNotifTypesFromRepo().collectAsState(initial = listOf()).value
    var selectedNotifType = notifTypeList.firstOrNull{it.notifTypeId == selectedDefaultNotifType.value?.notifTypeId}
    
    LaunchedEffect(Unit) {scope.launch{
        vm.checkDefaultNotifTypes()
        vm.updateNotifTypesFromDB()
    }}
    //val defaultNotifType = vm.getDefaultNotifTypeOf()
    
Column(Modifier.padding(5.dp)) {
    val notifTypeRowModifier : Modifier = Modifier.clip(RoundedCornerShape(10))
    Text("Set default notification")
    Spacer(modifier = Modifier.height(10.dp))
    Box(
        modifier = Modifier
            .clickable(
                onClick = {
                    notifTypeLazyColumnExpanded = !notifTypeLazyColumnExpanded
                }
            )
    )
    {
        selectedNotifType?.let {
            NotifTypeRow(
                notifType = it,
                modifier = notifTypeRowModifier
                    .background(color = Color.White.copy(alpha = 0.1f))
            )
        } ?: Text("Select here")
    }
    AnimatedVisibility(visible = notifTypeLazyColumnExpanded) {
        LazyColumn() {
            notifTypeList.forEach { notifType ->
                item {
                    Column(modifier = Modifier
                        .clickable(
                            onClick = {scope.launch{
                                vm.updateEntityInDB(
                                    DefaultNotifType(
                                        idForThis = selectedDefaultNotifType?.value?.groupDefaultNotifTypeId ?:-1,
                                        idProfile = dispProfileId,
                                        groupNumber = vm.groupToEdit,
                                        notifTypeId = notifType.notifTypeId,
                                    )
                                )
                                delay(50)
                                notifTypeLazyColumnExpanded = false
                            }},
                        )
                    ){
                        NotifTypeRow(notifType = notifType, modifier = notifTypeRowModifier)
                        Box(modifier = Modifier
                            .height(0.7.dp)
                            .fillMaxWidth()
                            .background(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        )
                    }
                    
                }
            }
        }
    }
    Row(modifier = Modifier.fillMaxWidth(), Arrangement.Center) {
        Box(){
            Button(
                modifier = Modifier
                    .size(35.dp)
                ,
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = ButtonDefaults.buttonColors().containerColor.copy(alpha = 0.75f)
                ),
                contentPadding = PaddingValues(0.dp),
                onClick = {
                notifTypeLazyColumnExpanded = !notifTypeLazyColumnExpanded
            }) {
                when (notifTypeLazyColumnExpanded) {
                    true -> Icon(Icons.Outlined.KeyboardArrowUp, "")
                    false -> Icon(Icons.Outlined.KeyboardArrowDown, "")
                }
                
            }
        }
    }
        
        /*
        DropdownMenu(
            expanded = notifTypeDropDownExpanded,
            onDismissRequest = { notifTypeDropDownExpanded = false
            })
        {
            notifTypeList.forEach {
                DropdownMenuItem(
                    text = {
                        Text(it.name)
                    },
                    onClick = {scope.launch{
                        vm.updateEntityInDB(
                            *//*
                            defaultNotifType.copy(
                                idProfile = dispProfileId,
                                groupNumber = vm.groupToEdit,
                                notifTypeId = it.notifTypeId,
                            )
                                *//*
                            DefaultNotifType(
                                idForThis = selectedNotifType?.value?.defaultNotifTypeId ?:-1,
                                idProfile = dispProfileId,
                                groupNumber = vm.groupToEdit,
                                notifTypeId = it.notifTypeId,
                            )
                            
                        )
                        delay(50)
                        notifTypeDropDownExpanded = false
                    }},
                )
            }
            */
            
    }
}



data class GroupDefaultNotifType(
    val name : String?,
    val notifTypeId : Int?,
    val groupDefaultNotifTypeId: Int?
)