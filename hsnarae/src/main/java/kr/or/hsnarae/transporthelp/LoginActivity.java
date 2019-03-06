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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.gson.Gson;
import com.ionemax.iomlibrarys.log.Logview;
import com.squareup.otto.Subscribe;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kr.or.hsnarae.transporthelp.common.util.BusEventProvider;
import kr.or.hsnarae.transporthelp.common.util.ErrorcodeToString;
import kr.or.hsnarae.transporthelp.common.util.GlobalValues;
import kr.or.hsnarae.transporthelp.impl.net.json.IJSONPageMethod;
import kr.or.hsnarae.transporthelp.impl.net.json.JSONMessageEvent;
import kr.or.hsnarae.transporthelp.impl.net.json.RetrofitProcessManager;
import kr.or.hsnarae.transporthelp.impl.net.json.rep.REP_JSONLogin;
import kr.or.hsnarae.transporthelp.impl.net.json.rep.REP_JSONTempPassword;
import kr.or.hsnarae.transporthelp.impl.net.json.rep.REP_JSONUserInfo;
import kr.or.hsnarae.transporthelp.impl.net.json.req.JSON_REQLogin;
import kr.or.hsnarae.transporthelp.impl.net.json.req.JSON_REQTempPassword;
import kr.or.hsnarae.transporthelp.impl.net.json.req.JSON_REQUserInfo;
import kr.or.hsnarae.transporthelp.impl.preference.ConfigPreference;
import kr.or.hsnarae.transporthelp.impl.preference.OrderPreference;

public class LoginActivity extends Activity implements View.OnClickListener
{
    private final String THIS_TAG = "LoginActivity";

    private final int MSG_HTTP_FAILED = 0;
    private final int MSG_LOGIN_SUCCESS = 1;
    private final int MSG_LOGIN_FALIED = 2;
    private final int MSG_USERINFO_SUCCESS = 3;
    private final int MSG_USERINFO_FAILED = 4;
    private final int MSG_DIALOG_OK = 5;

    private final int LOGIN_MSG_REAGREEMENTYN = 20;             // 1.재동의 대상
    private final int LOGIN_MSG_MEMBERINFOCHGYN = 21;           // 2.회원정보 수정
    private final int LOGIN_MSG_WEBPASSWORDRECHANGEYN = 22;     // 3.비밀번호 변경 요청

    private ConfigPreference mConfigPerference;
    private BootstrapEditText mEditID, mEditPW;
    private String mUserID="";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mConfigPerference = ConfigPreference.getInstance();
        mConfigPerference.setPreference(getBaseContext());

        // 로그인
        Button btn = (Button)findViewById(R.id.btnLogin);
        btn.setOnClickListener(this);

        // 회원가입
        Button bstBtn = (Button)findViewById(R.id.btnRegistry);
        bstBtn.setOnClickListener(this);

        // 비밀번호 찾기
        bstBtn = (Button)findViewById(R.id.btnPWSearch);
        bstBtn.setOnClickListener(this);

        // 아이디찾기
        bstBtn = (Button)findViewById(R.id.btnIDSearch);
        bstBtn.setOnClickListener(this);

        mEditID = (BootstrapEditText)findViewById(R.id.editID);
        mEditPW = (BootstrapEditText)findViewById(R.id.editPW);

        String id = mConfigPerference.getUserId();
        //id = "hsadmin";
        if (id != null && id.length() > 0)
        {
            mEditID.setText(id);
            mEditID.setSelection(mEditID.length());
            mEditPW.requestFocus();
        }

