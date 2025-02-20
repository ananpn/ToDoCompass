/*
 * Copyright 2022 André Claßen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import com.ToDoCompass.ViewModels.UiViewModel
import com.ToDoCompass.reorderable.InteractionsChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun Modifier.detectReorderAfterLongPressSimpleOld(state: ReorderableState<*>, onClick : () -> Unit) =
    this.then(
        Modifier.pointerInput(Unit) {
            
            forEachGesture {
                val down = awaitPointerEventScope {
                    awaitFirstDown(requireUnconsumed = false)
                }
                awaitLongPressOrCancellation(down)?.also {
                    state.interactions.trySend(StartDrag(down.id))
                }
            }
            
        }
    )

fun Modifier.detectReorderAfterLongPressSimple(state: ReorderableState<*>,
                                               scope : CoroutineScope
) =
    this.then(
        Modifier.pointerInput(Unit) {
            
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                scope.launch {
                    awaitLongPressOrCancellation(down)?.also {
                        state.interactions.trySend(StartDrag(down.id))
                    }
                }
            }
            
        }
    )

fun Modifier.detectReorderAfterLongPressQuadList(
    uiVM : UiViewModel,
    channel : InteractionsChannel,
    scope : CoroutineScope,
    onTap : () -> Unit,
    //initialPointerPositionUp : (Offset) -> Unit
) =
    this.then(
        Modifier.pointerInput(Unit) {

            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                val group = when (uiVM.hasAddedItem){
                    true -> 4
                    else -> findGroup(down.position, uiVM.columnSize)
                }
                uiVM.downPressedWithId(down.id.value.toInt())
                uiVM.updatePointerPositionFlow(down.position)
                uiVM.updateInGroup(group)
                if (down.changedToDownIgnoreConsumed()){
                    //Log.v("detectreaofr down changed to down", "uiVM.hasAdded = ${uiVM.hasAddedItem}")
                    //Log.v("detectreaofr down changed to down", "ui = ${v}")
                    //channel.resetPointerChannel()
                    channel.sendStartDrag(group)
                    channel.sendPointer(group)
                }
                scope.launch {
                    detectTapGestures(
                        onTap = {
                            if (!uiVM.hasAddedItem) {
                                //Log.v("detectreordr2", "tap")
                                onTap()
                            }
                        },
                        onPress = {
                            tryAwaitRelease()
                            uiVM.isDownPressed=false

                        }
                    )
                }
                scope.launch {
                    awaitLongPressOrCancellation(down)?.also {
                        uiVM.updateIsDragging(true)
                        channel.startDragChannel.trySend(StartDrag(down.id))
                        channel.pointerChannel.trySend(
                            down
                        )
                        scope.launch {
                            val dragStart = StartDrag(down.id)
                            //channel.startDragChannel.receive()
                            //Log.v("detectreOrder scope", "down position = ${down.position}")
                            if (down != null
                            ) {
                                uiVM.updatePointerPositionFlow(down.position)
                                channel.onDragStart(
                                    group,
                                    uiVM.groupOffsetGet(),
                                    verticalScroll = true,
                                    firstPress = true
                                )
                                //uiVM.updatePointerPosition(down.position.x.toInt(), down.position.y.toInt())
                                //positionUpX(down.position.x.toInt())
                                //positionUpY(down.position.y.toInt())
                                dragStart.offset?.apply {
                                    channel.onDrag(this, { down.position })
                                }
                                var position = down.position
                                detectDrag(
                                    down.id,
                                    onDragEnd = {
                                        uiVM.updateIsDragging(false)
                                        for (i in 0..4)
                                            channel.onDragCanceled(i)
                                    },
                                    onDragCancel = {
                                        uiVM.updateIsDragging(false)
                                        for (i in 0..4)
                                            channel.onDragCanceled(i)
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        position += dragAmount
                                        uiVM.updatePointerPositionFlow(position)
                                        channel.onDrag(dragAmount, { position })

                                    })
                            }
                        }
                    }
                }

                //if (!uiVM.hasAddedItem) {
            }


            
            /*

                            else {
                                uiVM.updateIsDragging(true)
                                channel.startDragChannel.trySend(StartDrag(down.id))
                                channel.pointerChannel.trySend(
                                    down
                                )
                                scope.launch {
                                    val dragStart = StartDrag(down.id)
                                    //channel.startDragChannel.receive()
                                    //Log.v("detectreOrder scope", "down position = ${down.position}")
                                    if (down != null
                                    ) {
                                        uiVM.updatePointerPositionFlow(down.position)
                                        channel.onDragStart(
                                            group,
                                            uiVM.groupOffsetGet(),
                                            verticalScroll = true,
                                            firstPress = true
                                        )
                                        //uiVM.updatePointerPosition(down.position.x.toInt(), down.position.y.toInt())
                                        //positionUpX(down.position.x.toInt())
                                        //positionUpY(down.position.y.toInt())
                                        dragStart.offset?.apply {
                                            channel.onDrag(this, { down.position })
                                        }
                                        var position = down.position
                                        detectDrag(
                                            down.id,
                                            onDragEnd = {
                                                uiVM.updateIsDragging(false)
                                                for (i in 0..4)
                                                    channel.onDragCanceled(i)
                                            },
                                            onDragCancel = {
                                                uiVM.updateIsDragging(false)
                                                for (i in 0..4)
                                                    channel.onDragCanceled(i)
                                            },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                position += dragAmount
                                                uiVM.updatePointerPositionFlow(position)
                                                channel.onDrag(dragAmount, { position })

                                            })
                                    }
                                }

                            }
            */

            //if uiVM.hasAddedItem
            /*
                var position = down.position
                detectDrag(
                    down = down.id,
                    onDrag = { change, dragAmount ->
                        change.consume()
                        position += dragAmount
                        uiVM.updatePointerPositionFlow(position)
                        //channel.onDrag(dragAmount, { position })
                    },
                    onDragEnd = {

                    }
                )
            }*/

            //}
        }
    )


