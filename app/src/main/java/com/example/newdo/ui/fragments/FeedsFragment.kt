package com.example.newdo.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newdo.R
import com.example.newdo.adapters.NewsAdapter
import com.example.newdo.databinding.FragmentArticleBinding
import com.example.newdo.databinding.FragmentFavouriteBinding
import com.example.newdo.databinding.FragmentFeedBinding
import com.example.newdo.ui.MainActivity
import com.example.newdo.ui.viewmodels.NewsViewModel
import com.example.newdo.utils.Resource

class FeedsFragment: Fragment(R.layout.fragment_feed) {

    private lateinit var binding: FragmentFeedBinding

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    val TAG = "FeedsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFeedBinding.bind(view)

        viewModel = (activity as MainActivity).viewModel

        //setup recycler view
        setUpRecyclerView()

        //pass data to the article page
        newsAdapter.setOnArticleClickListener { clickedArticle ->
            val bundle = Bundle().apply {
                putSerializable("article", clickedArticle)
            }

            findNavController().navigate(
                R.id.action_feedsFragment_to_articleFragment,
                bundle
            )
        }

        makeRequest()

        binding.refreshLayout.setOnRefreshListener {
            makeRequest()

            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                binding.refreshLayout.isRefreshing = false
            }, 4000)
        }

    }

    private fun makeRequest() {
        //observe changes and update view
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "an error occured: $message")
                    }
                }
            }

        })
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
    }
    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter(requireContext())
        binding.feedsRecyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}