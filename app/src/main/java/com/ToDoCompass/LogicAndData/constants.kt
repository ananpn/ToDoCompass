package com.ToDoCompass.LogicAndData

import ItemPosition
import android.Manifest
import android.os.Build
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.ToDoCompass.LogicAndData.LogicConstants.Companion.EMPTY_NOTIF_TYPE
import com.ToDoCompass.LogicAndData.LogicConstants.Companion.NOTIF_TYPE_USE_DEFAULT_ALARM
import com.ToDoCompass.LogicAndData.LogicConstants.Companion.SILENT_NOTIF_TYPE
import com.ToDoCompass.LogicAndData.LogicConstants.Companion.USE_GROUP_DEFAULT
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NULL_STRING
import com.ToDoCompass.ViewModels.ListOffset
import com.ToDoCompass.database.ListItem
import com.ToDoCompass.database.NotifType
import com.ToDoCompass.database.Task
import com.ToDoCompass.database.TaskAlarm
import com.ToDoCompass.database.TaskProfile
import com.ToDoCompass.di.AppSettingsData
import com.ToDoCompass.uiComponents.smallComponents.GroupDefaultNotifType


class Constants {
    companion object {
        val basicPermissions =  //API 31
            if (Build.VERSION.SDK_INT >= 33){
                arrayOf(
                    Manifest.permission.SCHEDULE_EXACT_ALARM,
                    Manifest.permission.USE_EXACT_ALARM,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.RECEIVE_BOOT_COMPLETED,
                    Manifest.permission.SET_ALARM,
                    Manifest.permission.VIBRATE,
                    Manifest.permission.USE_FULL_SCREEN_INTENT,
                )
            }
            else if (Build.VERSION.SDK_INT >= 31){
                arrayOf(
                    Manifest.permission.SCHEDULE_EXACT_ALARM,
                    Manifest.permission.RECEIVE_BOOT_COMPLETED,
                    Manifest.permission.SET_ALARM,
                    Manifest.permission.VIBRATE,
                    Manifest.permission.USE_FULL_SCREEN_INTENT,
                    Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                )
            }
            else arrayOf(
                Manifest.permission.INTERNET,
                Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
            )

        val defaultDarkTheme : Boolean = true
        val defaultSeedColorData : Float = 0f
        val defaultDispProfileId : Int = 0
        val defaultPaletteData : Int = 0
        
        const val TDCdivider0 = "|€|"
        const val TDCdivider1 = ","
        
        
        
        val vibrationPatternSilent = VibrationPatternData(
            "No vibration",
            longArrayOf(100, 0, 100)
        )
        
        val vibrationPatternDefault = VibrationPatternData(
            "Default",
            longArrayOf(0, 1000, 500, 1000)
        )
        val vibrationPatternLong = VibrationPatternData(
            "Long",
            longArrayOf(0, 1200, 800, 1200, 800, 1200, 800)
        )
        val vibrationPatternShort = VibrationPatternData(
            "Short",
            longArrayOf(0, 500, 500, 500, 250)
        )
        val vibrationPatternBlip = VibrationPatternData(
            "Blip",
            longArrayOf(0, 100, 100, 100, 100)
        )
        
        val defaultCustomVibrationPattern = VibrationPatternData(
            "Custom",
            longArrayOf(0, 1000, 500, 1000)
        )
        
        
        val defaultNotificationType = NotifType(
            notifTypeId = NOTIF_TYPE_USE_DEFAULT_ALARM,
            notifTypeOrder = 0,
            name = "Default",
            soundUriString = obtainSystemDefaultAlarmRingtone().toString(),
            soundFileName = "(Default alarm)",
            vibrationPatternString = obtainSystemDefaultVibrationPatternString(),
            persistentLength = 0,
            rampUp = false,
        )
        
        val emptyNotificationType = NotifType(
            notifTypeId = EMPTY_NOTIF_TYPE,
            notifTypeOrder = EMPTY_NOTIF_TYPE,
            name = "",
            soundUriString = NULL_STRING,
            soundFileName = "",
            vibrationPatternString = vibrationPatternSilent.toString(),
            persistentLength = 0,
            rampUp = false,
        )
        
        val emptyVibrationPattern = vibrationPatternSilent
        
        val silentNotificationType = NotifType(
            notifTypeId = SILENT_NOTIF_TYPE,
            notifTypeOrder = SILENT_NOTIF_TYPE,
            name = "Silent",
            soundUriString = NULL_STRING,
            soundFileName = "Silent",
            vibrationPatternString = obtainSystemDefaultVibrationPatternString(),
            persistentLength = 0,
            rampUp = false,
        )
        
        val defaultAlarmNotificationType = NotifType(
            notifTypeId = NOTIF_TYPE_USE_DEFAULT_ALARM,
            notifTypeOrder = NOTIF_TYPE_USE_DEFAULT_ALARM,
            name = "Default alarm",
            soundFileName = "(Default alarm)",
            soundUriString = obtainSystemDefaultAlarmRingtone().toString(),
            vibrationPatternString = obtainSystemDefaultVibrationPatternString(),
            persistentLength = 30,
            rampUp = false,
        )
        
        val testNotificationType = NotifType(
            notifTypeId = NOTIF_TYPE_USE_DEFAULT_ALARM,
            notifTypeOrder = NOTIF_TYPE_USE_DEFAULT_ALARM,
            name = "Default alarm",
            soundFileName = "(Default alarm)",
            soundUriString = obtainSystemDefaultAlarmRingtone().toString(),
            vibrationPatternString = obtainSystemDefaultVibrationPatternString(),
            persistentLength = 60,
            rampUp = true,
        )
        
        val useGroupDefaultNotificationType = NotifType(
            notifTypeId = USE_GROUP_DEFAULT,
            notifTypeOrder = USE_GROUP_DEFAULT,
            name = "(Use group default)",
            soundFileName = "(Use group default)",
            soundUriString = obtainSystemDefaultAlarmRingtone().toString(),
            vibrationPatternString = obtainSystemDefaultVibrationPatternString(),
            persistentLength = 30,
            rampUp = false,
        )
        
        val defaultGroupDefaultNotifType = GroupDefaultNotifType(
            name = "Default",
            notifTypeId = -1,
            groupDefaultNotifTypeId = 0
        )
        

        val emptyAppSettingsData = AppSettingsData(
            darkTheme = defaultDarkTheme,
            seedColorData = defaultSeedColorData,
            dispProfileId = defaultDispProfileId,
            paletteData = defaultPaletteData,
        )


        //animation durations, delays ***********************************************
        const val appInitializedDelay : Long = 50 //must be long since this is in delay()
        const val appDarkThemeGetDelay : Long = 70 //must be long since this is in delay()
        const val mainGridGroupChangeDelay : Long = 120 //must be long since this is in delay()
        const val mainGridGroupChangeAnimationDur : Int = 150
        const val navigationGraphEnterDelay : Int = 170
        const val navigationGraphEnterDur: Int = 200
        const val navigationGraphExitDur: Int = 130
        const val settingsEnterDur : Int = 150
        const val spacerHeightInt : Int = 3

        //Weights ************

        const val BT_WEIGHT = 9f
        const val LR_WEIGHT = 6f


        //Placeholders ************************************************************
        val emptyTask = Task(
            id=0,
            profile = 0,
            ord = 0,
            title = "",
        )

        val emptyTaskAlarm = TaskAlarm(
            alarmId = 0,
            parentId = 0,
            date = "",
            time = "",
            active = false,
            note = "",
            notifTypeId = 0,

        )

        val emptyProfile = TaskProfile(
            idProfile=0,
            profileOrder = 0,
            profileTitle = ""
        )

        val emptyListItem = ListItem(
            uniqueId = -1,
            ord = -1,
            profile = -1,
            group = -1,
            title = "",
            isChild = false,
            idOfParent = -1)
        val emptyItemPosition = ItemPosition(0,0)
        val emptyListOffset : ListOffset = ListOffset(0, Offset.Zero)

        val centerPress = PressInteraction.Press(Offset(200f,100f))

        //Other Constants******************************************************

        const val firstColumnWeight = 1.5f //weight in maingrid for title box
        const val taskTotalsFirstWeight = 1.1f
        const val taskTotalsWeight = 1f
        const val lastDoneWeight = 0.5f

        const val labelFontSize = 12

        const val normalFontSize = 10.5
        const val rowTotalsFontSize = 11.5
        const val lastDoneFontSize = 10

        const val defaultColorFloat = 345f
        val defaultSeedColor = transformFloatToColor(float = defaultColorFloat)

        val dateBoxHeight = 55.dp
        val boxHeight = 50.dp

        val pressZero = PressInteraction.Press(Offset(0.5f, 0.5f))

        val paletteItems = listOf(
            "PaletteStyle.TonalSpot",
            //"PaletteStyle.Expressive",
            "PaletteStyle.Neutral",
            //"PaletteStyle.Content",
            "PaletteStyle.FruitSalad",
            //"PaletteStyle.Fidelity",
            "PaletteStyle.Rainbow",
            "PaletteStyle.Vibrant"
        )

        const val dateRowHeight = 55

        val dateRowModifier = Modifier
            .height(dateRowHeight.dp)
        
        val onlyNumbersPattern = Regex("^\\d+\$")
        val decimalPattern = Regex("^\\d+\\.?\\d*\$")
        val noSpecialCharPattern = Regex("^\\d+\\.?\\d*\$")
        //val startSpacePattern = Regex("^\\s+")
        val whiteSpaceAnyWherePattern = Regex("\\s+")
        val spaceAnyWherePattern = Regex("\" \"+")
        val newLinePattern = Regex("\\v")
        val textInputNegativePattern = Regex("^\\v+\$")
        
        
        
        //time
        val currentDateStringConst = TimeFunctions.getTimeNow("yyyyMMdd")
        //val currentDate = LocalDate.now()
        private val lastMonday = TimeFunctions.lastMonday()
        val currentWeek = TimeFunctions.generateWeek(lastMonday)
        val currentWeekStrings = currentWeek.map{ date ->
            TimeFunctions.formatToString(
                date,
                "yyyyMMdd"
            )
        }
        val alarmTimeFormat = "yyyy-MM-ddHH:mm"

    }
}

data class VibrationPatternData(
    val title : String,
    val pattern : LongArray
)


