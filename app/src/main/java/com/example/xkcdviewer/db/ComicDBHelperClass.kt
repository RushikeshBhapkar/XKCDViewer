package com.example.xkcdviewer.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import java.lang.Exception

/**
 * Sqlite database handler CRUD operations
 */

class ComicDBHelperClass(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    /**
     * Insert Comic.
     * @param comic ComicModel
     */
    fun insertComic(comic: ComicModel): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase
        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DBContract.UserEntry.COLUMN_NUM, comic.num)
        values.put(DBContract.UserEntry.COLUMN_MONTH, comic.month)
        values.put(DBContract.UserEntry.COLUMN_LINK, comic.link)
        values.put(DBContract.UserEntry.COLUMN_YEAR, comic.year)
        values.put(DBContract.UserEntry.COLUMN_NEWS, comic.news)
        values.put(DBContract.UserEntry.COLUMN_SAFE_TITLE, comic.safe_title)
        values.put(DBContract.UserEntry.COLUMN_TRANSCRIPT, comic.transcript)
        values.put(DBContract.UserEntry.COLUMN_ALT, comic.alt)
        values.put(DBContract.UserEntry.COLUMN_IMG, comic.img)
        values.put(DBContract.UserEntry.COLUMN_TITLE, comic.title)
        values.put(DBContract.UserEntry.COLUMN_DAY, comic.day)
        values.put(DBContract.UserEntry.COLUMN_BOOKMARK, "1")

        // Insert the new row, returning the primary key value of the new row
        return try {
            val newRowId = db.insert(DBContract.UserEntry.TABLE_NAME, null, values)
            true
        } catch (ex: SQLiteConstraintException) {
            false
        } catch (ex: Exception) {
            false
        }
    }

    /**
     * Delete Comic.
     * @param comic ComicModel
     */
    fun deleteComic(comic: ComicModel): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase
        // Define 'where' part of query.
        val selection = DBContract.UserEntry.COLUMN_NUM + " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(comic.num.toString())
        // Issue SQL statement.
        return try {
            db.delete(DBContract.UserEntry.TABLE_NAME, selection, selectionArgs)
            true
        } catch (ex: Exception) {
            false
        }
    }

    /**
     * Read Comics.
     * @param comicID String
     */
    fun readComics(num: String): ArrayList<ComicModel> {

        val comics = ArrayList<ComicModel>()
        val db = writableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(
                "select * from " + DBContract.UserEntry.TABLE_NAME + " WHERE " + DBContract.UserEntry.COLUMN_NUM + "='" + num + "'",
                null
            )
        } catch (e: Exception) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var num: String
        var month: String
        var link: String
        var year: String
        var news: String
        var safeTitle: String
        var transcript: String
        var alt: String
        var img: String
        var title: String
        var day: String
        var bookmark: Boolean

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                num = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_NUM))
                month = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_MONTH))
                link = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_LINK))
                year = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_YEAR))
                news = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_NEWS))
                safeTitle = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_SAFE_TITLE))
                transcript = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_TRANSCRIPT))
                alt = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_ALT))
                img = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_IMG))
                title = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_TITLE))
                day = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_DAY))
                bookmark = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_BOOKMARK)).contentEquals("1")

                //val bookmarkValue = bookmark.contentEquals("1")
                comics.add(ComicModel(num.toInt(), month, link, year, news, safeTitle, transcript, alt, img, title, day, bookmark))
                cursor.moveToNext()
            }
        }
        return comics
    }

    /**
     * Read all comics.
     * @param comicID String
     */
    fun readAllComics(): ArrayList<ComicModel> {

        val comics = ArrayList<ComicModel>()
        val db = writableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(
                "select * from " + DBContract.UserEntry.TABLE_NAME,
                null
            )
        } catch (e: Exception) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var num: String
        var month: String
        var link: String
        var year: String
        var news: String
        var safeTitle: String
        var transcript: String
        var alt: String
        var img: String
        var title: String
        var day: String
        var bookmark: Boolean

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                num = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_NUM))
                month = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_MONTH))
                link = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_LINK))
                year = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_YEAR))
                news = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_NEWS))
                safeTitle = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_SAFE_TITLE))
                transcript = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_TRANSCRIPT))
                alt = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_TRANSCRIPT))
                img = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_IMG))
                title = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_TITLE))
                day = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_DAY))
                bookmark = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_BOOKMARK)).contentEquals("1")

                comics.add(ComicModel(num.toInt(), month, link, year, news, safeTitle, transcript, alt, img, title, day, bookmark))
                cursor.moveToNext()
            }
        }
        return comics
    }

    fun isComicPresentInDataBase(comicID: String) : Boolean  {
        val comicList = readComics(comicID)
        return comicList.count() >= 1
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Comic.db"
        const val SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBContract.UserEntry.TABLE_NAME + " (" +
                    DBContract.UserEntry.COLUMN_NUM + " TEXT," +
                    DBContract.UserEntry.COLUMN_MONTH + " TEXT," +
                    DBContract.UserEntry.COLUMN_LINK + " TEXT," +
                    DBContract.UserEntry.COLUMN_YEAR + " TEXT," +
                    DBContract.UserEntry.COLUMN_NEWS + " TEXT," +
                    DBContract.UserEntry.COLUMN_SAFE_TITLE + " TEXT," +
                    DBContract.UserEntry.COLUMN_TRANSCRIPT + " TEXT," +
                    DBContract.UserEntry.COLUMN_ALT + " TEXT," +
                    DBContract.UserEntry.COLUMN_IMG + " TEXT," +
                    DBContract.UserEntry.COLUMN_TITLE + " TEXT," +
                    DBContract.UserEntry.COLUMN_BOOKMARK + " TEXT,"+
                    DBContract.UserEntry.COLUMN_DAY + " TEXT)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.UserEntry.TABLE_NAME
    }
}

