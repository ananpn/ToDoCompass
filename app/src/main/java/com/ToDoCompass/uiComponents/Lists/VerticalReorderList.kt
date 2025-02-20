package com.ToDoCompass.uiComponents.Lists

import ReorderableItem
import ReorderableLazyListState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ToDoCompass.LogicAndData.getGroupColor
import com.ToDoCompass.ViewModels.DraggedItemStates
import com.ToDoCompass.ViewModels.MainViewModel
import com.ToDoCompass.ViewModels.UiViewModel
import com.ToDoCompass.database.ListItem
import com.ToDoCompass.uiComponents.TaskBoxes.itemConstructor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VerticalReorderList(
    vm : MainViewModel,
    uiVM : UiViewModel,
    data : SnapshotStateList<ListItem> = SnapshotStateList(),
    state : ReorderableLazyListState,
    group : Int,
    bgcolor : Color = getGroupColor(group),
    //channel : InteractionsChannel
) {
    /*
    val scope = rememberCoroutineScope()

    //var positionInRoot by remember{ mutableStateOf(Offset.Zero) }
    var dragOffset by remember{ mutableStateOf(Offset.Zero) }
    //var parentOffset by remember{ mutableStateOf(Offset.Zero) }
    var childOffset by remember{ mutableStateOf(Offset.Zero) }
    var childOffsetY by remember{mutableStateOf(0f)}
    val density = LocalDensity.current
    */
    var gettingInitialPosition by remember(){ mutableStateOf(true)}


    LaunchedEffect(uiVM.groupChangeCheck){
        if (uiVM.inGroup == group){
            while(uiVM.draggedItemState != DraggedItemStates.ITEMGOT &&
                        uiVM.draggedItemState !=  DraggedItemStates.ITEMUPDATED)
            {
                //Log.v("verticalreord LE", "while")
                delay(10)
            }
            gettingInitialPosition = true
        }
    }

    LazyColumn(
        state = state.listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .zIndex(1f)
            .onGloballyPositioned { coordinates ->
                uiVM.updateListOffset(group, coordinates.positionInRoot())
            }
            /*
            .reorderable(
                state = state,
                channel = channel,
                columnSizeObtain = { uiVM.columnSize })
        */
    ) {
        items(data, { it.uniqueId }) { item ->
            val _rootPositionYFlow = MutableStateFlow(0f)
            val rootPosition = _rootPositionYFlow.collectAsState(0f)
            var targetValue by remember{mutableStateOf(rootPosition.value)}
            var oldPosition by remember{mutableStateOf(rootPosition.value)}
            var springAnimation by remember(){ mutableStateOf(false)}
            var canAnimate by remember{ mutableStateOf(false)}
            var isDrawing by remember{ mutableStateOf(false)}

            LaunchedEffect(Unit){
                if (uiVM.isMovingGroup){
                    isDrawing = false
                    delay(140)
                    isDrawing = true
                }
                else isDrawing = true
            }
            
            //start animation when item reordered
            LaunchedEffect(item.ord) {
                //targetValue = rootPosition.value
                if (canAnimate) {
                    delay(20)
                    springAnimation = true
                    targetValue = rootPosition.value
                    //delay(370)
                    //oldPosition = rootPosition.value
                    //springAnimation = false


                    //delay(250)
                    //springAnimation = false
                    //oldPosition = rootPosition.value

                }
            }

            LaunchedEffect(derivedStateOf { uiVM.isMovingGroup }) {
                if (uiVM.isMovingGroup){
                    if (group == uiVM.fromGroup){
                        if (item.ord >= uiVM.fromIndex){
                            canAnimate = true
                        }
                    }
                    else if (group == uiVM.toGroup){
                        if (item.ord >= uiVM.toIndex){
                            canAnimate = true
                        }
                    }
                }
                else {
                    delay(100)
                    canAnimate = false
                    oldPosition = rootPosition.value
                    targetValue = oldPosition
                }
            }




            val animationState = animateFloatAsState(
                targetValue = when(springAnimation){
                    true -> targetValue
                    false -> oldPosition
                },
                animationSpec = when(springAnimation){
                    true-> //tween(durationMillis = 400, 0, easing = EaseInOutSine)
                        spring(Spring.DampingRatioNoBouncy, 2000f)
                    false -> snap(0)
                },
                        //tween(durationMillis = 150, 0, easing = EaseInOutSine),
                finishedListener = {
                    //Log.v("verticalreordlist LE ord", "${item.uniqueId} springanimation stopped")
                    springAnimation = false
                    oldPosition = rootPosition.value
                    targetValue = oldPosition
                    //offset = 0f
                },
                label = ""
            )
            Spacer(modifier = Modifier.height(5.dp))
            ReorderableItem(
                state= state,
                key = item.uniqueId,
                modifier = Modifier
                    //.zIndex(-item.ord.toFloat())
                    .onGloballyPositioned { coordinates ->
                        _rootPositionYFlow.value = coordinates.positionInRoot().y
                        if (gettingInitialPosition)
                            if (item.uniqueId == uiVM.draggedItem.uniqueId) {
                                uiVM.updateRealBoxPosition(coordinates.positionInRoot())
                                gettingInitialPosition = false
                            }
                    }
                    .graphicsLayer {
                        if (canAnimate) {
                            //compositingStrategy = CompositingStrategy.Auto
                            translationY = animationState.value - rootPosition.value
                        }
                        alpha = if (!isDrawing) {
                            0f
                        } else 1f

                    }



/*

                    .offset{
                        if (canAnimate) {
                            IntOffset(x=0,y=(animationState.value - targetValue).toInt())
                        }
                        else IntOffset.Zero
                    }

*/




                /*
                                modifier = Modifier.onGloballyPositioned { coordinates ->
                                    childOffsetY = coordinates.positionInRoot().y
                                }
                                */
                /*
                else if (item.uniqueId in uiVM.removeAnimationKeys && uiVM.removeAnimationRunning){
                    uiVM.addToRemoveAnimationPositions(
                        item.uniqueId,
                        coordinates.positionInRoot()
                    )
                }
                */

                /*
                defaultDraggingModifier = Modifier
                    .pointerInput(Unit) {
                        awaitEachGesture{}
                        detectDragGestures(

                        ) { change, dragAmount ->
                            change.consume()

                        }

                    },
*/
            ) { isDragging ->
                itemConstructor(
                    uiVM = uiVM,
                    isDragging = isDragging,
                    item = item,
                    bgcolor = bgcolor,
                    //rootPositionFlow = _rootPositionYFlow,
                    //scrollOffsetFlow = _scrollOffsetFlow
                )

            }
            Spacer(modifier = Modifier.height(5.dp))
        }
    }

}


