package com.nze.nzexchange.controller.common.webview;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Convenient extension of WebViewClient.
 * 自定义WebViewClient
 */
public class CustomWebViewClient extends WebViewClient {

	private IWeb mMainActivity;

	public CustomWebViewClient(IWeb mainActivity) {
		super();
		mMainActivity = mainActivity;
	}
	//在页面加载结束时调用。
	@Override
	public void onPageFinished(WebView view, String url) {
		((CustomWebView) view).notifyPageFinished();
		mMainActivity.onPageFinished(view, url);

		super.onPageFinished(view, url);
	}
	//在页面加载开始时调用。
	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		((CustomWebView) view).notifyPageStarted();
		mMainActivity.onPageStarted(view, url);
		super.onPageStarted(view, url, favicon);
	}

	@TargetApi(8)
	@Override
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		super.onReceivedError(view, errorCode, description, failingUrl);
		view.stopLoading();
		view.clearView();
		mMainActivity.onReceivedError();
	}
	//在点击请求的是链接是才会调用，重写此方法返回true表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边。
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		((CustomWebView) view).resetLoadedUrl();
		if (url != null && !url.startsWith("about:blank")) // 控制加载url
			view.loadUrl(url);
		mMainActivity.shouldOverrideUrlLoading(view, url);
		return false;
	}

	@Override
	public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
		handler.proceed();
	}
}
