package com.ToDoCompass.screens.utils

import ItemPosition
import ReorderableLazyListState
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.ToDoCompass.LogicAndData.Constants.Companion.BT_WEIGHT
import com.ToDoCompass.LogicAndData.Constants.Companion.LR_WEIGHT
import com.ToDoCompass.LogicAndData.Constants.Companion.emptyListItem
import com.ToDoCompass.LogicAndData.Constants.Companion.labelFontSize
import com.ToDoCompass.LogicAndData.StringConstants
import com.ToDoCompass.LogicAndData.StringConstants.Companion.ADD_TASK
import com.ToDoCompass.LogicAndData.StringConstants.Companion.BOTTOM_BRANCH_TEXT
import com.ToDoCompass.LogicAndData.StringConstants.Companion.INFO_DELETE_SUBTASK
import com.ToDoCompass.LogicAndData.StringConstants.Companion.INFO_DELETE_TASK
import com.ToDoCompass.LogicAndData.StringConstants.Companion.LEFT_BRANCH_TEXT
import com.ToDoCompass.LogicAndData.StringConstants.Companion.RIGHT_BRANCH_TEXT
import com.ToDoCompass.LogicAndData.StringConstants.Companion.TOP_BRANCH_TEXT
import com.ToDoCompass.LogicAndData.swapList
import com.ToDoCompass.ViewModels.MainViewModel
import com.ToDoCompass.ViewModels.UiViewModel
import com.ToDoCompass.database.ListItem
import com.ToDoCompass.reorderable.InteractionsChannel
import com.ToDoCompass.uiComponents.Lists.NewItemList
import com.ToDoCompass.uiComponents.Lists.VerticalReorderList
import com.ToDoCompass.uiComponents.Modals.AddTaskDialog
import com.ToDoCompass.uiComponents.Modals.DeleteDialogSuper
import com.ToDoCompass.uiComponents.Modals.EditDialogSuper
import com.ToDoCompass.uiComponents.TaskBoxes.DraggedFakeBox
import com.ToDoCompass.uiComponents.TaskCards.TaskCard
import com.ToDoCompass.uiComponents.smallComponents.AddButtonIcon
import com.ToDoCompass.uiComponents.smallComponents.AutoFitTextToFill
import detectReorderAfterLongPressQuadList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rememberReorderableLazyListState
import kotlin.math.max

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun mainScreen(
               //dispDates : List<String>,
               vm: MainViewModel,
               uiVM : UiViewModel
) {

    val scope = rememberCoroutineScope()
    
    var openAddNewTaskDialog by remember{ mutableStateOf(false) }

    var checkRunning by remember{ mutableStateOf(false) }

    //This is for detecting change of group when dragging
    /*
    LaunchedEffect(Unit){
        while(true){
            if (!checkRunning){
                checkRunning = true
                if (uiVM.isDragging){
                    //updateData(dataList)
                    uiVM.updateInGroupWhenDragging()
                    //Log.v("main LE Unit while true", "ingroup = ${uiVM.inGroup}")
                    delay(100)
                    checkRunning = false
                }
                else {
                    //uiVM.updateInGroup(0)
                    delay(500)
                    checkRunning = false
                }
            }
            delay(100)
        }
    }
    */

    fun updateData(
        _list : List<SnapshotStateList<ListItem>>,
        group1 : Int? = null,
        group2 : Int? = null
    ) {
        //Log.v("main updatedata", "uiVM.fromGroup = ${uiVM.fromGroup}, uiVM.toGroup = ${uiVM.toGroup}")
        if (group1 == null) {
            _list.forEachIndexed{_index, dataInList ->
                dataInList.swapList(vm.data.filter{ item -> item.group == _index}.sortedBy { it.ord })
            }
        }
        else {
            if (group1 != null){
                _list[group1].swapList(vm.data.filter{ item -> item.group == group1}.sortedBy { it.ord })
            }
            if (group2 != null){
                _list[group2].swapList(vm.data.filter{ item -> item.group == group2}.sortedBy { it.ord })
            }
        }

    }

    val data0 = remember { mutableStateListOf<ListItem>() }
    val data1 = remember { mutableStateListOf<ListItem>() }
    val data2 = remember { mutableStateListOf<ListItem>() }
    val data3 = remember { mutableStateListOf<ListItem>() }
    val data4 = remember { mutableStateListOf<ListItem>() }
    val dataList = listOf(data0, data1, data2, data3, data4)
    val onItemMove : (ItemPosition, ItemPosition, SnapshotStateList<ListItem>, group : Int) -> Unit = {
        from, to, data, group ->
            vm.switchItemsInDataByIndex(
                group = uiVM.inGroup,
                first = to.index,
                second = from.index
            )
            updateData(dataList, group)
    }

    LaunchedEffect(Unit){
        vm.closeTaskCard()
        vm.cancelAdding()
        uiVM.hasAddedItem = false
        vm.updateDataFromDB()
        vm.updateNotifTypesFromDB()
        delay(200)
        updateData(dataList)
    }

    LaunchedEffect(uiVM.isDragging){
        vm.sortData()
        if (!vm.groupMoveState.moveStage2Done && !vm.groupMoveState.moveStage1Done && uiVM.isDragging){
            updateData(dataList)
        }
        if (!uiVM.isDragging){
            //updateData(dataList)
            if (!uiVM.hasAddedItem) {
                uiVM.updateInGroup(-1)
                //vm.drawAllItems()
                vm.saveDataToDB()
            }
            else{
                //updateData(dataList)
            }
        }
    }

    //var upItemPosition by remember{ mutableStateOf(UpItemPosition(emptyItemPosition, emptyItemPosition, 0))}
    val state0 = rememberReorderableLazyListState(
        onMove = {from, to ->
            onItemMove(from, to, data0, 0)
        },
        group = 0,
    )
    val state1 = rememberReorderableLazyListState(
        onMove = {from, to ->
            onItemMove(from, to, data1, 1)
        },
        group = 1
    )
    val state2 = rememberReorderableLazyListState(
        onMove = { from, to ->
            onItemMove(from, to, data2, 2)
        },
        group = 2
    )
    val state3 = rememberReorderableLazyListState(
        onMove = { from, to ->
            onItemMove(from, to, data3, 3)
        },
        group = 3
    )
    val state4 = rememberReorderableLazyListState(
        onMove = { from, to ->
            onItemMove(from, to, data4, 4)
        },
        group = 4
    )
    val stateList = listOf(state0, state1, state2, state3, state4)
    val channel = remember{InteractionsChannel(
            scope,
            stateList,
            columnSizeObtain = {uiVM.columnSize},
            inGroupObtain = {max(uiVM.inGroup,0)},
        )
    }

    LaunchedEffect(uiVM.groupChangeCheck) {
        //Log.v("LE groupchangecheck mainscrene", "launch ${uiVM.pointerPositionFlow.value}")
        if (uiVM.inGroup in 0..4) {
            stateList[uiVM.inGroup].visibleItemsChanged()
            val offsetInGroup = uiVM.groupOffsetGet()
            //var index : Int? = null
            //Log.v("main LE groupchange", "ingroup = ${uiVM.inGroup}")
            var index = stateList[uiVM.inGroup]
                .findItemIndexAt(
                    offsetInGroup,
                    uiVM.isDragging
                )
            if (!uiVM.isDragging){
                //Log.v("main LE ingroup", "if !isdragging, index = $index")
                //sets uiVM.draggedItem to the item long clicked
                uiVM.updateDraggedItem( dataList[uiVM.inGroup].firstOrNull()
                    { item -> item.ord == index && item.group == uiVM.inGroup } ?: emptyListItem)
            }

            if (uiVM.draggedItem != emptyListItem){
                vm.setGroupMoveState(movedItem = uiVM.draggedItem, itemKey = uiVM.draggedItem.uniqueId)
                if (uiVM.isDragging && uiVM.inGroup != uiVM.draggedItem.group) {
                    uiVM.isMovingGroup(true)
                    stateList[uiVM.fromGroup].letIsMovingGroup(true)
                    stateList[uiVM.toGroup].letIsMovingGroup(true)
                    if (index == null){
                        index = dataList[uiVM.inGroup].lastIndex+1
                    }
                    vm.itemMoveStage1(
                        uiVM.draggedItem,
                        uiVM.inGroup,
                        index
                    )
                }
            }
        }
    }

    LaunchedEffect(derivedStateOf { vm.groupMoveState.moveStage1Done }){
        if (vm.groupMoveState.moveStage1Done && uiVM.inGroup in 0..3) {
            //movedItem is in new group
            uiVM.updateDraggedItem(vm.groupMoveState.movedItem)
            vm.sortData()
            updateData(dataList, uiVM.fromGroup, uiVM.toGroup)
            stateList[uiVM.inGroup].scrollToNewItem(vm.groupMoveState.movedItem.ord)
            delay(40)
            groupMoved(
                vm = vm,
                stateList = stateList,
                uiVM = uiVM
            )
            updateData(dataList, uiVM.fromGroup, uiVM.toGroup)

            //delay(100)
            stateList[uiVM.fromGroup].letIsMovingGroup(false)
            stateList[uiVM.toGroup].letIsMovingGroup(false)
            uiVM.isMovingGroup(false)
            uiVM.hasAddedItem = false
            vm.itemMoveFinalize()

        }
    }

    LaunchedEffect(uiVM.columnSize){
        uiVM.determineBounds()
    }
    Box(
        modifier = Modifier
            .detectReorderAfterLongPressQuadList(
                channel = channel,
                uiVM = uiVM,
                scope = rememberCoroutineScope(),
                onTap = {
                    if (uiVM.draggedItem != emptyListItem)
                        vm.openTaskCard(item = uiVM.draggedItem)
                }
            )
    ){
    DraggedFakeBox(
        uiVM = uiVM,
    )
    Column(modifier = Modifier
        .fillMaxSize()
    ){
        Row(modifier = Modifier
            .alpha(
                when (uiVM.hasAddedItem) {
                    false -> 1f
                    true -> 0.5f
                }
            )
            .padding(5.dp)
            .weight(BT_WEIGHT)){
            Box(modifier = Modifier
                .clipToBounds()
                .weight(LR_WEIGHT)
                .onSizeChanged { size ->
                    uiVM.columnSize = size
                }
            ) {
                VerticalReorderList(
                    uiVM = uiVM,
                    vm = vm,
                    data = data0,
                    state = state0,
                    group = 0,
                )
            }
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .offset(y = 20.dp),

            ){
                AutoFitTextToFill(
                    text = TOP_BRANCH_TEXT,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = labelFontSize.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    fontWeight = FontWeight(1000),
                    fitText = true,
                    vertical = true
                )
            }
            Box(modifier = Modifier
                .clipToBounds()
                .weight(LR_WEIGHT)
            ){
                VerticalReorderList(
                    uiVM = uiVM,
                    vm = vm,
                    data = data1,
                    state = state1,
                    group = 1,
                    //channel = channel
                )
            }
        }
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ){
            AutoFitTextToFill(
                modifier = Modifier.weight(LR_WEIGHT),
                text = LEFT_BRANCH_TEXT,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = labelFontSize.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                fontWeight = FontWeight(1000),
                fitText = true,
                vertical = false
            )
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
            ){

            }
            AutoFitTextToFill(
                modifier = Modifier.weight(LR_WEIGHT),
                text = RIGHT_BRANCH_TEXT,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = labelFontSize.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                fontWeight = FontWeight(1000),
                fitText = true,
                vertical = false
            )

        }

        Box(modifier = Modifier
            .fillMaxSize()
            .weight(BT_WEIGHT)
        ){
            Box(contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .offset(y = -85.dp)
                    .fillMaxSize()
                    .background(color = Color.Transparent)
                    .zIndex(3f)
            ){
                NewItemList(
                    uiVM = uiVM,
                    vm = vm,
                    data = data4,
                    state = state4,
                    group = 4,
                    cancelAdding = {
                        vm.cancelAdding()
                        uiVM.hasAddedItem = false
                        updateData(dataList)
                    }
                )
                this@Column.AnimatedVisibility(visible = !uiVM.hasAddedItem,
                    enter = EnterTransition.None) {
                    AddButtonIcon(
                        modifier = Modifier.offset(y = 50.dp),
                        onClicked = { vm.openAddNewDialog() }
                    )
                }

            }
            Row(modifier = Modifier
                .padding(5.dp)
                .alpha(
                    when (uiVM.hasAddedItem) {
                        false -> 1f
                        true -> 0.5f
                    }
                )
            ){
                Box(modifier = Modifier
                    .clipToBounds()
                    .weight(LR_WEIGHT)) {
                    VerticalReorderList(
                        uiVM = uiVM,
                        vm = vm,
                        data = data2,
                        state = state2,
                        group = 2,
                    )
                }
                Box(contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .offset(y = 20.dp),

                    ){
                    AutoFitTextToFill(
                        text = BOTTOM_BRANCH_TEXT,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = labelFontSize.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        fontWeight = FontWeight(1000),
                        fitText = true,
                        vertical = true
                    )
                }
                Box(modifier = Modifier
                    .clipToBounds()
                    .weight(LR_WEIGHT)){
                    VerticalReorderList(
                        uiVM = uiVM,
                        vm = vm,
                        data = data3,
                        state = state3,
                        group = 3,
                    )
                }
            }

        }

    }
    }
    if (vm.openAddNewDialog) {
        val context = LocalContext.current
        AddTaskDialog(
            vm = vm,
            label = ADD_TASK,
            onSave = {item, alarm -> scope.launch {
                try {
                    vm.insertNewTask(item = item, alarm = alarm)
                    uiVM.hasAddedItem =true
                    uiVM.updateInGroup(4)
                    delay(100)
                    vm.closeAddNewDialog()
                }
                catch(e :Exception){
                    val toast =
                        Toast.makeText(context, StringConstants.TASK_ADD_FAIL, Toast.LENGTH_LONG) // in Activity
                    toast.show()
                }
                updateData(dataList, 4)
            } //scope ^^^
            },
            onDismiss = {
                vm.closeAddNewDialog()
            }
        )
    }
    EditDialogSuper(vm = vm)
    DeleteDialogSuper(
        vm = vm,
        onDelete = {when (vm.deleteInfo){
            INFO_DELETE_TASK -> scope.launch{
                vm.updateDataFromDB()
                updateData(dataList)
                uiVM.updateIsDragging(false)
                delay(50)
                vm.closeTaskCard()
            }
            INFO_DELETE_SUBTASK -> scope.launch{
                vm.updateDataAndSubTasksFromDB()
                vm.showingSubTaskCard = false
            }
            else -> null
        } }
    )
    TaskCard(
        uiVM = uiVM,
        vm = vm,
        onDismiss = {scope.launch{
            vm.updateDataFromDB()
            updateData(dataList)
            uiVM.updateIsDragging(false)
            delay(50)
            vm.closeTaskCard()
        }},
        onUpdate = {scope.launch{
            vm.updateDataFromDB()
            updateData(dataList)
        }}
    )

    //EditTaskDialog(vm = vm)



}

