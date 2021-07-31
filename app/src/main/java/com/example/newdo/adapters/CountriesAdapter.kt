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
import com.example.newdo.database.model.Country
import com.example.newdo.databinding.ItemCountryBinding

class CountriesAdapter(private val context: Context) :
    RecyclerView.Adapter<CountriesAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(val binding: ItemCountryBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Country>() {
        override fun areItemsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem.countryName == newItem.countryName
        }

        override fun areContentsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
    var countries: List<Country>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun getItemCount() = countries.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            ItemCountryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.binding.apply {
            val country = countries[position]
            holder.itemView.apply {

                Glide.with(this).load(country.countryFlag).into(countryFlag)

                countryName.text = country.countryName

                //article card background
//                articleCardBackground(holder)

                //add click event for articles
                setOnClickListener {
                    onCountryClickListener?.let { it(country.countryCode) }
                }


            }
        }
    }


    private fun articleCardBackground(holder: NewsViewHolder) {
        holder.binding.apply {
            holder.itemView.apply {

                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_NO -> {
                        countryFlag.setBackgroundResource(R.drawable.article_card_bg)
                    } // Light mode is active

                    Configuration.UI_MODE_NIGHT_YES -> {
                        countryFlag.setBackgroundResource(R.drawable.article_card_bg_dark)
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

