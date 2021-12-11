package com.mufidz.githubusersubmission2.github.ui.detail

import android.content.ContentValues
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.mufidz.githubusersubmission2.R
import com.mufidz.githubusersubmission2.databinding.ActivityDetailUserBinding
import com.mufidz.githubusersubmission2.github.db.DatabaseContract
import com.mufidz.githubusersubmission2.github.db.FavoriteHelper
import com.mufidz.githubusersubmission2.github.model.Favorite
import java.text.SimpleDateFormat
import java.util.*

const val ISOFORMAT = "yyyy/MM/dd'T'HH:mm:ss"

class DetailUser : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityDetailUserBinding
    private lateinit var viewModel: DetailUserViewModel
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showLoading(true)
        val username = intent.getStringExtra(EXTRA_USERNAME)
        val bundle = Bundle()
        bundle.putString(EXTRA_USERNAME, username)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(DetailUserViewModel::class.java)
        viewModel.setUserDetail(username!!)
        viewModel.getUserDetail().observe(this, {
            setTitle("Detail User " + it.name)
            if (it != null) {
                val office: String
                val place: String
                office = it.company ?: "Unknown Company"
                place = it.company ?: "Unknown Location"
                binding.apply {
                    tvNameDetail.text = it.name
                    tvUsername.text = it.login
                    tvCompanyDetail.text = office
                    tvLocation.text = place
                    jmlFollower.text = it.followers.toString()
                    jmlFollowing.text = it.following.toString()
                    jmlRepository.text = it.public_repos.toString()
                    Glide.with(this@DetailUser)
                        .load(it.avatar_url)
                        .error(R.drawable.default_avatar)
                        .fitCenter()
                        .into(imgUserDetail)
                }
            }
        })
        val sectionPagerAdapter = SectionPagerAdapter(this, supportFragmentManager, bundle)
        binding.apply {
            viewPager.adapter = sectionPagerAdapter
            tabs.setupWithViewPager(viewPager)
        }


        val favoriteHelper = FavoriteHelper.getInstance(applicationContext)
        favoriteHelper.open()
        val user = findViewById<TextView>(R.id.tv_username).text.toString()
        val cursor = favoriteHelper.queryByUsername(user)
        Log.d("Data", user)
        if (cursor.count == 0) {
            Log.d("DEBUG", "NO DATA FOUND")
            val btnTitle = "Add Favorite"
            binding.btnAddFavorite.text = btnTitle
        } else {
            Log.d("DEBUG", "DATA FOUND")
            val btnTitle = "Remove Favorite"
            binding.btnAddFavorite.text = btnTitle
        }
        favoriteHelper.close()
        binding.btnAddFavorite.setOnClickListener(this)
        handler.postDelayed({
            showLoading(false)
        }, 3000)
    }

    override fun onClick(view: View) {
        if (view.id != R.id.btn_add_favorite) return
        val username = binding.tvUsername.text.toString().trim()
        if (username.isEmpty()) {
            binding.tvUsername.error = "Can not get username!"
            return
        }
        val favoriteHelper = FavoriteHelper.getInstance(applicationContext)
        favoriteHelper.open()
        val values = ContentValues()
        val user = binding.tvUsername.text.toString()
        val cursor = favoriteHelper.queryByUsername(user)
        val dateFormat = SimpleDateFormat(ISOFORMAT)
        val date = dateFormat.format(Date())
        values.put(DatabaseContract.FavoriteColumns.USERNAME, username)
        values.put(DatabaseContract.FavoriteColumns.DATE, date)
        if (cursor.count == 0) {
            val result = favoriteHelper.insert(values)
            Log.d("Debug_result", result.toString())
            if (result.toString() != "0") {
                val btnTitle = "Remove Favorite"
                binding.btnAddFavorite.text = btnTitle
                Toast.makeText(this, "Success add to favorite", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed add to favorite", Toast.LENGTH_SHORT).show()
            }
        } else {
            val result = favoriteHelper.deleteByUsername(username, values).toLong()
            if (result > 0) {
                val btnTitle = "Add Favorite"
                binding.btnAddFavorite.text = btnTitle
                finish()
            } else {
                Toast.makeText(this, "Failed remove favorite", Toast.LENGTH_SHORT).show()
            }
            return
        }
        favoriteHelper.close()
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) ProgressBar.VISIBLE else ProgressBar.GONE
    }

    companion object {
        const val EXTRA_USERNAME = "extra_username"
    }
}



