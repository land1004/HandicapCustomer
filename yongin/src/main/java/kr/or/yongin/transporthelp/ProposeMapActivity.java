package kr.or.yongin.transporthelp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.google.gson.Gson;
import com.ionemax.iomlibrarys.log.Logview;
import com.ionemax.iomlibrarys.util.Util;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapCalloutCustomOverlay;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.squareup.otto.Subscribe;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kr.or.yongin.transporthelp.common.FontAwesome.CFontAwesomeManager;
import kr.or.yongin.transporthelp.common.db.DBControlManager;
import kr.or.yongin.transporthelp.common.db.DBSchema;
import kr.or.yongin.transporthelp.common.db.SelectHelper;
import kr.or.yongin.transporthelp.common.util.AvailableDestination;
import kr.or.yongin.transporthelp.common.util.BusEventProvider;
import kr.or.yongin.transporthelp.common.util.ErrorcodeToString;
import kr.or.yongin.transporthelp.common.util.GlobalValues;
import kr.or.yongin.transporthelp.common.util.TimePickerDailog;
import kr.or.yongin.transporthelp.common.widget.SegmentedButton;
import kr.or.yongin.transporthelp.impl.net.json.IJSONPageMethod;
import kr.or.yongin.transporthelp.impl.net.json.JSONMessageEvent;
import kr.or.yongin.transporthelp.impl.net.json.RetrofitProcessManager;
import kr.or.yongin.transporthelp.impl.net.json.rep.REP_JSONCallAvailableTime;
import kr.or.yongin.transporthelp.impl.net.json.rep.REP_JSONReqCall;
import kr.or.yongin.transporthelp.impl.net.json.rep.REP_JSONReservReqCall;
import kr.or.yongin.transporthelp.impl.net.json.rep.REP_JSONReverseGeocoding;
import kr.or.yongin.transporthelp.impl.net.json.rep.REP_JSONUseTypeList2;
import kr.or.yongin.transporthelp.impl.net.json.req.JSON_REQCallAvailableTime;
import kr.or.yongin.transporthelp.impl.net.json.req.JSON_REQReqCall;
import kr.or.yongin.transporthelp.impl.net.json.req.JSON_REQReservReqCall;
import kr.or.yongin.transporthelp.impl.net.json.req.JSON_REQReverseGeocoding;
import kr.or.yongin.transporthelp.impl.net.json.req.JSON_REQUseTypeList2;
import kr.or.yongin.transporthelp.impl.never_map.MapContainerView;
import kr.or.yongin.transporthelp.impl.never_map.NMapPOIflagType;
import kr.or.yongin.transporthelp.impl.never_map.NMapViewerResourceProvider;
import kr.or.yongin.transporthelp.impl.popup.DialogBookmark;
import kr.or.yongin.transporthelp.impl.popup.DialogReceiveHandler;
import kr.or.yongin.transporthelp.impl.preference.ConfigPreference;
import kr.or.yongin.transporthelp.impl.preference.OrderAvailableTimePreference;
import kr.or.yongin.transporthelp.impl.preference.OrderPreference;
import kr.or.yongin.transporthelp.impl.rowadaptors.CustomSpinnerAdapter;

public class ProposeMapActivity extends NMapActivity
        implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
{
    private final String THIS_TAG = "ProposeMapActivity";

    private final int MSG_HTTP_FAILED = 0;
    private final int MSG_DATE_PICK = 10;
    private final int MSG_TIME_PICK = 11;
    private final int MSG_MAP_POSITION_CHAGE = 12;
    private final int MSG_SUCCESS_USELIST = 13;

    private final int MSG_POI_SEART_START = 200;
    private final int MSG_POI_SEART_END = 201;

    private final int MSG_ORDER_FAILED = 300;   // 접수 실패
    private final int MSG_ORDER_SUCCESS = 301;  // 접수 성공
    private final int MSG_BOOKINGORDER_SUCCESS = 302;  // 예약 접수 성공
    private final int MSG_USE_TYPE_CHANGE = 303;  // 이용목적 변경 - 날짜 시간 선택 변경

    private ArrayList<USETYPE_ITEM> mUseType;
    //private SimpleSideDrawer mNav;
    private OrderPreference mPreference = null;
    private boolean mBookingPicker;             // True : 예약, false : 왕복
    private boolean mMapviweState;              // true : 출발, false : 도착

    private boolean mSearchAddress;
    private REQ_CALL_INFO mReqCallInfo;

