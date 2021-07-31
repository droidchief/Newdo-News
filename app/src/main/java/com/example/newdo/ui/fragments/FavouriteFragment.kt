package com.example.newdo.ui.fragments

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newdo.R
import com.example.newdo.adapters.NewsAdapter
import com.example.newdo.databinding.FragmentFavouriteBinding
import com.example.newdo.ui.MainActivity
import com.example.newdo.ui.menu.MenuActivity
import com.example.newdo.ui.viewmodels.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class FavouriteFragment: Fragment(R.layout.fragment_favourite) {

    private lateinit var binding: FragmentFavouriteBinding

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavouriteBinding.bind(view)

        viewModel = (activity as MainActivity).viewModel

        observeDarkMode()

        binding.menu.setOnClickListener {
            startActivity(Intent(requireContext(), MenuActivity::class.java))
        }

        setUpRecyclerView() 

        //pass data to the article page
        newsAdapter.setOnArticleClickListener { clickedArticle ->
            val bundle = Bundle().apply {
                putSerializable("article", clickedArticle)
            }

            findNavController().navigate(
                R.id.action_favouriteFragment2_to_articleFragment,
                bundle
            )
        }

        //swipe to delete article
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]

                viewModel.deleteArticle(article)
                Snackbar.make(view, "Removed Successfully", Snackbar.LENGTH_SHORT).apply {
                    setAction("Undo") {
                        viewModel.saveArticle(article)
                    }

                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
             attachToRecyclerView(binding.favouriteRecyclerView)
        }

        //observes changes in database
        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { articles ->
            if (articles.isEmpty()) {
                binding.emptyFavouriteLayout.visibility = View.VISIBLE
                Glide.with(requireContext()).load(R.drawable.rocket).into(binding.emptyFavouriteGif)
            }else {
                binding.emptyFavouriteLayout.visibility = View.GONE
            }

            newsAdapter.differ.submitList(articles)
        })

    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter(requireContext())
        binding.favouriteRecyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            setPadding(0,0,0,0)
        }
    }

    private fun observeDarkMode() {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                binding.menu.setImageResource(R.drawable.ic_menu_dark)
            } // Light mode is active

            Configuration.UI_MODE_NIGHT_YES -> {
                binding.menu.setImageResource(R.drawable.ic_menu_light)
            } // Night mode is active
        }
    }

}