package com.example.rishabh.trackmyproduct;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class WebViewActivity extends AppCompatActivity {

    private WebView webview;
    private Button copyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webview = (WebView) findViewById(R.id.webView);
        webview.setWebViewClient(new WebViewClient());

        Intent intent = getIntent();
        webview.loadUrl(intent.getStringExtra("url"));

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        copyButton = (Button) findViewById(R.id.copyButton);
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = webview.getUrl();
                Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(webview.canGoBack()){
            webview.goBack();
        }
        else
            super.onBackPressed();
    }
}
