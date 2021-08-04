package com.example.newdo.ui.fragments

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.example.newdo.R
import com.example.newdo.adapters.StoriesAdapter
import com.example.newdo.database.model.Story
import com.example.newdo.databinding.FragmentStoriesBinding
import com.example.newdo.ui.MainActivity
import com.example.newdo.ui.viewmodels.NewsViewModel
import com.example.newdo.utils.Constants.Companion.YOUTUBE_AUDIO_TAG
import com.example.newdo.utils.Constants.Companion.YOUTUBE_I_TAG
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util

class StoriesFragment : Fragment(R.layout.fragment_stories) {

    //hard code demo urls temporally
    private lateinit var binding: FragmentStoriesBinding

    lateinit var viewModel: NewsViewModel
    lateinit var storyAdapter: StoriesAdapter
    private lateinit var myStoryList: ArrayList<Story>

    private var spanCount: Int = 1

    private var exoPlayer: SimpleExoPlayer? = null
    private var playOnReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStoriesBinding.bind(view)

        viewModel = (activity as MainActivity).viewModel

        observeDarkMode()

        setUpRecyclerViews()

        initializePlayer()

        //pass url to the media player
        storyAdapter.setOnStoryClickListener {

        }


    }

    private fun initializePlayer() {
        exoPlayer = SimpleExoPlayer.Builder(requireContext()).build()
        binding.storiesVideoView.player = exoPlayer

        //Phillipp Lackner on YouTube
        val videoLink  = "https://www.youtube.com/watch?v=aIGY1tWekGw"

        object : YouTubeExtractor(requireContext()) {
            override fun onExtractionComplete(
                ytFiles: SparseArray<YtFile>?,
                videoMeta: VideoMeta?
            ) {
                if (ytFiles != null) {
                    val itag = YOUTUBE_I_TAG //Tag of video 1080p. Check out YTFile.java
                    val audioTag = YOUTUBE_AUDIO_TAG //Tag of m4a audio
                    val videoUrl = ytFiles[itag].url
                    val auidoUrl = ytFiles[audioTag].url

                    val audioSource : MediaSource = ProgressiveMediaSource
                        .Factory(DefaultHttpDataSource.Factory())
                        .createMediaSource(MediaItem.fromUri(auidoUrl))

                    val videoSource : MediaSource = ProgressiveMediaSource
                        .Factory(DefaultHttpDataSource.Factory())
                        .createMediaSource(MediaItem.fromUri(videoUrl))

                    exoPlayer!!.setMediaSource(MergingMediaSource(
                        true, videoSource, audioSource
                    ), true)
                    exoPlayer!!.prepare()
                    exoPlayer!!.playWhenReady = playOnReady
                    exoPlayer!!.seekTo(currentWindow, playbackPosition)
                }
            }

        }.extract(videoLink, false, true)

    }

    private fun setUpRecyclerViews() {

        binding.storiesRecyclerView.apply {
            storyAdapter = StoriesAdapter(requireContext())
            adapter = storyAdapter
            layoutManager = GridLayoutManager(requireContext(), spanCount)

            //init list
            myStoryList = ArrayList()

            //add data
            myStoryList.add(
                Story(
                    R.drawable.ic_launcher_background,
                    "Meet the latest news app",
                    "https://r3---sn-5hnednlr.googlevideo.com/videoplayback?expire=1627965692&ei=nHQIYYidK4-P-gaLoJDIDA&ip=88.12.19.206&id=o-AIYLAlSNWie1ZgHgPobNlO3aiR1kyx6IsZXa7NV-VMi9&itag=18&source=youtube&requiressl=yes&vprv=1&mime=video%2Fmp4&ns=ugaM6nQnrGkpakePU8eialEG&gir=yes&clen=2647715&ratebypass=yes&dur=120.697&lmt=1623060508295199&fexp=24001373,24007246&c=WEB&txp=6310222&n=eGNRWqCSfVZXcUE7P&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRAIgdW2JJfdW68DSe-wxP3yWlJN6LAQhk4aJMd8QK-uu3hoCIC-KxP3wxYpvV0SLOAE1hE5qj0VotoH-StfXxNhHIOfD&cm2rm=sn-h5qzy7s&req_id=714761e0c44ba3ee&redirect_counter=2&rm=sn-5hneer7s&cms_redirect=yes&ipbypass=yes&mh=1A&mip=197.210.76.151&mm=34&mn=sn-5hnednlr&ms=ltu&mt=1627944036&mv=m&mvi=3&pl=24&lsparams=ipbypass,mh,mip,mm,mn,ms,mv,mvi,pl&lsig=AG3C_xAwRQIgHUP3n4BwZQnDO5PiTLLrIj36ym8V2EvazCGTJm079RYCIQCAEvuTCHCUCdnhMBFjMQBRpfpTa4zhC2I4QilY1_PAAA%3D%3D"
                )
            )
            myStoryList.add(
                Story(
                    R.drawable.ic_launcher_foreground,
                    "Meet the latest news app",
                    "https://r3---sn-5hnednlr.googlevideo.com/videoplayback?expire=1627965692&ei=nHQIYYidK4-P-gaLoJDIDA&ip=88.12.19.206&id=o-AIYLAlSNWie1ZgHgPobNlO3aiR1kyx6IsZXa7NV-VMi9&itag=18&source=youtube&requiressl=yes&vprv=1&mime=video%2Fmp4&ns=ugaM6nQnrGkpakePU8eialEG&gir=yes&clen=2647715&ratebypass=yes&dur=120.697&lmt=1623060508295199&fexp=24001373,24007246&c=WEB&txp=6310222&n=eGNRWqCSfVZXcUE7P&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRAIgdW2JJfdW68DSe-wxP3yWlJN6LAQhk4aJMd8QK-uu3hoCIC-KxP3wxYpvV0SLOAE1hE5qj0VotoH-StfXxNhHIOfD&cm2rm=sn-h5qzy7s&req_id=714761e0c44ba3ee&redirect_counter=2&rm=sn-5hneer7s&cms_redirect=yes&ipbypass=yes&mh=1A&mip=197.210.76.151&mm=34&mn=sn-5hnednlr&ms=ltu&mt=1627944036&mv=m&mvi=3&pl=24&lsparams=ipbypass,mh,mip,mm,mn,ms,mv,mvi,pl&lsig=AG3C_xAwRQIgHUP3n4BwZQnDO5PiTLLrIj36ym8V2EvazCGTJm079RYCIQCAEvuTCHCUCdnhMBFjMQBRpfpTa4zhC2I4QilY1_PAAA%3D%3D"
                )
            )
            myStoryList.add(
                Story(
                    R.drawable.ic_launcher_background,
                    "Meet the latest news app",
                    "https://r3---sn-5hnednlr.googlevideo.com/videoplayback?expire=1627965692&ei=nHQIYYidK4-P-gaLoJDIDA&ip=88.12.19.206&id=o-AIYLAlSNWie1ZgHgPobNlO3aiR1kyx6IsZXa7NV-VMi9&itag=18&source=youtube&requiressl=yes&vprv=1&mime=video%2Fmp4&ns=ugaM6nQnrGkpakePU8eialEG&gir=yes&clen=2647715&ratebypass=yes&dur=120.697&lmt=1623060508295199&fexp=24001373,24007246&c=WEB&txp=6310222&n=eGNRWqCSfVZXcUE7P&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRAIgdW2JJfdW68DSe-wxP3yWlJN6LAQhk4aJMd8QK-uu3hoCIC-KxP3wxYpvV0SLOAE1hE5qj0VotoH-StfXxNhHIOfD&cm2rm=sn-h5qzy7s&req_id=714761e0c44ba3ee&redirect_counter=2&rm=sn-5hneer7s&cms_redirect=yes&ipbypass=yes&mh=1A&mip=197.210.76.151&mm=34&mn=sn-5hnednlr&ms=ltu&mt=1627944036&mv=m&mvi=3&pl=24&lsparams=ipbypass,mh,mip,mm,mn,ms,mv,mvi,pl&lsig=AG3C_xAwRQIgHUP3n4BwZQnDO5PiTLLrIj36ym8V2EvazCGTJm079RYCIQCAEvuTCHCUCdnhMBFjMQBRpfpTa4zhC2I4QilY1_PAAA%3D%3D"
                )
            )
            myStoryList.add(
                Story(
                    R.drawable.ic_launcher_foreground,
                    "Meet the latest news app",
                    "https://r3---sn-5hnednlr.googlevideo.com/videoplayback?expire=1627965692&ei=nHQIYYidK4-P-gaLoJDIDA&ip=88.12.19.206&id=o-AIYLAlSNWie1ZgHgPobNlO3aiR1kyx6IsZXa7NV-VMi9&itag=18&source=youtube&requiressl=yes&vprv=1&mime=video%2Fmp4&ns=ugaM6nQnrGkpakePU8eialEG&gir=yes&clen=2647715&ratebypass=yes&dur=120.697&lmt=1623060508295199&fexp=24001373,24007246&c=WEB&txp=6310222&n=eGNRWqCSfVZXcUE7P&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRAIgdW2JJfdW68DSe-wxP3yWlJN6LAQhk4aJMd8QK-uu3hoCIC-KxP3wxYpvV0SLOAE1hE5qj0VotoH-StfXxNhHIOfD&cm2rm=sn-h5qzy7s&req_id=714761e0c44ba3ee&redirect_counter=2&rm=sn-5hneer7s&cms_redirect=yes&ipbypass=yes&mh=1A&mip=197.210.76.151&mm=34&mn=sn-5hnednlr&ms=ltu&mt=1627944036&mv=m&mvi=3&pl=24&lsparams=ipbypass,mh,mip,mm,mn,ms,mv,mvi,pl&lsig=AG3C_xAwRQIgHUP3n4BwZQnDO5PiTLLrIj36ym8V2EvazCGTJm079RYCIQCAEvuTCHCUCdnhMBFjMQBRpfpTa4zhC2I4QilY1_PAAA%3D%3D"
                )
            )
            myStoryList.add(
                Story(
                    R.drawable.ic_launcher_foreground,
                    "Meet the latest news app",
                    "https://r3---sn-5hnednlr.googlevideo.com/videoplayback?expire=1627965692&ei=nHQIYYidK4-P-gaLoJDIDA&ip=88.12.19.206&id=o-AIYLAlSNWie1ZgHgPobNlO3aiR1kyx6IsZXa7NV-VMi9&itag=18&source=youtube&requiressl=yes&vprv=1&mime=video%2Fmp4&ns=ugaM6nQnrGkpakePU8eialEG&gir=yes&clen=2647715&ratebypass=yes&dur=120.697&lmt=1623060508295199&fexp=24001373,24007246&c=WEB&txp=6310222&n=eGNRWqCSfVZXcUE7P&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRAIgdW2JJfdW68DSe-wxP3yWlJN6LAQhk4aJMd8QK-uu3hoCIC-KxP3wxYpvV0SLOAE1hE5qj0VotoH-StfXxNhHIOfD&cm2rm=sn-h5qzy7s&req_id=714761e0c44ba3ee&redirect_counter=2&rm=sn-5hneer7s&cms_redirect=yes&ipbypass=yes&mh=1A&mip=197.210.76.151&mm=34&mn=sn-5hnednlr&ms=ltu&mt=1627944036&mv=m&mvi=3&pl=24&lsparams=ipbypass,mh,mip,mm,mn,ms,mv,mvi,pl&lsig=AG3C_xAwRQIgHUP3n4BwZQnDO5PiTLLrIj36ym8V2EvazCGTJm079RYCIQCAEvuTCHCUCdnhMBFjMQBRpfpTa4zhC2I4QilY1_PAAA%3D%3D"
                )
            )


            storyAdapter.stories = myStoryList
        }

    }

    private fun observeDarkMode() {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                binding.pageTitle.setTextColor(Color.parseColor("#131313"))
            } // Light mode is active

            Configuration.UI_MODE_NIGHT_YES -> {
            } // Night mode is active
        }
    }

    override fun onStart() {
        super.onStart()

        if (Util.SDK_INT >= 24) initializePlayer()
    }

    override fun onResume() {
        super.onResume()

        if (Util.SDK_INT < 24 || exoPlayer == null) {
            initializePlayer()
            hideSystemUI()
        }
    }

    private fun hideSystemUI() {
        binding.storiesVideoView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )
    }

    override fun onPause() {
        if (Util.SDK_INT < 24) releaseExoPlayer()

        super.onPause()
    }

    override fun onStop() {
        if (Util.SDK_INT < 24) releaseExoPlayer()

        super.onStop()
    }

    private fun releaseExoPlayer() {
        if (exoPlayer != null) {
            playOnReady = exoPlayer!!.playWhenReady
            playbackPosition = exoPlayer!!.currentPosition
            currentWindow = exoPlayer!!.currentWindowIndex

            exoPlayer!!.release()
            exoPlayer = null
        }
    }
}