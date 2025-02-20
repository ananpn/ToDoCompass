package com.ToDoCompass

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ToDoCompass.LogicAndData.Constants
import com.ToDoCompass.LogicAndData.Constants.Companion.emptyTask
import com.ToDoCompass.LogicAndData.Constants.Companion.emptyTaskAlarm
import com.ToDoCompass.LogicAndData.StringConstants
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NOTIF_BUTTON3_INTENT
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NOTIF_FULL_SCREEN_INTENT
import com.ToDoCompass.LogicAndData.StringConstants.Companion.PICKED_TIME_IN_PAST
import com.ToDoCompass.LogicAndData.TimeFunctions.Companion.formatToString
import com.ToDoCompass.LogicAndData.toTimeUnitString
import com.ToDoCompass.ViewModels.MainViewModel
import com.ToDoCompass.database.Task
import com.ToDoCompass.database.TaskAlarm
import com.ToDoCompass.ui.theme.AppMainTheme
import com.ToDoCompass.ui.theme.getTextButtonColors
import com.ToDoCompass.uiComponents.Modals.DialogWrapper
import com.marosseleng.compose.material3.datetimepickers.date.ui.dialog.DatePickerDialog
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

@AndroidEntryPoint
class IntentsActivity() : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        val receivedIntent = intent
        val alarmId = receivedIntent?.getIntExtra(
            StringConstants.NOTIF_ALARM_ID, -1)
        //Log.v("intenstactivity oncreate", "Launch receivedIntent = ${receivedIntent?.action}")
        super.onCreate(savedInstanceState)
        setContent {
            val vm: MainViewModel = hiltViewModel()
            vm.dismissNotification(
                alarmId
            )
            //Log.v("intenstactivity oncreate", "setContent Launch")
            var initialized by rememberSaveable{ mutableStateOf(false) }
            LaunchedEffect(Unit){
                delay(Constants.appInitializedDelay)
                initialized=true
                //delay(appDarkThemeGetDelay)
                //darkTheme = viewModel.darkThemeGet()
            }
            AppMainTheme(
                viewModel = vm,
                //darkTheme = darkTheme
            )
            {
                AnimatedVisibility(
                    visible = initialized,
                    enter = fadeIn(
                        animationSpec = tween(
                            durationMillis = 200,
                            delayMillis = 0,
                            easing = EaseIn
                        )
                    ),
                    exit = fadeOut(
                        animationSpec = tween(
                            durationMillis = 100,
                            delayMillis = 0,
                            easing = EaseIn
                        )
                    )
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            //.padding(16.dp)
                            
                    ) {
                        alarmId?.let{
                            val taskOfAlarm = vm.getTaskOfAlarm(alarmId = alarmId)
                                .collectAsState(initial = emptyTask)
                            val alarmSent = vm.getAlarmWithIdFlow(alarmId = alarmId)
                                .collectAsState(initial = emptyTaskAlarm)
                            when (receivedIntent.action){
                                NOTIF_BUTTON3_INTENT -> ManageSentNotificationScreen(
                                    vm = vm,
                                    alarm = alarmSent.value,
                                    onDone = {
                                        finish()
                                    },
                                    task = taskOfAlarm.value
                                )
                                NOTIF_FULL_SCREEN_INTENT -> FullScreenIntentScreen(
                                    vm,
                                    alarm = alarmSent.value,
                                    onDone = {
                                        finish()
                                    },
                                    task = taskOfAlarm.value
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ManageSentNotificationScreen(
    vm : MainViewModel,
    alarm : TaskAlarm?,
    onDone : () -> Unit = {},
    task : Task?,
){
    var showConfirm by rememberSaveable { mutableStateOf(false) }
    var doneRescheduling by rememberSaveable { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    var datePick by rememberSaveable { mutableStateOf(false) }
    var timePick by rememberSaveable { mutableStateOf(false) }
    var pickedDate by rememberSaveable{ mutableStateOf(LocalDate.now()) }
    var pickedTime by rememberSaveable{ mutableStateOf(LocalTime.now()) }
    
    val context = LocalContext.current
    
    LaunchedEffect(doneRescheduling) {
        if (doneRescheduling){
            vm.updateAndScheduleAlarm(
                alarm = alarm,
                active = true,
                date = pickedDate.toString(),
                time = pickedTime.toString()
            )
            onDone()
        }
    }
    
    
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(40.dp))
        Text("ToDo Compass alarm ${task?.let{"for task: "+it.title} ?:""}")
        Spacer(modifier = Modifier.height(80.dp))
        Text("Ask again:")
        Row(){
            Button(onClick = {
                scope.launch{
                    val timeNowPlus = LocalDateTime.now().plusMinutes(15)
                    pickedDate = timeNowPlus.toLocalDate()
                    pickedTime = timeNowPlus.toLocalTime()
                    doneRescheduling = true
                }
            }){
                Text("in 15 minutes")
            }
            Spacer(modifier = Modifier.width(5.dp))
            Button(onClick = {
                scope.launch{
                    val timeNowPlus = LocalDateTime.now().plusMinutes(30)
                    pickedDate = timeNowPlus.toLocalDate()
                    pickedTime = timeNowPlus.toLocalTime()
                    doneRescheduling = true
                }
            }){
                Text("in 30 minutes")
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Row(){
            
            Button(onClick = {
                scope.launch{
                    val timeNowPlus = LocalDateTime.now().plusHours(1)
                    pickedDate = timeNowPlus.toLocalDate()
                    pickedTime = timeNowPlus.toLocalTime()
                    doneRescheduling = true
                }
            }){
                Text("in 1 hour")
            }
            Spacer(modifier = Modifier.width(5.dp))
            Button(onClick = {
                scope.launch{
                    val timeNowPlus = LocalDateTime.now().plusHours(3)
                    pickedDate = timeNowPlus.toLocalDate()
                    pickedTime = timeNowPlus.toLocalTime()
                    doneRescheduling = true
                }
            }){
                Text("in 3 hours")
                
            }
            Spacer(modifier = Modifier.width(5.dp))
            Button(onClick = {
                datePick = true
            }){
                Icon(ImageVector.vectorResource(R.drawable.baseline_access_time_24),"")
                Text("Set time")
                
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        Button(
            colors = getTextButtonColors(),
            onClick = {
                scope.launch {
                    alarm?.let{
                        vm.updateEntityInDB(it.copy(active = false))
                    }
                    onDone()
                }
            }
        ){
            //Icon(ImageVector.vectorResource(R.drawable.baseline_access_time_24),"")
            Text("Dismiss")
            
        }
        Spacer(modifier = Modifier.height(15.dp))
        IntentTaskCard(
            vm = vm,
            task = task
        )
    }
    
    if (datePick) {
        DatePickerDialog(
            title = {
                Text(StringConstants.DATE_PICK_TITLE,
                     style = MaterialTheme.typography.headlineSmall)
            },
            initialDate = pickedDate,
            onDismissRequest = {
                datePick = false
                timePick = false
            },
            onDateChange = {
                if (it.compareTo(LocalDate.now())>=0) {
                    pickedDate = it
                    timePick = true
                    datePick = false
                    if (!showConfirm){
                        timePick = true
                    }
                }
                else{
                    val toast =
                        Toast.makeText(context, PICKED_TIME_IN_PAST, Toast.LENGTH_SHORT)
                    toast.show()
                }
                
                
            }
        )
    }
    if (timePick) {
        TimePickerDialog(
            title = {
                Text(StringConstants.TIME_PICK_TITLE,
                     style = MaterialTheme.typography.headlineSmall)
                Text("Picked date: ${formatToString(pickedDate, "dd.MM.yyyy")}",
                     style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            },
            initialTime = pickedTime,
            onDismissRequest = {
                timePick = false
                if (!showConfirm){
                    datePick = true
                }
            },
            onTimeChange = {
                val pickedDateTime = LocalDateTime.of(pickedDate, it)
                if (pickedDateTime.compareTo(LocalDateTime.now())>=0){
                    pickedTime = it
                    timePick = false
                    showConfirm = true
                }
                else {
                    val toast =
                        Toast.makeText(context, PICKED_TIME_IN_PAST, Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
        
        )
    }
    
    if (showConfirm){
        DialogWrapper(
            mainLabel = "Reschedule alarm to",
            content = {
                val pickedDateTime = LocalDateTime.of(pickedDate, pickedTime)
                val millisFromNowToPicked = pickedDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()-LocalDateTime.now().toInstant(
                    ZoneOffset.UTC).toEpochMilli()
                Column(){
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
                    /*
                    Text("${pickedDateTime.toStringInFormat("HH:mm, dd.MM.yyyy")}?",
                         style = MaterialTheme.typography.headlineMedium.copy(
                             fontWeight = FontWeight.Bold
                         )
                    )
                    */
                    Text(millisFromNowToPicked.toTimeUnitString() +" from now")
                }
            },
            onConfirm = {
                doneRescheduling = true
                showConfirm = false
            },
            onDismiss = {
                showConfirm = false
            }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FullScreenIntentScreen(
    vm : MainViewModel,
    alarm : TaskAlarm?,
    onDone : () -> Unit = {},
    task : Task?,
){
    Column(){
        ManageSentNotificationScreen(
            vm = vm,
            alarm = alarm,
            task = task,
            onDone = {onDone()}
        )
    }
}

@Composable
fun IntentTaskCard(
    vm : MainViewModel,
    task : Task?,
){
    task?.let{
        Column(horizontalAlignment = Alignment.CenterHorizontally){
            Text(it.title)
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}