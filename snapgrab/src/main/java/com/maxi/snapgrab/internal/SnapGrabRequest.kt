package com.maxi.snapgrab.internal

import com.maxi.snapgrab.util.getUniqueId
import kotlinx.coroutines.Job

class SnapGrabRequest private constructor(
    internal val url: String,
    internal val dirPath: String,
    internal val fileName: String,
    internal val downloadId: Int,
    internal val tag: String?,
    internal val connectionTimeout: Int,
    internal val readTimeout: Int
) {

    internal var totalBytes: Long = 0L
    internal var downloadedBytes: Long = 0L
    internal lateinit var job: Job
    internal lateinit var onStart: () -> Unit //TODO: can have total bytes passed as the first param
    internal lateinit var onProgress: (value: Int) -> Unit
    internal lateinit var onPause: () -> Unit
    internal lateinit var onResume: () -> Unit
    internal lateinit var onCompleted: () -> Unit
    internal lateinit var onError: (value: String) -> Unit

    data class Builder(
        val url: String,
        val dirName: String,
        val fileName: String
    ) {
        private var tag: String? = null
        private var connectionTimeout: Int = 0
        private var readTimeout: Int = 0

        fun tag(tag: String): Builder = apply {
            this.tag = tag
        }

        fun connectionTimeout(timeout: Int): Builder = apply {
            this.connectionTimeout = timeout
        }

        fun readTimeout(timeout: Int): Builder = apply {
            this.readTimeout = timeout
        }

        fun build(): SnapGrabRequest =
            SnapGrabRequest(
                url,
                dirName,
                fileName,
                getUniqueId(url, dirName, fileName),
                tag,
                connectionTimeout,
                readTimeout
            )
    }
}