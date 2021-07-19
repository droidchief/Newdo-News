package com.example.newdo.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newdo.R
import com.example.newdo.adapters.NewsAdapter
import com.example.newdo.databinding.FragmentSearchBinding
import com.example.newdo.ui.MainActivity
import com.example.newdo.ui.viewmodels.NewsViewModel
import com.example.newdo.utils.Constants
import com.example.newdo.utils.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.example.newdo.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var binding: FragmentSearchBinding

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    val TAG = "SearchFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)

        viewModel = (activity as MainActivity).viewModel
        setUpRecyclerView()

        //pass data to the article page
        newsAdapter.setOnArticleClickListener { clickedArticle ->
            val bundle = Bundle().apply {
                putSerializable("article", clickedArticle)
            }

            findNavController().navigate(
                R.id.action_searchFragment2_to_articleFragment,
                bundle
            )
        }

        //handle search functionality
        var job: Job? = null
        binding.etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)

                //make request
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.getSearchNews(editable.toString())
                    }
                }
            }
        }


        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        //set pagination
                        val totalPages =
                            newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2 //last page of response is always empty and the integer division is always rounded off (+2)
                        isLastPage = viewModel.searchNewsCurrentPage == totalPages
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
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter(requireContext())
        binding.searchRecyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchFragment.scrollListener)
        }
    }


    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val fviPosition =
                layoutManager.findFirstVisibleItemPosition() //first visible item position
            val vic = layoutManager.childCount //visible item count
            val tic = layoutManager.itemCount //total item count

            val isNotLoadingAndAtLastPage = !isLoading && !isLastPage
            val isAtLastItem = fviPosition + vic >= tic
            val isNotArBeginning = fviPosition >= 0
            val isTotalMoreThanVisible = tic >= Constants.QUERY_PAGE_SIZE

            val paginate =
                isNotLoadingAndAtLastPage && isAtLastItem && isNotArBeginning && isTotalMoreThanVisible && isScrolling
            if (paginate) {
                viewModel.getSearchNews(binding.etSearch.text.toString())
                isScrolling = false
            } else {
                binding.searchRecyclerView.setPadding(0, 0, 0, 0)
            }
        }
    }
}