package com.example.xkcdviewer.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.xkcdviewer.R
import com.example.xkcdviewer.db.ComicModel
import com.example.xkcdviewer.view.adapter.ListViewModelAdapter
import com.example.xkcdviewer.viewmodel.ListViewModel

class ListViewActivity : AppCompatActivity() {

    enum class LoadType {
        BOOKMARKS, SEARCH_TITLE
    }

    companion object {
        /**
         * When bookmark operation completed
         */
        const val ACTIVITY_RESULT_SUCCESS = 200

        /**
         * When search completed
         */
        const val SEARCH_DIALOG_RESULT_SUCCESS = 300

        /**
         * Selected bookmark item
         */
        const val SELECTED_BOOKMARK = "SELECTED_BOOKMARK"
    }

    private lateinit var viewModel: ListViewModel
    private var loadtype = LoadType.BOOKMARKS


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view)
        title = "Favorites"
        val intent = intent
        loadtype = intent.getSerializableExtra(packageName + "LoadType") as LoadType

        if(loadtype == LoadType.SEARCH_TITLE) {
            showDialog()
            loadBookmarkComics()
        } else {
            loadBookmarkComics()
        }
    }

    private fun loadBookmarkComics()  {
        title = this.resources.getString(R.string.title_activity_bookmark)
        viewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)
        viewModel.loadDataBase(this)

        val listView = findViewById<ListView>(R.id.sample_listVw)
        var listViewAdapter = ListViewModelAdapter(this,viewModel.comicDBHelper.readAllComics() )
        listView.adapter = listViewAdapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->

            val comic = adapterView.adapter.getItem(position) as ComicModel
            val comicIntent = Intent()
            comicIntent.putExtra(SELECTED_BOOKMARK, comic.num.toString())
            setResult(ACTIVITY_RESULT_SUCCESS, comicIntent)
            finish()
        }
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Search comic by title")
        builder.setIcon(android.R.drawable.ic_menu_search)
        val dialogLayout = inflater.inflate(R.layout.search_dlg, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.search_dlg_edit_box)
        builder.setView(dialogLayout)
        builder.setPositiveButton("OK") { dialogInterface, i ->
            Toast.makeText(applicationContext, "Work in progress....", Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }
}
