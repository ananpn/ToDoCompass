package com.ToDoCompass.uiComponents.smallComponents

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import com.ToDoCompass.database.TaskProfile

@Composable
fun ProfileCard(profile : TaskProfile){
    Text(
        text = profile.profileTitle,
        textAlign = TextAlign.Center
    )
}