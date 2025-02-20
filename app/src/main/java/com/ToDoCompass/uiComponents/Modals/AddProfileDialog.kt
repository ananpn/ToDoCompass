package com.ToDoCompass.uiComponents.Modals

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ToDoCompass.LogicAndData.StringConstants.Companion.ADD_BUTTON
import com.ToDoCompass.LogicAndData.StringConstants.Companion.ADD_PROFILE
import com.ToDoCompass.LogicAndData.StringConstants.Companion.CANCEL_BUTTON
import com.ToDoCompass.LogicAndData.StringConstants.Companion.EMPTY_STRING
import com.ToDoCompass.LogicAndData.StringConstants.Companion.GIVE_PROFILE_TITLE
import com.ToDoCompass.LogicAndData.StringConstants.Companion.PROFILE_ADD_FAIL
import com.ToDoCompass.ViewModels.MainViewModel
import com.ToDoCompass.database.TaskProfile
import com.ToDoCompass.uiComponents.smallComponents.StringInputField
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AddProfileDialog(
    vm : MainViewModel,
) {
if (vm.openNewProfileDialog) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var title by remember { mutableStateOf(EMPTY_STRING) }
    DialogWrapper(
        mainLabel = ADD_PROFILE,
        confirmButtonText = ADD_BUTTON,
        dismissButtonText = CANCEL_BUTTON,
        onConfirm = {
            scope.launch {
                try{
                    val profile= TaskProfile(
                        profileTitle = title)
                    vm.insertEntityToDB(profile)
                    delay(50)
                    vm.updateProfilesFromDB()
                    delay(50)
                    vm.closeNewProfileDia()
                }
                catch(e : Exception){
                    val toast =
                        Toast.makeText(context, PROFILE_ADD_FAIL, Toast.LENGTH_SHORT) // in Activity
                    toast.show()
                }
                
            }
            
        },
        onDismiss = {
            vm.closeNewProfileDia()
        },
    ){Column {
        StringInputField(
            inVal = title,
            label = GIVE_PROFILE_TITLE,
            shouldFocus = true,
            onInput = {title = it}
        )
        Spacer(modifier = Modifier.height(14.dp))
    }
    }

    
}//IF
}

/*
    AlertDialog(
        onDismissRequest = {
            vm.closeNewProfileDia()
        },
        title = {
            Text(
                text = ADD_PROFILE
            )
        },
        text = {
            Column {
                stringInputField(
                    inVal = title,
                    label = GIVE_PROFILE_TITLE,
                    shouldFocus = true,
                    onInput = {title = it}
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        },
        confirmButton = {
            TextButton(
                colors = getTextButtonColors(),
                onClick = {
                    scope.launch {
                        try{
                            val profile= TaskProfile(
                                profileTitle = title)
                            vm.insertProfile(profile)
                            delay(50)
                            vm.updateProfilesFromDB()
                            delay(50)
                            vm.closeNewProfileDia()
                        }
                        catch(e : Exception){
                            val toast =
                                Toast.makeText(context, PROFILE_ADD_FAIL, Toast.LENGTH_SHORT) // in Activity
                            toast.show()
                        }

                    }

                }
            ) {
                Text(
                    text = ADD_BUTTON
                )
            }
        },
        dismissButton = {
            TextButton(
                colors = getTextButtonColors(),
                onClick = {
                    vm.closeNewProfileDia()
                }
            ) {
                Text(
                    text = CANCEL_BUTTON
                )
            }
        }
    )
*/