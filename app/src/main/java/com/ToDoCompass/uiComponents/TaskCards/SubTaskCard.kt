package com.ToDoCompass.uiComponents.TaskCards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.ToDoCompass.LogicAndData.StringConstants.Companion.INFO_DELETE_SUBTASK
import com.ToDoCompass.LogicAndData.StringConstants.Companion.SUBTASK_CARD_TITLE
import com.ToDoCompass.ViewModels.MainViewModel
import com.ToDoCompass.database.ListItem
import com.ToDoCompass.database.toTask
import com.ToDoCompass.uiComponents.smallComponents.CloseButton
import com.ToDoCompass.uiComponents.smallComponents.DeleteButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubTaskCard(
    vm : MainViewModel,
    showedSubTask : ListItem,
    onClose : () -> Unit,
    focusManager : FocusManager,
){
    //val focusManager = LocalFocusManager.current
    
    var subTaskName by remember(showedSubTask) {
        mutableStateOf(showedSubTask.title)
    }
    val scope = rememberCoroutineScope()
    Card(
        modifier = Modifier
            .fillMaxWidth(0.87f)
            .height(300.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            //containerColor = getGroupColorCard(group = item.group),
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        shape = RoundedCornerShape(5),


        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Row(horizontalArrangement = Arrangement.SpaceBetween){
                Text(
                    text = "Subtask",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.weight(1f))
                DeleteButton(
                    onDeletePressed = {
                        vm.openDeleteDialog(
                            entity = showedSubTask.toTask(),
                            info = INFO_DELETE_SUBTASK
                        )
                    }
                )
                Spacer(Modifier.width(2.dp))
                CloseButton(modifier = Modifier, onClose = {onClose()})
            }
            TextField(
                modifier = Modifier,
                //readOnly = true,
                value = subTaskName,
                onValueChange = {newValue ->
                    subTaskName = newValue
                },
                label = { Text(SUBTASK_CARD_TITLE) },
                colors = OutlinedTextFieldDefaults.colors(),
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {scope.launch{
                        vm.updateEntityInDB(
                            showedSubTask.copy(title = subTaskName).toTask()
                        )
                        vm.updateEveryThingFromDB()
                        focusManager.clearFocus()
                    }
                    }
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            
            AlarmBox(
                vm = vm,
                item = showedSubTask
            )
        }
    }
}
