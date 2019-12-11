package com.example.xkcdviewer.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.xkcdviewer.db.ComicDBHelperClass

/**
 * ViewModel for lost view activity
 */

class ListViewModel : ViewModel() {

    lateinit var comicDBHelper: ComicDBHelperClass
    // DB helper methods
    fun loadDataBase(context: Context) {
        comicDBHelper = ComicDBHelperClass(context)
    }
}
