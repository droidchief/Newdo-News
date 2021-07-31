package com.example.newdo.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AbsListView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newdo.R
import com.example.newdo.adapters.CountriesAdapter
import com.example.newdo.adapters.NewsAdapter
import com.example.newdo.database.model.Country
import com.example.newdo.databinding.FragmentFeedBinding
import com.example.newdo.ui.MainActivity
import com.example.newdo.ui.menu.MenuActivity
import com.example.newdo.ui.viewmodels.NewsViewModel
import com.example.newdo.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.example.newdo.utils.Resource

class FeedsFragment : Fragment(R.layout.fragment_feed) {

    private lateinit var binding: FragmentFeedBinding

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var countryAdapter: CountriesAdapter
    private lateinit var myModelList: ArrayList<Country>


    val TAG = "FeedsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFeedBinding.bind(view)

        viewModel = (activity as MainActivity).viewModel

        binding.menu.setOnClickListener {
            startActivity(Intent(requireContext(), MenuActivity::class.java))
        }

        //setup recycler views
        setUpRecyclerView()
        setUpCountryRecyclerView()

        //pass data to the article page
        countryAdapter.setOnCountryClickListener { countryCode ->
            val bundle = Bundle().apply {
                putString("country", countryCode)
            }

            findNavController().navigate(
                R.id.action_feedsFragment_to_countryTopTenFragment,
                bundle
            )
        }

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

    private fun setUpCountryRecyclerView() {
        binding.countryListRecyclerView.apply {
            countryAdapter = CountriesAdapter(requireContext())
            adapter = countryAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            //init list
            myModelList = ArrayList()

            //add items
            myModelList.add(Country(R.drawable.ic_launcher_background, "Nigeria", "ng"))
            myModelList.add(Country(R.drawable.ic_launcher_background, "United States", "us"))
            myModelList.add(Country(R.drawable.ic_launcher_background, "Argentina", "ar"))
            myModelList.add(Country(R.drawable.ic_launcher_background, "Greece", "gr"))
            myModelList.add(Country(R.drawable.ic_launcher_background, "Netherlands", "nl"))
            myModelList.add(Country(R.drawable.ic_launcher_background, "South Africa", "za"))
            myModelList.add(Country(R.drawable.ic_launcher_background, "Austrailia", "au"))
            myModelList.add(Country(R.drawable.ic_launcher_background, "Honk Kong", "hk"))
            myModelList.add(Country(R.drawable.ic_launcher_background, "New Zealand", "nz"))
            myModelList.add(Country(R.drawable.ic_launcher_background, "South Korea", "kr"))


            countryAdapter.countries = myModelList
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
                            binding.feedsRecyclerView.setPadding(0, 0, 0, 0)
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
        newsAdapter = NewsAdapter(requireContext())
        binding.feedsRecyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)

            addOnScrollListener(this@FeedsFragment.scrollListener)
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
                viewModel.getBreakingNews("us")
                isScrolling = false
            }
        }
    }
}