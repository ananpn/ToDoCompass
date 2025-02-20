package com.ToDoCompass.screens

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ToDoCompass.LogicAndData.Constants.Companion.emptyAppSettingsData
import com.ToDoCompass.LogicAndData.Constants.Companion.paletteItems
import com.ToDoCompass.LogicAndData.StringConstants.Companion.ALARMS_SETTINGS
import com.ToDoCompass.LogicAndData.StringConstants.Companion.COLORS_SETTINGS
import com.ToDoCompass.LogicAndData.StringConstants.Companion.DARK_THEME_SETTING
import com.ToDoCompass.LogicAndData.StringConstants.Companion.DEFAULT_HUE_BUTTON
import com.ToDoCompass.LogicAndData.StringConstants.Companion.HUE_SETTING
import com.ToDoCompass.LogicAndData.StringConstants.Companion.OTHER_SETTINGS
import com.ToDoCompass.LogicAndData.StringConstants.Companion.PALETTE_SETTING
import com.ToDoCompass.LogicAndData.StringConstants.Companion.SETTINGS_TITLE
import com.ToDoCompass.ViewModels.MainViewModel
import com.ToDoCompass.ui.theme.getTextButtonColors
import com.ToDoCompass.uiComponents.AlarmSettingsBox
import com.ToDoCompass.uiComponents.Lists.NoNestedScroll
import com.ToDoCompass.uiComponents.Modals.DeleteDialogSuper
import com.ToDoCompass.uiComponents.SetCustomVibrationPattern
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.alorma.compose.settings.storage.base.rememberFloatSettingState
import com.alorma.compose.settings.storage.base.rememberIntSettingState
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsListDropdown
import com.alorma.compose.settings.ui.SettingsSlider
import com.alorma.compose.settings.ui.SettingsSwitch
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(vm : MainViewModel,
                   context : Context,
) {
    
    
    val appSettings = vm.appSettingsFlow.collectAsState(initial = emptyAppSettingsData).value
    
    
    val seedColorFloat = appSettings.seedColorData
    val darkTheme = appSettings.darkTheme
    val paletteData = appSettings.paletteData

    val scope = rememberCoroutineScope()
    
    val hueState = rememberFloatSettingState(
        defaultValue = seedColorFloat
    )
    hueState.value = seedColorFloat
    

    val darkState = rememberBooleanSettingState(
        defaultValue = darkTheme
    )
    darkState.value = darkTheme

    val paletteState = rememberIntSettingState(
        defaultValue = paletteData
    )
    paletteState.value = paletteData

    val groups by vm._dbProfiles.collectAsState(initial = listOf())
    
    var openSetCustomVibrationPattern by remember{ mutableStateOf(false) }
    
    val nestedScrollConnection = NoNestedScroll()
    
    val activity = LocalContext.current as Activity
    val buttonOffset = 10.dp
    
    LaunchedEffect(Unit){
        vm.startCheckWorker()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(SETTINGS_TITLE)
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(
                    state = rememberScrollState(),
                    //flingBehavior = ,
                )
                .nestedScroll(
                    connection = nestedScrollConnection
                )
        ) {
            
            SettingsGroup(title = { Text(COLORS_SETTINGS) }) {
                Button(
                    modifier = Modifier.offset(x = buttonOffset),
                    onClick = {
                        vm.testNotification()
                    }
                ){
                    Text(text = "test notification")
                    
                }
                
                SettingsSwitch(
                    modifier = Modifier.height(70.dp),
                    state = darkState,
                    title = { Text(DARK_THEME_SETTING) },
                    onCheckedChange = {
                        scope.launch{
                            vm.setDarkMode(it)
                        }
                    }
                )

                SettingsSlider(
                    valueRange = (0f..360f),
                    state = hueState,
                    onValueChange = {
                        //hueState.value = it
                        vm.setSeedColor(hueState.value)
                    },
                    onValueChangeFinished = {
                    },
                    title = { Text(text = HUE_SETTING) }
                )
                
                Button(
                    modifier = Modifier.align(Alignment.End).offset(x = buttonOffset),
                    onClick = {
                        hueState.value = 180f
                        vm.setSeedColor(180f)
                    },
                    colors = getTextButtonColors()
                ) {
                    Icon(Icons.Filled.Refresh, "")
                    Text(
                        text = DEFAULT_HUE_BUTTON,
                        fontSize = 10.sp
                    )
                }
                var paletteItems2 : MutableList<String> = mutableListOf()
                paletteItems.onEach {paletteItems2.add(it.drop(13)) }
                SettingsListDropdown(
                    modifier = Modifier.height(70.dp),
                    title = {Text(PALETTE_SETTING)},
                    state = paletteState,
                    items = paletteItems2,
                    onItemSelected = {index, item ->
                        vm.setPalette(index)
                    },
                    menuItem = {index, item ->
                        Text(item)
                    },
                )

            }
            
            SettingsGroup(
                title = { Text(ALARMS_SETTINGS) }
            ) {
                AlarmSettingsBox(
                    vm = vm
                )
                Spacer(modifier = Modifier.height(10.dp))
                val player = vm.player
                Button(
                    modifier = Modifier.offset(x = buttonOffset),
                    onClick ={
                        player.stopSound()
                        player.stopPlayback()
                    }
                ){
                    Text("Stop playback")
                }
                Button(
                    modifier = Modifier.offset(x = buttonOffset),
                    onClick = {
                        openSetCustomVibrationPattern = true
                    }
                ){
                    Text(text = "Set custom vibration pattern")
                }
                if (openSetCustomVibrationPattern) {
                    SetCustomVibrationPattern(
                        vm = vm,
                        onDismiss = {
                            openSetCustomVibrationPattern = false
                        },
                        onSave = {
                            vm.saveCustomVibrationPattern(it)
                            openSetCustomVibrationPattern = false
                        }
                    )
                }
                
            }

            SettingsGroup(title = { Text(OTHER_SETTINGS) }) {
            
            }
        }
        DeleteDialogSuper(
            vm = vm,
            onDelete = {
                vm.closeUpdateDialog()
            }
        )


    }


}


/*
                Button(
                    onClick = {
                        vm.testNotification()
                    }
                ){
                    Text(text = "test notification")
                    
                }
                Button(
                    onClick = {
                        initializePermissionHelper(context, activity = activity)
                    }
                ){
                    Text(text = "check permissions")
                    
                }
                
                Button(
                    onClick = {
                        vm.testScheduleAlarmWork()
                    }
                ){
                    Text(text = "test alarm")
                    
                }
                */



/*
Button(
    onClick = {
        vm.checkAlarms()
    }
){
    Text(text = "check alarms")
    
}

Button(
    onClick = {
        vm.makeAllAlarmsInactive()
    }
){
    Text(text = "make all alarms inactive")
    
}

Button(
    onClick = {
        vm.cancelAllAlarms()
    }
){
    Text(text = "cancel all alarm pendingintents")
    
}

var flowData : Flow<List<Task>> = flowOf(listOf())

Button(
    onClick = {
        //flowData = vm._dbData
    }
){
    Text(text = "update data from database")

}
*/