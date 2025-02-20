package com.ToDoCompass.uiComponents.smallComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun verticalShadow(
    width : Dp,
    alpha : Float,
    color : Color = MaterialTheme
        .colorScheme.outline
){
    Box(modifier = Modifier
        .fillMaxHeight()
        .width(width)
        .background(
            color =color
                .copy(alpha = alpha)
        )
    )
}

@Composable
fun horizontalShadow(
    height : Dp,
    alpha : Float,
    color : Color = MaterialTheme
        .colorScheme.outline
){
    Box(modifier = Modifier
        .height(height)
        .fillMaxWidth()
        .background(
            color =color
                .copy(alpha = alpha)
        )
    )
}

@Composable
fun boxShadows(
    upHeight : Dp = 0.dp,
    downHeight : Dp = 0.dp,
    rightWidth : Dp = 0.dp,
    leftWidth : Dp = 0.dp,
    boxWidth : Dp,
    boxHeight : Dp,
    upAlpha : Float = 0f,
    downAlpha : Float = 0f,
    leftAlpha : Float = 0f,
    rightAlpha : Float = 0f,
    color : Color = MaterialTheme
        .colorScheme.outline
){
    Column(modifier = Modifier.fillMaxSize()){
        Row(modifier = Modifier
            .height(upHeight)
        ){
            horizontalShadow(
                height = upHeight,
                alpha = upAlpha,
                color = color)
        }
        Row(modifier = Modifier
            .height(boxHeight-upHeight-downHeight)
            .width(boxWidth)
        ){
            verticalShadow(
                width = leftWidth,
                alpha = leftAlpha,
                color = color
            )
            Box(modifier = Modifier
                .height(0.dp)
                .width(boxWidth-leftWidth-rightWidth)
                .background(color = Color.Transparent)
            )
            verticalShadow(
                width = rightWidth,
                alpha = rightAlpha,
                color = color
            )
        }
        Row(modifier = Modifier
            .height(downHeight)
        ){
            horizontalShadow(
                height = downHeight,
                alpha = downAlpha,
                color = color)
        }
    }
}

/*
@Composable
fun gradientBoxShadow(
    upHeight : Dp = 0.dp,
    downHeight : Dp = 0.dp,
    rightWidth : Dp = 0.dp,
    leftWidth : Dp = 0.dp,
    boxWidth : Dp,
    boxHeight : Dp,
    upAlpha : Float = 0f,
    downAlpha : Float = 0f,
    leftAlpha : Float = 0f,
    rightAlpha : Float = 0f,
    color : Color = MaterialTheme
        .colorScheme.outline
){
    val gradient = Brush.linearGradient(
        colors = listOf(color.copy(alpha = upAlpha), color.copy(alpha = downAlpha)),
        start = Offset(Float.NEGATIVE_INFINITY, 0f),
        end = Offset(0f, Float.NEGATIVE_INFINITY)
    )
    Box(modifier = Modifier
        .fillMaxSize()
        .background(gradient)
        .drawWithContent {
            drawContent()

            drawRect(
                color = Color.Transparent,
                topLeft = Offset(100f, 100f),
                size = Size(200f, 200f)
            )
        }
    )
        {
    }
}
*/


