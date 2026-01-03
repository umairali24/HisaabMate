package com.hisaabmate

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.hisaabmate.worker.GoldRateWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class HisaabMateApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupGoldRateWorker()
    }

    private fun setupGoldRateWorker() {
        val workRequest = PeriodicWorkRequestBuilder<GoldRateWorker>(12, TimeUnit.HOURS)
            .build()
        
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "GoldRateWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