    private DrawerLayout mDrawerLayout;
    private View mLeftSideDrawerView;
    private OrderAvailableTime mOrderAvailableTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_propose_map);

        mPreference = OrderPreference.getInstance();
        mPreference.setPreference(getBaseContext());

        // 기본 설정
        LinearLayout layoutBTNLeft = (LinearLayout)findViewById(R.id.layoutBTNLeft);
        LinearLayout layoutBTNCenter = (LinearLayout)findViewById(R.id.layoutBTNCenter);
        LinearLayout layoutBTNRight = (LinearLayout)findViewById(R.id.layoutBTNRight);

        Button bstBTNLeft = (Button)findViewById(R.id.btnLeft);
        Button bstBTNCenter = (Button)findViewById(R.id.btnCenter);
        Button bstBTNRight = (Button)findViewById(R.id.btnRight);

        layoutBTNLeft.setVisibility(View.GONE);
        layoutBTNCenter.setVisibility(View.VISIBLE);
        layoutBTNRight.setVisibility(View.GONE);

        bstBTNCenter.setOnClickListener(this);
        bstBTNCenter.setText("접수하기");

        //////////////////////////////////////////////////////////////
        // Left sliding menu
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mLeftSideDrawerView = (View)findViewById(R.id.drawer);

        TextView btnMenu = (TextView)findViewById(R.id.btnMenu);
        btnMenu.setTypeface(CFontAwesomeManager.getTypeface(this, CFontAwesomeManager.FONTAWESOME));
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(mLeftSideDrawerView);
            }
        });

        mDrawerLayout.addDrawerListener(myDrawerListener);

        TextView textView = (TextView)findViewById(R.id.btnMenu);
        //textView.setTypeface(CFontAwesomeManager.getTypeface(this, CFontAwesomeManager.FONTAWESOME));
        setTextViewFontAwesome (textView);

        initLeftSideMenu();

        // Map pin view
        textView = (TextView) findViewById(R.id.btnFromMap);
        setTextViewFontAwesome (textView);
        textView.setOnClickListener(this);

        textView = (TextView)findViewById(R.id.btnToMap);
        setTextViewFontAwesome (textView);
        textView.setOnClickListener(this);
        textView.setVisibility(View.GONE);

        textView = (TextView)findViewById(R.id.btnToMap1);
        setTextViewFontAwesome (textView);
        textView.setOnClickListener(this);
        textView.setVisibility(View.GONE);

        // 주소 바꾸기
        textView = (TextView) findViewById(R.id.btnFromPointChange);
        setTextViewFontAwesome (textView);
        textView.setOnClickListener(this);
        textView.setVisibility(View.GONE);

        textView = (TextView) findViewById(R.id.btnToPointChange);
        setTextViewFontAwesome (textView);
        textView.setOnClickListener(this);
        textView.setVisibility(View.GONE);
        textView = (TextView) findViewById(R.id.btnToPointChange1);
        setTextViewFontAwesome (textView);
        textView.setOnClickListener(this);
        textView.setVisibility(View.GONE);

        // 공지사항
        textView = (TextView)findViewById(R.id.btnNotice);
        textView.setTypeface(CFontAwesomeManager.getTypeface(this, CFontAwesomeManager.FONTAWESOME));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSubMenuActivity (NoticeListActivity.class);
            }
        });

        // Order type
        // Create the segmented buttons
        SegmentedButton btnOrderType = (SegmentedButton)findViewById(R.id.segmented);
        btnOrderType.clearButtons();
        btnOrderType.addButtons("즉 시", "예 약");
        btnOrderType.setOnClickListener(new SegmentedButton.OnClickListenerSegmentedButton() {
            @Override
            public void onClick(int index)
            {
                if (index == 0) {
                    setOrderType ("S");
                } else {
                    setOrderType ("B");
                }
            }
        });

        mOrderAvailableTime = new OrderAvailableTime();
        mOrderAvailableTime.bset = false;

        initOrder();

        mMapviweState = true;
        mSearchAddress = false;

        // 출발지 검색
        TextView tv = (TextView)findViewById(R.id.txtFromDong);
        tv.setOnClickListener(this);
        tv = (TextView)findViewById(R.id.txtFromSiDo);
        tv.setOnClickListener(this);
        tv = (TextView)findViewById(R.id.txtFromHint);
        tv.setOnClickListener(this);

        tv = (TextView)findViewById(R.id.txtToDong);
        tv.setOnClickListener(this);
        tv = (TextView)findViewById(R.id.txtToSiDO);
        tv.setOnClickListener(this);
        tv = (TextView)findViewById(R.id.txtToDong1);
        tv.setOnClickListener(this);
        tv = (TextView)findViewById(R.id.txtToSiDO1);
        tv.setOnClickListener(this);

        tv = (TextView)findViewById(R.id.txtToHint);
        tv.setOnClickListener(this);
        tv = (TextView)findViewById(R.id.txtToHint1);
        tv.setOnClickListener(this);

        tv = (TextView)findViewById(R.id.txtFromTitle);
        tv.setOnClickListener(this);
        tv = (TextView)findViewById(R.id.txtTotitle);
        tv.setOnClickListener(this);
        tv = (TextView)findViewById(R.id.txtTotitle1);
        tv.setOnClickListener(this);

        tv = (TextView)findViewById(R.id.btnStartEndMapview);
        tv.setOnClickListener(this);

        mReqCallInfo = new REQ_CALL_INFO();
        // Map init
        initMapview ();

        // 즐겨찾기 표시
        if (doCheckBookmarkExist(false))
            viewBookmarkDialog ();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //return super.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onResume()
    {
        Logview.Logwrite(THIS_TAG, "onResume");
        try {
            BusEventProvider.getInstance().register(this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        // 배차 가능 시간 설정
        //doOrderAvailableTime();
        if (mOrderAvailableTime != null) {
            mOrderAvailableTime.setOrderAvailableTime();

            if (mOrderAvailableTime.bset)
                viewOrderAvailableTime ();
            else
                Logview.Logwrite(THIS_TAG, "배차가능 시간 에러");
        }

        if (mMapInit)
            doShowMarker ();

        super.onResume();
    }

    @Override
    protected void onPause()
    {
        Logview.Logwrite(THIS_TAG, "onPause");

        try {
            BusEventProvider.getInstance().unregister(this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        onDestroyMap ();
        stopProgress ();

        super.onPause();
    }

    @Override
    protected void onStart() {
        Logview.Logwrite(THIS_TAG, "onStart");

        super.onStart();
    }

    @Override
    protected void onStop() {
        Logview.Logwrite(THIS_TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    /**
     * FontAwesome으로 font face 설정
     */
    private void setTextViewFontAwesome(TextView textView)
    {
        textView.setTypeface(CFontAwesomeManager.getTypeface(this, CFontAwesomeManager.FONTAWESOME));
    }

    private ArrayList<String>mGroupList;
    private HashMap<String, ArrayList<String>>mChildList;
    private ExpandableListView mExpandableListView;
    /**
     * Left side menu 초기화
     */
    private void initLeftSideMenu()
    {
        ConfigPreference config = ConfigPreference.getInstance();
        config.setPreference(getBaseContext());
        TextView tv = (TextView)findViewById(R.id.txtLeftSideUserName);
        tv.setText(config.getUserName());

        tv = (TextView)findViewById(R.id.txtLeftSideUserNumber);
        tv.setText(Util.makeTelNumber(config.getPhonenumber()));

        // 나의 정보
        ImageView iv = (ImageView)findViewById(R.id.btnUserSetting);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeLeftMenu();
                doSubMenuActivity (MyInfoIntroActivity.class);
            }
        });

        // 나의 이용 내역
        tv = (TextView)findViewById(R.id.menuItemCallHistory);
        tv.setOnClickListener(this);
        tv = (TextView)findViewById(R.id.menuItemBookmark);
        tv.setOnClickListener(this);
        // 정보 아이콘
        tv = (TextView)findViewById(R.id.menuItemInfoIcon);
        tv.setTypeface(CFontAwesomeManager.getTypeface(this, CFontAwesomeManager.FONTAWESOME));

        tv = (TextView)findViewById(R.id.menuItemInfo);
        tv.setOnClickListener(this);


        tv = (TextView)findViewById(R.id.menuItemEtc);
        tv.setOnClickListener(this);
        // 기타 아이콘
        tv = (TextView)findViewById(R.id.menuItemEtcIcon);
        tv.setTypeface(CFontAwesomeManager.getTypeface(this, CFontAwesomeManager.FONTAWESOME));

        // 정보
        tv = (TextView)findViewById(R.id.infoNotisetting);
        tv.setOnClickListener(this);
        tv = (TextView)findViewById(R.id.infoUseNote);
        tv.setOnClickListener(this);
        tv = (TextView)findViewById(R.id.infoService);
        tv.setOnClickListener(this);
        tv = (TextView)findViewById(R.id.infoVersion);
        tv.setOnClickListener(this);

        // 기타
        tv = (TextView)findViewById(R.id.etcMenu1);
        tv.setOnClickListener(this);
        tv = (TextView)findViewById(R.id.etcMenu2);
        tv.setOnClickListener(this);
        tv = (TextView)findViewById(R.id.etcMenu3);
        tv.setOnClickListener(this);
        tv = (TextView)findViewById(R.id.etcMenu4);
        tv.setOnClickListener(this);
        tv = (TextView)findViewById(R.id.etcMenu5);
        tv.setOnClickListener(this);
    }

    /**
     * 초기작업
     */
    private void initOrder ()
    {
        // Preference clear
        mPreference.setOrderType("S");
        mPreference.setOrderFromSelect(false);
        mPreference.setOrderFromDetail("");
        mPreference.setOrderFromAddr("");
        mPreference.setOrderFromX(0);
        mPreference.setOrderFromY(0);

        mPreference.setOrderToSelect(false);
        mPreference.setOrderToDetail("");
        mPreference.setOrderToAddr("");
        mPreference.setOrderToX(0);
        mPreference.setOrderToY(0);

        mPreference.setOrderBookingTurn(false);
        mPreference.setOrderBookingDate("");
        mPreference.setOrderBookingDateSend("");
        mPreference.setOrderBookingTime("");
        mPreference.setOrderBookingTimeSend("");

        mPreference.setOrderBookingTurnDate("");
        mPreference.setOrderBookingTurnDateSend("");
        mPreference.setOrderBookingTime("");
        mPreference.setOrderBookingTimeSend("");

        String orderType = mPreference.getOrderType();
        Logview.Logwrite(THIS_TAG, "initOrder orderType : " + orderType);

        if (orderType == null || orderType.length() < 1)
            orderType = "S";

        // 접수 형태
        setOrderType(orderType);

        // 동승인원
        ArrayList<String> stateList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.geton_count)));

        Spinner spinState = (Spinner)findViewById(R.id.spinerGetOnCount);
        CustomSpinnerAdapter customSpinnerAdapter=new CustomSpinnerAdapter(getBaseContext(), stateList);
        spinState.setAdapter(customSpinnerAdapter);
        spinState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mPreference.setOrderGetonCount(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Logview.Logwrite(THIS_TAG, "initOrder getOrderGetonCount : " + mPreference.getOrderGetonCount());
        spinState.setSelection(mPreference.getOrderGetonCount());

        // 이용목적
        String sql = "SELECT * FROM " + DBSchema.TABLE_NAME_USETYPE + ";";
        SelectHelper select = DBControlManager.dbSelect(sql);

        if (select != null && select.getCount() > 0)
        {
            if (mUseType != null)
                mUseType.clear();
            mUseType = null;
            mUseType = new ArrayList<USETYPE_ITEM>();
            USETYPE_ITEM data;
            select.moveFirst();
            do {
                data = new USETYPE_ITEM(select.getValue(DBSchema.COL_NAME),
                        select.getValue(DBSchema.COL_CODE),
                        select.getValue("bookingstartdate"), select.getValue("bookingenddate"),
                        select.getValue("bookingstarttime"), select.getValue("bookingendtime"),
                        select.getValue("roundavailable"));
                mUseType.add(data);
            } while (select.moveNext());
        } else {
            doUseType();
        }
        ArrayList<String> useList = new ArrayList<String>();
        if (mUseType != null) {
            for (USETYPE_ITEM item : mUseType) {
                useList.add(item.getName());
            }
        }

        Spinner spinUseCause = (Spinner)findViewById(R.id.spinerUseCause);
        CustomSpinnerAdapter customSpinnerAdapter2 =new CustomSpinnerAdapter(getBaseContext(), useList);
        spinUseCause.setAdapter(customSpinnerAdapter2);
        spinUseCause.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mPreference.setOrderUseCause(i);

                if (mPreference.getOrderType().equalsIgnoreCase("B"))
                {
                    handler.obtainMessage(MSG_USE_TYPE_CHANGE, i, 0, null).sendToTarget();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Logview.Logwrite(THIS_TAG, "initOrder getOrderuseCause : " + mPreference.getOrderuseCause());
        spinUseCause.setSelection(mPreference.getOrderuseCause());

        // 휠체어 사용여부
        CheckBox checkWheel = (CheckBox)findViewById(R.id.checkUseWheel);
        checkWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPreference.setOrderUseWheel(((CheckBox)view).isChecked());
            }
        });
        Logview.Logwrite(THIS_TAG, "initOrder getOrderUseWheel : " + mPreference.getOrderUseWheel());
        checkWheel.setChecked(mPreference.getOrderUseWheel());

        // 예약
        AwesomeTextView awesomeBtn = (AwesomeTextView)findViewById(R.id.btnFromCalendar);
        awesomeBtn.setOnClickListener(this);
        awesomeBtn = (AwesomeTextView)findViewById(R.id.btnFromClock);
        awesomeBtn.setOnClickListener(this);

        if (orderType.equalsIgnoreCase("B"))
        {
            mBookingPicker = true;
            // 예약날짜
            setBookingDate();
            // 예약시간
            setBookingTime();
        } else {

        }

        // 왕복여부
        CheckBox check = (CheckBox)findViewById(R.id.checkTurnBooking);
        check.setOnClickListener(this);

        // 왕복 예약
        awesomeBtn = (AwesomeTextView)findViewById(R.id.btnToCalendar);
        awesomeBtn.setOnClickListener(this);
        awesomeBtn = (AwesomeTextView)findViewById(R.id.btnToClock);
        awesomeBtn.setOnClickListener(this);
        Logview.Logwrite(THIS_TAG, "initOrder getOrderBookingTurn : " + mPreference.getOrderBookingTurn());
        if (mPreference.getOrderBookingTurn())
        {
            if (!check.isChecked()) {
                check.setChecked(true);
                mBookingPicker = false;
                // 왕복 날짜
                setBookingDate();
                // 왕복 시간
                setBookingTime();
            }
        } else {
            check.setChecked(false);
        }
    }

    /**
     * 즉시, 예약 버튼 처리
     * @param flag
     */
    private void setOrderType(String flag)
    {
        LinearLayout layoutBookingTurn = (LinearLayout)findViewById(R.id.layoutBookingTurn);
        LinearLayout layoutBookingTurnTemp = (LinearLayout)findViewById(R.id.layoutBookingTurnTemp);

        if (flag.equalsIgnoreCase("B"))
        {
            layoutBookingTurn.setVisibility(View.VISIBLE);
            layoutBookingTurnTemp.setVisibility(View.GONE);

            setBookingForm (true);
            setTurnBooikngForm(mPreference.getOrderBookingTurn());
            mPreference.setOrderType("B");

            doSetBookingDateTime (mPreference.getOrderuseCause());
            // 2018.07.03 CKS
            // 예약가능시간
            Calendar today = Calendar.getInstance();
            int hour = today.get(Calendar.HOUR_OF_DAY);
            if (hour < mPreference.getOrderBookingAvailableStime() || hour > mPreference.getOrderBookingAvailableEtime()) {

                String temp = String.format("예약접수 가능시간은 %d 시부터 %d 시까지 입니다.",
                        mPreference.getOrderBookingAvailableStime(),
                        mPreference.getOrderBookingAvailableEtime());
                doAlertDialogView("알림", temp);
            }
        } else {
            setBookingForm (false);
            setTurnBooikngForm (false);

            layoutBookingTurn.setVisibility(View.GONE);
            layoutBookingTurnTemp.setVisibility(View.VISIBLE);

            mPreference.setOrderType("S");
        }
    }

    /**
     * 예약 처리
     * @param flag
     */
    private void setBookingForm(boolean flag)
    {
        LinearLayout booking_date = (LinearLayout)findViewById(R.id.layoutBookingDate);
        LinearLayout booking_time = (LinearLayout)findViewById(R.id.layoutBookingTime);
        LinearLayout booking_temp = (LinearLayout)findViewById(R.id.layoutBookingTemp);

        if (flag)
        {
            booking_date.setVisibility(View.VISIBLE);
            booking_time.setVisibility(View.VISIBLE);
            booking_temp.setVisibility(View.GONE);
        } else {
            booking_date.setVisibility(View.GONE);
            booking_time.setVisibility(View.GONE);
            booking_temp.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 왕복여부 체크
     * @param flag
     */
    private void setTurnBooikngForm(boolean flag)
    {
        LinearLayout booking_date = (LinearLayout)findViewById(R.id.layoutTurnBookingDate);
        LinearLayout booking_time = (LinearLayout)findViewById(R.id.layoutTurnBookingTime);
        LinearLayout booking_temp = (LinearLayout)findViewById(R.id.layoutBookingTemp2);

        if (flag)
        {
            booking_date.setVisibility(View.VISIBLE);
            booking_time.setVisibility(View.VISIBLE);
            booking_temp.setVisibility(View.GONE);
        } else {
            booking_date.setVisibility(View.GONE);
            booking_time.setVisibility(View.GONE);
            booking_temp.setVisibility(View.VISIBLE);

            TextView tv = (TextView) findViewById(R.id.txtBookingTurnDate);
            tv.setText("");
            mPreference.setOrderBookingTurnDate("");
            tv = (TextView) findViewById(R.id.txtBookingTurnTime);
            tv.setText("");
            mPreference.setOrderBookingTurnTime("");
        }
    }

    /**
     * 예약날자 설정
     */
    private void setBookingDate()
    {
        TextView tv;
        if (mBookingPicker) {
            tv = (TextView) findViewById(R.id.txtBookingDate);
            tv.setText(mPreference.getOrderBookingDate());
        } else {
            tv = (TextView) findViewById(R.id.txtBookingTurnDate);
            tv.setText(mPreference.getOrderBookingTurnDate());
        }
    }

    /**
     * 예약시간 설정
     */
    private void setBookingTime()
    {
        TextView tv;
        if (mBookingPicker) {
            tv = (TextView) findViewById(R.id.txtBookingTime);
            tv.setText(mPreference.getOrderBookingTime());
        } else {
            tv = (TextView) findViewById(R.id.txtBookingTurnTime);
            tv.setText(mPreference.getOrderBookingTurnTime());
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.txtFromHint:
                doPOISearch ("출발지 검색", true, MSG_POI_SEART_START);
                break;
            case R.id.txtFromTitle:
            case R.id.txtFromDong:         // 출발지 검색
            case R.id.txtFromSiDo:
                doPOISearch ("출발지 검색", true, MSG_POI_SEART_START);
                break;
            case R.id.txtToHint:
                doPOISearch ("도착지 검색", false, MSG_POI_SEART_END);
                break;
            case R.id.txtTotitle:
            case R.id.txtToSiDO:
            case R.id.txtToDong:
                doPOISearch ("도착지 검색", false, MSG_POI_SEART_END);
                break;
            case R.id.txtToHint1:
                doPOISearch ("도착지 검색", false, MSG_POI_SEART_END);
                break;
            case R.id.txtTotitle1:
            case R.id.txtToSiDO1:
            case R.id.txtToDong1:
                doPOISearch ("도착지 검색", false, MSG_POI_SEART_END);
                break;

            case R.id.btnFromMap:           // 출발지 위치
                doViewMapPin(true);
                break;
            case R.id.btnToMap1:            // 도착지 위치 -> 출발지
                doViewMapPin(true);
                break;
            case R.id.btnToMap:             // 출발지 -> 도착지 위치
                doViewMapPin(false);
                break;
            case R.id.btnFromPointChange:
            case R.id.btnToPointChange:
            case R.id.btnToPointChange1:     // 출/도착지 위치 바꾸기
                doPointChange();
                break;
            case R.id.checkTurnBooking:     // 왕복 체크
                mPreference.setOrderBookingTurn(((CheckBox)view).isChecked());
                setTurnBooikngForm (((CheckBox)view).isChecked());
                break;
            case R.id.btnFromCalendar:      // 예약 날자
                mBookingPicker = true;
                doDatepicker ();
                break;
            case R.id.btnFromClock:         // 예약 시간
                mBookingPicker = true;
                doTimepicker ();
                break;
            case R.id.btnToCalendar:      // 왕복 예약 날자
                mBookingPicker = false;
                doDatepicker ();
                break;
            case R.id.btnToClock:         // 왕복 예약 시간
                mBookingPicker = false;
                doTimepicker ();
                break;
            case R.id.btnCenter:        // 접수하기
                doOrder ();
                break;

            case R.id.menuItemCallHistory:  // 내콜 이력
                closeLeftMenu();
                doSubMenuActivity (CallHistoryActivity.class);
                break;
            case R.id.menuItemBookmark:     // 즐겨찾기
                closeLeftMenu();
                viewBookmarkDialog();
                break;
            case R.id.menuItemInfo:
                LinearLayout layoutInfo = (LinearLayout)findViewById(R.id.layoutItemInfo);
                TextView tv = (TextView)findViewById(R.id.menuItemInfoIcon);
                if (layoutInfo.getVisibility() == View.VISIBLE) {
                    layoutInfo.setVisibility(View.GONE);
                    tv.setText(getResources().getString(R.string.fa_chevron_down));
                }
                else {
                    layoutInfo.setVisibility(View.VISIBLE);
                    tv.setText(getResources().getString(R.string.fa_chevron_up));
                }
                break;
            case R.id.menuItemEtc:
                LinearLayout layoutEtc = (LinearLayout)findViewById(R.id.layoutItemEtc);
                TextView textView = (TextView)findViewById(R.id.menuItemEtcIcon);
                if (layoutEtc.getVisibility() == View.VISIBLE) {
                    layoutEtc.setVisibility(View.GONE);
                    textView.setText(getResources().getString(R.string.fa_chevron_down));
                } else {
                    layoutEtc.setVisibility(View.VISIBLE);
                    textView.setText(getResources().getString(R.string.fa_chevron_up));
                }
                break;
            case R.id.etcMenu1:     // 서비스 이용약관
                doProvisionView (getString(R.string.provision_view2), "서비스 이용약관");
                break;
            case R.id.etcMenu2:     // 개인정보 보호
                doProvisionView (getString(R.string.provision_view3), "개인정보 취급방침");
                break;
            case R.id.etcMenu3:     // 위치기반
                doProvisionView (getString(R.string.provision_view4), "위치기반 서비스 약관");
                break;
            case R.id.etcMenu4:     // 서약서
                doProvisionView (getString(R.string.provision_view1), "서약서");
                break;
            case R.id.etcMenu5:     // 고유식별정보 수집 동의
                doProvisionView (getString(R.string.provision_view5), "고유식별정보 수집 동의");
                break;
            case R.id.btnStartEndMapview:   // 출도착지 지도보기
                if (mPreference.getOrderFromSelect() && mPreference.getOrderToSelect()) {
                    closeLeftMenu();
                    Intent intent = new Intent(ProposeMapActivity.this, TraceMapActivity.class);
                    intent.putExtra("mapviewstate", false);
                    startActivity(intent);
                } else {
                    Toast.makeText(getBaseContext(), "출발지와 도착지가 지정되지 않았습니다.", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.infoNotisetting:      // 공지사항 알림 설정
                doSubMenuActivity (NotiSettingActivity.class);
                break;

            case R.id.infoUseNote:  // 이용안내
                doInfoMessageView ("이용안내", GlobalValues.INFO_VIEW_USE_NOTICE);
                break;
            case R.id.infoService:  // 서비스 문의
                doInfoMessageView ("서비스 문의", GlobalValues.INFO_VIEW_SERVICE_NOTICE);
                break;
            case R.id.infoVersion:  // 버전정보
                doInfoMessageView ("버전정보", GlobalValues.INFO_VIEW_VERSION_NOTICE);
                break;

        }
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_DATE_PICK:
                    setBookingDate ();
                    break;
                case MSG_TIME_PICK:
                    setBookingTime();
                    break;
                case MSG_MAP_POSITION_CHAGE:
                    setFromToAddress ();
                    break;
                case MSG_ORDER_FAILED:
                    String log = (String)msg.obj;
                    doAlertDialogView ("알림", log);
                    break;
                case MSG_ORDER_SUCCESS:
                    // 대기 화면
                    doOrderWaitView ();
                    break;
                case MSG_BOOKINGORDER_SUCCESS:
                    doAlertDialogView ("알림", getResources().getString(R.string.booking_order));
                    break;
                case MSG_SUCCESS_USELIST:
                    doViewUseList ();
                    break;
                case MSG_USE_TYPE_CHANGE:
                    doSetBookingDateTime (msg.arg1);
                    TextView tv = (TextView) findViewById(R.id.txtBookingDate);
                    tv.setText("");
                    mPreference.setOrderBookingDate("");
                    tv = (TextView) findViewById(R.id.txtBookingTime);
                    tv.setText("");
                    mPreference.setOrderBookingTime("");

                    break;
            }
        }
    };

    /**
     * 예약 날짜와 시간 설정.
     */
    private void doSetBookingDateTime (int index)
    {
        Logview.Logwrite(THIS_TAG, "doSetBookingDateTime");
        if (mUseType == null && mUseType.size() < index)
            return;

        USETYPE_ITEM item = mUseType.get(index);
        if (item != null) {
            Logview.Logwrite(THIS_TAG, "use type start date : " + item.getStartDate());
            Logview.Logwrite(THIS_TAG, "use type end date : " + item.getEndDate());
            Logview.Logwrite(THIS_TAG, "use type start time : " + item.getStartTime());
            Logview.Logwrite(THIS_TAG, "use type end time : " + item.getEndTime());
            Logview.Logwrite(THIS_TAG, "use type round : " + item.getBookingRound());

            mOrderAvailableTime.reservedStartDate = item.getStartDate();
            mOrderAvailableTime.reservedStartTime = item.getStartTime();
            mOrderAvailableTime.reservedEndDate = item.getEndDate();
            mOrderAvailableTime.reservedEndTime = item.getEndTime();
            //mOrderAvailableTime.bset = mOrderAvailableTime.setReservedStartEndTime();

            LinearLayout layoutBookingTurn = (LinearLayout)findViewById(R.id.layoutBookingTurn);
            LinearLayout layoutBookingTurnTemp = (LinearLayout)findViewById(R.id.layoutBookingTurnTemp);

            if (item.getBookingRound())
            {
                layoutBookingTurn.setVisibility(View.VISIBLE);
                layoutBookingTurnTemp.setVisibility(View.GONE);

                setTurnBooikngForm (mPreference.getOrderBookingTurn());
            } else {
                CheckBox check = (CheckBox)findViewById(R.id.checkTurnBooking);
                check.setChecked(false);

                layoutBookingTurn.setVisibility(View.GONE);
                layoutBookingTurnTemp.setVisibility(View.VISIBLE);

                mPreference.setOrderBookingTurn(false);
                setTurnBooikngForm (false);
            }
        }
    }

    private DialogBookmark mBookmarkDiaglog;
    private void viewBookmarkDialog ()
    {
        mBookmarkDiaglog = new DialogBookmark(this, dialogReceiveHandler);
        mBookmarkDiaglog.show();
    }

    private DialogReceiveHandler dialogReceiveHandler = new DialogReceiveHandler()
    {
        @Override
        public void handlerReceive(Intent intent) {
            mBookmarkDiaglog.dismiss();
            boolean flag = intent.getBooleanExtra("flag", false);
            if (flag) {
                onDestroyMap ();
                setBookmarkSelectData(intent.getStringExtra("ordertype"), intent.getBundleExtra("data"));
            }
        }
    };

    /**
     * 북마크 데이터를 설치
     * @param orderType
     * @param bundle
     */
    private void setBookmarkSelectData(String orderType, Bundle bundle)
    {
        Logview.Logwrite(THIS_TAG, "setBookmarkSelectData : " + orderType);

        // 오더타입 설정
        SegmentedButton btnOrderType = (SegmentedButton)findViewById(R.id.segmented);
        if (orderType.equalsIgnoreCase("S"))
            btnOrderType.setPushedButtonIndex(0);
        else if (orderType.equalsIgnoreCase("B"))
            btnOrderType.setPushedButtonIndex(1);
        setOrderType(orderType);

        // 동승인인원
        String companion = bundle.getString("companion");
        if (companion != null && companion.length() > 0) {
            Spinner spinState = (Spinner) findViewById(R.id.spinerGetOnCount);
            spinState.setSelection(Integer.parseInt(companion));
        }

        // 이용목적
        String usetype = bundle.getString("usetype");
        if (usetype != null && usetype.length() > 0)
        {
            Spinner spinUseCause = (Spinner)findViewById(R.id.spinerUseCause);
            if(mUseType != null)
            {
                int i=0;
                for (USETYPE_ITEM item : mUseType)
                {
                    if (item.getEqualsCode(usetype)) {
                        spinUseCause.setSelection(i);
                        break;
                    }
                    i++;
                }
            }
        }

        // 출발지
        Logview.Logwrite(THIS_TAG, "getOrderFromX : " + bundle.getString("startPosx") + " / " + Double.parseDouble(bundle.getString("startPosx")));
        Logview.Logwrite(THIS_TAG, "getOrderFromX : " + bundle.getString("startPosy") + " / " + Double.parseDouble(bundle.getString("startPosy")));

        mPreference.setOrderFromSelect(true);
        mPreference.setOrderFromDetail(bundle.getString("startDetail"));
        mPreference.setOrderFromAddr(bundle.getString("startAddr"));
        mPreference.setOrderFromX(Double.parseDouble(bundle.getString("startPosx")));
        mPreference.setOrderFromY(Double.parseDouble(bundle.getString("startPosy")));

        LinearLayout layoutHint = (LinearLayout)findViewById(R.id.layoutFromhint);
        layoutHint.setVisibility(View.GONE);
        LinearLayout layoutFrom = (LinearLayout)findViewById(R.id.layoutFrom);
        layoutFrom.setVisibility(View.VISIBLE);

        // 도착지
        mPreference.setOrderToSelect(true);
        mPreference.setOrderToDetail(bundle.getString("endDetail"));
        mPreference.setOrderToAddr(bundle.getString("endAddr"));
        mPreference.setOrderToX(Double.parseDouble(bundle.getString("endPosx")));
        mPreference.setOrderToY(Double.parseDouble(bundle.getString("endPosy")));

        TextView textView = (TextView) findViewById(R.id.btnToMap);
        setTextViewFontAwesome(textView);
        textView.setVisibility(View.VISIBLE);
        textView = (TextView)findViewById(R.id.btnToMap1);
        setTextViewFontAwesome(textView);
        textView.setVisibility(View.VISIBLE);

        layoutHint = (LinearLayout)findViewById(R.id.layoutOrderHint);
        layoutHint.setVisibility(View.GONE);
        LinearLayout layoutTo = (LinearLayout)findViewById(R.id.layoutOrderTo);
        layoutTo.setVisibility(View.VISIBLE);

        LinearLayout layoutHint1 = (LinearLayout)findViewById(R.id.layoutOrderHint1);
        layoutHint1.setVisibility(View.GONE);
        LinearLayout layoutTo1 = (LinearLayout)findViewById(R.id.layoutOrderTo1);
        layoutTo1.setVisibility(View.VISIBLE);

        doViewMapPin (false);

        textView = (TextView) findViewById(R.id.btnFromPointChange);
        setTextViewFontAwesome (textView);
        textView.setOnClickListener(this);
        textView.setVisibility(View.VISIBLE);
        textView = (TextView)  findViewById(R.id.btnToPointChange);
        setTextViewFontAwesome (textView);
        textView.setOnClickListener(this);
        textView.setVisibility(View.VISIBLE);
        textView = (TextView)  findViewById(R.id.btnToPointChange1);
        setTextViewFontAwesome (textView);
        textView.setOnClickListener(this);
        textView.setVisibility(View.VISIBLE);
    }

    /**
     * 정보 서브 메뉴 내용
     */
    private void doInfoMessageView (String title, String type)
    {
        closeLeftMenu();

        Intent intent;
        intent = new Intent(ProposeMapActivity.this, InfoMessageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("title", title);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    /**
     * 배차대기 화면
     */
    private void doOrderWaitView()
    {
        Intent intent;
        // 배차 성공이면
        if (mReqCallInfo.callstate.toUpperCase().equalsIgnoreCase(GlobalValues.CALL_STATE_ALLOC))
        {
            intent = new Intent(ProposeMapActivity.this, TraceMapActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return;
        }

        intent = new Intent(ProposeMapActivity.this, OrderWaitActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // 대기
        if (mReqCallInfo.callstate.toUpperCase().equalsIgnoreCase(GlobalValues.CALL_STATE_ORDER))
            intent.putExtra("view_state", GlobalValues.ORDER_WAIT_VIEW_ORDER);
        else if (mReqCallInfo.callstate.toUpperCase().equalsIgnoreCase(GlobalValues.CALL_STATE_FAILED))
            intent.putExtra("view_state", GlobalValues.ORDER_WAIT_VIEW_FAILED);
        else
            return;

        intent.putExtra("drvseq", mReqCallInfo.drvseq);
        intent.putExtra("intervaltime", mReqCallInfo.intervaltime);
        startActivity(intent);
        finish();
    }

    /**
     * 서브메뉴 보기
     * @param activity
     */
    private void doSubMenuActivity(Class activity)
    {
        Intent intent = new Intent(ProposeMapActivity.this, activity);
        startActivity(intent);
    }

    /**
     * 사이드 메뉴 기타
     * @param url
     * @param title
     */
    private void doProvisionView(String url, String title)
    {
        closeLeftMenu();

        Intent intent = new Intent(ProposeMapActivity.this, ProvisionViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    /**
     * 이용목적 표시
     */
    private void doViewUseList()
    {
        Logview.Logwrite(THIS_TAG, "doViewUseList");
        // 이용목적
        String sql = "SELECT * FROM " + DBSchema.TABLE_NAME_USETYPE+ ";";
        SelectHelper select = DBControlManager.dbSelect(sql);

        if (select != null && select.getCount() > 0)
        {
            if (mUseType != null)
                mUseType.clear();
            mUseType = null;
            mUseType = new ArrayList<USETYPE_ITEM>();
            USETYPE_ITEM data;
            select.moveFirst();
            do {
                data = new USETYPE_ITEM(
                        select.getValue(DBSchema.COL_NAME), select.getValue(DBSchema.COL_CODE),
                        select.getValue("bookingstartdate"), select.getValue("bookingenddate"),
                        select.getValue("bookingstarttime"), select.getValue("bookingendtime"),
                        select.getValue("roundavailable"));
                mUseType.add(data);
            } while (select.moveNext());
        } else {
            doUseType();
        }

        ArrayList<String> useList = new ArrayList<String>();
        if (mUseType != null) {
            for (USETYPE_ITEM item : mUseType) {
                useList.add(item.getName());
            }
        }

        Spinner spinUseCause = (Spinner)findViewById(R.id.spinerUseCause);
        CustomSpinnerAdapter customSpinnerAdapter2 =new CustomSpinnerAdapter(getBaseContext(), useList);
        spinUseCause.setAdapter(customSpinnerAdapter2);
        spinUseCause.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mPreference.setOrderUseCause(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Logview.Logwrite(THIS_TAG, "initOrder getOrderuseCause : " + mPreference.getOrderuseCause());
        spinUseCause.setSelection(mPreference.getOrderuseCause());
    }

    /**
     * POI 검색
     */
    private void doPOISearch (String title, boolean flag, int request_code)
    {
        Intent intent = new Intent(ProposeMapActivity.this, POISearchActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("flag", flag);

        intent.putExtra("poiname", mPOIName);
        intent.putExtra("addr", mPOIJibunAddr);
        intent.putExtra("latitude", mPOILatitude);
        intent.putExtra("longitude", mPOILongitude);

        startActivityForResult(intent, request_code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            if (requestCode == MSG_POI_SEART_START)
            {
                // 출발지 검색 결과
                mPreference.setOrderFromSelect(true);
                mPreference.setOrderFromDetail(data.getStringExtra("detail"));
                mPreference.setOrderFromAddr(data.getStringExtra("addr"));
                mPreference.setOrderFromX(Double.parseDouble(data.getStringExtra("xpos")));
                mPreference.setOrderFromY(Double.parseDouble(data.getStringExtra("ypos")));

                LinearLayout layoutHint = (LinearLayout)findViewById(R.id.layoutFromhint);
                layoutHint.setVisibility(View.GONE);
                LinearLayout layoutTo = (LinearLayout)findViewById(R.id.layoutFrom);
                layoutTo.setVisibility(View.VISIBLE);

                doViewMapPin (true);

                // POI History db
                if (!findPISHistory(data.getStringExtra("detail"))) {
                    String sql = String.format("INSERT INTO %s (detailAddress, roadFullAddress, jibunFullAddress, posx, posy, type, regdate ) "
                                    + " VALUES ( '%s', '%s','%s', '%s','%s', '%s','%s');", DBSchema.TABLE_POI_HISTORY, data.getStringExtra("detail"), data.getStringExtra("addr"), data.getStringExtra("addr"),
                            data.getStringExtra("xpos"), data.getStringExtra("ypos"), "출발", Util.getCurrentDateTime("yyyyMMdd"));
                    DBControlManager.dbTransaction(sql);
                }
            }
            else if (requestCode == MSG_POI_SEART_END)
            {
                // 도착지 검색 결과
                mPreference.setOrderToSelect(true);
                mPreference.setOrderToDetail(data.getStringExtra("detail"));
                mPreference.setOrderToAddr(data.getStringExtra("addr"));
                mPreference.setOrderToX(Double.parseDouble(data.getStringExtra("xpos")));
                mPreference.setOrderToY(Double.parseDouble(data.getStringExtra("ypos")));

                TextView textView = (TextView) findViewById(R.id.btnToMap);
                setTextViewFontAwesome(textView);
                textView.setVisibility(View.VISIBLE);
                textView = (TextView)findViewById(R.id.btnToMap1);
                setTextViewFontAwesome(textView);
                textView.setVisibility(View.VISIBLE);

                LinearLayout layoutHint = (LinearLayout)findViewById(R.id.layoutOrderHint);
                layoutHint.setVisibility(View.GONE);
                LinearLayout layoutTo = (LinearLayout)findViewById(R.id.layoutOrderTo);
                layoutTo.setVisibility(View.VISIBLE);

                LinearLayout layoutHint1 = (LinearLayout)findViewById(R.id.layoutOrderHint1);
                layoutHint1.setVisibility(View.GONE);
                LinearLayout layoutTo1 = (LinearLayout)findViewById(R.id.layoutOrderTo1);
                layoutTo1.setVisibility(View.VISIBLE);

                doViewMapPin (false);

                // POI History db
                if (!findPISHistory(data.getStringExtra("detail"))) {
                    String sql = String.format("INSERT INTO %s (detailAddress, roadFullAddress, jibunFullAddress, posx, posy, type, regdate ) "
                                    + " VALUES ( '%s', '%s','%s', '%s','%s', '%s','%s');", DBSchema.TABLE_POI_HISTORY, data.getStringExtra("detail"), data.getStringExtra("addr"), data.getStringExtra("addr"),
                            data.getStringExtra("xpos"), data.getStringExtra("ypos"), "도착", Util.getCurrentDateTime("yyyyMMdd"));
                    DBControlManager.dbTransaction(sql);
                }
            }

            if (requestCode == MSG_POI_SEART_START || requestCode == MSG_POI_SEART_END) {
                TextView textView = (TextView) findViewById(R.id.btnFromPointChange);
                setTextViewFontAwesome (textView);
                textView.setOnClickListener(this);
                textView.setVisibility(View.VISIBLE);
                textView = (TextView)  findViewById(R.id.btnToPointChange);
                setTextViewFontAwesome (textView);
                textView.setOnClickListener(this);
                textView.setVisibility(View.VISIBLE);
                textView = (TextView)  findViewById(R.id.btnToPointChange1);
                setTextViewFontAwesome (textView);
                textView.setOnClickListener(this);
                textView.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * POI History에 저장되어 있는가 확인.
     * @param detail
     * @return
     */
    private boolean findPISHistory(String detail)
    {
        String sql = String.format("SELECT * FROM %s WHERE detailAddress='%s';",
                DBSchema.TABLE_POI_HISTORY, detail);

        SelectHelper selectHelper = DBControlManager.dbSelect(sql);

        if (selectHelper == null || selectHelper.getCount() < 1) {
            Logview.Logwrite(THIS_TAG, "findPISHistory not found ");
            return false;
        }
        Logview.Logwrite(THIS_TAG, "findPISHistory found : " + selectHelper.getCount());

        return true;
    }

    /**
     * 출도착지 맵에서 설정
     * @param falg
     */
    private void doViewMapPin(boolean falg)
    {
        LinearLayout layoutTemp1 = (LinearLayout)findViewById(R.id.layoutTo1);
        LinearLayout layoutTemp2 = (LinearLayout)findViewById(R.id.layoutTo2);

        if (falg) {
            // 출발지 보기
            if (layoutTemp1.getVisibility() == View.VISIBLE)
            {
                layoutTemp1.setVisibility(View.GONE);
                layoutTemp2.setVisibility(View.VISIBLE);

                mMapviweState = true;
            }
        } else {
            // 도착지 보기
//            if (layoutTemp1.getVisibility() != View.VISIBLE)
//            {
                layoutTemp1.setVisibility(View.VISIBLE);
                layoutTemp2.setVisibility(View.GONE);

                mMapviweState = false;
//            } else {
//                layoutTemp1.setVisibility(View.GONE);
//                layoutTemp2.setVisibility(View.VISIBLE);
//
//                mMapviweState = true;
//            }
        }

        doShowMarker ();

        setFromToAddress ();
    }

    /**
     * 출,도착지 표시
     */
    private void setFromToAddress ()
    {
        Logview.Logwrite(THIS_TAG, "setFromToAddress");

        TextView txtFromDong = (TextView)findViewById(R.id.txtFromDong);
        TextView txtFromSiDo = (TextView)findViewById(R.id.txtFromSiDo);

        TextView txtToDong = (TextView)findViewById(R.id.txtToDong);
        TextView txtToSiDo = (TextView)findViewById(R.id.txtToSiDO);
        TextView txtToDong1 = (TextView)findViewById(R.id.txtToDong1);
        TextView txtToSiDo1 = (TextView)findViewById(R.id.txtToSiDO1);

        // 출발지
        txtFromDong.setText(mPreference.getOrderFromDetail());
        txtFromSiDo.setText(mPreference.getOrderFromAddr());

        // 도착지
        txtToDong.setText(mPreference.getOrderToDetail());
        txtToSiDo.setText(mPreference.getOrderToAddr());

        txtToDong1.setText(mPreference.getOrderToDetail());
        txtToSiDo1.setText(mPreference.getOrderToAddr());
    }

    /**
     * 출도착지 변경
     */
    private void doPointChange ()
    {
        // 출발지는 항상 용인시 관내여야 한다.
        if (mPreference.getOrderToSelect())
        {
            String addr = mPreference.getOrderToAddr();
            if (addr.indexOf("용인시") < 0)
            {
                Toast.makeText(getBaseContext(), "출발지는 용인시 관내만 가능합니다.", Toast.LENGTH_LONG).show();
                return;
            }
        }

        boolean formSelect = mPreference.getOrderFromSelect();
        String fromDetail = mPreference.getOrderFromDetail();
        String fromAddr = mPreference.getOrderFromAddr();
        double fromX = mPreference.getOrderFromX();
        double fromY = mPreference.getOrderFromY();

        mPreference.setOrderFromSelect(mPreference.getOrderToSelect());
        mPreference.setOrderFromDetail(mPreference.getOrderToDetail());
        mPreference.setOrderFromAddr(mPreference.getOrderToAddr());
        mPreference.setOrderFromX(mPreference.getOrderToX());
        mPreference.setOrderFromY(mPreference.getOrderToY());

        mPreference.setOrderToSelect(formSelect);
        mPreference.setOrderToDetail(fromDetail);
        mPreference.setOrderToAddr(fromAddr);
        mPreference.setOrderToX(fromX);
        mPreference.setOrderToY(fromY);

        if (mPreference.getOrderFromSelect())
        {
            LinearLayout layoutHint = (LinearLayout)findViewById(R.id.layoutFromhint);
            layoutHint.setVisibility(View.GONE);
            LinearLayout layoutTo = (LinearLayout)findViewById(R.id.layoutFrom);
            layoutTo.setVisibility(View.VISIBLE);
        } else {
            LinearLayout layoutHint = (LinearLayout)findViewById(R.id.layoutFromhint);
            layoutHint.setVisibility(View.VISIBLE);
            LinearLayout layoutTo = (LinearLayout)findViewById(R.id.layoutFrom);
            layoutTo.setVisibility(View.GONE);

            mMapviweState = false;
        }

        if (mPreference.getOrderToSelect())
        {
            TextView textView = (TextView)findViewById(R.id.btnToMap);
            setTextViewFontAwesome(textView);
            textView.setVisibility(View.VISIBLE);
            textView = (TextView)findViewById(R.id.btnToMap1);
            setTextViewFontAwesome(textView);
            textView.setVisibility(View.VISIBLE);

            LinearLayout layoutHint = (LinearLayout)findViewById(R.id.layoutOrderHint);
            layoutHint.setVisibility(View.GONE);
            LinearLayout layoutTo = (LinearLayout)findViewById(R.id.layoutOrderTo);
            layoutTo.setVisibility(View.VISIBLE);

            LinearLayout layoutHint1 = (LinearLayout)findViewById(R.id.layoutOrderHint1);
            layoutHint1.setVisibility(View.GONE);
            LinearLayout layoutTo1 = (LinearLayout)findViewById(R.id.layoutOrderTo1);
            layoutTo1.setVisibility(View.VISIBLE);
        } else {
            TextView textView = (TextView)findViewById(R.id.btnToMap);
            setTextViewFontAwesome(textView);
            textView.setVisibility(View.GONE);
            textView = (TextView)findViewById(R.id.btnToMap1);
            setTextViewFontAwesome(textView);
            textView.setVisibility(View.GONE);

            LinearLayout layoutHint = (LinearLayout)findViewById(R.id.layoutOrderHint);
            layoutHint.setVisibility(View.VISIBLE);
            LinearLayout layoutTo = (LinearLayout)findViewById(R.id.layoutOrderTo);
            layoutTo.setVisibility(View.GONE);

            LinearLayout layoutHint1 = (LinearLayout)findViewById(R.id.layoutOrderHint1);
            layoutHint1.setVisibility(View.VISIBLE);
            LinearLayout layoutTo1 = (LinearLayout)findViewById(R.id.layoutOrderTo1);
            layoutTo1.setVisibility(View.GONE);

            mMapviweState = true;
        }

        doViewMapPin(mMapviweState);
    }

    private int mYear, mMonth, mDay, mHour, mMinute;
    /**
     * 예약날자를 얻어온다.
     */
    private void doDatepicker()
    {
        Calendar now = Calendar.getInstance();
        mYear = now.get(Calendar.YEAR);
        mMonth = now.get(Calendar.MONTH);
        mDay = now.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                ProposeMapActivity.this,
                mYear,
                mMonth,
                mDay
        );

        Logview.Logwrite(THIS_TAG, "Order start date : " + mOrderAvailableTime.reservedStartDate);
        Logview.Logwrite(THIS_TAG, "Order end date : " + mOrderAvailableTime.reservedEndDate);

        int count = Util.getDiffDayCount("yyyy-MM-dd", mOrderAvailableTime.reservedStartDate, mOrderAvailableTime.reservedEndDate);
        int skip = Util.getDiffDayCount("yyyy-MM-dd", Util.getCurrentDateTime("yyyy-MM-dd"), mOrderAvailableTime.reservedStartDate);
        if (skip > 0) skip--;
        count ++;
        Logview.Logwrite(THIS_TAG, "Order count : " + count);

        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        Calendar[] days = new Calendar[count];
        for (int i=0; i<count; i++) {
            Calendar day = Calendar.getInstance();
            day.add(Calendar.DAY_OF_MONTH, i+1 + skip);
            days[i] = day;
        }

        dpd.setSelectableDays(days);

        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    private void doTimepicker ()
    {
//        Calendar now = Calendar.getInstance();
//        TimePickerDialog tpd = TimePickerDialog.newInstance(
//                ProposeMapActivity.this,
//                now.get(Calendar.HOUR_OF_DAY),
//                now.get(Calendar.MINUTE),
//                true
//        );
//        tpd.setVersion(TimePickerDialog.Version.VERSION_2);
//        //tpd.setTimeInterval(1,10);
//
//        tpd.show(getFragmentManager(), "Timepickerdialog");

        TimePickerDailog timePickerDailog = new TimePickerDailog(ProposeMapActivity.this,
                mOrderAvailableTime.reservedStartTime, mOrderAvailableTime.reservedEndTime,
                new TimePickerDailog.TimePickerListner() {
            @Override
            public void OnDoneButton(Dialog datedialog, int hour, int minute) {
                datedialog.dismiss();
                String time = String.format(Locale.KOREA, "%02d 시 %02d 분 ", hour, minute);

                Logview.Logwrite(THIS_TAG, "선택 시간 : " + hour + " 시  " + minute + " 분 ");

                if (mBookingPicker){
                    mPreference.setOrderBookingTime(time);
                    mPreference.setOrderBookingTimeSend(String.format(Locale.KOREA, "%02d:%02d", hour, minute));
                } else {
                    mPreference.setOrderBookingTurnTime(time);
                    mPreference.setOrderBookingTurnTimeSend(String.format(Locale.KOREA, "%02d:%02d", hour, minute));
                }

                handler.obtainMessage(MSG_TIME_PICK).sendToTarget();
            }

            @Override
            public void OnCancelButton(Dialog datedialog) {
                datedialog.dismiss();
            }
        });

        timePickerDailog.show();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth)
    {
        monthOfYear++;
        String date = String.format(Locale.KOREA, "%d년 %02d월 %02d일", year, monthOfYear, dayOfMonth);
        Logview.Logwrite(THIS_TAG, "선택 날짜 : " + year + " 년 " + monthOfYear + " 월 " + dayOfMonth + " 일 " + date);

        if (year < mYear ) {
            Toast.makeText(getBaseContext(), "날짜 입력이 잘못되었습니다.", Toast.LENGTH_LONG).show();
            return;
        }
        //171229 송명진 - 논리적오류 주석처리
//        if (monthOfYear < mMonth ) {
//            Toast.makeText(getBaseContext(), "날짜 입력이 잘못되었습니다.", Toast.LENGTH_LONG).show();
//            return;
//        }

        if (year == mYear && monthOfYear == mMonth && dayOfMonth <= mDay)
        {
            Toast.makeText(getBaseContext(), "예약 날짜는 오늘 이후로 설정 해야합니다.", Toast.LENGTH_LONG).show();
            return;
        }

        if (mBookingPicker)
        {
            mPreference.setOrderBookingDate(date);
            mPreference.setOrderBookingDateSend(String.format(Locale.KOREA, "%d-%02d-%02d", year, monthOfYear, dayOfMonth));
        } else {
            mPreference.setOrderBookingTurnDate(date);
            mPreference.setOrderBookingTurnDateSend(String.format(Locale.KOREA, "%d-%02d-%02d", year, monthOfYear, dayOfMonth));
        }

        handler.obtainMessage(MSG_DATE_PICK).sendToTarget();
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second)
    {
        String time = String.format(Locale.KOREA, "%02d 시 %02d 분 ", hourOfDay, minute);

        Logview.Logwrite(THIS_TAG, "선택 시간 : " + hourOfDay + " 시  " + minute + " 분 " + second + " 초 " + time);

        if (mBookingPicker){
            mPreference.setOrderBookingTime(time);
            mPreference.setOrderBookingTimeSend(String.format(Locale.KOREA, "%02d:%02d", hourOfDay, minute, second));
        } else {
            mPreference.setOrderBookingTurnTime(time);
            mPreference.setOrderBookingTurnTimeSend(String.format(Locale.KOREA, "%02d:%02d", hourOfDay, minute, second));
        }

        handler.obtainMessage(MSG_TIME_PICK).sendToTarget();
    }

    /**
     * 맵에서 위치 검색
     * @param mapState
     */
    public void doMapView(boolean mapState)
    {
        Logview.Logwrite(THIS_TAG, "doMapView : " + mapState);
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("mapviewstate", mMapviweState);

        if (mapState)
            startActivityForResult(intent, MSG_POI_SEART_START);
        else
            startActivityForResult(intent, MSG_POI_SEART_END);
    }

    private void doOrder ()
    {
        // 접수가능 시간인가?
        if (mPreference.getOrderType().equalsIgnoreCase("B")) {
            Calendar today = Calendar.getInstance();
            int hour = today.get(Calendar.HOUR_OF_DAY);
            if (hour < mPreference.getOrderBookingAvailableStime() || hour > mPreference.getOrderBookingAvailableEtime()) {
                // 2018.07.03 CKS
                // 예약가능시간
                String temp = String.format("예약접수 가능시간이 아닙니다.\n예약접수 가능시간은 %d 시부터 %d 시까지 입니다.",
                        mPreference.getOrderBookingAvailableStime(),
                        mPreference.getOrderBookingAvailableEtime());

                doAlertDialogView("알림", temp);
                return;
            }
        }

        mReqCallInfo.clear();

        // 출발지
        if (mPreference.getOrderFromSelect() == false)
        {
            Toast.makeText(getBaseContext(), "출발지를 지정하십시요.", Toast.LENGTH_LONG).show();
            return;
        }

        // 도착지
        if (mPreference.getOrderToSelect() == false)
        {
            Toast.makeText(getBaseContext(), "도착지를 지정하십시요.", Toast.LENGTH_LONG).show();
            return;
        } else {
            if (mPreference.getOrderType().equalsIgnoreCase("S")) {
                AvailableDestination mAvilableDestination = new AvailableDestination();

                if (mAvilableDestination != null)
                {
                    if (!mAvilableDestination.isAvailableDestination(mPreference.getOrderToAddr()))
                    {
                        Toast.makeText(getBaseContext(), "도착지는 용인시 관내 및 용인시 인접 지역만 가능합니다.", Toast.LENGTH_SHORT).show();
                        mAvilableDestination.terminate();
                        mAvilableDestination = null;
                        return;
                    }
                    mAvilableDestination.terminate();
                    mAvilableDestination = null;
                }
            }
        }

        // 동승인원
        String rideTogether = String.valueOf(mPreference.getOrderGetonCount());

        // 이용목적
        String usecode = mUseType.get(mPreference.getOrderuseCause()).getCode();

        // 휠체어사용
        String wheel = (mPreference.getOrderUseWheel() == true ? "Y" : "N");

        // 즉시 / 예약
        String orderType = mPreference.getOrderType();
        if (orderType.equalsIgnoreCase("S"))
        {
            ConfigPreference config = ConfigPreference.getInstance();
            config.setPreference(getBaseContext());

            startProgress();

            // 즉시 요청
            JSON_REQReqCall json = new JSON_REQReqCall(config.getAuthkey());
            json.setStart(mPreference.getOrderFromDetail(), String.valueOf(mPreference.getOrderFromX()), String.valueOf(mPreference.getOrderFromY()));
            json.setEnd(mPreference.getOrderToDetail(), String.valueOf(mPreference.getOrderToX()), String.valueOf(mPreference.getOrderToY()));
            json.setCompanion(rideTogether);
            json.setUseTypeCode(usecode);
            json.setWheelchaiarYN(wheel);

            RetrofitProcessManager.doRetrofitRequest(JSON_REQReqCall.PAGE_NAME, json.getParams());

        } else {
            // 예약 날짜
            String bookingdate = mPreference.getOrderBookingDateSend();
            if (bookingdate == null || bookingdate.length() < 1)
            {
                Toast.makeText(getBaseContext(), "예약날짜를 지정하십시요.", Toast.LENGTH_LONG).show();
                return;
            }

            // 예약시간
            String bookingtime = mPreference.getOrderBookingTimeSend();
            if (bookingtime == null || bookingtime.length() < 1)
            {
                Toast.makeText(getBaseContext(), "예약시간을 지정하십시요.", Toast.LENGTH_LONG).show();
                return;
            }

            // 왕복여부
            String turnyn = "N";
            String bookingturndate = "";
            String bookingturntime = "";
            if (mPreference.getOrderBookingTurn())
            {
                turnyn = "Y";

                // 왕복예약날짜
                bookingturndate = mPreference.getOrderBookingTurnDateSend();
                if (bookingturndate == null || bookingturndate.length() < 1)
                {
                    Toast.makeText(getBaseContext(), "왕복 예약날짜를 지정하십시요.", Toast.LENGTH_LONG).show();
                    return;
                }
                bookingturntime = mPreference.getOrderBookingTurnTimeSend();
                if (bookingturntime == null || bookingturntime.length() < 1)
                {
                    Toast.makeText(getBaseContext(), "왕복 예약시간을 지정하십시요.", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            ConfigPreference config = ConfigPreference.getInstance();
            config.setPreference(getBaseContext());

            startProgress();

            // 예약 요청
            JSON_REQReservReqCall json = new JSON_REQReservReqCall(config.getAuthkey());
            json.setStart(mPreference.getOrderFromDetail(), String.valueOf(mPreference.getOrderFromX()), String.valueOf(mPreference.getOrderFromY()));
            json.setEnd(mPreference.getOrderToDetail(), String.valueOf(mPreference.getOrderToX()), String.valueOf(mPreference.getOrderToY()));
            json.setCompanion(rideTogether);
            json.setUseTypeCode(usecode);
            json.setWheelchaiarYN(wheel);

            json.setStartDate(bookingdate, bookingtime);
            json.setRoundDate(turnyn, bookingturndate, bookingturntime);

            RetrofitProcessManager.doRetrofitRequest(JSON_REQReservReqCall.PAGE_NAME, json.getParams());
        }

        // 즐겨찾기에 추가
        // 1. 중복검사
        if (!doCheckBookmarkExist (true)) {
            // 2. 저장
            Logview.Logwrite(THIS_TAG, "getOrderFromX : " + mPreference.getOrderFromX() + " / " + String.valueOf(mPreference.getOrderFromX()));
            Logview.Logwrite(THIS_TAG, "getOrderFromY : " + mPreference.getOrderFromY() + " / " + String.valueOf(mPreference.getOrderFromY()));

            String sql = "INSERT INTO " + DBSchema.TABLE_CALL_HISTORY + DBSchema.CALL_HISTORY_COLUMN + " VALUES ('"
                    + mPreference.getOrderFromDetail() + "','" + mPreference.getOrderFromAddr() + "','"
                    + mPreference.getOrderToDetail() + "','" + mPreference.getOrderToAddr() + "','"
                    + rideTogether + "','" + String.valueOf(mPreference.getOrderFromX()) + "','" + String.valueOf(mPreference.getOrderFromY()) + "','"
                    + String.valueOf(mPreference.getOrderToX()) + "','" + String.valueOf(mPreference.getOrderToY()) + "','"
                    + mUseType.get(mPreference.getOrderuseCause()).getName() + "','"
                    + usecode + "','" + wheel + "','" + Util.getCurrentDateTime("yyyyMMddHHmm") + "');";

            if (DBControlManager.dbTransaction(sql))
                Logview.Logwrite(THIS_TAG, "저장 되었습니다.");
            else
                Logview.Logwrite(THIS_TAG, "저장이 실패 되었습니다.");
        } else {
            Logview.Logwrite(THIS_TAG, "같은 데이터가 이미 있음.");
        }
    }

    /**
     * 배차 가능시간 설정
     */
    private void doOrderAvailableTime ()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSON_REQCallAvailableTime json = new JSON_REQCallAvailableTime();
                RetrofitProcessManager.doRetrofitRequest(JSON_REQCallAvailableTime.PAGE_NAME, json.getParams());
            }
        }).start();
    }

    /**
     * Call history table - Bookmark table에 동일 혹은 데이터가 있는가 검사
     */
    private boolean doCheckBookmarkExist(boolean flag)
    {
        boolean bret = false;

        String sql = "SELECT * FROM " + DBSchema.TABLE_CALL_HISTORY;

        if (flag) {
            sql += " WHERE " + DBSchema.COL_BM_STARTDETAIL + "='" + mPreference.getOrderFromDetail()
                    + "' AND " + DBSchema.COL_BM_STARTX + "='" + String.valueOf(mPreference.getOrderFromX())
                    + "' AND " + DBSchema.COL_BM_STARTY + "='" + String.valueOf(mPreference.getOrderFromY())
                    + "' AND " + DBSchema.COL_BM_ENDDETAIL + "='" + mPreference.getOrderToDetail()
                    + "' AND " + DBSchema.COL_BM_ENDX + "='" + String.valueOf(mPreference.getOrderToX())
                    + "' AND " + DBSchema.COL_BM_ENDY + "='" + String.valueOf(mPreference.getOrderToY())
                    + "'";
        }

        sql += ";";

        Logview.Logwrite(THIS_TAG, sql);

        SelectHelper selectHelper = DBControlManager.dbSelect(sql);

        if (selectHelper != null && selectHelper.getCount() > 0)
        {
            Logview.Logwrite(THIS_TAG, "존재하거나 데이터가 있음");
            bret = true;
        } else {
            bret = false;
        }

        return bret;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Naver map
    //
    private NMapView mMapView;
    private MapContainerView mMapContainerView;
    private NMapOverlayManager mOverlayManager;
    private NMapViewerResourceProvider mMapViewerResourceProvider;
    private NMapLocationManager mMapLocationManager;
    private NMapController mMapController;

    private NMapCompassManager mMapCompassManager;
    private NMapMyLocationOverlay mMyLocationOverlay;

    private NMapPOIdataOverlay mFloatingPOIdataOverlay;
    private NMapPOIitem mFloatingPOIitem;
    private boolean mMapInit = false;   // 맵 초기화
    private void initMapview()
    {
        // create map view
        mMapView = (NMapView)findViewById(R.id.mapView);

        //  set a registered Client Id for Open MapViewer Library
        mMapView.setClientId (getResources().getString(R.string.naver_client_key));

        // initialize map view
        mMapView.setClickable(true);               // 맵 고정 false / 이동 true
        mMapView.setEnabled(true);                  // 뷰를 화면에 보인다.
//        mMapView.setFocusable(true);
//        mMapView.setFocusableInTouchMode(true);
//        mMapView.requestFocus();
        mMapView.setScalingFactor(2.5f);    // 화면 스케일 조정 - 폰트크게 하는 효과

        // register listener for map state changes
        mMapView.setOnMapStateChangeListener(onMapViewStateChangeListener);
        mMapView.setOnMapViewTouchEventListener(onMapViewTouchEventListener);
//        mMapView.setOnMapViewDelegate(onMapViewTouchDelegate);

        // use map controller to zoom in/out, pan and set map center, zoom level etc.
        mMapController = mMapView.getMapController();
        mMapController.setZoomLevel(13);

//        // use built in zoom controls
//        NMapView.LayoutParams lp = new NMapView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                                                            ViewGroup.LayoutParams.WRAP_CONTENT,
//                                                            NMapView.LayoutParams.BOTTOM_RIGHT);
//        mMapView.setBuiltInZoomControls(true, lp);

        // create resource provider
        mMapViewerResourceProvider = new NMapViewerResourceProvider(this);

        // set data provider listener
        super.setMapDataProviderListener(onDataProviderListener);

        // create overlay manager
        mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);

        // register callout overlay listener to customize it.
        mOverlayManager.setOnCalloutOverlayListener(onCalloutOverlayListener);

        // register callout overlay view listener to customize it.
        mOverlayManager.setOnCalloutOverlayViewListener(onCalloutOverlayViewListener);

        // location manager
        mMapLocationManager = new NMapLocationManager(this);
        mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);

        // compass manager
        mMapCompassManager = new NMapCompassManager(this);

        // create my location overlay
        mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);

        startMyLocation ();

        mMapInit = false;
    }

    private void onDestroyMap ()
    {
        Logview.Logwrite(THIS_TAG, "onDestroyMap");
        stopMyLocation ();

        if (mMapLocationManager != null) {
            mMapLocationManager.removeOnLocationChangeListener(onMyLocationChangeListener);
        }
        mMapLocationManager = null;
    }

    /**
     * 출도착지 마커 표시
     */
    public void doShowMarker()
    {
        double xp = 0.0, yp = 0.0;
        int poiFlagType=0;
        String detail = "";
        if (mMapviweState)
        {
            if (mPreference.getOrderFromSelect()) {
                mMyLocationOverlay.setHidden(true);
                xp = mPreference.getOrderFromX();
                yp = mPreference.getOrderFromY();
                detail = mPreference.getOrderFromDetail();

                poiFlagType = NMapPOIflagType.FROM;
            } else {
                mMyLocationOverlay.setHidden(false);
                Logview.Logwrite(THIS_TAG, "mMyLocationOverlay view : " + mMyLocationOverlay.isHidden());
                mMapController.animateTo(mMyLocation);
            }
        }
        else {
            if (mPreference.getOrderToSelect()) {
                mMyLocationOverlay.setHidden(true);
                xp = mPreference.getOrderToX();
                yp = mPreference.getOrderToY();
                detail = mPreference.getOrderToDetail();

                poiFlagType = NMapPOIflagType.TO;
            }
        }

        if (xp > 0 && yp > 0)
        {
            if (mMapView != null)
            {
                mOverlayManager.clearOverlays();

                NMapPOIdata poiData = new NMapPOIdata(1, mMapViewerResourceProvider);
                poiData.beginPOIdata(2);
                poiData.addPOIitem(xp, yp, detail, poiFlagType, 0);
                poiData.endPOIdata();

                NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
                poiDataOverlay.showAllPOIdata(0);
            }
        }
    }

    /**
     * 위치 측위 시작
     */
    private void startMyLocation()
    {
        if (mMyLocationOverlay != null)
        {
            if (!mOverlayManager.hasOverlay(mMyLocationOverlay)) {
                mOverlayManager.addOverlay(mMyLocationOverlay);
            }

            mMyLocationOverlay.setCompassHeadingVisible(true);
            mMapCompassManager.enableCompass();

            if (mMapLocationManager.isMyLocationEnabled())
            {
                if (!mMapView.isAutoRotateEnabled())
                {
                    //mMyLocationOverlay.setCompassHeadingVisible(true);

                    mMapCompassManager.enableCompass();

                    mMapView.setAutoRotateEnabled(true, false);

                } else {
                    stopMyLocation();
                }

                mMapView.postInvalidate();
            } else {
                boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(false);
                if (!isMyLocationEnabled)
                {
//                    Toast.On("Please enable a My Location source in system settings");
                    Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(goToSettings);

                    return;
                }
            }
        }
    }

    /**
     * 위치 측위 종료
     */
    private void stopMyLocation()
    {
        Logview.Logwrite(THIS_TAG, "stopMyLocation");
        if (mMyLocationOverlay != null) {
            if (mMapLocationManager != null)
                mMapLocationManager.disableMyLocation();

            if (mMapView.isAutoRotateEnabled()) {
                mMyLocationOverlay.setCompassHeadingVisible(false);

                mMapCompassManager.disableCompass();

                mMapView.setAutoRotateEnabled(false, false);

                mMapContainerView.requestLayout();
            }
        }
    }

    /* MapView State Change Listener*/
    private final NMapView.OnMapStateChangeListener onMapViewStateChangeListener = new NMapView.OnMapStateChangeListener()
    {
        @Override
        public void onMapInitHandler(NMapView nMapView, NMapError nMapError)
        {
            if (nMapError == null) { // success
                Logview.Logwrite(THIS_TAG, "onMapViewStateChangeListener : mapview success");
                mMapInit = true;
                // restore map view state such as map center position and zoom level.
                //restoreInstanceState();
                if (!mSearchAddress)
                    getNaverChangeAddress (nMapView.getMapController().getMapCenter());
            } else { // fail
                Logview.Logwrite(THIS_TAG, "onFailedToInitializeWithError: " + nMapError.toString());

                //Toast.makeText(NMapViewer.this, errorInfo.toString(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onMapCenterChange(NMapView nMapView, NGeoPoint nGeoPoint) {

        }

        @Override
        public void onMapCenterChangeFine(NMapView nMapView) {

        }

        @Override
        public void onZoomLevelChange(NMapView nMapView, int level) {
            Logview.Logwrite(THIS_TAG, "onZoomLevelChange: " + level);
        }

        @Override
        public void onAnimationStateChange(NMapView nMapView, int animType, int animState) {

        }
    };

    /* Map touch event Listener*/
    private final NMapView.OnMapViewTouchEventListener onMapViewTouchEventListener = new NMapView.OnMapViewTouchEventListener()
    {
        @Override
        public void onLongPress(NMapView nMapView, MotionEvent motionEvent) {

        }

        @Override
        public void onLongPressCanceled(NMapView nMapView) {

        }

        @Override
        public void onTouchDown(NMapView nMapView, MotionEvent motionEvent) {
            Logview.Logwrite(THIS_TAG, "onTouchDown : ");
        }

        @Override
        public void onTouchUp(NMapView nMapView, MotionEvent motionEvent) {
            Logview.Logwrite(THIS_TAG, "onTouchUp : ");
            doMapView (mMapviweState);
        }

        @Override
        public void onScroll(NMapView nMapView, MotionEvent motionEvent, MotionEvent motionEvent1) {
            Logview.Logwrite(THIS_TAG, "onScroll : ");
        }

        @Override
        public void onSingleTapUp(NMapView nMapView, MotionEvent motionEvent) {
            Logview.Logwrite(THIS_TAG, "onSingleTapUp : ");
        }
    };

    private final NMapView.OnMapViewDelegate onMapViewTouchDelegate = new NMapView.OnMapViewDelegate()
    {
        @Override
        public boolean isLocationTracking() {
            if (mMapLocationManager != null) {
                if (mMapLocationManager.isMyLocationEnabled()) {
                    return mMapLocationManager.isMyLocationFixed();
                }
            }
            return false;
        }

    };

    /* MyLocation Listener */
    private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener()
    {
        @Override
        public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {

            if (mMapController != null) {
                if (!mSearchAddress)
                    getNaverChangeAddress (myLocation);
                mMapController.animateTo(myLocation);
            }

            return true;
        }

        @Override
        public void onLocationUpdateTimeout(NMapLocationManager locationManager) {

            // stop location updating
            //			Runnable runnable = new Runnable() {
            //				public void run() {
            //					stopMyLocation();
            //				}
            //			};
            //			runnable.run();

            Toast.makeText(ProposeMapActivity.this, "Your current location is temporarily unavailable.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation)
        {
            stopMyLocation();
        }
    };

    /* NMapDataProvider Listener */
    private final OnDataProviderListener onDataProviderListener = new OnDataProviderListener()
    {
        @Override
        public void onReverseGeocoderResponse(NMapPlacemark placeMark, NMapError errInfo)
        {
            Logview.Logwrite(THIS_TAG, "onReverseGeocoderResponse: placeMark="
                        + ((placeMark != null) ? placeMark.toString() : null));

            if (errInfo != null)
            {
                Logview.Logwrite(THIS_TAG, "Failed to findPlacemarkAtLocation: error=" + errInfo.toString());

                return;
            }

            if (mFloatingPOIitem != null && mFloatingPOIdataOverlay != null) {
                mFloatingPOIdataOverlay.deselectFocusedPOIitem();

                if (placeMark != null) {
                    mFloatingPOIitem.setTitle(placeMark.toString());
                }
                mFloatingPOIdataOverlay.selectPOIitemBy(mFloatingPOIitem.getId(), false);
            }
        }

    };

    private final NMapOverlayManager.OnCalloutOverlayListener onCalloutOverlayListener = new NMapOverlayManager.OnCalloutOverlayListener()
    {
        @Override
        public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay nMapOverlay, NMapOverlayItem nMapOverlayItem, Rect itemBounds)
        {
            return new NMapCalloutCustomOverlay(nMapOverlay, nMapOverlayItem, itemBounds, mMapViewerResourceProvider);
        }
    };

    private final NMapOverlayManager.OnCalloutOverlayViewListener onCalloutOverlayViewListener = new NMapOverlayManager.OnCalloutOverlayViewListener()
    {
        @Override
        public View onCreateCalloutOverlayView(NMapOverlay nMapOverlay, NMapOverlayItem nMapOverlayItem, Rect rect)
        {
            // null을 반환하면 말풍선 오버레이를 표시하지 않음
            return null;
        }
    };

    ///////////////////////////////////////////////////////////////////////////////////////////
    // 좌표 주소 변환
    //
    private NGeoPoint mMyLocation;
    private void getNaverChangeAddress(final NGeoPoint location)
    {
        mSearchAddress = true;
        mMyLocation = location;

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 검색실시
                JSON_REQReverseGeocoding json = new JSON_REQReverseGeocoding(String.valueOf(location.longitude), String.valueOf(location.latitude));
                RetrofitProcessManager.doRetrofitRequest(JSON_REQReverseGeocoding.PAGE_NAME, json.getParams());
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
        Logview.Logwrite(THIS_TAG, "jsonMessageEvent 수신 상태 : " + event.getMessageStatus());
        if (event.getMessageStatus()) {
            String pageName = event.getPageName();
            Logview.Logwrite(THIS_TAG, "jsonMessageEvent page : " + pageName);

            if (pageName != null) {
                if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.ReverseGeocoding.name().toLowerCase())) {
                    receiveReverseGeocoding(event);
                }
                else if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.ReqCall.name().toLowerCase())) {
                    stopProgress ();

                    receiveReqCall(event);
                }
                else if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.ReservReqCall.name().toLowerCase())) {
                    stopProgress ();

                    receiveReservReqCall(event);
                }
                else if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.UseTypeAvailTimeList.name().toLowerCase())) {
                    receiveUseTypeList(event);
                }
                else if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.CallAvailableTime.name().toLowerCase())) {
                    receiveCallAvailableTime(event);
                }

            } else {
                Logview.Logwrite(THIS_TAG, "Page name not found");
                stopProgress ();

                handler.sendEmptyMessage(MSG_HTTP_FAILED);
            }
        } else {
            stopProgress ();

            handler.sendEmptyMessage(MSG_HTTP_FAILED);
        }
    }

    /**
     * 좌표롤 주소 얻어오기
     * @param event
     */
    private void receiveReverseGeocoding(JSONMessageEvent event)
    {
        Gson gson = new Gson();

        REP_JSONReverseGeocoding json = gson.fromJson(event.getMessageBody(), REP_JSONReverseGeocoding.class);
        if (json.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success)))
        {
            Logview.Logwrite(THIS_TAG, "receiveReverseGeocoding success");
        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receiveReverseGeocoding false : " + json.getBody().getCause());
            mSearchAddress = false;

            return;
        }

        Logview.Logwrite(THIS_TAG, "Total count : "+ json.getBody().getTotalCount());

        List<REP_JSONReverseGeocoding.Body.ITEM> list = json.getBody().getItemList();
        if (list != null && list.size() > 0)
        {
            REP_JSONReverseGeocoding.Body.ITEM item = list.get(0);

            Logview.Logwrite(THIS_TAG, "poiname : "+ item.poiName);
            Logview.Logwrite(THIS_TAG, "jibunFullAddr : "+ item.jibunFullAddr);
            Logview.Logwrite(THIS_TAG, "roadFullAddr : "+ item.roadFullAddr);
            Logview.Logwrite(THIS_TAG, "longitude : "+ item.longitude);
            Logview.Logwrite(THIS_TAG, "latitude : "+ item.latitude);

            if (mPreference.getOrderFromSelect() == false) {
                mPreference.setOrderFromDetail(item.poiName);
                mPreference.setOrderFromAddr(item.jibunFullAddr);
                mPreference.setOrderFromX(mMyLocation.longitude);
                mPreference.setOrderFromY(mMyLocation.latitude);

                mPOIName = item.poiName;
                mPOIJibunAddr = item.jibunFullAddr;
                mPOILatitude = mMyLocation.latitude;
                mPOILongitude = mMyLocation.longitude;
            }

            handler.obtainMessage(MSG_MAP_POSITION_CHAGE).sendToTarget();
        }
        mSearchAddress = false;
    }
    private String mPOIName="", mPOIJibunAddr="";
    private double mPOILatitude=0, mPOILongitude=0;

    /**
     * 즉시 배차 응답
     * @param event
     */
    private void receiveReqCall(JSONMessageEvent event)
    {
        Gson gson = new Gson();

        REP_JSONReqCall json = gson.fromJson(event.getMessageBody(), REP_JSONReqCall.class);
        if (json.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success)))
        {
            Logview.Logwrite(THIS_TAG, "receiveReqCall success");
        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receiveReqCall false : " + json.getBody().getCause());
            handler.obtainMessage(MSG_ORDER_FAILED,
                    ErrorcodeToString.getError(json.getBody().getResult(),
                            json.getBody().getCause())).sendToTarget();
            return;
        }

        String callid = json.getBody().getCallid();
        String callstate = json.getBody().getCallState();
        String drvseq = json.getBody().getDrvSEQ();
        String inervaltime = json.getBody().getIntervalTime();

        Logview.Logwrite(THIS_TAG, "receiveReqCall callid : " + callid);
        Logview.Logwrite(THIS_TAG, "receiveReqCall callstate : " + callstate);
        Logview.Logwrite(THIS_TAG, "receiveReqCall drvseq : " + drvseq);
        Logview.Logwrite(THIS_TAG, "receiveReqCall inervaltime : " + inervaltime);

        mReqCallInfo.callid = callid;
        mReqCallInfo.callstate = callstate;
        mReqCallInfo.drvseq = drvseq;
        mReqCallInfo.intervaltime = inervaltime;

        mPreference.setOrderCallid(json.getBody().getCallid());
        handler.sendEmptyMessage(MSG_ORDER_SUCCESS);
    }

    /**
     * 예약배차 응답
     * @param event
     */
    private void receiveReservReqCall(JSONMessageEvent event)
    {
        Gson gson = new Gson();

        REP_JSONReservReqCall json = gson.fromJson(event.getMessageBody(), REP_JSONReservReqCall.class);
        if (json.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success)))
        {
            Logview.Logwrite(THIS_TAG, "receiveReservReqCall success");
        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receiveReservReqCall false : " + json.getBody().getCause());
            handler.obtainMessage(MSG_ORDER_FAILED,
                    ErrorcodeToString.getError(json.getBody().getResult(),
                            json.getBody().getCause())).sendToTarget();
            return;
        }

        mPreference.setOrderCallid(json.getBody().getCallid());
        handler.sendEmptyMessage(MSG_BOOKINGORDER_SUCCESS);
    }

    /**
     * 메시지 출력
     * @param title
     * @param msg
     */
    public void doAlertDialogView (String title, String msg)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(ProposeMapActivity.this);
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

    class ITEM
    {
        public String name = "";
        public String code = "";

        public ITEM (String name, String code)
        {
            this.name = name;
            this.code = code;
        }

        public String getName () {return this.name;}
        public String getCode () {return this.code;}
        public boolean getEquals(String code)
        {
            return this.code.equalsIgnoreCase(code);
        }
    }

    class USETYPE_ITEM
    {
        public String name = "";
        public String code = "";
        public String startDate="";
        public String endDate="";
        public String startTime="";
        public String endTime="";
        public boolean bBookingRound = true;

        public USETYPE_ITEM (String name, String code, String sdate, String edate, String stime, String etime, String round)
        {
            this.name = name;
            this.code = code;

            this.startDate= sdate;
            this.endDate = edate;
            this.startTime = stime;
            this.endTime = etime;
            if (round != null && round.toUpperCase().equalsIgnoreCase("Y"))
                this.bBookingRound = true;
            else
                this.bBookingRound = false;
        }

        public boolean getEqualsCode(String code)
        {
            return this.code.equalsIgnoreCase(code);
        }

        public String getName () {return this.name;}
        public String getCode () {return this.code;}

        public String getStartDate () {return this.startDate;}
        public String getEndDate () {return this.endDate;}
        public String getStartTime () {return this.startTime;}
        public String getEndTime () {return this.endTime;}
        public boolean getBookingRound () {return this.bBookingRound;}
    }


    class REQ_CALL_INFO
    {
        public String callid="";
        public String callstate="";
        public String drvseq="";
        public String intervaltime="";

        public REQ_CALL_INFO ()
        {

        }

        public void clear()
        {
            this.callid = "";
            this.callstate ="";
            this.drvseq = "";
            this.intervaltime="";
        }
    }

    /**
     * 이용목적 요청
     */
    private void doUseType ()
    {
        Logview.Logwrite(THIS_TAG, "doUseTypeList");
        // 이용목적 목록
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSON_REQUseTypeList2 reqvalue = new JSON_REQUseTypeList2();
                RetrofitProcessManager.doRetrofitRequest(JSON_REQUseTypeList2.PAGE_NAME, reqvalue.getParams());
            }
        }).start();
    }

    /**
     * 이용목적 리스트
     * @param event
     */
    private void receiveUseTypeList (JSONMessageEvent event)
    {
        Gson gson = new Gson();

        final REP_JSONUseTypeList2 json = gson.fromJson(event.getMessageBody(), REP_JSONUseTypeList2.class);

        if (json.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success)))
        {
            // DB에 저장
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<REP_JSONUseTypeList2.Body.UseType> list = json.getBody().getUseTypes();
                    if (list != null && list.size() > 0)
                    {
                        // 기존에 있는 모든 자료 삭제
                        DBControlManager.dbTableClear(DBSchema.TABLE_NAME_USETYPE);

                        // 새포운 자료 입력
                        String sql = "";
                        for (REP_JSONUseTypeList2.Body.UseType item : list)
                        {
                            sql = String.format("INSERT INTO %s (name, code, realstartdate, realenddate, realstarttime, realendtime, " +
                                            "bookingstartdate, bookingenddate, bookingstarttime, bookingendtime, roundavailable) " +
                                            "VALUES ('%s', '%s','%s','%s','%s','%s', '%s','%s','%s','%s', '%s');"
                                    ,DBSchema.TABLE_NAME_USETYPE
                                    ,item.getUsetype(),item.getUsetypecode()
                                    ,item.getRealStartDate(), item.getRealEndDate(), item.getRealStartTime(), item.getRealEndTime()
                                    ,item.getBookingStartDate(), item.getBookingEndDate(), item.getBookingStartTime(), item.getBookingEndTime()
                                    ,item.getRoundAvilable());
                            DBControlManager.dbTransaction(sql);
                        }

                        handler.sendEmptyMessage(MSG_SUCCESS_USELIST);

                    } else {
                        Logview.Logwrite(THIS_TAG, "use type data null");
                    }
                }
            }).start();

        } else  {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receiveUseTypeList false : " + json.getBody().getCause());
        }
        Logview.Logwrite(THIS_TAG, "receiveUseTypeList success");
    }

    /**
     * 배차 가능시간 수신
     * @param event
     */
    private void receiveCallAvailableTime (JSONMessageEvent event)
    {
        Gson gson = new Gson();

        final REP_JSONCallAvailableTime json = gson.fromJson(event.getMessageBody(), REP_JSONCallAvailableTime.class);

        if (json.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success)))
        {
            Logview.Logwrite(THIS_TAG, "receiveCallAvailableTime success");
        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receiveCallAvailableTime false : " + json.getBody().getCause());
            return;
        }

        mOrderAvailableTime.startDate = json.getBody().getStartDate();
        mOrderAvailableTime.startTime = json.getBody().getStartTime();
        mOrderAvailableTime.endDate = json.getBody().getEndDate();
        mOrderAvailableTime.endTime = json.getBody().getEndTime();
        mOrderAvailableTime.bset = mOrderAvailableTime.setStarEndTime();
        if (mOrderAvailableTime.bset) {
            if (mPreference.getOrderType().equalsIgnoreCase("S")) {
                mOrderAvailableTime.reservedStartDate = json.getBody().getReservedStartDate();
                mOrderAvailableTime.reservedStartTime = json.getBody().getReservedStartTime();
                mOrderAvailableTime.reservedEndDate = json.getBody().getReservedEndDate();
                mOrderAvailableTime.reservedEndTime = json.getBody().getReservedEndTime();
                mOrderAvailableTime.bset = mOrderAvailableTime.setReservedStartEndTime();
            }
        }

        if (mOrderAvailableTime.bset)
            viewOrderAvailableTime ();
        else
            Logview.Logwrite(THIS_TAG, "배차가능 시간 에러");
    }

    /**
     * 즉시 배차 가능시간 보이기
     */
    private void viewOrderAvailableTime()
    {
        String orderType = mPreference.getOrderType();
        if (orderType != null && orderType.equalsIgnoreCase("S"))
        {
            Logview.Logwrite(THIS_TAG, "viewOrderAvailableTime start : " + mOrderAvailableTime.lStart
                    + " / end : " + mOrderAvailableTime.lEnd);

            String current = Util.getCurrentDateTime("yyyyMMddHHmm");
            long ncurrent = Long.parseLong(current);
            Logview.Logwrite(THIS_TAG, "viewOrderAvailableTime current : " + current);
            if (mOrderAvailableTime.lStart <= ncurrent && mOrderAvailableTime.lEnd >= ncurrent)
            {
                Logview.Logwrite(THIS_TAG, "viewOrderAvailableTime success : ");
            } else {
                Logview.Logwrite(THIS_TAG, "viewOrderAvailableTime false : ");

                String msg = "즉시 배차 가능 시간\n\n";
                msg += mOrderAvailableTime.startDate + " " + mOrderAvailableTime.startTime + " 부터\n";
                msg += mOrderAvailableTime.endDate + " " + mOrderAvailableTime.endTime + " 까지 입니다.\n\n";
                msg += "지금은 즉시 배차 가능 시간이 아닙니다.";
                doAlertDialogView ("알림", msg);
            }
        }
    }

    ////////////////////////////////////////////////////////////
    // side menu control
    //
    private void closeLeftMenu()
    {
        mDrawerLayout.closeDrawers();
    }

    private DrawerLayout.DrawerListener myDrawerListener = new DrawerLayout.DrawerListener()
    {
        public void onDrawerClosed(View drawerView)
        {
            Logview.Logwrite(THIS_TAG, "onDrawerClosed");
        }

        public void onDrawerOpened(View drawerView)
        {
            Logview.Logwrite(THIS_TAG, "onDrawerOpened");
        }

        public void onDrawerSlide(View drawerView, float slideOffset)
        {
            Logview.Logwrite(THIS_TAG, "onDrawerSlide: "
                    + String.format("%.2f", slideOffset));
        }

        public void onDrawerStateChanged(int newState)
        {
            String state;
            switch (newState) {
                case DrawerLayout.STATE_IDLE:
                    state = "STATE_IDLE";
                    break;
                case DrawerLayout.STATE_DRAGGING:
                    state = "STATE_DRAGGING";
                    break;
                case DrawerLayout.STATE_SETTLING:
                    state = "STATE_SETTLING";
                    break;
                default:
                    state = "unknown!";
            }

            Logview.Logwrite(THIS_TAG, state);
        }
    };

    /////////////////////////////////////////////////////////////////////////////////////
    // 배차 가능시간
    //
    class OrderAvailableTime
    {
        public boolean bset =false;
        public String startDate="";
        public String startTime="";
        public String endDate="";
        public String endTime="";
        public long lStart=0;
        public long lEnd = 0;

        public String reservedStartDate="";
        public String reservedStartTime="";
        public String reservedEndDate="";
        public String reservedEndTime="";
        public long lReservedStart=0;
        public long lReservedEnd = 0;

        public OrderAvailableTime ()
        {

        }

        public void setOrderAvailableTime()
        {
            OrderAvailableTimePreference preference = OrderAvailableTimePreference.getInstance();
            preference.setPreference(getBaseContext());

            startDate = preference.getOrderabailableStartdate();
            startTime = preference.getOrderabailableStarttime();
            endDate = preference.getOrderabailableEnddate();
            endTime = preference.getOrderabailableEndtime();

            bset = setStarEndTime();
            if (bset) {
                reservedStartDate = preference.getOrderabailableReservecdStartdate();
                reservedStartTime = preference.getOrderabailableReservecdStarttime();
                reservedEndDate = preference.getOrderabailableReservecdEnddate();
                reservedEndTime = preference.getOrderabailableReservecdEndtime();
                bset = setReservedStartEndTime();
            }
        }

        public boolean setStarEndTime()
        {
            if (startDate == null || startDate.length() < 1 || startTime == null || startTime.length() < 1 ||
                    endDate == null || endDate.length() < 1 || endTime == null || endTime.length() < 1
                    )
                return false;

            try {
                String start = startDate + startTime;
                String temp = start.replace("-", "");
                start = temp.replace(":", "");
                lStart = Long.parseLong(start);

                String end = endDate + endTime;
                temp = end.replace("-", "");
                end = temp.replace(":", "");
                lEnd = Long.parseLong(end);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        public boolean setReservedStartEndTime()
        {
            if (reservedStartDate == null || reservedStartDate.length() < 1 ||
                    reservedStartTime == null || reservedStartTime.length() < 1 ||
                    reservedEndDate == null || reservedEndDate.length() < 1 ||
                    reservedEndTime == null || reservedEndTime.length() < 1
                    )
                return false;

            try {
                String start = reservedStartDate + reservedStartTime;
                String temp = start.replace("-", "");
                start = temp.replace(":", "");
                lReservedStart = Long.parseLong(start);

                String end = reservedEndDate + reservedEndTime;
                temp = end.replace("-", "");
                end = temp.replace(":", "");
                lReservedEnd = Long.parseLong(end);
            } catch (NumberFormatException e){
                e.printStackTrace();
                return false;
            }

            return true;
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////
    // Progress 원형
    //
    private ProgressDialog mProgressDialog = null;
    private synchronized void startProgress()
    {
        stopProgress();

        mProgressDialog = ProgressDialog.show(ProposeMapActivity.this, "", "잠시 기다려주세요", true);
    }

    private synchronized void stopProgress()
    {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
        mProgressDialog = null;
    }

}
