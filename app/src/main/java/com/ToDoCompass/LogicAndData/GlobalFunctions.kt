package com.ToDoCompass.LogicAndData

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.ColorUtils.HSLToColor
import com.ToDoCompass.LogicAndData.Constants.Companion.paletteItems
import com.ToDoCompass.LogicAndData.TimeFunctions.Companion.formatToLocalDate
import com.ToDoCompass.database.Task
import com.ToDoCompass.database.TaskProfile
import com.materialkolor.PaletteStyle
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random


fun roundToDecimalPlaces(number: Float, scale : Int): Float {
    val bd = BigDecimal(number.toDouble())
        .setScale(scale, RoundingMode.HALF_UP)
    return bd.toFloat()
}

@Composable
fun lastOrder(tasks : List<Task>) : Int {
    var output : Int = 0
    for (task in tasks){
        output = max(output, task.ord)
    }
    return output
}

//@Composable
fun lastGroupOrder(groups : List<TaskProfile>) : Int {
    var output : Int = 0
    for (group in groups){
        output = max(output, group.profileOrder)
    }
    return output
}
/*
fun handleUigroupOrder(bigger : Int, smaller : Int, viewModel : GridViewModel) {
    if (viewModel.uiState.dispGroupId == bigger){
        viewModel.setDispGroup(groupOrder = smaller)

    }
    else if (viewModel.uiState.dispGroupId == smaller){
        viewModel.setgroupOrder(groupOrder = bigger)
    }
}*/

/*
@Composable
fun transformColorToLong(color : Color) : Long {
    val output = color.value.toLong()
    Log.v("transformColorToLong", "output = ${color.value}")
    return output
}
*/

fun transformFloatToColor(float : Float, defaultColorFloat : Float = 36f) : Color {
    val float2 = (180f+defaultColorFloat-float).mod(360f)
    //max(float+defaultColorFloat-180f,0f)+max(360f-defaultColorFloat-180f+float,0f)

    //Log.v("transformFloatToColor", "float2 = ${float2}")
    val output = Color(HSLToColor(floatArrayOf(float2, 1f, 0.5f)))
    //val output = Color(float.toLong()*417)
    //Log.v("transformFloatToColor", "output = ${output}")
    return output
}

@Composable
fun transformIntToPaletteStyle(input : Int) : PaletteStyle {
    val string = paletteItems[input]
    if (string == "PaletteStyle.TonalSpot")
        return PaletteStyle.TonalSpot
    if (string == "PaletteStyle.Neutral")
        return PaletteStyle.Neutral
    if (string == "PaletteStyle.Vibrant")
        return PaletteStyle.Vibrant
    if (string == "PaletteStyle.FruitSalad")
        return PaletteStyle.FruitSalad
    return PaletteStyle.Rainbow
}


@Composable
fun transformBGColor(color : Color) : Color{
    val output = color.copy(
        //alpha = (color.alpha+0.9f)/2,
        red = (color.red+0.1f)/1.1f,
        //red = min(1f,(color.red+0.7f)/1.6f),
        green = calculateTransform(color.green),
        blue = calculateTransform(color.blue)
    )
    //Log.v("transformBGColor", "output = ${output}")
    return output
}

fun calculateTransform(x: Float): Float {
    //val result = (1 + (tan((PI - 0.5) * (x - 0.5))) / 5) / 2
    var result : Float = 0f
    if (x<=0.5) {
        result = (x + 0.04f) / 0.96f
    }
    if (x>0.5){
        result = (x-0.07f)
    }
    //return result
    return result
}

fun selectedColor(color : Color) : Color{
    val output = color.copy(
        //alpha = (color.alpha+0.9f)/2,
        red = calculateTransform(color.red),
        //red = min(1f,(color.red+0.7f)/1.6f),
        green = calculateTransform(color.green),
        blue = calculateTransform(color.blue)
    )
    return output
}

