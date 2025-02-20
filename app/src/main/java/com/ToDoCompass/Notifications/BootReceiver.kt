package com.ToDoCompass.Notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ToDoCompass.ViewModels.WorkerDependency
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    @Inject lateinit var workerDependency : WorkerDependency

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            workerDependency.startCheckWorker()
        }
    }
}
