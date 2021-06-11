package com.citrus.digitalsignage.view


import android.view.View
import androidx.fragment.app.activityViewModels
import com.citrus.digitalsignage.databinding.FragmentB1Binding
import com.citrus.digitalsignage.model.vo.DataBean
import com.citrus.digitalsignage.util.BaseFragment
import com.citrus.digitalsignage.viewmodel.BlockStatus
import com.citrus.digitalsignage.viewmodel.SharedViewModel
import com.citrus.digitalsignage.viewmodel.ShowType
import com.youth.banner.Banner
import com.youth.banner.adapter.BannerImageAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class B1Fragment : BaseFragment<FragmentB1Binding>(FragmentB1Binding::inflate) {
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun initView() {
        binding.youtubeView.visibility = View.GONE
        binding.webView.visibility = View.GONE
        binding.bannerView.visibility = View.GONE
        binding.menuView.visibility = View.GONE
    }

    override fun initObserver() {
        sharedViewModel.p1.observe(viewLifecycleOwner, { status ->
            when (status) {
                is BlockStatus.StatusInfo -> {
                    when (status.type) {
                        ShowType.VIDEO -> {
                            showVideo(binding.youtubeView, status.param as String)
                        }
                        ShowType.URL -> {
                            showWebView(binding.webView, status.param as String)
                        }
                        ShowType.BANNER -> {
                            showBanner(
                                binding.bannerView as Banner<DataBean, BannerImageAdapter<DataBean>>,
                                status.param as List<DataBean>
                            )
                        }
                        ShowType.IMG -> {
                            showMenu(binding.menuView, status.param as String)
                        }
                    }
                }
            }
        })
    }


}