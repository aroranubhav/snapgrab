package com.maxi.snapgrab.internal

import com.maxi.snapgrab.network.HttpClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SnapGrabDispatcher(
    private val httpClient: HttpClient
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun enqueue(request: SnapGrabRequest): Int {
        val job = scope.launch {
            try {
                execute(request)
            } catch (e: CancellationException) {
                println("${e.message}")
            }
        }
        request.job = job
        return request.downloadId
    }

    private suspend fun execute(request: SnapGrabRequest) {
        SnapGrabDownloader(request, httpClient).run(
            onStart = {
                runOnUiThread {
                    request.onStart()
                }
            },
            onProgress = { progress ->
                runOnUiThread {
                    request.onProgress(progress)
                }
            },
            onPause = {
                runOnUiThread {
                    request.onPause()
                }
            },
            onResume = {
                runOnUiThread {
                    request.onResume()
                }
            },
            onCompleted = {
                runOnUiThread {
                    request.onCompleted()
                }
            },
            onError = { error ->
                runOnUiThread {
                    request.onError(error)
                }
            }
        )
    }

    private fun runOnUiThread(block: () -> Unit) {
        scope.launch {
            block()
        }
    }

    fun cancel(request: SnapGrabRequest) {
        request.job.cancel()
    }

    fun cancelAll() {
        scope.cancel() //TODO: logic needs to be updated
    }
}