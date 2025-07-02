package com.maxi.snapgrab

import com.maxi.snapgrab.internal.SnapGrabDispatcher
import com.maxi.snapgrab.internal.SnapGrabRequest
import com.maxi.snapgrab.internal.SnapGrabRequestQueue

class SnapGrab private constructor(
    private val config: SnapGrabDefaultConfig
) {

    companion object {
        fun create(config: SnapGrabDefaultConfig = SnapGrabDefaultConfig()): SnapGrab {
            return SnapGrab(config = config)
        }
    }

    fun newRequestBuilder(
        url: String,
        dirName: String,
        fileName: String
    ): SnapGrabRequest.Builder =
        SnapGrabRequest
            .Builder(
                url,
                dirName,
                fileName
            )
            .connectionTimeout(config.connectionTimeOut)
            .readTimeout(config.readTimeOut)

    private val requestsQueue = SnapGrabRequestQueue(SnapGrabDispatcher(config.httpClient))

    fun enqueue(
        request: SnapGrabRequest,
        onStart: () -> Unit = {},
        onProgress: (value: Int) -> Unit = { _ -> },
        onPause: () -> Unit = {},
        onResume: () -> Unit = {},
        onCompleted: () -> Unit = {},
        onError: (value: String) -> Unit = { _ -> }
    ): Int {
        request.onStart = onStart
        request.onProgress = onProgress
        request.onPause = onPause
        request.onResume = onResume
        request.onCompleted = onCompleted
        request.onError = onError
        return requestsQueue.enqueue(request)
    }

    fun pause(requestId: Int) {
        requestsQueue.pause(requestId)
    }

    fun resume(requestId: Int) {
        requestsQueue.resume(requestId)
    }

    fun cancel(requestId: Int) {
        requestsQueue.cancel(requestId)
    }

    fun cancelViaTag(requestTag: String) {
        requestsQueue.cancelViaTag(requestTag)
    }

    fun cancelAll() {
        requestsQueue.cancelAll()
    }
}