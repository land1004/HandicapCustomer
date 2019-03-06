package kr.or.hsnarae.transporthelp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.or.hsnarae.transporthelp.common.FontAwesome.CFontAwesomeManager;
import kr.or.hsnarae.transporthelp.common.util.BusEventProvider;
import kr.or.hsnarae.transporthelp.common.util.ErrorcodeToString;
import kr.or.hsnarae.transporthelp.impl.net.json.IJSONPageMethod;
import kr.or.hsnarae.transporthelp.impl.net.json.JSONMessageEvent;
import kr.or.hsnarae.transporthelp.impl.net.json.RetrofitProcessManager;
import kr.or.hsnarae.transporthelp.impl.net.json.rep.REP_JSONMemberInfo;
import kr.or.hsnarae.transporthelp.impl.net.json.rep.REP_JSONZipcode;
import kr.or.hsnarae.transporthelp.impl.net.json.req.JSON_REQChgMemberInfo;
import kr.or.hsnarae.transporthelp.impl.net.json.req.JSON_REQMemberInfo;
import kr.or.hsnarae.transporthelp.impl.preference.ConfigPreference;

public class MyInfoActivity extends Activity implements View.OnClickListener
{
    private final String THIS_TAG = "MyInfoActivity";

    private final int MSG_HTTP_FAILED = 0;

    private final int MSG_READ_SUCCESS= 10;
    private final int MSG_SAVE_SUEECSS = 11;
    private final int MSG_ZIPCODE_FIND = 20;

    private REP_JSONMemberInfo.Body mMemberInfo;
    private REP_JSONZipcode.Address mZipcodeAddress;

    private BootstrapEditText mUserPW, mReUserPW;
    private String mUserID;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

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
        bstBTNCenter.setText("변경사항 적용하기");

        //////////////////////////////////////////////////////////////
        RadioButton radioButton = (RadioButton)findViewById(R.id.radioButtonM);
        radioButton.setEnabled(false);
        radioButton = (RadioButton)findViewById(R.id.radioButtonW);
        radioButton.setEnabled(false);

