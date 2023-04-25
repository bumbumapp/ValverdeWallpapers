package com.lordapps.wallpaper.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import com.lordapps.wallpaper.R
import kotlin.collections.ArrayList
var PREMIUM_SHOWED=false

fun intToBitmap(image: Int, context: Context): Bitmap {
    val inputStream = context.applicationContext.resources.openRawResource(image)
    return BitmapFactory.decodeStream(inputStream)


}

fun rotateImage(imageView: ImageView): Bitmap {
    val bitmap = (imageView.drawable as BitmapDrawable).bitmap
    val matrix = Matrix()
    matrix.postRotate(90f)

    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}


fun effectsResults(): ArrayList<Int> {
    return arrayListOf(
        R.color.effect1,
        R.color.effect2,
        R.color.effect3,
        R.color.effect4,
        R.color.effect5,
        R.color.effect6,
        R.color.effect7,
        R.color.effect8,
        R.color.effect9,
        R.color.effect10,
        R.color.effect11,
        R.color.effect12,
        R.color.effect13,
        R.color.effect14,
        R.color.effect15,
        R.color.effect16,
        R.color.effect17,
        R.color.effect18,
        R.color.effect19,
        R.color.effect20
    )
}