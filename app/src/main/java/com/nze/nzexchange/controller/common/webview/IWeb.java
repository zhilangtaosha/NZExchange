package com.nze.nzexchange.controller.common.webview;

import android.webkit.WebView;

/**
 * Created by zwy on 2017/11/10.
 * email:16681805@qq.com
 */

public interface IWeb {
    void onPageFinished(WebView view, String url2);

    void onPageStarted(WebView view, String url2);

    void onReceivedError();

    void shouldOverrideUrlLoading(WebView view, String url2);
}
