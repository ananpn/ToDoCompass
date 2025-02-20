package com.ToDoCompass.uiComponents.smallComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ToDoCompass.database.NotifType

@Composable
fun NotifTypeRow(
    notifType: NotifType,
    modifier : Modifier = Modifier
){
    //val rowModifier : Modifier = modifier.padding(5.dp)
    Row(modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ){
        Box(modifier = Modifier.weight(3f)){
            AutoFitTextBox(
                text = notifType.name,
                targetFontSize = 11.0,
            )
        }
        Box(modifier = Modifier.weight(4f)){
            AutoFitTextBox(
                text = notifType.soundFileName,
                targetFontSize = 11.0,
            )
        }
        Box(modifier = Modifier.weight(1f)){
            if (notifType.persistentLength > 0){
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = ""
                )
            }
            else{
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = ""
                )
            }
        }
        Box(modifier = Modifier.weight(1f)){
            if (notifType.rampUp){
                Icon(
                    imageVector = Icons.Outlined.ThumbUp,
                    contentDescription = ""
                )
            }
            else{
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = ""
                )
            }
        }
    }
}