package com.lordapps.wallpaper.utils

sealed class RawImageResult {
    data class Success(val image: Int) : RawImageResult()
    data class Error(val message: String) : RawImageResult()
}