        BootstrapButton btnzipcode = (BootstrapButton)findViewById(R.id.btnZipcode);
        btnzipcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyInfoActivity.this, ZipcodeActivity.class);
                startActivityForResult(intent, ZipcodeActivity.ZIPCODE_RESULT_CODE);
            }
        });

        doGetUserInfo();

        mUserPW = (BootstrapEditText)findViewById(R.id.editUserPW);
        mUserPW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 입력하기 전에
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 입력되는 텍스트에 변화가 있을 때
                mReUserPW.setTag(false);
                mReUserPW.setText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 입력이 끝났을 때

            }
        });

        mUserPW.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b)
                    doPasswordValidationCheck (mUserPW.getText().toString().trim());
            }
        });

        mReUserPW = (BootstrapEditText)findViewById(R.id.editReUserPW);
        mReUserPW.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b)
                    doPWCheck();
            }
        });
        mReUserPW.setTag(false);

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnCenter:        // 변경사항 적용
                doUserInfoSave();
                break;
        }
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
    protected void onStop() {
        super.onStop();
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_HTTP_FAILED:
                    break;
                case MSG_READ_SUCCESS:
                    doViewUserInfo();
                    break;
                case MSG_ZIPCODE_FIND:
                    setZipcodView ((String)msg.obj);
                    break;
                case MSG_SAVE_SUEECSS:
                    doAlertDialogView ("알림", "수정되었습니다.");
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            if (requestCode == ZipcodeActivity.ZIPCODE_RESULT_CODE)
            {
                String addr = data.getStringExtra("addr");
                Logview.Logwrite(THIS_TAG, "addres : " + addr);

                Gson gson = new Gson();

                REP_JSONZipcode json = gson.fromJson(addr, REP_JSONZipcode.class);

                mZipcodeAddress = json.getAddress();
                handler.obtainMessage(MSG_ZIPCODE_FIND, json.getDetailAddress()).sendToTarget();
            }
        }

    }

    /**
     * 주소 표시
     * @param detailAddress
     */
    private void setZipcodView(String detailAddress)
    {
        // 우편번호
        TextView tv = (TextView)findViewById(R.id.txtZipcode);
        tv.setText (mZipcodeAddress.zipcode);

        // 기본 주소
        tv = (TextView)findViewById(R.id.txtUserAddress);
        tv.setText (mZipcodeAddress.addr1);

        // 상세 주소
        BootstrapEditText detail = (BootstrapEditText)findViewById(R.id.editUserAddrDetail);
        detail.setText(detailAddress);
    }

    /**
     * 입력된 비밀번호 검사
     */
    private void doPWCheck()
    {
        String pw = mUserPW.getText().toString().trim();
        String repw = mReUserPW.getText().toString().trim();

        if (pw.equalsIgnoreCase(repw))
        {
            mReUserPW.setTag(true);
        } else {
            Toast.makeText(getBaseContext(), "입력한 비밀번호가 맞지 않습니다.", Toast.LENGTH_LONG).show();
            mReUserPW.setFocusable(true);
            mReUserPW.setTag(false);
        }
    }

    /**
     * 비밀번호 유효성 검사
     */
    private boolean doPasswordValidationCheck(String pw)
    {
        boolean bret = true;

        if (pw != null && pw.length() > 8) {
            Pattern p = Pattern.compile("([a-zA-Z0-9].*[!,@,$,%,^,&,*,?,_,~])|([!,@,$,%,^,&,*,?,_,~].*[a-zA-Z0-9])");
            Matcher m = p.matcher(pw);
            if (!m.find()){
                Toast.makeText(getBaseContext(), "비밀번호는 영문, 숫자, 특수문자(!,@,$,%,^,&,*,?,_,~ 만 사용)를 사용하여 9자이상 입니다." +
                        "영문은 대소문자를 구분합니다.\n", Toast.LENGTH_LONG).show();
                return false;
            }
        } else {
            Toast.makeText(getBaseContext(), "비밀번호는 영문, 숫자, 특수문자(!,@,$,%,^,&,*,?,_,~ 만 사용)를 사용하여 9자이상 입니다." +
                    "영문은 대소문자를 구분합니다.\n", Toast.LENGTH_LONG).show();
            return false;
        }

        int SamePass_0 = 0; //동일문자 카운트
        int SamePass_1 = 0; //연속성(+) 카운드
        int SamePass_2 = 0; //연속성(-) 카운드

        int temp0, temp1, temp2;

        for(int i=0; i < pw.length() - 2; i++) {
            temp0 = pw.charAt(i);
            temp1 = pw.charAt(i+1);
            temp2 = pw.charAt(i+2);
            // 동일문자 연속 사용 검사
            if (temp0 == temp1 && temp1 == temp2){
                SamePass_0 ++;
            }

            //연속성(+) 카운드
            if( temp0 - temp1 == 1 && temp1 - temp2 == 1) {
                SamePass_1 = SamePass_1 + 1;
            }

            //연속성(-) 카운드
            if(temp0 - temp1 == -1 && temp1 - temp2 == -1) {
                SamePass_2 = SamePass_2 + 1;
            }
        }

        if(SamePass_0 > 0) {
            Toast.makeText(getBaseContext(), "연속된 동일문자(111, 또는 aaa 등)을 사용할 수 없습니다.", Toast.LENGTH_LONG).show();
            return false;
        }

        if(SamePass_1 > 0 || SamePass_2 > 0 ) {
            Toast.makeText(getBaseContext(), "연속된 문자열(123 또는 321, abc, cba 등)을 사용 할 수 없습니다.", Toast.LENGTH_LONG).show();
            return false;
        }

        // 제한 문자
        String[] restrict = {"love", "happy", "qwer", "asdf", "zxcv", "test", "gpin", "ipin"};
        int find =-1;
        for (String temp : restrict)
        {
            find = pw.indexOf(temp);
            if (find > -1)
            {
                Toast.makeText(getBaseContext(), "비밀번호 제한 문자가 포함되어 있습니다.\n(love, happy, qwer,asdf,zxcv,test,gpin,ipin)은 사용할 수 없습니다.", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        // 전화번호
        ConfigPreference configPreference = ConfigPreference.getInstance();
        configPreference.setPreference(getBaseContext());
        String minno = configPreference.getPhonenumber();
        if (minno != null && minno.length() > 8) {
            String[] phone = new String[2];
            phone[0] = minno.substring(minno.length() - 4, minno.length());
            phone[1] = minno.substring(minno.length() - 8, minno.length() - 4);

            Logview.Logwrite(THIS_TAG, "Phone num " + minno + " / m : " + phone[1] + " / l : " + phone[0]);

            for (String temp : phone) {
                find = pw.indexOf(temp);
                if (find > -1)
                {
                    Toast.makeText(getBaseContext(), "비밀번호에 전화번호가 포함되어 있습니다.", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }

        // 아이디와 동일 문자가 연속해서 3자리 이상 포함
        int idTemp0, idTemp1, idTemp2;
        if (mUserID != null && mUserID.length() > 3) {
            for (int i = 0; i < pw.length() - 2; i++) {
                temp0 = pw.charAt(i);
                temp1 = pw.charAt(i + 1);
                temp2 = pw.charAt(i + 2);
                for (int j=0; j<mUserID.length() - 2; j++)
                {
                    idTemp0 = mUserID.charAt(j);
                    idTemp1 = mUserID.charAt(j+1);
                    idTemp2 = mUserID.charAt(j+2);

                    if (temp0 == idTemp0 && temp1 == idTemp1 && temp2 == idTemp2)
                    {
                        Toast.makeText(getBaseContext(), "아이디와 연속한 3자리 이상 일치하는 비밀번호는 사용할 수 없습니다.", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
            }
        }
        return bret;
    }

    /**
     * 사용자 정보 표시
     */
    private void doViewUserInfo ()
    {
        if (mMemberInfo == null) return;

        // 이름
        TextView tv = (TextView)findViewById(R.id.txtUserName);
        tv.setText(mMemberInfo.getName());

        // 성별
        String sex = mMemberInfo.getSex().trim().toUpperCase();
        if (sex != null)
        {
            RadioButton radioButton;
            if (sex.equalsIgnoreCase("M"))
            {
                radioButton = (RadioButton)findViewById(R.id.radioButtonM);
            } else {
                radioButton = (RadioButton)findViewById(R.id.radioButtonW);
            }
            radioButton.setChecked(true);
        }

        // 휴대폰
        tv = (TextView)findViewById(R.id.txtUserPhone);
        tv.setText(Util.makeTelNumber(mMemberInfo.getPhoneNumber()));

        // 아이디
        tv = (TextView)findViewById(R.id.txtUserID);
        tv.setText(mMemberInfo.getId());
        mUserID = mMemberInfo.getId();

        // 이메일
        BootstrapEditText ed = (BootstrapEditText)findViewById(R.id.editUserEMail);
        ed.setText(mMemberInfo.getEmail());

        // 생년월일
        tv = (TextView)findViewById(R.id.txtUserBirthday);
        tv.setText(mMemberInfo.getBirthDay());

        // 우편번호
        tv = (TextView)findViewById(R.id.txtZipcode);
        tv.setText(mMemberInfo.getZipcode());

        // 주소
        String addr = mMemberInfo.getRoadAddr().trim();
        if (addr == null || addr.length() < 1)
            addr = mMemberInfo.getAddr1();
        tv = (TextView)findViewById(R.id.txtUserAddress);
        tv.setText(addr);

        // 상세
        ed = (BootstrapEditText)findViewById(R.id.editUserAddrDetail);
        ed.setText(mMemberInfo.getAddr2());

        // 휠체어
        String wheel = mMemberInfo.getWheelYN().trim().toUpperCase();
        if (wheel != null)
        {
            RadioButton radioButton;
            if (wheel.equalsIgnoreCase("A"))
                radioButton = (RadioButton)findViewById(R.id.radioButton1);
            else if (wheel.equalsIgnoreCase("M"))
                radioButton = (RadioButton)findViewById(R.id.radioButton2);
            else
                radioButton = (RadioButton)findViewById(R.id.radioButton3);

            radioButton.setChecked(true);
        }

        // 추가연락처
        ed = (BootstrapEditText)findViewById(R.id.editSubPhoneNum);
        ed.setText(Util.makeTelNumber(mMemberInfo.getAddPhoneNumber()));

        // 보호자
        ed = (BootstrapEditText)findViewById(R.id.editGuradianName);
        ed.setText(mMemberInfo.getGuadianName());

        // 보호자 전화
        ed = (BootstrapEditText)findViewById(R.id.editGuardianPhoneNum);
        ed.setText(Util.makeTelNumber(mMemberInfo.getGuadianPhone()));

        // 의사소통여부
        String communication = mMemberInfo.getCommunication();
        if (communication != null)
        {
            RadioButton radioButton;
            if (communication.equalsIgnoreCase("Y"))
                radioButton = (RadioButton)findViewById(R.id.radioTalk1);
            else
                radioButton = (RadioButton)findViewById(R.id.radioTalk2);

            radioButton.setChecked(true);
        }

        // 도움여부
        String helpType = mMemberInfo.getHelptype();
        if (helpType != null)
        {
            RadioButton radioButton;
            if (helpType.equalsIgnoreCase("N"))
                radioButton = (RadioButton)findViewById(R.id.radioHelp1);
            else if (helpType.equalsIgnoreCase("I"))
                radioButton = (RadioButton)findViewById(R.id.radioHelp2);
            else
                radioButton = (RadioButton)findViewById(R.id.radioHelp3);

            radioButton.setChecked(true);
        }

        // 보조인 여부
        String assistant = mMemberInfo.getAssistant();
        if (assistant != null)
        {
            RadioButton radioButton;
            if (assistant.equalsIgnoreCase("Y"))
                radioButton = (RadioButton)findViewById(R.id.radioGuardian1);
            else
                radioButton = (RadioButton)findViewById(R.id.radioGuardian2);

            radioButton.setChecked(true);
        }
    }

    /**
     * 시용자 정보 저장
     */
    private void doUserInfoSave ()
    {
        // 이메일
        BootstrapEditText editText = (BootstrapEditText)findViewById(R.id.editUserEMail);
        String email =editText.getText().toString().trim();


        // 비밀번호
        String pw = mUserPW.getText().toString().trim();

        if (pw != null && pw.length() > 1)
        {
            // 패스워드 유효성 검사
            if (!doPasswordValidationCheck(pw))
                return;

            if (((boolean)mReUserPW.getTag()) == false)
            {
                Toast.makeText(getBaseContext(), "비밀번호 확인을 하십시요.", Toast.LENGTH_LONG).show();
                return;
            }
        }

        // 주소
        editText = (BootstrapEditText)findViewById(R.id.editUserAddrDetail);
        String detail = editText.getText().toString().trim();

        // 휠체어
        String wheel = "N";
        RadioButton radioButton = (RadioButton)findViewById(R.id.radioButton1);
        if (radioButton.isChecked())
            wheel = "A";
        radioButton = (RadioButton)findViewById(R.id.radioButton2);
        if (radioButton.isChecked())
            wheel = "M";
        radioButton = (RadioButton)findViewById(R.id.radioButton3);
        if (radioButton.isChecked())
            wheel = "N";

        // 추가 연락처
        editText = (BootstrapEditText)findViewById(R.id.editSubPhoneNum);
        String addPhoneNumber = editText.getText().toString().trim();
        if (addPhoneNumber != null && addPhoneNumber.length() > 0) {
            if (Util.isValidCellPhoneNumber(addPhoneNumber) == false) {
                Toast.makeText(getBaseContext(), "추가연락처가 잘못입력되었습니다.", Toast.LENGTH_LONG).show();
                return;
            }
        }
        String temp = addPhoneNumber.replace("-", "");
        addPhoneNumber = temp;

        // 보호자
        editText = (BootstrapEditText)findViewById(R.id.editGuradianName);
        String gadianName = editText.getText().toString().trim();

        // 보호자 전화
        editText = (BootstrapEditText)findViewById(R.id.editGuardianPhoneNum);
        String gadianPhone = editText.getText().toString().trim();
        if (gadianPhone != null && gadianPhone.length() > 0) {
            if (Util.isValidCellPhoneNumber(gadianPhone) == false) {
                Toast.makeText(getBaseContext(), "보호자 연락처가 잘못입력되었습니다.", Toast.LENGTH_LONG).show();
                return;
            }
        }
        temp = gadianPhone.replace("-", "");
        gadianPhone = temp;

        // 의사소통여부
        String communication = "Y";
        radioButton = (RadioButton)findViewById(R.id.radioTalk1);
        if (radioButton.isChecked())
            communication = "Y";
        radioButton = (RadioButton)findViewById(R.id.radioTalk2);
        if (radioButton.isChecked())
            communication = "N";

        // 도움여부
        String help = "N";
        radioButton = (RadioButton)findViewById(R.id.radioHelp1);
        if (radioButton.isChecked())
            help = "N";
        radioButton = (RadioButton)findViewById(R.id.radioHelp2);
        if (radioButton.isChecked())
            help = "I";
        radioButton = (RadioButton)findViewById(R.id.radioHelp3);
        if (radioButton.isChecked())
            help = "Y";

        // 보조인여부
        String assistant = "Y";
        radioButton = (RadioButton)findViewById(R.id.radioGuardian1);
        if (radioButton.isChecked())
            assistant = "Y";
        radioButton = (RadioButton)findViewById(R.id.radioGuardian2);
        if (radioButton.isChecked())
            assistant = "N";

        startProgress();

        // 호출
        ConfigPreference configPreference = ConfigPreference.getInstance();
        configPreference.setPreference(getBaseContext());

        JSON_REQChgMemberInfo sendData = new JSON_REQChgMemberInfo(configPreference.getAuthkey());

        sendData.setUserEmail(email);
        sendData.setWheelChiarYN(wheel);
        sendData.setAddPhone(addPhoneNumber);
        sendData.setGuardian(gadianName, gadianPhone);
        sendData.setCommunication (communication);
        sendData.setHelpType(help);
        sendData.setAssistant(assistant);

        //20170814 송명진 - 비밀번호 입력양식이 규칙에 맞지 않거나 수정하지 않았을 때 기존의 pw로 입력되어 저장
        mReUserPW.getText().toString().trim();

        if (pw != null && pw.length() > 1)
            sendData.setUserPW(getSHAEncryption(pw));
//        else
//            sendData.setUserPW(mReUserPW.getText().toString().trim());


        if (mZipcodeAddress != null)
        {
            sendData.setZipcode(mZipcodeAddress.zipcode);
            sendData.setSido(mZipcodeAddress.sido);
            sendData.setSigugun(mZipcodeAddress.sigungunA, mZipcodeAddress.sigungunB);
            sendData.setDong(mZipcodeAddress.dong1, mZipcodeAddress.dong2);
            sendData.setAddr(mZipcodeAddress.addr1, detail);
            sendData.setRoadAddr(mZipcodeAddress.roadAddress + " " + detail);
        } else {
            sendData.setZipcode(mMemberInfo.getZipcode());
            sendData.setSido(mMemberInfo.getSido());
            sendData.setSigugun(mMemberInfo.getSiguGunA(), mMemberInfo.getSiguGunB());
            sendData.setDong(mMemberInfo.getDong1(), mMemberInfo.getDong2());
            sendData.setAddr(mMemberInfo.getAddr1(), detail);
            sendData.setRoadAddr(mMemberInfo.getRoadAddr() + " " + detail);
        }

        RetrofitProcessManager.doRetrofitRequest(JSON_REQChgMemberInfo.PAGE_NAME, sendData.getParams());
    }

    /**
     * 사용자 정보 요청
     */
    private void doGetUserInfo()
    {
        startProgress();

        new Thread(new Runnable() {
            @Override
            public void run() {
                ConfigPreference configPreference = ConfigPreference.getInstance();
                configPreference.setPreference(getBaseContext());

                JSON_REQMemberInfo login = new JSON_REQMemberInfo(configPreference.getAuthkey(), configPreference.getPushkey());
                RetrofitProcessManager.doRetrofitRequest(JSON_REQMemberInfo.PAGE_NAME, login.getParams());
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
                if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.MemberInfo.name().toLowerCase())) {
                    receiveMemberInfo(event);
                }
                else if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.ChgMemberInfo.name().toLowerCase())) {
                    receiveMemberInfoSave(event);
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
     * 사용자 정보 수신
     * @param event
     */
    private void receiveMemberInfo (JSONMessageEvent event)
    {
        Logview.Logwrite(THIS_TAG, "receiveMemberInfo");
        Gson gson = new Gson();

        REP_JSONMemberInfo json = gson.fromJson(event.getMessageBody(), REP_JSONMemberInfo.class);

        if (json.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success)))
        {
            Logview.Logwrite(THIS_TAG, "receiveMemberInfo success");
        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receiveMemberInfo false : " + json.getBody().getCause());
            Toast.makeText(getBaseContext(),
                    ErrorcodeToString.getError(json.getBody().getResult(), json.getBody().getCause()),
                    Toast.LENGTH_LONG).show();
            handler.sendEmptyMessage(MSG_HTTP_FAILED);
            return;
        }

        mMemberInfo = json.getBody();
        handler.sendEmptyMessage(MSG_READ_SUCCESS);
    }

    /**
     * 사용자 정보 변경 저장 수신
     * @param event
     */
    private void receiveMemberInfoSave (JSONMessageEvent event)
    {
        Logview.Logwrite(THIS_TAG, "receiveMemberInfoSave");
        Gson gson = new Gson();

        REP_JSONMemberInfo json = gson.fromJson(event.getMessageBody(), REP_JSONMemberInfo.class);

        if (json.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success)))
        {
            Logview.Logwrite(THIS_TAG, "receiveMemberInfoSave success");
        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receiveMemberInfoSave false : " + json.getBody().getCause());
            Toast.makeText(getBaseContext(),
                    ErrorcodeToString.getError(json.getBody().getResult(), json.getBody().getCause()),
                    Toast.LENGTH_LONG).show();
            handler.sendEmptyMessage(MSG_HTTP_FAILED);
            return;
        }

        handler.sendEmptyMessage(MSG_SAVE_SUEECSS);
    }

    /**
     * Dialog message view
     * //@param context
     * @param title
     * @param msg
     */
    public void doAlertDialogView (String title, String msg)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(MyInfoActivity.this);
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


    //////////////////////////////////////////////////////////////////////////////////////
    // Progress 원형
    //
    private ProgressDialog mProgressDialog = null;
    private synchronized void startProgress()
    {
        stopProgress();

        mProgressDialog = ProgressDialog.show(MyInfoActivity.this, "", "잠시 기다려주세요", true);
    }

    private synchronized void stopProgress()
    {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
        mProgressDialog = null;
    }

}
