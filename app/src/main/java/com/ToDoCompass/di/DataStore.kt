package com.ToDoCompass.di

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ToDoCompass.LogicAndData.Constants.Companion.defaultCustomVibrationPattern
import com.ToDoCompass.LogicAndData.Constants.Companion.defaultDarkTheme
import com.ToDoCompass.LogicAndData.Constants.Companion.defaultDispProfileId
import com.ToDoCompass.LogicAndData.Constants.Companion.defaultPaletteData
import com.ToDoCompass.LogicAndData.Constants.Companion.defaultSeedColorData
import com.ToDoCompass.LogicAndData.toStringForStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

data class AppSettingsData(
    val darkTheme : Boolean,
    val seedColorData : Float,
    val dispProfileId : Int,
    val paletteData : Int,
)

/*data class NotificationSettings(
    val soundUri: Uri?,
    val vibrationPattern: LongArray?,
    // Add any other settings you need
)*/





val Context.ToDoCompassPrefs by preferencesDataStore("ToDoCompassPrefs")

class WeekListPrefs(context: Context) : PrefsImpl {

    private val dataStore = context.ToDoCompassPrefs

    // used to get the data from datastore
    override val darkTheme: Flow<Boolean>
        get() = dataStore.data.map { preferences ->
            val darkTheme = preferences[DARK_KEY] ?: false
            darkTheme
        }

    // used to save the ui preference to datastore
    override suspend fun saveDarkTheme(isNightMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_KEY] = isNightMode
        }
    }



    /*override fun readSeedColorFloat(): Flow<Float> =
        dataStore.data.map { preferences ->
            val colorFloat = preferences[SEED_COLOR_KEY] ?:0f
            colorFloat
        }*/

    override val dispProfileId: Flow<Int>
        get() = dataStore.data.map { preferences ->
            val dispProfileId = preferences[PROFILE_KEY] ?:1
            dispProfileId
        }

    override suspend fun saveDispProfileId(dispProfileId: Int) {
        dataStore.edit { preferences ->
            preferences[PROFILE_KEY] = dispProfileId
        }
    }

    override val seedColorData: Flow<Float>
        get() = dataStore.data.map { preferences ->
            val seedColorData = preferences[SEED_COLOR_KEY] ?: 180f
            seedColorData
        }


    override suspend fun saveSeedColorData(newSeedColorData: Float) {
        dataStore.edit { preferences ->
            preferences[SEED_COLOR_KEY] = newSeedColorData
        }
    }

    override val paletteData: Flow<Int>
        get() = dataStore.data.map { preferences ->
            val paletteData = preferences[PALETTE_KEY] ?: 0
            paletteData
        }


    override suspend fun savePaletteData(newPaletteData: Int) {
        dataStore.edit { preferences ->
            preferences[PALETTE_KEY] = newPaletteData
        }
    }
    
    override suspend fun saveCustomVibPattern(new: String) {
        dataStore.edit { preferences ->
            preferences[CUSTOM_VIB_PATTERN_KEY] = new
        }
    }
    

    override val appSettingsDataFlow: Flow<AppSettingsData> = dataStore.data
        .catch { exception ->
            throw exception
            /*
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
            */
        }.map { preferences ->
            val darkTheme = preferences[DARK_KEY] ?: defaultDarkTheme
            val seedColorData = preferences[SEED_COLOR_KEY] ?: defaultSeedColorData
            val dispProfileId = preferences[PROFILE_KEY] ?: defaultDispProfileId
            val paletteData = preferences[PALETTE_KEY] ?: defaultPaletteData

            AppSettingsData(
                darkTheme,
                seedColorData,
                dispProfileId,
                paletteData,
            )
        }
    
    override val customVibPattern: Flow<String> = dataStore.data
        .catch { exception ->
            throw exception
            /*
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
            */
        }.map {preferences ->
            val customVibPattern = preferences[CUSTOM_VIB_PATTERN_KEY]
                ?:defaultCustomVibrationPattern.toStringForStore().also{
                    Log.v("pref customVibPattern", "fail")
                }
            customVibPattern
        }

    companion object {
        private val DARK_KEY = booleanPreferencesKey("dark_theme")
        private val SEED_COLOR_KEY = floatPreferencesKey("seed_color")
        private val PROFILE_KEY = intPreferencesKey("dispProfileId")
        private val PALETTE_KEY = intPreferencesKey("palette")
        private val NOTIFICATION_0_KEY = stringPreferencesKey("notification_0")
        private val NOTIFICATION_1_KEY = stringPreferencesKey("notification_1")
        private val NOTIFICATION_2_KEY = stringPreferencesKey("notification_2")
        private val CUSTOM_VIB_PATTERN_KEY = stringPreferencesKey("custom_vib_pattern")
        
        
        //private val EDIT_PAST_KEY = booleanPreferencesKey("past_edit")
        //private val ROW_TAP_MODE_KEY = booleanPreferencesKey("row_tap_mode")
    }



}

@Singleton
interface PrefsImpl {

    val darkTheme: Flow<Boolean>

    suspend fun saveDarkTheme(isNightMode: Boolean)

    val seedColorData: Flow<Float>

    suspend fun saveSeedColorData(newSeedColorData : Float)

    val dispProfileId : Flow<Int>

    suspend fun saveDispProfileId(dispProfileId : Int)

    val paletteData : Flow<Int>

    suspend fun savePaletteData(paletteData : Int)
    
    //suspend fun saveNotificationSound(soundUri: Uri, type : AlarmType)
    
    suspend fun saveCustomVibPattern(new : String)

    val customVibPattern : Flow<String>
    
    val appSettingsDataFlow : Flow<AppSettingsData>

    /*
    val rowTapMode : Flow<Boolean>

    suspend fun saveRowTapMode(rowTapMode : Boolean)
    */
}



/*
@Singleton
class DataStoreManager(private val dataStore: DataStore<Preferences>) {

    suspend fun writeIntegerValue(value: Int) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.INTEGER_KEY] = value
        }
    }

    suspend fun writeStringValue(value: String) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.STRING_KEY] = value
        }
    }

    val integerValueFlow: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[DataStoreKeys.INTEGER_KEY] ?: 0
        }
}

object DataStoreKeys {
    val INTEGER_KEY = intPreferencesKey("integer_key")
    val STRING_KEY = stringPreferencesKey("string_key")
}
*/

