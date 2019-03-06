package kr.or.hsnarae.transporthelp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.or.hsnarae.transporthelp.common.FontAwesome.CFontAwesomeManager;
import kr.or.hsnarae.transporthelp.common.db.DBControlManager;
import kr.or.hsnarae.transporthelp.common.db.DBSchema;
import kr.or.hsnarae.transporthelp.common.db.SelectHelper;
import kr.or.hsnarae.transporthelp.common.util.BusEventProvider;
import kr.or.hsnarae.transporthelp.common.util.DatePickerDailog;
import kr.or.hsnarae.transporthelp.common.util.ErrorcodeToString;
import kr.or.hsnarae.transporthelp.common.util.PermissionRequest;
import kr.or.hsnarae.transporthelp.impl.net.json.IJSONPageMethod;
import kr.or.hsnarae.transporthelp.impl.net.json.JSONMessageEvent;
import kr.or.hsnarae.transporthelp.impl.net.json.RetrofitProcessManager;
import kr.or.hsnarae.transporthelp.impl.net.json.rep.REP_JSONIDCheck;
import kr.or.hsnarae.transporthelp.impl.net.json.rep.REP_JSONRegMember;
import kr.or.hsnarae.transporthelp.impl.net.json.rep.REP_JSONZipcode;
import kr.or.hsnarae.transporthelp.impl.net.json.req.JSON_REQIDCheck;
import kr.or.hsnarae.transporthelp.impl.net.json.req.JSON_REQRegistry;
import kr.or.hsnarae.transporthelp.impl.preference.ConfigPreference;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class RegistryActivity extends DefaultActivity implements View.OnClickListener
{
    private final String THIS_TAG = "RegistryActivity";

    private final int MSG_INIT = 0;
    private final int MSG_DATE_PICKER = 1;
    private final int MSG_ALERT_OK = 100;
    private final int MSG_HTTP_FAILED = 101;
    private final int MSG_INIT_SUCCESS = 102;
    private final int MSG_CAMERA_FINISHED = 200;
    private final int MSG_SELECT_IMAGE = 201;
    private final int MSG_ZIPCODE_FIND = 202;
    private final int MSG_USER_REGISTRY_SUCCESS=301;
    private final int MSG_USER_REGISTRY_FAILED=302;
    private final int MSG_USER_REGISTRY_SUCCESS_VIEW=303;
    private final int MSG_DIAROG_OK= 400;

    private ArrayList<ITEM> mHanditypeList = null;      // 장애유형
    private ArrayList<ITEM> mHandiDegreeList = null;    // 장애등급

    private int mSelectHaditype = 0, mSelecthadiDegree=0;
    private Calendar dateandtime;
    private int mYear=0, mMonth=0, mDay=0;
    private BootstrapEditText mUserID, mUserPW, mReUserPW;
    private PhotoView mPhotoView;
    private String imagePath;
    private String mImageData;
    private REP_JSONZipcode.Address mZipcodeAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_registry);

        // 기본 설정
        TextView title = (TextView)findViewById(R.id.txtTitle);
        title.setText("회원가입");

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
        bstBTNCenter.setText("회원가입하기");

        //////////////////////////////////////////////////////////////
        AwesomeTextView btnCalendar = (AwesomeTextView)findViewById(R.id.btnBirthCalendar);
        btnCalendar.setOnClickListener(this);
        BootstrapButton btn = (BootstrapButton)findViewById(R.id.btnZipcode);
        btn.setOnClickListener(this);
        btn = (BootstrapButton)findViewById(R.id.btnIDDuplicate);
        btn.setOnClickListener(this);
        btn = (BootstrapButton)findViewById(R.id.btnCamera);
        btn.setOnClickListener(this);
        btn = (BootstrapButton)findViewById(R.id.btnInsertFile);
        btn.setOnClickListener(this);

        mYear=0; mMonth=0; mDay=0;
        mUserID = (BootstrapEditText)findViewById(R.id.editUserID);
        mUserID.setTag(false);
        mUserID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 입력하기 전에
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // 입력되는 텍스트에 변화가 있을 때
                mUserID.setTag(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 입력이 끝났을 때
            }
        });

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

        mPhotoView = (PhotoView)findViewById(R.id.imgView);
        mPhotoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                Logview.Logwrite(THIS_TAG, "image tap...");
            }
        });

        mImageData = "";

        doInitView();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view)
    {
        Intent intent;

        switch (view.getId())
        {
            case R.id.btnCenter:        // 회원가입
                doRegistry();
                break;
            case R.id.btnBirthCalendar: // 생년월일 달력
                doDatepicker ();
                break;
            case R.id.btnZipcode:       // 우편번호 찾기
                intent = new Intent(RegistryActivity.this, ZipcodeActivity.class);
                startActivityForResult(intent, ZipcodeActivity.ZIPCODE_RESULT_CODE);
                break;
            case R.id.btnIDDuplicate:   // 아이디 중복확인
                doUserIDDuplicate();
                break;
            case R.id.btnCamera:        // 사진 촬영
                doAlertMessageDialogView ("알림", "주민등록번호 뒷번호를 가린 후 사진촬영을 하십시요.");
                //doCamera();
                break;
            case R.id.btnInsertFile:    // 파일첨부
                intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, MSG_SELECT_IMAGE);
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
                case MSG_DATE_PICKER:
                    String date = String.format(Locale.KOREA, "%d년 %02d월 %02d일", mYear, mMonth, mDay);
                    TextView tv = (TextView)findViewById(R.id.txtUserBirthday);
                    tv.setText(date);
                    break;
                case MSG_ZIPCODE_FIND:
                    setZipcodView ((String)msg.obj);
                    break;
                case MSG_USER_REGISTRY_SUCCESS:
                    //Login 이동
                    Intent intent = new Intent(RegistryActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case MSG_USER_REGISTRY_FAILED:
                    String log = (String)msg.obj;
                    doAlertReturnDialogView("알림", log);
                    break;
                case MSG_USER_REGISTRY_SUCCESS_VIEW:
                    doAlertReturnDialogView("알림", "회원가입이 완료되었습니다.");
                    break;

            }
        }
    };

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
     *  초기 데이터 설정
     */
    private void doDataInit ()
    {
        // 장애유형
        String sql = "SELECT * FROM " + DBSchema.TABLE_NAME_HANDITYPE + ";";
        SelectHelper handiTypeModelsList = DBControlManager.dbSelect(sql);

        if (handiTypeModelsList != null && handiTypeModelsList.getCount() > 0)
        {
            if (mHanditypeList != null)
                mHanditypeList.clear();
            mHanditypeList = null;
            mHanditypeList = new ArrayList<ITEM>();

            ITEM data;
            handiTypeModelsList.moveFirst();
            do {
                data = new ITEM(handiTypeModelsList.getValue(DBSchema.COL_NAME), handiTypeModelsList.getValue(DBSchema.COL_CODE));
                mHanditypeList.add(data);
            }while (handiTypeModelsList.moveNext());
        }

        // 장애등급
        sql = "SELECT * FROM " + DBSchema.TABLE_NAME_HANDIDEGREE + ";";
        SelectHelper degreeModelses = DBControlManager.dbSelect(sql);

        if (degreeModelses != null && degreeModelses.getCount() > 0)
        {
            if (mHandiDegreeList != null)
                mHandiDegreeList.clear();
            mHandiDegreeList = null;
            mHandiDegreeList = new ArrayList<ITEM>();

            ITEM data;
            degreeModelses.moveFirst();
            do {
                data = new ITEM(degreeModelses.getValue(DBSchema.COL_NAME), degreeModelses.getValue(DBSchema.COL_CODE));
                mHandiDegreeList.add(data);
            }while (degreeModelses.moveNext());
        }

    }

    /**
     * 초기 데이터 설정
     */
    private void doInitView ()
    {
        doDataInit ();

        Spinner spinnerType = (Spinner)findViewById(R.id.spinHandiType);
        ArrayList<String> type = new ArrayList<String>();
        if (mHanditypeList != null && mHanditypeList.size() > 0)
        {
            for (ITEM item : mHanditypeList)
                type.add(item.getName());
        }

        ArrayAdapter adapterType = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, type);
        adapterType.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterType);
        spinnerType.setSelection(0);
        mSelectHaditype = 0;
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectHaditype = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        Spinner spinnerDegree = (Spinner)findViewById(R.id.spinHandiDegree);
        ArrayList<String> degree = new ArrayList<String>();
        if (mHandiDegreeList != null && mHandiDegreeList.size() > 0)
        {
            for (ITEM item : mHandiDegreeList)
                degree.add(item.getName());
        }

        ArrayAdapter adapterDegree = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, degree);
        adapterDegree.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        spinnerDegree.setAdapter(adapterDegree);
        spinnerDegree.setSelection(0);
        mSelecthadiDegree = 0;
        spinnerDegree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelecthadiDegree = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * 생년월일 선택
     */
    private void doDatepicker()
    {
        dateandtime = Calendar.getInstance(Locale.KOREA);
        DatePickerDailog dp = new DatePickerDailog(RegistryActivity.this,
                dateandtime, new DatePickerDailog.DatePickerListner() {

            @Override
            public void OnDoneButton(Dialog datedialog, Calendar c) {
                datedialog.dismiss();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH) + 1;
                mDay = c.get(Calendar.DAY_OF_MONTH);

                Logview.Logwrite(THIS_TAG, "Y : " + mYear + " / M : " + mMonth + " / D : " + mDay);
                handler.sendEmptyMessage(MSG_DATE_PICKER);
            }

            @Override
            public void OnCancelButton(Dialog datedialog) {
                datedialog.dismiss();
            }
        });
        dp.show();
    }

    /**
     * 아이디 중복확인
      */
    private void doUserIDDuplicate ()
    {
        String userid = mUserID.getText().toString().trim();
        if (userid == null || userid.length() < 4)
        {
            Toast.makeText(getBaseContext(), "아이디는 4자 이상입니다.", Toast.LENGTH_LONG).show();
            return;
        }

        startProgress();
        // 중복확인
        new Thread(new Runnable() {
            @Override
            public void run() {
                String userid = mUserID.getText().toString().trim();
                JSON_REQIDCheck sendData = new JSON_REQIDCheck(userid);
                RetrofitProcessManager.doRetrofitRequest(JSON_REQIDCheck.PAGE_NAME, sendData.getParams());
            }
        }).start();
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
                        "영문은 대소분자를 구분합니다.\n", Toast.LENGTH_LONG).show();
                return false;
            }
        } else {
            Toast.makeText(getBaseContext(), "비밀번호는 영문, 숫자, 특수문자(!,@,$,%,^,&,*,?,_,~ 만 사용)를 사용하여 9자이상 입니다." +
                    "영문은 대소분자를 구분합니다.\n", Toast.LENGTH_LONG).show();
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
        String id = mUserID.getText().toString().trim();
        int idTemp0, idTemp1, idTemp2;
        if (id != null && id.length() > 3) {
            for (int i = 0; i < pw.length() - 2; i++) {
                temp0 = pw.charAt(i);
                temp1 = pw.charAt(i + 1);
                temp2 = pw.charAt(i + 2);
                for (int j=0; j<id.length() - 2; j++)
                {
                    idTemp0 = id.charAt(j);
                    idTemp1 = id.charAt(j+1);
                    idTemp2 = id.charAt(j+2);

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
     * 카메라 호출
     */
    private void doCamera()
    {
        // Camera Application이 있으면
        if (isExistCameraApplication())
        {
            // Camera Application을 실행한다.
            Intent cameraApp = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // 찍은 사진을 보관할 파일 객체를 만들어서 보낸다.
            File picture = savePictureFile();
            if (picture != null)
            {
                Uri contentUri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    contentUri = FileProvider.getUriForFile(RegistryActivity.this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            picture);
                } else {
                    contentUri = Uri.fromFile( picture );
                }

                cameraApp.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                startActivityForResult(cameraApp, MSG_CAMERA_FINISHED);
            }
        } else {
            Toast.makeText(RegistryActivity.this, "카메라 앱을 설치하세요.", Toast.LENGTH_SHORT).show();
        }

//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, MSG_CAMERA_FINISHED);
    }

    /** * Android에 Camera Application이 설치되어있는지 확인한다.
     * * * @return 카메라 앱이 있으면 true, 없으면 false
     * */
    private boolean isExistCameraApplication()
    {
        // Android의 모든 Application을 얻어온다.
        PackageManager packageManager = getPackageManager();
        // Camera Application
        Intent cameraApp = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // MediaStore.ACTION_IMAGE_CAPTURE을 처리할 수 있는 App 정보를 가져온다.
        List cameraApps = packageManager.queryIntentActivities( cameraApp, PackageManager.MATCH_DEFAULT_ONLY);
        // 카메라 App이 적어도 한개 이상 있는지 리턴

        return cameraApps.size() > 0;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            if (requestCode == MSG_CAMERA_FINISHED)
            {
                try {
                    if (imagePath != null && imagePath.length() > 0) {
                        BitmapFactory.Options factory = new BitmapFactory.Options();
                        factory.inJustDecodeBounds = false;
                        //factory.inPurgeable = true;
                        factory.inSampleSize =4;
                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, factory);

                        mPhotoView.setImageBitmap(bitmap);
                    }
                }
                catch(Exception e){
                    return;
                }
            }
            else if (requestCode == MSG_SELECT_IMAGE)
            {
                try {
                    imagePath = getImagePath(data.getData());
                    final Bitmap image_bitmap 	= MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                    mPhotoView.setImageBitmap (image_bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (requestCode == ZipcodeActivity.ZIPCODE_RESULT_CODE)
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

    private String getImagePath(Uri uri)
    {
        String[] proj = { MediaStore.Images.Media.DATA };

        CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);    }

    /**
     * 회원등록
     */
    private void doRegistry ()
    {
        // 이름
        BootstrapEditText editText = (BootstrapEditText)findViewById(R.id.editUserName);
        String name = editText.getText().toString().trim();
        if (name == null || name.length() < 1)
        {
            Toast.makeText(getBaseContext(), "이름을 입력하십시요.", Toast.LENGTH_LONG).show();
            return;
        }

        // 성별
        boolean bMan = true;
        RadioButton radioButton = (RadioButton)findViewById(R.id.radioButtonM);
        if (radioButton.isChecked() == false) bMan = false;
        if (!bMan)
        {
            radioButton = (RadioButton)findViewById(R.id.radioButtonW);
            if (radioButton.isChecked() == false && bMan == false)
            {
                Toast.makeText(getBaseContext(), "성별을 선택하십시요.", Toast.LENGTH_LONG).show();
                return;
            } else {
                bMan = true;
            }
        }

        // 이메일
        editText = (BootstrapEditText)findViewById(R.id.editUserEMail);
        String email = editText.getText().toString().trim();

        // 생년월일
        if (mYear == 0)
        {
            Toast.makeText(getBaseContext(), "생년월일을 입력하십시요.", Toast.LENGTH_LONG).show();
            return;
        }
        String birth = String.format("%d-%2d-%2d", mYear, mMonth, mDay);

        // 우편번호
        TextView tv = (TextView)findViewById(R.id.txtZipcode);
        String zipcode = tv.getText().toString().trim();
        if (zipcode == null || zipcode.length() < 1)
        {
            Toast.makeText(getBaseContext(), "주소을 입력하십시요.", Toast.LENGTH_LONG).show();
            return;
        }

        // 기본 주소
        tv = (TextView)findViewById(R.id.txtUserAddress);
        String address = tv.getText().toString().trim();

        // 상세 주소
        BootstrapEditText detail = (BootstrapEditText)findViewById(R.id.editUserAddrDetail);
        String detailAddress = detail.getText().toString().trim();

        // 아이디
        String id = mUserID.getText().toString().trim();
        if (id == null || id.length() < 1)
        {
            Toast.makeText(getBaseContext(), "아이디를 입력하십시요.", Toast.LENGTH_LONG).show();
            return;
        }

        if (((boolean)mUserID.getTag()) == false)
        {
            Toast.makeText(getBaseContext(), "아이디 중복확인을 하십시요.", Toast.LENGTH_LONG).show();
            return;
        }

        // 비밀번호
        String pw = mUserPW.getText().toString().trim();

        if (pw == null ||pw.length() < 1)
        {
            Toast.makeText(getBaseContext(), "비밀번호를 입력하십시요.", Toast.LENGTH_LONG).show();
            return;
        }

        // 패스워드 유효성 검사
        if (!doPasswordValidationCheck(pw))
            return;

        if (((boolean)mReUserPW.getTag()) == false)
        {
            Toast.makeText(getBaseContext(), "비빌번호 확인을 하십시요.", Toast.LENGTH_LONG).show();
            return;
        }

        // 휠체어 사용
        String wheel = "N";
        radioButton = (RadioButton)findViewById(R.id.radioButton1);
        if (radioButton.isChecked())
            wheel = "A";
        radioButton = (RadioButton)findViewById(R.id.radioButton2);
        if (radioButton.isChecked())
            wheel = "M";
        radioButton = (RadioButton)findViewById(R.id.radioButton3);
        if (radioButton.isChecked())
            wheel = "N";

        // 추가연락처
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

        // 보호자 이름
        editText = (BootstrapEditText)findViewById(R.id.editGuradianName);
        String gadianName = editText.getText().toString().trim();

        // 보호자 연락처
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
            wheel = "Y";
        radioButton = (RadioButton)findViewById(R.id.radioTalk2);
        if (radioButton.isChecked())
            wheel = "N";

        // 도움여부
        String help = "N";
        radioButton = (RadioButton)findViewById(R.id.radioHelp1);
        if (radioButton.isChecked())
            wheel = "N";
        radioButton = (RadioButton)findViewById(R.id.radioHelp2);
        if (radioButton.isChecked())
            wheel = "I";
        radioButton = (RadioButton)findViewById(R.id.radioHelp3);
        if (radioButton.isChecked())
            wheel = "Y";

        // 보조인여부
        String assistant = "Y";
        radioButton = (RadioButton)findViewById(R.id.radioGuardian1);
        if (radioButton.isChecked())
            wheel = "Y";
        radioButton = (RadioButton)findViewById(R.id.radioGuardian2);
        if (radioButton.isChecked())
            wheel = "N";

        // 복지사본
        if (imagePath == null || imagePath.length() < 1)
        {
            Toast.makeText(getBaseContext(), "복지사본이 첨부되지 않았습니다.", Toast.LENGTH_LONG).show();
            return;
        }

        // 호출
        startProgress();

        ConfigPreference configPreference = ConfigPreference.getInstance();
        configPreference.setPreference(getBaseContext());

        HSNaraeApplication app = (HSNaraeApplication)getApplication();
        String minno = "";
        if (app != null)
            minno = app.doPhoneNumber();

        JSON_REQRegistry sendData = new JSON_REQRegistry(minno, configPreference.getAuthkey());
        sendData.setName(name);
        sendData.setEmail(email);
        sendData.setSex((bMan?"M" : "F"));
        sendData.setBirth(birth);
        sendData.setAddress(mZipcodeAddress, detailAddress);
        sendData.setID(id);
        sendData.setPW(getSHAEncryption(pw));
        sendData.setHandiTypeCode(mHanditypeList.get(mSelectHaditype).getCode());
        sendData.setHandiDegreeCode(mHandiDegreeList.get(mSelecthadiDegree).getCode());
        sendData.setWheelchaiar(wheel);
        sendData.setAddPhoneNumber(addPhoneNumber);
        sendData.setGuardianName(gadianName);
        sendData.setGuardianPhone(gadianPhone);
        sendData.setCommunication (communication);
        sendData.setHelpType(help);
        sendData.setAssistant(assistant);

        RetrofitProcessManager.doUploadFile (sendData.getParams(), imagePath);
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
        stopProgress();

        Logview.Logwrite(THIS_TAG, "jsonMessageEvent 수신 상태 : " + event.getMessageStatus());
        if (event.getMessageStatus()) {
            String pageName = event.getPageName();
            Logview.Logwrite(THIS_TAG, "jsonMessageEvent page : " + pageName);

            if (pageName != null) {
                if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.IdCheck.name().toLowerCase())) {
                    receiveUserIDCheck(event);
                }
                else if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.RegMember.name().toLowerCase())) {
                    receiveUserRegistry(event);
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
     * 사용자 아이디 중복 확인
     * @param event
     */
    private void receiveUserIDCheck (JSONMessageEvent event)
    {
        Gson gson = new Gson();

        REP_JSONIDCheck json = gson.fromJson(event.getMessageBody(), REP_JSONIDCheck.class);

        if (json.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success)) == false)
        {
            Logview.Logwrite(THIS_TAG, "receiveUserIDCheck false : " + json.getBody().getCause());
            Toast.makeText(RegistryActivity.this,
                    ErrorcodeToString.getError(json.getBody().getResult(), json.getBody().getCause()),
                    Toast.LENGTH_LONG).show();
            handler.sendEmptyMessage(MSG_HTTP_FAILED);
            return;
        }

        String existYN = json.getBody().getExistYN();
        if (existYN != null && existYN.toUpperCase().trim().equalsIgnoreCase("N")) {
            doAlertDialogView(RegistryActivity.this, "알림", "사용가능한 아이디 입니다.");
            mUserID.setTag(true);
        } else {
            doAlertDialogView(RegistryActivity.this, "알림", "이미 사용자가 있습니다.");
            mUserID.setTag(false);
        }
    }

    /**
     * 사용자 등록
     * @param event
     */
    private void receiveUserRegistry (JSONMessageEvent event)
    {
        Gson gson = new Gson();

        REP_JSONRegMember json = gson.fromJson(event.getMessageBody(), REP_JSONRegMember.class);

        if (json.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success)) == false)
        {
            Logview.Logwrite(THIS_TAG, "receiveUserRegistry false : " + json.getBody().getCause());
            handler.obtainMessage(MSG_USER_REGISTRY_FAILED,
                    ErrorcodeToString.getError(json.getBody().getResult(), json.getBody().getCause())).sendToTarget();
            return;
        }

        // 정상적으로 등록이 됨.
        ConfigPreference configPreference = ConfigPreference.getInstance();
        configPreference.setPreference(getBaseContext());
        configPreference.setUserId(mUserID.getText().toString().trim());

        handler.sendEmptyMessage(MSG_USER_REGISTRY_SUCCESS_VIEW);
    }

    /**  카메라에서 찍은 사진을 외부 저장소에 저장한다.
     * *  @return
     */
    private File savePictureFile()
    {
        // 외부 저장소 쓰기 권한을 얻어온다.
        PermissionRequest.Builder requester = new PermissionRequest.Builder(this);
        int result = requester
                .create()
                .request( Manifest.permission.WRITE_EXTERNAL_STORAGE, 20000,
                        new PermissionRequest.OnClickDenyButtonListener()
                        {
                            @Override
                            public void onClick(Activity activity) { }
                        });

        // 사용자가 권한을 수락한 경우
        if (result == PermissionRequest.ALREADY_GRANTED || result == PermissionRequest.REQUEST_PERMISSION)
        {
            // 사진 파일의 이름을 만든다.
            // Date는 java.util 을 Import 한다.
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                    .format(new Date());
            String fileName = "IMG_" + timestamp;
            /** * 사진파일이 저장될 장소를 구한다.
             * * 외장메모리에서 사진을 저장하는 폴더를 찾아서
             * * 그곳에 MYAPP 이라는 폴더를 만든다.
             * */
            File pictureStorage = new File( Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES), "MYAPP/");
            // 만약 장소가 존재하지 않는다면 폴더를 새롭게 만든다.
            if (!pictureStorage.exists())
            {
                /** * mkdir은 폴더를 하나만 만들고,
                 * * mkdirs는 경로상에 존재하는 모든 폴더를 만들어준다.
                 * */
                pictureStorage.mkdirs();
            }
            try {
                File file = File.createTempFile(fileName, ".jpg", pictureStorage);
                // ImageView에 보여주기위해 사진파일의 절대 경로를 얻어온다.
                imagePath = file.getAbsolutePath();
                // 찍힌 사진을 "갤러리" 앱에 추가한다.
                Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE );
                File f = new File( imagePath );

                Uri contentUri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    contentUri = FileProvider.getUriForFile(RegistryActivity.this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            f);
                } else {
                    contentUri = Uri.fromFile( f );
                }

                mediaScanIntent.setData( contentUri );
                this.sendBroadcast( mediaScanIntent );
                return file;
            } catch (IOException e) { e.printStackTrace(); }
        }
        // 사용자가 권한을 거부한 경우
        else { }

        return null;
    }

    private byte[] bitmapToByteArray( Bitmap bitmap )
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        return byteArray ;
    }

    private String bitmapToBase64 (Bitmap bitmap)
    {
        byte[] byteArray= bitmapToByteArray (bitmap);

        if (byteArray == null) return null;

        byte[] outdata = Base64.encode(byteArray, Base64.NO_WRAP);
        String base64 = new String (outdata);

        // BASE64 변환해서 전송시 문제 때문에 처리된 것
        String temp1 = base64.replace("+", "-");
        base64 = temp1.replace("/", "_");

        return base64;
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

    }

    /**
     * Dialog message view
     * //@param context
     * @param title
     * @param msg
     */
    public void doAlertReturnDialogView (String title, String msg)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(RegistryActivity.this);
        alert.setTitle(title);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
                handler.sendEmptyMessage(MSG_USER_REGISTRY_SUCCESS);
            }
        });
        alert.setMessage(msg);
        alert.show();

    }

    public void doAlertMessageDialogView (String title, String msg)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(RegistryActivity.this);
        alert.setTitle(title);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
                //handler.sendEmptyMessage(MSG_USER_REGISTRY_SUCCESS);
                doCamera();
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

        mProgressDialog = ProgressDialog.show(RegistryActivity.this, "", "잠시 기다려주세요", true);
    }

    private synchronized void stopProgress()
    {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
        mProgressDialog = null;
    }


}
