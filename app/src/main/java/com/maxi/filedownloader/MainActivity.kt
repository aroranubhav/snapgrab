package com.maxi.filedownloader

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.maxi.filedownloader.databinding.ActivityMainBinding
import com.maxi.snapgrab.Downloader
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var downloader: Downloader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getDirectory()
    }

    private fun getDirectory() {
        // 1️⃣ Get app-specific external Download directory
        val downloadDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)

        if (downloadDir == null) {
            Toast.makeText(
                this, "External storage not available",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // 2️⃣ Optional: create subfolder for your app
        val appFolder = File(downloadDir, "File Downloads")
        if (!appFolder.exists()) {
            appFolder.mkdirs()
        }

        // 3️⃣ Values your library expects
        val fileUrl = "https://www.pexels.com/download/video/35535064/"
        val dirPath = appFolder.absolutePath
        val fileName = "pexels.mp4"
        binding.btnDownload.setOnClickListener {
            Log.d(TAG, "Download clicked!")
            setupDownloader(fileUrl, dirPath, fileName)
        }
    }

    private fun setupDownloader(fileUrl: String, dirPath: String, fileName: String) {
        downloader = (application as App).downloader

        val request = downloader
            .requestBuilder(
                fileUrl,
                dirPath,
                fileName
            )
            .tag("Danke")
            .build()

        val requestId = downloader.enqueue(
            request,
            onStart = {
                Log.d(TAG, "Download Started!")
            },
            onProgress = { progress ->
                Log.d(TAG, "Download Progress - $progress%")
            },
            onPause = {
                Log.d(TAG, "Download Paused!")
            },
            onResume = {
                Log.d(TAG, "Download Resumed!")
            },
            onCancel = {
                Log.d(TAG, "Download Canceled!")
            },
            onError = { error ->
                Log.d(TAG, "Download Error - $error!")
            },
            onComplete = {
                Log.d(TAG, "Download Complete!")
            }
        )
        Log.d(TAG, "setupDownloader: $requestId")

        binding.apply {
            if (::downloader.isInitialized) {
                btnPause.setOnClickListener {
                    downloader.pause(requestId)
                }
                btnResume.setOnClickListener {
                    downloader.resume(requestId)
                }
                btnCancel.setOnClickListener {
                    downloader.cancel(requestId)
                }
            }
        }
    }
}

private const val TAG = "SnapGrabMainActivity"