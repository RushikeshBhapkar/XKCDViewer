package com.example.xkcdviewer.db

interface IComicInfo {
    fun getImageUrl(): String
    fun isBookmarked(): Boolean
    fun setBookmarked(b: Boolean)
    fun getNextId(): String
    fun getPrevId(): String
    fun getlink(): String
}
