package kr.or.hsnarae.transporthelp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.gson.Gson;
import com.ionemax.iomlibrarys.log.Logview;
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

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kr.or.hsnarae.transporthelp.common.FontAwesome.CFontAwesomeManager;
import kr.or.hsnarae.transporthelp.common.util.BusEventProvider;
import kr.or.hsnarae.transporthelp.common.util.ErrorcodeToString;
import kr.or.hsnarae.transporthelp.common.util.GlobalValues;
import kr.or.hsnarae.transporthelp.impl.BusEvent.ServiceMessageEvent;
import kr.or.hsnarae.transporthelp.impl.net.json.IJSONPageMethod;
import kr.or.hsnarae.transporthelp.impl.net.json.JSONMessageEvent;
import kr.or.hsnarae.transporthelp.impl.net.json.RetrofitProcessManager;
import kr.or.hsnarae.transporthelp.impl.net.json.rep.REP_JSONCallInfo;
import kr.or.hsnarae.transporthelp.impl.net.json.rep.REP_JSONCarPos;
import kr.or.hsnarae.transporthelp.impl.net.json.rep.REP_JSONReqCallCancel;
import kr.or.hsnarae.transporthelp.impl.net.json.rep.REP_JSONReverseGeocoding;
import kr.or.hsnarae.transporthelp.impl.net.json.req.JSON_REQCallInfo;
import kr.or.hsnarae.transporthelp.impl.net.json.req.JSON_REQCarPos;
import kr.or.hsnarae.transporthelp.impl.net.json.req.JSON_REQReqCallCancel;
import kr.or.hsnarae.transporthelp.impl.net.json.req.JSON_REQReverseGeocoding;
import kr.or.hsnarae.transporthelp.impl.never_map.MapContainerView;
import kr.or.hsnarae.transporthelp.impl.never_map.NMapPOIflagType;
import kr.or.hsnarae.transporthelp.impl.never_map.NMapViewerResourceProvider;
import kr.or.hsnarae.transporthelp.impl.preference.ConfigPreference;
import kr.or.hsnarae.transporthelp.impl.preference.OrderPreference;

public class TraceMapActivity extends NMapActivity implements View.OnClickListener
{
    private final String THIS_TAG = "MapActivity";

    private final int MSG_HTTP_FAILED = 0;

    private final int MSG_CAR_POS = 10;
    private final int MSG_CAR_CURRENT = 11;
    private final int MSG_CALL_CANCELED = 12;
    private final int MSG_CALLINFO_CALL=13;
    private final int MSG_CALLINFO_SUCCESS=14;
    private final int MSG_CALLINFO_FALIED=15;
    private final int MSG_DIALOG_OK = 16;
    private final int MSG_CALL_CANCEL = 20;
    private final int MSG_CAR_POS_FAILED = 30;

    private OrderPreference mPreference = null;
    private boolean mMapviewstate; // True : Trace, false : Start <=> End position view

    private double mCarPosX = 0, mCarPosY=0;
    private CALLINFO mCallInfo=null;

