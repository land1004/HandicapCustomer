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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.gson.Gson;
import com.ionemax.iomlibrarys.log.Logview;
import com.ionemax.iomlibrarys.util.Util;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;

import kr.or.hsnarae.transporthelp.common.FontAwesome.CFontAwesomeManager;
import kr.or.hsnarae.transporthelp.common.util.BusEventProvider;
import kr.or.hsnarae.transporthelp.common.util.ErrorcodeToString;
import kr.or.hsnarae.transporthelp.impl.net.json.IJSONPageMethod;
import kr.or.hsnarae.transporthelp.impl.net.json.JSONMessageEvent;
import kr.or.hsnarae.transporthelp.impl.net.json.RetrofitProcessManager;
import kr.or.hsnarae.transporthelp.impl.net.json.rep.REP_JSONLogin;
import kr.or.hsnarae.transporthelp.impl.net.json.rep.REP_JSONSmsAuthSend;
import kr.or.hsnarae.transporthelp.impl.net.json.req.JSON_REQLogin;
import kr.or.hsnarae.transporthelp.impl.net.json.req.JSON_REQSmsAuthSend;
import kr.or.hsnarae.transporthelp.impl.preference.ConfigPreference;
import kr.or.hsnarae.transporthelp.impl.rowadaptors.CustomSpinnerAdapter;

public class CertificationActivity extends Activity implements View.OnClickListener
{
    private final String THIS_TAG = "CertificationActivity";

    private final int MSG_ALERT_OK = 0;
    private final int MSG_HTTP_FAILED = 1;
    private final int MSG_SEND_SUCCESS = 2;

    private ConfigPreference mPreference;
    private BootstrapEditText mPhoneMiddle, mPhoneLast;
    private int mSpinSelectIndex = 0;
    private ArrayList<String> mPhoneFirstList;
    private int mAuthLimtCount = 0;
    private String mUserMinno = "";
    private boolean mRegistry = true;
    private String mIDPWSearch="";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certification);

        mPreference = ConfigPreference.getInstance();
        mPreference.setPreference(getBaseContext());

        // 기본 설정
        Intent intent = getIntent();
        mRegistry = intent.getBooleanExtra("registry", true);
        mIDPWSearch = intent.getStringExtra("idpw_search");

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
        bstBTNCenter.setText("인증번호 요청");

        //////////////////////////////////////////////////////////////

        initView();

    }

    @Override
    protected void onPause() {
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
        stopProgress();

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView ()
    {
        String minno = mPreference.getPhonenumber();
        int size = minno.length();
        String first=minno.substring(0, size-8);
        String middle=minno.substring(size-8, size-4);
        String last=minno.substring(size-4, size);

        // 전화번호 국번
        mPhoneFirstList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.phone_first)));

        Spinner spinState = (Spinner)findViewById(R.id.spinnerMinFirst);
        CustomSpinnerAdapter customSpinnerAdapter=new CustomSpinnerAdapter(getBaseContext(), mPhoneFirstList);
        spinState.setAdapter(customSpinnerAdapter);
        spinState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSpinSelectIndex = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mSpinSelectIndex = mPhoneFirstList.indexOf(first);
        spinState.setSelection(mSpinSelectIndex);

        mPhoneMiddle = (BootstrapEditText)findViewById(R.id.editMinMiddle);
        mPhoneMiddle.setText(middle);
        mPhoneLast = (BootstrapEditText)findViewById(R.id.editMinLast);
        mPhoneLast.setText(last);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnCenter:    // 인증번호 요청
                doSmsAuthSend ();
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
                case MSG_SEND_SUCCESS:
                    Intent intent = new Intent(CertificationActivity.this, CertificationConfirmActivity.class);
                    intent.putExtra("title", "휴대폰 인증");
                    intent.putExtra("minno", mUserMinno);
                    intent.putExtra("limitcount", mAuthLimtCount);
                    intent.putExtra("registry", mRegistry);
                    intent.putExtra("idpw_search", mIDPWSearch);
                    startActivity(intent);

                    break;
                case MSG_HTTP_FAILED:
                    break;
            }
        }
    };

    /**
     * 메시지 다이얼로그
     * @param title
     * @param msg
     */
    private void doMessageView (String title, String msg)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(CertificationActivity.this);
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
     * 인증번호 요청
     */
    private void doSmsAuthSend ()
    {
        CheckBox ch = (CheckBox)findViewById(R.id.checkCertification);
        if (!ch.isChecked())
        {
            Toast.makeText(getBaseContext(),"휴대폰 번호 수집에 동의해 주십시요.", Toast.LENGTH_LONG).show();
            return;
        }

        String midle = mPhoneMiddle.getText().toString().trim();
        String last = mPhoneLast.getText().toString().trim();

        if (midle == null || midle.length() != 4 || last.length() != 4)
        {
            Toast.makeText(getBaseContext(),"전화번호 입력이 잘못 되었습니다.", Toast.LENGTH_LONG).show();
            return;
        }

        mUserMinno = mPhoneFirstList.get(mSpinSelectIndex);
        mUserMinno += midle;
        mUserMinno += last;

        if(Util.isValidCellPhoneNumber(mUserMinno) == false)
        {
            Toast.makeText(getBaseContext(),"전화번호 입력이 잘못 되었습니다.", Toast.LENGTH_LONG).show();
            return;
        }

        startProgress ();

        // SMS 전송 호출
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSON_REQSmsAuthSend smssend = new JSON_REQSmsAuthSend(mUserMinno, "D", mPreference.getPushkey());
                RetrofitProcessManager.doRetrofitRequest(JSON_REQSmsAuthSend.PAGE_NAME, smssend.getParams());
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
                if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.SmsAuthSend.name().toLowerCase())) {
                    receiveSmsSend(event);
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
     * SMS 전송 요청 응답 처리
     * @param event
     */
    private void receiveSmsSend (JSONMessageEvent event)
    {
        Gson gson = new Gson();

        REP_JSONSmsAuthSend json = gson.fromJson(event.getMessageBody(), REP_JSONSmsAuthSend.class);

        if (json.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success)))
        {
            Logview.Logwrite(THIS_TAG, "SMS Send success limit : " + json.getBody().getAuthLimitFlag());
            handler.sendEmptyMessage(MSG_SEND_SUCCESS);
        } else {
            Logview.Logwrite(THIS_TAG, "SMS Send false : " + json.getBody().getCause());
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

    //////////////////////////////////////////////////////////////////////////////////////
    // Progress 원형
    //
    private ProgressDialog mProgressDialog = null;
    private synchronized void startProgress()
    {
        stopProgress();

        mProgressDialog = ProgressDialog.show(CertificationActivity.this, "", "잠시 기다려주세요", true);
    }

    private synchronized void stopProgress()
    {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
        mProgressDialog = null;
    }


}
