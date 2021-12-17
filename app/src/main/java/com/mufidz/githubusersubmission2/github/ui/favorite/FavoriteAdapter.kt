package com.mufidz.githubusersubmission2.github.ui.favorite

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mufidz.githubusersubmission2.R
import com.mufidz.githubusersubmission2.databinding.ItemUserBinding
import com.mufidz.githubusersubmission2.github.model.Favorite
import com.mufidz.githubusersubmission2.github.ui.detail.DetailUser

class FavoriteAdapter : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {
    var listFavorites = ArrayList<Favorite>()
    set(listFavorites) {
        if (listFavorites.size > 0){
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

    inner class FavoriteViewHolder(val binding: ItemUserBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(favorite: Favorite) {
            binding.root.setOnClickListener {
                val intent = Intent(it.context, DetailUser::class.java)
                intent.putExtra(DetailUser.EXTRA_USERNAME, favorite.username)
                it.context.startActivity(intent)
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

    fun addItem(note: Favorite) {
        this.listFavorites.add(note)
        notifyItemInserted(this.listFavorites.size - 1)
    }
    fun removeItem(position: Int) {
        this.listFavorites.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, this.listFavorites.size)
    }

}