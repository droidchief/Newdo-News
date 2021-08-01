package com.example.newdo.adapters

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newdo.R
import com.example.newdo.database.model.Menu
import com.example.newdo.databinding.ItemCountryBinding
import com.example.newdo.databinding.ItemMenuBinding

class MenuAdapter(private val context: Context) :
    RecyclerView.Adapter<MenuAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(val binding: ItemMenuBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Menu>() {
        override fun areItemsTheSame(oldItem: Menu, newItem: Menu): Boolean {
            return oldItem.menuName == newItem.menuName
        }

        override fun areContentsTheSame(oldItem: Menu, newItem: Menu): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
    var menuList: List<Menu>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun getItemCount() = menuList.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            ItemMenuBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.binding.apply {
            val menu = menuList[position]
            holder.itemView.apply {

                Glide.with(this).load(menu.menuIcon).into(menuIcon)

                menuName.text = menu.menuName

                //article card background
                articleCardBackground(holder)

                //add click event for articles
                setOnClickListener {
                    onMenuClickListener?.let { it(position) }
                }


            }
        }

    }


    private fun articleCardBackground(holder: NewsViewHolder) {
        holder.binding.apply {
            holder.itemView.apply {

                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_NO -> {
                        menuName.setTextColor(Color.parseColor("#131313"))
                    } // Light mode is active

                    Configuration.UI_MODE_NIGHT_YES -> {
                    } // Night mode is active
                }
            }
        }

    }

    private var onMenuClickListener: ((Int) -> Unit)? = null

    fun setOnMenuClickListener(listener: (Int) -> Unit) {
        onMenuClickListener = listener
    }
}

