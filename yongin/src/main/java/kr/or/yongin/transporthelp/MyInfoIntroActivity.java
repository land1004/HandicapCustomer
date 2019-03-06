package kr.or.yongin.transporthelp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.gson.Gson;
import com.ionemax.iomlibrarys.log.Logview;
import com.ionemax.iomlibrarys.util.Util;
import com.squareup.otto.Subscribe;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kr.or.yongin.transporthelp.common.FontAwesome.CFontAwesomeManager;
import kr.or.yongin.transporthelp.common.util.BusEventProvider;
import kr.or.yongin.transporthelp.impl.net.json.IJSONPageMethod;
import kr.or.yongin.transporthelp.impl.net.json.JSONMessageEvent;
import kr.or.yongin.transporthelp.impl.net.json.RetrofitProcessManager;
import kr.or.yongin.transporthelp.impl.net.json.rep.REP_JSONPwConfirm;
import kr.or.yongin.transporthelp.impl.net.json.req.JSON_REQPwConfirm;
import kr.or.yongin.transporthelp.impl.preference.ConfigPreference;

public class MyInfoIntroActivity extends Activity implements View.OnClickListener
{
    private final String THIS_TAG = "MyInfoIntroActivity";

    private final int MSG_HTTP_FAILED = 0;

    private final int MSG_PWCONFIRM_SUCCESS = 10;
    private final int MSG_PWCONFIRM_FAILED = 11;

    private ConfigPreference mConfigPrefernce;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info_intro);

        mConfigPrefernce = ConfigPreference.getInstance();
        mConfigPrefernce.setPreference(getBaseContext());

        // 기본 설정
        TextView title = (TextView)findViewById(R.id.txtTitle);
        title.setText("나의정보");

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
        bstBTNCenter.setText("상세보기 및 수정하기");

        //////////////////////////////////////////////////////////////
        BootstrapButton btn = (BootstrapButton)findViewById(R.id.btnCheckPassword);
        btn.setOnClickListener(this);

        TextView tv = (TextView)findViewById(R.id.txtMyInfoUserName);
        tv.setText(mConfigPrefernce.getUserName());
        tv = (TextView)findViewById(R.id.txtMyInfouserPhone);
        tv.setText(Util.makeTelNumber(mConfigPrefernce.getPhonenumber()));
        tv = (TextView)findViewById(R.id.txtMyInfoUserEmail);
        tv.setText(mConfigPrefernce.getUserEmail());
    }

    @Override
    protected void onPause() {
        BusEventProvider.getInstance().unregister(this);
        super.onPause();
    }

    @Override
    protected void onStart()
    {
        Logview.Logwrite(THIS_TAG, "onStart");
        BusEventProvider.getInstance().register(this);
        stopProgress ();
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        Logview.Logwrite(THIS_TAG, "onStop");

        super.onStop();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnCenter:        // 수정
                doPwConfirmView ();
                break;
            case R.id.btnCheckPassword: // 비밀번호 확인
                doPWConfirm();
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
                case MSG_HTTP_FAILED:
                    Toast.makeText(getBaseContext(), "정보요청 실패", Toast.LENGTH_LONG).show();
                    break;
                case MSG_PWCONFIRM_SUCCESS:
                    Intent intent = new Intent(MyInfoIntroActivity.this, MyInfoActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                    break;
                case MSG_PWCONFIRM_FAILED:
                    doAlertDialogView ("알림", "비밀번호가 맞지 않습니다.");
                    break;
            }
        }
    };

    /**
     * 비밀번호 확인 화면 구성
     */
    private void doPwConfirmView()
    {
        // 기본정보
        LinearLayout layout = (LinearLayout)findViewById(R.id.layoutMyInfoDefault);
        layout.setVisibility(View.GONE);
        FrameLayout btnlayout = (FrameLayout)findViewById(R.id.framlayoutBottom);
        btnlayout.setVisibility(View.GONE);

        // 비밀번호 입력
        layout = (LinearLayout)findViewById(R.id.layoutMyInfoPW);
        layout.setVisibility(View.VISIBLE);
    }

    /**
     * 비밀번호 확인
     */
    private void doPWConfirm ()
    {
        Logview.Logwrite(THIS_TAG, "doPWConfirm");

        startProgress();

        new Thread(new Runnable() {
            @Override
            public void run() {
                BootstrapEditText et = (BootstrapEditText)findViewById(R.id.editUserPW);
                String pw = et.getText().toString().trim();

                if (pw == null || pw.length() < 1)
                {
                    Toast.makeText(getBaseContext(), "비밀번호 입력이 잘못되었습니다.", Toast.LENGTH_LONG).show();
                    return;
                }

                // 암호 SHA 256으로 변환
                String pw_encryption = getSHAEncryption (pw);
                if (pw_encryption == null)
                {
                    Toast.makeText(getBaseContext(), "비밀번호 암호화 실패", Toast.LENGTH_LONG).show();
                    return;
                }

                Logview.Logwrite(THIS_TAG, "PW Encryption : " + pw_encryption + " / org pw : " + pw);

                JSON_REQPwConfirm reqvalue = new JSON_REQPwConfirm(mConfigPrefernce.getAuthkey(), pw_encryption);
                RetrofitProcessManager.doRetrofitRequest(JSON_REQPwConfirm.PAGE_NAME, reqvalue.getParams());

            }
        }).start();
    }

    /**
     * SHA 256으로 암호화
     * @param strPW
     * @return
     */
    private String getSHAEncryption(String strPW)
    {
        String sha = "";

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            try {
                byte[] hash = digest.digest(strPW.getBytes("UTF-8"));
                StringBuffer sb = new StringBuffer();

                for(int i = 0 ; i < hash.length ; i++){
                    sb.append(Integer.toString((hash[i]&0xff) + 0x100, 16).substring(1));
                }
                sha = sb.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            sha = null;
        }

        return sha;
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
                if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.PwConfirm.name().toLowerCase())) {
                    receivePWConfirm(event);
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
     * 비밀번호 확인
     * @param event
     */
    private void receivePWConfirm(JSONMessageEvent event)
    {
        Gson gson = new Gson();

        REP_JSONPwConfirm login = gson.fromJson(event.getMessageBody(), REP_JSONPwConfirm.class);

        if (login.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success)))
        {
            Logview.Logwrite(THIS_TAG, "receivePWConfirm success");
            handler.sendEmptyMessage(MSG_PWCONFIRM_SUCCESS);
        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receivePWConfirm false : " + login.getBody().getCause());
            handler.sendEmptyMessage(MSG_PWCONFIRM_FAILED);
        }
    }

    /**
     * 메시지 출력
     * @param title
     * @param msg
     */
    public void doAlertDialogView (String title, String msg)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(MyInfoIntroActivity.this);
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

        mProgressDialog = ProgressDialog.show(MyInfoIntroActivity.this, "", "잠시 기다려주세요", true);
    }

    private synchronized void stopProgress()
    {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
        mProgressDialog = null;
    }

}
