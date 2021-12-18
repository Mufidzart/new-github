package com.mufidz.githubusersubmission2.github.ui.favorite

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mufidz.githubusersubmission2.R
import com.mufidz.githubusersubmission2.databinding.ItemUserBinding
import com.mufidz.githubusersubmission2.github.db.DatabaseContract
import com.mufidz.githubusersubmission2.github.db.FavoriteHelper
import com.mufidz.githubusersubmission2.github.model.Favorite
import com.mufidz.githubusersubmission2.github.model.UserGitHub
import com.mufidz.githubusersubmission2.github.ui.detail.DetailUser

class FavoriteAdapter(val context: Context) :
    RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {
    var listFavorites = ArrayList<Favorite>()
        set(listFavorites) {
            if (listFavorites.size > 0) {
                this.listFavorites.clear()
            }
            this.listFavorites.addAll(listFavorites)
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(listFavorites[position])
    }

    override fun getItemCount(): Int = this.listFavorites.size

    inner class FavoriteViewHolder(val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(favorite: Favorite) {
            binding.root.setOnClickListener {
                val intent = Intent(it.context, DetailUser::class.java)
                intent.putExtra(DetailUser.EXTRA_USERNAME, favorite.username)
                it.context.startActivity(intent)
            }
            binding.btnDelete.visibility = View.VISIBLE
            binding.btnDelete.setOnClickListener {
                deleteUser(favorite.username)
            }
            binding.apply {
                tvName.text = favorite.username
                Glide.with(itemView)
                    .load(favorite.avatar)
                    .error(R.drawable.default_avatar)
                    .centerCrop()
                    .into(ivUser)
            }
        }
    }

    private fun deleteUser(username: String?) {
        AlertDialog.Builder(context)
            .setTitle("Delete from favorites")
            .setMessage("Are you sure delete this user")
            .setPositiveButton("Yes") { dialog, _ ->
                val favoriteHelper = FavoriteHelper.getInstance(context)
                favoriteHelper.open()
                val values = ContentValues()
                values.put(DatabaseContract.FavoriteColumns.USERNAME, username.toString())
                val result = favoriteHelper.deleteByUsername(username.toString(), values).toLong()
                if (result > 0) {
                    Toast.makeText(context, "Removed from favorite", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed remove favorite", Toast.LENGTH_SHORT).show()
                }
                val cursor = favoriteHelper.queryAll()
                val usernameColumn = cursor.getColumnIndex(DatabaseContract.FavoriteColumns.USERNAME)
                val avatarColumn = cursor.getColumnIndex(DatabaseContract.FavoriteColumns.AVATAR)
                val dateColumn = cursor.getColumnIndex(DatabaseContract.FavoriteColumns.DATE)
                val idColumn = cursor.getColumnIndex(DatabaseContract.FavoriteColumns._ID)
                val favorites = ArrayList<Favorite>()
                cursor.moveToFirst()
                do {
                    val username = cursor.getString(usernameColumn)
                    val avatar = cursor.getString(avatarColumn)
                    val date = cursor.getString(dateColumn)
                    val id = cursor.getInt(idColumn)
                    favorites.add(Favorite(id, username, avatar, date))
                } while (cursor.moveToNext())
                favoriteHelper.close()
                listFavorites.clear()
                listFavorites.addAll(favorites)
                notifyDataSetChanged()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

}