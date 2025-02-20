package com.ToDoCompass.uiComponents.smallComponents

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.ToDoCompass.LogicAndData.Constants.Companion.normalFontSize
import com.ToDoCompass.LogicAndData.newLineAtCenter
import kotlinx.coroutines.delay

@Composable
fun mainTextBox(textIn : String, order : Int){
    var fontSize by rememberSaveable { mutableStateOf(13.0) }
    var targetFontSize by rememberSaveable { mutableStateOf(normalFontSize) }
    var readyToDraw by rememberSaveable { mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf(textIn) }
    var inflated by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(textIn){
        text=textIn
    }
    LaunchedEffect(order){
        fontSize = 1.06*fontSize
        targetFontSize = 1.06*targetFontSize
        inflated = true
        text = textIn
        delay(80)
        fontSize = fontSize/1.06
        targetFontSize = targetFontSize/1.06
        inflated = false
    }
    Text(
        text = text,
        fontSize = fontSize.sp,
        maxLines = 2,
        softWrap = false,
        textAlign = TextAlign.Center,
        lineHeight = (1.8*fontSize).sp,
        modifier = Modifier
            .drawWithContent {
            if (readyToDraw) drawContent()
        },
        onTextLayout = { textLayoutResult ->
            Log.v("onTextLayout", "fontSize = $fontSize")
            if (fontSize>targetFontSize+.5) {
                fontSize = targetFontSize
            }
            if (textLayoutResult.didOverflowWidth) {
                text = newLineAtCenter(textIn)
                if (fontSize>9.0 && inflated) {
                    fontSize = fontSize * 0.85
                }
                if (fontSize>7.3 && !inflated) {
                    fontSize = fontSize * 0.85
                }
            }
            else {
                readyToDraw = true
            }
            if (fontSize<=7.3) readyToDraw = true
        },
    )


}