package com.ToDoCompass.LogicAndData

class StringConstants {
    companion object {
        //Strings *********************************************************************
        
        const val NULL_STRING = "null"
        
        
        const val NOTIF_ALARM_ID = "alarm_id"
        const val NOTIF_REQUEST_CODE = "request_code"
        const val NOTIF_ALARM_TYPE = "alarm_type"
        const val NOTIF_ALARM_NOTE = "alarm_note"
        const val NOTIF_ICON_PATH = "icon_path"
        const val NOTIF_ALARM = "notification_alarm_intent"
        const val NOTIF_DELETE_INTENT = "notification_delete_intent"
        
        
        const val NOTIF_FULL_SCREEN_INTENT = "notification_full_screen_intent"
        const val NOTIF_CONTENT_INTENT = "notification_content_intent"
        const val NOTIF_SNOOZE_INTENT = "notification_snooze_intent"
        const val NOTIF_DONE_INTENT = "notification_done_intent"
        const val NOTIF_BUTTON1_INTENT = "notification_silence_intent"
        const val NOTIF_BUTTON2_INTENT = "notification_dismiss_intent"
        const val NOTIF_BUTTON3_INTENT = "notification_manage_intent"
        
        const val NOTIFICATION_BUTTON_TEXT_1 = "Silence"
        const val NOTIFICATION_BUTTON_TEXT_2 = "Mark as Done"
        const val NOTIFICATION_BUTTON_TEXT_3 = "Reschedule"
        
        
        
        const val NOTIF_ASK_AGAIN = "notification_ask_again"
        const val NOTIF_REPEATING_ALARM = "repeating_alarm"
        const val NOTIF_NON_REPEATING_ALARM = "non_repeating_alarm"
        const val NOTIF_TASK_TITLE = "task_title"
        const val NOTIF_PROPERTIES = "notification_properties"
        const val NOTIFICATION = "notification"
        const val NOTIFICATION_LIST = "notification list"
        const val CHANNEL_ID = "ToDo Compass"
        const val CHANNEL_NAME = "ToDo Compass Channel"



        //Room
        const val TASK_TABLE = "tasks"
        const val NOTE_TABLE = "task_notes"
        const val ALARM_TABLE = "task_alarms"
        const val PROFILE_TABLE = "profiles"
        const val NOTIF_TYPE_TABLE = "notif_types"
        const val DEFAULT_NOTIF_TYPE_TABLE = "default_notif_types"
        const val POSITION_TABLE = "positions"
        const val LIST_ITEM_VIEW = "list_items"

        //Titles
        const val WEEK_TOTAL_TITLE = "All tasks"
        const val LAST_DONE = "Last Done"
        const val DATE_PICK_TITLE = "Pick reminder time."
        const val TIME_PICK_TITLE = "Pick reminder time."
        const val ADD_ALARM_TITLE = "Set reminder"
        const val EDIT_ALARM_TITLE = "Editing reminder "
        const val ALARM_LIST_TITLE = "Reminders of task "
        const val ALARM_NOTE_LABEL = "Note (optional):"
        const val PICK_NOTIF_TYPE = "Notification style:"


        //Actions
        const val CONFIRM_DELETE = "Really delete?"
        const val DAY_TOTAL = "Day total"
        const val CONFIRM_DELETE_ALARM = "Really delete this reminder?"

        //Task Strings
        const val ADD_TASK = "Add a task."
        const val ADD_SUBTASK = "Add a subtask."
        const val DELETE_TASK = "Delete a task."
        const val MODIFY_TASK = "Edit task:"
        const val CONFIRM_DELETE_TASK = "Really delete this task? This also deletes subtasks and reminders."
        const val CONFIRM_DELETE_SUBTASK = "Really delete this subtask? This also deletes reminders."
        const val GIVE_TASK_TITLE = "Name of task:"
        //const val MAXIMUM = "Maximum value..."
        const val GIVE_TASK_CLICK_STEP = "How many taps to add 1:"
        const val SUBTASK_CARD_TITLE = "Name:"

        //Count Strings
        const val MODIFY_COUNT = "Modify count:"

        //Profile Strings
        const val PROFILE_OF_TASK = "Group:"
        const val ADD_PROFILE = "Add a group."
        const val EDIT_PROFILE = "Edit group:"
        const val DELETE_PROFILE = "Delete group."
        const val CONFIRM_DELETE_PROFILE = "Really delete this group? It cannot be recovered."
        const val GIVE_PROFILE_TITLE = "Name group:"
        const val GIVE_PROFILE_CLICK_STEP = "Taps needed to add 1:"
        const val PROFILE_CLICK_STEP_INFO = "Applies to new tasks."
        
        //Dialog strings
        const val ADD_NOTIF_TYPE = "Add a notification profile."
        const val EDIT_NOTIF_TYPE = "Edit notification profile:"
        const val DELETE_NOTIF_TYPE = "Delete notification profile."
        const val CONFIRM_DELETE_NOTIF_TYPE = "Really delete this notification type? It cannot be recovered."
        const val GIVE_NOTIF_TYPE_TITLE = "Name notification profile:"
        const val GIVE_NOTIF_TYPE_DURATION = "Duration in seconds (minimum):"
        

        //Buttons
        const val PREVIOUS_WEEK = "Previous Week"
        const val NEXT_WEEK = "Next Week"
        const val ADD_BUTTON = "Add new"
        const val CREATE_BUTTON = "Create"
        const val ADD = "Add"
        const val ADD_PROFILE_BUTTON = "Add a group"
        const val ADD_TASK_BUTTON = "Add a task"
        const val CANCEL_BUTTON = "Cancel"
        const val UPDATE_BUTTON = "Update"
        const val CONFIRM_BUTTON = "Confirm"
        const val SETTINGS_BUTTON = "Settings"
        const val DONE_BUTTON = "Done"
        const val SAVE_BUTTON = "Save"
        const val BACK_BUTTON = "Back"
        const val SET_REMINDER_BUTTON = "Set \nreminder"
        const val SET_NEW_REMINDER_BUTTON = "Set new \nreminder"
        const val DISABLE_ALARM_BUTTON = "Disable reminder"
        const val ACTIVATE_ALARM_BUTTON = "Activate reminder"


        const val DELETE_BUTTON = "Delete"
        const val ADD_ALARM = "Add alarm"
        
        const val RAMP_UP_SWITCH = "Ramp up alarm volume?"
        const val RESPECT_SYSTEM_SWITCH = "Respect system sound mode?"

        //Settings
        const val SETTINGS_TITLE = "Settings"
        const val CURRENT_PROFILE = "Current displayed profile"
        const val PROFILES_SETTINGS = "Add and modify profiles"
        const val PROFILES_SETTINGS_TOP = "Groups"
        const val COLORS_SETTINGS = "Colors"
        const val ALARMS_SETTINGS = "Alarms"
        const val TAP_SETTINGS = "Tap Behaviour"
        const val OTHER_SETTINGS = "Other"
        const val DARK_THEME_SETTING = "Dark Theme"
        const val HUE_SETTING = "Base Color"
        const val DEFAULT_HUE_BUTTON = "Default"
        const val PALETTE_SETTING = "Palette:"
        const val EDIT_PAST_SETTING = "Tapping only affects current day"
        const val EDIT_PAST_SUBTITLE = "Other dates can still be edited by long tapping"
        const val ROW_TAP_SETTING = "Tap on row to add to current day"
        const val ROW_TAP_SUBTITLE = "Other dates can still be edited by long tapping"
        
        const val INFO_DELETE_TASK = "Task"
        const val INFO_DELETE_SUBTASK = "SubTask"


        //Placeholders
        const val EMPTY_STRING = ""

        //Toasts
        const val TASK_ADD_FAIL = "Something went wrong, nothing was saved..."
        const val TASK_EDIT_FAIL = "Something went wrong, no changes saved..."
        const val PROFILE_ADD_FAIL = "Inputted values invalid, group not created..."
        const val PROFILE_EDIT_FAIL = "Inputted values invalid, no changes..."
        const val ALARM_ADD_FAIL = "Something went wrong, reminder not saved..."
        const val NOTIF_TYPE_ADD_FAIL = "Something went wrong, nothing was saved..."
        const val PICKED_TIME_IN_PAST = "Time must be in the future"


        //UI STRINGS
        const val LEFT_BRANCH_TEXT =" EI KIIREELLISET "
        const val RIGHT_BRANCH_TEXT = " KIIREELLISET "
        const val BOTTOM_BRANCH_TEXT = "EI TÄRKEÄT"
        const val TOP_BRANCH_TEXT = "TÄRKEÄT"



    }
}