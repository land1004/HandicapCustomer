package kr.or.yongin.transporthelp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ionemax.iomlibrarys.log.Logview;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kr.or.yongin.transporthelp.common.FontAwesome.CFontAwesomeManager;
import kr.or.yongin.transporthelp.common.util.BusEventProvider;
import kr.or.yongin.transporthelp.common.util.ErrorcodeToString;
import kr.or.yongin.transporthelp.common.util.GlobalValues;
import kr.or.yongin.transporthelp.impl.net.json.IJSONPageMethod;
import kr.or.yongin.transporthelp.impl.net.json.JSONMessageEvent;
import kr.or.yongin.transporthelp.impl.net.json.RetrofitProcessManager;
import kr.or.yongin.transporthelp.impl.net.json.rep.REP_JSONCallHistory;
import kr.or.yongin.transporthelp.impl.net.json.rep.REP_JSONLogin;
import kr.or.yongin.transporthelp.impl.net.json.rep.REP_JSONReqCallCancel;
import kr.or.yongin.transporthelp.impl.net.json.req.JSON_REQCallHistory;
import kr.or.yongin.transporthelp.impl.net.json.req.JSON_REQReqCallCancel;
import kr.or.yongin.transporthelp.impl.net.json.req.JSON_REQUserInfo;
import kr.or.yongin.transporthelp.impl.preference.ConfigPreference;
import kr.or.yongin.transporthelp.impl.preference.OrderPreference;
import kr.or.yongin.transporthelp.impl.rowadaptors.ListHistoryRowAdaptor;
import kr.or.yongin.transporthelp.impl.rowadaptors.ListNoticeRowAdaptor;

public class CallHistoryActivity extends Activity
{
    private final String THIS_TAG = "CallHistoryActivity";

    private final int MSG_HTTP_FAILED = 0;

    private final int MSG_HISTORY_SUCCESS=10;
    private final int MSG_BOOKING_CANCEL = 20;
    private final int MSG_CANCEL_SUCCESS=21;
    private final int MSG_CALL_CANCEL = 22;
    private final int MSG_HISTORY_CALL = 23;


    private ConfigPreference mConfigPerference;
    private ArrayList<CallHistoryItem> mCallHistory;
    private ListView mHistoryList;
    private String mBookingCallid="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_history);

        mConfigPerference = ConfigPreference.getInstance();
        mConfigPerference.setPreference(getBaseContext());

        // 기본 설정
        TextView title = (TextView)findViewById(R.id.txtTitle);
        title.setText("나의 이용 내역");

        TextView btnBack = (TextView)findViewById(R.id.btnBack);
        btnBack.setTypeface(CFontAwesomeManager.getTypeface(this, CFontAwesomeManager.FONTAWESOME));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //////////////////////////////////////////////////////////////
        mCallHistory = new ArrayList<CallHistoryItem>();
        mHistoryList = (ListView)findViewById(R.id.list_history);

        doCallHistory ();
        //doHistoryListView ();
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
        if (mCallHistory != null)
            mCallHistory.clear();
        mCallHistory = null;

