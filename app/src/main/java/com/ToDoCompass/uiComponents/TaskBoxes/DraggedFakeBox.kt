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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ToDoCompass.LogicAndData.Constants.Companion.emptyListItem
import com.ToDoCompass.LogicAndData.getGroupColorDraggedFake
import com.ToDoCompass.LogicAndData.offsetToIntOffset
import com.ToDoCompass.ViewModels.DraggedItemStates
import com.ToDoCompass.ViewModels.UiViewModel
import kotlinx.coroutines.delay

@Composable
fun DraggedFakeBox(
    uiVM : UiViewModel,
) {
    val elevation = 32.dp

    var readyToDraw by remember{ mutableStateOf(false)}

    var initialPointerPosition by remember{ mutableStateOf( IntOffset.Zero)}
    val pointerPositionState = uiVM.pointerPositionFlow.collectAsState()



    LaunchedEffect(uiVM.isDragging){
        if (uiVM.isDragging) {
            //Log.v("fakebox LE isdragging", "uiVM.draggedItem = ${uiVM.draggedItem}")
            initialPointerPosition = offsetToIntOffset(pointerPositionState.value)
            while (uiVM.draggedItemState != DraggedItemStates.POSITIONGOT)
            {
                delay(10)
            }
            uiVM.setInitialFakeBoxOffset()
            if (uiVM.draggedItem != emptyListItem){
                readyToDraw = true
                uiVM.letDraggedItemState(DraggedItemStates.DRAWING)
            }
            else readyToDraw = false
        }
        else {
            readyToDraw = false
        }
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
            .width(when (readyToDraw) {
                       true -> 170.dp
                       false -> 169.dp
                   }
            )
            .height(80.dp)
            .onSizeChanged {
                if (readyToDraw)
                    uiVM.updateDraggedBoxSize(it)
            }
            .clip(shape = RoundedCornerShape(4))
            .shadow(elevation)
            .background(color = //Color.Red
                getGroupColorDraggedFake(uiVM.inGroup)
            )
        
    )
    {
        TaskContent(uiVM.draggedItem)
    }
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
