package com.example.xkcdviewer.Utility

import android.content.Context
import android.graphics.Bitmap
import android.content.ContextWrapper
import android.graphics.BitmapFactory
import com.example.xkcdviewer.Constants.IMAGE_DIRECTORY_LOCATION
import java.io.*

/**
 * Image handler
 */

object ImageHandler {

    fun saveToInternalStorage(bitmapImage: Bitmap, imageName: String, context: Context): Boolean {
        val cw = ContextWrapper(context)
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        // Create imageDir
        val mypath = File(directory, "$imageName.jpg")
        var isImageSaved = false
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            isImageSaved = bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
            isImageSaved = false
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                //e.printStackTrace()
                isImageSaved = false
            }
        }
        return isImageSaved
    }

    fun loadImageFromStorage(imageName: String): Bitmap? {
        try {
            val f = File(IMAGE_DIRECTORY_LOCATION, "$imageName.jpg")
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            return b
        } catch (e: FileNotFoundException) {
            //e.printStackTrace()
            return null
        }
    }

    fun getImageUrlFromStorage(imageName: String): File {
        return File(IMAGE_DIRECTORY_LOCATION, "$imageName.jpg")
    }
}