        super.onDestroy();
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
                case MSG_HISTORY_SUCCESS:
                    doHistoryListView ();
                    break;
                case MSG_BOOKING_CANCEL:
                    viewBookingCancelMessage (msg.arg1);
                    break;
                case MSG_CANCEL_SUCCESS:
                    doAlertDialogView2 ("알림", "예약배차가 취소되었습니다.\n 감사합니다.");
                    break;
                case MSG_CALL_CANCEL:
                    doBookingCancel ();
                    break;
                case MSG_HISTORY_CALL:
                    doCallHistory ();
                    break;
            }
        }
    };

    private View.OnClickListener onButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Logview.Logwrite(THIS_TAG, "Booking call cancel : " + view.getTag().toString());

            handler.obtainMessage(MSG_BOOKING_CANCEL, Integer.parseInt(view.getTag().toString()),0 ).sendToTarget();
        }
    };
    /**
     * 리스트를 표시한다.
     */
    private void doHistoryListView ()
    {
        if (mCallHistory == null || mCallHistory.size() < 1)
            return;

        ArrayList<HashMap<String, String>>list = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map;

        String groupName = "";
        String bookingtime="";
        boolean bBookingtime = false;
        for (CallHistoryItem item : mCallHistory)
        {
            map = new HashMap<String, String>();
            if (item.callStorageState != null)
            {
                if (item.callStorageState.equalsIgnoreCase(groupName) == false) {
                    groupName = item.callStorageState;
                    if (groupName.equalsIgnoreCase("B")) {
                        map.put("list_group_item", "예약배차");
                        bBookingtime = true;
                    }
                    else if (groupName.equalsIgnoreCase("I")) {
                        map.put("list_group_item", "진행배차");
                        bBookingtime = false;
                    }
                    else if (groupName.equalsIgnoreCase("L")) {
                        map.put("list_group_item", "배차이력");
                        bBookingtime = false;
                    }
                } else {
                    map.put("list_group_item", "");
                }
            } else {
                map.put("list_group_item", "");
            }

//            if (bBookingtime)
//                bookingtime = " ( 이용예정시간 : " + item.bookingtime + " )";
//            else
//                bookingtime = "";

            if (item.bookingtime != null && item.bookingtime.length() > 0)
                bookingtime = " ( 이용예정시간 : " + item.bookingtime + " )";
            else
                bookingtime = "";

            map.put("list_item1", item.boardDate + " " + bookingtime);

            map.put("list_item2", "출발");
            map.put("list_item3", item.tcTime);
            map.put("list_item4", item.startAddr);

            map.put("list_item5", "도착");
            map.put("list_item6", item.offTime);
            map.put("list_item7", item.endAddr);

            map.put("list_item8", "상태");
            map.put("list_item9", getStatusToString(item.callStatus));

            map.put("list_item10", item.drvName);
            map.put("list_item11", item.carNumber);

            list.add(map);
        }

        if (mListadaptor != null ) {
            mListadaptor.clear();
            mListadaptor = null;
        }
        mListadaptor = new ListHistoryRowAdaptor(getBaseContext(), R.layout.listhistory_items_row, list, onButtonClick);
        mHistoryList.setAdapter(mListadaptor);
    }
    private ListHistoryRowAdaptor mListadaptor = null;
    /**
     * 상태코드를 이름으로 변경
     * @param status
     * @return
     */
    private String getStatusToString(String status)
    {
        String ret = "";

        if (status.toUpperCase().equalsIgnoreCase(GlobalValues.CALL_STATE_ORDER))
            ret = "접수";
        else if (status.toUpperCase().equalsIgnoreCase(GlobalValues.CALL_STATE_ALLOC))
            ret = "배차";
        else if (status.toUpperCase().equalsIgnoreCase(GlobalValues.CALL_STATE_GETON))
            ret = "승차";
        else if (status.toUpperCase().equalsIgnoreCase(GlobalValues.CALL_STATE_GETOFF))
            ret = "하차";
        else if (status.toUpperCase().equalsIgnoreCase(GlobalValues.CALL_STATE_CANCEL))
            ret = "취소";
        else if (status.toUpperCase().equalsIgnoreCase(GlobalValues.CALL_STATE_CUSTOMER_CANCEL))
            ret = "고객취소";
        else if (status.toUpperCase().equalsIgnoreCase(GlobalValues.CALL_STATE_FAILED))
            ret = "실패";
        else if (status.toUpperCase().equalsIgnoreCase(GlobalValues.CALL_STATE_SUCCESS))
            ret = "완료";
        else if (status.toUpperCase().equalsIgnoreCase(GlobalValues.CALL_STATE_WAIT))
            ret = "대기";

        return ret;
    }

    /**
     * 콜이력을 불러온다.
     */
    private void doCallHistory ()
    {
        Logview.Logwrite(THIS_TAG, "doCallHistory");

        startProgress();

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSON_REQCallHistory reqvalue = new JSON_REQCallHistory(mConfigPerference.getAuthkey());
                RetrofitProcessManager.doRetrofitRequest(JSON_REQCallHistory.PAGE_NAME, reqvalue.getParams());
            }
        }).start();
    }

    /**
     * 취소요청 메시지 출력
     * @param index
     */
    private void viewBookingCancelMessage(int index)
    {
        Logview.Logwrite(THIS_TAG, "viewBookingCancelMessage : " + index);
        if (mCallHistory == null || mCallHistory.size() < 1) {
            Toast.makeText(getBaseContext(), "예약취소할 접수번호가 없습니다.", Toast.LENGTH_LONG).show();
            return;
        }

        CallHistoryItem item = mCallHistory.get(index);

        String msg = String.format("%s > %s\n예약 배차를 취소하시겠습니까?", item.startAddr, item.endAddr);
        mBookingCallid = item.callid;

        doAlertDialogView("알림", msg);
    }

    /**
     * 예약콜 취소
     */
    private void doBookingCancel()
    {
        Logview.Logwrite(THIS_TAG, "doBookingCancel call id : " + mBookingCallid);

        startProgress();

        new Thread(new Runnable() {
            @Override
            public void run()
            {
                ConfigPreference config = ConfigPreference.getInstance();
                config.setPreference(getBaseContext());

                JSON_REQReqCallCancel json = new JSON_REQReqCallCancel(config.getAuthkey(), mBookingCallid);
                RetrofitProcessManager.doRetrofitRequest(JSON_REQReqCallCancel.PAGE_NAME, json.getParams());
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
                if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.CallHistory.name().toLowerCase())) {
                    receiveCallHistory(event);
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

        //Toast.makeText(getBaseContext(), "예약배차가 취소되었습니다. 감사합니다.", Toast.LENGTH_LONG).show();

        handler.sendEmptyMessage(MSG_CANCEL_SUCCESS);
    }

    /**
     * 콜 이력 정보 수신
     * @param event
     */
    private void receiveCallHistory (JSONMessageEvent event)
    {
        Gson gson = new Gson();

        REP_JSONCallHistory json = gson.fromJson(event.getMessageBody(), REP_JSONCallHistory.class);

        if (json.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success)))
        {
            Logview.Logwrite(THIS_TAG, "receiveCallHistory success");
        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receiveCallHistory false : " + json.getBody().getCause());
            handler.sendEmptyMessage(MSG_HTTP_FAILED);
        }

        if (mCallHistory == null)
            mCallHistory = new ArrayList<CallHistoryItem>();
        mCallHistory.clear();

        List<REP_JSONCallHistory.Body.CallInfoItem> list = json.getBody().getCallHistory();
        if (list != null && list.size() > 0)
        {
            String temp;
            CallHistoryItem item;
            for (REP_JSONCallHistory.Body.CallInfoItem call : list)
            {
                item = new CallHistoryItem ();
                temp = call.getStartDetail();
                if (temp == null || temp.trim().length() < 1)
                    return;

                item.callStorageState = call.getCallTotalState();
                item.callStatus = call.getCallState();
                item.startAddr = call.getStartDetail();
                item.endAddr = call.getEndDetail();
                item.boardDate = call.getBoardDate();
                item.tcTime = call.getTcTime();
                item.offTime = call.getOffTime();
                item.callStatus = call.getCallState();
                item.drvName = call.getDrvName();
                item.carNumber = call.getCarNumber();
                item.callid = call.getCallid();
                item.bookingtime = call.getBookingtime();

                Logview.Logwrite(THIS_TAG, "callStorageState : " + item.callStorageState);
                Logview.Logwrite(THIS_TAG, "callStatus : " + item.callStatus);
                Logview.Logwrite(THIS_TAG, "startAddr : " + item.startAddr);
                Logview.Logwrite(THIS_TAG, "endAddr : " + item.endAddr);
                Logview.Logwrite(THIS_TAG, "boardDate : " + item.boardDate);
                Logview.Logwrite(THIS_TAG, "tcTime : " + item.tcTime);
                Logview.Logwrite(THIS_TAG, "offTime : " + item.offTime);
                Logview.Logwrite(THIS_TAG, "drvName : " + item.drvName);
                Logview.Logwrite(THIS_TAG, "carNumber : " + item.carNumber);
                Logview.Logwrite(THIS_TAG, "callid : " + item.callid);
                Logview.Logwrite(THIS_TAG, "bookingtime : " + item.bookingtime);

                mCallHistory.add(item);

                item = null;
            }

            handler.sendEmptyMessage(MSG_HISTORY_SUCCESS);
        }
    }

    class CallHistoryItem {
        public String callStorageState="";
        public String startAddr = "";
        public String endAddr="";
        public String boardDate = "";       // 탑승날짜
        public String tcTime = "";          // 출발시간
        public String offTime = "";         // 도착시간
        public String callStatus = "";
        public String drvName="";
        public String carNumber="";
        public String callid="";
        public String bookingtime="";       // 예약시간

        public CallHistoryItem () {}
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // Progress 원형
    //
    private ProgressDialog mProgressDialog = null;
    private synchronized void startProgress()
    {
        stopProgress();

        mProgressDialog = ProgressDialog.show(CallHistoryActivity.this, "", "잠시 기다려주세요", true);
    }

    private synchronized void stopProgress()
    {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
        mProgressDialog = null;
    }

    public void doAlertDialogView (String title, String msg)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(CallHistoryActivity.this);
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

    public void doAlertDialogView2 (String title, String msg)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(CallHistoryActivity.this);
        alert.setTitle(title);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.sendEmptyMessage(MSG_HISTORY_CALL);
                dialog.dismiss();     //닫기
            }
        });
        alert.setMessage(msg);
        alert.show();

    }
}
