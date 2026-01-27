package com.maxi.snapgrab.internal

import com.maxi.snapgrab.httpclient.HttpClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DownloadDispatcher(
    private val httpClient: HttpClient
) {
    /**
     * enqueue
     * pause,
     * resume,
     * cancel,
     */
    internal companion object {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    }

    fun enqueue(request: DownloadRequest): Int {
        val job = scope.launch {
            execute(request)
        }
        request.job = job
        return request.requestId
    }

    private suspend fun execute(request: DownloadRequest) {
        DownloadTask(
            request,
            httpClient
        ).run(
            onStart = {
                postToMain {
                    request.onStart()
                }
            },
            onProgress = { value ->
                postToMain {
                    request.onProgress(value)
                }
            },
            onPause = {
                postToMain {
                    request.onPause()
                }
            },
            onResume = {
                postToMain {
                    request.onResume()
                }
            },
            onError = { error ->
                postToMain {
                    request.onError(error)
                }
            },
            onCancel = {
                postToMain {
                    request.onCancel()
                }
            },
            onComplete = {
                postToMain {
                    request.onComplete()
                }
            }
        )
    }

    private fun postToMain(block: () -> Unit) {
        scope.launch {
            block()
        }
    }

    fun cancel(request: DownloadRequest) {
        request.job.cancel(CancellationException())
    }
}