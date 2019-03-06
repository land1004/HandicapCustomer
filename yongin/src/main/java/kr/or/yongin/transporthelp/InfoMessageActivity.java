package kr.or.yongin.transporthelp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.or.yongin.transporthelp.common.FontAwesome.CFontAwesomeManager;
import kr.or.yongin.transporthelp.common.util.GlobalValues;
import kr.or.yongin.transporthelp.impl.preference.ConfigPreference;

public class InfoMessageActivity extends Activity
{
    private final String THIS_TAG = "InfoMessageActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_message);

        Intent intent = getIntent();
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

        LinearLayout layoutFirst = (LinearLayout)findViewById(R.id.layoutFirst);
        LinearLayout layoutSecond = (LinearLayout)findViewById(R.id.layoutSecond);

        layoutFirst.setVisibility(View.GONE);
        layoutSecond.setVisibility(View.GONE);

        TextView tvFirstTitle = (TextView)findViewById(R.id.txtFirstTitle);
        TextView tvFirstNote = (TextView)findViewById(R.id.txtFirstNote);
        TextView tvSecondTitle = (TextView)findViewById(R.id.txtSecondTitle);
        TextView tvSecondNote = (TextView)findViewById(R.id.txtSecondNote);

        String noticetype = intent.getStringExtra("type");
        if (noticetype.equalsIgnoreCase(GlobalValues.INFO_VIEW_USE_NOTICE))
        {
            layoutSecond.setVisibility(View.VISIBLE);
            WebView webView = (WebView) findViewById(R.id.webInfoUse);
            webView.setWebViewClient(new WebViewClient());

            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);

            String url = "http://CTMAppITF.yonginnuri.or.kr:38080/userGuide.jsp";

            webView.loadUrl(url);

        }
        else if (noticetype.equalsIgnoreCase(GlobalValues.INFO_VIEW_SERVICE_NOTICE))
        {
            layoutFirst.setVisibility(View.VISIBLE);

            tvFirstTitle.setText("연락처");
            tvFirstNote.setText(getResources().getString(R.string.center_tel));
            tvSecondTitle.setText("FAX");
            tvSecondNote.setText("031-339-6598");
        }
        else if (noticetype.equalsIgnoreCase(GlobalValues.INFO_VIEW_VERSION_NOTICE))
        {
            layoutFirst.setVisibility(View.VISIBLE);

            ConfigPreference config = ConfigPreference.getInstance();
            config.setPreference(getBaseContext());

            tvFirstTitle.setText("현재버전");
            tvFirstNote.setText(config.getVersion());
            tvSecondTitle.setText("최신버전");
            tvSecondNote.setText(config.getNewVersion());
        }
    }
}
