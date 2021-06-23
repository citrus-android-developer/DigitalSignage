package com.citrus.digitalsignage.view

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.citrus.digitalsignage.BuildConfig
import com.citrus.digitalsignage.R
import com.citrus.digitalsignage.databinding.FragmentMainBinding
import com.citrus.digitalsignage.di.prefs
import com.citrus.digitalsignage.viewmodel.SharedViewModel


class MainFragment : Fragment(R.layout.fragment_main) {
    private val sharedViewModel: SharedViewModel by activityViewModels()


    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)
        initView()
        initObserver()
    }

    private fun initObserver() {
        sharedViewModel.error.observe(viewLifecycleOwner,{
            AlertDialog.Builder(requireContext())
                .setTitle("Error")
                .setMessage("查無結果，請確認輸入資訊是否正確")
                .setPositiveButton("OK"){ _,_ ->

                }
                .create()
                .show()
        })

    }

    private fun initView() {
        binding.apply {
            loadPrefsInfo()

            tvVersion?.text = "2021 © Citrus Solutions Co., Ltd. Version " + BuildConfig.VERSION_NAME

            btnOK?.setOnClickListener {
                val serverIP =  serverIP?.text!!.trimStr()
                val storeID = storeID?.text!!.trimStr()
                val deviceID = deviceID?.text!!.trimStr()

                if(inputCheck(serverIP,storeID,deviceID)){
                    sharedViewModel.isBackSetting = 1
                    prefs.serverIP = serverIP
                    prefs.storeID = storeID
                    prefs.deviceID = deviceID

                    sharedViewModel.fetchLayoutInfo()
                }
            }


            apkDownLoad?.setOnClickListener {
                sharedViewModel.intentUpdate()
            }
        }
    }



    private fun loadPrefsInfo() {
        binding.serverIP?.setText(prefs.serverIP,false)
        binding.storeID?.setText(prefs.storeID,false)
        binding.deviceID?.setText(prefs?.deviceID,false)
    }


    private fun inputCheck(serverIP:String, storeID:String, deviceID: String): Boolean{
        return!(TextUtils.isEmpty(serverIP) && TextUtils.isEmpty(storeID) && TextUtils.isEmpty(deviceID))
    }

    private fun Editable.trimStr() = this.toString().replace(" ","")

}