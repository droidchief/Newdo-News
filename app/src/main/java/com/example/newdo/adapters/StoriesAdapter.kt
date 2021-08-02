package com.example.newdo.adapters

import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newdo.R
import com.example.newdo.database.model.Story
import com.example.newdo.database.model.Topics
import com.example.newdo.databinding.ItemStoryBinding
import com.example.newdo.databinding.ItemTopicBinding

class StoriesAdapter(private val context: Context) :
    RecyclerView.Adapter<StoriesAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Story>() {
        override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
    var stories: List<Story>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun getItemCount() = stories.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            ItemStoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.binding.apply {
            val story = stories[position]
            holder.itemView.apply {

                Glide.with(context).load(story.thumbnail).into(thumbnail)
                name.text = story.name

                //article card background
                articleCardBackground(holder)

                //add click event for articles
                setOnClickListener {
                    onStoryClickListener?.let { it(story) }

                }


            }
        }
    }


    private fun articleCardBackground(holder: StoriesAdapter.NewsViewHolder) {
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

    private var onStoryClickListener: ((Story) -> Unit)? = null

    fun setOnStoryClickListener(listener: (Story) -> Unit) {
        onStoryClickListener = listener
    }
}

