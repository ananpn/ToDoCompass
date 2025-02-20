package com.ToDoCompass.Notifications

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ToDoCompass.ViewModels.WorkerDependency
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    workerDependency: WorkerDependency
): Worker(context, workerParams) {
    val workerDependency = workerDependency
    override fun doWork(): Result {
        //Log.v("CheckWorker", "doWork start")
        workerDependency.checkWorkerWork()
        return Result.success()
    }
}