package com.ToDoCompass.LogicAndData

import android.net.Uri
import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.ToDoCompass.LogicAndData.Constants.Companion.TDCdivider0
import com.ToDoCompass.LogicAndData.Constants.Companion.TDCdivider1
import com.ToDoCompass.LogicAndData.Constants.Companion.vibrationPatternDefault
import com.ToDoCompass.database.ListItem
import com.ToDoCompass.database.Task

fun taskToListItem(task : Task) : ListItem {
    return ListItem(
        uniqueId = task.id,
        group = task.groupId,
        profile = task.profile,
        title = task.title,
        ord = task.ord,
        isChild = task.isChild,
        idOfParent = task.idOfParent,
    )
    
}

fun listItemToTask(task : Task) : ListItem {
    return ListItem(
        uniqueId = task.id,
        group = task.groupId,
        profile = task.profile,
        title = task.title,
        ord = task.ord,
        isChild = task.isChild,
        idOfParent = task.idOfParent,
    )
    
}

fun IntSize.toHalfOffset(): Offset {
    return Offset(this.width.toFloat()/2f, this.height.toFloat()/2f)
}


fun String?.toSoundUri() : Uri?{
    this?.let{
        return Uri.parse(it)
    }
    return null
}

fun String?.toVibrationPatternData() : VibrationPatternData{
    try{
        val list0 = this?.split(TDCdivider0)
        val title = list0?.firstOrNull() ?:vibrationPatternDefault.title
        val pattern = list0?.lastOrNull().toVibrationPattern()
        return VibrationPatternData(
            title = title,
            pattern = pattern
        )
    }
    catch (e : Exception){
        Log.v("string to vibpat", "exception $e")
    }
    return Constants.vibrationPatternDefault
}

fun VibrationPatternData.toStringForStore() : String{
    return this.title + TDCdivider0 + this.pattern.toVibrationPatternString()
    
}

fun String?.toVibrationPatternArray() : LongArray?{
    try{
        
        return this?.split(TDCdivider1)?.map { it.toLong() }?.toLongArray() ?:null
    }
    catch (e : Exception){
    }
    return null
}

fun String?.toVibrationPattern() : LongArray{
    try{
        return this?.split(TDCdivider1)?.map { it.toLong() }?.toLongArray() ?:Constants.vibrationPatternDefault.pattern
    }
    catch (e : Exception){
        Log.v("string to vibpat", "exception $e")
    }
    return Constants.vibrationPatternDefault.pattern
}

fun LongArray.toVibrationPatternString() : String{
    return this.joinToString(TDCdivider1)
}


fun String?.toIntSafe() : Int{
    if (this == null) return 0
    try {
        return this.toInt()
    }
    catch (e : Exception){
        if (this == "") return 0
        else return -1
    }
    return -1
    
    
}
