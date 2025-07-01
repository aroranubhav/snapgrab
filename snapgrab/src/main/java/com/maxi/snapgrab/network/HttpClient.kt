package com.maxi.snapgrab.network

import com.maxi.snapgrab.internal.SnapGrabRequest
import java.net.HttpURLConnection

interface HttpClient {

    fun connect(request: SnapGrabRequest): HttpURLConnection
}