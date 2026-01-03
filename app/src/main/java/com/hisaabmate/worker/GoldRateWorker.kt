package com.hisaabmate.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hisaabmate.data.local.entity.GoldRateEntity
import com.hisaabmate.data.remote.GoldRateScraper
import com.hisaabmate.domain.repository.HisaabRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GoldRateWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface GoldRateWorkerEntryPoint {
        fun repository(): HisaabRepository
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            GoldRateWorkerEntryPoint::class.java
        )
        val repository = entryPoint.repository()
        val scraper = GoldRateScraper()

        try {
            val rate = scraper.fetchGoldRate()
            if (rate != null && rate > 0) {
                val entity = GoldRateEntity(
                    ratePerTola = rate,
                    ratePerGram = rate / 11.664,
                    timestamp = System.currentTimeMillis()
                )
                repository.insertGoldRate(entity)
                Log.d("GoldRateWorker", "Gold rate updated: $rate")
                Result.success()
            } else {
                Log.e("GoldRateWorker", "Failed to fetch gold rate")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e("GoldRateWorker", "Error in worker", e)
            Result.failure()
        }
    }
}
