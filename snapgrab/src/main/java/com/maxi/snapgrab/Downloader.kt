package com.maxi.snapgrab

import com.maxi.snapgrab.internal.DownloadDispatcher
import com.maxi.snapgrab.internal.DownloadRequest
import com.maxi.snapgrab.internal.DownloaderRequestQueue
import com.maxi.snapgrab.model.DefaultDownloaderConfig

class Downloader private constructor(
    private val config: DefaultDownloaderConfig
) {

    companion object {
        fun create(config: DefaultDownloaderConfig = DefaultDownloaderConfig()): Downloader {
            return Downloader(config)
        }
    }

    private val requestQueue = DownloaderRequestQueue(DownloadDispatcher(config.client))

    fun requestBuilder(
        url: String,
        dirPath: String,
        fileName: String
    ): DownloadRequest.Builder {
        return DownloadRequest.Builder(url, dirPath, fileName)
            .connectionTimeOut(config.connectionTimeOut)
            .readTimeOut(config.readTimeOut)
    }

    fun enqueue(
        request: DownloadRequest,
        onStart: () -> Unit = {},
        onProgress: (value: Int) -> Unit = { _ -> },
        onPause: () -> Unit = {},
        onResume: () -> Unit = {},
        onCancel: () -> Unit = {},
        onError: (error: String) -> Unit = { _ -> },
        onComplete: () -> Unit = {}
    ): Int {
        request.onStart = onStart
        request.onProgress = onProgress
        request.onPause = onPause
        request.onResume = onResume
        request.onCancel = onCancel
        request.onError = onError
        request.onComplete = onComplete
        return requestQueue.enqueue(request)
    }

    fun pause(requestId: Int) {
        requestQueue.pause(requestId)
    }

    fun resume(requestId: Int) {
        requestQueue.resume(requestId)
    }

    fun cancel(requestId: Int) {
        requestQueue.cancel(requestId)
    }

    fun cancel(requestTag: String) {
        requestQueue.cancel(requestTag)
    }

    fun cancelAll() {
        requestQueue.cancelAll()
    }

}