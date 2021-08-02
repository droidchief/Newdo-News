package com.example.newdo.ui.fragments

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AbsListView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newdo.R
import com.example.newdo.adapters.CountriesAdapter
import com.example.newdo.adapters.MenuAdapter
import com.example.newdo.adapters.NewsAdapter
import com.example.newdo.database.model.Country
import com.example.newdo.database.model.Menu
import com.example.newdo.databinding.FragmentFeedBinding
import com.example.newdo.ui.MainActivity
import com.example.newdo.ui.menu.SettingsActivity
import com.example.newdo.ui.viewmodels.NewsViewModel
import com.example.newdo.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.example.newdo.utils.Resource
import java.util.*
import kotlin.collections.ArrayList

class FeedsFragment : Fragment(R.layout.fragment_feed) {

    private lateinit var binding: FragmentFeedBinding

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var countryAdapter: CountriesAdapter
    lateinit var menuAdapter: MenuAdapter
    private lateinit var myCountryList: ArrayList<Country>
    private lateinit var myMenuList: ArrayList<Menu>

    lateinit var currentLocation: Location


    val TAG = "FeedsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFeedBinding.bind(view)

        viewModel = (activity as MainActivity).viewModel

        observeDarkMode()

        //setup recycler views
        setUpRecyclerView()
        setUpCountryRecyclerView()
        setUpMenuRecyclerView()

        //more menu
        binding.menu.setOnClickListener {
            openMenu()
        }
        binding.closeMenu.setOnClickListener {
            closeMenu()
        }

        menuAdapter.setOnMenuClickListener { position ->
            when(position) {
                0 -> {
                    val bundle = Bundle().apply {
                        putString("country", "ng")
                    }

                    findNavController().navigate(
                        R.id.action_feedsFragment_to_countryTopTenFragment,
                        bundle
                    )
                }

                3 -> startActivity(Intent(requireContext(), SettingsActivity::class.java))

            }
        }

        //pass data to the article page
        countryAdapter.setOnCountryClickListener { countryCode ->
            if (binding.menuLayout.isVisible) {
                return@setOnCountryClickListener

            } else {
                val bundle = Bundle().apply {
                    putString("country", countryCode)
                }

                findNavController().navigate(
                    R.id.action_feedsFragment_to_countryTopTenFragment,
                    bundle
                )
            }

        }

        newsAdapter.setOnArticleClickListener { clickedArticle ->
            if (binding.menuLayout.isVisible) {
                return@setOnArticleClickListener

            } else {

                val bundle = Bundle().apply {
                    putSerializable("article", clickedArticle)
                }

                findNavController().navigate(
                    R.id.action_feedsFragment_to_articleFragment,
                    bundle
                )

            }

        }

        makeRequest()

        binding.refreshLayout.setOnRefreshListener {
            makeRequest()

            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                binding.refreshLayout.isRefreshing = false
            }, 4000)
        }

    }

    private fun openMenu() {
        if (!binding.menuLayout.isVisible) {
            binding.menuLayout.visibility = View.VISIBLE
        }
    }

    private fun closeMenu() {
        if (binding.menuLayout.isVisible) {
            binding.menuLayout.visibility = View.INVISIBLE
        }

    }

    private fun setUpMenuRecyclerView() {
        binding.menuRecyclerView.apply {
            menuAdapter = MenuAdapter(requireContext())
            adapter = menuAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            //init list
            myMenuList = ArrayList()

            //add items
            myMenuList.add(Menu(R.drawable.ic_baseline_settings_input_svideo_24, "Discover"))
            myMenuList.add(Menu(R.drawable.ic_baseline_slow_motion_video_24, "Stories"))
            myMenuList.add(Menu(R.drawable.empty_vector, ""))
            myMenuList.add(Menu(R.drawable.ic_baseline_settings_24, "Settings"))

            menuAdapter.menuList = myMenuList
        }

    }

    private fun setUpCountryRecyclerView() {
        binding.countryListRecyclerView.apply {
            countryAdapter = CountriesAdapter(requireContext())
            adapter = countryAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            //init list
            myCountryList = ArrayList()

            //add items
            myCountryList.add(Country(R.drawable.ic_nigeria, "Nigeria", "ng"))
            myCountryList.add(Country(R.drawable.ic_united_states, "United States", "us"))
            myCountryList.add(Country(R.drawable.ic_argentina, "Argentina", "ar"))
            myCountryList.add(Country(R.drawable.ic_greece, "Greece", "gr"))
            myCountryList.add(Country(R.drawable.ic_netherlands, "Netherlands", "nl"))
            myCountryList.add(Country(R.drawable.ic_south_africa, "South Africa", "za"))
            myCountryList.add(Country(R.drawable.ic_austrailia, "Australia", "au"))
            myCountryList.add(Country(R.drawable.ic_hong_kong, "Honk Kong", "hk"))
            myCountryList.add(Country(R.drawable.ic_new_zealand, "New Zealand", "nz"))
            myCountryList.add(Country(R.drawable.ic_south_korea, "South Korea", "kr"))


            countryAdapter.countries = myCountryList
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
                viewModel.getBreakingNews("ng")
                isScrolling = false
            }
        }
    }

    private fun observeDarkMode() {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                binding.menu.setImageResource(R.drawable.ic_menu_dark)
                binding.pageTitle.setTextColor(Color.parseColor("#131313"))
                binding.menuLayout.setBackgroundResource(R.color.white)
            } // Light mode is active

            Configuration.UI_MODE_NIGHT_YES -> {
                binding.menu.setImageResource(R.drawable.ic_menu_light)
                binding.menuLayout.setBackgroundResource(R.color.black)
            } // Night mode is active
        }
    }

}