package kr.or.hsnarae.transporthelp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ionemax.iomlibrarys.log.Logview;
import com.squareup.otto.Subscribe;

import kr.or.hsnarae.transporthelp.common.FontAwesome.CFontAwesomeManager;
import kr.or.hsnarae.transporthelp.common.util.BusEventProvider;
import kr.or.hsnarae.transporthelp.common.util.ErrorcodeToString;
import kr.or.hsnarae.transporthelp.impl.net.json.IJSONPageMethod;
import kr.or.hsnarae.transporthelp.impl.net.json.JSONMessageEvent;
import kr.or.hsnarae.transporthelp.impl.net.json.RetrofitProcessManager;
import kr.or.hsnarae.transporthelp.impl.net.json.rep.REP_JSONNoticeDetail;
import kr.or.hsnarae.transporthelp.impl.net.json.req.JSON_REQNoticeDetail;
import kr.or.hsnarae.transporthelp.impl.preference.ConfigPreference;

public class NoticeDetailActivity extends Activity {
    private final String THIS_TAG = "NoticeDetailActivity";

    private final int MSG_HTTP_FAILED = 0;

    private final int MSG_NOTICE_SUCCESS = 10;
    private final int MSG_NOTICE_FALIED = 11;
    private NoticeDetail mNoticeDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);

        // 기본 설정
        TextView title = (TextView) findViewById(R.id.txtTitle);
        title.setText("공지사항 상세");

        TextView btnBack = (TextView)findViewById(R.id.btnBack);
        btnBack.setTypeface(CFontAwesomeManager.getTypeface(this, CFontAwesomeManager.FONTAWESOME));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoticeDetailActivity.this, NoticeListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //////////////////////////////////////////////////////////////

        Intent intent = getIntent();
        String noticeseq = intent.getStringExtra("notice_seq");

        mNoticeDetail = new NoticeDetail();

