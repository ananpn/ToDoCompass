package com.ToDoCompass.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.ToDoCompass.Notifications.AppNotificationInfa
import com.ToDoCompass.Notifications.AppNotificationManager
import com.ToDoCompass.Notifications.SoundAndVibrationPlayer
import com.ToDoCompass.Permissions.PermissionHelper
import com.ToDoCompass.ViewModels.WorkerDependency
import com.ToDoCompass.database.AppDatabase
import com.ToDoCompass.database.AppRepository
import com.ToDoCompass.database.OfflineRepository
import com.ToDoCompass.database.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    
    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context : Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideWorkerDependency(
        repo : AppRepository,
        notifMan : AppNotificationInfa,
        //prefs: PrefsImpl,
        player : SoundAndVibrationPlayer,
        @ApplicationContext context : Context
    ): WorkerDependency {
        return WorkerDependency(
            repo,
            notifMan,
            player,
            context,
        )
    }

    @Provides
    fun provideAppNotificationManager(@ApplicationContext context: Context): AppNotificationInfa {
        return AppNotificationManager(context)
    }

    @Provides
    @Singleton
    fun provideAppDb(
        @ApplicationContext
        context: Context
    ) = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "todo_compass_db"
    )
        .fallbackToDestructiveMigration()
        //.addMigrations(AppDatabase.MIGRATION_12_13)
        .build()

    @Provides
    fun provideAppDao(
        taskDatabase: AppDatabase
    ) = taskDatabase.taskDao()
    
    @Singleton
    @Provides
    fun provideAppRepository(
        taskDao: TaskDao
    ): AppRepository = OfflineRepository(
        taskDao = taskDao
    )

    @Singleton
    @Provides
    fun providePreferenceManager(@ApplicationContext context: Context): PrefsImpl {
        return WeekListPrefs(context)
    }
    
    @Singleton
    @Provides
    fun provideSoundAndVibrationPlayer(@ApplicationContext context: Context): SoundAndVibrationPlayer {
        return SoundAndVibrationPlayer(context)
    }
    
    @Singleton
    @Provides
    fun providePermissionHelper(@ApplicationContext context: Context) : PermissionHelper {
        return PermissionHelper(context)
    }
    /*
    @Singleton
    @Provides
    fun provideIntentsActivity(@ApplicationContext context: Context): IntentsActivity {
        return IntentsActivity(context)
    }
    */
}