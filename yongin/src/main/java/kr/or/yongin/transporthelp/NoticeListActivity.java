package kr.or.yongin.transporthelp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ionemax.iomlibrarys.log.Logview;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kr.or.yongin.transporthelp.common.FontAwesome.CFontAwesomeManager;
import kr.or.yongin.transporthelp.common.util.BusEventProvider;
import kr.or.yongin.transporthelp.impl.net.json.IJSONPageMethod;
import kr.or.yongin.transporthelp.impl.net.json.JSONMessageEvent;
import kr.or.yongin.transporthelp.impl.net.json.RetrofitProcessManager;
import kr.or.yongin.transporthelp.impl.net.json.rep.REP_JSONNoticeList;
import kr.or.yongin.transporthelp.impl.net.json.req.JSON_REQNoticeList;
import kr.or.yongin.transporthelp.impl.preference.ConfigPreference;
import kr.or.yongin.transporthelp.impl.rowadaptors.ListNoticeRowAdaptor;

public class NoticeListActivity extends Activity
{
    private final String THIS_TAG = "NoticeListActivity";

    private final int MSG_HTTP_FAILED = 0;
    private final int MSG_NOTICELIST_SUCCESS = 10;
    private final int MSG_LIST_SELECT = 11;

    private ArrayList<NoticeItem> mNoticeList;
    private ListView mListView;
    private int mListSelectIndex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_list);

        // 기본 설정
        TextView title = (TextView)findViewById(R.id.txtTitle);
        title.setText("공지사항");

        TextView btnBack = (TextView)findViewById(R.id.btnBack);
        btnBack.setTypeface(CFontAwesomeManager.getTypeface(this, CFontAwesomeManager.FONTAWESOME));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //////////////////////////////////////////////////////////////
        mListView = (ListView)findViewById(R.id.list_notice);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mListSelectIndex = i;
                handler.sendEmptyMessage(MSG_LIST_SELECT);
            }
        });

        doNoticeList ();
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

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_HTTP_FAILED:
                    break;
                case MSG_NOTICELIST_SUCCESS:
                    doViewNoticeList();
                    break;
                case MSG_LIST_SELECT:
                    doNoticeDetailView();
                    break;
            }
        }
    };

    /**
     * 공지사항 상세 표시
     */
    private void doNoticeDetailView ()
    {
        String seq = mNoticeList.get(mListSelectIndex).seq;
        if (seq == null || seq.length() < 1) return;

        Intent intent = new Intent(NoticeListActivity.this, NoticeDetailActivity.class);
        intent.putExtra("notice_seq", seq);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * 공지사항 리스트 표시
     */
    private void doViewNoticeList ()
    {
        if (mNoticeList == null || mNoticeList.size() < 1)
            return;

        // 데이터
        ArrayList<HashMap<String, String>>list = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map;

        for (NoticeItem item : mNoticeList)
        {
            map = new HashMap<String, String>();
            map.put("list_1st_item", item.title);

            list.add(map);
        }

        ListNoticeRowAdaptor listadaptor = new ListNoticeRowAdaptor(getBaseContext(), R.layout.listnotice_items_row, list);
        mListView.setAdapter(listadaptor);
    }

    /**
     * 공지사항 리스트 요청
     */
    private void doNoticeList()
    {
        startProgress();

        new Thread(new Runnable() {
            @Override
            public void run() {
                ConfigPreference config = ConfigPreference.getInstance();
                config.setPreference(getBaseContext());

                JSON_REQNoticeList login = new JSON_REQNoticeList(config.getAuthkey());
                RetrofitProcessManager.doRetrofitRequest(JSON_REQNoticeList.PAGE_NAME, login.getParams());
            }
        }).start();
    }

    /**
     * HTTP 요청 수신 처리
     * @param event
     */
    @Subscribe
    public void jsonMessageEvent(JSONMessageEvent event)
    {
        stopProgress();
        Logview.Logwrite(THIS_TAG, "jsonMessageEvent 수신 상태 : " + event.getMessageStatus());
        if (event.getMessageStatus()) {
            String pageName = event.getPageName();
            Logview.Logwrite(THIS_TAG, "jsonMessageEvent page : " + pageName);

            if (pageName != null) {
                if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.NoticeList.name().toLowerCase())) {
                    receiveNoticeList(event);
                }
            } else {
                Logview.Logwrite(THIS_TAG, "Page name not found");
                handler.sendEmptyMessage(MSG_HTTP_FAILED);
            }
        } else {
            handler.sendEmptyMessage(MSG_HTTP_FAILED);
        }
    }

    private void receiveNoticeList (JSONMessageEvent event)
    {
        Gson gson = new Gson();

        REP_JSONNoticeList json = gson.fromJson(event.getMessageBody(), REP_JSONNoticeList.class);

        if (json.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success)))
        {
            Logview.Logwrite(THIS_TAG, "receiveNoticeList success");
        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receiveNoticeList false : " + json.getBody().getCause());
            handler.sendEmptyMessage(MSG_HTTP_FAILED);
        }

        String count = json.getBody().getNoticeCount();
        List<REP_JSONNoticeList.Body.NOTICE> list = json.getBody().getNoticeList();

        if (list != null && list.size() > 0)
        {
            if (mNoticeList == null)
                mNoticeList = new ArrayList<NoticeItem>();
            mNoticeList.clear();

            NoticeItem notice;
            for (REP_JSONNoticeList.Body.NOTICE item : list)
            {
                notice = new NoticeItem();
                notice.title = item.noticeTitle;
                notice.seq = item.noticeSeq;
                notice.date = item.noticeDate;

                mNoticeList.add(notice);
            }

            handler.sendEmptyMessage(MSG_NOTICELIST_SUCCESS);
        }
    }

    class NoticeItem {
        public String title="";
        public String date="";
        public String seq="";

        public NoticeItem ()
        {

        }
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // Progress 원형
    //
    private ProgressDialog mProgressDialog = null;
    private synchronized void startProgress()
    {
        stopProgress();

        mProgressDialog = ProgressDialog.show(NoticeListActivity.this, "", "잠시 기다려주세요", true);
    }

    private synchronized void stopProgress()
    {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
        mProgressDialog = null;
    }

}
