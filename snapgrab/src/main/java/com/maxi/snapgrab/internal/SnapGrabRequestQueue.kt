package com.maxi.snapgrab.internal

class SnapGrabRequestQueue(
    private val dispatcher: SnapGrabDispatcher
) {

    private val requestsMap: HashMap<Int, SnapGrabRequest> = hashMapOf()

    fun enqueue(request: SnapGrabRequest): Int {
        requestsMap[request.downloadId] = request
        return dispatcher.enqueue(request)
    }

    fun pause(requestId: Int) {
        requestsMap[requestId]?.let { request ->
            dispatcher.cancel(request)
        }
    }

    fun cancel(requestId: Int) {
        requestsMap[requestId]?.let { request ->
            dispatcher.cancel(request)
        }
        requestsMap.remove(requestId)
    }

    fun resume(requestId: Int) {
        requestsMap[requestId]?.let { request ->
            dispatcher.enqueue(request)
        }
    }

    fun cancelViaTag(requestTag: String) {
        val requests = requestsMap.values.filter {
            it.tag == requestTag
        }

        requests.forEach {
            cancel(it.downloadId)
        }
    }

    fun cancelAll() {
        requestsMap.clear()
        dispatcher.cancelAll()
    }
}