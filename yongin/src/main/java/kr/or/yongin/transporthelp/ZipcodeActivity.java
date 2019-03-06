package kr.or.yongin.transporthelp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.ionemax.iomlibrarys.log.Logview;

import kr.or.yongin.transporthelp.common.FontAwesome.CFontAwesomeManager;

public class ZipcodeActivity extends Activity
{
    private final String THIS_TAG = "ZipcodeActivity";

    public static final int ZIPCODE_RESULT_CODE = 20000;
    private final int MSG_FIND_ZIPCODE = 0;

    private final String ZIPCODE_FIND_URL = "http://CTMAppITF.yonginnuri.or.kr:38080/findAddressiframe.jsp";
    private WebView m_webView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zipcode);

        // 기본 설정
        TextView title = (TextView)findViewById(R.id.txtTitle);
        title.setText("주소찾기");

        TextView btnBack = (TextView)findViewById(R.id.btnBack);
        btnBack.setTypeface(CFontAwesomeManager.getTypeface(this, CFontAwesomeManager.FONTAWESOME));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("addr", "");
                setResult(RESULT_CANCELED, intent);

                finish();
            }
        });
        ///////////////////////////////////////////////////////////////

        m_webView = (WebView)findViewById(R.id.zipWebview);
        m_webView.clearCache(true);
        m_webView.clearFormData();

        m_webView.setWebViewClient(new ZipcodeWebViewClient());

        m_webView.getSettings().setJavaScriptEnabled(true);	// javascript 실행
        m_webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); //자바스크립트가 “window.open()”을 사용하여 자동으로 창을 열 수 있도록 설정
        //m_webView.getSettings().setPluginsEnabled(true);	// 웹 플러그인을 사용할 수 있도록 설정
        m_webView.getSettings().setSupportMultipleWindows(false);	// 윈도우를 여러 개 생성할 수 있도록 설정
        // 이 설정은 “android.webkit.WebChromeClient” 클래스의 “onCreateWindow()” 메소드를 사용할 할 수 있게 할 것인지를 의미함
        m_webView.getSettings().setSupportZoom(true);			// 줌(확대/축소) 기능을 지원하도록 설정
        m_webView.getSettings().setBuiltInZoomControls(true);	// 안드로이드에서 제공하는 줌 툴(Zoom Tool)을 사용하도록 설정
        // 줌 툴을 사용하려면 “setSupportZoom(true)” 설정과 함께 사용해야함
        m_webView.getSettings().setBlockNetworkImage(false);	// 다만, 이 값을 “true”로 설정하면, 웹페이지에서 서버로 부터받아 출력하는 이미지는 화면에 나타나지 않음
        m_webView.getSettings().setLoadsImagesAutomatically(true);	// 웹뷰가 앱에 등록되어 있는 이미지 리소스를 자동으로 로드하게 설정
        m_webView.getSettings().setUseWideViewPort(true);			// HTML 파일의 <Meta/> 태그레서 정의하는 “view viewport”를 사용하게 함
        // webview 확대축소시 텍스트 자동 줄맞춤 해제 default : false
        m_webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);	// 웹뷰가 캐시(cache)를 사용하지 않고 항상 서버에서 새로 지정한 HTML 파일을 가져와 화면에 출력되도록 함

        m_webView.requestFocus();

        m_webView.setHorizontalScrollBarEnabled(false); // 세로 scroll 제거
        m_webView.setVerticalScrollBarEnabled(false); 	// 가로 scroll 제거

        m_webView.addJavascriptInterface(new AndroidBridge(), "android");	// 웹에서 호출할 클레스

        m_webView.loadUrl(ZIPCODE_FIND_URL);
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_FIND_ZIPCODE:
                    String addr = (String)msg.obj;
                    Logview.Logwrite(THIS_TAG, "Find zipcode : " + addr);
                    sendZipcode (addr);
                    break;
            }
        }
    };

    private void sendZipcode (String value)
    {
        Intent intent = new Intent();
        intent.putExtra("addr", value);
        setResult(RESULT_OK, intent);

        finish();
    }

    private class ZipcodeWebViewClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
        {
            super.onReceivedError(view, request, error);
            Toast.makeText(getBaseContext(), "페이지 읽기 에러 ", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * javascript -> app
     * 호춯
     */
    private class AndroidBridge
    {
        @JavascriptInterface
        public void setMessage(final String msg)
        {
            handler.obtainMessage(MSG_FIND_ZIPCODE, msg).sendToTarget();
        }
    }
}
