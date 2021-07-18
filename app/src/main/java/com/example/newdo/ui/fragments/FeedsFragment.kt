package com.example.newdo.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.newdo.R
import com.example.newdo.databinding.FragmentArticleBinding
import com.example.newdo.databinding.FragmentFavouriteBinding
import com.example.newdo.databinding.FragmentFeedBinding
import com.example.newdo.ui.MainActivity
import com.example.newdo.ui.viewmodels.NewsViewModel

class FeedsFragment: Fragment(R.layout.fragment_feed) {

    private lateinit var binding: FragmentFeedBinding
    lateinit var viewModel: NewsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFeedBinding.bind(view)

        viewModel = (activity as MainActivity).viewModel

    }
}