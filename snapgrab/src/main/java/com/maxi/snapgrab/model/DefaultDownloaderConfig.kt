package com.maxi.snapgrab.model

import com.maxi.snapgrab.httpclient.DefaultHttpClient
import com.maxi.snapgrab.httpclient.HttpClient
import com.maxi.snapgrab.utils.Constants.CONNECTION_TIME_OUT
import com.maxi.snapgrab.utils.Constants.READ_TIME_OUT

data class DefaultDownloaderConfig(
    val client: HttpClient = DefaultHttpClient(),
    val connectionTimeOut: Long = CONNECTION_TIME_OUT,
    val readTimeOut: Long = READ_TIME_OUT
)
