package com.maxi.snapgrab.internal

enum class DownloadRequestState {
    NONE,
    IN_PROGRESS,
    PAUSED,
    CANCELED,
    COMPLETED,
    FAILED
}