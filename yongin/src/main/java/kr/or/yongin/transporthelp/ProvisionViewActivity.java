package kr.or.yongin.transporthelp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import kr.or.yongin.transporthelp.common.FontAwesome.CFontAwesomeManager;

public class ProvisionViewActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provision_view);

        Intent intent = getIntent();

        // 기본 설정
        TextView title = (TextView)findViewById(R.id.txtTitle);
        title.setText(intent.getStringExtra("title"));

        TextView btnBack = (TextView)findViewById(R.id.btnBack);
        btnBack.setTypeface(CFontAwesomeManager.getTypeface(this, CFontAwesomeManager.FONTAWESOME));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //////////////////////////////////////////////////////////////

        String url = intent.getStringExtra("url");

        WebView webView = (WebView) findViewById(R.id.webProvision);
        webView.setWebViewClient(new WebViewClient());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.loadUrl(url);
    }
}
