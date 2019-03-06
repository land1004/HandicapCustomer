package kr.or.yongin.transporthelp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.ionemax.iomlibrarys.log.Logview;
import com.ionemax.iomlibrarys.util.Util;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import kr.or.yongin.transporthelp.common.FCM.PushKeyTokenEvent;
import kr.or.yongin.transporthelp.common.db.DBControlManager;
import kr.or.yongin.transporthelp.common.db.DBSchema;
import kr.or.yongin.transporthelp.common.util.BusEventProvider;
import kr.or.yongin.transporthelp.common.util.ErrorcodeToString;
import kr.or.yongin.transporthelp.common.util.GlobalValues;
import kr.or.yongin.transporthelp.impl.net.json.IJSONPageMethod;
import kr.or.yongin.transporthelp.impl.net.json.JSONMessageEvent;
import kr.or.yongin.transporthelp.impl.net.json.JSONUpdateMessageEvent;
import kr.or.yongin.transporthelp.impl.net.json.RetrofitProcessManager;
import kr.or.yongin.transporthelp.impl.net.json.rep.REP_JSONAvilableDestination;
import kr.or.yongin.transporthelp.impl.net.json.rep.REP_JSONCallAvailableTime;
import kr.or.yongin.transporthelp.impl.net.json.rep.REP_JSONHandDegreeList;
import kr.or.yongin.transporthelp.impl.net.json.rep.REP_JSONHandTypeList;
import kr.or.yongin.transporthelp.impl.net.json.rep.REP_JSONIntro;
import kr.or.yongin.transporthelp.impl.net.json.rep.REP_JSONUPdateCheck;
import kr.or.yongin.transporthelp.impl.net.json.rep.REP_JSONUseTypeList2;
import kr.or.yongin.transporthelp.impl.net.json.rep.REP_JSONUserInfo;
import kr.or.yongin.transporthelp.impl.net.json.req.JSON_REQAvailableDestination;
import kr.or.yongin.transporthelp.impl.net.json.req.JSON_REQCallAvailableTime;
import kr.or.yongin.transporthelp.impl.net.json.req.JSON_REQHandDegreeList;
import kr.or.yongin.transporthelp.impl.net.json.req.JSON_REQHandTypeList;
import kr.or.yongin.transporthelp.impl.net.json.req.JSON_REQIntro;
import kr.or.yongin.transporthelp.impl.net.json.req.JSON_REQUpdateCheck;
import kr.or.yongin.transporthelp.impl.net.json.req.JSON_REQUseTypeList2;
import kr.or.yongin.transporthelp.impl.net.json.req.JSON_REQUserInfo;
import kr.or.yongin.transporthelp.impl.preference.ConfigPreference;
import kr.or.yongin.transporthelp.impl.preference.OrderAvailableTimePreference;
import kr.or.yongin.transporthelp.impl.preference.OrderPreference;

public class IntroActivity extends Activity
{
    private final String THIS_TAG = "IntroActivity";

    private final int MSG_VIEW_LOG = 1;
    private final int MSG_HTTP_FAILED = 2;

    private final int PAGE_PUSHKEY_CHECK = 1000;
    private final int PAGE_UPDATE_CHECK = 1001;
    private final int PAGE_INTRO_CHECK = 1002;
    private final int PAGE_USERINFO_CHECK = 1003;

    private final int MSG_PUSHKEY_SUCCESS= 10;
    private final int MSG_MINNO_FALIED= 11;
    private final int MSG_UPDATECHECK_SUCCESS = 12;
    private final int MSG_INTRO_SUCCESS = 13;
    private final int MSG_INTRO_FAILED = 14;
    private final int MSG_USERINFO_SUCCESS=15;
    private final int MSG_USERINFO_FAILED = 16;
    private final int MSG_UPDATECHECK_CANCEL = 17;
    private final int MSG_UPDATE_VIEW = 18;

    private final int MSG_HANDI_TYPE = 20;
    private final int MSG_HANDI_DEGREE = 21;
    private final int MSG_USE_TYPE = 22;
    private final int MSG_DESTINATION = 23;
    private final int MSG_SUCCESS_INITDATE = 24;

    private final int MSG_PERMISION_OK = 100;
    private final int MSG_PERMISION_CANCEL = 101;

    private final int REQUEST_CODE_SETTING_ACTIVITY = 9090;

    private ConfigPreference mPreference;

    private int ACTION_STATE = PAGE_PUSHKEY_CHECK;
    private boolean mIntroSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        mPreference = ConfigPreference.getInstance();
        mPreference.setPreference(getBaseContext());

