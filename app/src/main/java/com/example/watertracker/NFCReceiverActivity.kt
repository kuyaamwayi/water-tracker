package com.example.watertracker

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.glance.appwidget.updateAll
import androidx.work.*
import com.example.watertracker.widget.WaterWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class NFCReceiverActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = WaterRepository(this)

        // FIX: Use lifecycleScope pattern — finish() must run on main thread
        CoroutineScope(Dispatchers.IO).launch {
            // 1. Log the bottle
            repository.addBottle()

            // 2. Refresh the widget
            WaterWidget().updateAll(this@NFCReceiverActivity)

            // 3. Schedule midnight reset (only queues if not already pending)
            scheduleMidnightReset(this@NFCReceiverActivity)

            // FIX: Switch back to main thread before calling finish()
            withContext(Dispatchers.Main) {
                finish()
            }
        }
    }
}

// FIX: Actual WorkManager reset implementation — was completely empty before
fun scheduleMidnightReset(context: Context) {
    val now = LocalDateTime.now()
    val midnight = LocalDateTime.of(now.toLocalDate().plusDays(1), LocalTime.MIDNIGHT)
    val delayMillis = java.time.Duration.between(now, midnight).toMillis()

    val resetRequest = OneTimeWorkRequestBuilder<MidnightResetWorker>()
        .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
        // UniqueWork ensures only one reset is ever queued at a time
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        "midnight_reset",
        ExistingWorkPolicy.KEEP, // Don't replace if already scheduled
        resetRequest
    )
}

// FIX: Actual Worker class — was missing entirely from the codebase
class MidnightResetWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val repository = WaterRepository(applicationContext)
            repository.performMidnightReset()
            WaterWidget().updateAll(applicationContext)

            // Schedule the NEXT midnight reset (chain it forward)
            scheduleMidnightReset(applicationContext)

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
