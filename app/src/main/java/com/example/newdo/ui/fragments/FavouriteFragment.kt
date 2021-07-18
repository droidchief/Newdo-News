package com.example.newdo.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.newdo.R
import com.example.newdo.databinding.FragmentArticleBinding
import com.example.newdo.databinding.FragmentFavouriteBinding
import com.example.newdo.ui.MainActivity
import com.example.newdo.ui.viewmodels.NewsViewModel

class FavouriteFragment: Fragment(R.layout.fragment_favourite) {

    private lateinit var binding: FragmentFavouriteBinding
    lateinit var viewModel: NewsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavouriteBinding.bind(view)

        viewModel = (activity as MainActivity).viewModel

    }
}