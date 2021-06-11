package com.citrus.digitalsignage.view

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import cn.pedant.SweetAlert.SweetAlertDialog
import com.citrus.digitalsignage.BuildConfig
import com.citrus.digitalsignage.R
import com.citrus.digitalsignage.di.MyApplication
import com.citrus.digitalsignage.util.apkDownload.DownloadTask
import com.citrus.digitalsignage.viewmodel.LayoutID
import com.citrus.digitalsignage.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.*


enum class BackStackTrace {
    EXIST, NOT_EXIST
}

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var currentApiVersion: Int = 0
    private val sharedViewModel: SharedViewModel by viewModels()


    override fun onResume() {
        super.onResume()
        currentApiVersion = Build.VERSION.SDK_INT
        val flags: Int = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            window.decorView.systemUiVisibility = flags
            val decorView: View = window.decorView
            decorView.setOnSystemUiVisibilityChangeListener { visibility ->
                if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN === 0) {
                    decorView.systemUiVisibility = flags
                }
            }
        }
        MyApplication().setCurrentPage(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController: NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment?
        navController = navHostFragment!!.navController



        sharedViewModel.layoutID.observe(this,{
            Log.e("trigger",it.name)
            when(it){
                LayoutID.A01 -> {
                    navigateToTarget(R.id.b1Fragment)
                }
                LayoutID.A02 -> {
                    navigateToTarget(R.id.b2Fragment)
                }
                LayoutID.A03 -> {
                    navigateToTarget(R.id.b3Fragment)
                }
                LayoutID.A04 -> {
                    navigateToTarget(R.id.b4Fragment)
                }
                LayoutID.A05 -> {
                    navigateToTarget(R.id.b5Fragment)
                }
            }
        })

        sharedViewModel.triggerUpdate.observe(this,{
            updateDialog()
        })


    }

    private fun navigateToTarget(id: Int) {
        var navController = findNavController(R.id.fragment)
        when (navController.getBackStackTrace(id)) {
            BackStackTrace.EXIST -> {
                navController.popBackStack(id, false)
            }
            BackStackTrace.NOT_EXIST -> {
                navController.navigate(id, null, navOptions {
                    anim {
                        enter = android.R.animator.fade_in
                        exit = android.R.animator.fade_out
                        popEnter = android.R.animator.fade_in
                        popExit = android.R.animator.fade_out
                    }
                })
            }
        }
    }

    private fun NavController.getBackStackTrace(destinationId: Int): BackStackTrace {
        return try {
            getBackStackEntry(destinationId)
            BackStackTrace.EXIST
        } catch (e: Exception) {
            BackStackTrace.NOT_EXIST
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val v = currentFocus
        if (v != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) &&
            v is EditText || v is AutoCompleteTextView
        ) {
            val sourceCoordinates = IntArray(2)
            v.getLocationOnScreen(sourceCoordinates)
            val x = ev.rawX + v.left - sourceCoordinates[0]
            val y = ev.rawY + v.top - sourceCoordinates[1]
            if (x < v.left || x > v.right || y < v.top || y > v.bottom) {
                hideKeyboard(this)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun hideKeyboard(activity: Activity?) {
        if (activity != null && activity.window != null) {
            activity.window.decorView
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
            findViewById<View>(android.R.id.content).clearFocus()
        }
    }

    private fun updateDialog() {
        val item: View = LayoutInflater.from(this@MainActivity).inflate(
            R.layout.dialog_setting_code,
            null
        )
        val etCode = item.findViewById<EditText>(R.id.etCode)
        val btnOk = item.findViewById<Button>(R.id.btn_ok)
        val btnCancel = item.findViewById<Button>(R.id.btn_cancel)
        etCode.isFocusable = true
        etCode.isFocusableInTouchMode = true
        etCode.requestFocus()
        //        etCode.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        val dialog = SweetAlertDialog(this)
        Objects.requireNonNull(dialog.window)
            ?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.setTitleText("輸入版本號碼").setCustomView(item)
            .hideConfirmButton().show()
        btnOk.setOnClickListener {
            if (etCode.text.toString() != "") {
                downloadApk(etCode.text.toString())
                dialog.dismissWithAnimation()
            } else {
                Toast.makeText(this@MainActivity, "請輸入版本號碼", Toast.LENGTH_SHORT)
            }
        }
        btnCancel.setOnClickListener { v: View? -> dialog.dismissWithAnimation() }
    }

    private fun downloadApk(name: String) {

        // instantiate it within the onCreate method
        val mProgressDialog = ProgressDialog(this@MainActivity)
        mProgressDialog.setMessage("正在下載更新")
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        mProgressDialog.setCancelable(false)


        // execute this when the downloader must be fired
        val downloadTask = DownloadTask(this@MainActivity, mProgressDialog) { o ->
            if (o != null) {
                showAlertDialog(this, "發生錯誤", o.toString())
            } else {
                Toast.makeText(this, "下載成功", Toast.LENGTH_SHORT)
                    .show()
                val path =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(path, "digitalSignage.apk")
                val apkUri = FileProvider.getUriForFile(
                    this@MainActivity,
                    BuildConfig.APPLICATION_ID + ".provider",
                    file
                )
                val install = Intent(Intent.ACTION_INSTALL_PACKAGE)
                install.data = apkUri
                install.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                startActivity(install)
            }
        }
        mProgressDialog.setButton(
            Dialog.BUTTON_NEGATIVE,
            "取消"
        ) { _: DialogInterface?, _: Int ->
            downloadTask.cancel(
                true
            )
        }
        downloadTask.execute("http://hq.citrus.tw/apk/digitalSignage_signed_v$name.apk")
    }

    private fun showAlertDialog(context: Context?, title: String?, message: String) {
        val item: View = LayoutInflater.from(context).inflate(R.layout.dialog_alert_download, null)
        val btnSubmit = item.findViewById<Button>(R.id.btn_submit)
        val tvContent = item.findViewById<TextView>(R.id.tv_contentLeft)
        if (message == "") tvContent.visibility = View.GONE
        tvContent.text = message
        val sweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
        sweetAlertDialog.setCanceledOnTouchOutside(false)
        sweetAlertDialog.setOnKeyListener { _, keyCode, _ ->
            if (keyCode === KeyEvent.KEYCODE_BACK) {
                // do nothing
            }
            true
        }
        sweetAlertDialog.setTitleText(title)
            .setCustomView(item)
            .hideConfirmButton()
            .show()
        btnSubmit.setOnClickListener { sweetAlertDialog.dismissWithAnimation() }
    }
}