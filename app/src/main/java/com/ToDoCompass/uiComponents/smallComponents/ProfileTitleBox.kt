package com.ToDoCompass.uiComponents.smallComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ToDoCompass.ViewModels.MainViewModel
import com.ToDoCompass.uiComponents.Modals.SelectProfileMenu

@Composable
fun profileTitleBox(viewModel : MainViewModel,
                  profileChanged : () -> Unit,
                  modifier : Modifier = Modifier,
                  clickable : Boolean = true){
    val profileTitle by viewModel.profileTitleFlow.collectAsStateWithLifecycle(initialValue = "")
    Box(modifier = modifier
        .fillMaxSize()
        .clickable(enabled = clickable
            ,
            onClick = {
                viewModel.setUiState(selectProfile = true)
            }
        ),
        contentAlignment = Alignment.Center) {
        if (profileTitle != null) {
            Text(text = profileTitle)
        }
        else  {
            Text("")
            viewModel.setFirstProfileId()
        }
        if(clickable) {
            SelectProfileMenu(
                viewModel,
                profileChanged = { profileChanged() }
            )
        }
    }
}