        mIntroSuccess = false;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            doAppInitialize ();
        } else {
            PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    //Toast.makeText(IntroActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    Logview.Logwrite(THIS_TAG, "권한 설정이 됨.");
                    //handler.sendEmptyMessage(MSG_PERMISION_OK);
                    handler.sendEmptyMessageDelayed(MSG_PERMISION_OK, 500);
                }

                @Override
                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                    //Toast.makeText(IntroActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                    Logview.Logwrite(THIS_TAG, "권한 설정이 안됨");
                    handler.sendEmptyMessage(MSG_PERMISION_CANCEL);
                }


            };

            TedPermission.with(this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE)
                            //,Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS)
                    .check();
        }
    }

    @Override
    protected void onPause()
    {
        Logview.Logwrite(THIS_TAG, "onPause");
        BusEventProvider.getInstance().unregister(this);

        super.onPause();
    }

    @Override
    protected void onResume() {
        Logview.Logwrite(THIS_TAG, "onResume");
        BusEventProvider.getInstance().register(this);
        super.onResume();
    }

    @Override
    protected void onStart()
    {
        Logview.Logwrite(THIS_TAG, "onStart");

        super.onStart();
    }

    @Override
    protected void onStop()
    {
        Logview.Logwrite(THIS_TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    private void doAppInitialize()
    {
        ((YINuriApplication)getApplication()).initializeDB();

        /**
         * 1. PushKey Registry
         * 2. 앱버전 검사
         * 3. 인증키(Intro) 검사
         *    3.1 인증키 없는 경우
         *      기본데이터 -> 로그인
         *    3.2 인증기 있는 경우
         *      UserInfo 검사
         *    3.3 실패
         *      기본데이터 -> 로그인인         * 4. UserInfo
         *    4.1 배차 차량 있을 경우 -> TraceMap
         *    4.2 배차 차량 없을 경우 -> 배차 화면
         *    4.3 실패 -> 로그인
         *
         */
        String puchkey = mPreference.getPushkey();
        Logview.Logwrite(THIS_TAG, "Push key : " + puchkey);
        if (puchkey == null || puchkey.length() < 1)
            doPUSHRegistry ();
        else
            doCheckUpdate ();
    }

    private void doFinishedApp ()
    {

    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Intent intent;
            switch (msg.what)
            {
                case MSG_PUSHKEY_SUCCESS:
                    doCheckUpdate();
                    break;
                case MSG_MINNO_FALIED:
                    finish();
                    break;
                case MSG_UPDATECHECK_SUCCESS:
                    doCheckIntro ();
                    break;
                case MSG_INTRO_FAILED:
                    doLoginpage (false);
                    break;
                case MSG_INTRO_SUCCESS:
                    mIntroSuccess = true;
                    ACTION_STATE = MSG_HANDI_TYPE;
                    doHandiTypeList();
                    //doUserInfo();
                    break;
                case MSG_USERINFO_SUCCESS:
                    doUserInfoProcess();
                    break;
                case MSG_USERINFO_FAILED:
                    doLoginpage(true);
                    break;
                case MSG_HANDI_TYPE:
                    ACTION_STATE = MSG_HANDI_TYPE;
                    doHandiTypeList();
                    break;
                case MSG_HANDI_DEGREE:
                    ACTION_STATE = MSG_HANDI_DEGREE;
                    doHandiDegreeList();
                    break;
                case MSG_USE_TYPE:
                    ACTION_STATE = MSG_USE_TYPE;
                    doUseTypeList();
                    break;
                case MSG_DESTINATION:
                    ACTION_STATE = MSG_DESTINATION;
                    doAvailableDestination();
                    break;
                case MSG_SUCCESS_INITDATE:
                    if (mIntroSuccess)
                        doUserInfo();
                    else
                        doLoginpage (true);
                    break;
                case MSG_VIEW_LOG:
                    break;
                case MSG_HTTP_FAILED:
                    if (ACTION_STATE == PAGE_UPDATE_CHECK) {
                        doCheckIntro();
                    }
                    else if (ACTION_STATE == PAGE_INTRO_CHECK) {
                        Toast.makeText(getBaseContext(), (String)msg.obj, Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else if (ACTION_STATE == PAGE_USERINFO_CHECK)
                    {
                        doLoginpage(true);
                    }

                    else if (ACTION_STATE == MSG_HANDI_TYPE)
                    {
                        ACTION_STATE = MSG_HANDI_DEGREE;
                        doHandiDegreeList();
                    } else if (ACTION_STATE == MSG_HANDI_DEGREE) {
                        ACTION_STATE = MSG_USE_TYPE;
                        doUseTypeList();
                    } else if (ACTION_STATE == MSG_USE_TYPE) {
                        ACTION_STATE = MSG_DESTINATION;
                        doAvailableDestination();
                    } else if (ACTION_STATE == MSG_DESTINATION) {
                        //doLoginpage(true);
                        if (mIntroSuccess)
                            doUserInfo();
                        else
                            doLoginpage (true);
                    }

                    break;
                case MSG_PERMISION_OK:
                    doAppInitialize();
                    break;
                case MSG_PERMISION_CANCEL:
                    finish();
                    break;
                case MSG_UPDATE_VIEW:
                    doAlertDialogReturnView ("알림", getResources().getString(R.string.update_noti));//"신규버전이 있습니다.\n지금 업데이트 하시겠습니까?");
                    break;
                case MSG_UPDATECHECK_CANCEL:
                    finish();
                    break;
            }
        }
    };

    /**
     * 업데이트 플레이스토어 이동
     * @param title
     * @param msg
     */
    public void doAlertDialogReturnView (String title, String msg)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(IntroActivity.this);
        alert.setTitle(title);
        alert.setPositiveButton("업데이트", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String packageName = getPackageName();
                String url = "market://details?id=" + packageName;
                Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
                marketLaunch.setData(Uri.parse(url));
                startActivity(marketLaunch);
                dialog.dismiss();     //닫기
            }
        });
        alert.setNegativeButton("취 소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                handler.sendEmptyMessage(MSG_UPDATECHECK_CANCEL);
                dialog.dismiss();     //닫기
            }
        });
        alert.setMessage(msg);
        alert.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE_SETTING_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                Logview.Logwrite(THIS_TAG, "권한 설정이 됨.");
            } else {
                Logview.Logwrite(THIS_TAG, "권한 설정 취소됨.");
            }
        }
    }

    private String mCallState="";

    /**
     * 사용자 상태에 따라서 분기처리
     */
    private void doUserInfoProcess ()
    {
        Intent intent;

        OrderPreference orderPreference = OrderPreference.getInstance();
        orderPreference.setPreference(getBaseContext());
        String callid = orderPreference.getOrderCallid();

        if (callid != null && callid.length() > 0) {
            // 차량 위치 확인
            if (mCallState.toUpperCase().equalsIgnoreCase(GlobalValues.CALL_STATE_ORDER))
            {
                intent = new Intent(IntroActivity.this, OrderWaitActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS|
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("view_state", GlobalValues.ORDER_WAIT_VIEW_ORDER);
                startActivity(intent);
                finish();
            }
            else if (mCallState.toUpperCase().equalsIgnoreCase(GlobalValues.CALL_STATE_CANCEL))
            {
                intent = new Intent(IntroActivity.this, OrderWaitActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS|
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                if (mCallCancelType)
                    intent.putExtra("view_state", GlobalValues.ORDER_WAIT_VIEW_FAILED);
                else
                    intent.putExtra("view_state", GlobalValues.ORDER_WAIT_VIEW_CANCLED);
                startActivity(intent);
                finish();
            }
            else if (mCallState.toUpperCase().equalsIgnoreCase(GlobalValues.CALL_STATE_ALLOC))
            {
                intent = new Intent(IntroActivity.this, TraceMapActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
            else if (mCallState.toUpperCase().equalsIgnoreCase(GlobalValues.CALL_STATE_GETON))
            {
                intent = new Intent(IntroActivity.this, GetONMessageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS|
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Logview.Logwrite(THIS_TAG, "어디로 가야할까나...상태 : " + mCallState);
            }
        } else {
            String id = mPreference.getUserId();
            if (id != null && id.length() > 0) {
                //2018.08.02 CKS
                // 배차 가능시간 설정 요청
                // 예약 및 배차가능 시간설정이 메인에 있어서 시간차가 발생해서 옮김.
                doOrderAvailableTime();

//                // 접수 화면
//                intent = new Intent(IntroActivity.this, ProposeMapActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
//                        Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                finish();
            } else {
                // 아이디가 없으면 로그인 화면으로 이동 시킨다.
                intent = new Intent(IntroActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
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
     * 인트로 로그 출력
     * @param msg
     */
    private void doViewLog(String msg)
    {
        TextView tv = (TextView)findViewById(R.id.txtIntroLog);
        tv.setText(msg);
    }

    /**
     * 앱 버전 정보
     */
    private void setAppversion ()
    {
        Logview.Logwrite(THIS_TAG, "setAppversion");
        String version;
        try {
            PackageInfo i = getBaseContext().getPackageManager().getPackageInfo(getBaseContext().getPackageName(), 0);
            version = i.versionName;
            Logview.Logwrite(THIS_TAG, "Version : " + version);

            mPreference.setVersion(version);
        } catch(PackageManager.NameNotFoundException e) { }
    }

    /**
     * 0. 앱 업데이트 검사
     */
    private void doCheckUpdate ()
    {
        Logview.Logwrite(THIS_TAG, "doCheckUpdate");
        doViewLog ("업데이트 검사");

        // 앱 버전
        setAppversion ();

        // 폰번호
        String minno = mPreference.getPhonenumber();
        if (minno == null || minno.length() < 1)
        {
            YINuriApplication app = (YINuriApplication)getApplication();
            if (app != null)
                minno = app.doPhoneNumber();

            if (minno == null || Util.makeTelNumber(minno) == null)
            {
                AlertDialog.Builder _alert = new AlertDialog.Builder(IntroActivity.this);
                _alert.setTitle("오류");
                _alert.setMessage("전화번호가 존재 하지 않는 핸드폰입니다. 개통후 사용해주시기 바랍니다.");
                _alert.setPositiveButton("확 인", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        handler.obtainMessage(MSG_MINNO_FALIED, "전화번호 오류").sendToTarget();
                        dialog.dismiss();
                    }
                });

                _alert.show();
            } else {
                mPreference.setPhonenumber(minno);

                // 앱 버전검사
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSON_REQUpdateCheck intro = new JSON_REQUpdateCheck(mPreference.getVersion(), mPreference.getPhonenumber());
                        RetrofitProcessManager.doRetrofitUpdateRequest(JSON_REQUpdateCheck.PAGE_NAME, intro.getParams());
                    }
                }).start();

                ACTION_STATE = PAGE_UPDATE_CHECK;
            }
        } else {
            mPreference.setPhonenumber(minno);

            // 앱 버전검사
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSON_REQUpdateCheck intro = new JSON_REQUpdateCheck(mPreference.getVersion(), mPreference.getPhonenumber());
                    RetrofitProcessManager.doRetrofitUpdateRequest(JSON_REQUpdateCheck.PAGE_NAME, intro.getParams());
                }
            }).start();

            ACTION_STATE = PAGE_UPDATE_CHECK;
        }
    }

    /**
     * 1. PUSH servcie 등록 구글 FCM 적용
     */
    private void doPUSHRegistry()
    {
        Logview.Logwrite(THIS_TAG, "doPUSHRegistry");
        doViewLog ("PUSH 등록");

        String token = FirebaseInstanceId.getInstance().getToken();
        Logview.Logwrite(THIS_TAG, "Pushkey token : " + token);
        if (token != null) {
            mPreference.setPushkey(token);
            doCheckUpdate();
        } else {
            ACTION_STATE = PAGE_PUSHKEY_CHECK;
            //doCheckUpdate ();
        }
    }

    /**
     * 2. 인증키 검사
     *   2.1 인증키가 없을 경우 인증 page 이동
     *   2.2 인증키 검사에서 실패일 경우 Login page 이동
     *
     */
    private void doCheckIntro()
    {
        Logview.Logwrite(THIS_TAG, "doCheckAuthkey");
        doViewLog ("인증키 검사");

        String authkey = mPreference.getAuthkey();
        Logview.Logwrite(THIS_TAG, "Auth key : " + authkey);

        if (authkey == null || authkey.length() < 1) {
            handler.sendEmptyMessage(MSG_INTRO_FAILED);
            return;
        }

        // 인증키 검사
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSON_REQIntro intro = new JSON_REQIntro(mPreference.getAuthkey(), mPreference.getPushkey());
                RetrofitProcessManager.doRetrofitRequest(JSON_REQIntro.PAGE_NAME, intro.getParams());
            }
        }).start();

        ACTION_STATE = PAGE_INTRO_CHECK;
    }

    /**
     * 3. 로그인 페이지로 이동
     *   3.1 인증키를 받은 뒤로는 항상 자동 로구인 인가.
     *   3.2 배차받은 콜이 있는 경우 현재 차량위치 추적으로 이동
     */
    private void doLoginpage(boolean flag)
    {
        Logview.Logwrite(THIS_TAG, "doStartLoginpage");
        if (!flag) {
            handler.sendEmptyMessage(MSG_HANDI_TYPE);
            return;
        }

        Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS|
                        Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * 사용자 정복를 가져 온다.
     */
    private void doUserInfo ()
    {
        doViewLog ("사용자 정보");
        Logview.Logwrite(THIS_TAG, "doUserInfo");
        mCallState = "";

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSON_REQUserInfo reqvalue = new JSON_REQUserInfo(mPreference.getAuthkey(), mPreference.getPushkey());
                RetrofitProcessManager.doRetrofitRequest(JSON_REQUserInfo.PAGE_NAME, reqvalue.getParams());
            }
        }).start();

        ACTION_STATE = PAGE_USERINFO_CHECK;
    }

    /**
     * 기본 데이터 초기화
     */
    private void doHandiTypeList()
    {
        doViewLog ("기초데이터 수신");
        Logview.Logwrite(THIS_TAG, "doHandiTypeList");
        // 장애유형 목록
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSON_REQHandTypeList reqvalue = new JSON_REQHandTypeList();
                RetrofitProcessManager.doRetrofitRequest(JSON_REQHandTypeList.PAGE_NAME, reqvalue.getParams());
            }
        }).start();
    }

    private void doHandiDegreeList()
    {
        Logview.Logwrite(THIS_TAG, "doHandiDegreeList");
        // 장애등급 목록
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSON_REQHandDegreeList reqvalue = new JSON_REQHandDegreeList();
                RetrofitProcessManager.doRetrofitRequest(JSON_REQHandDegreeList.PAGE_NAME, reqvalue.getParams());
            }
        }).start();
    }

    private void doUseTypeList()
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
                if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.Intro.name().toLowerCase())) {
                    receiveIntro(event);
                }
                else if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.HandiTypeList.name().toLowerCase())) {
                    receiveHandiTypeList(event);
                }
                else if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.HandiDegreeList.name().toLowerCase())) {
                    receiveHandiDegreeList(event);
                }
                else if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.UseTypeAvailTimeList.name().toLowerCase())) {
                    receiveUseTypeList(event);
                }
                else if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.UserInfo.name().toLowerCase())) {
                    receiveUserInfo(event);
                }
                else if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.AvailableDestination.name().toLowerCase())) {
                    receiveAvailableDestination(event);
                }
                else if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.CallAvailableTime.name().toLowerCase())) {
                    receiveCallAvailableTime(event);
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
    public void pushKeyTokenEvent(PushKeyTokenEvent event)
    {
        Logview.Logwrite(THIS_TAG, "pushKeyTokenEvent : " + event.getPushkey());

        mPreference.setPushkey(event.getPushkey());

        handler.sendEmptyMessage(MSG_PUSHKEY_SUCCESS);
    }

    /**
     * UPdate check message receiver
     * @param event
     */
    @Subscribe
    public void jsonUpdateMessageEvent(JSONUpdateMessageEvent event)
    {
        Logview.Logwrite(THIS_TAG, "jsonUpdateMessageEvent 수신 상태 : " + event.getMessageStatus());
        if (event.getMessageStatus()) {
            String pageName = event.getPageName();
            Logview.Logwrite(THIS_TAG, "jsonUpdateMessageEvent page : " + pageName);

            if (pageName != null) {
                if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.Check.name().toLowerCase())) {
                    receiveUpdateCheck(event);
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
     * 인트로 Auth Key 검사
     * 단말기에 가지고 있는 인증키의 유효성 체크 값, Y:사용가능, N:사용불가능
     * @param event
     */
    private void receiveIntro (JSONMessageEvent event) {
        Gson gson = new Gson();

        REP_JSONIntro json = gson.fromJson(event.getMessageBody(), REP_JSONIntro.class);

        if (json.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success))) {
            Logview.Logwrite(THIS_TAG, "receiveIntro success");
        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receiveIntro false : " + json.getBody().getCause());
            handler.obtainMessage(MSG_INTRO_FAILED,
                    ErrorcodeToString.getError(json.getBody().getResult(), json.getBody().getCause())).sendToTarget();
            return;
        }

        String authKeyYN = json.getBody().getAuthKeyYN();
        String updateURL = json.getBody().getUpdateUrl();

        Logview.Logwrite(THIS_TAG, "authKey : " + authKeyYN);
        Logview.Logwrite(THIS_TAG, "updateURL : " + updateURL);

        if (authKeyYN.trim().equalsIgnoreCase("N")) {
            handler.sendEmptyMessage(MSG_INTRO_FAILED);
        } else if (authKeyYN.trim().equalsIgnoreCase("Y")) {
            handler.sendEmptyMessage(MSG_INTRO_SUCCESS);
        } else {
            Logview.Logwrite(THIS_TAG, "Unknow authkey YN");
            handler.sendEmptyMessage(MSG_INTRO_FAILED);
        }
    }

    /**
     * 업데이트 체크
     * @param event
     */
    private void receiveUpdateCheck (JSONUpdateMessageEvent event)
    {
        Gson gson = new Gson();

        REP_JSONUPdateCheck json = gson.fromJson(event.getMessageBody(), REP_JSONUPdateCheck.class);

        if (json.getBody().getResult().equalsIgnoreCase("1"))
        {
            Logview.Logwrite(THIS_TAG, "receiveUpdateCheck success");
        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receiveUpdateCheck false : " + json.getBody().getCause());
            handler.sendEmptyMessage(MSG_UPDATECHECK_SUCCESS);
            return;
        }

        if (json.getBody().getAppUpdateYN())
        {
            Logview.Logwrite(THIS_TAG, getResources().getString(R.string.update_noti)); //"신규버전이 있습니다.\n지금 업데이트 하시겠습니까");
            mPreference.setNewVersion(json.getBody().getAppVersion());
            handler.sendEmptyMessage(MSG_UPDATE_VIEW);
        } else {
            handler.sendEmptyMessage(MSG_UPDATECHECK_SUCCESS);
            mPreference.setNewVersion(mPreference.getVersion());
        }
    }


    /**
     * 장애유형 리스트
     * @param event
     */
    private void receiveHandiTypeList (JSONMessageEvent event)
    {
        Gson gson = new Gson();

        final REP_JSONHandTypeList json = gson.fromJson(event.getMessageBody(), REP_JSONHandTypeList.class);

        if (json.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success)))
        {
            // DB에 저장
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<REP_JSONHandTypeList.Body.HandiType> handiTypes = json.getBody().getHandiTypes();
                    if (handiTypes != null && handiTypes.size() > 0)
                    {
                        // 기존에 있는 모든 자료 삭제
                        DBControlManager.dbTableClear(DBSchema.TABLE_NAME_HANDITYPE);

                        // 새포운 자료 입력
                        try {
                            String sql="";
                            for (REP_JSONHandTypeList.Body.HandiType item : handiTypes) {
                                sql = String.format("INSERT INTO %s (name, code) VALUES ('%s', '%s');" ,DBSchema.TABLE_NAME_HANDITYPE
                                        ,item.getHandiType(),item.getHandiTypeCode());
                                DBControlManager.dbTransaction(sql);
                            }
                        } finally {
                        }
                    } else {
                        Logview.Logwrite(THIS_TAG, "Handi type data null");
                    }
                }
            }).start();
        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receiveHandiTypeList false : " + json.getBody().getCause());
        }
        Logview.Logwrite(THIS_TAG, "receiveHandiTypeList success");


        // 장애등급 호출
        handler.sendEmptyMessage(MSG_HANDI_DEGREE);
    }

    /**
     * 장애등급리스트
     * @param event
     */
    private void receiveHandiDegreeList (JSONMessageEvent event)
    {
        Gson gson = new Gson();

        final REP_JSONHandDegreeList json = gson.fromJson(event.getMessageBody(), REP_JSONHandDegreeList.class);

        if (json.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success)))
        {
            // DB에 저장
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<REP_JSONHandDegreeList.Body.HandiDegree> list = json.getBody().getHandiDegree();
                    if (list != null && list.size() > 0)
                    {
                        // 기존에 있는 모든 자료 삭제
                        DBControlManager.dbTableClear(DBSchema.TABLE_NAME_HANDIDEGREE);

                        // 새포운 자료 입력
                        try {
                            String sql="";
                            for (REP_JSONHandDegreeList.Body.HandiDegree item : list)
                            {
                                sql = String.format("INSERT INTO %s (name, code) VALUES ('%s', '%s');" ,DBSchema.TABLE_NAME_HANDIDEGREE
                                        ,item.getHandiDegree(),item.getHandiDegreeCode());
                                DBControlManager.dbTransaction(sql);
                            }
                        } finally {
                        }

                        // 입력된 자료 확인
                    } else {
                        Logview.Logwrite(THIS_TAG, "Handi degree data null");
                    }
                }
            }).start();

        } else  {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receiveHandiDegreeList false : " + json.getBody().getCause());
        }
        Logview.Logwrite(THIS_TAG, "receiveHandiDegreeList success");

        // 이용목적 호출
        handler.sendEmptyMessage(MSG_USE_TYPE);
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
                        try{
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
                        } finally {
                        }

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

        handler.sendEmptyMessage(MSG_DESTINATION);
    }

    private boolean mCallCancelType = false; // 'S' : True
    /**
     * 사용자 정보요청
     * @param event
     */
    private void receiveUserInfo (JSONMessageEvent event)
    {
        Gson gson = new Gson();

        REP_JSONUserInfo json = gson.fromJson(event.getMessageBody(), REP_JSONUserInfo.class);

        if (json.getBody().getResult().equalsIgnoreCase("S0000"))
        {
            Logview.Logwrite(THIS_TAG, "receiveUserInfo success");
        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receiveUserInfo false : " + json.getBody().getCause());
            handler.sendEmptyMessage(MSG_UPDATECHECK_SUCCESS);
            return;
        }

        String username = json.getBody().getUsername();
        String minno = json.getBody().getUserphone();
        String email = json.getBody().getUseremail();
        String callid = json.getBody().getCallid();
        String authkey = json.getBody().getAuthkey();
        String callstate = json.getBody().getCallState();

        Logview.Logwrite(THIS_TAG, "username : " + username);
        Logview.Logwrite(THIS_TAG, "minno : " + minno);
        Logview.Logwrite(THIS_TAG, "email : " + email);
        Logview.Logwrite(THIS_TAG, "callid : " + callid);
        Logview.Logwrite(THIS_TAG, "callstate : " + callstate);
        Logview.Logwrite(THIS_TAG, "Cancel type : " + json.getBody().getCall_canceltype());
        Logview.Logwrite(THIS_TAG, "Intro receiveUserInfo authkey : " + authkey);

        mPreference.setUserName((username == null ? "" : username));
        mPreference.setPhonenumber((minno == null ? "" : minno));
        mPreference.setUserEmail((email == null ? "" : email));
        mPreference.setAuthkey((authkey == null ? "" : authkey));

        mCallState=callstate;
        OrderPreference orderPreference = OrderPreference.getInstance();
        orderPreference.setPreference(getBaseContext());

        orderPreference.setOrderCallid((callid == null ? "" : callid));
        orderPreference.setOrderState((callstate == null ? "" : callstate));

        mCallCancelType = json.getBody().getCall_canceltype();

        handler.sendEmptyMessage(MSG_USERINFO_SUCCESS);
    }

    /**
     * APP 권한 검사
     */
    private boolean doCheckPermision()
    {
        boolean bret = true;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            handler.sendEmptyMessage(MSG_PERMISION_OK);
            return true;
        }

