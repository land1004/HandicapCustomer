package kr.or.hsnarae.transporthelp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.gson.Gson;
import com.ionemax.iomlibrarys.log.Logview;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kr.or.hsnarae.transporthelp.common.FontAwesome.CFontAwesomeManager;
import kr.or.hsnarae.transporthelp.common.db.DBControlManager;
import kr.or.hsnarae.transporthelp.common.db.DBSchema;
import kr.or.hsnarae.transporthelp.common.db.SelectHelper;
import kr.or.hsnarae.transporthelp.common.util.BusEventProvider;
import kr.or.hsnarae.transporthelp.impl.net.json.IJSONPageMethod;
import kr.or.hsnarae.transporthelp.impl.net.json.JSONMessageEvent;
import kr.or.hsnarae.transporthelp.impl.net.json.RetrofitProcessManager;
import kr.or.hsnarae.transporthelp.impl.net.json.rep.REP_JSONPOISearch;
import kr.or.hsnarae.transporthelp.impl.net.json.req.JSON_REQPOISearch;
import kr.or.hsnarae.transporthelp.impl.preference.OrderPreference;
import kr.or.hsnarae.transporthelp.impl.rowadaptors.ListPOISearchRowAdaptor;
import kr.or.hsnarae.transporthelp.impl.rowadaptors.SelectMessageEvent;

public class POISearchActivity extends Activity
{
    private final String THIS_TAG = "POISearchActivity";
    private final int MSG_HTTP_FAILED = 0;
    private final int MSG_LIST_VIEW = 1;
    private final int MSG_LIST_SELECT = 2;
    private final int MSG_CURRENT_POI = 3;

    private BootstrapEditText mTxtPOISearch;
    private ArrayList<POIItem> mPOIItemList;
    private ListView mSearchList;
    private boolean mStartFlag = true;
    private BootstrapEditText mPOIDetail;
    private TextView mPOIAddress;
    private int mPOISelectIndex;

    private String mSearchRestrict="";
    private boolean mLastitemVisibleFlag = false;        //화면에 리스트의 마지막 아이템이 보여지는지 체크

    private int mTotalCount=0, mCurrentCount=0;
    private boolean mScrollBottom = false;              // 스크롤 끝에서 검색

