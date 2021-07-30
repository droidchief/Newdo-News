package com.example.newdo.ui.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.bumptech.glide.Glide
import com.example.newdo.R
import com.example.newdo.database.model.Article
import com.example.newdo.databinding.FragmentArticleBinding
import com.example.newdo.ui.MainActivity
import com.example.newdo.ui.menu.SettingsActivity
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

        showPopupMenu(view)


    }

    private fun showPopupMenu(view: View) {
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
                        if (article.url != null) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("${article.url}"))
                            startActivity(intent)
                        }

                        true
                    }

                    R.id.copyLink -> {
                        if (article.url != null) {
                            copyToClipboard()
                        }

                        true
                    }

                    R.id.shareArticle -> {
                        if (article.url != null) {
                            shareArticleToOtherApps()
                        }

                        true
                    }

                    R.id.settings -> {
                        startActivity(Intent(requireContext(), SettingsActivity::class.java))

                        true
                    }

                    else -> false
                }

            }

            popupMenu.inflate(R.menu.article_menu)
            popupMenu.show()
        }

    }

    private fun shareArticleToOtherApps() {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, article.url)
        intent.type = "text/plain"

        startActivity(Intent.createChooser(intent, "Share to :"))
    }

    private fun copyToClipboard() {

        val clipboard =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Page Url", article.url.toString())

        clipboard.setPrimaryClip(clip)

        Toast.makeText(requireContext(), "Copied", Toast.LENGTH_SHORT).show()

    }

    private fun loadWebView(view: View) {
        //pass news detail
        Glide.with(this).load(article.urlToImage).into(binding.articleImage)
        if (article.author != null) binding.author.text =
            "Author - ${article.author}" else binding.author.text = "No Aurthor"

        //setup web view
        binding.webView.apply {
            val webSettings = settings
            webSettings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()

            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_NO -> {
                    if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                        WebSettingsCompat.setForceDark(
                            webSettings,
                            WebSettingsCompat.FORCE_DARK_OFF
                        )
                    }
                } // Light mode is active

                Configuration.UI_MODE_NIGHT_YES -> {
                    if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                        WebSettingsCompat.setForceDark(webSettings, WebSettingsCompat.FORCE_DARK_ON)
                    }
                } // Night mode is active
            }



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