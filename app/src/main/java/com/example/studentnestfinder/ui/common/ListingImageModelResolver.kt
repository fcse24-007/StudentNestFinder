package com.example.studentnestfinder.ui.common

import android.content.Context
import com.example.studentnestfinder.R

fun resolveListingImageModel(context: Context, imagePath: String?): Any {
    if (imagePath.isNullOrBlank()) return R.drawable.ic_property_placeholder
    if (
        imagePath.startsWith("content://") ||
        imagePath.startsWith("file://") ||
        imagePath.startsWith("https://")
    ) {
        return imagePath
    }
    val drawableResId = context.resources.getIdentifier(imagePath, "drawable", context.packageName)
    return if (drawableResId != 0) drawableResId else R.drawable.ic_property_placeholder
}
