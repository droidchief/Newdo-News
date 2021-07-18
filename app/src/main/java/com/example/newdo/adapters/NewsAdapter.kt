package com.example.newdo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newdo.database.model.Article
import com.example.newdo.databinding.ItemArticleBinding
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
                Glide.with(this).load(article.urlToImage).into(articleImage)
                title.text = article.title
                description.text = article.description
                source.text = article.source.name
                timePublished.text = article.publishedAt

                //add click event for articles
                setOnClickListener {
                    onArticleClickListener?.let {it(article)}
                }
            }
        }
    }

    private var onArticleClickListener: ((Article) -> Unit)? = null

    fun setOnArticleClickListener(listener : (Article) -> Unit) {
        onArticleClickListener = listener
    }
}