fun groupMoved (vm : MainViewModel,
                stateList : List<ReorderableLazyListState>,
                uiVM : UiViewModel
) {
    stateList[vm.groupMoveState.fromGroup].onDragCanceled()
    stateList[vm.groupMoveState.toGroup].itemMovedGroup(
            pointerPosition = uiVM.pointerPositionFlow.value,
            realBoxPosition = uiVM.realBoxPosition,
            newItemKey = uiVM.draggedItem.uniqueId,
            fakeBoxPositionOffset = uiVM.initialFakeBoxOffset,
        )
    //stateList[vm.groupMoveState.fromGroup].visibleItemsChanged()
}

/*

fun updateData : ( list : List<SnapshotStateList<ListItemFront>>, group1  Int?, Int?) -> Unit = {
        _list, group1, group2 ->
    vm.sortData()
    if (group1 == null){
        _list.forEachIndexed{_index, dataInList ->
            dataInList.swapList(vm.data.filter{ item -> item.group == _index}.sortedBy { it.ord })
        }
    }
    else{
        _list[group1].swapList(vm.data.filter{ item -> item.group == group1}.sortedBy { it.ord })
    }
    if (group2 != null){
        _list[group2].swapList(vm.data.filter{ item -> item.group == group2}.sortedBy { it.ord })
    }
}

*/



