package com.example.newdo.ui.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
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

        handleOnBackPressed()


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
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("${article.url}"))
                        startActivity(intent)

                        true
                    }

                    R.id.copyLink -> {
                        copyToClipboard()
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

    private fun copyToClipboard() {

        val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Page Url", article.url.toString())

        clipboard.setPrimaryClip(clip)

        Toast.makeText(requireContext(), "Copied", Toast.LENGTH_SHORT).show()

    }

    private fun loadWebView(view: View) {
        //pass news detail
        Glide.with(this).load(article.urlToImage).into(binding.articleImage)
        binding.author.text = "Author - ${article.author}"

        //setup web view
        binding.webView.apply {
            val webSettings = settings
            webSettings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()

            if (article.url != null) {
                loadUrl(article.url!!)
            }

            //bug causing crash: NullPointerException
//            webViewClient = object : WebViewClient() {
//                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
//                    //show loading progress
//                    binding.progressIndicator.visibility = View.VISIBLE
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        binding.progressIndicator.setProgress(30, true)
//                    } else {
//                        binding.progressIndicator.progress = 30
//                    }
//
//                    super.onPageStarted(view, url, favicon)
//                }
//
//                override fun onPageFinished(view: WebView?, url: String?) {
//                    binding.progressIndicator.progress = 100
//                    binding.progressIndicator.visibility = View.INVISIBLE
//                    super.onPageFinished(view, url)
//                }
//
//            }

        }

        binding.saveArticleBtn.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view, "Saved Successfully", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun handleOnBackPressed() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    // Do custom work here
                    if (binding.webView.canGoBack()) {
                        binding.webView.goBack()
                    } else {
                        if (isEnabled) {
                            isEnabled = false
                            requireActivity().onBackPressed()
                        }

                    }

                }
            }
            )

    }

}