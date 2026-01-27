package com.maxi.snapgrab.internal

import com.maxi.snapgrab.utils.getUniqueId
import kotlinx.coroutines.Job

class DownloadRequest private constructor(
    internal val url: String,
    internal val dirPath: String,
    internal val fileName: String,
    internal val requestId: Int,
    internal val tag: String?,
    internal val connectionTimeOut: Long,
    internal val readTimeOut: Long
) {

    internal var totalBytes: Long = 0L
    internal var downloadedBytes: Long = 0L

    internal var state: DownloadRequestState = DownloadRequestState.NONE
    internal lateinit var job: Job

    internal lateinit var onStart: () -> Unit
    internal lateinit var onProgress: (value: Int) -> Unit
    internal lateinit var onPause: () -> Unit
    internal lateinit var onResume: () -> Unit
    internal lateinit var onError: (error: String) -> Unit
    internal lateinit var onCancel: () -> Unit
    internal lateinit var onComplete: () -> Unit

    class Builder(
        private val url: String,
        private val dirPath: String,
        private val fileName: String
    ) {

        private var tag: String? = null
        private var connectionTimeOut: Long = 0L
        private var readTimeOut: Long = 0L

        fun tag(tag: String) = apply {
            this.tag = tag
        }

        fun connectionTimeOut(timeOut: Long) = apply {
            this.connectionTimeOut = timeOut
        }

        fun readTimeOut(timeOut: Long) = apply {
            this.readTimeOut = timeOut
        }

        fun build(): DownloadRequest {
            return DownloadRequest(
                url,
                dirPath,
                fileName,
                getUniqueId(url, dirPath, fileName),
                tag,
                connectionTimeOut,
                readTimeOut
            )
        }
    }
}