package com.ToDoCompass.di

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class HiltApplication : Application(), Configuration.Provider {
    
    
    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory
    
    override val workManagerConfiguration
        get () = Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .build()
    
    
}

/*
{

    */
/**
     * AppContainer instance used by the rest of classes to obtain dependencies
     *//*

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
*/
