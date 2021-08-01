package com.example.newdo.adapters

import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.newdo.R
import com.example.newdo.database.model.Topics
import com.example.newdo.databinding.ItemTopicBinding

class TopicAdapter(private val context: Context) :
    RecyclerView.Adapter<TopicAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(val binding: ItemTopicBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Topics>() {
        override fun areItemsTheSame(oldItem: Topics, newItem: Topics): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Topics, newItem: Topics): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
    var topics: List<Topics>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun getItemCount() = topics.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            ItemTopicBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.binding.apply {
            val topic = topics[position]
            holder.itemView.apply {

                title.text = topic.title

                //article card background
                articleCardBackground(holder)

                //add click event for articles
                setOnClickListener {
                    onCountryClickListener?.let { it(topic.title) }

                }


            }
        }
    }


    private fun articleCardBackground(holder: TopicAdapter.NewsViewHolder) {
        holder.binding.apply {
            holder.itemView.apply {

                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_NO -> {
                        itemCard.setBackgroundResource(R.drawable.article_card_bg)
                    } // Light mode is active

                    Configuration.UI_MODE_NIGHT_YES -> {
                        itemCard.setBackgroundResource(R.drawable.article_card_bg_dark)
                    } // Night mode is active
                }
            }
        }

    }

    private var onCountryClickListener: ((String) -> Unit)? = null

    fun setOnCountryClickListener(listener: (String) -> Unit) {
        onCountryClickListener = listener
    }
}

