package com.citrus.digitalsignage


import android.view.View
import com.citrus.digitalsignage.databinding.FragmentTestBinding
import com.citrus.digitalsignage.model.vo.DataBean
import com.citrus.digitalsignage.util.BaseFragment
import com.youth.banner.Banner
import com.youth.banner.adapter.BannerImageAdapter


class TestFragment : BaseFragment<FragmentTestBinding>(FragmentTestBinding::inflate){
    override fun initView() {
        binding.p1YoutubeView.visibility = View.GONE
        binding.p1WebView.visibility = View.GONE
        binding.p1BannerView.visibility = View.GONE
        binding.p1MenuView.visibility = View.GONE

        binding.p2YoutubeView?.visibility = View.GONE
        binding.p2WebView?.visibility = View.GONE
        binding.p2BannerView?.visibility = View.GONE
        binding.p2MenuView?.visibility = View.GONE

        binding.p3YoutubeView?.visibility = View.GONE
        binding.p3WebView?.visibility = View.GONE
        binding.p3BannerView?.visibility = View.GONE
        binding.p3MenuView?.visibility = View.VISIBLE

        binding.p4YoutubeView?.visibility = View.GONE
        binding.p4WebView?.visibility = View.GONE
        binding.p4BannerView?.visibility = View.GONE
        binding.p4MenuView?.visibility = View.VISIBLE
    }

    override fun initObserver() {
        showVideo(binding.p1YoutubeView, "https://www.youtube.com/watch?v=Y8Nw6i-V5rI")
        showWebView(binding.p2WebView,"http://cms.citrus.tw/taione/Orderready/#/M9g0T7TtAseK3zKsSgndLw%3D%3D")
    }
}