package com.zacmckenney.comicnexus;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zac on 9/14/16.
 */
public class WebViewFragment extends Fragment {
    @BindView(R.id.webview)
    WebView webView;

    @BindView(R.id.webview_progressbar) ProgressBar loadProgressBar;

    String url;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_webview, container, false);
        ButterKnife.bind(this, rootView);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                loadProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadProgressBar.setVisibility(View.GONE);



            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheMaxSize(10 * 1024 * 1024);
        webSettings.setLoadWithOverviewMode(true);

        Bundle bundleURL = this.getArguments();
        if (bundleURL != null) {
            url = bundleURL.getString("webURL", "http://www.marvel.com");
        } else {
            url = "http://www.marvel.com";
        }

        webView.loadUrl(url);
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    switch (i){
                        case KeyEvent.KEYCODE_BACK:
                            if (webView.canGoBack()){
                                webView.goBack();
                                return true;
                            }
                            break;
                    }
                }


                return false;
            }
        });

        return rootView;
    }
}