fun Modifier.detectReorderAfterLongPressSuper(
    uiVM : UiViewModel,
    channel : InteractionsChannel,
    scope : CoroutineScope,
    //initialPointerPositionUp : (Offset) -> Unit
) =
    this.then(
        Modifier.pointerInput(Unit) {
            forEachGesture {
                val down = awaitPointerEventScope {
                    awaitFirstDown(requireUnconsumed = false)
                }
                uiVM.downPressedWithId(down.id.value.toInt())
                val group = when (uiVM.hasAddedItem){
                    true -> 4
                    else -> findGroup(down.position, uiVM.columnSize)
                }
                uiVM.updatePointerPositionFlow(down.position)
                uiVM.updateInGroup(group)
                if (down.changedToDownIgnoreConsumed()){
                    //Log.v("detectreaofr down changed to down", "uiVM.hasAdded = ${uiVM.hasAddedItem}")
                    //Log.v("detectreaofr down changed to down", "ui = ${v}")
                    //channel.resetPointerChannel()
                    channel.sendStartDrag(group)
                    channel.sendPointer(group)
                }

                //if (!uiVM.hasAddedItem) {
                awaitLongPressOrCancellation(down)?.also {
                    uiVM.updateIsDragging(true)
                    channel.startDragChannel.trySend(StartDrag(down.id))
                    channel.pointerChannel.trySend(
                        down
                    )
                    scope.launch {
                        val dragStart = StartDrag(down.id)
                        //channel.startDragChannel.receive()
                        //Log.v("detectreOrder scope", "down position = ${down.position}")
                        if (down != null
                        ) {
                            uiVM.updatePointerPositionFlow(down.position)
                            channel.onDragStart(
                                group,
                                uiVM.groupOffsetGet(),
                                verticalScroll = true,
                                firstPress = true
                            )
                            //uiVM.updatePointerPosition(down.position.x.toInt(), down.position.y.toInt())
                            //positionUpX(down.position.x.toInt())
                            //positionUpY(down.position.y.toInt())
                            dragStart.offset?.apply {
                                channel.onDrag(this, { down.position })
                            }
                            var position = down.position
                            detectDrag(
                                down.id,
                                onDragEnd = {
                                    uiVM.updateIsDragging(false)
                                    for (i in 0..4)
                                        channel.onDragCanceled(i)
                                },
                                onDragCancel = {
                                    uiVM.updateIsDragging(false)
                                    for (i in 0..4)
                                        channel.onDragCanceled(i)
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    position += dragAmount
                                    uiVM.updatePointerPositionFlow(position)
                                    channel.onDrag(dragAmount, { position })

                                })
                        }
                    }
                }



            }
            /*

                            else {
                                uiVM.updateIsDragging(true)
                                channel.startDragChannel.trySend(StartDrag(down.id))
                                channel.pointerChannel.trySend(
                                    down
                                )
                                scope.launch {
                                    val dragStart = StartDrag(down.id)
                                    //channel.startDragChannel.receive()
                                    //Log.v("detectreOrder scope", "down position = ${down.position}")
                                    if (down != null
                                    ) {
                                        uiVM.updatePointerPositionFlow(down.position)
                                        channel.onDragStart(
                                            group,
                                            uiVM.groupOffsetGet(),
                                            verticalScroll = true,
                                            firstPress = true
                                        )
                                        //uiVM.updatePointerPosition(down.position.x.toInt(), down.position.y.toInt())
                                        //positionUpX(down.position.x.toInt())
                                        //positionUpY(down.position.y.toInt())
                                        dragStart.offset?.apply {
                                            channel.onDrag(this, { down.position })
                                        }
                                        var position = down.position
                                        detectDrag(
                                            down.id,
                                            onDragEnd = {
                                                uiVM.updateIsDragging(false)
                                                for (i in 0..4)
                                                    channel.onDragCanceled(i)
                                            },
                                            onDragCancel = {
                                                uiVM.updateIsDragging(false)
                                                for (i in 0..4)
                                                    channel.onDragCanceled(i)
                                            },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                position += dragAmount
                                                uiVM.updatePointerPositionFlow(position)
                                                channel.onDrag(dragAmount, { position })

                                            })
                                    }
                                }

                            }
            */

            //if uiVM.hasAddedItem
            /*
                var position = down.position
                detectDrag(
                    down = down.id,
                    onDrag = { change, dragAmount ->
                        change.consume()
                        position += dragAmount
                        uiVM.updatePointerPositionFlow(position)
                        //channel.onDrag(dragAmount, { position })
                    },
                    onDragEnd = {

                    }
                )
            }*/

            //}
        }
    )