//        mNoticeDetail.title = "test HTML";
//        mNoticeDetail.note = tempHtmp;
//        doNoticeDetailView ();
        if (noticeseq != null && noticeseq.length() > 0)
            doNoticeDetail(noticeseq);
    }

    @Override
    protected void onPause() {
        BusEventProvider.getInstance().unregister(this);
        stopProgress ();
        super.onPause();
    }

    @Override
    protected void onStart() {
        BusEventProvider.getInstance().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_HTTP_FAILED:
                    break;
                case MSG_NOTICE_FALIED:
                    String error_str = (String) msg.obj;
                    if (mNoticeDetail != null) {
                        mNoticeDetail.title = "";
                        mNoticeDetail.note = error_str;
                    }
                    break;
                case MSG_NOTICE_SUCCESS:
                    doNoticeDetailView();
                    break;
            }
        }
    };

    /**
     * 공지 사항을 보여준다.
     */
    private void doNoticeDetailView() {
        if (mNoticeDetail == null) return;

        TextView tv = (TextView) findViewById(R.id.txtNoticeTitle);
        tv.setText(mNoticeDetail.title);
//        tv = (TextView)findViewById(R.id.txtNoticeNote);
//        tv.setText(mNoticeDetail.note);
//        tv.setText(Html.fromHtml(Html.fromHtml(mNoticeDetail.note).toString()));

        //180130 송명진 - 날짜 추가해줌...
        TextView dt = (TextView) findViewById(R.id.txtNoticeDate);
        dt.setText(mNoticeDetail.date);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WebView webView = (WebView) findViewById(R.id.webNoticeNote);
                webView.setWebViewClient(new WebViewClient());

                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setLoadWithOverviewMode(true);
                webSettings.setUseWideViewPort(true);

                String HEADERHTML =
                        "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">"
                                + "<html>  <head>  <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">"
                                + "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0\">"
                                + "</head>  <body>"
                                + "\t<form>\n" +
                                "\t\t<table>\n" +
                                "\t\t\t<tr style=\"vertical-align:top;\">\n" +
                                "\t\t\t\t<td colspan=\"6\" style=\"width:680px; padding:30px 10px\" class=\"artCont\">\n";

                String FOOTERHTML = "\t\t\t\t</td>\n" +
                        "\t\t\t</tr>\n" +
                        "\t\t</table>\n" +
                        "\t</form>\n" +
                        "</body></html>";

//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//            webView.loadData(mNoticeDetail.note, "text/html", "UTF-8");
//        else
//            webView.loadData(HEADERHTML + mNoticeDetail.note + FOOTERHTML, "text/html; charset=UTF-8", null);
                //webView.loadDataWithBaseURL(null, Html.fromHtml(Html.fromHtml(mNoticeDetail.note).toString()).toString(), "text/html", "UTF-8", null);
                webView.loadDataWithBaseURL(null, HEADERHTML + Html.fromHtml(mNoticeDetail.note).toString() + FOOTERHTML, "text/html", "UTF-8", null);

            }
        });
    }

    /**
     * 공지사항 상세 요청
     *
     * @param seq
     */
    private void doNoticeDetail(final String seq)
    {
        startProgress();

        new Thread(new Runnable() {
            @Override
            public void run() {
                ConfigPreference config = ConfigPreference.getInstance();
                config.setPreference(getBaseContext());

                JSON_REQNoticeDetail json = new JSON_REQNoticeDetail(config.getAuthkey(), seq);
                RetrofitProcessManager.doRetrofitRequest(JSON_REQNoticeDetail.PAGE_NAME, json.getParams());

            }
        }).start();
    }

    /**
     * HTTP 요청 수신 처리
     *
     * @param event
     */
    @Subscribe
    public void jsonMessageEvent(JSONMessageEvent event)
    {
        stopProgress ();
        Logview.Logwrite(THIS_TAG, "jsonMessageEvent 수신 상태 : " + event.getMessageStatus());
        if (event.getMessageStatus()) {
            String pageName = event.getPageName();
            Logview.Logwrite(THIS_TAG, "jsonMessageEvent page : " + pageName);

            if (pageName != null) {
                if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.NoticeDetail.name().toLowerCase())) {
                    receiveNoticeDetail(event);
                }
            } else {
                Logview.Logwrite(THIS_TAG, "Page name not found");
                handler.sendEmptyMessage(MSG_HTTP_FAILED);
            }
        } else {
            handler.sendEmptyMessage(MSG_HTTP_FAILED);
        }
    }

    /**
     * 공지사항 상세
     *
     * @param event
     */
    private void receiveNoticeDetail(JSONMessageEvent event) {
        Gson gson = new Gson();

        REP_JSONNoticeDetail json = gson.fromJson(event.getMessageBody(), REP_JSONNoticeDetail.class);

        if (json.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success))) {
            Logview.Logwrite(THIS_TAG, "receiveNoticeDetail success");
        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receiveNoticeDetail false : " + json.getBody().getCause());
            handler.obtainMessage(MSG_NOTICE_FALIED,
                    ErrorcodeToString.getError(json.getBody().getResult(), json.getBody().getCause())).sendToTarget();
        }

        if (mNoticeDetail == null)
            mNoticeDetail = new NoticeDetail();

        mNoticeDetail.title = json.getBody().getTitle();
        mNoticeDetail.note = json.getBody().getNoticeBody();
        mNoticeDetail.date = json.getBody().getDate();
        mNoticeDetail.link = json.getBody().getLink();

        Logview.Logwrite(THIS_TAG, "receiveNoticeDetail title : " + mNoticeDetail.title);
        Logview.Logwrite(THIS_TAG, "receiveNoticeDetail note : " + mNoticeDetail.note);
        Logview.Logwrite(THIS_TAG, "receiveNoticeDetail note : " + mNoticeDetail.date);
        Logview.Logwrite(THIS_TAG, "receiveNoticeDetail link : " + mNoticeDetail.link);


        handler.sendEmptyMessage(MSG_NOTICE_SUCCESS);
    }

    class NoticeDetail {
        public String title = "";
        public String date = "";
        public String note = "";
        public String link = "";

        public NoticeDetail() {

        }
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // Progress 원형
    //
    private ProgressDialog mProgressDialog = null;
    private synchronized void startProgress()
    {
        stopProgress();

        mProgressDialog = ProgressDialog.show(NoticeDetailActivity.this, "", "잠시 기다려주세요", true);
    }

    private synchronized void stopProgress()
    {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
        mProgressDialog = null;
    }

}
