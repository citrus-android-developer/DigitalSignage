package com.citrus.digitalsignage.view

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import cn.pedant.SweetAlert.SweetAlertDialog
import com.citrus.digitalsignage.BuildConfig
import com.citrus.digitalsignage.R
import com.citrus.digitalsignage.databinding.ActivityMainBinding
import com.citrus.digitalsignage.util.Constants
import com.citrus.digitalsignage.util.DownloadStatus
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
    private lateinit var binding: ActivityMainBinding
    private var currentApiVersion: Int = 0
    private val sharedViewModel: SharedViewModel by viewModels()

    private var storagePermissionGranted = false
    private var installPermissionGranted = false
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var mProgressDialog: ProgressDialog


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
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initProgressDialog()

        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                storagePermissionGranted =
                    permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE]
                        ?: storagePermissionGranted
                installPermissionGranted =
                    permissions[Manifest.permission.REQUEST_INSTALL_PACKAGES]
                        ?: installPermissionGranted

                if (!installPermissionGranted) {
                    startInstallPermissionSettingActivity()
                    return@registerForActivityResult
                }

                if (storagePermissionGranted) {
                    updateDialog()
                }
            }

        sharedViewModel.layoutID.observe(this, {
            binding.progressBar.visibility = View.GONE
            when (it) {
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
                LayoutID.Err -> {
                    AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("????????????????????????")
                        .setPositiveButton("OK") { _, _ ->

                        }
                        .create()
                        .show()
                }
            }
        })

        sharedViewModel.isLoading.observe(this, {
            binding.progressBar.visibility = View.VISIBLE
        })

        sharedViewModel.triggerUpdate.observe(this, {
            if (isPermissionGranted()) {
                updateDialog()
            }
        })

        sharedViewModel.downloadStatus.observe(this, { event ->
            when (event) {
                is DownloadStatus.Success -> {
                    mProgressDialog.dismiss()

                    val apkUri = FileProvider.getUriForFile(
                        this@MainActivity,
                        BuildConfig.APPLICATION_ID + ".provider",
                        getFile()
                    )
                    val install = Intent(Intent.ACTION_INSTALL_PACKAGE)
                    install.data = apkUri
                    install.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    startActivity(install)
                }
                is DownloadStatus.Error -> {
                    mProgressDialog.dismiss()
                    showAlertDialog(this, "????????????", event.message)
                }
                is DownloadStatus.Progress -> {
                    mProgressDialog.isIndeterminate = false
                    mProgressDialog.progress = event.progress
                }
            }
        })


    }

    private fun initProgressDialog() {
        mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("??????????????????")
        mProgressDialog.isIndeterminate = true
        mProgressDialog.max = 100
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        mProgressDialog.setCancelable(false)
        mProgressDialog.setButton(
            Dialog.BUTTON_NEGATIVE,
            "??????"
        ) { _: DialogInterface?, _: Int ->
            sharedViewModel.cancelUpdateJob()
        }
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
        dialog.setTitleText("??????????????????").setCustomView(item)
            .hideConfirmButton().show()
        btnOk.setOnClickListener {
            if (etCode.text.toString() != "") {
                downloadApk(etCode.text.toString())
                dialog.dismissWithAnimation()
            } else {
                Toast.makeText(this@MainActivity, "?????????????????????", Toast.LENGTH_SHORT)
            }
        }
        btnCancel.setOnClickListener { v: View? -> dialog.dismissWithAnimation() }
    }

    private fun downloadApk(name: String) {
        sharedViewModel.intentUpdate(getFile(),
            Constants.DOWNLOAD_URL + "digitalSignage_signed_v$name.apk")
        mProgressDialog.show()
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isPermissionGranted(): Boolean {
        val hasStoragePermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val hasInstallPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.REQUEST_INSTALL_PACKAGES
        ) == PackageManager.PERMISSION_GRANTED

        storagePermissionGranted = hasStoragePermission
        installPermissionGranted = hasInstallPermission

        val permissionsToRequest = mutableListOf<String>()
        if (!storagePermissionGranted) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        return if (permissionsToRequest.isNotEmpty()) {
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
            false
        } else {
            true
        }
    }


    fun setInstallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val hasInstallPermission: Boolean =
                this.packageManager.canRequestPackageInstalls()
            if (!hasInstallPermission) {
                startInstallPermissionSettingActivity()
                return
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun startInstallPermissionSettingActivity() {
        val packageURI: Uri = Uri.parse("package:$packageName")
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivityForResult(intent, 888)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 888) {
            Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getFile(): File {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File(
                getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                "digitalSignage.apk"
            )
        } else {
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "digitalSignage.apk"
            )
        }
    }
}
