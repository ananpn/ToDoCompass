package com.ToDoCompass.uiComponents.Modals

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ToDoCompass.LogicAndData.Constants.Companion.defaultCustomVibrationPattern
import com.ToDoCompass.LogicAndData.Constants.Companion.vibrationPatternBlip
import com.ToDoCompass.LogicAndData.Constants.Companion.vibrationPatternDefault
import com.ToDoCompass.LogicAndData.Constants.Companion.vibrationPatternLong
import com.ToDoCompass.LogicAndData.Constants.Companion.vibrationPatternShort
import com.ToDoCompass.LogicAndData.Constants.Companion.vibrationPatternSilent
import com.ToDoCompass.LogicAndData.StringConstants
import com.ToDoCompass.LogicAndData.StringConstants.Companion.CANCEL_BUTTON
import com.ToDoCompass.LogicAndData.StringConstants.Companion.EMPTY_STRING
import com.ToDoCompass.LogicAndData.StringConstants.Companion.RAMP_UP_SWITCH
import com.ToDoCompass.LogicAndData.StringConstants.Companion.RESPECT_SYSTEM_SWITCH
import com.ToDoCompass.LogicAndData.toIntSafe
import com.ToDoCompass.LogicAndData.toVibrationPatternString
import com.ToDoCompass.Notifications.SoundAndVibrationPlayer
import com.ToDoCompass.Notifications.getNotificationSounds
import com.ToDoCompass.ViewModels.MainViewModel
import com.ToDoCompass.database.NotifType
import com.ToDoCompass.uiComponents.smallComponents.IntegerInputField
import com.ToDoCompass.uiComponents.smallComponents.StringInputField
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddNotifTypeDialog(
    vm : MainViewModel,
    player : SoundAndVibrationPlayer = vm.player,
    label : String = "",
    confirmButtonText : String = "",
    onDismiss : () -> Unit,
    onSave : (NotifType) -> Unit,
) {
if (vm.openAddNewDialog) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var title by remember { mutableStateOf(EMPTY_STRING) }
    val notificationSoundList = getNotificationSounds()
    
    val customVibrationPattern =
        vm.customVibrationPatternFlow
            .collectAsState(initial = defaultCustomVibrationPattern).value
    val vibrationPatternList = remember(customVibrationPattern){listOf(
        vibrationPatternSilent,
        vibrationPatternDefault,
        vibrationPatternShort,
        vibrationPatternBlip,
        vibrationPatternLong,
        customVibrationPattern
    )}
    
    var selectedSound by remember{ mutableStateOf(notificationSoundList.first()) }
    var selectedPattern by remember{ mutableStateOf(vibrationPatternList.first()) }
    
    var persistentLength by remember{ mutableStateOf( "0" )}
    var rampUp by remember{ mutableStateOf(false) }
    var respectSystem by remember{ mutableStateOf(false) }
    
    
    
    DialogWrapper(
        mainLabel = StringConstants.ADD_NOTIF_TYPE,
        confirmButtonText = StringConstants.ADD_BUTTON,
        dismissButtonText = CANCEL_BUTTON,
        onConfirm = {
            scope.launch {
                try{
                    val notifType = NotifType(
                        name = title,
                        soundUriString = selectedSound.uri.toString(),
                        soundFileName = selectedSound.title,
                        vibrationPatternString = selectedPattern.pattern.toString(),
                        persistentLength = persistentLength.toIntSafe(),
                        rampUp = rampUp,
                        respectSystem = respectSystem
                    )
                    vm.insertEntityToDB(notifType)
                    delay(50)
                    vm.updateNotifTypesFromDB()
                    delay(50)
                    player.stopPlayback()
                    vm.closeAddNewDialog()
                }
                catch(e : Exception){
                    val toast =
                        Toast.makeText(context, StringConstants.NOTIF_TYPE_ADD_FAIL, Toast.LENGTH_SHORT) // in Activity
                    toast.show()
                }
            }
        },
        onDismiss = {
            player.stopPlayback()
            vm.closeAddNewDialog()
        },
    ){Column {
        StringInputField(
            inVal = title,
            label = StringConstants.GIVE_NOTIF_TYPE_TITLE,
            shouldFocus = true,
            onInput = {title = it}
        )
        Spacer(modifier = Modifier.height(14.dp))
        var soundUriDropDownExpanded by remember{ mutableStateOf(false) }
        Box(modifier = Modifier.clickable {
            soundUriDropDownExpanded = true
        }){
            Text(selectedSound.title)
            DropdownMenu(
                expanded = soundUriDropDownExpanded,
                onDismissRequest = { soundUriDropDownExpanded = false
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
                                player.playSoundWithLooping(
                                    soundUri = it.uri,
                                )
                                
                            }
                            selectedSound = it
                            soundUriDropDownExpanded = false
                        }
                    )
                }
            }
        }
        
        LaunchedEffect(Unit){
            /*
            val alarmTypePattern = vm.getAlarmTypePatternArray(alarmType = alarmType)
            selectedPatternTitle = vibrationPatternList.firstOrNull {
                it.pattern.contentEquals(alarmTypePattern)
            }?.title ?:vibrationPatternList.first().title
            */
        }
        Spacer(modifier = Modifier.height(14.dp))
        var vibrationPatternDropdownExpanded by remember{ mutableStateOf(false) }
        Box(modifier = Modifier.clickable {
            vibrationPatternDropdownExpanded = true
        }){
            Text(selectedPattern.title)
            DropdownMenu(
                expanded = vibrationPatternDropdownExpanded,
                onDismissRequest = { vibrationPatternDropdownExpanded = false
                })
            {
                vibrationPatternList.forEach {
                    DropdownMenuItem(
                        text = {
                            Text(it.title)
                        },
                        onClick = {
                            player.vibratePatternOnce(it.pattern)
                            selectedPattern = it
                            vibrationPatternDropdownExpanded = false
                            
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        IntegerInputField(
            inVal = persistentLength,
            label = "How long the alarm should last?",
            onInput = {
                persistentLength = it
            }
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(RAMP_UP_SWITCH)
        Switch(checked = rampUp,
               onCheckedChange = {
                   rampUp = it
               }
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(RESPECT_SYSTEM_SWITCH)
        Switch(checked = respectSystem,
               onCheckedChange = {
                   respectSystem = it
               }
        )
        Button(modifier = Modifier.offset(x = 30.dp, y = 40.dp),
               onClick = {
                   player.playNotificationSound(notifType = NotifType(
                       name = title,
                       soundUriString = selectedSound.uri.toString(),
                       soundFileName = selectedSound.title,
                       vibrationPatternString = selectedPattern.pattern.toVibrationPatternString(),
                       persistentLength = persistentLength.toIntSafe(),
                       rampUp = rampUp,
                   ))
               }){
            Text("Test")
        }
        
    }
    }
    
    
}//IF
}
