package com.maxi.snapgrab.internal

import com.maxi.snapgrab.network.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection

class SnapGrabDownloader(
    private val request: SnapGrabRequest,
    private val httpClient: HttpClient
) {

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 8 * 1024
    }

    private lateinit var connection: HttpURLConnection

    suspend fun run(
        onStart: () -> Unit = {},
        onProgress: (value: Int) -> Unit = { _ -> },
        onPause: () -> Unit = {},
        onResume: () -> Unit = {},
        onCompleted: () -> Unit = {},
        onError: (value: String) -> Unit = { _ -> }
    ) {
        withContext(Dispatchers.IO) {
            try {
                onStart()

                connection = httpClient.connect(request)
                val responseCode = connection.responseCode

                if (responseCode !in 200..299) {
                    onError("Failed to download: HTTP $responseCode")
                    return@withContext
                }

                val contentLength = connection.getHeaderFieldLong("Content-Length", -1)
                request.totalBytes = contentLength
                if (contentLength == -1L) {
                    onError("Cannot determine file size")
                    return@withContext
                }

                val inputStream = BufferedInputStream(connection.inputStream)
                val outputStream = BufferedOutputStream(
                    FileOutputStream(
                        request.dirPath,
                        false
                    )
                )

                inputStream.use { input ->
                    outputStream.use { output ->
                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                        var bytesRead: Int
                        var totalBytesRead = 0L
                        var lastReportedProgress = 0

                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            ensureActive()
                            output.write(buffer, 0, bytesRead)
                            totalBytesRead += bytesRead

                            request.downloadedBytes = totalBytesRead

                            val progress = ((totalBytesRead * 100) / contentLength).toInt()
                            if (progress != lastReportedProgress) {
                                lastReportedProgress = progress
                                onProgress(progress)
                            }
                        }
                        output.flush()
                    }
                }
                onCompleted()
            } catch (e: Exception) {
                onError(e.message.toString())
            } finally {
                if (::connection.isInitialized) {
                    connection.disconnect()
                }
            }
        }
    }
}