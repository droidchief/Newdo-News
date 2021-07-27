package com.example.newdo.adapters

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newdo.R
import com.example.newdo.database.model.Article
import com.example.newdo.databinding.ItemArticleBinding
import com.example.newdo.helperfile.ThemeManager
import com.example.newdo.ui.MainActivity
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class NewsAdapter(private val context: Context) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            ItemArticleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.binding.apply {
            val article = differ.currentList[position]
            holder.itemView.apply {

                //check if article has image
                if (article.urlToImage != null) {
                    Glide.with(this).load(article.urlToImage).into(articleImage)
                }else {
                    //make image place holder invisible
                    itemCard.visibility = View.GONE
                }

                title.text = article.title
                description.text = article.description
                source.text = article.source?.name
                timePublished.text = article.publishedAt

                //add click event for articles
                setOnClickListener {
                    onArticleClickListener?.let {it(article)}
                }

                //article card background
                articleCardBackground(holder)
            }
        }
    }

    private fun articleCardBackground(holder: NewsViewHolder) {
        holder.binding.apply {
            holder.itemView.apply {

                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_NO -> {
                        itemCard.setBackgroundResource(R.drawable.article_card_bg)
                    } // Night mode is not active

                    Configuration.UI_MODE_NIGHT_YES -> {
                        itemCard.setBackgroundResource(R.drawable.article_card_bg_dark)
                    } // Night mode is active
                }
            }
        }

    }

    private var onArticleClickListener: ((Article) -> Unit)? = null

    fun setOnArticleClickListener(listener : (Article) -> Unit) {
        onArticleClickListener = listener
    }
}
