package com.example.xkcdviewer.db

import android.provider.BaseColumns


object DBContract {

    /* Inner class that defines the table contents */
    class UserEntry : BaseColumns {
        companion object {
            const val TABLE_NAME = "comic"
            const val COLUMN_NUM = "num"
            const val COLUMN_MONTH = "month"
            const val COLUMN_LINK = "link"
            const val COLUMN_YEAR = "year"
            const val COLUMN_NEWS = "news"
            const val COLUMN_SAFE_TITLE = "safeTitle"
            const val COLUMN_TRANSCRIPT = "transcript"
            const val COLUMN_ALT = "alt"
            const val COLUMN_IMG = "img"
            const val COLUMN_TITLE = "title"
            const val COLUMN_DAY = "day"
            const val COLUMN_BOOKMARK = "bookmark"
        }
    }
}
