package com.ToDoCompass.LogicAndData

import android.util.Log
import androidx.compose.runtime.Composable
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import kotlin.math.roundToLong

class TimeFunctions {
    companion object {
        /**Takes format eg "HHmmddMMyyy" or "yyyy" and returns the current time in this format.
        * Note: HH is 24h clock, hh 12h clock.
         */
        fun getTimeNow(format : String) : String {
            val formatter = DateTimeFormatter.ofPattern(format)
            val current = LocalDateTime.now().format(formatter)
            return current
        }

        fun formatToLocalDateTime(
            time : String,
            format : String //must have
        ) : LocalDateTime {
            //Log.v("fTLC time format", time+" "+format)
            val formatter = DateTimeFormatter.ofPattern(format)
            val output = LocalDateTime.parse(time, formatter)
            return output
        }

        fun formatToString(date : LocalDate, format : String) : String {
            val formatter = DateTimeFormatter.ofPattern(format)
            val output = date.format(formatter)
            return output
        }
        
        fun formatLocalDateTimeToString(time : LocalDateTime, format : String) : String {
            val formatter = DateTimeFormatter.ofPattern(format)
            val output = time.format(formatter)
            return output
        }

        fun formatToLocalDate(time : String, format : String) : LocalDateTime {
            //Log.v("fTLC time format", time+" "+format)
            val formatter = DateTimeFormatter.ofPattern(format)
            val output = LocalDateTime.parse(time, formatter)
            return output
        }

        fun formatStringToDisplay(input : String) : String {
            return input.takeLast(2)+"."+input.substring(4,6)
        }

        @Composable
        fun timeMillis(time : String) : Long {
            val date= formatToLocalDate(time.replace(" ", "0"),
                "HHmmddMMyyyy")
            val millis = date.toEpochSecond(ZoneId.systemDefault()
                                                    .getRules()
                                                    .getOffset(Instant.now())
                    )*1000
            //Log.v("timeMillis", time+" "+millis)
            return millis
        }

        fun generateWeek(monday: LocalDate): List<LocalDate> {
            val dateList = mutableListOf<LocalDate>()
            var currentDate = monday

            while (currentDate <= monday.plusDays(6)) {
                dateList.add(currentDate)
                currentDate = currentDate.plusDays(1)
            }

            return dateList
        }

        fun lastMonday() : LocalDate{
            var output = LocalDate.now()
            while (output.dayOfWeek != DayOfWeek.MONDAY){
                output = output.minusDays(1)
            }
            return output
        }

        fun isCurrentDay(date : String) : Boolean {
            return (getTimeNow("yyyyMMdd") == date)
        }

        fun isTimeBeforeCurrentFull(time : String, format : String) : Boolean {
            try {
                val timeAsLocalDateTime = formatToLocalDateTime(time, format)
                val formatterYMD = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
                val formattedTime = timeAsLocalDateTime.format(formatterYMD)
                val currentYMD = LocalDateTime.now().format(formatterYMD)
                return formattedTime < currentYMD
            }
            catch (e : Exception) {
                Log.v("isTimeBeforeCurrentFull","error $e")
                return false
            }
        }
        
        
        
        /** Format: date yyyy-dd-MM time HH:mm */
        fun epochMillisFromDateAndTime(date : String, time : String) : Long {
            val (hours, min) = //listOf(12,4)
                time.take(5)!!.split(":").map { it.toInt() }
            val (year, month, day) =
                //listOf(2024,1,19)
                date!!.split("-")
                    .map { it.toInt() }
            val calendar = Calendar.getInstance()
            calendar.set(year, month - 1, day, hours, min,0)
            return calendar.timeInMillis
            
        }





    }
}

fun LocalDate.toDisplayStringMMdd() : String{
    return "${this.monthValue}.${this.dayOfWeek}"
}

fun LocalTime.toDisplayStringHHmm() : String{
    return "${this.hour}:${this.minute}"
}

fun LocalDateTime.toStringInFormat(format : String) : String{
    //format for example "HH:mm, dd.MM.yyyy"
    val formatter = DateTimeFormatter.ofPattern(format)
    val output = this.format(formatter)
    return output
}

fun Long.toTimeUnitString(
    minutes : Boolean = true,
    seconds : Boolean = false,
) : String{
    //millis to weeks, days, hours, minutes, seconds
    
    //Log.v("Long.toTimeUnitString", "launch this = $this")
    var outputString = ""
    
    fun prependOutput(number : Long, postfix : String){
        if (number != 0L){
            val postfixToAdd = when(outputString == ""){
                true -> postfix.trimEnd().dropLast(1)
                false -> postfix
            }
            outputString = outputString.prepend(number.toString() + postfixToAdd)
        }
        
    }
    val secondsTotal = (this.toDouble()/1000.0).roundToLong()
    val secondsDisp = when (seconds){
        true -> secondsTotal%60
        false -> 0L
    }
    if (seconds) prependOutput(secondsDisp, " seconds, ")
    val minutesTotal = (secondsTotal-secondsDisp)/60
    if (minutesTotal == 0L){
        prependOutput(secondsTotal%60, " seconds, ")
    }
    val minutesDisp = when(minutes){
        true -> minutesTotal%60
        false -> 0L
    }
    if (minutes) prependOutput(minutesDisp, " minutes, ")
    val hoursTotal = (minutesTotal-minutesDisp)/60
    val hoursDisp = hoursTotal%24
    prependOutput(hoursDisp, " hours, ")
    val daysTotal = (hoursTotal-hoursDisp)/24
    val daysToDisp = daysTotal%7
    prependOutput(daysToDisp, " days, ")
    val weeksTotal = (daysTotal - daysToDisp)/7
    prependOutput( weeksTotal, " weeks, ")
    if (outputString == ""){
        outputString = "0 minutes"
    }
    return outputString
    
    
}


fun String.prepend(input : String) : String {
    return input + this
}

