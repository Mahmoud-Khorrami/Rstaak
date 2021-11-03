package com.example.rstaak.general

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ResizeDrawable @AssistedInject constructor(
    @Assisted var context: Context,
    @Assisted("id") var id: Int,
    @Assisted("width") var width: Int,
    @Assisted("height") var height:Int)
{
    @AssistedFactory
    interface Factory
    {
        fun create(context: Context?,
            @Assisted("id") id: Int,
            @Assisted("width") width: Int,
            @Assisted("height") height: Int): ResizeDrawable?
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun resize(): Drawable
    {
        val drawable = context.resources.getDrawable(id)
        val bitmap = (drawable as BitmapDrawable).bitmap
        return BitmapDrawable(context.resources, Bitmap.createScaledBitmap(bitmap, width, height, true))
    }
}