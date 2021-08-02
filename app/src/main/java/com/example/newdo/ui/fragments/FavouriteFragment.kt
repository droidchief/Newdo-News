package com.example.newdo.ui.fragments

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newdo.R
import com.example.newdo.adapters.MenuAdapter
import com.example.newdo.adapters.NewsAdapter
import com.example.newdo.database.model.Menu
import com.example.newdo.databinding.FragmentFavouriteBinding
import com.example.newdo.ui.MainActivity
import com.example.newdo.ui.menu.SettingsActivity
import com.example.newdo.ui.viewmodels.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class FavouriteFragment: Fragment(R.layout.fragment_favourite) {

    private lateinit var binding: FragmentFavouriteBinding

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var myMenuList: ArrayList<Menu>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavouriteBinding.bind(view)

        viewModel = (activity as MainActivity).viewModel

        observeDarkMode()

        setUpRecyclerView()
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
                        R.id.action_favouriteFragment_to_countryTopTenFragment,
                        bundle
                    )
                }

                1 -> findNavController().navigate(R.id.action_favouriteFragment_to_storiesFragment)


                3 -> startActivity(Intent(requireContext(), SettingsActivity::class.java))

            }
        }



        //pass data to the article page
        newsAdapter.setOnArticleClickListener { clickedArticle ->
            val bundle = Bundle().apply {
                putSerializable("article", clickedArticle)
            }

            findNavController().navigate(
                R.id.action_favouriteFragment2_to_articleFragment,
                bundle
            )
        }

        //swipe to delete article
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]

                viewModel.deleteArticle(article)
                Snackbar.make(view, "Removed Successfully", Snackbar.LENGTH_SHORT).apply {
                    setAction("Undo") {
                        viewModel.saveArticle(article)
                    }

                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
             attachToRecyclerView(binding.favouriteRecyclerView)
        }

        //observes changes in database
        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { articles ->
            if (articles.isEmpty()) {
                binding.emptyFavouriteLayout.visibility = View.VISIBLE
                Glide.with(requireContext()).load(R.drawable.rocket).into(binding.emptyFavouriteGif)
            }else {
                binding.emptyFavouriteLayout.visibility = View.GONE
            }

            newsAdapter.differ.submitList(articles)
        })

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


    private fun openMenu() {
        if (!binding.menuLayout.isVisible) {
            val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right)
            binding.menuLayout.animation = anim
            binding.menuLayout.visibility = View.VISIBLE
        }
    }

    private fun closeMenu() {
        if (binding.menuLayout.isVisible) {
            val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_right)
            binding.menuLayout.animation = anim
            binding.menuLayout.visibility = View.INVISIBLE
        }

    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter(requireContext())
        binding.favouriteRecyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            setPadding(0,0,0,0)
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