package com.ToDoCompass.uiComponents.Modals

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.ToDoCompass.LogicAndData.StringConstants
import com.ToDoCompass.LogicAndData.StringConstants.Companion.CANCEL_BUTTON
import com.ToDoCompass.ui.theme.getTextButtonColors

@Composable
fun DialogWrapper(
    //vm : MainViewModel,
    mainLabel : String,
    confirmButtonText : String = StringConstants.CONFIRM_BUTTON,
    dismissButtonText : String? = CANCEL_BUTTON,
    onConfirm: () -> Unit,
    onDismiss : () -> Unit = {},
    content : @Composable (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = {
            Text(
                text = mainLabel
            )
        },
        text = {
            content?.invoke()
        },
        confirmButton = {
            TextButton(
                colors = getTextButtonColors(),
                onClick = {
                    onConfirm()
                }
            ) {
                Text(
                    text = confirmButtonText
                )
            }
        },
        dismissButton = {
            when (dismissButtonText){
                "" -> {}
                null -> {}
                else ->
                    TextButton(
                        colors = getTextButtonColors(),
                        onClick = {
                            onDismiss()
                        }
                    ) {
                        Text(
                            text = dismissButtonText
                        )
                    }
            }
        }
        
    )
    
    
}