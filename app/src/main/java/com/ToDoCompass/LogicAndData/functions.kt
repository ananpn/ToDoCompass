package com.ToDoCompass.LogicAndData

import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.ToDoCompass.LogicAndData.Constants.Companion.TDCdivider0
import com.ToDoCompass.LogicAndData.Constants.Companion.emptyTaskAlarm
import com.ToDoCompass.LogicAndData.Constants.Companion.vibrationPatternDefault
import com.ToDoCompass.database.NotifType
import com.ToDoCompass.database.TaskAlarm
import kotlin.math.min

fun offsetToGroupOffset(//group : Int = 0,
                        offsetIn : Offset = Offset.Zero,
                        xIn : Float = offsetIn.x,
                        yIn : Float = offsetIn.y,
                        columnSize: IntSize,
                        ) : Offset {
    var x = xIn
    var y = yIn
    //Log.v("offsetToGroupOffset", "offset = $offset")
    //Log.v("offsetToGroupOffset", "columnSize = $columnSize")
    while (x - columnSize.width >= 0){
        //Log.v("offsetToGroupOffset", "offset.x = ${offset.x}")
        x = x-columnSize.width
    }
    while (y - columnSize.height >= 0){
        y = y-columnSize.height
    }
    return Offset(x = x, y=y)
}

fun findGroupFromOffset(group : Int = 0,
                        offset : Offset,
                        columnSize: IntSize,
) : Offset {
    var offset = offset
    Log.v("offsetToGroupOffset", "columnSize = $columnSize")
    while (offset.x - columnSize.width >= 0){
        Log.v("offsetToGroupOffset", "offset.x = ${offset.x}")
        offset = offset.copy(x = offset.x-columnSize.width)
    }
    while (offset.y - columnSize.height >= 0){
        offset = offset.copy(y = offset.y-columnSize.height)
    }/*
    if (group == 1){
        offset = offset.copy(
            x = offset.x-columnSize.width
        )
    }
    if (group == 2){
        offset = offset.copy(
            y = offset.y-columnSize.height
        )

    }
    if (group == 3){
        offset = offset.copy(
            x = offset.x-columnSize.width,
            y = offset.y-columnSize.height
        )
    }*/
    return offset
}

@Composable
fun getGroupColor(group : Int) : Color{
    when (group) {
        0 -> return MaterialTheme
            .colorScheme.tertiaryContainer
            .copy(
                alpha = 1f,
                red = MaterialTheme.colorScheme.tertiaryContainer.red * 1.0f.coerceAtMost(1f),
                blue = MaterialTheme.colorScheme.tertiaryContainer.blue * 0.92f.coerceAtMost(1f),
                green = MaterialTheme.colorScheme.tertiaryContainer.green * 1.1f.coerceAtMost(1f),
            )
        1 -> return MaterialTheme
            .colorScheme.tertiaryContainer
            .copy(
                alpha = 1f,
                red = MaterialTheme.colorScheme.tertiaryContainer.red * 1.15f.coerceAtMost(1f),
                blue = MaterialTheme.colorScheme.tertiaryContainer.blue * 0.83f.coerceAtMost(1f),
                green = MaterialTheme.colorScheme.tertiaryContainer.green * 0.8f.coerceAtMost(1f),
            )
        2 -> return MaterialTheme
            .colorScheme.primaryContainer
            .copy(
                alpha = 1f,
                red = MaterialTheme.colorScheme.primaryContainer.red * 0.9f.coerceAtMost(1f),
                blue = MaterialTheme.colorScheme.primaryContainer.blue * 0.9f.coerceAtMost(1f),
                green = MaterialTheme.colorScheme.primaryContainer.green * 1.1f.coerceAtMost(1f),
            )
        3 -> return MaterialTheme
            .colorScheme.secondaryContainer
            .copy(
                alpha = 1f,
                red = MaterialTheme.colorScheme.secondaryContainer.red * 0.9f.coerceAtMost(1f),
                blue = MaterialTheme.colorScheme.secondaryContainer.blue * 0.95f.coerceAtMost(1f),
                green = MaterialTheme.colorScheme.secondaryContainer.green * 0.9f.coerceAtMost(1f),
            )
        4 -> return MaterialTheme
            .colorScheme.secondaryContainer
            .copy(
                alpha = 1f,
                red = MaterialTheme.colorScheme.secondaryContainer.red * 0.75f.coerceAtMost(1f),
                blue = MaterialTheme.colorScheme.secondaryContainer.blue * 0.55f.coerceAtMost(1f),
                green = MaterialTheme.colorScheme.secondaryContainer.green * 0.95f.coerceAtMost(1f),
            )
    }
    if (group == 0) return Color.LightGray
    if (group == 1) return Color.DarkGray
    if (group == 2) return Color.Blue
    if (group == 3) return Color.Yellow
    if (group == 4) return Color.Green
    return Color.LightGray
}

