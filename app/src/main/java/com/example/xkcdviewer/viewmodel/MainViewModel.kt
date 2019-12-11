package com.example.xkcdviewer.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.example.tidal.business.SingleLiveEvent
import com.example.xkcdviewer.Constants.FIRST_COMIC
import com.example.xkcdviewer.Constants.LAST_COMIC
import com.example.xkcdviewer.Utility.ImageHandler
import com.example.xkcdviewer.XkcdStates
import com.example.xkcdviewer.db.ComicDBHelperClass
import com.example.xkcdviewer.db.ComicModel
import com.example.xkcdviewer.servicehelper.ServiceHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * View model for main activity
 */

class MainViewModel : ViewModel() {

    lateinit var comic: ComicModel
    lateinit var comicDBHelper: ComicDBHelperClass
    var xkcdStatesLiveData = SingleLiveEvent<XkcdStates>()
    var offlineComics = ArrayList<ComicModel>()
    var isNetworkConnected = false


    //Comic Handler
    fun loadComic(comicID: Int) {
        xkcdStatesLiveData.postValue(XkcdStates.Loading)

        if(isNetworkConnected) {
            //First check comic is present in the local DB
            offlineComics.clear()
            if (isComicPresentInLocalDB(comicID.toString())) {
                comic = getComic(comicID.toString())
                xkcdStatesLiveData.postValue(XkcdStates.ComicLoadedSuccessfully)
            } else {
                GlobalScope.launch {
                    val (comicResponse, exception) = ServiceHelper.getComic(comicID).await()

                    if (comicResponse != null) {
                        comic = comicResponse
                        xkcdStatesLiveData.postValue(XkcdStates.ComicLoadedSuccessfully)
                    } else {
                        xkcdStatesLiveData.postValue(XkcdStates.ComicLoadingFailed("Error while fetching comics from server"))
                    }
                }
            }
        } else { //TODO : Work in progress for offline mode

            if (offlineComics.count() == 0)  {
                offlineComics = getAllComic()
                if(offlineComics.count() > 0) {
                    comic = offlineComics.first()
                    xkcdStatesLiveData.postValue(XkcdStates.ComicLoadedSuccessfully)
                } else {
                    xkcdStatesLiveData.postValue(XkcdStates.ComicLoadingFailed("Offline comics are not present"))
                }
            } else  {
                if(comic.num < comicID)  { //Next button pressed
                    var index = offlineComics.indexOf(comic)
                    var newComic = offlineComics.elementAtOrNull(index+1)
                    if ( newComic != null ) {
                        comic = newComic
                    } else {
                        xkcdStatesLiveData.postValue(XkcdStates.ComicLoadingFailed("Comic not present"))
                    }
                } else if (comic.num > comicID)  { //Prev button pressed
                    var index = offlineComics.indexOf(comic)
                    var newComic = offlineComics.elementAtOrNull(index-1)
                    if ( newComic != null ) {
                        comic = newComic
                    } else {
                        xkcdStatesLiveData.postValue(XkcdStates.ComicLoadingFailed("Comic not present"))
                    }
                } else {
                    xkcdStatesLiveData.postValue(XkcdStates.ComicLoadingFailed("Comic not present"))

                }
            }
        }
    }

    fun loadRandomComic() {
        loadComic((FIRST_COMIC until LAST_COMIC).random())
    }

    fun loadNextComic() {
        if (comic.getNextId().toInt() <= LAST_COMIC) {
            loadComic(comic.getNextId().toInt())
        } else {
            xkcdStatesLiveData.postValue(XkcdStates.ComicLoadingFailed("That's all comics we have right now."))
        }
    }

    fun loadPrevComic() {
        if (comic.getPrevId().toInt() >= FIRST_COMIC) {
            loadComic(comic.getPrevId().toInt())
        } else {
            xkcdStatesLiveData.postValue(XkcdStates.ComicLoadingFailed("This is the first comic."))
        }
    }

    fun loadFirstComic() {
        loadComic(FIRST_COMIC)
    }

    fun loadLastComic() {
        loadComic(LAST_COMIC)
    }

    // DB helper methods
    fun loadDataBase(context: Context) {
        comicDBHelper = ComicDBHelperClass(context)
    }

    fun saveComic(bitmapImage: Bitmap, context: Context) {
        comic.bookmark = true

        //Save imageinto internal storage
        if (ImageHandler.saveToInternalStorage(bitmapImage, comic.num.toString(), context)) {
            //Save comic metadata
            val isDataSaved = comicDBHelper.insertComic(comic)
        }
    }

    fun deleteComic() {
        comic.bookmark = false
        comicDBHelper.deleteComic(comic)
    }

    fun getAllComic(): ArrayList<ComicModel> {
        return comicDBHelper.readAllComics()
    }

    fun getComic(comicID: String): ComicModel {
        return comicDBHelper.readComics(comicID)[0]
    }

    fun isComicPresentInLocalDB(comicID: String): Boolean {

        return if (ImageHandler.loadImageFromStorage(comicID) != null) {
            comicDBHelper.isComicPresentInDataBase(comicID)
        } else {
            false
        }
    }
}
