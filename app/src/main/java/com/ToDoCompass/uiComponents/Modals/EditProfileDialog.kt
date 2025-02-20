package com.ToDoCompass.uiComponents.Modals

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ToDoCompass.LogicAndData.StringConstants.Companion.EDIT_PROFILE
import com.ToDoCompass.LogicAndData.StringConstants.Companion.GIVE_PROFILE_TITLE
import com.ToDoCompass.LogicAndData.StringConstants.Companion.PROFILE_EDIT_FAIL
import com.ToDoCompass.LogicAndData.StringConstants.Companion.UPDATE_BUTTON
import com.ToDoCompass.ViewModels.MainViewModel
import com.ToDoCompass.database.TaskProfile
import com.ToDoCompass.uiComponents.smallComponents.DeleteButton
import com.ToDoCompass.uiComponents.smallComponents.StringInputField
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
    vm: MainViewModel,
    onDismiss : () -> Unit = {},
    onDelete : () -> Unit = {},
) {(vm.entityToUpdate as? TaskProfile)?.also{ profileToUpdate ->
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    //val upProfile by remember { mutableStateOf(vm.uiState.entityToUpdate as? TaskProfile ?:emptyProfile) }
    var title by remember { mutableStateOf(
        profileToUpdate.profileTitle
    )
    }
    
    DialogWrapper(
        mainLabel = EDIT_PROFILE,
        confirmButtonText = UPDATE_BUTTON,
        onConfirm = { scope.launch{
            try{
                var output = profileToUpdate.copy(
                    profileTitle = title,
                )
                //vm.setUiState(entityToUpdate = output)
                vm.updateEntityInDB(output)
                delay(50)
                onDismiss()
            }
            catch(e : Exception){
                val toast =
                    Toast.makeText(context, PROFILE_EDIT_FAIL, Toast.LENGTH_SHORT) // in Activity
                toast.show()
            }
        }
        },
        onDismiss = {
            onDismiss()
        },
    ){
        Box(modifier = Modifier.padding(6.dp)) {
            DeleteButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 0.dp, y = -60.dp)
                ,
                onDeletePressed = {
                    vm.openDeleteDialog(profileToUpdate)
                }
            )
            Column(){
                StringInputField(
                    inVal = title,
                    label = GIVE_PROFILE_TITLE,
                    shouldFocus = false,
                    onInput = {title = it}
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
    
}//if
}

