package com.ToDoCompass.uiComponents.Lists

import ReorderableItem
import ReorderableLazyListState
import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ToDoCompass.uiComponents.smallComponents.OverFlowIcon
import detectReorderAfterLongPressSimple
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import simpleReorderable

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T, R : Any> SimpleReorderableList(
    state : ReorderableLazyListState,
    modifier : Modifier = Modifier,
    itemsClickable : Boolean = true,
    onItemClick : (T) -> Unit = {},
    items : List<T>,
    itemKey : (T) -> R,
    itemOrder : (T) -> R,
    itemContentBox : @Composable (Boolean, T) -> Unit
    
    //channel : InteractionsChannel
) {
    val scope = rememberCoroutineScope()
    /*
    val state = rememberReorderableLazyListState(
        onMove = {from, to ->
            onItemMove(from, to)
        },
        group = 69,
        onDragEnd = {_first, _second ->
            vm.saveSubTasksToDB()
        },
        maxScrollPerFrame = 8.dp

    )
    */
    //val items = vm.subTasks.sortedBy { it.childOrd}
    Box(modifier = Modifier){
    LazyColumn(
        state = state.listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = when (items.isEmpty()) {
            true -> Modifier
            false -> modifier
                //.background(color = MaterialTheme.colorScheme.background)
                .zIndex(1f)
                .simpleReorderable(
                    state = state
                )
            
        },
        //contentPadding = PaddingValues(vertical = 25.dp)
    ) {
        items(items, { itemKey(it) })
        { item ->
            val _rootPositionYFlow = MutableStateFlow(0f)
            val rootPosition = _rootPositionYFlow.collectAsState(0f)
            var targetValue by remember { mutableStateOf(rootPosition.value) }
            var oldPosition by remember { mutableStateOf(rootPosition.value) }
            var springAnimation by remember() { mutableStateOf(false) }
            var canAnimate by remember { mutableStateOf(false) }
            
            //start animation when item reordered
            LaunchedEffect(itemOrder(item)) {
                if (canAnimate) {
                    delay(20)
                    springAnimation = true
                    targetValue = rootPosition.value
                    
                }
            }
            
            val animationState = animateFloatAsState(
                targetValue = when (springAnimation) {
                    true -> targetValue
                    false -> oldPosition
                },
                animationSpec = when (springAnimation) {
                    true -> //tween(durationMillis = 400, 0, easing = EaseInOutSine)
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
            Spacer(modifier = Modifier.height(1.dp))
            ReorderableItem(
                state = state,
                key = itemKey(item),
                modifier = Modifier
                    .detectReorderAfterLongPressSimple(state, scope = scope)
                    //.zIndex(-item.ord.toFloat())
                    .onGloballyPositioned { coordinates ->
                        _rootPositionYFlow.value = coordinates.positionInRoot().y
                    }
                    /*
                    .graphicsLayer {
                        *//*
                        if (canAnimate) {
                            //compositingStrategy = CompositingStrategy.Auto
                            translationY = animationState.value - rootPosition.value
                        }
                        *//*

                    }
                    */
                    
                    .clickable(
                        enabled = itemsClickable,
                        onClick = {
                            onItemClick(item)
                        }
                    )
            
            ) { isDragging ->
                itemContentBox(isDragging, item)
                /*
                subTaskConstructor(
                    uiVM = uiVM,
                    isDragging = isDragging,
                    item = item,
                    taskDoneUp = {
                        vm.updateTaskAndData(item.copy(taskDone = it).toTask())
                    }
                )
                */
                
            }
            Spacer(modifier = Modifier.height(1.dp))
        }
    }
    
    if (state.listState.canScrollForward) {
        Log.v("simplereorderab+lelist", "canScrollForward")
        OverFlowIcon(
            modifier = Modifier
                //.background(color = Color.Red)
                .align(Alignment.BottomCenter)
                .zIndex(100f)
            ,
        )
    }
    
    if (state.listState.canScrollBackward){
        OverFlowIcon(
            modifier = Modifier
                .align(Alignment.TopCenter)
                //.background(color = Color.Red)
                .zIndex(100f),
            directionUp = true
        )
    }
    }
}