    private String mPOIName="", mPOIJibunAddr="";       // 현재 위치
    private double mPOILatitude=0, mPOILongitude=0;
    private boolean mCurrentPOI = false;
    private boolean mSpeedAlloc = false;                // 즉시배차

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poisearch);

        Intent intent = getIntent();

        // 현재 위치
        mPOIName = intent.getStringExtra("poiname");
        mPOIJibunAddr = intent.getStringExtra("addr");
        mPOILatitude = intent.getDoubleExtra("latitude", 0.0);
        mPOILongitude = intent.getDoubleExtra("longitude", 0.0);
        mCurrentPOI = false;

        // 기본 설정
        TextView title = (TextView)findViewById(R.id.txtTitle);
        title.setText(intent.getStringExtra("title"));
        mStartFlag = intent.getBooleanExtra("flag", true);

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
        BootstrapButton btn = (BootstrapButton)findViewById(R.id.btnPOISearch);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTotalCount = 0;
                mCurrentCount = 0;
                mScrollBottom = false;

                doPOISearch ("1", "50");
            }
        });

        mTxtPOISearch = (BootstrapEditText)findViewById(R.id.editPOISearch);
        mTxtPOISearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    mTotalCount = 0;
                    mCurrentCount = 0;
                    mScrollBottom = false;

                    doPOISearch ("1", "50");
                }
                return false;
            }
        });

        mSearchList = (ListView)findViewById(R.id.list_detail);
        mSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Logview.Logwrite(THIS_TAG, "Item click : " + i);
                mPOISelectIndex = i;
                mCurrentPOI = false;
                handler.sendEmptyMessage(MSG_LIST_SELECT);
            }
        });

        mSearchList.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState)
            {
                //OnScrollListener.SCROLL_STATE_IDLE은 스크롤이 이동하다가 멈추었을때 발생되는 스크롤 상태입니다.
                //즉 스크롤이 바닦에 닿아 멈춘 상태에 처리를 하겠다는 뜻
                if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && mLastitemVisibleFlag) {
                    Logview.Logwrite(THIS_TAG, "리스트 마지막 ");
                    Logview.Logwrite(THIS_TAG, "Total : " + mTotalCount + " / current : " + mCurrentCount);
                    if (mTotalCount > mCurrentCount) {
                        doPOISearch(String.valueOf(mCurrentCount + 1), "50");
                        mScrollBottom = true;
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가
                // 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때
                mLastitemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }
        });

        // 현재 위치를 출발지로
        TextView tv = (TextView)findViewById(R.id.txtSetCurrent);

        // 검색 조건
        if (mStartFlag) {
            tv.setText("현재 위치를 출발지로 설정하기");
            mSearchRestrict= "\"화성시\"";
            Toast.makeText(getBaseContext(), "출발지는 화성시 관내만 가능합니다.", Toast.LENGTH_LONG).show();
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handler.sendEmptyMessage(MSG_CURRENT_POI);
                }
            });
        }
        else {
            OrderPreference preference = OrderPreference.getInstance();
            preference.setPreference(getBaseContext());
            if (preference.getOrderType().equalsIgnoreCase("S")) {
                LinearLayout layout = (LinearLayout) findViewById(R.id.layoutCurrentPosition);
                layout.setVisibility(View.GONE);
                mSearchRestrict= "\"화성시\"";
                doAlertMessageDialogView ("알림", getResources().getString(R.string.smart_alloc_msg));
                mSpeedAlloc = true;
            } else {
                LinearLayout layout = (LinearLayout) findViewById(R.id.layoutCurrentPosition);
                layout.setVisibility(View.GONE);
                layout = (LinearLayout) findViewById(R.id.layoutSearchArea);
                layout.setVisibility(View.VISIBLE);

                RadioButton radioButton = (RadioButton) findViewById(R.id.radioSearchArea1);
                radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b)
                            mSearchRestrict = "\"경기도\"";
                    }
                });
                radioButton.setChecked(true);

                radioButton = (RadioButton) findViewById(R.id.radioSearchArea2);
                radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b)
                            mSearchRestrict = "\"서울특별시\"";
                    }
                });

                radioButton = (RadioButton) findViewById(R.id.radioSearchArea3);
                radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b)
                            mSearchRestrict = "\"인천광역시\"";
                    }
                });
            }
        }

        mPOIDetail = (BootstrapEditText)findViewById(R.id.editPOIDetail);
        mPOIAddress = (TextView)findViewById(R.id.txtPOIAddress);
        BootstrapButton select = (BootstrapButton)findViewById(R.id.btnPOISelect);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doFinish();
            }
        });
        if (mStartFlag)
            select.setText("출발");
        else
            select.setText("도착");
        LinearLayout layout = (LinearLayout)findViewById(R.id.layoutDetail);
        layout.setVisibility(View.GONE);

        mTotalCount = 0;
        mCurrentCount = 0;

        doPOIHistory ();
    }

    @Override
    protected void onPause() {
        try {
            BusEventProvider.getInstance().unregister(this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_LIST_VIEW:
                    int size = msg.arg1;
                    doListView (size);
                    break;
                case MSG_LIST_SELECT:
                    doViewDetailLayout();
                    break;
                case MSG_CURRENT_POI:
                    mCurrentPOI = true;
                    doViewDetailLayout ();
                    break;
            }
        }
    };

    /**
     * 검색결과 전달
     */
    private void doFinish ()
    {
        Intent intent = new Intent();
        intent.putExtra("detail", mPOIDetail.getText().toString().trim());
        intent.putExtra("addr", mPOIAddress.getText().toString().trim());
        if (mCurrentPOI)
        {
            intent.putExtra("xpos", String.valueOf(mPOILongitude));
            intent.putExtra("ypos", String.valueOf(mPOILatitude));
        } else {
            intent.putExtra("xpos", mPOIItemList.get(mPOISelectIndex).longitude);
            intent.putExtra("ypos", mPOIItemList.get(mPOISelectIndex).latitude);
        }
        setResult(RESULT_OK, intent);

        finish();
    }

    /**
     * 상세주소 입력
     */
    private void doViewDetailLayout ()
    {
        if (mCurrentPOI) {
            if (mPOIJibunAddr != null && mPOIJibunAddr.indexOf(mSearchRestrict) > -1) {
                LinearLayout layout = (LinearLayout) findViewById(R.id.layoutDetail);
                layout.setVisibility(View.VISIBLE);
                mPOIAddress.setText(mPOIJibunAddr);
                mPOIDetail.setText(mPOIName);
            } else {
                Toast.makeText(getBaseContext(), "출발지는 관내(화성시)만 가능합니다.", Toast.LENGTH_LONG).show();
            }
        } else {
            if (mPOIItemList.get(mPOISelectIndex).jibunAddr != null && mPOIItemList.get(mPOISelectIndex).jibunAddr.length() > 0) {
                LinearLayout layout = (LinearLayout) findViewById(R.id.layoutDetail);
                layout.setVisibility(View.VISIBLE);

                mPOIAddress.setText(mPOIItemList.get(mPOISelectIndex).jibunAddr);
                mPOIDetail.setText(mPOIItemList.get(mPOISelectIndex).poiname);
            }
        }
    }

    private  ListPOISearchRowAdaptor mListadaptor;
    /**
     * 검색결과를 리스트에 표시
     */
    private void doListView(int size)
    {
        if (mPOIItemList == null || mPOIItemList.size() < 1)
            return;
        mLastitemVisibleFlag = false;

        if (size == 0)
            mCurrentCount = 0;

        // 데이터
        ArrayList<HashMap<String, String>>list = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map;
        //for (POIItem item : mPOIItemList)
        POIItem item;
        int nStart = 0;
        for (int i=mCurrentCount; i<mPOIItemList.size(); i++)
        {
            item = mPOIItemList.get(i);
            map = new HashMap<String, String>();
            map.put("list_1st_item", item.poiname);
            map.put("list_2nd_item", item.jibunAddr);
            if (mStartFlag)
                map.put("list_3rd_item", "출발");
            else
                map.put("list_3rd_item", "도착");

            Logview.Logwrite(THIS_TAG, "poiName : " + item.poiname);
//            Logview.Logwrite(THIS_TAG, "roadFullAddr : " + item.roadAddr);
//            Logview.Logwrite(THIS_TAG, "jibunFullAddr : " + item.jibunAddr);
//            Logview.Logwrite(THIS_TAG, "latitude : " + item.latitude);
//            Logview.Logwrite(THIS_TAG, "longitude : " + item.longitude);

            list.add(map);
            map = null;
        }

        if (size != 0) {
            mCurrentCount += size;
        }

        if (!mScrollBottom) {
            mListadaptor = new ListPOISearchRowAdaptor(getBaseContext(), R.layout.listpoisearch_items_row, list);
            mSearchList.setAdapter(mListadaptor);
        } else {
            mListadaptor.addAll(list);
        }
    }

    /**
     * POI 검색 실시
     */
    private void doPOISearch (final String start, final String display)
    {
        Logview.Logwrite(THIS_TAG, "doPOISearch");
        String seardh = mTxtPOISearch.getText().toString().trim();

        if (seardh == null || seardh.length() < 1)
        {
            Toast.makeText(getBaseContext(), "검색어 입력이 잘못되었습니다.", Toast.LENGTH_LONG).show();
            return;
        }

        // 검색실시
        new Thread(new Runnable() {
            @Override
            public void run() {
                String searchPoi = mSearchRestrict + " " + mTxtPOISearch.getText().toString().trim();
                JSON_REQPOISearch json = new JSON_REQPOISearch(searchPoi, display, start);
                RetrofitProcessManager.doRetrofitRequest(JSON_REQPOISearch.PAGE_NAME, json.getParams());
            }
        }).start();

        startProgress();

        // 키보드 숨기기
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mTxtPOISearch.getWindowToken(), 0);
    }

    /**
     * HTTP 요청 수신 처리
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
                if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.PoiSearch.name().toLowerCase())) {
                    receivePOISearch(event);
                }
            } else {
                Logview.Logwrite(THIS_TAG, "Page name not found");
                handler.sendEmptyMessage(MSG_HTTP_FAILED);
            }
        } else {
            handler.sendEmptyMessage(MSG_HTTP_FAILED);
        }
    }

    @Subscribe
    public void selectMessageEvent(SelectMessageEvent event)
    {
        Logview.Logwrite(THIS_TAG, "selectMessageEvent : " + event.getSelectIndex());
    }

    /**
     * 검색 결과 수신
     * @param event
     */
    private void receivePOISearch (JSONMessageEvent event)
    {
        Gson gson = new Gson();

        REP_JSONPOISearch json = gson.fromJson(event.getMessageBody(), REP_JSONPOISearch.class);

        if (mCurrentCount == 0) {
            if (mPOIItemList != null)
                mPOIItemList.clear();
            mPOIItemList = null;
            mPOIItemList = new ArrayList<POIItem>();
        } else {
            if (mPOIItemList == null)
                mPOIItemList = new ArrayList<POIItem>();
        }

        if (json.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success)))
        {
            Logview.Logwrite(THIS_TAG, "receivePOISearch success");
        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receivePOISearch false : " + json.getBody().getCause());
            handler.sendEmptyMessage(MSG_HTTP_FAILED);
            if (mPOIItemList != null)
                mPOIItemList.clear();
            mPOIItemList = null;
            mPOIItemList = new ArrayList<POIItem>();

            POIItem data = new POIItem ("검색결과가 없습니다.", "", "",
                    "", "");
            mPOIItemList.add(data);
            handler.obtainMessage(MSG_LIST_VIEW, 0, 0).sendToTarget();
            return;
        }

        List<REP_JSONPOISearch.Body.ITEM> list = json.getBody().getItemList();
        Logview.Logwrite(THIS_TAG, "Total count : " + json.getBody().getTotalCount());

        if (json.getBody().getTotalCount() != null && json.getBody().getTotalCount().length() > 0)
            mTotalCount = Integer.parseInt(json.getBody().getTotalCount());
        else
            mTotalCount = 0;

        if (list != null && list.size() > 0)
        {
            Logview.Logwrite(THIS_TAG, "List count : " + list.size());
            for (REP_JSONPOISearch.Body.ITEM item : list)
            {
                Logview.Logwrite(THIS_TAG, "수신 >> poiName : " + item.poiName);
//                Logview.Logwrite(THIS_TAG, "roadFullAddr : " + item.roadFullAddr);
//                Logview.Logwrite(THIS_TAG, "jibunFullAddr : " + item.jibunFullAddr);
//                Logview.Logwrite(THIS_TAG, "latitude : " + item.latitude);
//                Logview.Logwrite(THIS_TAG, "longitude : " + item.longitude);
//
                POIItem data = new POIItem (item.poiName, item.roadFullAddr, item.jibunFullAddr,
                        item.latitude, item.longitude);
                mPOIItemList.add(data);
            }
        }

        handler.obtainMessage(MSG_LIST_VIEW, list.size(), 0).sendToTarget();
    }

    class POIItem
    {
        public String poiname="";
        public String roadAddr = "";
        public String jibunAddr = "";
        public String latitude = "";
        public String longitude = "";

        public POIItem (String name, String roadaddr, String jibunaddr, String lati, String longi)
        {
            this.poiname = name;
            this.roadAddr = roadaddr;
            this.jibunAddr = jibunaddr;
            this.latitude = lati;
            this.longitude = longi;
        }
    }

    /**
     * 이전에 사용한 것이 있으면 표시한다.
     */
    private void doPOIHistory()
    {
        String sql = "SELECT * FROM " + DBSchema.TABLE_POI_HISTORY; // + " ORDER BY _id DESC;";

        if (mStartFlag || mSpeedAlloc)
            sql += " WHERE jibunfulladdress LIKE '%화성시%' ";
        sql += " ORDER BY _id DESC;";

        SelectHelper select = DBControlManager.dbSelect(sql);

        if (select != null && select.getCount() > 0)
        {
            if (mPOIItemList != null)
                mPOIItemList.clear();
            mPOIItemList = null;
            mPOIItemList = new ArrayList<POIItem>();
            POIItem data;
            select.moveFirst();
            do{
                data = new POIItem (select.getValue(DBSchema.COL_DETAIL_ADDR),
                        select.getValue(DBSchema.COL_ROAD_ADDR), select.getValue(DBSchema.COL_JIBUN_ADDR),
                        select.getValue(DBSchema.COL_POS_Y), select.getValue(DBSchema.COL_POS_X));

                mPOIItemList.add(data);
            }while (select.moveNext());

            handler.sendEmptyMessage(MSG_LIST_VIEW);
        }
    }

    /**
     * 메시지 표시
     * @param title
     * @param msg
     */
    private void doAlertMessageDialogView (String title, String msg)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(POISearchActivity.this);
        alert.setTitle(title);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
            }
        });
        alert.setMessage(msg);
        alert.show();

    }


    //////////////////////////////////////////////////////////////////////////////////////
    // Progress 원형
    //
    private ProgressDialog mProgressDialog = null;
    private synchronized void startProgress()
    {
        stopProgress();

        mProgressDialog = ProgressDialog.show(POISearchActivity.this, "", "잠시 기다려주세요", true);
    }

    private synchronized void stopProgress()
    {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
        mProgressDialog = null;
    }


}
