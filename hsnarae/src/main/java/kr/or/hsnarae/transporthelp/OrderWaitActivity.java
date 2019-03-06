package kr.or.hsnarae.transporthelp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.UiThread;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.gson.Gson;
import com.ionemax.iomlibrarys.log.Logview;
import com.squareup.otto.Subscribe;

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
import kr.or.hsnarae.transporthelp.impl.net.json.rep.REP_JSONReqCallCancel;
import kr.or.hsnarae.transporthelp.impl.net.json.req.JSON_REQCallInfo;
import kr.or.hsnarae.transporthelp.impl.net.json.req.JSON_REQReqCallCancel;
import kr.or.hsnarae.transporthelp.impl.preference.ConfigPreference;
import kr.or.hsnarae.transporthelp.impl.preference.OrderPreference;

public class OrderWaitActivity extends Activity implements View.OnClickListener
{
    private final String THIS_TAG = "OrderWaitActivity";

    private final int MSG_HTTP_FAILED = 0;
    private final int MSG_CALL_CANCEL_ALERT = 10;
    private final int MSG_CALL_CANCEL = 11;
    private final int MSG_CALL_INFO = 20;
    private final int MSG_CALLINFO_SUCCESS = 21;

    private OrderPreference mPreference = null;
    private CALLINFO mCallInfo=null;
    private LinearLayout mCallWait, mCallState;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_wait);

        mPreference = OrderPreference.getInstance();
        mPreference.setPreference(getBaseContext());

        // 기본 설정
        TextView btnBack = (TextView)findViewById(R.id.btnBack);
        btnBack.setTypeface(CFontAwesomeManager.getTypeface(this, CFontAwesomeManager.FONTAWESOME));
        btnBack.setVisibility(View.GONE);

        Intent intent = getIntent();
        String viewstate = intent.getStringExtra("view_state");

        mCallWait = (LinearLayout)findViewById(R.id.layoutWait);
        mCallState = (LinearLayout)findViewById(R.id.layoutState);

        if (viewstate != null)
        {
            if (viewstate.equalsIgnoreCase(GlobalValues.ORDER_WAIT_VIEW_ORDER))
                callAllocationView ();
            else if (viewstate.equalsIgnoreCase(GlobalValues.ORDER_WAIT_VIEW_FAILED))
                callAllocFailedView();
            else if (viewstate.equalsIgnoreCase(GlobalValues.ORDER_WAIT_VIEW_CANCLED)) {
                if (mCallInfo.canceltype != null && mCallInfo.canceltype.toUpperCase().trim().equalsIgnoreCase("S"))
                    callAllocFailedView();
                else
                    callCanceledView();
            }
            else
                callAllocationView ();
        }
    }

    /**
     * 차량 배차중 화면
     */
    private void callAllocationView()
    {
        TextView title = (TextView)findViewById(R.id.txtTitle);
        title.setText("차량호출");

        // Center log
        ImageView iv = (ImageView)findViewById(R.id.imgCallState);
        iv.setBackgroundResource(R.drawable.logo);

        mCallWait.setVisibility(View.VISIBLE);
        mCallState.setVisibility(View.GONE);

        TextView tv = (TextView)findViewById(R.id.txtCallWaitLog);
        tv.setText(getResources().getString(R.string.call_wait));

        // 호출 취소 버튼
        LinearLayout layoutCancel = (LinearLayout)findViewById(R.id.layoutCallCancel);
        layoutCancel.setVisibility(View.VISIBLE);
        //layoutCancel.setVisibility(View.GONE);

        tv = (TextView)findViewById(R.id.txtOrderCancel);
        tv.setOnClickListener(this);

        // Call info layout
        FrameLayout layoutCallInfo = (FrameLayout)findViewById(R.id.layoutCallInfo);
        layoutCallInfo.setVisibility(View.VISIBLE);

        // Center button
        BootstrapButton btn = (BootstrapButton)findViewById(R.id.btnCenter1);
        btn.setVisibility(View.GONE);
        btn = (BootstrapButton)findViewById(R.id.btnCenter2);
        btn.setVisibility(View.GONE);

        startTimer();
    }

    /**
     * 콜 취소 후 화면 (고객 취소 후 화면)
     */
    private void callCanceledView()
    {
        TextView title = (TextView)findViewById(R.id.txtTitle);
        title.setText("배차취소");

        // Center log
        ImageView iv = (ImageView)findViewById(R.id.imgCallState);
        iv.setBackgroundResource(R.drawable.failed);

        mCallWait.setVisibility(View.GONE);
        mCallState.setVisibility(View.VISIBLE);

        TextView tv = (TextView)findViewById(R.id.txtCallWaitLog);
        tv.setText(getResources().getString(R.string.call_canceled));

        // 호출 취소 버튼
        LinearLayout layoutCancel = (LinearLayout)findViewById(R.id.layoutCallCancel);
        layoutCancel.setVisibility(View.GONE);

        // Call info layout
        FrameLayout layoutCallInfo = (FrameLayout)findViewById(R.id.layoutCallInfo);
        layoutCallInfo.setVisibility(View.GONE);

        // Center button
        BootstrapButton btn = (BootstrapButton)findViewById(R.id.btnCenter1);
        btn.setVisibility(View.VISIBLE);
        btn.setText("다시 접수하기");
        btn.setOnClickListener(this);

        btn = (BootstrapButton)findViewById(R.id.btnCenter2);
        btn.setVisibility(View.VISIBLE);
        btn.setText("종료하기");
        btn.setOnClickListener(this);
    }

    /**
     * 배차 실패 화면
     */
    private void callAllocFailedView ()
    {
        TextView title = (TextView)findViewById(R.id.txtTitle);
        title.setText("배차실패");

        // Center log
        ImageView iv = (ImageView)findViewById(R.id.imgCallState);
        iv.setBackgroundResource(R.drawable.failed);

        mCallWait.setVisibility(View.GONE);
        mCallState.setVisibility(View.VISIBLE);

        TextView tv = (TextView)findViewById(R.id.txtCallWaitLog);
        tv.setText(getResources().getString(R.string.call_alloc_failed));

        // 호출 취소 버튼
        LinearLayout layoutCancel = (LinearLayout)findViewById(R.id.layoutCallCancel);
        layoutCancel.setVisibility(View.GONE);

        // Call info layout
        FrameLayout layoutCallInfo = (FrameLayout)findViewById(R.id.layoutCallInfo);
        layoutCallInfo.setVisibility(View.GONE);

        // Center button
        BootstrapButton btn = (BootstrapButton)findViewById(R.id.btnCenter1);
        btn.setVisibility(View.VISIBLE);
        btn.setText("확인");
        btn.setOnClickListener(this);

        btn = (BootstrapButton)findViewById(R.id.btnCenter2);
        btn.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        stopTimer();
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
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.txtOrderCancel:
                doAlertDialogView ("호출취소", getResources().getString(R.string.call_cancel));
                break;
            case R.id.btnCenter1:   // 확인 / 다시접수하기
                Intent intent = new Intent(OrderWaitActivity.this, ProposeMapActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            case R.id.btnCenter2:   // 종료하기
                finish();
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
                case MSG_CALL_CANCEL:       // 콜 취소
                    doCallCancel();
                    break;
                case MSG_CALL_INFO:         // 콜 인포 호출
                    doCallInfo ();
                    break;
                case MSG_CALLINFO_SUCCESS:  // 콜 정보 수신
                    doCallInfoProcess ();
                    break;
            }
        }
    };

    /**
     * 콜 정보 수신
     */
    private void doCallInfoProcess()
    {
        if (mCallInfo.callState == null)
        {
            Logview.Logwrite(THIS_TAG, "doCallInfoProcess call state NULL..");
            return;
        }

        Intent intent;

        String callstate = mCallInfo.callState;
        if (callstate.toUpperCase().equalsIgnoreCase(GlobalValues.CALL_STATE_ALLOC))
        {
            // 배차
            stopTimer();
            intent = new Intent(OrderWaitActivity.this, TraceMapActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

            // 재요청 처리
            mPreference.setOrderReorderCount(0);
            mPreference.setOrderReorderTime(0);
        }
        else if (callstate.toUpperCase().equalsIgnoreCase(GlobalValues.CALL_STATE_FAILED))
        {
            stopTimer();
            callAllocFailedView ();
        }
        else if (callstate.toUpperCase().equalsIgnoreCase(GlobalValues.CALL_STATE_CANCEL))
        {
            stopTimer();
            //callCanceledView();
            if (mCallInfo.canceltype != null && mCallInfo.canceltype.toUpperCase().trim().equalsIgnoreCase("S"))
                callAllocFailedView();
            else
                callCanceledView();

        }
        else if (callstate.toUpperCase().equalsIgnoreCase(GlobalValues.CALL_STATE_ORDER) == false)
        {
            Logview.Logwrite(THIS_TAG, "doCallInfoProcess 알수없는 콜 상태 : " + mCallInfo.callState);
            return;
        }

        TextView tv = (TextView)findViewById(R.id.txtOrderFrom);
        tv.setText(mCallInfo.startDetail);

        tv = (TextView)findViewById(R.id.txtOrderTo);
        tv.setText(mCallInfo.endDetail);
    }

    /**
     * 호출 취소
     */
    private void doCallCancel()
    {
        Logview.Logwrite(THIS_TAG, "콜 취소 ");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String callid = mPreference.getOrderCallid();
                if (callid != null && callid.length() > 0)
                {
                    ConfigPreference config = ConfigPreference.getInstance();
                    config.setPreference(getBaseContext());

                    JSON_REQReqCallCancel json = new JSON_REQReqCallCancel(config.getAuthkey(), mPreference.getOrderCallid());
                    RetrofitProcessManager.doRetrofitRequest(JSON_REQReqCallCancel.PAGE_NAME, json.getParams());
                }
            }
        }).start();

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
                if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.CallInfo.name().toLowerCase())) {
                    receiveCallInfo(event);
                }
                else if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.ReqCallCancel.name().toLowerCase())) {
                    receiveCallCancel(event);
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
            return;
        }

        if (mCallInfo == null)
            mCallInfo = new CALLINFO();
        mCallInfo.clear();

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
        mCallInfo.callState = json.getBody().getCallState();
        mCallInfo.canceltype = json.getBody().getCall_canceltype();

        Logview.Logwrite(THIS_TAG, "Call calltatus : " + mCallInfo.callState);
        Logview.Logwrite(THIS_TAG, "Call startDetail : " + mCallInfo.startDetail);
        Logview.Logwrite(THIS_TAG, "Call endDetail : " + mCallInfo.endDetail);
        Logview.Logwrite(THIS_TAG, "Call drvseq : " + mCallInfo.drvseq);
        Logview.Logwrite(THIS_TAG, "Call drvName : " + mCallInfo.drvName);
        Logview.Logwrite(THIS_TAG, "Call canceltype : " + mCallInfo.canceltype);

        handler.sendEmptyMessage(MSG_CALLINFO_SUCCESS);
    }

    /**
     * 콜 취소 수신
     * @param event
     */
    private void receiveCallCancel(JSONMessageEvent event)
    {
        Gson gson = new Gson();

        REP_JSONReqCallCancel json = gson.fromJson(event.getMessageBody(), REP_JSONReqCallCancel.class);
        if (json.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success))) {
            Logview.Logwrite(THIS_TAG, "receiveCallCancel success");
        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receiveCallCancel false : " + json.getBody().getCause());
            Toast.makeText(getBaseContext(), "콜 취소 실패 " +
                            ErrorcodeToString.getError(json.getBody().getResult(), json.getBody().getCause()),
                    Toast.LENGTH_LONG).show();
            return;
        }

        Logview.Logwrite(THIS_TAG, "Call callid : " + json.getBody().getCallid());
        //D 기사 취소, P 승객취소, M 관리자
        Logview.Logwrite(THIS_TAG, "Call startDetail : " + json.getBody().getCandeltype());

        stopTimer();
        callCanceledView();
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
        public String callState="";
        public String canceltype="";

        public CALLINFO ()
        {

        }

        public void clear()
        {
            this.startDetail="";
            this.startPosX = 0;
            this.startPosY = 0;
            this.endDetail="";
            this.endPosX=0;
            this.endPosY=0;
            this.drvseq="";
            this.drvName="";
            this.drvMinno = "";
            this.carNumber="";
            this.callState="";
        }
    }

    /**
     * 메시지 출력
     * @param title
     * @param msg
     */
    public void doAlertDialogView (String title, String msg)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(OrderWaitActivity.this);
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

    /////////////////////////////////////////////////////////////
    // Call Info 폴링 타이머
    private final int TIMER_DEFAULT_DELAY = 1000 * 30;  // 1분 간격
    private Timer mTimer = null;
    private void startTimer()
    {
        if (mTimer != null)
            stopTimer ();

        mTimer = new Timer();

        mTimer.schedule(new CallInfoTimer(), 1000, TIMER_DEFAULT_DELAY);
    }

    private void stopTimer()
    {
        if (mTimer != null)
            mTimer.cancel();
        mTimer = null;
    }

    class CallInfoTimer extends TimerTask
    {
        @Override
        public void run() {
            Logview.Logwrite(THIS_TAG, "Call into call");
            handler.sendEmptyMessage(MSG_CALL_INFO);
        }
    }
}
