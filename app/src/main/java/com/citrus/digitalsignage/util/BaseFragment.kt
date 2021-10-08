package com.citrus.digitalsignage.util

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.citrus.digitalsignage.R
import com.citrus.digitalsignage.model.vo.DataBean
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.youth.banner.Banner
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator


typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewBinding>(private val inflate: Inflate<VB>) : Fragment() {

    private var _binding: VB? = null
    val binding get() = _binding!!

    var videoList = mutableListOf<YouTubePlayerView>()


    override fun onDestroyView() {
        for (item in videoList) {
            item.release()
        }
        _binding = null
        super.onDestroyView()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserver()

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack(R.id.mainFragment, false)
                }
            })
    }

    abstract fun initView()
    abstract fun initObserver()


    fun showMenu(imageView: ImageView, url: String) {
        imageView.visibility = View.VISIBLE
        imageView.scaleType = ImageView.ScaleType.FIT_XY

        Glide.with(requireContext())
            .load(Constants.IMG_DOMAIN + url)
            .into(imageView)
    }


    fun showBanner(
        myBanner: Banner<DataBean, BannerImageAdapter<DataBean>>,
        imgList: List<DataBean>
    ) {
        myBanner.visibility = View.VISIBLE
        myBanner.setLoopTime(5000)
        var banner: Banner<DataBean, BannerImageAdapter<DataBean>> = myBanner
        banner.setAdapter(object : BannerImageAdapter<DataBean>(imgList) {
            override fun onBindView(
                holder: BannerImageHolder,
                data: DataBean,
                position: Int,
                size: Int
            ) {
                holder.imageView.scaleType = ImageView.ScaleType.FIT_XY
                var url = data.imageUrl!!.replace("\r\n","")

                Log.e("url",url)

                Glide.with(holder.itemView)
                    .load(url.replace("\n",""))
                    .into(holder.imageView)
            }
        }).addBannerLifecycleObserver(viewLifecycleOwner).indicator =
            CircleIndicator(requireContext())
    }


    fun showWebView(webView: WebView, url: String) {
        webView.visibility = View.VISIBLE
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        webView.loadUrl(url)
    }


    fun showVideo(youTubeView: YouTubePlayerView, videoId: String) {
        youTubeView.visibility = View.VISIBLE
        lifecycle.addObserver(youTubeView)

        var paraAry = videoId.split("=")

        youTubeView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(paraAry[1], 0f)
            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
                super.onStateChange(youTubePlayer, state)
                when (state) {
                    PlayerConstants.PlayerState.ENDED -> {
                        youTubePlayer.loadVideo(paraAry[1], 0f)
                    }
                }
            }
        })

        videoList.add(youTubeView)
    }


}

