package com.ToDoCompass.uiComponents.Lists

import ReorderableItem
import ReorderableLazyListState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ToDoCompass.LogicAndData.getGroupColor
import com.ToDoCompass.ViewModels.DraggedItemStates
import com.ToDoCompass.ViewModels.MainViewModel
import com.ToDoCompass.ViewModels.UiViewModel
import com.ToDoCompass.database.ListItem
import com.ToDoCompass.uiComponents.TaskBoxes.itemConstructor
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NewItemList(
    vm : MainViewModel,
    uiVM : UiViewModel,
    data : SnapshotStateList<ListItem> = SnapshotStateList(),
    state : ReorderableLazyListState,
    group : Int,
    bgcolor : Color = getGroupColor(group),
    //channel : InteractionsChannel,
    cancelAdding : () -> Unit
) {
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
            //.offset(y = -30.dp)
            .background(color = Color.Transparent)
            .zIndex(if (uiVM.hasAddedItem) 4f else -2f)
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
            var isDrawing by remember{ mutableStateOf(false)}
            var itemSize = IntSize(width = 100, height = 40)

            LaunchedEffect(Unit){
                if (uiVM.isMovingGroup){
                    isDrawing = false
                    delay(140)
                    isDrawing = true
                }
                else isDrawing = true
            }
            Icon(imageVector = Icons.Filled.Clear,
                contentDescription = "",
                tint = Color.LightGray,
                modifier = Modifier
                    .offset { IntOffset(x=itemSize.width/2, y = itemSize.height/4)}
                    .alpha(when (uiVM.isDragging){
                        true -> 0f
                        false -> 1f
                    })
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                    .zIndex(10f)
                    .size(30.dp)
                    .clickable(
                        enabled = !uiVM.isDragging,
                        onClick = {
                            cancelAdding()
                        }
                    )

            )
            ReorderableItem(
                state= state,
                key = item.uniqueId,
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        if (gettingInitialPosition)
                            if (item.uniqueId == uiVM.draggedItem.uniqueId) {
                                uiVM.updateRealBoxPosition(coordinates.positionInRoot())

                                gettingInitialPosition = false
                            }
                    }
                    .onSizeChanged { itemSize = it }
            ) { isDragging ->
                itemConstructor(
                    uiVM = uiVM,
                    isDragging = isDragging,
                    item = item,
                    bgcolor = bgcolor,
                )
            }

            //Spacer(modifier = Modifier.height(5.dp))
        }
    }

}


