package com.ToDoCompass.reorderable

import ReorderableState
import StartDrag
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.unit.IntSize
import com.ToDoCompass.LogicAndData.offsetToGroupOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Singleton
class InteractionsChannel(
    private val scope: CoroutineScope,
    private val stateList : List<ReorderableState<*>>,
    val columnSizeObtain : () -> IntSize,
    val inGroupObtain : () -> Int,
    //position : (Offset) -> Unit
)
{
    internal var startDragChannel = Channel<StartDrag>()
    //internal var dragOffsetChannel = Channel<Offset>()
    internal var pointerChannel = Channel<PointerInputChange>()

    fun sendStartDrag(
        group : Int //this could also from viewmodel but not needed
    ){
        startDragChannel.cancel()
        startDragChannel = createChannel()

        scope.launch {
            val new = startDragChannel.receive()
            //val group = findGroup(new.offset ?: Offset.Zero)
            //Log.v("sendInteractions ", "new = $new")
            //Log.v("sendInteractions", "group = $group")
            stateList[group]
                .interactions
                .trySend(//new
                    startDragInGroup(new, columnSizeObtain())
                )
            //channel.invokeOnClose { Log.v("sendInteractions ", "channel closed") }
        }
    }


    fun sendPointer(
        group : Int,
        //columnSizeObtain : () -> IntSize //this could also from viewmodel but not needed
    ){
        pointerChannel.cancel()
        pointerChannel = createPointerChannel()

        scope.launch {
            val new = pointerChannel.receive()
            //val group = findGroup(new.offset ?: Offset.Zero)
            //Log.v("sendInteractions ", "new = $new")
            //Log.v("sendInteractions", "group = $group")

            stateList[group]
                .pointerChannel
                .trySend(new
                    //pointerChannel.receive()
                )

            //channel.invokeOnClose { Log.v("sendInteractions ", "channel closed") }
        }
    }

    fun onDrag(
        offset : Offset,
        positionObtain : ()->Offset
    ){
        val group = inGroupObtain()
        stateList[group].onDrag(offset.x.toInt(), offset.y.toInt())
    }

    fun onDragCanceled(
        group : Int
    ){
        stateList[group].onDragCanceled()
    }

    fun onDragStart(
        group : Int,
        offsetInGroup : Offset,
        verticalScroll : Boolean,
        firstPress : Boolean = true
    ) : Boolean {
        //Log.v("channel onDragStart", "offset = $offset, offsetInGroup = $offsetInGroup" )
        var dragStartX : Int = offsetInGroup.x.toInt()
        var dragStartY : Int = offsetInGroup.y.toInt()
        if (!firstPress){
            while (!stateList[group].canStartDrag(dragStartX,dragStartY)
                && dragStartX >= 0 && dragStartY >= 0 )
            {
                //Log.v("wtf", "group = $group")
                if (!verticalScroll) dragStartX -= 25
                else dragStartY -= 25
                //Log.v("wtf", "dragStartY = $dragStartY")
            }
        }
        return stateList[group].onDragStart(dragStartX, dragStartY)
    }
/*
    fun sendDragOffset(
        group : Int,
        columnSizeObtain : () -> IntSize //this could also from viewmodel but not needed
    ){
        dragOffsetChannel.cancel()
        dragOffsetChannel = createOffsetChannel()

        scope.launch {
            val new = startDragChannel.receive()
            //val group = findGroup(new.offset ?: Offset.Zero)
            //Log.v("sendInteractions ", "new = $new")
            //Log.v("sendInteractions", "group = $group")
            stateList[group]
                .interactions
                .trySend(//new
                    startDragInGroup(group, new, columnSizeObtain())
                )
            //channel.invokeOnClose { Log.v("sendInteractions ", "channel closed") }
        }
    }*/

}

private fun startDragInGroup(new : StartDrag, columnSize: IntSize) : StartDrag {
    return new.copy(
        offset = offsetToGroupOffset(
            offsetIn = new.offset ?: Offset.Zero,
            columnSize = columnSize
        )
    )
}

private fun createChannel(): Channel<StartDrag> {
    return Channel<StartDrag>()
}

private fun createOffsetChannel(): Channel<Offset> {
    return Channel<Offset>()
}

private fun createPointerChannel(): Channel<PointerInputChange> {
    return Channel<PointerInputChange>()
}

