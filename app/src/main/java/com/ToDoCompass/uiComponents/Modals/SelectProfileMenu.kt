package com.ToDoCompass.uiComponents.Modals


import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ToDoCompass.LogicAndData.Constants.Companion.emptyProfile
import com.ToDoCompass.LogicAndData.StringConstants.Companion.ADD
import com.ToDoCompass.ViewModels.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun SelectProfileMenu(vm : MainViewModel,
                      profileChanged : () -> Unit
) {
    val profiles by vm._dbProfiles.collectAsStateWithLifecycle(initialValue = listOf())
    var buttonPressed by rememberSaveable{ mutableStateOf(false)}
    LaunchedEffect(buttonPressed){
        if (buttonPressed) {
            delay(50)
            vm.setUiState(
                upProfile = profiles.find { it.idProfile == vm.uiState.dispProfileId } ?: emptyProfile
            )
            delay(50)
            buttonPressed=false
        }
    }
    DropdownMenu(
        modifier = Modifier,
        expanded = vm.uiState.selectProfile,
        onDismissRequest = {
            vm.setUiState(selectProfile = false)
        })
    {
        profiles.forEach { profile ->
            DropdownMenuItem(
                modifier = Modifier.background(
                    color = when(profile.idProfile == vm.uiState.dispProfileId){
                        false -> MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                        true -> MaterialTheme.colorScheme.surfaceColorAtElevation(15.dp)
                    }
                ),
                text = { Text(profile.profileTitle) },
                onClick = {
                    if (profile.idProfile != vm.uiState.dispProfileId) {
                        buttonPressed=true
                        vm.setUiState(
                            upProfile = profile,
                            dispProfileId = profile.idProfile ?:0,
                            selectProfile = false
                        )
                        vm.setDispProfile(profile.idProfile ?:0)
                        vm.updateDispProfileId()
                        profileChanged()
                    }
                }
            )
        }
        DropdownMenuItem(
            text = {Text(ADD)},
            leadingIcon = { Icon(Icons.Filled.Add, "")},
            onClick = {
                buttonPressed = true
                vm.openNewProfileDia()
            }
        )
    }

    AddProfileDialog(vm)
}