    private int ACTION_HTTP = MSG_CALLINFO_CALL;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace_map);

        Logview.Logwrite(THIS_TAG, "onCreate");
        mPreference = OrderPreference.getInstance();
        mPreference.setPreference(getBaseContext());

        Intent intent = getIntent();
        mMapviewstate = intent.getBooleanExtra("mapviewstate", true);

        // 기본 설정
        LinearLayout layoutBTNLeft = (LinearLayout)findViewById(R.id.layoutBTNLeft);
        LinearLayout layoutBTNCenter = (LinearLayout)findViewById(R.id.layoutBTNCenter);
        LinearLayout layoutBTNRight = (LinearLayout)findViewById(R.id.layoutBTNRight);

        Button bstBTNLeft = (Button)findViewById(R.id.btnLeft);
        Button bstBTNCenter = (Button)findViewById(R.id.btnCenter);
        Button bstBTNRight = (Button)findViewById(R.id.btnRight);

        TextView textView = (TextView)findViewById(R.id.btnBack);
        textView.setTypeface(CFontAwesomeManager.getTypeface(this, CFontAwesomeManager.FONTAWESOME));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (mMapviewstate) {
            // Back button hiden
            textView.setVisibility(View.GONE);

            // 차량 위치
            layoutBTNLeft.setVisibility(View.VISIBLE);
            bstBTNLeft.setText("기사님께 전화");
            bstBTNLeft.setOnClickListener(this);

            layoutBTNCenter.setVisibility(View.GONE);

            layoutBTNRight.setVisibility(View.VISIBLE);
            bstBTNRight.setText("배차취소");
            bstBTNRight.setOnClickListener(this);
        }
        else {
            // 출, 도착지 위치 표시
            layoutBTNLeft.setVisibility(View.GONE);

            layoutBTNCenter.setVisibility(View.GONE);

            layoutBTNRight.setVisibility(View.GONE);
        }
        ///////////////////////////////////////////////////////////

        initView();

        // map view
        initMapview();
    }

    @Override
    protected void onResume()
    {
        Logview.Logwrite(THIS_TAG, "onResume");
        if (mMapviewstate)
            doCallInfo();

        super.onResume();
    }

    @Override
    protected void onPause()
    {
        Logview.Logwrite(THIS_TAG, "onResume");
        onDestroyMap ();
        stopTimer();
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

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    /**
     * 기본값 설정
     */
    private void initView ()
    {
        Logview.Logwrite(THIS_TAG, "initView");

        ImageView btn = (ImageView)findViewById(R.id.btnMyLocation);
        btn.setOnClickListener(this);

        mCallInfo=null;

        if (!mMapviewstate) {
            btn.setVisibility(View.GONE);
            mCallInfo = new CALLINFO();
            mCallInfo.startDetail = mPreference.getOrderFromDetail();
            mCallInfo.startPosX = mPreference.getOrderFromX();
            mCallInfo.startPosY = mPreference.getOrderFromY();
            mCallInfo.endDetail = mPreference.getOrderToDetail();
            mCallInfo.endPosX = mPreference.getOrderToX();
            mCallInfo.endPosY = mPreference.getOrderToY();

            setCallInfo ();
        }
    }

    /**
     * 콜 정보 표시
     */
    private void setCallInfo()
    {
        if (!mMapviewstate) {
            TextView tv = (TextView) findViewById(R.id.txtDriverName);
            tv.setText("출발 : " + mPreference.getOrderFromDetail());

            tv = (TextView) findViewById(R.id.txtCarNumber);
            tv.setText("도착 : " + mPreference.getOrderToDetail());

        } else {
            TextView tv = (TextView) findViewById(R.id.txtDriverName);
            tv.setText(mCallInfo.drvName);

            tv = (TextView) findViewById(R.id.txtCarNumber);
            tv.setText(mCallInfo.carNumber);

            tv = (TextView) findViewById(R.id.txtFrom);
            tv.setText(mCallInfo.startDetail);

            doShowMarker();
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnMyLocation:    // 현재 나의 위치로 이동
                setCurrentMyLocation();
                break;
            case R.id.btnLeft:          // 기사 통화
                doDriverCalling();
                break;
            case R.id.btnRight:         // 배차 취소
                //doCallCancel();
                doAlertDialogView ("알림", "현재 배차 받은 것을 취소하시겠습니까?");
                break;
        }
    }

    /**
     * 현재 위치로 이동
     */
    private void setCurrentMyLocation ()
    {
        if (mMyLocationOverlay != null && mMapController != null)
        {
            if (mMapviewstate)
            {
                if (mCarPosX >0 && mCarPosY>0)
                {
                    mMapController.animateTo(new NGeoPoint(mCarPosX, mCarPosY));
                }
            } else {
                mMapController.animateTo(mCurrentPoint);
            }
        }
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_CAR_POS:           // 차량 현재 위치 요청
                    doCallCurrentPosition();
                    break;
                case MSG_CAR_CURRENT:       // 차량 현재 위치
                    //doShowMarker();
                    doShowCarMarker ();
                    break;
                case MSG_CALL_CANCELED:     // 콜 취소 되었다.
                    doAlertDialogReturnView("알림", "배차가 취소 되었습니다.");
                    break;
                case MSG_CALLINFO_SUCCESS:  // 콜 인포 수신
                    doCallInfoProcess ();
                    break;
                case MSG_CALLINFO_CALL:     // 콜 인포 요청
                    doCallInfo();
                    break;
                case MSG_HTTP_FAILED:
                    if (ACTION_HTTP == MSG_CALLINFO_CALL)
                    {
                        doCallInfo();
                    }
                    break;
                case MSG_DIALOG_OK:
                    finish();
                    break;
                case MSG_CALL_CANCEL:
                    doCallCancel();
                    break;
                case MSG_CAR_POS_FAILED:
                    doCallInfo();
                    break;

            }
        }
    };

    /**
     * 콜 상태에 따라서 이동
     */
    private void doCallInfoProcess()
    {
        Intent intent;

        if (mPreference.getOrderState().equalsIgnoreCase(GlobalValues.CALL_STATE_CANCEL))
        {
            intent = new Intent(TraceMapActivity.this, OrderWaitActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS|
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("view_state", GlobalValues.ORDER_WAIT_VIEW_CANCLED);
            startActivity(intent);
            finish();
        }
        else if (mPreference.getOrderState().equalsIgnoreCase(GlobalValues.CALL_STATE_ORDER))
        {
            // 재배차 메시지를 보여줘야 한다. - 서버에서 보내주는 것으로 표시
            Toast.makeText(getBaseContext(), "부득이한 사정으로 인하여 다시 배차 요청중입니다.", Toast.LENGTH_LONG).show();

            intent = new Intent(TraceMapActivity.this, OrderWaitActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS|
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("view_state", GlobalValues.ORDER_WAIT_VIEW_ORDER);
            startActivity(intent);
            finish();
        }
        else if (mPreference.getOrderState().equalsIgnoreCase(GlobalValues.CALL_STATE_FAILED))
        {
            intent = new Intent(TraceMapActivity.this, OrderWaitActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS|
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("view_state", GlobalValues.ORDER_WAIT_VIEW_CANCLED);
            startActivity(intent);
            finish();
        }
        else if (mPreference.getOrderState().equalsIgnoreCase(GlobalValues.CALL_STATE_GETON))
        {
            intent = new Intent(TraceMapActivity.this, GetONMessageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS|
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            String intervaltime = mCallInfo.intervaltime;
            int interval = 30;
            if (intervaltime != null && intervaltime.length() > 0) {
                try {
                    interval = Integer.parseInt(intervaltime);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            startTimer(interval);
            setCallInfo();
        }
    }

    /**
     * 기사에게 전화
     */
    private void doDriverCalling()
    {
        String minno = mCallInfo.drvMinno;
        if (minno == null || minno.length() < 7)
        {
            Toast.makeText(getBaseContext(), "기사님 전화번호가 없습니다.", Toast.LENGTH_LONG).show();
            return;
        }

        String url = "tel:" + minno;
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
        startActivity(intent);
    }

    /**
     * 콜 인포 요청
     */
    private void doCallInfo()
    {
        Logview.Logwrite(THIS_TAG, "doCallInfo");

        new Thread(new Runnable() {
            @Override
            public void run() {
                ConfigPreference config = ConfigPreference.getInstance();
                config.setPreference(getBaseContext());
                JSON_REQCallInfo json = new JSON_REQCallInfo(config.getAuthkey(), mPreference.getOrderCallid());
                RetrofitProcessManager.doRetrofitRequest(JSON_REQCallInfo.PAGE_NAME, json.getParams());
            }
        }).start();

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
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

    private NGeoPoint mCurrentPoint;

    private void initMapview()
    {
        // create map view
        mMapView = (NMapView)findViewById(R.id.mapViewSearch);

        //  set a registered Client Id for Open MapViewer Library
        mMapView.setClientId (getResources().getString(R.string.naver_client_key));

        // initialize map view
        mMapView.setClickable(true);
        mMapView.setEnabled(true);                  // 뷰를 화면에 보인다.
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();


        // register listener for map state changes
        mMapView.setOnMapStateChangeListener(onMapViewStateChangeListener);
        mMapView.setOnMapViewTouchEventListener(onMapViewTouchEventListener);
        mMapView.setOnMapViewDelegate(onMapViewTouchDelegate);
        mMapView.setScalingFactor(2.0f);    // 화면 스케일 조정 - 폰트크게 하는 효과

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

        mCurrentPoint = new NGeoPoint();

//        if (!mMapviewstate)
//            startMyLocation ();
    }

    /**
     * 맵 위치 리스너 삭제
     */
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
     * 마커 표시
     */
    public void doShowMarker()
    {
        if (mMapView == null || mCallInfo == null) return;

        mOverlayManager.clearOverlays();
        NMapPOIdata poiData = new NMapPOIdata(2, mMapViewerResourceProvider);
        poiData.beginPOIdata(3);

        // 출발지 표시
        poiData.addPOIitem(mCallInfo.startPosX, mCallInfo.startPosY,
                mCallInfo.startDetail, NMapPOIflagType.FROM, 2);

        // 도착지 표시
        poiData.addPOIitem(mCallInfo.endPosX, mCallInfo.endPosY,
                mCallInfo.endDetail, NMapPOIflagType.TO, 3);

        poiData.endPOIdata();

        if (poiData.count() > 0)
        {
            Logview.Logwrite(THIS_TAG, "doShowMarker distance : " +
                    NGeoPoint.getDistance(new NGeoPoint(mCallInfo.startPosX, mCallInfo.startPosY),
                            new NGeoPoint(mCallInfo.endPosX, mCallInfo.endPosY)));
            NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
            poiDataOverlay.showAllPOIdata(getMapZoomLevel(new NGeoPoint(mCallInfo.startPosX, mCallInfo.startPosY),
                    new NGeoPoint(mCallInfo.endPosX, mCallInfo.endPosY)));
            Logview.Logwrite(THIS_TAG, "doShowMarker zoomlevel : " + mMapController.getZoomLevel());

            // center
            double x = (mCallInfo.startPosX + mCallInfo.endPosX)/2;
            double y = (mCallInfo.startPosY + mCallInfo.endPosY)/2;

            mMapController.setMapCenter(new NGeoPoint(x, y));
        }


    }

    /**
     * 표시 거리에 따라서 zoom level 조정
     * @param from
     * @param to
     * @return
     */
    private int getMapZoomLevel(NGeoPoint from, NGeoPoint to)
    {
        double distance = NGeoPoint.getDistance(from, to)/1000;
        int level = 12;

        if (distance <= 1d) level = 11;
        else if (distance > 1d && distance <= 1.3d) level = 10;
        else if (distance > 1.3d && distance <= 2.4d) level = 9;
        else if (distance > 2.4d && distance <= 4.9d) level = 8;
        else if (distance > 4.9d && distance <= 10d) level = 7;
        else if (distance > 10d && distance <= 19.5d) level = 6;
        else level = 5;

        return level;
    }

    private NMapPOIdataOverlay mCarpoiDataOverlay;
    private void doShowCarMarker()
    {
        int zoom = mMapController.getZoomLevel();
        if (mCarpoiDataOverlay != null) {
            mOverlayManager.clearCalloutOverlayWith(mCarpoiDataOverlay);
            mCarpoiDataOverlay.removeAllPOIdata();
        }

        NMapPOIdata poiData = new NMapPOIdata(1, mMapViewerResourceProvider);
        poiData.beginPOIdata(1);

        // 차량 위치 표시
        NGeoPoint location = null;
        if (mMapviewstate) {
            if (mCarPosX > 0 && mCarPosY > 0) {
                poiData.addPOIitem(mCarPosX, mCarPosY,
                        mPreference.getOrderDriverCar(), NMapPOIflagType.CAR, 1);
                location = new NGeoPoint(mCarPosX, mCarPosY);
            }
        }

        poiData.endPOIdata();

        if (poiData.count() > 0)
        {
            mCarpoiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
            //mCarpoiDataOverlay.showAllPOIdata(0);
//            if (location != null)
//                mMapController.animateTo(location);

        }

        mMapController.setZoomLevel(zoom);
    }

    private void viewCarPosition()
    {

    }

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

    private void stopMyLocation()
    {
        Logview.Logwrite(THIS_TAG, "stopMyLocation");
        if (mMyLocationOverlay != null)
        {
            if (mMapLocationManager != null && mMapLocationManager.isMyLocationEnabled())
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
                if (!mMapviewstate)
                    doShowMarker ();
            } else { // fail
                Logview.Logwrite(THIS_TAG, "onFailedToInitializeWithError: " + nMapError.toString());
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
            //Logview.Logwrite(THIS_TAG, "onTouchDown : ");
            stopMyLocation();
        }

        @Override
        public void onTouchUp(NMapView nMapView, MotionEvent motionEvent) {
            //Logview.Logwrite(THIS_TAG, "onTouchUp : ");
        }

        @Override
        public void onScroll(NMapView nMapView, MotionEvent motionEvent, MotionEvent motionEvent1) {
            //Logview.Logwrite(THIS_TAG, "onScroll : ");
        }

        @Override
        public void onSingleTapUp(NMapView nMapView, MotionEvent motionEvent) {
            //Logview.Logwrite(THIS_TAG, "onSingleTapUp : ");
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
                mMapController.animateTo(myLocation);
                mCurrentPoint.set(myLocation);
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

            Toast.makeText(TraceMapActivity.this, "Your current location is temporarily unavailable.", Toast.LENGTH_LONG).show();
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

    /**
     * 현재 차량 위치
     */
    private void doCallCurrentPosition()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ConfigPreference config = ConfigPreference.getInstance();
                config.setPreference(getBaseContext());
                JSON_REQCarPos json = new JSON_REQCarPos(config.getAuthkey(), mPreference.getOrderCallid(), mPreference.getOrderDriverSeq());
                RetrofitProcessManager.doRetrofitRequest(JSON_REQCarPos.PAGE_NAME, json.getParams());
            }
        }).start();
    }

    /**
     * 배차 취소
     */
    private void doCallCancel ()
    {
        startProgress();

        new Thread(new Runnable() {
            @Override
            public void run() {
                ConfigPreference config = ConfigPreference.getInstance();
                config.setPreference(getBaseContext());
                JSON_REQReqCallCancel json = new JSON_REQReqCallCancel(config.getAuthkey(), mPreference.getOrderCallid());
                RetrofitProcessManager.doRetrofitRequest(JSON_REQReqCallCancel.PAGE_NAME, json.getParams());
            }
        }).start();
    }

    /**
     * 서버로 수신된 PUSH 메시지에 대한 것
     * @param event
     */
    @Subscribe
    public void serviceMessageEvent (ServiceMessageEvent event)
    {
        Logview.Logwrite(THIS_TAG, "serviceMessageEvent : " + event.getCallstate());
        String callState = event.getCallstate();

        if (callState != null)
        {
            stopTimer();
            doCallInfo();
        }
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
                if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.CarPos.name().toLowerCase())) {
                    receiveCarPos(event);
                }
                else if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.ReqCallCancel.name().toLowerCase())) {
                    stopProgress ();
                    receiveCallCancel(event);
                }
                else if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.CallInfo.name().toLowerCase())) {
                    receiveCallInfo(event);
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
     * 차량 위치 응답
     * @param event
     */
    private void receiveCarPos(JSONMessageEvent event)
    {
        Gson gson = new Gson();

        REP_JSONCarPos json = gson.fromJson(event.getMessageBody(), REP_JSONCarPos.class);
        if (json.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success)))
        {
            Logview.Logwrite(THIS_TAG, "receiveReverseGeocoding success");
        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receiveReverseGeocoding false : " + json.getBody().getCause());
            handler.sendEmptyMessage(MSG_CAR_POS_FAILED);
            return;
        }

        String posx = json.getBody().getPosX().trim();
        String posy = json.getBody().getPosY().trim();
        Logview.Logwrite(THIS_TAG, "Pos X : "+ posx);
        Logview.Logwrite(THIS_TAG, "Pos Y : "+ posy);

        try {
            if (posx != null && posx.length() > 0)
                mCarPosX = Double.parseDouble(posx);
            if (posy != null && posy.length() > 0)
                mCarPosY = Double.parseDouble(posy);

            handler.obtainMessage(MSG_CAR_CURRENT).sendToTarget();
        } catch (NumberFormatException ne) {
            ne.printStackTrace();
        }
    }

    /**
     * 배차취소 응답
     * @param event
     */
    private void receiveCallCancel(JSONMessageEvent event)
    {
        Gson gson = new Gson();

        REP_JSONReqCallCancel json = gson.fromJson(event.getMessageBody(), REP_JSONReqCallCancel.class);
        if (json.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success))) {
            Logview.Logwrite(THIS_TAG, "receiveReverseGeocoding success");
        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receiveReverseGeocoding false : " + json.getBody().getCause());
            Toast.makeText(getBaseContext(), "콜 취소 실패 " +
                            ErrorcodeToString.getError(json.getBody().getResult(), json.getBody().getCause()),
                    Toast.LENGTH_LONG).show();
            return;
        }

        String callid = json.getBody().getCallid();
        String canceltype = json.getBody().getCandeltype();
        Logview.Logwrite(THIS_TAG, "Call id : "+ callid);
        Logview.Logwrite(THIS_TAG, "Cancel type : "+ canceltype);

        if (callid.equalsIgnoreCase(mPreference.getOrderCallid()))
        {
            mPreference.setOrderCallid("");
            handler.sendEmptyMessage(MSG_CALL_CANCELED);
        }
    }

    /**
     * CallInfo 수신 처리
     * @param event
     */
    private void receiveCallInfo(JSONMessageEvent event)
    {
        Gson gson = new Gson();

        REP_JSONCallInfo json = gson.fromJson(event.getMessageBody(), REP_JSONCallInfo.class);
        if (json.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success))) {
            Logview.Logwrite(THIS_TAG, "receiveCallInfo success");
        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receiveCallInfo false : " + json.getBody().getCause());
            Toast.makeText(getBaseContext(), "콜 정보요청 실패 " +
                    ErrorcodeToString.getError(json.getBody().getResult(), json.getBody().getCause()),
                    Toast.LENGTH_LONG).show();
            ACTION_HTTP = 0;
            return;
        }

        if (mCallInfo == null)
            mCallInfo = new CALLINFO();

        mCallInfo.startDetail = json.getBody().getStartDetail();
        mCallInfo.startPosX = Double.parseDouble(json.getBody().getStrarPosX());
        mCallInfo.startPosY = Double.parseDouble(json.getBody().getStartPosY());
        mCallInfo.endDetail = json.getBody().getEndDetail();
        mCallInfo.endPosX = Double.parseDouble(json.getBody().getEndPosX());
        mCallInfo.endPosY = Double.parseDouble(json.getBody().getEndPosY());
        mCallInfo.drvseq = json.getBody().getDrvSEQ();
        mCallInfo.drvName = json.getBody().getDrvName();
        mCallInfo.drvMinno = json.getBody().getDrvMinno();
        mCallInfo.carNumber = json.getBody().getCarNumber();
        mCallInfo.intervaltime = json.getBody().getIntervalTime();

        Logview.Logwrite(THIS_TAG, "Call Status : " + json.getBody().getCallState());
        Logview.Logwrite(THIS_TAG, "Call startDetail : " + mCallInfo.startDetail);
        Logview.Logwrite(THIS_TAG, "Call drvseq : " + mCallInfo.drvseq);
        Logview.Logwrite(THIS_TAG, "Call drvName : " + mCallInfo.drvName);
        Logview.Logwrite(THIS_TAG, "intervaltime : " + mCallInfo.intervaltime);

        mPreference.setOrderDriverSeq(mCallInfo.drvseq);
        mPreference.setOrderDriverName(mCallInfo.drvName);
        mPreference.setOrderDriverPhone(mCallInfo.drvMinno);
        mPreference.setOrderDriverCar(mCallInfo.carNumber);
        mPreference.setOrderState(json.getBody().getCallState());

        handler.sendEmptyMessage(MSG_CALLINFO_SUCCESS);

        ACTION_HTTP = 0;
    }

    class CALLINFO {
        public String startDetail="";
        public double startPosX = 0;
        public double startPosY = 0;
        public String endDetail="";
        public double endPosX=0;
        public double endPosY=0;
        public String drvseq="";
        public String drvName="";
        public String drvMinno = "";
        public String carNumber="";
        public String intervaltime="";

        public CALLINFO ()
        {

        }
    }

    /////////////////////////////////////////////////////////////
    // 차량 위치 폴링 타이머
    private Timer mTimer = null;
    private void startTimer(int interval)
    {
        if (mTimer != null)
            stopTimer ();

        mTimer = new Timer();

        mTimer.schedule(new CarPosTimer(), 1000, interval * 1000);
    }

    private void stopTimer()
    {
        if (mTimer != null)
            mTimer.cancel();
        mTimer = null;
    }

    class CarPosTimer extends TimerTask
    {
        @Override
        public void run() {
            Logview.Logwrite(THIS_TAG, "Car pos call");
            handler.sendEmptyMessage(MSG_CAR_POS);
        }
    }

    public void doAlertDialogReturnView (String title, String msg)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(TraceMapActivity.this);
        alert.setTitle(title);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
                handler.sendEmptyMessage(MSG_DIALOG_OK);
            }
        });
        alert.setMessage(msg);
        alert.show();

    }

    public void doAlertDialogView (String title, String msg)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(TraceMapActivity.this);
        alert.setTitle(title)
                .setMessage(msg)
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.sendEmptyMessage(MSG_CALL_CANCEL);
                        dialog.dismiss();     //닫기
                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // Progress 원형
    //
    private ProgressDialog mProgressDialog = null;
    private synchronized void startProgress()
    {
        stopProgress();

        mProgressDialog = ProgressDialog.show(TraceMapActivity.this, "", "잠시 기다려주세요", true);
    }

    private synchronized void stopProgress()
    {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
        mProgressDialog = null;
    }

}


