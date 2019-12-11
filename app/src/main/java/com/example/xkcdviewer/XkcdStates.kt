package com.example.xkcdviewer

sealed class XkcdStates {
    object Loading : XkcdStates()
    object ComicLoadedSuccessfully : XkcdStates()
    data class ComicLoadingFailed(val errorMessage : String) : XkcdStates()
}
