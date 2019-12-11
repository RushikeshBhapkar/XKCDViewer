package com.example.xkcdviewer.servicehelper

import android.util.Log
import com.example.xkcdviewer.BuildConfig
import com.example.xkcdviewer.Constants
import com.example.xkcdviewer.db.ComicModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import com.google.gson.Gson
import timber.log.Timber

/**
 * Http service handler
 */

object ServiceHelper {

    /**
     * Get Comic.
     * @param comicId comic id
     * return @param comic object
     * return @param Exception when service failed
     */
    fun getComic(comicID: Int): Deferred<Pair<ComicModel?, XKCDException?>> = GlobalScope.async {

        var requestUrl = "${Constants.GET_COMIC_REQUEST_URL}$comicID/info.0.json"

        var urlBuilder = HttpUrl.parse(requestUrl)?.newBuilder()
        val urlString = urlBuilder?.build().toString()
        val request = Request.Builder().url(urlString)

        var comicResponse: Pair<ComicModel?, XKCDException?> = Pair(null, null)

        comicResponse = try {
            val httpClient = getHttpClient()
            val httpResponse = httpClient.newCall(request.build()).execute()

            if (httpResponse.isSuccessful) {
                val json = JSONObject(httpResponse.body()?.string())
                val comic = Gson().fromJson(json.toString(), ComicModel::class.java)
                Timber.d("$json")

                Pair(comic, null)
            } else {
                Pair(null, XKCDException.fromTemplate(XKCDException.GeneralErrors.GENERIC_ERROR, null))
            }
        } catch (ex: IOException) {
            Pair(null, XKCDException.fromTemplate(XKCDException.GeneralErrors.NETWORK_ERROR, ex))
        } catch (ex: JSONException) {
            Pair(null, XKCDException.fromTemplate(XKCDException.GeneralErrors.JSON_DESERIALIZATION_ERROR, ex))
        } catch (ex: Exception) {
            Pair(null, XKCDException.fromTemplate(XKCDException.GeneralErrors.GENERIC_ERROR, ex))
        }
        return@async comicResponse
    }

    /**
     * Get Random Comic.
     * @param comicId comic id
     * return @param comic object
     * return @param Exception when service failed
     */
    fun getRandomComic(): Deferred<Pair<ComicModel?, XKCDException?>> = GlobalScope.async {

        var requestUrl = "${Constants.GET_RANDOM_COMIC_REQUEST_URL}"

        var urlBuilder = HttpUrl.parse(requestUrl)?.newBuilder()
        val urlString = urlBuilder?.build().toString()
        val request = Request.Builder().url(urlString)

        var comicResponse: Pair<ComicModel?, XKCDException?> = Pair(null, null)

        comicResponse = try {
            val httpClient = getHttpClient()
            val httpResponse = httpClient.newCall(request.build()).execute()

            if (httpResponse.isSuccessful) {
                val json = JSONObject(httpResponse.body()?.string())
                val comic = Gson().fromJson(json.toString(), ComicModel::class.java)
                Pair(comic, null)
            } else {
                Pair(null, XKCDException.fromTemplate(XKCDException.GeneralErrors.GENERIC_ERROR, null))
            }
        } catch (ex: IOException) {
            Pair(null, XKCDException.fromTemplate(XKCDException.GeneralErrors.NETWORK_ERROR, ex))
        } catch (ex: JSONException) {
            Pair(null, XKCDException.fromTemplate(XKCDException.GeneralErrors.JSON_DESERIALIZATION_ERROR, ex))
        } catch (ex: Exception) {
            Pair(null, XKCDException.fromTemplate(XKCDException.GeneralErrors.GENERIC_ERROR, ex))
        }
        return@async comicResponse
    }

    private fun getHttpClient(): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging())
            .build()
        return okHttpClient
    }

    fun logging() = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
        Log.v("SAMPLE_", it)
    }).apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}