//        // 저장 권한
//        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
//                checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
//        {
//            Logview.Logwrite(THIS_TAG, "저장 권한 필요");
//            bret = setPermission (android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        }
//
//        // 위치 권한
//        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
//                checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//        {
//            Logview.Logwrite(THIS_TAG, "위치 권한 필요");
//            bret = setPermission (android.Manifest.permission.ACCESS_FINE_LOCATION);
//        }
//
//        // 전화궎한
//        if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
//                checkSelfPermission(android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
//        {
//            Logview.Logwrite(THIS_TAG, "전화 권한 필요");
//            bret = setPermission (android.Manifest.permission.CALL_PHONE);
//        }
//
//        // SMS 권한
//        if (checkSelfPermission(android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED )
//        {
//            Logview.Logwrite(THIS_TAG, "SMS 권한 필요");
//            bret = setPermission (android.Manifest.permission.READ_SMS);
//        }

        return bret;
    }


    private boolean setPermission (String permission)
    {
        boolean bret = false;
//        PermissionRequest.Builder requester = new PermissionRequest.Builder(this);
//        int result = requester
//                .create()
//                .request( permission, 20000,
//                        new PermissionRequest.OnClickDenyButtonListener()
//                        {
//                            @Override
//                            public void onClick(Activity activity) { }
//                        });
//
//        // 사용자가 권한을 수락한 경우
//        if (result == PermissionRequest.ALREADY_GRANTED || result == PermissionRequest.REQUEST_PERMISSION)
//        {
//
//            bret = true;
//        }

        Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + getBaseContext().getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        startActivityForResult(i, REQUEST_CODE_SETTING_ACTIVITY);

        Toast.makeText(getBaseContext(), "권한 설정이 필요합니다.\n설정후 다시 시작하십시요.", Toast.LENGTH_LONG).show();
        Logview.Logwrite(THIS_TAG, "권한 설정이 필요합니다.");

        return bret;
    }

    /**
     * 즉시배차 요청이 가능한 목적지 지역 리스트 요청
     */
    private void doAvailableDestination ()
    {
        Logview.Logwrite(THIS_TAG, "doAvailableDestination");
        // 이용목적 목록
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSON_REQAvailableDestination reqvalue = new JSON_REQAvailableDestination();
                RetrofitProcessManager.doRetrofitRequest(JSON_REQAvailableDestination.PAGE_NAME, reqvalue.getParams());
            }
        }).start();
    }

    private void receiveAvailableDestination (JSONMessageEvent event)
    {
        Gson gson = new Gson();

        final REP_JSONAvilableDestination json = gson.fromJson(event.getMessageBody(), REP_JSONAvilableDestination.class);

        if (json.getBody().getResult().equalsIgnoreCase("S0000"))
        {
            Logview.Logwrite(THIS_TAG, "receiveAvailableDestination success");

            // DB에 저장
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String count = json.getBody().getCount();
                    Logview.Logwrite(THIS_TAG, "count : " + count);
                    List<REP_JSONAvilableDestination.Body.Destination> list = null;
                    if (count != null && count.length() > 0)
                    {
                        int ncount = Integer.parseInt(count);
                        // 기존 데이터를 삭제 한다.
                        DBControlManager.dbTableClear(DBSchema.TABLE_NAME_AVAILABLE_DESTINATION);

                        // 신규 데이터
                        list = json.getBody().getDestinations();
                    }

                    if (list != null && list.size() > 0)
                    {
                        // 새포운 자료 입력
                        try{
                            String sql = "";
                            String si="", gu="", dong="";
                            for (REP_JSONAvilableDestination.Body.Destination item : list)
                            {
                                Logview.Logwrite(THIS_TAG, "si : " + item.getSi() + " gu : " + item.getGu() + " dong : " + item.getDong());

                                sql = String.format("INSERT INTO %s (seq, si, gu, dong) " +
                                                "VALUES ('%s', '%s', '%s','%s');"
                                        ,DBSchema.TABLE_NAME_AVAILABLE_DESTINATION
                                        ,item.getSeq(), item.getSi(), item.getGu(), item.getDong());

                                DBControlManager.dbTransaction(sql);
                            }
                        } finally {
                        }

                    } else {
                        Logview.Logwrite(THIS_TAG, "use type data null");
                    }
                }
            }).start();

        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receiveAvailableDestination false : " + json.getBody().getCause());
        }


        handler.sendEmptyMessage(MSG_SUCCESS_INITDATE);
    }

    /**
     * 배차 가능시간 수신
     * @param event
     */
    private void receiveCallAvailableTime (JSONMessageEvent event)
    {
        OrderAvailableTimePreference preference = OrderAvailableTimePreference.getInstance();
        preference.setPreference(getBaseContext());

        Gson gson = new Gson();

        final REP_JSONCallAvailableTime json = gson.fromJson(event.getMessageBody(), REP_JSONCallAvailableTime.class);

        if (json.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success)))
        {
            Logview.Logwrite(THIS_TAG, "receiveCallAvailableTime success");
            preference.setOrderabailableStartdate(json.getBody().getStartDate());
            preference.setOrderabailableStarttime(json.getBody().getStartTime());
            preference.setOrderabailableEnddate(json.getBody().getEndDate());
            preference.setOrderabailableEndtime(json.getBody().getEndTime());

            preference.setOrderabailableReservecdStartdate(json.getBody().getReservedStartDate());
            preference.setOrderabailableReservecdStarttime(json.getBody().getReservedStartTime());
            preference.setOrderabailableReservecdEnddate(json.getBody().getReservedEndDate());
            preference.setOrderabailableReservecdEndtime(json.getBody().getReservedEndTime());
        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receiveCallAvailableTime false : " + json.getBody().getCause());

            preference.setOrderabailableStartdate("");
            preference.setOrderabailableStarttime("");
            preference.setOrderabailableEnddate("");
            preference.setOrderabailableEndtime("");

            preference.setOrderabailableReservecdStartdate("");
            preference.setOrderabailableReservecdStarttime("");
            preference.setOrderabailableReservecdEnddate("");
            preference.setOrderabailableReservecdEndtime("");
        }

        // 접수 화면
        Intent intent;
        intent = new Intent(IntroActivity.this, ProposeMapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
