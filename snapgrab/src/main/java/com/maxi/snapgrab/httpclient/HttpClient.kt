package com.maxi.snapgrab.httpclient

import com.maxi.snapgrab.internal.DownloadRequest
import com.maxi.snapgrab.model.HttpResponse

interface HttpClient {

    fun connect(request: DownloadRequest): HttpResponse
}