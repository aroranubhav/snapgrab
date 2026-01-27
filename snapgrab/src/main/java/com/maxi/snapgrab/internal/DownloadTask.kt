package com.maxi.snapgrab.internal

import com.maxi.snapgrab.httpclient.HttpClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class DownloadTask(
    private val request: DownloadRequest,
    private val httpClient: HttpClient
) {

    suspend fun run(
        onStart: () -> Unit = {},
        onProgress: (value: Int) -> Unit = { _ -> },
        onPause: () -> Unit = {},
        onResume: () -> Unit = {},
        onError: (error: String) -> Unit = { _ -> },
        onCancel: () -> Unit = {},
        onComplete: () -> Unit = {}
    ) {
        val prevState = request.state
        withContext(Dispatchers.IO) {
            try {
                onStart()
                request.state = DownloadRequestState.IN_PROGRESS
                val response = httpClient.connect(request)

                val dir = File(request.dirPath)
                if (!dir.exists()) {
                    dir.mkdirs()
                }

                val file = File(dir, request.fileName)

                if (request.downloadedBytes > 0) {
                    if (!response.supportsResume) {
                        request.downloadedBytes = 0
                        if (file.exists()) {
                            file.delete()
                        }
                    } else if (prevState == DownloadRequestState.PAUSED) {
                        onResume()
                    }
                }

                //request.totalBytes = response.totalBytes
                val outputStream = FileOutputStream(
                    file,
                    request.downloadedBytes > 0 && response.supportsResume
                )

                response.inputStream.use { input ->
                    outputStream.use { output ->
                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                        var bytesRead: Int

                        while (true) {
                            ensureActive()

                            bytesRead = input.read(buffer)
                            if (bytesRead == -1) break

                            outputStream.write(buffer, 0, bytesRead)
                            request.downloadedBytes += bytesRead

                            val progress = if (request.totalBytes > 0) {
                                ((request.downloadedBytes * 100) / request.totalBytes).toInt()
                            } else {
                                0
                            }
                            onProgress(progress)
                        }
                    }
                }
                onComplete()
            } catch (ce: CancellationException) {
                when (request.state) {
                    DownloadRequestState.PAUSED -> {
                        onPause()
                    }

                    DownloadRequestState.CANCELED -> {
                        onCancel()
                    }

                    else -> Unit
                }
            } catch (e: Exception) {
                onError(e.message ?: "Request failed with an unknown error!")
            }
        }
    }
}