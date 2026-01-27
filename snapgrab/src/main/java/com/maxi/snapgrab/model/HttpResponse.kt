package com.maxi.snapgrab.model

import java.io.InputStream

data class HttpResponse(
    val responseCode: Int,
    val totalBytes: Long,
    val contentLength: Long,
    val inputStream: InputStream,
    val supportsResume: Boolean
)
