package com.a103.eyakrev1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView

class DeviceInfoFragment : Fragment() {
    private lateinit var layout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layout = inflater.inflate(R.layout.fragment_device_info, container, false)

        val webview = layout.findViewById<WebView>(R.id.pdfWebView)
        webview.settings.javaScriptEnabled = true
        val pdf = "android_asset/device_info.pdf"
        webview.loadUrl("https://docs.google.com/gview?embedded=true&url="+ pdf)

        return layout
    }
}