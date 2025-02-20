package com.ToDoCompass.uiComponents.smallComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun DoneIndicator(isDone : Boolean, taskDoneUp : (Boolean) -> Unit){
    Box(modifier = Modifier
        .size(23.dp)
        .clip(CircleShape)
        .background(color = MaterialTheme.colorScheme.onSecondary)
        .clickable(
            onClick = {
                taskDoneUp(!isDone)
            }
        ),
        contentAlignment = Alignment.Center
    ){
        Icon(imageVector = when (isDone) {
                true -> Icons.Filled.Done
                false -> Icons.Filled.Clear
            },
            contentDescription = "DoneGray",
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun DeleteIcon(deleteClicked : () -> Unit){
    Box(modifier = Modifier
        .size(23.dp)
        .clip(CircleShape)
        .background(color = MaterialTheme.colorScheme.onSecondary)
        .clickable(
            onClick = { deleteClicked() }
        ),
        contentAlignment = Alignment.Center
    ){
        Icon(
            imageVector = Icons.Outlined.Delete,
            contentDescription = "DoneGray",
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun ActivateIcon(clicked : () -> Unit){
    Box(modifier = Modifier
        .size(23.dp)
        .clip(CircleShape)
        .background(color = MaterialTheme.colorScheme.onSecondary)
        .clickable(
            onClick = { clicked() }
        ),
        contentAlignment = Alignment.Center
    ){
        Icon(
            imageVector = Icons.Outlined.Refresh,
            contentDescription = "RefreshGray",
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}