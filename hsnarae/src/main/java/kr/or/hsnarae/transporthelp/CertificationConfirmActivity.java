package kr.or.hsnarae.transporthelp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.gson.Gson;
import com.ionemax.iomlibrarys.log.Logview;
import com.squareup.otto.Subscribe;

import java.util.Timer;
import java.util.TimerTask;

import kr.or.hsnarae.transporthelp.common.FontAwesome.CFontAwesomeManager;
import kr.or.hsnarae.transporthelp.common.SMS.SMSMessageEvent;
import kr.or.hsnarae.transporthelp.common.util.BusEventProvider;
import kr.or.hsnarae.transporthelp.common.util.ErrorcodeToString;
import kr.or.hsnarae.transporthelp.impl.net.json.IJSONPageMethod;
import kr.or.hsnarae.transporthelp.impl.net.json.JSONMessageEvent;
import kr.or.hsnarae.transporthelp.impl.net.json.RetrofitProcessManager;
import kr.or.hsnarae.transporthelp.impl.net.json.rep.REP_JSONSmsAuthCheck;
import kr.or.hsnarae.transporthelp.impl.net.json.rep.REP_JSONSmsAuthSend;
import kr.or.hsnarae.transporthelp.impl.net.json.rep.REP_JSONTempPassword;
import kr.or.hsnarae.transporthelp.impl.net.json.req.JSON_REQSmsAuthCheck;
import kr.or.hsnarae.transporthelp.impl.net.json.req.JSON_REQSmsAuthSend;
import kr.or.hsnarae.transporthelp.impl.net.json.req.JSON_REQTempPassword;
import kr.or.hsnarae.transporthelp.impl.preference.ConfigPreference;

public class CertificationConfirmActivity extends Activity implements View.OnClickListener
{
    private final String THIS_TAG = "CertificationConfirmActivity";

    private final int MSG_ALERT_OK = 0;
    private final int MSG_HTTP_FAILED = 1;
    private final int MSG_SEND_SUCCESS = 2;
    private final int MSG_CHECK_SUCCESS = 3;
    private final int MSG_CHECK_FAILD = 4;
    private final int MSG_TIMER_STEP = 10;
    private final int MSG_SMS_RECEIVE=20;

    private final int MAX_TIME_COUNT = 3 * 60;

    private ConfigPreference mConfigPreference;
    private BootstrapEditText mConfirmCode;
    private BootstrapButton btnResend;
    private int mTimerCount = MAX_TIME_COUNT;
    private int mAuthLimtCount = 0;
    private String mUserMinno = "";

