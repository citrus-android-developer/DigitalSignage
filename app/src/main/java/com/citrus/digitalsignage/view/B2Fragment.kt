package com.citrus.digitalsignage.view


import android.view.View
import androidx.fragment.app.activityViewModels
import com.citrus.digitalsignage.databinding.FragmentB2Binding
import com.citrus.digitalsignage.model.vo.DataBean
import com.citrus.digitalsignage.util.BaseFragment
import com.citrus.digitalsignage.viewmodel.BlockStatus
import com.citrus.digitalsignage.viewmodel.SharedViewModel
import com.citrus.digitalsignage.viewmodel.ShowType
import com.youth.banner.Banner
import com.youth.banner.adapter.BannerImageAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class B2Fragment : BaseFragment<FragmentB2Binding>(FragmentB2Binding::inflate) {
    private val sharedViewModel: SharedViewModel by activityViewModels()


    override fun initView() {
        binding.p1YoutubeView.visibility = View.GONE
        binding.p1WebView.visibility = View.GONE
        binding.p1BannerView.visibility = View.GONE
        binding.p1MenuView.visibility = View.GONE

        binding.p2YoutubeView?.visibility = View.GONE
        binding.p2WebView?.visibility = View.GONE
        binding.p2BannerView?.visibility = View.GONE
        binding.p2MenuView?.visibility = View.GONE
    }

    override fun initObserver() {
        sharedViewModel.p1.observe(viewLifecycleOwner, { status ->
            when (status) {
                is BlockStatus.StatusInfo -> {
                    when (status.type) {
                        ShowType.VIDEO -> {
                            showVideo(binding.p1YoutubeView, status.param as String)
                        }
                        ShowType.URL -> {
                            showWebView(binding.p1WebView, status.param as String)
                        }
                        ShowType.BANNER -> {
                            showBanner(
                                binding.p1BannerView as Banner<DataBean, BannerImageAdapter<DataBean>>,
                                status.param as List<DataBean>
                            )
                        }
                        ShowType.IMG -> {
                            showMenu(binding.p1MenuView, status.param as String)
                        }
                    }
                }
            }
        })

        sharedViewModel.p2.observe(viewLifecycleOwner, { status ->
            when (status) {
                is BlockStatus.StatusInfo -> {
                    when (status.type) {
                        ShowType.VIDEO -> {
                            showVideo(binding.p2YoutubeView, status.param as String)
                        }
                        ShowType.URL -> {
                            showWebView(binding.p2WebView, status.param as String)
                        }
                        ShowType.BANNER -> {
                            showBanner(
                                binding.p2BannerView as Banner<DataBean, BannerImageAdapter<DataBean>>,
                                status.param as List<DataBean>
                            )
                        }
                        ShowType.IMG -> {
                            showMenu(binding.p2MenuView, status.param as String)
                        }
                    }
                }
            }
        })
    }


}