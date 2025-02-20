package com.ToDoCompass.uiComponents.TaskBoxes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ToDoCompass.LogicAndData.Constants.Companion.TDCdivider0
import com.ToDoCompass.R
import com.ToDoCompass.database.ListItem
import com.ToDoCompass.uiComponents.TaskCards.formatAlarmTimeToDisplay

@Composable
fun TaskContent(item : ListItem){
    Column(modifier = Modifier.padding(5.dp).fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ){
        Row(modifier = Modifier.fillMaxWidth(),
            //horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.title,
                fontWeight = FontWeight.Bold
            )
        }
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
            //horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (item.alarmString.isNotEmpty()) {
                val alarmData = item.alarmString.split(TDCdivider0)
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_alarm_24),
                    "",
                    modifier = Modifier.size(15.dp)
                )
                Text(text = alarmData[0],
                     fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(5.dp))
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    "",
                    modifier = Modifier.size(15.dp)
                )
                //TODO better time formatter maybe String -> String with both old and new formats..
                Text(
                    text = formatAlarmTimeToDisplay(alarmData[1], time = ""),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    
                    )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = alarmData[2],
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

}