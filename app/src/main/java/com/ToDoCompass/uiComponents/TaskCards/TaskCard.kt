package com.ToDoCompass.uiComponents.TaskCards

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ToDoCompass.LogicAndData.Constants.Companion.emptyListItem
import com.ToDoCompass.LogicAndData.StringConstants
import com.ToDoCompass.LogicAndData.StringConstants.Companion.ADD_SUBTASK
import com.ToDoCompass.LogicAndData.StringConstants.Companion.INFO_DELETE_TASK
import com.ToDoCompass.LogicAndData.getBorderColor
import com.ToDoCompass.LogicAndData.getTableColor
import com.ToDoCompass.ViewModels.MainViewModel
import com.ToDoCompass.ViewModels.UiViewModel
import com.ToDoCompass.database.ListItem
import com.ToDoCompass.database.toTask
import com.ToDoCompass.uiComponents.Lists.SimpleReorderableList
import com.ToDoCompass.uiComponents.Modals.AddTaskDialog
import com.ToDoCompass.uiComponents.smallComponents.CloseButton
import com.ToDoCompass.uiComponents.smallComponents.DeleteButton
import com.ToDoCompass.uiComponents.smallComponents.SecureButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
       ExperimentalFoundationApi::class
)
@Composable
fun TaskCard(
    uiVM : UiViewModel,
    vm : MainViewModel,
    item : ListItem = vm.uiState.taskCardListItem,
    onDismiss : () -> Unit,
    onUpdate : () -> Unit,
){
if (vm.showingTaskCard) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    
    var addNewSubTask by rememberSaveable { mutableStateOf(false) }

    var showedSubTask by remember{ mutableStateOf(emptyListItem) }
    
    var taskName by rememberSaveable {
        mutableStateOf(item.title)
    }
    var canEdit by rememberSaveable { mutableStateOf(false) }
    var titleEditComplete by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember{MutableInteractionSource()}
    
    /*
    //group menu dropdown
    var expanded by remember { mutableStateOf(false) }
    var selectedProfileId by rememberSaveable { mutableStateOf(item.profile) }
    //Not from viewModel.profileTitle, since otherwise the menu would always display the opened group
    var dispProfile by rememberSaveable { mutableStateOf("") }
    dispProfile = vm.getProfileTitle(item.profile).collectAsState(initial = "").value
    val profiles by vm.profiles.collectAsState(initial = listOf())
    */

    LaunchedEffect(Unit){
        vm.updateDataAndSubTasksFromDB()
        vm.updateNotifTypesFromDB()
        //delay(50)
        //updateData(subTasks)
    }

    LaunchedEffect(uiVM.isDragging){
        if (!uiVM.isDragging){
            vm.saveSubTasksToDB()
        }
    }
    
    LaunchedEffect(titleEditComplete) {
        if (titleEditComplete){
            vm.updateEntityInDB(
                item.copy(
                    title = taskName
                ).toTask()
            )
            uiVM.updateDraggedItem(item.copy(title = taskName))
            vm.updateDataAndSubTasksFromDB()
            delay(50)
            onUpdate()
            titleEditComplete = false
        }
    }

    Box(modifier = Modifier
        .background(color = Color.Black.copy(alpha = 0.3f))
        .fillMaxSize()
        .zIndex(11f)
        .clickable(onClick = {
            onDismiss()
        })
    ){

    }
    Box(modifier = Modifier
        .background(color = Color.Transparent)
        .fillMaxSize()
        .fillMaxHeight(0.9f)
        .offset(y = 100.dp)
        .zIndex(
            when (canEdit) {
                true -> 12f
                false -> -12f
            }
        )
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = {
                titleEditComplete = true
                canEdit = false
                focusRequester.freeFocus()
            })
        
    ){
    
    }
    Column(modifier = Modifier
        .background(color = Color.Transparent)
        .fillMaxSize()
        .zIndex(11f)
        .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Card(
            modifier = Modifier
                .fillMaxWidth(0.87f)
                .height(380.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
                    /*
                    .copy(
                    red = MaterialTheme.colorScheme.secondaryContainer.red*0.95f,
                    blue = MaterialTheme.colorScheme.secondaryContainer.blue*0.95f,
                    green = MaterialTheme.colorScheme.secondaryContainer.green*0.95f,
                )*/
                ,
                //containerColor = getGroupColorCard(group = item.group),
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp 
            ),
            shape = RoundedCornerShape(5),


        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally){
                Row(horizontalArrangement = Arrangement.SpaceBetween){
                    TextField(
                        value = taskName,
                        onValueChange = {newValue : String ->
                            taskName = newValue
                        },
                        enabled = canEdit,
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .fillMaxWidth(0.6f)
                            .onFocusChanged {
                                if (it.isCaptured) {
                                
                                }
                            }
                            .combinedClickable(
                                onClick = {
                                    canEdit = true
                                    
                                },
                                onDoubleClick = {
                                    scope.launch {
                                        canEdit = true
                                        delay(30)
                                        focusRequester.requestFocus()
                                        focusRequester.freeFocus()
                                    }
                                }
                            )
                        ,
                        singleLine = true,
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        textStyle = MaterialTheme.typography.headlineSmall,
                        keyboardActions = KeyboardActions (
                            onDone = {
                                titleEditComplete = true
                                focusManager.clearFocus()
                                canEdit = false
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors().copy(
                            unfocusedTextColor = OutlinedTextFieldDefaults.colors().focusedTextColor,
                            disabledTextColor = OutlinedTextFieldDefaults.colors().focusedTextColor,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                        ),
                        
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    DeleteButton(
                        onDeletePressed = {
                            vm.openDeleteDialog(
                                entity = item.toTask(),
                                info = INFO_DELETE_TASK
                            )
                        }
                    )
                    Spacer(Modifier.width(2.dp))
                    CloseButton(
                        modifier = Modifier,
                        onClose = {
                            onDismiss()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                AlarmBox(
                    vm = vm,
                    item = item,
                )
                Spacer(
                    modifier = Modifier.height(10.dp)
                )
                val state = rememberReorderableLazyListState(
                    onMove = {from, to ->
                        vm.switchSubTasks(from.index, to.index)
                    },
                    group = 69,
                    onDragEnd = {_first, _second ->
                        vm.saveSubTasksToDB()
                    },
                    maxScrollPerFrame = 8.dp
                
                )
                SimpleReorderableList(
                    state = state,
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            brush = SolidColor(
                                getBorderColor(MaterialTheme.colorScheme.background)
                            ),
                            shape = RoundedCornerShape(5)
                        )
                        .background(color = getTableColor(MaterialTheme.colorScheme.secondaryContainer))
                        .fillMaxHeight(0.5f)
                        .clip(RoundedCornerShape(5))
                    ,
                    onItemClick = { item ->
                        showedSubTask = item
                        vm.showingSubTaskCard = true

                    },
                    itemKey = {
                        it : ListItem -> it.uniqueId
                    },
                    itemOrder = {
                        it : ListItem -> it.ord
                    },
                    items = vm.subTasks.sortedBy { it.childOrd},
                    itemContentBox = { isDragging, item ->
                        TaskRow(
                            content = {
                                SubTaskCardRowContent(
                                    item = item,
                                    taskDoneUp = { newDone ->
                                        scope.launch {
                                            vm.updateTaskDone(item.toTask(), done = newDone)
                                            vm.updateDataAndSubTasksFromDB()
                                        }
                                    }
                                )
                            }
                        )
                    }
                )
                Spacer(
                    modifier = Modifier.height(14.dp)
                )
                Button(
                    modifier = Modifier.scale(0.9f),
                    onClick = {
                        addNewSubTask = true
                    }
                ){
                    Icon(Icons.Filled.Add, "")
                    Text(text = "Add subtask")

                }
                Spacer(
                    modifier = Modifier.height(10.dp)
                )
                SecureButton(
                    modifier = Modifier,
                    onButtonPressed = {scope.launch{
                        vm.updateTaskDone(item.toTask())
                        vm.updateDataAndSubTasksFromDB()
                        onDismiss()
                    }},
                    buttonContent = {
                        Icon(Icons.Filled.Done, "")
                        Text(text = "Task done")
                    },
                    
                    
                )
            }

        }
        if (vm.showingSubTaskCard) {
            SubTaskCard(
                vm = vm,
                showedSubTask = showedSubTask,
                onClose = {vm.showingSubTaskCard = false},
                focusManager = focusManager
            )
        }
        if (addNewSubTask) {
            val context = LocalContext.current
            AddTaskDialog(
                vm = vm,
                label = ADD_SUBTASK,
                onSave = {savedItem, savedAlarm -> scope.launch {
                    try {
                        vm.insertNewTask(
                            item = savedItem.copy(
                                group = item.group,
                                isChild = true,
                                idOfParent = item.uniqueId
                            ),
                            alarm = savedAlarm
                        )
                        delay(100)
                        addNewSubTask = false
                    }
                    catch(e :Exception){
                        val toast =
                            Toast.makeText(context, StringConstants.TASK_ADD_FAIL, Toast.LENGTH_LONG) // in Activity
                        toast.show()
                    }
                } //scope ^^^
                },
                onDismiss = {
                    addNewSubTask = false
                }
            )
        }
    }
}
}
