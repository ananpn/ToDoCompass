package com.ToDoCompass.uiComponents.smallComponents

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.ToDoCompass.LogicAndData.Constants.Companion.newLinePattern
import com.ToDoCompass.LogicAndData.Constants.Companion.whiteSpaceAnyWherePattern
import com.ToDoCompass.LogicAndData.breakWordAtCenter
import com.ToDoCompass.LogicAndData.countMatches
import com.ToDoCompass.LogicAndData.newLineAtCenter
import kotlinx.coroutines.delay
import kotlin.math.max

@Composable
fun AutoFitTextToFill(
    text : String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = MaterialTheme.colorScheme.onBackground,
    fontSize: TextUnit = style.fontSize,
    fontStyle: FontStyle = FontStyle.Normal,
    fontWeight: FontWeight? = style.fontWeight,
    fontFamily: FontFamily? = style.fontFamily,
    letterSpacing: TextUnit = style.letterSpacing,
    lineHeight: TextUnit = style.lineHeight,
    textDecoration: TextDecoration? = TextDecoration.None,
    textAlign: TextAlign = TextAlign.Center,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    softWrap: Boolean = true,
    maxLines: Int? = null,
    minLines: Int? = null,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    fitText : Boolean = false,
    vertical : Boolean = false,


    ){
    var textToDisp by remember{ mutableStateOf("") }
    //Log.v("autofittext", "vertical = $vertical, text = $text")
    LaunchedEffect(Unit){
        if (vertical){
            for (char in text){
                textToDisp += char.toString()+"\n"
            }
            textToDisp += "\n"
        }
        else {
            textToDisp = text
        }
    }
    //Log.v("autofittext", "vertical = $vertical, textToDisp = $textToDisp")
    if (!fitText) {
        Text(
            text = textToDisp,
            modifier = modifier,
            color = color,
            style = style,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines ?:Int.MAX_VALUE,
            minLines = minLines ?:1,
            onTextLayout = onTextLayout,
        )
    }
    if (fitText){
        var fittedLineHeight by remember{ mutableStateOf(lineHeight.value) }
        var fittedLetterSpacing by remember{ mutableStateOf(letterSpacing.value) }
        var isFitting by remember{ mutableStateOf(true) }
        Text(
            text = textToDisp,
            modifier = modifier,
            color = color,
            style = style,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = fittedLetterSpacing.sp,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = fittedLineHeight.sp,
            overflow = TextOverflow.Clip,
            softWrap = false,
            maxLines = maxLines ?:Int.MAX_VALUE,
            minLines = minLines ?:1,
            onTextLayout = {
                if (isFitting) {
                    if (vertical) {
                        if (!it.didOverflowHeight) {
                            fittedLineHeight *= 1.1f
                        }
                        if (it.didOverflowHeight) {
                            fittedLineHeight *= 1f/1.1f
                            isFitting = false
                        }
                    }
                    if (!vertical) {
                        if (!it.didOverflowWidth) {
                            fittedLetterSpacing *= 1.1f
                        }
                        if (it.didOverflowWidth) {
                            fittedLetterSpacing *= 1f/1.1f
                            isFitting = false
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun AutoFitTextBox(
    text : String,
    targetFontSize : Double,
    minimumFontSize : Double = targetFontSize*0.62,
    maxIndexOfRow : Int = 1000,
    animationOn : Boolean = false,
    animationKey : Any = 0,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = MaterialTheme.colorScheme.onBackground,
    //fontSize: TextUnit = style.fontSize,
    fontStyle: FontStyle = FontStyle.Normal,
    fontWeight: FontWeight? = style.fontWeight,
    fontFamily: FontFamily? = style.fontFamily,
    letterSpacing: TextUnit = style.letterSpacing,
    //lineHeight: TextUnit = style.lineHeight,
    textDecoration: TextDecoration? = TextDecoration.None,
    textAlign: TextAlign = TextAlign.Center,
    //overflow: TextOverflow = TextOverflow.Ellipsis,
    softWrap : Boolean = true,
    maxLines : Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    //fitText : Boolean = false,
    //vertical : Boolean = false,
){
    var fontSize by remember(text) { mutableStateOf(targetFontSize+3.0) }
    var targetFontSize by rememberSaveable { mutableStateOf(targetFontSize) }
    var readyToDraw by remember(text) { mutableStateOf(false) }
    var text by rememberSaveable(text, animationKey) { mutableStateOf(text) }
    var inflated by rememberSaveable(text) { mutableStateOf(false) }
    LaunchedEffect(animationKey){
        if (animationOn) {
            fontSize = 1.06*fontSize
            targetFontSize = 1.06*targetFontSize
            inflated = true
            delay(80)
            fontSize = fontSize/1.06
            targetFontSize = targetFontSize/1.06
            inflated = false
        }
    }
    Text(
        text = text,
        color = color,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        fontSize = fontSize.sp,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        maxLines = maxLines,
        softWrap = softWrap,
        textAlign = textAlign,
        lineHeight = lineHeightFromFontSize(fontSize).sp,
        //overFlow = TextOverflow.Ellipsis,
        modifier = Modifier
            .drawWithContent {
                if (readyToDraw) drawContent()
            },
        onTextLayout = { textLayoutResult ->
            if (fontSize>targetFontSize+.5) {
                fontSize = targetFontSize
            }
            readyToDraw = false
            if ((textLayoutResult.didOverflowWidth||textLayoutResult.didOverflowHeight) && !readyToDraw) {
                if (textLayoutResult.didOverflowWidth
                    && !softWrap
                    && countMatches(text, newLinePattern)<maxLines-1
                    && text.contains(" ")) {
                    text = newLineAtCenter(text)
                    fontSize = targetFontSize
                }
                else if (
                    textLayoutResult.didOverflowWidth
                    && (fontSize < minimumFontSize*1.2)
                    && !softWrap
                    && countMatches(text, whiteSpaceAnyWherePattern) == 0) {
                    text = breakWordAtCenter(text, maxIndexOfRow)
                    fontSize = targetFontSize
                }
                if (fontSize>(minimumFontSize*1.06)*1.16 && inflated) {
                    fontSize = fontSize * 0.95
                }
                if (fontSize>minimumFontSize*1.16 && !inflated) {
                    fontSize = fontSize * 0.85
                }
            }
            else {
                readyToDraw = true
            }
            if (fontSize<=minimumFontSize*1.16) readyToDraw = true
        },
    )
}

private fun lineHeightFromFontSize(size : Double) : Double{
    val coefficient = max(1.1, 2.0-(size)/(18.0))
    return coefficient*size
}