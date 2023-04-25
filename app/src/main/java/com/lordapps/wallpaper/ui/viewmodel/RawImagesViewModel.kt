package com.lordapps.wallpaper.ui.viewmodel

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lordapps.wallpaper.R
import com.lordapps.wallpaper.utils.RawImageResult
import com.lordapps.wallpaper.utils.RawImagesResult
import kotlinx.coroutines.launch

class RawImagesViewModel : ViewModel() {
    var rawId=0
    var bitmap:Bitmap?=null
    var bitmapWallpaper:Bitmap?=null
    private val _rawImagesLiveData = MutableLiveData<RawImagesResult>()
    val rawImagesLiveData: LiveData<RawImagesResult>
        get() = _rawImagesLiveData
    private val _rawImageLiveData = MutableLiveData<RawImageResult>()
    val rawImageLiveData: LiveData<RawImageResult>
        get() = _rawImageLiveData
    private val _setWallpaperSuccess = MutableLiveData<Boolean>()
    val setWallpaperSuccess: LiveData<Boolean> = _setWallpaperSuccess

    fun setWallpaper(context: Context,image: Bitmap) {
       val wallpaperManager = WallpaperManager.getInstance(context.applicationContext)

        viewModelScope.launch {
            try {
                wallpaperManager.setBitmap(image)
                _setWallpaperSuccess.postValue(true)
            } catch (e: Exception) {
                _setWallpaperSuccess.postValue(false)
            }
        }
    }

    fun loadRawImages() {
        _rawImagesLiveData.value = RawImagesResult.Success(getImagesFromRaw())
    }
     fun loadRawImage() {
        _rawImageLiveData.value = RawImageResult.Success(getImageFromRaw())
    }

   private fun getImagesFromRaw(): List<Int> {
        val fields = R.raw::class.java.fields
        val list = mutableListOf<Int>()
        for (i in fields.indices) {
            try {
                list.add(fields[i].getInt(null))
            } catch (e: IllegalAccessException) {
                _rawImagesLiveData.value =
                    RawImagesResult.Error("Failed to access resource ID: ${fields[i].name}")
            }
        }
        return list
    }
   fun getImageFromRaw(): Int {
        var item:Int=0
        try {
            item=getImagesFromRaw()[rawId]
            } catch (e: IllegalAccessException) {
                _rawImageLiveData.value =
                    RawImageResult.Error("Failed to access resource ID: ${getImagesFromRaw()[rawId]}")
            }

        return item
    }

    fun setLockScreenWallpaper(context: Context, bitmap: Bitmap) {
        val wallpaperManager = WallpaperManager.getInstance(context)
        viewModelScope.launch {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
                }
                _setWallpaperSuccess.postValue(true)
            } catch (e: Exception) {
                _setWallpaperSuccess.postValue(false)
            }
        }
    }

}