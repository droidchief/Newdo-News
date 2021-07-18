package com.example.newdo.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.newdo.R
import com.example.newdo.databinding.FragmentFeedBinding
import com.example.newdo.databinding.FragmentSearchBinding
import com.example.newdo.ui.MainActivity
import com.example.newdo.ui.viewmodels.NewsViewModel

class SearchFragment: Fragment(R.layout.fragment_search) {

    private lateinit var binding: FragmentSearchBinding
    lateinit var viewModel: NewsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)

        viewModel = (activity as MainActivity).viewModel

    }
}