package com.example.xkcdviewer.view

import android.content.Context
import android.speech.tts.TextToSpeech
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.xkcdviewer.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.View
import com.bumptech.glide.request.target.CustomTarget
import com.example.xkcdviewer.XkcdStates
import com.bumptech.glide.request.transition.Transition
import com.example.xkcdviewer.Utility.ImageHandler
import androidx.core.graphics.drawable.toBitmap
import com.example.xkcdviewer.R
import androidx.core.content.FileProvider
import com.example.xkcdviewer.BuildConfig
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class MainActivity : BaseActivity(), TextToSpeech.OnInitListener  {

    companion object {
        private const val ACTIVITY_REQUEST_CODE = 5000
    }

    private lateinit var viewModel: MainViewModel
    private val permissions = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE)
    private val SHARE_STORAGE_PERMS_REQUEST_CODE: Int = 900
    private var tts: TextToSpeech? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.isNetworkConnected = isNetworkConnected(this)
        viewModel.loadDataBase(this)
        viewModel.loadRandomComic()

        registerAndListenCallbacks()
        tts = TextToSpeech(this, this)

        btnBookmark.setOnClickListener { view ->
            toogleBookmark()
        }

        btnFirst.setOnClickListener {
            viewModel.loadFirstComic()
        }

        btnLast.setOnClickListener {
            viewModel.loadLastComic()
        }

        btnNext.setOnClickListener {
            viewModel.loadNextComic()
        }

        btnPrevious.setOnClickListener {
            viewModel.loadPrevComic()
        }

        etComic.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideSoftKeyboard()
                var comicId = etComic.text.toString()
                viewModel.isNetworkConnected = isNetworkConnected(this)
                viewModel.loadComic(comicId.toInt())
            }
            return@setOnEditorActionListener true
        }

        btnTextToSpeech.setOnClickListener {

            var ttsMessage = "No audio transcript available for this comic"
            if(viewModel.comic.transcript.isNotEmpty() )  {
                ttsMessage = viewModel.comic.transcript
            } else {
                ttsMessage = viewModel.comic.alt
            }
            speakOut(ttsMessage)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //stop TTS
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
    }

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {
            // set UK English as language for tts
            val result = tts!!.setLanguage(Locale.ENGLISH)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
                tts!!.language = Locale.getDefault()
            } else {
                btnTextToSpeech.isEnabled = true
            }
        } else {
            Log.e("TTS", "Initialization Failed!")
            btnTextToSpeech.isEnabled = false

        }

    }

    private fun speakOut(message : String) {

        if (tts!!.isSpeaking) {
            tts!!.stop()
            btnTextToSpeech.setImageResource(android.R.drawable.ic_lock_silent_mode_off)
        } else {
            btnTextToSpeech.setImageResource(android.R.drawable.ic_lock_silent_mode)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts!!.setSpeechRate(0.9f)
                tts!!.speak(message, TextToSpeech.QUEUE_FLUSH, null, "")
            } else {
                @Suppress("DEPRECATION")
                tts!!.speak(message, TextToSpeech.QUEUE_FLUSH, null)
            }
        }
    }

    private fun toogleBookmark() {
        if (viewModel.comic != null) {
            if (viewModel.comic.bookmark) {
                viewModel.deleteComic()
            } else {
                viewModel.saveComic(
                    ivComicViewer.drawable.toBitmap(
                        ivComicViewer.width, ivComicViewer.height, Bitmap.Config.RGB_565
                    ), applicationContext
                )
            }
        }
        refreshBookmarkButton()
    }

    private fun refreshBookmarkButton() {
        if (viewModel.comic != null && viewModel.comic.isBookmarked()) {
            btnBookmark.setBackgroundResource(android.R.drawable.btn_star_big_on)
        } else {
            btnBookmark.setBackgroundResource(android.R.drawable.btn_star_big_off)
        }
    }

    private fun registerAndListenCallbacks() {
        viewModel.xkcdStatesLiveData.observe(this, Observer { state ->
            when (state) {
                is XkcdStates.Loading -> {
                    //TODO : update ui accordingly
                }
                is XkcdStates.ComicLoadedSuccessfully -> {
                    updateUI()
                }
                is XkcdStates.ComicLoadingFailed -> {
                    Toast.makeText(this, state.errorMessage, Toast.LENGTH_LONG).show()
                    progressBar.visibility = View.GONE

                }
            }
        })
    }

    private fun updateUI() {
        val comic = viewModel.comic
        tvTitle.text = comic.title
        etComic.setText(comic.num.toString())

        //Load image from local storage
        if (viewModel.isComicPresentInLocalDB(viewModel.comic.num.toString())) {
            ivComicViewer.setImageBitmap(ImageHandler.loadImageFromStorage(viewModel.comic.num.toString()))
            progressBar.visibility = View.GONE

        } else { // Load image from remote server
            Glide.with(this)
                .asBitmap()
                .load(comic.img)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        progressBar.visibility = View.GONE
                        ivComicViewer.setImageBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        progressBar.visibility = View.GONE
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        progressBar.visibility = View.GONE
                    }

                    override fun onLoadStarted(placeholder: Drawable?) {
                        progressBar.visibility = View.VISIBLE
                    }
                })
        }
        refreshBookmarkButton()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.MENU_SHARE_LINK -> {
                shareLink()
                true
            }
            R.id.MENU_SHARE_IMAGE -> {
                shareImage()
                true
            }
            R.id.MENU_SEARCH_TITLE -> {
                val intent = Intent(this, ListViewActivity::class.java)
                intent.putExtra(packageName + "LoadType", ListViewActivity.LoadType.SEARCH_TITLE)
                startActivityForResult(intent, ACTIVITY_REQUEST_CODE)
                true
                true
            }
            R.id.MENU_BOOKMARKS -> {
                val intent = Intent(this, ListViewActivity::class.java)
                intent.putExtra(packageName + "LoadType", ListViewActivity.LoadType.BOOKMARKS)
                startActivityForResult(intent, ACTIVITY_REQUEST_CODE)
                true
            }
            R.id.MENU_ABOUT -> {
                true
            }
            else -> {
                return false
            }
        }
    }

    private fun shareLink() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, viewModel.comic.getlink())
        startActivity(Intent.createChooser(shareIntent, "Share link using"))
    }

    private fun shareImage() {

        val hasPermission = EasyPermissions.hasPermissions(applicationContext, *permissions)
        val bitmap = ivComicViewer.drawable.toBitmap(ivComicViewer.width, ivComicViewer.height, Bitmap.Config.RGB_565)

        if (hasPermission) {
            //val uri = getUriImageFromBitmap(bitmap, this@MainActivity)
            shareImageFromBitmap(bitmap)
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Application needs access to your storage",
                SHARE_STORAGE_PERMS_REQUEST_CODE,
                *permissions
            )
        }
    }

    private fun shareImageFromBitmap(bmp: Bitmap) {
        val uri = getUriImageFromBitmap(bmp, this@MainActivity)
            ?: //Show no URI message
            return

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_TEXT, viewModel.comic.img)
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.type = "image/png"
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(shareIntent, "Share comic using"))
    }

    private fun getUriImageFromBitmap(bmp: Bitmap?, context: Context): Uri? {
        if (bmp == null)
            return null

        var bmpUri: Uri? = null
        try {
            val file = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                  "${viewModel.comic.num}.png"
            )
            val out = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.flush()
            out.close()
            bmpUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file)

        } catch (e: IOException) {
            //e.printStackTrace()
        }
        return bmpUri
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            ListViewActivity.ACTIVITY_RESULT_SUCCESS -> {
                val comicID = data?.getStringExtra(ListViewActivity.SELECTED_BOOKMARK)

                if (comicID != null) {
                    viewModel.isNetworkConnected = isNetworkConnected(this)
                    viewModel.loadComic(comicID.toInt())
                }
            }
        }
    }
}
