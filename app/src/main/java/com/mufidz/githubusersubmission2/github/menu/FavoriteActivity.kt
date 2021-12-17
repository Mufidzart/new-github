package com.mufidz.githubusersubmission2.github.menu

import android.content.Intent
import android.database.SQLException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mufidz.githubusersubmission2.R
import com.mufidz.githubusersubmission2.databinding.ActivityFavoriteUserBinding
import com.mufidz.githubusersubmission2.github.db.DatabaseContract
import com.mufidz.githubusersubmission2.github.db.FavoriteHelper
import com.mufidz.githubusersubmission2.github.model.Favorite
import com.mufidz.githubusersubmission2.github.ui.GithubUserAdapter
import com.mufidz.githubusersubmission2.github.ui.favorite.FavoriteAdapter

class FavoriteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_user)

        val favoriteHelper = FavoriteHelper.getInstance(applicationContext)
        favoriteHelper.open()

        val cursor = favoriteHelper.queryAll()
        if (cursor.count == 0) {
            Log.d("DEBUG", "NO DATA FOUND")
            val image = findViewById<ImageView>(R.id.notfound)
            image.visibility = ImageView.VISIBLE
        } else {
            val usernameColumn = cursor.getColumnIndex(DatabaseContract.FavoriteColumns.USERNAME)
            val avatarColumn = cursor.getColumnIndex(DatabaseContract.FavoriteColumns.AVATAR)
            val dateColumn = cursor.getColumnIndex(DatabaseContract.FavoriteColumns.DATE)
            val idColumn = cursor.getColumnIndex(DatabaseContract.FavoriteColumns._ID)
            val adapter = FavoriteAdapter()
            val favorites = ArrayList<Favorite>()
            cursor.moveToFirst()
            do {
                val username = cursor.getString(usernameColumn)
                val avatar = cursor.getString(avatarColumn)
                val date = cursor.getString(dateColumn)
                val id = cursor.getInt(idColumn)
                favorites.add(Favorite(id, username, avatar, date))
            } while (cursor.moveToNext())
            adapter.listFavorites = favorites
            val recyclerView = findViewById<RecyclerView>(R.id.rv_user_favorite)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
        favoriteHelper.close()
    }

    override fun onRestart() {
        super.onRestart()
        val intent = Intent(this, FavoriteActivity::class.java)
        startActivity(intent)
        finish()
    }
}