package com.example.newdo.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.newdo.R
import com.example.newdo.database.model.Article
import com.example.newdo.databinding.FragmentArticleBinding
import com.example.newdo.ui.MainActivity
import com.example.newdo.ui.viewmodels.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private lateinit var binding: FragmentArticleBinding

    lateinit var viewModel: NewsViewModel
    val args: ArticleFragmentArgs by navArgs()

    private lateinit var article: Article

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)

        viewModel = (activity as MainActivity).viewModel

        //get current article passed from previous page
        article = args.article


        loadWebView(view)

        //popup menu
        binding.menuBtn.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.refresh -> {

                        loadWebView(view)

                        true
                    }

                    R.id.openInBrowser -> {
                        Toast.makeText(requireContext(), "Open in Browser", Toast.LENGTH_SHORT)
                            .show()
                        true
                    }

                    R.id.copyLink -> {
                        Toast.makeText(requireContext(), "Copy Link", Toast.LENGTH_SHORT)
                            .show()
                        true
                    }

                    R.id.shareArticle -> {
                        Toast.makeText(requireContext(), "Share", Toast.LENGTH_SHORT)
                            .show()
                        true
                    }

                    else -> false
                }

            }

            popupMenu.inflate(R.menu.article_menu)
            popupMenu.show()
        }

    }

    private fun loadWebView(view: View) {
        //pass news detail
        Glide.with(this).load(article.urlToImage).into(binding.articleImage)
        binding.author.text = "Author - ${article.author}"

        //setup web view
        binding.webView.apply {
            webViewClient = WebViewClient()
            if (article.url != null) {
                loadUrl(article.url!!)
            }
        }

        //show loading progress
        val progress = binding.webView.progress

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.progressIndicator.setProgress(progress, true)
        } else {
            binding.progressIndicator.progress = progress
        }

        binding.saveArticleBtn.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view, "Saved Successfully", Snackbar.LENGTH_SHORT).show()
        }
    }
}