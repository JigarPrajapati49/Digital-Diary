package com.rayo.digitaldiary.ui.settings

import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.databinding.FragmentDeleteAccountBinding
import com.rayo.digitaldiary.helper.Constants

class DeleteAccountFragment : BaseFragment<FragmentDeleteAccountBinding, BaseViewModel?>() {

    override val viewModel: BaseViewModel? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the web view settings instance
        val settings = binding.webView.settings

        // Enable java script in web view
        settings.javaScriptEnabled = true

        // Enable and setup web view cache

        settings.cacheMode = WebSettings.LOAD_DEFAULT


        // Enable zooming in web view
        settings.setSupportZoom(false)
        settings.builtInZoomControls = false
        settings.displayZoomControls = false

        // Zoom web view text
        settings.textZoom = 100

        // Enable disable images in web view
        settings.blockNetworkImage = false
        // Whether the WebView should load image resources
        settings.loadsImagesAutomatically = true


        //settings.pluginState = WebSettings.PluginState.ON
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.mediaPlaybackRequiresUserGesture = false


        // More optional settings, you can enable it by yourself
        settings.domStorageEnabled = true
        settings.setSupportMultipleWindows(true)
        settings.loadWithOverviewMode = true
        settings.allowContentAccess = true
        settings.setGeolocationEnabled(true)
        settings.allowUniversalAccessFromFileURLs = true
        settings.allowFileAccess = true

        // WebView settings
        binding.webView.fitsSystemWindows = true

        binding.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)


        // Set web view client
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null) {
                    view?.loadUrl(url)
                }
                return true
            }
        }
        binding.webView.loadUrl(Constants.DELETE_ACCOUNT)
    }

    override fun getFragmentId(): Int {
        return R.layout.fragment_delete_account
    }

}