        mEditPW.setText("");
        mEditPW.setSelection(mEditPW.length());
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
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnLogin:     // 로그인
                doLogin();
                break;
            case R.id.btnRegistry:  // 회원가입
                doUserRegistry (false);
                break;
            case R.id.btnPWSearch:  // 비밀번호 찾기
                doSearchPW("P");
                break;
            case R.id.btnIDSearch:  // 아이디 찾기
                doSearchPW("I");
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
                case MSG_LOGIN_SUCCESS:
                    if (msg.arg1 == LOGIN_MSG_REAGREEMENTYN)
                    {
                        // 1.재동의 대상
                        mConfigPerference.setUserReagreement(false);
                        doUserRegistry (true);
                    } else if (msg.arg1 == LOGIN_MSG_MEMBERINFOCHGYN)
                    {
                        // 2.회원정보 수정
                        doAlertDialogReturnView ("알림", (String)msg.obj);

                    } else if (msg.arg1 == LOGIN_MSG_WEBPASSWORDRECHANGEYN)
                    {
                        // 3.비밀번호 변경 요청
                        doAlertDialogReturnView ("알림", (String)msg.obj);
                    } else {
                        doUserInfo ();
                    }
//                    boolean flag = (boolean)msg.obj;
//                    if (!flag)
//
//                    else
//                        doAlertDialogReturnView ("알림", getResources().getString(R.string.phonenum_failed));
                    break;
                case MSG_HTTP_FAILED:
                    break;
                case MSG_USERINFO_SUCCESS:
                    Intent intent = new Intent(LoginActivity.this, ProposeMapActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case MSG_DIALOG_OK:
                    doUserInfo ();
                    break;
                case MSG_LOGIN_FALIED:
                    break;
            }
        }
    };

    /**
     * 회원가입 버튼처리
     */
    private void doUserRegistry (boolean flag)
    {
        Intent intent;

        intent = new Intent(LoginActivity.this, ProvisionsActivity.class);
        intent.putExtra("type", flag);
        intent.putExtra("registry", (flag ? false : true));
        startActivity(intent);
        finish();
    }

    /**
     * 로그인 처리
     * 1. AuthKey가 없을 경우는 회원등록을 먼저 하도록 처리
     */
    private void doLogin ()
    {
//        String authkey = mConfigPerference.getAuthkey();
//        if (authkey == null || authkey.length() < 1)
//        {
//            Toast.makeText(getBaseContext(), "등록 회원이 아닙니다.", Toast.LENGTH_LONG).show();
//            return;
//        }

        // id와 pw 유효성 검사
        String id = mEditID.getText().toString().trim();
        String pw = mEditPW.getText().toString().trim();

        if (id==null || id.length() < 4)
        {
            Toast.makeText(getBaseContext(), "아이디 입력이 잘못 되었습니다.", Toast.LENGTH_LONG).show();
            return;
        }

        if (pw == null || pw.length() < 4)
        {
            Toast.makeText(getBaseContext(), "비밀번호 입력이 잘못 되었습니다.", Toast.LENGTH_LONG).show();
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

        startProgress();
        boolean agreement = mConfigPerference.getUserReagreement();

        // 로그인 호출
        JSON_REQLogin login = new JSON_REQLogin(id, pw_encryption,
                mConfigPerference.getPhonenumber(), (agreement ? "S" : ""));
        RetrofitProcessManager.doRetrofitRequest(JSON_REQLogin.PAGE_NAME, login.getParams());

        mConfigPerference.setUserReagreement(false);
        mUserID = id;
    }

    /**
     * 사용자 정복를 가져 온다.
     */
    private void doUserInfo ()
    {
        Logview.Logwrite(THIS_TAG, "doUserInfo");

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSON_REQUserInfo reqvalue = new JSON_REQUserInfo(mConfigPerference.getAuthkey(), mConfigPerference.getPushkey());
                RetrofitProcessManager.doRetrofitRequest(JSON_REQUserInfo.PAGE_NAME, reqvalue.getParams());
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
     * 비밀번호 찾기
     */
    private void doSearchPW(final String search)
    {
        Logview.Logwrite(THIS_TAG, "doSearchPW");

//        String authKey = mConfigPerference.getAuthkey();
//        if (authKey == null || authKey.length() < 1)
//        {
            //Toast.makeText(getBaseContext(), "인증키가 없습니다.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(LoginActivity.this, ProvisionsActivity.class);
            intent.putExtra("type", false);
            intent.putExtra("registry", false);
            intent.putExtra("idpw_search", search);
            startActivity(intent);
            finish();

//            return;
//        }
//
//        startProgress();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                JSON_REQTempPassword reqvalue = new JSON_REQTempPassword(mConfigPerference.getPhonenumber(),
//                        mConfigPerference.getPushkey(), mConfigPerference.getAuthkey(), search);
//                RetrofitProcessManager.doRetrofitRequest(JSON_REQTempPassword.PAGE_NAME, reqvalue.getParams());
//            }
//        }).start();
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
                if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.Login.name().toLowerCase())) {
                    receiveLogin(event);
                }
                else if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.UserInfo.name().toLowerCase())) {
                    receiveUserInfo(event);
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
     * 로그인 수신 처리
     * @param event
     */
    private void receiveLogin(JSONMessageEvent event)
    {
        Logview.Logwrite(THIS_TAG, "receiveLogin");
        Gson gson = new Gson();

        REP_JSONLogin login = gson.fromJson(event.getMessageBody(), REP_JSONLogin.class);

        if (login.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success)))
        {
            Logview.Logwrite(THIS_TAG, "Login success");
        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "Login false : " + login.getBody().getCause());
            Toast.makeText(getBaseContext(),
                    ErrorcodeToString.getError(login.getBody().getResult(), login.getBody().getCause()),
                    Toast.LENGTH_LONG).show();

            handler.sendEmptyMessage(MSG_LOGIN_FALIED);
            return;
        }

        String authkey = login.getBody().getAuthKey();
        Logview.Logwrite(THIS_TAG, "Login authkey : " + authkey);
        mConfigPerference.setAuthkey(authkey);
        mConfigPerference.setUserId(mUserID);

        int msg_type = 0;
        // 1. 재동의 대상
        Logview.Logwrite(THIS_TAG, "Login REAGREEMENTYN : " + login.getBody().getReagrement());
        // 2. 회원정보 수정
        Logview.Logwrite(THIS_TAG, "Login MEMBERINFOCHGYN : " + login.getBody().getChangeInfo());
        // 3. 비밀번호 변경요청
        Logview.Logwrite(THIS_TAG, "Login WEBPASSWORDRECHANGEYN : " + login.getBody().getPasswrodchange());

        String msg="";
        if (login.getBody().getReagrement()) {
            msg_type = LOGIN_MSG_REAGREEMENTYN;
            msg = getString(R.string.regreement);
        }
        else if (login.getBody().getChangeInfo()) {
            msg_type = LOGIN_MSG_MEMBERINFOCHGYN;
            msg = getString(R.string.phonenum_failed);
        }
        else if (login.getBody().getPasswrodchange()) {
            msg_type = LOGIN_MSG_WEBPASSWORDRECHANGEYN;
            msg = getString(R.string.passwordchange);
        }

        // 비밀번호 실패 5회
        Logview.Logwrite(THIS_TAG, "Login WEBPASSWORDFAILYN : " + login.getBody().getPasswrodfail());

        handler.obtainMessage(MSG_LOGIN_SUCCESS, msg_type, 0, msg).sendToTarget();
    }

    /**
     * 사용자 정보요청
     * @param event
     */
    private void receiveUserInfo (JSONMessageEvent event)
    {
        Logview.Logwrite(THIS_TAG, "receiveUserInfo");
        Gson gson = new Gson();

        REP_JSONUserInfo json = gson.fromJson(event.getMessageBody(), REP_JSONUserInfo.class);

        if (json.getBody().getResult().equalsIgnoreCase("S0000"))
        {
            Logview.Logwrite(THIS_TAG, "receiveUserInfo success");
        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receiveUserInfo false : " + json.getBody().getCause());
            Toast.makeText(getBaseContext(),
                    ErrorcodeToString.getError(json.getBody().getResult(), json.getBody().getCause()),
                    Toast.LENGTH_LONG).show();

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
        Logview.Logwrite(THIS_TAG, "Login receiveUserInfo authkey : " + authkey);

        mConfigPerference.setUserName((username == null ? "" : username));
        mConfigPerference.setPhonenumber((minno == null ? "" : minno));
        mConfigPerference.setUserEmail((email == null ? "" : email));
        mConfigPerference.setAuthkey((authkey == null ? "" : authkey));

        OrderPreference orderPreference = OrderPreference.getInstance();
        orderPreference.setPreference(getBaseContext());

        orderPreference.setOrderCallid((callid == null ? "" : callid));
        orderPreference.setOrderState((callstate == null ? "" : callstate));

        handler.sendEmptyMessage(MSG_USERINFO_SUCCESS);
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

        if (json.getBody().getResult().equalsIgnoreCase("S0000"))
        {
            Logview.Logwrite(THIS_TAG, "receiveTempPassword success");
            msg = "임시 비밀번호를 문자 발송하였습니다.";
        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receiveTempPassword false : " + json.getBody().getCause());
            msg = ErrorcodeToString.getError(json.getBody().getResult(), json.getBody().getCause());
            mConfigPerference.setAuthkey("");
        }

        doAlertDialogView ("알림", msg);
    }

    /**
     * 메시지 출력
     * @param title
     * @param msg
     */
    public void doAlertDialogView (String title, String msg)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
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

    public void doAlertDialogReturnView (String title, String msg)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
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

    //////////////////////////////////////////////////////////////////////////////////////
    // Progress 원형
    //
    private ProgressDialog mProgressDialog = null;
    private synchronized void startProgress()
    {
        stopProgress();

        mProgressDialog = ProgressDialog.show(LoginActivity.this, "", "잠시 기다려주세요", true);
    }

    private synchronized void stopProgress()
    {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
        mProgressDialog = null;
    }


}