fun formatCountToDisp(count : Int, denominator : Int) : String{
    var drawnCount : Float = 0f
    try{
        drawnCount = count.toFloat() / denominator.toFloat()
    }
    catch (e : Exception){
        drawnCount = count.toFloat()
    }
    val decimals = drawnCount.toString()
        .replace(
            Regex("(^-?\\d+\\.\\d*[1-9])(0+\$)|(\\.0+\$)"),
            "$1"
        ) //trims end zeroes
        .substringAfterLast(".", "").length
    val decimals2 = min(decimals, 2)
    val format: String = "%.${decimals2}f"
    return String
        .format(format, drawnCount)
        .replace(
            Regex("(^-?\\d+\\.\\d*[1-9])(0+\$)|(\\.0+\$)"),
            "$1"
        )
        .replace(Regex("^0{1}$"), "")
        .take(5)
        .dropLastWhile { it.toString() == "." }
}

fun formatCountToFloat(count : Int, denominator: Int) : Float{
    val drawnCount = count.toFloat() / denominator.toFloat()
    return drawnCount
}


fun formatTotalToDisp(total : Float) : String{
    val decimals = total.toString()
        .replace(
            Regex("(^-?\\d+\\.\\d*[1-9])(0+\$)|(\\.0+\$)"),
            "$1"
        ) //trims end zeroes
        .substringAfterLast(".", "").length
    val decimals2 = min(decimals, 2)
    val format: String = "%.${decimals2}f"
    return String
        .format(format, total)
        .replace(
            Regex("(^-?\\d+\\.\\d*[1-9])(0+\$)|(\\.0+\$)"),
            "$1"
        )
        .replace(Regex("^0{1}$"), "")
        .take(5)
        .dropLastWhile { it.toString() == "." }
}

fun booleanToInt(boolean : Boolean) : Int{
    if (boolean) return 1
    return 0
}

fun gcd(a: Int, b: Int): Int {
    if (b == 0) return a
    return gcd(b, a % b)
}

fun lcm(a: Int, b: Int): Int {
    return a / gcd(a, b) * b
}

fun newDenominator(oldDenominator : Int, clickStep : Int) : Int {
    return (oldDenominator*clickStep)/ gcd(oldDenominator, clickStep)
}

fun xIndexFromOffset(xOffset : Float, screenWidthPx : Float) : Int{
    var index = 0
    while (xOffset > (screenWidthPx*(1.5f+index.toFloat()))/8.5f){
        index+=1
    }
    return index
}

fun newLineAtCenter(text : String) : String{
    var centerSpaceInd = 0
    var minVal = text.lastIndex*2
    for(i in 1..text.lastIndex-1) {
        if (text.substring(i..i) == " "){
            if (abs(text.lastIndex-2*i) < minVal) {
                centerSpaceInd = i
                minVal = abs(text.lastIndex - 2 * i)
            }
        }
    }
    if (centerSpaceInd >0 ) {
        return text.replaceRange(centerSpaceInd..centerSpaceInd,
            "\n")
    }
    else return text

}

fun formatLastDateToDisp(lastDateString : String) : String{
    if (lastDateString.isEmpty() || lastDateString == "0") return ""
    val lastDate = formatToLocalDate(lastDateString+"0000", "yyyyMMddHHmm")
    val currentDate = LocalDateTime.now()
    val days = Duration.between(lastDate, currentDate).toDays().toInt()
    if (days == 0) return "Today"
    if (days == 1) return "Yesterday"
    else return "$days days ago"

}

fun getNotificationUniqueId() = System.currentTimeMillis() % 1000000


fun obtainRandomRequestCode() : Int {
    val output = Random.nextInt(10000000, 30000000)
    Log.v("obtainRandomRequestCode", "output = $output")
    return output
}

fun countMatches(text : String, pattern : Regex) : Int{
    return pattern.split(text).count()-1
}

fun breakWordAtCenter(text : String, maxIndexOnRow : Int = 1000) : String{
    val indexToInject = min(text.lastIndex/2, maxIndexOnRow)
    return text.replaceRange(indexToInject..< indexToInject,
                             "-\n")
    
}


