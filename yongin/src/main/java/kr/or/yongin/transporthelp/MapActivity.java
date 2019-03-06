package kr.or.yongin.transporthelp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
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

import kr.or.yongin.transporthelp.common.FontAwesome.CFontAwesomeManager;
import kr.or.yongin.transporthelp.common.util.AvailableDestination;
import kr.or.yongin.transporthelp.common.util.BusEventProvider;
import kr.or.yongin.transporthelp.impl.net.json.IJSONPageMethod;
import kr.or.yongin.transporthelp.impl.net.json.JSONMessageEvent;
import kr.or.yongin.transporthelp.impl.net.json.RetrofitProcessManager;
import kr.or.yongin.transporthelp.impl.net.json.rep.REP_JSONReverseGeocoding;
import kr.or.yongin.transporthelp.impl.net.json.req.JSON_REQReverseGeocoding;
import kr.or.yongin.transporthelp.impl.never_map.MapContainerView;
import kr.or.yongin.transporthelp.impl.never_map.NMapPOIflagType;
import kr.or.yongin.transporthelp.impl.never_map.NMapViewerResourceProvider;
import kr.or.yongin.transporthelp.impl.preference.OrderPreference;

public class MapActivity extends NMapActivity implements View.OnClickListener
{
    private final String THIS_TAG = "MapActivity";

    private final int MSG_HTTP_FAILED = 0;
    private final int MSG_MAP_ADDRESS_SEARCH = 10;
    private final int MSG_MAP_ADDRESS_CHAGE = 11;
    private final int MSG_MAP_ADDRESS_CHAGE_FAILED = 12;

    public static final String PREVIEW_ORDER_PAGE= "order";
    public static final String PREVIEW_SEARCH_PAGE= "search";

    private TextView mMapSearchNoti;
    private boolean mSearchAddress;
    private OrderPreference mPreference = null;
    private boolean mMapviewstate;
    private ADDR_SEARCH mAddressSearch;
    private BootstrapEditText mEditDetail;
    private AvailableDestination mAvilableDestination = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Logview.Logwrite(THIS_TAG, "onCreate");
        mPreference = OrderPreference.getInstance();
        mPreference.setPreference(getBaseContext());

