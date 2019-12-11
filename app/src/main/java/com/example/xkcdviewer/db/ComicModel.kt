package com.example.xkcdviewer.db

/**
 * Comic data model
 */

class ComicModel(val num: Int,
                 val month: String,
                 val link: String,
                 val year: String,
                 val news: String,
                 val safe_title: String,
                 val transcript: String,
                 val alt: String,
                 val img: String,
                 val title: String,
                 val day: String,
                 var bookmark:Boolean): IComicInfo {

    override fun getImageUrl(): String {
        return link
    }

    override fun getNextId(): String {
        var n = num + 1
        // #404 is xkcd's error page!
        if (n == 404) ++n
        return Integer.toString(n)
    }

    override fun getPrevId(): String {
        var n = num - 1
        // #404 is xkcd's error page!
        if (n == 404) --n
        return Integer.toString(n)
    }

    override fun isBookmarked(): Boolean {
        return bookmark
    }

    override fun setBookmarked(b: Boolean) {
        bookmark = b
    }

    override fun getlink(): String {
        return "http://xkcd.com/$num/"
    }
}

