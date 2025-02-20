package com.ToDoCompass.Notifications

/*
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.media3.common.util.NotificationUtil.createNotificationChannel
import com.ToDoCompass.R

// Function to show a custom notification with a button
fun showCustomNotification(context: Context) {
    val notificationManager = NotificationManagerCompat.from(context)
    val channelId = "custom_notification_channel"
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        createNotificationChannel(context, channelId)
    }
    
    // Create a Composable for the custom layout
    val customNotificationContent = @Composable {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Custom Notification")
            Button(onClick = { */
/* Button action *//*
 }) {
                Text(text = "Button")
            }
        }
    }
    
    // Set content of notification using ComposeView
    val notificationContent = ComposeView(context).apply {
        setContent {
            customNotificationContent()
        }
    }
    
    // Build the notification
    val notification = Notification.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .cm
        .build()
    
    // Show the notification
    notificationManager.notify(*/
/* Notification ID *//*
, notification)
}

private fun createNotificationChannel(context: Context, channelId: String) {
    val channelName = "Custom Notification Channel"
    val channelDescription = "Channel for custom notifications"
    val channelImportance = NotificationManager.IMPORTANCE_DEFAULT
    val notificationChannel = NotificationChannel(channelId, channelName, channelImportance).apply {
        description = channelDescription
        enableLights(true)
        lightColor = 34
    }
    
    getSystemService(context, NotificationManager::class.java)?.createNotificationChannel(notificationChannel)
}





@Composable
fun AlarmNotificationOverlay(
    onSnoozeClicked: () -> Unit,
    onDismissClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Alarm!",
            fontSize = 24.sp,
            color = Color.Red
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onSnoozeClicked) {
            Text(text = "Snooze")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onDismissClicked) {
            Text(text = "Dismiss")
        }
    }
}





// Function to show the alarm notification overlay
@Composable
fun showAlarmOverlay(context: Context, onSnoozeClicked: () -> Unit, onDismissClicked: () -> Unit) {
    val alarmOverlayView = LayoutInflater.from(context).inflate(R.layout.overlay_layout, null)
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    
    windowManager.addView(alarmOverlayView, WindowManager.LayoutParams().apply {
        // Configure layout parameters to display overlay on top of everything
        type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.MATCH_PARENT
    })
    */
/*
    alarmOverlayView.findViewById<ComposeView>(R.id.compose_view).setContent {
        AlarmNotificationOverlay(
            onSnoozeClicked = onSnoozeClicked,
            onDismissClicked = onDismissClicked
        )
    }
    *//*

}

// Function to dismiss the alarm notification overlay
fun dismissAlarmOverlay(context: Context, overlayView: View) {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowManager.removeView(overlayView)
}*/
