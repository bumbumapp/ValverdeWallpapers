package com.lordapps.wallpaper.utils

sealed class RawImagesResult {
    data class Success(val images: List<Int>) : RawImagesResult()
    data class Error(val message: String) : RawImagesResult()
}