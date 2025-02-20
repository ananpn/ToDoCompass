package com.ToDoCompass.uiComponents.TaskBoxes


import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ToDoCompass.LogicAndData.Constants.Companion.centerPress
import com.ToDoCompass.ViewModels.UiViewModel
import com.ToDoCompass.database.ListItem
import kotlinx.coroutines.delay


@Composable
fun itemConstructor(
    uiVM : UiViewModel,
    isDragging : Boolean = false,
    item : ListItem,
    bgcolor : Color,
    //rootPositionFlow : Flow<Int>,
    //scrollOffsetFlow : Flow<Float>
    //density : Density = LocalDensity.current
){
    var isDrawing by remember{ mutableStateOf(true)}

    //to hide the long pressed box
    LaunchedEffect(isDragging){
        if (isDragging){
            if (item.uniqueId == uiVM.draggedItem.uniqueId){
                delay(20)
                isDrawing = false
            }
            else isDrawing = true
        }
        else {isDrawing = true}
    }
/*

    LaunchedEffect(animating){
        if (item.uniqueId == 1){
            Log.v("itemconstr animating LE", "animating = $animating")
        }
    }
*/
/*
    val animationState = animateIntAsState(
        targetValue = targetValue,
        animationSpec = spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium),
        //tween(durationMillis = 150, 0, easing = EaseInOutSine),
        finishedListener = {
            springAnimation = false
            oldPosition = rootPosition.value
            //targetValue = oldPosition
            //offset = 0f
        },
        label = ""
    )
    */
/*
    Box(modifier = Modifier
        *//*
        .graphicsLayer {
            translationY
        }
        .offset() {
            IntOffset(
                x = 0,
                y = (animationState.value - targetValue)
            )
        }
            *//*
        .alpha(
            if (isDrawing && springAnimation) 1f
            else if (!isDragging && item.isDrawn) 1f
            else 0f
        )
        .width(150.dp)
        .height(80.dp)
        .clip(shape = RoundedCornerShape(3))
        .background(color = bgcolor)

    ){
        taskCard(item)
    }*/
    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(uiVM.draggedItem.uniqueId){
        if (uiVM.draggedItem.uniqueId == item.uniqueId){
            interactionSource.emit(interaction = centerPress)
        }
    }
    LaunchedEffect(uiVM.isDownPressed){
        if (!uiVM.isDownPressed){
            interactionSource.emit(interaction = PressInteraction.Release(centerPress))
        }
    }
    Box(modifier = Modifier
        .indication(
            interactionSource = interactionSource,
            indication = LocalIndication.current
        )
        /*
        .offset{

            IntOffset(x = 0,
                y= (oldPosition-rootPosition.value))

        }
        */
        .alpha(
            if (isDrawing) 1f
            else 0f
        )
        .width(170.dp)
        .height(80.dp)
        .clip(shape = RoundedCornerShape(4))
        .background(color = bgcolor)
    ){
        TaskContent(item)
    }

}

/*

fun groupCoeffY(group : Int) : Int{
    if (group<=1) return 0
    else return 1
}

fun groupCoeffX(group : Int) : Int{
    if (group.mod(2)==0) return 0
    else return 1
}


fun simulateDrag(startX: Float, startY: Float, dragDistance: Float) {
    val instrumentation = Instrumentation()
    val downTime = SystemClock.uptimeMillis()
    val eventTime = SystemClock.uptimeMillis()
    val event = MotionEvent.obtain(
        downTime,
        eventTime,
        MotionEvent.ACTION_DOWN,
        startX,
        startY,
        0
    )
    instrumentation.sendPointerSync(event)

    // Simulate dragging by sending move events
    val moveEvent = MotionEvent.obtain(
        downTime,
        eventTime,
        MotionEvent.ACTION_MOVE,
        startX + dragDistance,
        startY + dragDistance,
        0
    )
    instrumentation.sendPointerSync(moveEvent)

    // Simulate releasing the drag
    val upEvent = MotionEvent.obtain(
        downTime,
        eventTime,
        MotionEvent.ACTION_UP,
        startX + dragDistance,
        startY + dragDistance,
        0
    )
    instrumentation.sendPointerSync(upEvent)
}
*/



