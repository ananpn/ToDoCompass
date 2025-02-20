package com.ToDoCompass.uiComponents.TaskBoxes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ToDoCompass.LogicAndData.getGroupColor
import com.ToDoCompass.LogicAndData.offsetToIntOffset
import com.ToDoCompass.ViewModels.UiViewModel

@Composable
fun NewFakeBox(
    uiVM : UiViewModel,
) {
    val elevation = 16.dp

    var readyToDraw by remember{ mutableStateOf(false)}

    var initialPointerPosition by remember{ mutableStateOf( IntOffset.Zero)}
    val pointerPositionState = uiVM.pointerPositionFlow.collectAsState()


    LaunchedEffect(uiVM.hasAddedItem){
        if (uiVM.hasAddedItem) {
            readyToDraw = true
        }
        else readyToDraw = false
    }

    Box(
        modifier = Modifier
            .alpha(
                when (readyToDraw) {
                    true -> 1f
                    false -> 0f
                }
            )
            .zIndex(10f)
            .offset { offsetToIntOffset(pointerPositionState.value + uiVM.initialFakeBoxOffset) }
            .width(170.dp)
            .height(80.dp)
            .clip(shape = RoundedCornerShape(4))
            .shadow(elevation)
            .background(//color = Color.Red
                getGroupColor(4).copy(alpha = 0.9f)
            )
    )
    {
        TaskContent(uiVM.newItem)
    }
}

fun intPixelsToDp(density : Density, input : Int) : Dp {
    return density.run{
        input.toDp()
    }
}

fun offsetInsideBox(boxPosition : Offset, initialPointerPosition : Offset, size : IntSize) : Offset {
    var output = initialPointerPosition

    return Offset.Zero
}
/*

Box(modifier = Modifier
    .offset() {
        //IntOffset(x = 0, y = (animationOffsetState.value - oldPosition).toInt())
        when (animating){
            //when (!isFake&&notUnderFakeBox&&item.isDrawn) {
            true -> IntOffset(x = 0,
                y = (animationOffsetState.value-positionInRoot.y).toInt())
            //y = (animatable.value).toInt())
            false -> {
                if (notUnderFakeBox) IntOffset(x = 0, y = (offset).toInt())
                else IntOffset.Zero}
        }
    }
    .alpha(
        if (notUnderFakeBox) 1f
        else if (!isDragging && item.isDrawn) 1f
        else if (!isFake) 0f
        else 1f
    )
    .width(150.dp)
    .height(
        when (isFake) {
            true -> 80.dp
            false -> 80.dp
        }
    )
    .clip(shape = RoundedCornerShape(3))
    .shadow(elevation.value)
    .background(color = bgcolor)

){
    taskCard(item)
}
*/
