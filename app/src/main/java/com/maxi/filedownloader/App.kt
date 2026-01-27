package com.maxi.filedownloader

import android.app.Application
import com.maxi.snapgrab.Downloader

class App: Application() {

    lateinit var downloader: Downloader

    override fun onCreate() {
        super.onCreate()
        downloader = Downloader.create()
    }
}