fun findGroup(offset : Offset,
              columnSize : IntSize? = null,
              columnWidth : Int? = columnSize?.width,
              columnHeight : Int? = columnSize?.height
) : Int {
    if (offset.x < columnWidth ?:0){
        if (offset.y < columnHeight ?:0) return 0
        else return 2
    }
    else{
        if (offset.y < columnHeight ?:0) return 1
        else return 3
    }
}

/*
fun downToGroup(group : Int, down : PointerInputChange, columnSize: IntSize) : PointerInputChange {
    return down.copy(currentPosition = offsetToGroupOffset(
        group,
        down.position,
        columnSize
    ))
}*/
/*
fun Modifier.detectReorder(state: ReorderableState<*>) =
    this.then(
        Modifier.pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    var drag: PointerInputChange?
                    var overSlop = Offset.Zero
                    do {
                        drag = awaitPointerSlopOrCancellation(down.id, down.type) { change, over ->
                            change.consume()
                            overSlop = over
                        }
                    } while (drag != null && !drag.isConsumed)
                    if (drag != null) {
                        state.interactions.trySend(StartDrag(down.id, overSlop))
                    }
                }
            }
        }
    )*/




/*
this.detectDrag(down.id,
onDrag = {change: PointerInputChange, dragAmount: Offset ->
    Log.v("detect drag", "dragAmount = $dragAmount")

}
)
*/
/*
val downOffset = offsetToGroupOffset(
offsetIn = down?.position ?: Offset.Zero,
columnSize = columnSizeObtain()
)
*/

/*
detectDrag(
down.id,
onDragEnd = {
    channel.onDragCanceled(group)
},
onDragCancel = {
    channel.onDragCanceled(group)
},
onDrag = { change, dragAmount ->
    change.consume()
    channel.onDrag(group = group, x = dragAmount.x.toInt(), y = dragAmount.y.toInt())
    Log.v("onDrag, group = ${group}", "dragAmount = $dragAmount")
})
*/
