package com.ToDoCompass.uiComponents.smallComponents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.ToDoCompass.LogicAndData.Constants

@Composable
fun IntegerInputField(
    inVal : String,
    label : String,
    maxLength : Int = 5,
    onInput : (String) -> Unit
) {
    TextField(
        value = inVal,
        onValueChange = {
            if ((it.isEmpty()) || (it.matches(Constants.onlyNumbersPattern) && it.length<=maxLength)) {
                onInput(it)
            }
        },
        label = {
            Text(
                text = label
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        /*
        trailingIcon = {when(isTask) {
            true -> null
            false ->
                Icon(
                    modifier = Modifier
                        .clickable(
                            onClick = {

                            }
                        )
                    ,
                    imageVector = Icons.Filled.Info,
                    contentDescription = ""
                )
        }
        },
        */
    )
}

@Composable
fun StringInputField(
    inVal : String,
    label : String = "",
    shouldFocus : Boolean,
    onInput : (String) -> Unit
) {
    val focusRequester = FocusRequester()
    var textFieldLoaded by remember { mutableStateOf(false) }
    TextField(
        modifier = when(shouldFocus) {
            true -> Modifier
                .focusRequester(focusRequester)
                .onGloballyPositioned {
                    if (!textFieldLoaded) {
                        focusRequester.requestFocus() // IMPORTANT
                        textFieldLoaded = true // stop cyclic recompositions
                    }
                }
            false -> Modifier
        },
        value = inVal,
        onValueChange = {
            if (it.length<50) {
                onInput(it)
            }
        },
        label = {
            Text(
                text = label
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences),
        singleLine = true,

    )
}

@Composable
fun profileClickStepInfo() {
    
    Box(){
    }

}

@Composable
fun EditableText(
    inputText : String = "",
    
){
    var textFieldValue : TextFieldValue by remember{mutableStateOf( TextFieldValue(
        text = inputText
    ))}
}