//package com.lin.magic.activity
//
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.graphics.drawable.ColorDrawable
//import android.net.Uri
//import android.os.Bundle
//import android.util.Base64
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.webkit.WebResourceRequest
//import android.webkit.WebSettings
//import android.webkit.WebView
//import android.webkit.WebViewClient
//import android.widget.CheckBox
//import androidx.appcompat.app.AppCompatActivity
//import com.google.android.material.snackbar.Snackbar
//import com.lin.magic.AppTheme
//import com.lin.magic.R
//import com.lin.magic.utils.ThemeUtils
//import java.io.IOException
//import java.io.InputStream
//import javax.inject.Inject
//
//
//class ReadabilityActivity: AppCompatActivity() {
//
//    companion object {
//        const val TAG = "ReadabilityActivity"
//        const val ARG_KEY = "default:arg"
//        private fun getIntent(context: Context, url: String): Intent {
//            return Intent(context, ReadabilityActivity::class.java).apply {
//                putExtra(ARG_KEY, url)
//            }
//        }
//
//        fun launch(activity: Activity, url: String) {
//            activity.startActivity(getIntent(activity, url))
//        }
//    }
//
//    private var themeId: AppTheme = AppTheme.LIGHT
//
//    private lateinit var binding: ReadabilityActivityBinding
//
//    @Inject
//    internal lateinit var userPreferences: UserPreferences
//    @Inject
//    internal lateinit var logger: NoOpLogger
////    @Inject
////    lateinit var webViewFactory: WebViewFactory
//
//    private var mTouchX = 0.0f
//    private var mTouchY = 0.0f
//
//    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        injector.inject(this)
//        themeId = userPreferences.useTheme
//
//        // set the theme
//        when (themeId) {
//            AppTheme.LIGHT -> {
//                setTheme(R.style.Theme_SettingsTheme)
//                window.setBackgroundDrawable(ColorDrawable(ThemeUtils.getPrimaryColor(this)))
//            }
//            AppTheme.DARK -> {
//                setTheme(R.style.Theme_SettingsTheme_Dark)
//                window.setBackgroundDrawable(ColorDrawable(ThemeUtils.getPrimaryColorDark(this)))
//            }
//            AppTheme.BLACK -> {
//                setTheme(R.style.Theme_SettingsTheme_Black)
//                window.setBackgroundDrawable(ColorDrawable(ThemeUtils.getPrimaryColorDark(this)))
//            }
//        }
//        super.onCreate(savedInstanceState)
//        binding = ReadabilityActivityBinding.inflate(LayoutInflater.from(this))
//        setContentView(binding.root)
//
//        val url = intent.getStringExtra(ARG_KEY)
//        Log.v("owp", "======>$url")
//        // 配置WebView
//        val webSettings: WebSettings = binding.webView.settings
//        webSettings.javaScriptEnabled = true
//
//        // 设置WebChromeClient，用于处理页面标题等
//        binding.webView.webViewClient = object : WebViewClient() {
//            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
//                val uri = request?.url
//                // 检查URL是否具有一个schema，并且不是http或https
//                if (uri?.scheme != null && uri.scheme != "http" && uri.scheme != "https") {
//                    // 这里填写你的schema，例如 "intent://" 或者 "yourapp://"
//                    showConfirmSnackbar(request.url?.toString() ?: "")
//                    return true
//                }
//                return super.shouldOverrideUrlLoading(view, request)
//            }
//
//            override fun onPageFinished(view: WebView, url: String) {
//                /*val getReaderModeBodyTextJs = """
//                    javascript:(function() {
//                        var documentClone = document.cloneNode(true);
//                        var article = new Readability(documentClone, {classesToPreserve: preservedClasses}).parse();
//                        return article.textContent;
//                    })()
//                """
//
//                evaluateMozReaderModeJs {
//                    binding.webView.evaluateJavascript(getReaderModeBodyTextJs) { text ->
//                        Log.v("owp", "==>${text.substring(1, text.length - 2)}")
//                        preloadNext()
//                    }
//                }*/
//                val js = """
//                    var script = document.createElement('script');
//                    script.type = 'text/javascript';
//                    script.src = '//cdn.bootcss.com/eruda/1.4.2/eruda.min.js';
//                    document.body.appendChild(script);
//                    script.onload = function() { eruda.init(); };
//                """
//                binding.webView.evaluateJavascript(js) {
//                    binding.webView.postDelayed({preloadNext()}, 5000)
//                }
//            }
//        }
//
//        binding.webView.loadUrl(url ?: "")
//    }
//
//    private fun showConfirmSnackbar(url: String) {
//        Snackbar.make(
//            binding.webView,
//            R.string.prohibit_redirects_app_tip,
//            Snackbar.LENGTH_INDEFINITE
//        ).setAction(R.string.action_open) {
//            navigateToExternalApp(url)
//        }.show()
//    }
//
//    private fun navigateToExternalApp(url: String) {
//        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//        startActivity(intent)
//    }
//
//    private fun evaluateMozReaderModeJs(postAction: (() -> Unit)? = null) {
//        val cssByteArray = getByteArrayFromAsset("readerview/ReaderView.css")
//        injectCss(cssByteArray)
//
//        val jsString = getStringFromAsset("readerview/Readability.js")
//        binding.webView.evaluateJavascript(jsString) {
//            binding.webView.evaluateJavascript("javascript:(function() { window.scrollTo(0, 0); })()", null)
//            postAction?.invoke()
//        }
//    }
//
//    private fun injectCss(bytes: ByteArray) {
//        try {
//            val encoded = Base64.encodeToString(bytes, Base64.NO_WRAP)
//            binding.webView.loadUrl(
//                "javascript:(function() {" +
//                        "var parent = document.getElementsByTagName('head').item(0);" +
//                        "var style = document.createElement('style');" +
//                        "style.type = 'text/css';" +
//                        "style.innerHTML = window.atob('" + encoded + "');" +
//                        "parent.appendChild(style)" +
//                        "})()"
//            )
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    private fun getByteArrayFromAsset(fileName: String): ByteArray {
//        return try {
//            val assetInput: InputStream = assets.open(fileName)
//            val buffer = ByteArray(assetInput.available())
//            assetInput.read(buffer)
//            assetInput.close()
//
//            buffer
//        } catch (e: IOException) {
//            // TODO Auto-generated catch block
//            e.printStackTrace()
//            ByteArray(0)
//        }
//    }
//
//    private fun getStringFromAsset(fileName: String): String = assets.open(fileName).bufferedReader().use { it.readText() }
//
//    private fun preloadNext() {
//        val jsString = getStringFromAsset("PreloadBook.js")
//        binding.webView.evaluateJavascript(jsString) { urls ->
//            Log.v("owp", "5555555555-->$urls")
////            var parser = new PreloadNext.A3PLParser();
////            binding.webView.evaluateJavascript("javascript:PreloadNext();") {}
////            binding.webView.evaluateJavascript("javascript:A3PLParser.init();") {}
////            binding.webView.evaluateJavascript("javascript:A3PLParser.parserDocument(document, window);") {}
////            binding.webView.evaluateJavascript("javascript:A3PLParser.getNextLinkObject();") {}
////            binding.webView.evaluateJavascript("javascript:A3PLParser.getNextLinkUrl();") { urls ->
////                Log.v("owp", "555==>${urls}")
////            }
//        }
//    }
//
//}