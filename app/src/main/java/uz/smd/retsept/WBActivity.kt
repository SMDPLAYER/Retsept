package uz.smd.retsept

import android.annotation.TargetApi
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isGone
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_wb.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

var BASE_URL = ""
class WBActivity : AppCompatActivity() {

    private val webView: WebView by lazy { findViewById(R.id.viewHolder) }
    private val progressBar: View by lazy { findViewById(R.id.progressBar) }
    var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    var mCameraPhotoPath: String? = null
    val PERMISSION_CODE = 1000
    var size: Long = 0
    var liveProgress = MutableLiveData<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wb)
        initUI()
        setUI()
        liveProgress.observe(this) {
            progressBar.isGone = it
        }
    }

    private fun initUI() {
        supportActionBar?.hide()
    }

    private fun setUI() {
        liveProgress.value = false
//        progressBar.visibility = View.VISIBLE
        configureWebView()
        if (!isInternetAvailable(this)) {
            showAlert(this, BASE_URL)
        } else {
            webView.isGone = false
            webView.loadUrl(intent.getStringExtra(EXTRA_TASK_URL) ?: "")
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
    }

    private fun verifyStoragePermissions(activity: Activity) {

        val writePermission =
            ActivityCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        val readPermission =
            ActivityCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        val cameraPermission =
            ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA)

        val permission = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED || cameraPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, permission, PERMISSION_CODE)
        }
    }

    inner class PQChromeClient : WebChromeClient() {


        override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
            super.onShowCustomView(view, callback)
            viewHolder.visibility = View.GONE
            customView.visibility = View.VISIBLE
            customView.addView(view)
        }

        override fun onHideCustomView() {
            super.onHideCustomView()
            viewHolder.visibility = View.VISIBLE
            customView.visibility = View.GONE
        }


        override fun onShowFileChooser(
            view: WebView,
            filePath: ValueCallback<Array<Uri>>,
            fileChooserParams: FileChooserParams,
        ): Boolean {
            mFilePathCallback?.onReceiveValue(null)
            mFilePathCallback = filePath
            Log.e("FileCooserParams => ", filePath.toString())
            var takePictureIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent != null) {
                if (takePictureIntent.resolveActivity(packageManager) != null) {
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath)
                    } catch (ex: IOException) {
                        Log.e("aga", "Unable to create Image File", ex)
                    }
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.absolutePath
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
                    } else {
                        takePictureIntent = null
                    }
                }
            }
            val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
            contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            contentSelectionIntent.type = "image/*"

            val pickIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"
            val chooserIntent = Intent.createChooser(contentSelectionIntent, "Select Image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
            startActivityForResult(pickIntent, 1)
            return true
        }
    }

    private fun configureWebView() {
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.settings.domStorageEnabled = true
        webView.setInitialScale(1)
//        webView.settings.setAppCacheEnabled(true)
        webView.settings.allowFileAccess = true
        webView.settings.javaScriptEnabled = true
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
        /*    if (Build.VERSION.SDK_INT >= 21) {
                val cookieManager = CookieManager.getInstance()
                cookieManager.setAcceptThirdPartyCookies(webView, true)
                cookieManager.flush()
                cookieManager.setAcceptThirdPartyCookies(webView, true) TODO COOKIES
            }*/
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.webChromeClient = PQChromeClient()
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webView.settings.domStorageEnabled = true
        webView.settings.databaseEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webView.settings.safeBrowsingEnabled = true
        }
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return if (
                    url.startsWith("mailto://") ||
                    url.startsWith("tel://") ||
                    url.startsWith("line://") ||
                    url.startsWith("whatsapp://") ||
                    url.startsWith("viber://") ||
                    url.startsWith("tg://") ||
                    url.startsWith("tg:resolve")
                ) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(url)
                        startActivity(intent)
                        true
                    } catch (e: ActivityNotFoundException) {
                        loadUrl(view, url)
//                        false
                    } catch (e: Exception) {
                        loadUrl(view, url)
//                        false
                    }
                } else {
//                    Prefs.url = (url)
                    loadUrl(view, url)
//                    false
                }
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                loadUrl(view, BASE_URL)
                super.onReceivedError(view, request, error)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                url?.let { urlSafe ->
                }
                lifecycleScope.launch {
                    delay(1000)
                    liveProgress.postValue(true)
                }
//                progressBar.visibility = View.GONE
            }
        }
        verifyStoragePermissions(this)
    }


    fun loadUrl(view: WebView, url: String): Boolean {
        return if (!isInternetAvailable(this@WBActivity)) {
            webView.isGone = true
            showAlert(this@WBActivity, url)
            true
        } else {
            webView.isGone = false
            view.loadUrl(url)
            false
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        webView.onPause()
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val bundle = Bundle()
        webView.saveState(bundle)
        outState.putBundle("webViewState", bundle)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        webView.restoreState(savedInstanceState)
        (savedInstanceState.getParcelable<Bundle?>("webViewState"))?.let {
            super.onRestoreInstanceState(it)
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null || mCameraPhotoPath != null) {
            var count = 0
            var images: ClipData? = null
            try {
                images = data?.clipData
            } catch (e: Exception) {
                Log.e("Error!", e.localizedMessage)
            }
            if (images == null && data != null && data.dataString != null) {
                count = data.dataString!!.length
            } else if (images != null) {
                count = images.getItemCount()
            }
            var results = arrayOfNulls<Uri>(count)
            if (resultCode == Activity.RESULT_OK) {
                if (size != 0L) {
                    if (mCameraPhotoPath != null) {
                        results = arrayOf(Uri.parse(mCameraPhotoPath))
                    }
                } else if (data != null) {
                    if (data.clipData == null) {
                        results = arrayOf(Uri.parse(data.dataString))
                    } else {
                        if (images != null) {
                            for (i in 0 until images.itemCount) {
                                results[i] = images.getItemAt(i).uri
                            }
                        }
                    }
                }
            }
            mFilePathCallback?.onReceiveValue(results as Array<Uri>)
            mFilePathCallback = null
        }
    }

    companion object {
        private const val EXTRA_TASK_URL = "task_url"

        fun openWB(context: Context, url: String) {
            val intentWb = Intent(context, WBActivity::class.java)
            intentWb.putExtra(EXTRA_TASK_URL, url)
            intentWb.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intentWb)
        }
    }
}