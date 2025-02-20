package com.ToDoCompass.uiComponents

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ToDoCompass.LogicAndData.Constants.Companion.defaultCustomVibrationPattern
import com.ToDoCompass.LogicAndData.StringConstants
import com.ToDoCompass.LogicAndData.VibrationPatternData
import com.ToDoCompass.LogicAndData.toVibrationPattern
import com.ToDoCompass.LogicAndData.toVibrationPatternString
import com.ToDoCompass.ViewModels.MainViewModel
import com.ToDoCompass.ui.theme.getTextButtonColors
import com.ToDoCompass.uiComponents.Lists.SimpleReorderableList
import com.ToDoCompass.uiComponents.Modals.AddNotifTypeDialog
import com.ToDoCompass.uiComponents.Modals.EditDialogSuper
import com.ToDoCompass.uiComponents.smallComponents.AddButton
import com.ToDoCompass.uiComponents.smallComponents.NotifTypeRow
import kotlinx.coroutines.launch
import rememberReorderableLazyListState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun  AlarmSettingsBox(
    vm: MainViewModel,
)
{
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    //var isDone by remember(item.taskDone){ mutableStateOf(item.taskDone) }
    
    val notifTypeListFlow = vm.getNotifTypesFromRepo().collectAsStateWithLifecycle(
        initialValue = listOf()
    )
    val notifTypesList = vm.notifTypes.filter { it.notifTypeId >= 0 }.sortedBy { it.notifTypeOrder }
    
    LaunchedEffect(Unit){
        vm.updateNotifTypesFromDB()
    }
    Column(
        modifier = Modifier
            .height((notifTypesList.size*45+5).coerceAtMost(6*45+40).dp)
    ) {
        //Spacer(modifier = Modifier.height(10.dp))
        val state = rememberReorderableLazyListState(
            onMove = {from, to ->
                vm.switchNotifTypes(from.index, to.index)
            },
            group = 70,
            onDragEnd = {_first, _second -> scope.launch{
                vm.saveNotifTypesToDB()
            }
            },
            maxScrollPerFrame = 8.dp
        
        )
        Box(modifier = Modifier
            .height((notifTypesList.size*45+5).coerceAtMost(7*45+40).dp)
        )
        {
            SimpleReorderableList(
                state = state,
                /*
                modifier = Modifier.nestedScroll(
                    connection = ,
                    dispatcher = NestedScrollDispatcher()
                ),
                */
                itemsClickable = false,
                items = notifTypesList,
                itemKey = {
                        it -> it.notifTypeId
                },
                itemOrder = {
                        it -> it.notifTypeOrder
                },
                itemContentBox = {isDragging, item ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(20))
                            .height(40.dp)
                            .combinedClickable(
                                onClick = {
                                },
                                onDoubleClick = {
                                    vm.openUpdateDialog(
                                        entity = item
                                    )
                                }
                            )
                            .background(
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                            .shadow(elevation = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        Spacer(modifier = Modifier.width(20.dp))
                        NotifTypeRow(item)
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    
                }
            )
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
    AddButton(
        onClicked = {
            vm.openAddNewDialog()
        },
        modifier = Modifier.offset(x = 30.dp)
    )
    
    
    AddNotifTypeDialog(
        vm = vm,
        onDismiss = {
            vm.closeAddNewDialog()
        },
        onSave = {scope.launch {
            vm.updateNotifTypesFromDB()
        }
        }
    )
    
    EditDialogSuper(vm = vm)
    
    
    
    
    
    /*
    Row(modifier = Modifier
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val notificationSoundList = getNotificationSounds()
        val customVibrationPattern = remember(notifSettings.customVibrationPattern) {
            VibrationPattern(
                title = "Custom",
                pattern = notifSettings.customVibrationPattern.toVibrationPatternArray()
                        ?: vibrationPatternSilent.pattern
            )
        }
        val vibrationPatternList = remember(notifSettings.customVibrationPattern) {listOf(
            vibrationPatternSilent,
            vibrationPatternDefault,
            vibrationPatternShort,
            vibrationPatternBlip,
            vibrationPatternLong,
            customVibrationPattern
        )}
        for (alarmType in AlarmType.entries){
            AlarmTypeEditBox(
                context = context,
                vm = vm,
                alarmType = alarmType,
                notifSettings = notifSettings,
                notificationSoundList = notificationSoundList,
                vibrationPatternList = vibrationPatternList
            )
            
        }
    }
    */
}






/*

@Composable
fun  AlarmTypeEditBox (
    context : Context,
    vm : MainViewModel,
    alarmType: AlarmType,
    notifSettings : NotifSettingsData,
    notificationSoundList : List<NotificationSound>,
    vibrationPatternList : List<VibrationPattern>
){
    val player = vm.player
    var notifSettings : NotificationSettings by remember{ mutableStateOf(
        when(alarmType){
            AlarmType.SILENT -> defaultNotificationSettings0
            AlarmType.GENTLE -> defaultNotificationSettings1
            AlarmType.MAX -> defaultNotificationSettings2
        }
    ) }
    LaunchedEffect(Unit) {
        notifSettings = vm.getNotificationSettings(alarmType)
            ?: defaultNotificationSettings0
    }
    Column {
        Text(text = alarmType.name,
             style = MaterialTheme.typography.bodyLarge
        )
        SetAlarmTypeSound(context, vm, alarmType, notificationSoundList)
        SetAlarmTypeVibrationPattern(context, vm, alarmType, vibrationPatternList)
        
        
        val scope = rememberCoroutineScope()
        Button(
            onClick ={ scope.launch{
                val alarmTypePattern = vm.getAlarmTypePatternArray(alarmType = alarmType)
                val alarmTypeSoundUri = vm.getAlarmTypeSoundUri(alarmType = alarmType)
                /*
                player.startPersistentAlarm(
                    notifType =
                    
                )
                */
            }}
        ){
            Text("test alarm")
        }
    }
}

