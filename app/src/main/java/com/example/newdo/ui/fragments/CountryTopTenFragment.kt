package com.example.newdo.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.newdo.R
import com.example.newdo.adapters.CountryTopTenAdapter
import com.example.newdo.customview.CenterZoomLayout
import com.example.newdo.database.model.Article
import com.example.newdo.databinding.FragmentCountryTopTenBinding
import com.example.newdo.ui.MainActivity
import com.example.newdo.ui.viewmodels.NewsViewModel
import com.example.newdo.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.example.newdo.utils.Resource
import com.google.android.material.snackbar.Snackbar

class CountryTopTenFragment : Fragment(R.layout.fragment_country_top_ten) {

    private lateinit var binding: FragmentCountryTopTenBinding

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: CountryTopTenAdapter

    val args: CountryTopTenFragmentArgs by navArgs()
    private lateinit var countryCode: String



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCountryTopTenBinding.bind(view)

        viewModel = (activity as MainActivity).viewModel

        //get current country passed from previous page
        countryCode = args.country

        //setup recycler view
        setUpRecyclerView()

        //pass data to the article page
        newsAdapter.setOnArticleClickListener { clickedArticle ->
            val bundle = Bundle().apply {
                putSerializable("article", clickedArticle)
            }

            findNavController().navigate(
                R.id.action_countryTopTenFragment_to_articleFragment,
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
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        //set pagination
                        val totalPages =
                            newsResponse.totalResults / QUERY_PAGE_SIZE + 2 //last page of response is always empty and the integer division is always rounded off (+2)
                        isLastPage = viewModel.breakingNewsCurrentPage == totalPages

                        if (isLastPage) {
                            binding.discoverRecyclerView.setPadding(0, 0, 0, 0)
                        }

                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        //show visual feedback for error messages
                        if (message == "Check Internet Connection") {
                            poorInternetFeedback(message)

                        } else {
                            poorInternetFeedback(message)

                        }

                    }
                }
            }

        })
    }

    private fun poorInternetFeedback(message: String) {
        binding.noInternetFeedbackLayout.visibility = View.VISIBLE
        binding.noInternetFeedbackDes.text = message

        binding.noInternetFeedbackBtn.setOnClickListener {
            //hide feedback
            if (binding.noInternetFeedbackLayout.isVisible) {
                binding.noInternetFeedbackLayout.visibility = View.GONE
            }
            makeRequest()
        }
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
        val contentZoomLayoutManager = CenterZoomLayout(requireContext()).apply {
            orientation = LinearLayoutManager.HORIZONTAL
            reverseLayout = true
            stackFromEnd = true
        }

        newsAdapter = CountryTopTenAdapter(requireContext())
        binding.discoverRecyclerView.apply {
            adapter = newsAdapter
            layoutManager = contentZoomLayoutManager

            //set view to center automatically
            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(this)
            isNestedScrollingEnabled = false


            addOnScrollListener(this@CountryTopTenFragment.scrollListener)
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
            val isTotalMoreThanVisible = tic >= QUERY_PAGE_SIZE

            val paginate =
                isNotLoadingAndAtLastPage && isAtLastItem && isNotArBeginning && isTotalMoreThanVisible && isScrolling
            if (paginate) {
                viewModel.getBreakingNews(countryCode)
                isScrolling = false
            }
        }
    }
}