package com.example.newdo.ui.fragments

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.widget.AbsListView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newdo.R
import com.example.newdo.adapters.NewsAdapter
import com.example.newdo.databinding.FragmentSearchBinding
import com.example.newdo.ui.MainActivity
import com.example.newdo.ui.viewmodels.NewsViewModel
import com.example.newdo.utils.Constants
import com.example.newdo.utils.Constants.Companion.RQ_SPEECH_REC
import com.example.newdo.utils.Constants.Companion.SEARCH_NEWS_DELAY_TIME
import com.example.newdo.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var binding: FragmentSearchBinding

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    val args: SearchFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)

        viewModel = (activity as MainActivity).viewModel

        observeDarkMode()

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
                delay(SEARCH_NEWS_DELAY_TIME)

                //make request
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.getSearchNews(editable.toString())
                    }
                }
            }
        }

        //voice search
        binding.voiceSearch.setOnClickListener {
            voiceSearch()
        }

        searchSelectedTopic()

        makeRequest()

    }

    private fun searchSelectedTopic() {
        val topic = args.topic
        binding.etSearch.setText(topic)
    }

    private fun voiceSearch() {
        //check if device is capable of using voice recognition
        if (!SpeechRecognizer.isRecognitionAvailable(requireContext())) {
            //Voice Recognition not available
        } else {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Try saying something")
            startActivityForResult(intent, RQ_SPEECH_REC)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RQ_SPEECH_REC && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            //pass result to the search editText
            val resultString = result?.get(0).toString()
            binding.etSearch.setText(resultString)
        }
    }

    private fun makeRequest() {
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

                        if (isLastPage) {
                            binding.searchRecyclerView.setPadding(0, 0, 0, 0)
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
            }
        }
    }

    private fun observeDarkMode() {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                binding.toolBar.setBackgroundResource(R.drawable.et_search_bg)
                binding.voiceSearch.setImageResource(R.drawable.ic_mic)
            } // Light mode is active

            Configuration.UI_MODE_NIGHT_YES -> {
                binding.toolBar.setBackgroundResource(R.drawable.et_search_bg_dark)
                binding.voiceSearch.setImageResource(R.drawable.ic_mic_dark)

            } // Night mode is active
        }
    }

}