@Composable
fun SetAlarmTypeSound(
    context : Context,
    vm : MainViewModel,
    alarmType: AlarmType,
    notificationSoundList : List<NotificationSound>,
){
    val player = vm.player
    var selectedSoundTitle by remember{ mutableStateOf(notificationSoundList.first().title) }
    LaunchedEffect(Unit){
        val alarmTypeSoundUri = vm.getAlarmTypeSoundUri(alarmType = alarmType)
        selectedSoundTitle = notificationSoundList.firstOrNull {
            it.uri == alarmTypeSoundUri
        }?.title ?:when(alarmType){
            AlarmType.SILENT -> notificationSoundList.first().title
            AlarmType.GENTLE -> notificationSoundList.first().title
            AlarmType.MAX -> notificationSoundList.first().title
        }
    }
    var expanded by remember{ mutableStateOf(false) }
    Box(modifier = Modifier.clickable {
        expanded = true
    }){
        Text(selectedSoundTitle)
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false
            })
        {
            notificationSoundList.forEach {
                DropdownMenuItem(
                    text = {
                        Text(it.title)
                    },
                    enabled = it.title.substring(0,1) != "-",
                    onClick = {
                        if (it.uri != null) {
                            /*
                            player.playSoundWithVibration(
                                soundUri = it.uri,
                                beLooping = false
                            )
                            */
                            
                        }
                        selectedSoundTitle = it.title
                        vm.saveNotificationSettingsToPrefs(
                            alarmType, it.uri, null
                        )
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun  SetAlarmTypeVibrationPattern(
    context : Context,
    vm : MainViewModel,
    alarmType: AlarmType,
    vibrationPatternList : List<VibrationPattern>
){
    val player = vm.player
    var selectedPatternTitle by remember{ mutableStateOf(vibrationPatternList.first().title) }
    LaunchedEffect(Unit){
        val alarmTypePattern = vm.getAlarmTypePatternArray(alarmType = alarmType)
        selectedPatternTitle = vibrationPatternList.firstOrNull {
            it.pattern.contentEquals(alarmTypePattern)
        }?.title ?:vibrationPatternList.first().title
    }
    
    var expanded by remember{ mutableStateOf(false) }
    Box(modifier = Modifier.clickable {
        expanded = true
    }){
        Text(selectedPatternTitle)
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false
            })
        {
            vibrationPatternList.forEach {
                DropdownMenuItem(
                    text = {
                        Text(it.title)
                    },
                    onClick = {
                        player.vibratePatternOnce(it.pattern)
                        selectedPatternTitle = it.title
                        vm.saveNotificationSettingsToPrefs(
                            alarmType, null, it.pattern
                        )
                        expanded = false
                        
                    }
                )
            }
        }
    }
}



@Composable
fun SetAlarmTypePersistence(
    context : Context,
    vm : MainViewModel,
    alarmType: AlarmType,
){
    var selectedPatternTitle by remember{ mutableStateOf(vibrationPatternList.first().title) }
    LaunchedEffect(Unit){
        val alarmTypePattern = vm.getAlarmTypePatternArray(alarmType = alarmType)
        selectedPatternTitle = vibrationPatternList.firstOrNull {
            it.pattern.contentEquals(alarmTypePattern)
        }?.title ?:vibrationPatternList.first().title
    }
    
    var expanded by remember{ mutableStateOf(false) }
    Box(modifier = Modifier.clickable {
        expanded = true
    }){
        Text(selectedPatternTitle)
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false
            })
        {
            vibrationPatternList.forEach {
                DropdownMenuItem(
                    text = {
                        Text(it.title)
                    },
                    onClick = {
                        vibratePattern(context, it.pattern)
                        selectedPatternTitle = it.title
                        vm.saveNotificationSettingsToPrefs(
                            alarmType, null, it.pattern
                        )
                        expanded = false
                        
                    }
                )
            }
        }
    }
}
*/

@Composable
fun SetCustomVibrationPattern(
    vm : MainViewModel,
    onDismiss : () -> Unit,
    onSave : (VibrationPatternData) -> Unit
){
    var vibrationPatternData = vm.customVibrationPatternFlow.collectAsStateWithLifecycle(initialValue = defaultCustomVibrationPattern)
    var titleString by remember(vibrationPatternData.value){mutableStateOf(vibrationPatternData.value.title)}
    var patternString by remember(vibrationPatternData.value){mutableStateOf(vibrationPatternData.value.pattern.toVibrationPatternString())}
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        
        title = {
            Text(
                text = "Set custom vibration pattern"
            )
        },
        text = {
            Column() {
                TextField(
                    label = {
                        Text("Title")
                    },
                    value = titleString,
                    onValueChange = {
                        titleString = it
                    },
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "For example the pattern \"0,100,150,200\" " +
                            "means wait 0 ms, vibrate 100 ms, wait 150 ms, vibrate 200 ms"
                )
                Spacer(modifier = Modifier.height(5.dp))
                TextField(
                    label = {
                        Text("Pattern")
                    },
                    value = patternString,
                    onValueChange = {
                        patternString = it
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Decimal
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                colors = getTextButtonColors(),
                onClick = {
                    onSave(
                        VibrationPatternData(
                            title = titleString,
                            pattern = patternString.toVibrationPattern()
                        )
                    )
                }
            ) {
                Text(
                    text = StringConstants.SAVE_BUTTON
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
                    text = StringConstants.CANCEL_BUTTON
                )
            }
        },
        
        )
}