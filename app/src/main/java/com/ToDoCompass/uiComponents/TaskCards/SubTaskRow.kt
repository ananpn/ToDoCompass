package com.ToDoCompass.uiComponents.TaskCards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ToDoCompass.database.ListItem
import com.ToDoCompass.uiComponents.smallComponents.ActivateIcon
import com.ToDoCompass.uiComponents.smallComponents.DeleteIcon
import com.ToDoCompass.uiComponents.smallComponents.DoneIndicator

@Composable
fun SubTaskCardRowContent(item : ListItem, taskDoneUp : (Boolean) -> Unit){
    var isDone by remember(item.taskDone){ mutableStateOf(item.taskDone) }
    //val alarms = remember{}
    Text(
        text = item.title,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.width(5.dp))
    Spacer(modifier = Modifier.width(5.dp))
    DoneIndicator(
        isDone = isDone,
        taskDoneUp = {
            taskDoneUp(it)
        }
    )
}

@Composable
fun RowScope.DoneTaskRowContent(
    item : ListItem,
    activateTaskUp : () -> Unit,
    deleteTaskFinally : () -> Unit,
) {
    var isDone by remember(item.taskDone) { mutableStateOf(item.taskDone) }
    //val alarms = remember{}
    Text(
        modifier = Modifier.weight(7f),
        text = item.title,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.width(5.dp))
    Spacer(modifier = Modifier.width(5.dp))
    Row(modifier = Modifier.weight(2f),
        horizontalArrangement = Arrangement.SpaceEvenly) {
        DeleteIcon(deleteClicked = deleteTaskFinally)
        ActivateIcon(clicked = activateTaskUp)
    }
    
}


@Composable
fun TaskRow(
    modifier : Modifier = Modifier,
    content : @Composable RowScope.() -> Unit,
){
    Row(
        modifier = modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
            .height(25.dp)
            .clip(shape = RoundedCornerShape(4)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        content = content
        //.background(color = bgcolor)
    )
    
}