        TextView btnBack = (TextView)findViewById(R.id.btnBack);
        btnBack.setTypeface(CFontAwesomeManager.getTypeface(this, CFontAwesomeManager.FONTAWESOME));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doResult (RESULT_CANCELED);
            }
        });

        Intent intent = getIntent();
        mMapviewstate = intent.getBooleanExtra("mapviewstate", true);

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
        if (mMapviewstate) {
            bstBTNCenter.setText("출발지로 설정");
        }
        else {
            bstBTNCenter.setText("도착지로 설정");
            mAvilableDestination = new AvailableDestination();
        }
        ///////////////////////////////////////////////////////////

        initView();

        mSearchAddress = false;
        mAddressSearch = new ADDR_SEARCH();
        mEditDetail = (BootstrapEditText)findViewById(R.id.editDetail);

        // map view
        initMapview();
    }

    @Override
    protected void onResume()
    {
        Logview.Logwrite(THIS_TAG, "onResume");

        super.onResume();
    }

    @Override
    protected void onPause()
    {
        Logview.Logwrite(THIS_TAG, "onResume");
        onDestroyMap ();
        BusEventProvider.getInstance().unregister(this);

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
        if (mAvilableDestination != null)
            mAvilableDestination.terminate();
        mAvilableDestination = null;

        super.onDestroy();
    }

    /**
     * 기본값 설정
     */
    private void initView ()
    {
        Logview.Logwrite(THIS_TAG, "initView");

        mMapSearchNoti = (TextView)findViewById(R.id.txtMapsearchNoti);

        ImageView btn = (ImageView)findViewById(R.id.btnMyLocation);
        btn.setOnClickListener(this);
    }

    /**
     * 선택한 위치 설정값을 전달한다.
     * @param result
     */
    private void doResult (int result)
    {
        if (result == RESULT_OK)
        {
            Intent intent = new Intent();
            intent.putExtra("detail", mEditDetail.getText().toString().trim()); //mAddressSearch.poiname);
            intent.putExtra("addr", mAddressSearch.jibunAddress);
            intent.putExtra("xpos", String.valueOf(mAddressSearch.longitude));
            intent.putExtra("ypos", String.valueOf(mAddressSearch.latitude));

            setResult(RESULT_OK, intent);
        }

        finish();
    }

    /**
     * 텍스트 검색 화면으로 이동
     * @param editText
     */
    private void doSearchActivity(EditText editText)
    {
        // 키보드를 숨기고
        doHideKeyboard (editText);

        // 검색 화면으로 이동
    }

    /**
     * 키보드 숨기기
     * @param editText
     */
    private void doHideKeyboard(EditText editText)
    {
        InputMethodManager imm= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromInputMethod(editText.getWindowToken(), 0);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnMyLocation:    // 현재 나의 위치로 이동
                setCurrentMyLocation();
                break;
            case R.id.btnCenter:        // 목적지 설정
                TextView txtSiDO = (TextView)findViewById(R.id.txtSiDO);

                if (checkOrderAddress(txtSiDO.getText().toString().trim()))
                    doResult(RESULT_OK);
                else {
                    if (mPreference.getOrderType().equalsIgnoreCase("S"))
                        doAlertDialogView("알림", (mMapviewstate ? "출발지는 관내(용인시)만 가능합니다." : "도착지는 용인시 관내 및 용인시 인접 지역만 가능합니다."));
                    else
                        doAlertDialogView("알림", (mMapviewstate ? "출발지는 관내(용인시)만 가능합니다." : "도착지는 경기도, 서울시, 인천시만 가능합니다."));
                }
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
            mMapController.animateTo(mCurrentPoint);
        }
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_MAP_ADDRESS_SEARCH:
                    setSeahAddress (true);
                    break;
                case MSG_MAP_ADDRESS_CHAGE:
                    setSeahAddress (false);
                    mSearchAddress = false;
                    setMapCenterAddress ();
                    break;
                case MSG_MAP_ADDRESS_CHAGE_FAILED:
                    setSeahAddress (false);
                    break;

            }
        }
    };

    /**
     * 검색 메시지
     * @param flag
     */
    private void setSeahAddress(boolean flag)
    {
        TextView tv = (TextView)findViewById(R.id.txtMapsearchNoti);
        if (flag) {
            tv.setText(getResources().getString(R.string.position_search_addr));
            tv.setTextColor(Color.BLUE);
        }
        else {
            tv.setText(getResources().getString(R.string.position_set));
            tv.setTextColor(Color.WHITE);
        }
    }
    /**
     * 검색되 주소를 표시한다.
     */
    private void setMapCenterAddress()
    {
        Logview.Logwrite(THIS_TAG, "setMapCenterAddress");

        //TextView txtDong = (TextView)findViewById(R.id.txtDong);
        TextView txtSiDO = (TextView)findViewById(R.id.txtSiDO);

        //txtDong.setText(mAddressSearch.poiname);
        mEditDetail.setText(mAddressSearch.poiname);
        mEditDetail.setSelection(mEditDetail.length());
        txtSiDO.setText(mAddressSearch.jibunAddress);

        // 지역 설정
        checkOrderAddress(mAddressSearch.jibunAddress);
    }

    /**
     * View size 얻기
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        mMapView.setLogoImageOffset(10, mMapView.getHeight() - 100);
        Logview.Logwrite(THIS_TAG, "Mapview size > x: " + mMapView.getWidth() + " / y: " + mMapView.getHeight());
    }

    /**
     * 출, 도착지 위치 검사
     * @param addr
     * @return
     */
    private boolean checkOrderAddress(String addr)
    {
        if (mMapviewstate){
            // 출발지 (관내 : 용인시)
            if (addr != null && addr.indexOf("용인시") < 0) {
                Toast.makeText(getBaseContext(), "출발지는 관내(용인시)만 가능합니다.", Toast.LENGTH_LONG).show();
                return false;
            }
        } else {
            boolean bfind = false;
            // 도착지 경기도, 서울특별시, 인천광역시
            if (mPreference.getOrderType().equalsIgnoreCase("S") && mAvilableDestination != null)
            {
                if (!mAvilableDestination.isAvailableDestination(addr)) {
                    Toast.makeText(getBaseContext(), "도착지는 용인시 관내 및 용인시 인접 지역만 가능합니다.", Toast.LENGTH_LONG).show();
                    return false;
                } else {
                    bfind = true;
                }
            } else {
                if (addr != null && addr.indexOf("경기도") > -1) {
                    bfind = true;
                } else if (addr != null && addr.indexOf("서울특별시") > -1) {
                    bfind = true;
                } else if (addr != null && addr.indexOf("인천광역시") > -1) {
                    bfind = true;
                }

                if (!bfind) {
                    Toast.makeText(getBaseContext(), "도착지는 경기도, 서울시, 인천시만 가능합니다.", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }

        return true;
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
       // mMapController.setMapViewMode(NMapView.VIEW_MODE_VECTOR);   // 일반지도

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

        //doShowMarker();
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

    public void doShowMarker()
    {
        double xp = 0.0, yp = 0.0;
        int poiFlagType=0;
        String detail = "";
        if (mMapviewstate)
        {
            if (mPreference.getOrderFromSelect()) {
                mMyLocationOverlay.setHidden(true);
                xp = mPreference.getOrderFromX();
                yp = mPreference.getOrderFromY();
                detail = mPreference.getOrderFromDetail();

                poiFlagType = NMapPOIflagType.FROM;

            } else {
                startMyLocation ();
            }
        }
        else {
            if (mPreference.getOrderToSelect()) {
                mMyLocationOverlay.setHidden(true);
                xp = mPreference.getOrderToX();
                yp = mPreference.getOrderToY();
                detail = mPreference.getOrderToDetail();

                poiFlagType = NMapPOIflagType.TO;
            } else {
                startMyLocation ();
            }
        }

        if (xp > 0 && yp > 0)
        {
            if (mMapView != null)
            {
                mCurrentPoint.set(xp, yp);

                mOverlayManager.clearOverlays();

                NMapPOIdata poiData = new NMapPOIdata(1, mMapViewerResourceProvider);
                poiData.beginPOIdata(1);
                poiData.addPOIitem(xp, yp, detail, poiFlagType, 0);
                poiData.endPOIdata();

                NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
                poiDataOverlay.showAllPOIdata(0);

                mMapController.setZoomLevel(13);
                mMapController.animateTo(mCurrentPoint);

            }
        }
    }

    //GPS설정이 되어있으면 현재 GPS정보를 가리키고 그렇지않으면 설정창으로 넘어간다.
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
                    //170615 안내메시지 띄워줌
                    new AlertDialog.Builder(this).setTitle("GPS 설정")
                            .setMessage("현재 위치를 확인할 수 없습니다.\n정확한'현재 위치' 설정을 위해\n위치 서비스를 켜주세요.").setPositiveButton("설정", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            //GPS 설정 화면을 띄움
                            Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(goToSettings,1);
                        }
                    }).setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).create().show();

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
                doShowMarker ();
                if (!mSearchAddress)
                    getNaverChangeAddress (nMapView.getMapController().getMapCenter());
                // restore map view state such as map center position and zoom level.
                //restoreInstanceState();
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
            stopMyLocation();
        }

        @Override
        public void onTouchUp(NMapView nMapView, MotionEvent motionEvent) {
            Logview.Logwrite(THIS_TAG, "onTouchUp : ");

            if (!mSearchAddress)
                getNaverChangeAddress (nMapView.getMapController().getMapCenter());
            else
                Logview.Logwrite(THIS_TAG, "Search address running...");
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
                mMapController.animateTo(myLocation);
                mCurrentPoint.set(myLocation);

                if (!mSearchAddress)
                    getNaverChangeAddress (myLocation);
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

            Toast.makeText(MapActivity.this, "Your current location is temporarily unavailable.", Toast.LENGTH_LONG).show();
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
    // 좌표로 주소 얻어 온기
    // GET 방식 retrofit 사용
    // 좌표 주소 변환
    // GET https://openapi.naver.com/
    //           v1/map/reversegeocode?encoding=utf-8&coordType=latlng&query=127.1052133,37.3595316
    //

    private void getNaverChangeAddress(final NGeoPoint location)
    {
        mSearchAddress = true;
        mAddressSearch.setAddreSearchPOI(location.longitude, location.latitude);
        // 검색실시
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSON_REQReverseGeocoding json = new JSON_REQReverseGeocoding(String.valueOf(location.longitude), String.valueOf(location.latitude));
                RetrofitProcessManager.doRetrofitRequest(JSON_REQReverseGeocoding.PAGE_NAME, json.getParams());
            }
        }).start();

        handler.obtainMessage(MSG_MAP_ADDRESS_SEARCH).sendToTarget();
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
            } else {
                Logview.Logwrite(THIS_TAG, "Page name not found");
                handler.sendEmptyMessage(MSG_HTTP_FAILED);
            }
        } else {
            handler.sendEmptyMessage(MSG_HTTP_FAILED);
        }

        mSearchAddress = false;
    }

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

            mAddressSearch.setAddreSearch(item.poiName, item.jibunFullAddr, item.roadFullAddr);

            handler.obtainMessage(MSG_MAP_ADDRESS_CHAGE).sendToTarget();
        }
    }

    /**
     * Dialog message view
     //* @param context
     * @param title
     * @param msg
     */
    public void doAlertDialogView (String title, String msg)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(MapActivity.this);
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

    class ADDR_SEARCH
    {
        public String poiname="";
        public String jibunAddress= "";
        public String roadAddress = "";
        public double longitude=0;
        public double latitude = 0;

        public ADDR_SEARCH ()
        {

        }

        public void setAddreSearch(String poiname, String jibun, String road)
        {
            this.poiname = poiname;
            this.jibunAddress = jibun;
            this.roadAddress = road;
        }

        public void setAddreSearchPOI(double longitude, double latitude)
        {
            this.longitude = longitude;
            this.latitude = latitude;
        }
    }
 }