    private boolean mRegistry = true;
    private String mIDPWSearch="";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certification_confirm);

        mConfigPreference = ConfigPreference.getInstance();
        mConfigPreference.setPreference(getBaseContext());

        Intent intent = getIntent();
        mAuthLimtCount = intent.getIntExtra("limitcount", 0);
        mUserMinno = intent.getStringExtra("minno");
        mRegistry = intent.getBooleanExtra("registry", true);
        mIDPWSearch = intent.getStringExtra("idpw_search");

        // 기본 설정
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
        bstBTNCenter.setText("확 인");

        //////////////////////////////////////////////////////////////
        mConfirmCode = (BootstrapEditText)findViewById(R.id.editConfirmCode);
        btnResend = (BootstrapButton)findViewById(R.id.btnReSend);
        btnResend.setOnClickListener(this);
        btnResend.setEnabled(false);

        mTimerCount = MAX_TIME_COUNT;

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        startTimer();

        handler.sendEmptyMessageDelayed(MSG_ALERT_OK, 1000);
    }

    @Override
    protected void onPause()
    {
        BusEventProvider.getInstance().unregister(this);
        stopTimer();
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

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnCenter:        // 확인
                doSendAuthCheck ();
                break;
            case R.id.btnReSend:        // 재전송
                if (mAuthLimtCount > 0)
                    doResend ();
                else
                    doMessageView("알림", "인증 횟수가 초과하였습니다.센터로 문의 하시기 바랍니다.");

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
                case MSG_ALERT_OK:
                    break;
                case MSG_TIMER_STEP:
                    doTimerView ();
                    break;
                case MSG_CHECK_SUCCESS:
                    Intent intent = null;
                    if (mRegistry) {
                        intent = new Intent(CertificationConfirmActivity.this, RegistryActivity.class);
                    } else {
                        if (mIDPWSearch != null && mIDPWSearch.length() > 0)
                            doSearchPW (mIDPWSearch);
                        else
                            intent = new Intent(CertificationConfirmActivity.this, LoginActivity.class);
                    }

                    if (intent != null) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                                Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(intent);
                        finish();
                    }
                    break;
                case MSG_CHECK_FAILD:
                    if (mAuthLimtCount > 0)
                        doResend ();
                    break;
                case MSG_SMS_RECEIVE:
                    String code = (String)msg.obj;
                    setConfirmCode (code);
                    break;
            }
        }
    };

    /**
     * SMS 수신된 코드를 입력 한다.
     * @param code
     */
    private void setConfirmCode(String code)
    {
        mConfirmCode.setText(code);

        doSendAuthCheck ();
    }

    /**
     * SMS 인증 요청
     */
    private void doSendAuthCheck()
    {
        String code = mConfirmCode.getText().toString().trim();
        if (code == null || code.length() < 1)
        {
            Toast.makeText(getBaseContext(), "인증번호 입력이 잘못되었습니다.", Toast.LENGTH_LONG).show();
            return;
        }

        stopTimer();

        startProgress();
        // 인증요청
        new Thread(new Runnable() {
            @Override
            public void run() {
                String code = mConfirmCode.getText().toString().trim();
                JSON_REQSmsAuthCheck smscheck = new JSON_REQSmsAuthCheck(mUserMinno, "D", mConfigPreference.getPushkey(), code);
                RetrofitProcessManager.doRetrofitRequest(JSON_REQSmsAuthCheck.PAGE_NAME, smscheck.getParams());
            }
        }).start();
    }

    /**
     * 재전송
     */
    private void doResend ()
    {
        startProgress();

        // SMS 전송 호출
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSON_REQSmsAuthSend smssend = new JSON_REQSmsAuthSend(mUserMinno, "D", mConfigPreference.getPushkey());
                RetrofitProcessManager.doRetrofitRequest(JSON_REQSmsAuthSend.PAGE_NAME, smssend.getParams());
            }
        }).start();
    }

    /**
     * 제한 시간 표시
     */
    private void doTimerView()
    {
        mTimerCount --;
        String msg = "";
        if (mTimerCount == 0) {
            msg = "인증번호 입력시간이 초과되었습니다.";
            stopTimer();
            if (mAuthLimtCount > 0)
                btnResend.setEnabled(true);
            else
                doMessageView("알림", "인증 횟수가 초과하였습니다.센터로 문의 하시기 바랍니다.");
        } else {
            int min=0, sec=0;
            min = (int)(mTimerCount / 60);
            sec = (int)(mTimerCount % 60);

            msg = String.format("인증번호 입력까지 %d:%02d초 남았습니다.", min, sec);
        }

        TextView tv = (TextView)findViewById(R.id.txtTimeView);
        tv.setText(msg);
    }

    /**
     * ID/PW 찾기
     * @param search
     */
    private void doSearchPW(final String search)
    {
        Logview.Logwrite(THIS_TAG, "doSearchPW");

        String authKey = mConfigPreference.getAuthkey();

        startProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSON_REQTempPassword reqvalue = new JSON_REQTempPassword(mConfigPreference.getPhonenumber(),
                        mConfigPreference.getPushkey(), mConfigPreference.getAuthkey(), search);
                RetrofitProcessManager.doRetrofitRequest(JSON_REQTempPassword.PAGE_NAME, reqvalue.getParams());
            }
        }).start();
    }

    /**
     * 메시지 다이얼로그
     * @param title
     * @param msg
     */
    private void doMessageView (String title, String msg)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(CertificationConfirmActivity.this);
        alert.setTitle(title);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.obtainMessage(MSG_ALERT_OK).sendToTarget();
                dialog.dismiss();     //닫기
            }
        });
        alert.setMessage(msg);
        alert.show();
    }

    /**
     * SMS를 파싱하여 인증 코드를 얻는다.
     * @param event
     */
    @Subscribe
    public void smsMessageEvent(SMSMessageEvent event)
    {
        String code = event.getConfirmCode();
        Logview.Logwrite(THIS_TAG, "smsMessageEvent code : "  + code);
        if (code != null && code.length() > 0)
            handler.obtainMessage(MSG_SMS_RECEIVE, code).sendToTarget();
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
                if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.SmsAuthCheck.name().toLowerCase())) {
                    receiveAuthCheck(event);
                }
                else if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.SmsAuthSend.name().toLowerCase())) {
                    receiveSmsSend(event);
                }
                else if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.TempPassword.name().toLowerCase())) {
                    receiveTempPassword(event);
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
     * SMS 인증 처리 결과
     * @param event
     */
    private void receiveAuthCheck(JSONMessageEvent event)
    {
        Gson gson = new Gson();

        REP_JSONSmsAuthCheck json = gson.fromJson(event.getMessageBody(), REP_JSONSmsAuthCheck.class);

        if (json.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success)))
        {
            Logview.Logwrite(THIS_TAG, "receiveAuthCheck success authkey : " + json.getBody().getAuthKey());
        } else {
            Logview.Logwrite(THIS_TAG, "receiveAuthCheck false : " + json.getBody().getCause());
            Toast.makeText(getBaseContext(),
                    ErrorcodeToString.getError(json.getBody().getResult(), json.getBody().getCause()),
                    Toast.LENGTH_LONG).show();
            handler.sendEmptyMessage(MSG_CHECK_FAILD);
            return;
        }

        mConfigPreference.setAuthkey(json.getBody().getAuthKey());

        handler.sendEmptyMessage(MSG_CHECK_SUCCESS);
    }

    /**
     * SMS 전송 요청 응답 처리
     * @param event
     */
    private void receiveSmsSend (JSONMessageEvent event)
    {
        Gson gson = new Gson();

        REP_JSONSmsAuthSend json = gson.fromJson(event.getMessageBody(), REP_JSONSmsAuthSend.class);

        if (json.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success)))
        {
            Logview.Logwrite(THIS_TAG, "receiveSmsSend success : " + json.getBody().getAuthLimitFlag());
            handler.sendEmptyMessage(MSG_SEND_SUCCESS);
        } else {
            Logview.Logwrite(THIS_TAG, "receiveSmsSend false : " + json.getBody().getCause());
            Toast.makeText(getBaseContext(),
                    ErrorcodeToString.getError(json.getBody().getResult(), json.getBody().getCause()),
                    Toast.LENGTH_LONG).show();
            handler.sendEmptyMessage(MSG_HTTP_FAILED);
            return;
        }

        String limit = json.getBody().getAuthLimitFlag();
        if (limit != null && limit.length() > 0) {
            try {
                mAuthLimtCount = Integer.parseInt(limit);
            }catch (NumberFormatException e) {
                mAuthLimtCount = 0;
                e.printStackTrace();
            }
        } else {
            mAuthLimtCount = 0;
        }

    }

    /**
     * 임시 비밀번호 SMS 전송
     */
    private void receiveTempPassword (JSONMessageEvent event)
    {
        Logview.Logwrite(THIS_TAG, "receiveTempPassword");
        String msg = "";
        Gson gson = new Gson();

        REP_JSONTempPassword json = gson.fromJson(event.getMessageBody(), REP_JSONTempPassword.class);

        if (json.getBody().getResult().equalsIgnoreCase("S0000")) {
            Logview.Logwrite(THIS_TAG, "receiveTempPassword success");
            if (mIDPWSearch != null && mIDPWSearch.length() > 0) {
                if (mIDPWSearch.equalsIgnoreCase("P"))
                    msg = "임시 비밀번호를 문자 발송하였습니다.\n잠시만 기다려 주십시요.";
                else
                    msg = "아이디가 문자 전송되었습니다.";
            }

        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receiveTempPassword false : " + json.getBody().getCause());
            msg = ErrorcodeToString.getError(json.getBody().getResult(), json.getBody().getCause());
        }

        doAlertDialogView ("알림", msg);
    }

    public void doAlertDialogView (String title, String msg)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(CertificationConfirmActivity.this);
        alert.setTitle(title);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mIDPWSearch = "";
                handler.sendEmptyMessage(MSG_CHECK_SUCCESS);
                dialog.dismiss();     //닫기
            }
        });
        alert.setMessage(msg);
        alert.show();

    }

    /////////////////////////////////////////////////////////////////////////////////
    private Timer m_timer = null;
    protected void startTimer()
    {
        Logview.Logwrite(THIS_TAG, "PUSH Timer task run....");
        if (m_timer == null)
        {
            m_timer = new Timer();
            m_timer.schedule(new netTimerTask(), 1000, 1000);
        }
        else
        {
            stopTimer();

            try
            {
                Thread.sleep(100);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            m_timer = new Timer();
            m_timer.schedule(new netTimerTask(), 1000, 1000);
        }
    }

    protected synchronized void stopTimer()
    {
        if (m_timer != null)
            m_timer.cancel();
        m_timer = null;
    }

    class netTimerTask extends TimerTask
    {
        public void run()
        {
            if (handler != null)
                handler.sendEmptyMessage(MSG_TIMER_STEP);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // Progress 원형
    //
    private ProgressDialog mProgressDialog = null;
    private synchronized void startProgress()
    {
        stopProgress();

        mProgressDialog = ProgressDialog.show(CertificationConfirmActivity.this, "", "잠시 기다려주세요", true);
    }

    private synchronized void stopProgress()
    {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
        mProgressDialog = null;
    }


}
