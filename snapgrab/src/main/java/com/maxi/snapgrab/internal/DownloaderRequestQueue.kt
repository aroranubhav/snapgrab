package com.maxi.snapgrab.internal

class DownloaderRequestQueue(
    private val dispatcher: DownloadDispatcher
) {

    private val requestMap = mutableMapOf<Int, DownloadRequest>()

    fun enqueue(request: DownloadRequest): Int {
        requestMap[request.requestId] = request
        return dispatcher.enqueue(request)
    }

    fun pause(requestId: Int) {
        requestMap[requestId]?.let { request ->
            request.state = DownloadRequestState.PAUSED
            dispatcher.cancel(request)
        }
    }

    fun resume(requestId: Int) {
        requestMap[requestId]?.let { request ->
            dispatcher.enqueue(request)
        }
    }

    fun cancel(requestId: Int) {
        requestMap[requestId]?.let { request ->
            request.state = DownloadRequestState.CANCELED
            dispatcher.cancel(request)
            requestMap.remove(requestId)
        }
    }

    fun cancel(tag: String) {
        val requests = requestMap.values.filter {
            it.tag == tag
        }

        requests.forEach { request ->
            cancel(request.requestId)
        }
    }

    fun cancelAll() {
        requestMap.keys.forEach { requestId ->
            cancel(requestId)
        }
    }
}