/*
    LaunchedEffect(vm.groupMoveState.isMoving){
        if (vm.groupMoveState.isMoving){
            if (group == vm.groupMoveState.fromGroup) {
                state.onDragCanceled()
                data.swapList(vm.data.filter{ item -> item.group == group})
            }
            if (group == vm.groupMoveState.toGroup) {
                data.swapList(vm.data.filter{ item -> item.group == group})
                state.itemMovedGroup(
                    vm.groupMoveState.offsetX-groupCoeffX(group)*state.listState.layoutInfo.viewportSize.width,
                    vm.groupMoveState.offsetY-groupCoeffY(group)*state.listState.layoutInfo.viewportSize.height,
                    vm.groupMoveState.itemKey,
                    data.lastIndex
                )
                delay(100)
                state.visibleItemsChanged()
                vm.itemMoveFinished()
                *//*
                state.onDragStart(vm.groupMoveState.offsetX, vm.groupMoveState.offsetY)
                state.onDrag(dragOffset.x.toInt(), dragOffset.y.toInt())

                draggedItem(vm.data.first(){it.uniqueId==vm.groupMoveState.itemKey})
                isDragging(group)
                positionInRoot(
                    Offset(vm.groupMoveState.offsetX.toFloat(),
                        vm.groupMoveState.offsetY.toFloat())+dragOffset
                )
                *//*
            }
        }
    }
    */


/*
 val animatable = remember{Animatable(0f, Float.VectorConverter)}
 LaunchedEffect(item.ord){
     offset = + oldPosition - positionInRoot.y
     animatable.snapTo(offset)
     animatable.animateTo(
         targetValue = 0f,
         animationSpec = spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium)
     )
     while (animatable.isRunning){
         delay(30)
     }
     oldPosition = positionInRoot.y
 }*/
/*
    LaunchedEffect(item.ord){
        offset = + oldPosition - positionInRoot.y
        targetValueFloat = -offset

    }
    val animationOffsetState = animateFloatAsState(
        targetValue = targetValueFloat,
        animationSpec = spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium),
            //tween(durationMillis = 300, 0, easing = EaseInOutSine),
        finishedListener = {
            oldPosition = positionInRoot.y
            offset = 0f
            targetValueFloat=0f
        }
    )*/





/*

    LaunchedEffect(Unit){
        if (uiVM.isDragging ) {
            if (item.uniqueId == uiVM.draggedItem.uniqueId){
                delay(30)
                notUnderFakeBox = false
            }
        }
        springAnimation = false
        delay(10)
        oldPosition = rootPosition.value
    }

//isDragging is different from uiVM.isDragging : isDragging comes from ReorderableItem

    LaunchedEffect(isDragging){
        if (isDragging) {
            if (item.uniqueId == uiVM.draggedItem.uniqueId){
                delay(30)
                notUnderFakeBox = false
                springAnimation = false
            }
            else {
                notUnderFakeBox = true
            }
        }
        //this else should fire only when this item dragging is stopped
        else {
            oldPosition = rootPosition.value
            notUnderFakeBox = true
            springAnimation = false
        }
    }


    LaunchedEffect(item.ord){
        if (item.uniqueId != uiVM.draggedItem.uniqueId && notUnderFakeBox) {
            targetValue = rootPosition.value
            oldScrollOffset = scrollOffset.value
            delay(20)
            //offset = +oldPosition - positionInRoot
            //Log.v("itemconst LE ord", "id = ${item.uniqueId}, ord = ${item.ord}, posfromparent = $positionInRoot")
            //Log.v("itemconst LE ord IF", "id = ${item.uniqueId}, ord = ${item.ord}, targetValueFloat = $targetValueFloat")
            springAnimation = true
            targetValue = rootPosition.value - 15f*(scrollOffset.value - oldScrollOffset)
            //Log.v("itemconst LE ord IF", "id = ${item.uniqueId}, scrollOffset new-old = ${newScrollOffset- oldScrollOffset}")
            //delay(10)
            //Log.v("itemconst LE ord IF", "id = ${item.uniqueId}, rootposition -targetval = ${rootPosition.value- targetValue}")
            delay(150)
            springAnimation = false
            oldPosition = rootPosition.value
        }
    }

*/
