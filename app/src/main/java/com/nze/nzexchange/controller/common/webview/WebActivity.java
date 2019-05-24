package com.nze.nzexchange.controller.common.webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ZoomButtonsController;

import com.nze.nzexchange.R;
import com.nze.nzexchange.widget.CommonTopBar;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @version 1.0
 * @author: zwy
 * @类 说   明:
 * @创建时间：2015年3月24日 下午5:26:29
 */
public class WebActivity extends Activity implements IWeb {
    private CustomWebView mWebView;
    private ProgressBar pb;
    private RelativeLayout webView_parent;
    private CommonTopBar ctb;

    private String url = "http://www.baidu.com";
    private String mTitle;


    public static void skip(Context context, String url, String title) {
        context.startActivity(new Intent(context, WebActivity.class).putExtra("url", url).putExtra("title", title));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        mTitle = intent.getStringExtra("title");


        mWebView = (CustomWebView) findViewById(R.id.webview);
        pb = (ProgressBar) findViewById(R.id.progressbar);
        ctb = (CommonTopBar) findViewById(R.id.ctb_web);
        ctb.setTitle(mTitle);
        initWebView();
        mWebView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        webView_parent.removeView(mWebView);
        mWebView.setVisibility(View.GONE);
        mWebView.destroy();
        mWebView = null;
        super.onDestroy();
    }

    private void initWebView() {
        webView_parent = (RelativeLayout) findViewById(R.id.webView_parent);
        mWebView.setCanTouchZoom(false);
        hideBuiltInZoomControls(mWebView);

        mWebView.setDownloadListener(new DownloadListener() {// 下载
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        mWebView.setWebViewClient(new CustomWebViewClient(this));
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (TextUtils.isEmpty(mTitle)) ctb.setTitle(title);
                Log.i("zwy", title);//网页标题
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                pb.setProgress(newProgress);
                if (newProgress == 100) {
                    pb.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, newProgress);// 进度条
                // if (newProgress > 90) {
                // hideLoading();
                // }
                Log.i("", "the webview onProgressChanged newProgress" + newProgress);
            }

        });
    }

    public void onPageStarted(WebView view, String url2) {
        showLoadingProgress();
    }

    public void onPageFinished(WebView view, String url2) {
        hideLoadingProgress();
    }

    public void onReceivedError() {
        hideLoadingProgress();
    }

    public void shouldOverrideUrlLoading(WebView view, String url2) {

    }

    private void hideBuiltInZoomControls(WebView view) {
        if (Build.VERSION.SDK_INT < 11) {
            try {
                Field field = WebView.class.getDeclaredField("mZoomButtonsController");
                field.setAccessible(true);
                ZoomButtonsController zoomCtrl = new ZoomButtonsController(view);
                zoomCtrl.getZoomControls().setVisibility(View.GONE);
                field.set(view, zoomCtrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                WebSettings settings = view.getSettings();
                Method method = WebSettings.class.getMethod("setDisplayZoomControls", boolean.class);
                method.invoke(settings, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 显示进度
     */
    private void showLoadingProgress() {
        if (pb.getVisibility() == View.GONE) {
            pb.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏进度
     */
    private void hideLoadingProgress() {
        if (pb.getVisibility() == View.VISIBLE) {
            pb.setVisibility(View.GONE);
        }
    }
}
