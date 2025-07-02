package com.maxi.snapgrab

import com.maxi.snapgrab.network.DefaultHttpClient
import com.maxi.snapgrab.network.HttpClient
import com.maxi.snapgrab.util.Constants.DEFAULT_CONNECTION_TIMEOUT_MILLS
import com.maxi.snapgrab.util.Constants.DEFAULT_READ_TIMEOUT_MILLS

data class SnapGrabDefaultConfig(
    val httpClient: HttpClient = DefaultHttpClient(),
    val connectionTimeOut: Int = DEFAULT_CONNECTION_TIMEOUT_MILLS,
    val readTimeOut: Int = DEFAULT_READ_TIMEOUT_MILLS
)
