package com.maxi.snapgrab.httpclient

import com.maxi.snapgrab.internal.DownloadRequest
import com.maxi.snapgrab.model.HttpResponse
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class DefaultHttpClient : HttpClient {

    override fun connect(request: DownloadRequest): HttpResponse {
        val url = URL(request.url)

        val connection = url.openConnection() as HttpURLConnection

        connection.apply {
            requestMethod = "GET"
            connectTimeout = request.connectionTimeOut.toInt()
            readTimeout = request.readTimeOut.toInt()
        }

        if (request.downloadedBytes > 0) {
            connection.addRequestProperty(
                "Range", "bytes=${request.downloadedBytes}-"
            )
        }

        connection.connect()

        val responseCode = connection.responseCode

        if (responseCode !in 200..299) {
            throw IOException("HTTP Error: $responseCode - ${connection.responseMessage}")
        }

        val supportsResume = responseCode == HttpURLConnection.HTTP_PARTIAL

        val totalBytes: Long
        val contentLength: Long

        if (supportsResume) {
            /**
             * HTTP/1.1 206 Partial Content
             * Content-Range: bytes 500000-999999/1000000
             * Content-Length: 500000
             *
             * bytes start-end/total
             */
            val contentRange = connection.getHeaderField("Content-Range")
            totalBytes = contentRange?.substringAfter("/")?.toLong() ?: -1L
            contentLength = connection.getHeaderField("Content-Length")?.toLong() ?: -1L
        } else {
            totalBytes = connection.contentLengthLong
            contentLength = connection.contentLengthLong
        }

        request.totalBytes = totalBytes

        return HttpResponse(
            responseCode,
            totalBytes,
            contentLength,
            connection.inputStream,
            supportsResume
        )
    }
}