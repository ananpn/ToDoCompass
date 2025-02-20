package com.ToDoCompass.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ToDoCompass.LogicAndData.StringConstants.Companion.PROFILE_TABLE

@Database(entities = [Task::class, TaskNote::class, TaskProfile::class, TaskAlarm::class, NotifType::class, DefaultNotifType::class],
    version = 10,
    exportSchema = false
    /*
    autoMigrations = [
        AutoMigration (from = 13, to = 14)
    ]
    */
)
@TypeConverters(AlarmTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    companion object {
       // val MIGRATION_12_13 = migration_12_13
    //val MIGRATION_13_14 = migration_13_14
    }
}

/*
val migration_12_13 = object : Migration(12, 13) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Perform the necessary database migration operations here
        // For example, you can add a new column to an existing table
        database.execSQL("ALTER TABLE $PROFILE_TABLE ADD COLUMN defaultClickStep INTEGER NOT NULL DEFAULT 1")
    }
}



val migration_13_14 = object : Migration(13, 14) {
    override fun migrate(database: SupportSQLiteDatabase) {
    }
}
*/

class AlarmTypeConverter {
    @TypeConverter
    fun fromEnum(value: AlarmType): String {
        return value.name
    }
    
    @TypeConverter
    fun toEnum(value: String): AlarmType {
        return enumValueOf(value)
    }
}





    /*companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "task_database")
                    .fallbackToDestructiveMigration()
                    //.addCallback(AppDatabaseCallback(scope = CoroutineScope(Dispatchers.IO)))
                    .build()
                    .also { Instance = it }
            }
        }*/

/*

            private class AppDatabaseCallback(
                private val scope: CoroutineScope
            ) : RoomDatabase.Callback() {
                */
/**
                 * Override the onCreate method to populate the database.
                 *//*

                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // If you want to keep the data through app restarts,
                    // comment out the following line.
                    Instance?.let { database ->
                        scope.launch(Dispatchers.IO) {
                            populateDatabase(database.taskDao())
                        }
                    }
                }
            }

            suspend fun populateDatabase(taskDao: TaskDao) {
                // Start the app with a clean database every time.
                // Not needed if you only populate on creation.
                taskDao.deleteAllTasks()
                taskDao.deleteAllTaskNotes()
            }
        }
*/

        /**
         * Populate the database in a new coroutine.
         * If you want to start with more words, just add them.
         */






/*
object DatabaseProvider {
    private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            val database = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            ).build()
            instance = database
            database
        }
    }
}*/


/*

@Database(entities = [Task::class, TaskNote::class, TaskGroup::class],
            version = 12,
    exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
*/