@Composable
fun getGroupColorDraggedFake(group : Int) : Color{
    var output = getGroupColor(group = group)
    output = output.copy(
        alpha = 0.9f,
        red = min(output.red*1.05f, 1f),
        green = min(output.green*1.05f, 1f),
        blue = min(output.blue*1.05f, 1f),
        )
    //if (group == 1) output = Color.DarkGray
    //if (group == 2) output = Color.Blue
    //if (group == 3) output = Color.Yellow
    //if (group == 4) output = Color.Green
    return output
}

@Composable
fun getGroupColorCard(group : Int) : Color{
    var output = getGroupColor(group = group)
    output = output.copy(
        alpha = 1f,
        red = min(output.red*0.7f, 1f),
        green = min(output.green*0.7f, 1f),
        blue = min(output.blue*0.7f, 1f),
    )
    //if (group == 1) output = Color.DarkGray
    //if (group == 2) output = Color.Blue
    //if (group == 3) output = Color.Yellow
    //if (group == 4) output = Color.Green
    return output
}

fun <T> SnapshotStateList<T>.swapList(newList: List<T>){
    clear()
    addAll(newList)
}

fun offsetToIntOffset(//group : Int = 0,
    offset : Offset = Offset.Zero,
) : IntOffset {
    return IntOffset(offset.x.toInt(), offset.y.toInt())
}

fun findGroupMove(group : Int,
                  offset : Offset,
                  columnWidth : Int, //487
                  columnHeight : Int //922
) : Int {
    //Log.v("functions findgroupmove", "launch group = $group, offset = $offset")
    //Log.v("functions findgroupmove", "columnWidth = $columnWidth, columnHeight = $columnHeight")
    val leftToRight : Int = 250
    val rightToLeft : Int = 160
    val topToBottom : Int = 250
    val bottomToTop : Int = 140
    if (group == 0){
        if (offset.x < columnWidth+leftToRight){
            if (offset.y < columnHeight+topToBottom)
                return 0
            else return 2
        }
        else{
            if (offset.y < columnHeight)
                return 1
            else return 3
        }

    }
    if (group == 1){
        if (offset.x > columnWidth-rightToLeft){
            if (offset.y < columnHeight+topToBottom)
                return 1
            else return 3
        }
        else{
            if (offset.y < columnHeight)
                return 0
            else return 2
        }

    }
    if (group == 2){
        if (offset.x < columnWidth + leftToRight){
            if (offset.y > columnHeight-bottomToTop)
                return 2
            else return 0
        }
        else{
            if (offset.y < columnHeight)
                return 1
            else return 3
        }

    }
    if (group == 3){
        if (offset.x > columnWidth-rightToLeft){
            if (offset.y > columnHeight-bottomToTop)
                return 3
            else return 1
        }
        else{
            if (offset.y < columnHeight)
                return 0
            else return 2
        }
    }
    if (group == 4){
        //Log.v("functions findgroupmove", "group 4 offset = $offset")
        if (offset.x < columnWidth - rightToLeft){
            if (offset.y < columnHeight - topToBottom)
                return 0
            if (offset.y > columnHeight + bottomToTop)
                return 2
        }
        if (offset.x > columnWidth + rightToLeft){
            if (offset.y < columnHeight - topToBottom)
                return 1
            if (offset.y > columnHeight + bottomToTop)
                return 3
        }
        return 4
    }
    return 0
}

@Composable
fun getBorderColor(color : Color) : Color{
    return color.copy(alpha = 0.5f)
}

@Composable
fun getTableColor(bgColor : Color) : Color{
    return bgColor.copy(
        alpha = 0.9f,
        red = 0.93f*bgColor.red,
        green = 0.93f*bgColor.green,
        blue = 0.93f*bgColor.blue
    )
}

fun obtainSystemDefaultAlarmRingtone() : Uri {
    return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
}

fun obtainSystemDefaultNotificationRingtone() : Uri {
    return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
}

fun obtainSystemDefaultVibrationPatternString() : String {
    return vibrationPatternDefault.pattern.toVibrationPatternString()
}

fun obtainSystemDefaultNotificationTypeForGroup(group : Int) : NotifType {
    return NotifType(
        notifTypeId = -group - 1,
        name = "Default",
        soundUriString = obtainSystemDefaultAlarmRingtone().toString(),
        vibrationPatternString = obtainSystemDefaultVibrationPatternString(),
        persistentLength = 0,
        rampUp = false,
        respectSystem = false
    )
}

fun constructAlarmString(alarmList : List<TaskAlarm>) : String{
    val size = alarmList.size
    if (size == 0) return ""
    else {
        val nextAlarm = alarmList.firstOrNull() ?: emptyTaskAlarm
        return size.toString() + TDCdivider0 + nextAlarm.date + TDCdivider0 + nextAlarm.time
    }
}

