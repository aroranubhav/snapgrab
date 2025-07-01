package com.maxi.snapgrab.network

import com.maxi.snapgrab.internal.SnapGrabRequest
import com.maxi.snapgrab.util.Constants
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

class DefaultHttpClient : HttpClient {

    override fun connect(request: SnapGrabRequest): HttpURLConnection {
        val connection: HttpURLConnection = URL(request.url).openConnection()
                as HttpURLConnection

        val range: String = String.format(
            Locale.ENGLISH, "bytes=%d-", request.downloadedBytes
        )

        return connection.apply {
            connectTimeout = request.connectionTimeout
            readTimeout = request.readTimeout
            requestMethod = "GET"
            addRequestProperty(
                Constants.RANGE,
                range
            )
        }
    }
}