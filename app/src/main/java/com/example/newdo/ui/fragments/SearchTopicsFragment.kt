package com.example.newdo.ui.fragments

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.newdo.R
import com.example.newdo.adapters.TopicAdapter
import com.example.newdo.database.model.Topics
import com.example.newdo.databinding.FragmentSearchTopicsBinding
import com.example.newdo.ui.MainActivity
import com.example.newdo.ui.viewmodels.NewsViewModel

class SearchTopicsFragment : Fragment(R.layout.fragment_search_topics) {

    private lateinit var binding: FragmentSearchTopicsBinding

    lateinit var viewModel: NewsViewModel
    lateinit var topicAdapter: TopicAdapter
    private lateinit var myTopicList: ArrayList<Topics>

    private var spanCount: Int = 2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchTopicsBinding.bind(view)

        viewModel = (activity as MainActivity).viewModel

        observeDarkMode()

        setUpRecyclerView()

        //pass data to the article page
        topicAdapter.setOnTopicClickListener { selectedTopic ->
            val bundle = Bundle().apply {
                putString("topic", selectedTopic)
            }

            findNavController().navigate(
                 R.id.action_searchTopicsFragment_to_searchFragment,
                bundle
            )
        }

        binding.searchFragment.setOnClickListener {
            val bundle = Bundle().apply {
                putString("topic", "")
            }
            findNavController().navigate(
                R.id.action_searchTopicsFragment_to_searchFragment,
                bundle
            )
        }


    }

    private fun setUpRecyclerView() {

        binding.topicsRecyclerView.apply {
            topicAdapter = TopicAdapter(requireContext())
            adapter = topicAdapter
            layoutManager = GridLayoutManager(requireContext(), spanCount)
            //init list
            myTopicList = ArrayList()

            //add data
            myTopicList.add(Topics("Sports"))
            myTopicList.add(Topics("Sports"))
            myTopicList.add(Topics("Sports"))
            myTopicList.add(Topics("Sports"))
            myTopicList.add(Topics("Sports"))

            topicAdapter.topics = myTopicList
        }


    }

    private fun observeDarkMode() {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                binding.pageTitle.setTextColor(Color.parseColor("#131313"))
            } // Light mode is active

            Configuration.UI_MODE_NIGHT_YES -> {
                binding.searchFragment.setImageResource(R.drawable.ic_baseline_search_dark_24)
            } // Night mode is active
        }
    }

}