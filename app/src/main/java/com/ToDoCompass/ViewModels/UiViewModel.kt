package com.ToDoCompass.ViewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ToDoCompass.LogicAndData.Constants.Companion.emptyListItem
import com.ToDoCompass.LogicAndData.findGroupMove
import com.ToDoCompass.LogicAndData.toHalfOffset
import com.ToDoCompass.database.ListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UiViewModel @Inject constructor(

) : ViewModel() {
    //Pointer position and state ******************************************

    val pointerId = MutableStateFlow(-1)
    var isDownPressed by mutableStateOf(false)

    private val _pointerPositionFlow = MutableStateFlow(Offset.Zero)
    val pointerPositionFlow: StateFlow<Offset> = _pointerPositionFlow
    var updateCounter : Int = 0

    fun updatePointerPositionFlow(offset: Offset) {
        _pointerPositionFlow.value = offset
        updateCounter +=1
        if (updateCounter == 5){
            //Log.v("uivm updatepointerposflow", "updateCounter == 7")
            updateCounter=0
            updateInGroupWhenDragging()
        }
    }

    fun downPressedWithId(new : Int){
        pointerId.value = new
        isDownPressed = true
        groupChangeCheck = pointerId.value + 10*inGroup
    }


    //Offsets ****************************************

    var initialFakeBoxOffset by mutableStateOf(Offset.Zero)
    var realBoxPosition by mutableStateOf(Offset.Zero)
    var draggedBoxSize by mutableStateOf(IntSize.Zero)

    fun setInitialFakeBoxOffset(){
        initialFakeBoxOffset = realBoxPosition-pointerPositionFlow.value
    }

    var listBoundaries = MainViewModel.ColumnBoundaries()

    var columnSize : IntSize = IntSize.Zero

    fun determineBounds(){
        listBoundaries=listBoundaries.copy(
            xCenter = columnSize.width,
            yCenter = columnSize.height
        )
    }

    fun groupOffsetGet() : Offset {
        return pointerPositionFlow.value-listOffsets[inGroup]
    }

    fun updateRealBoxPosition(new : Offset){
        //Log.v("uiVM updaterealbox", "realboxposition = $new")
        realBoxPosition = new
        letDraggedItemState(DraggedItemStates.POSITIONGOT)
    }
    
    fun updateDraggedBoxSize(new : IntSize){
        //Log.v("uiVM updaterealbox", "realboxposition = $new")
        draggedBoxSize = new
    }

    val listOffsets = mutableListOf<Offset>(Offset.Zero, Offset.Zero, Offset.Zero, Offset.Zero, Offset.Zero)
    fun updateListOffset(group : Int, offset: Offset){
        listOffsets.removeAt(group)
        listOffsets.add(group, offset)
        //Log.v("uivm updatelistoffset", "listoffsets = $listOffsets")
    }


    //Dragged Item ******************************************************

    var isDragging by mutableStateOf(false)
    var draggedItem by mutableStateOf(emptyListItem)
    var draggedItemState by mutableStateOf(DraggedItemStates.IDLE)

    fun updateIsDragging(new : Boolean) = viewModelScope.launch{
        //Log.v("uiVM updateisdragging", "new = $new")
        isDragging = new
        delay(10)
        if (!new){
            draggedItem = emptyListItem
            letDraggedItemState(DraggedItemStates.IDLE)
        }
    }

    fun updateDraggedItem(new : ListItem){
        //Log.v("uiVM updatedraggedItem", "new = $new")
        if (isMovingGroup){
            toGroup = new.group
            toIndex = new.ord
            letDraggedItemState(DraggedItemStates.ITEMUPDATED)
        }
        else {
            fromGroup = new.group
            fromIndex = new.ord
            letDraggedItemState(DraggedItemStates.ITEMGOT)
        }
        draggedItem = new
    }

    fun letDraggedItemState(new : DraggedItemStates){
        draggedItemState = new
    }

    //Group Change Stuff ************************************************

    var inGroup by mutableStateOf(-1)
    var fromGroup by mutableStateOf(-1)
    var toGroup by mutableStateOf(-1)
    var fromIndex by mutableStateOf(-1)
    var toIndex by mutableStateOf(-1)

    var isMovingGroup by mutableStateOf(false)


    //the coeff 10 because pointerId always increments by 1
    //this way groupChangeCheck changes if pointerId or inGroup changes
    var groupChangeCheck by mutableStateOf(pointerId.value + 10*inGroup)

    //this has 40 ms delay in
    //DraggedFakeBox from uiVM.isDragging becoming true
    fun updateInGroup(new : Int){
        //Log.v("uiVM updateingroup", "new = $new")
        inGroup = new
        groupChangeCheck = pointerId.value + 10*inGroup
    }

    fun updateInGroupWhenDragging() {
        inGroup = findGroupMove(
            group = draggedItem.group,
            offset = pointerPositionFlow.value+initialFakeBoxOffset+draggedBoxSize.toHalfOffset(),
            columnWidth = listBoundaries.xCenter,
            columnHeight = listBoundaries.yCenter
        )
        groupChangeCheck = pointerId.value + 10*inGroup
        //Log.v("uivm updateInGroupFromOffset", "inGroup = $inGroup")
    }



    fun isMovingGroup(new : Boolean) {
        isMovingGroup = new
        if (new) toGroup = inGroup
        else fromGroup = inGroup
    }

    //State Booleans *************************************************

    var isClickable by mutableStateOf(false)



    //Adding Item *************************************************

    var hasAddedItem by mutableStateOf(false)
    var newItem by mutableStateOf(emptyListItem)

    var centerBoxOffset : Offset = listOffsets[3]-Offset(x = 200f, y = 80f)

    fun addItem(item : ListItem){
        newItem = item
        draggedItemState = DraggedItemStates.ITEMGOT
        initialFakeBoxOffset = centerBoxOffset
        hasAddedItem = true
    }

    fun updateInGroupFromOffsetWhenAdded(){
        inGroup = findGroupMove(
            group = 4,
            offset = pointerPositionFlow.value,
            columnWidth = listBoundaries.xCenter,
            columnHeight = listBoundaries.yCenter
        )
        groupChangeCheck = pointerId.value + 10*inGroup
        Log.v("uivm updateInGroupFromOffsetWhenAdded", "inGroup = $inGroup")
    }

    fun addItemToData(item : ListItem){
        newItem = item
        draggedItemState = DraggedItemStates.ITEMGOT
        initialFakeBoxOffset = centerBoxOffset
        hasAddedItem = true
    }

    fun cancelAdding(){
        hasAddedItem = false
    }








}

enum class DraggedItemStates{
    IDLE,
    ITEMGOT,
    POSITIONGOT,
    DRAWING,
    ITEMUPDATED
}


data class ListOffset(
    val group : Int,
    val offset : Offset
)


/*
    fun checkInGroup() = viewModelScope.launch{
        updateInGroupFromOffset()
    }
    */
