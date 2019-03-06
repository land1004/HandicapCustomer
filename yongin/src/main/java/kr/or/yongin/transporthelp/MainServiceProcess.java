package kr.or.yongin.transporthelp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ionemax.iomlibrarys.log.Logview;

import kr.or.yongin.transporthelp.common.util.BusEventProvider;
import kr.or.yongin.transporthelp.common.util.ErrorcodeToString;
import kr.or.yongin.transporthelp.common.util.GlobalValues;
import kr.or.yongin.transporthelp.impl.BusEvent.ServiceMessageEvent;
import kr.or.yongin.transporthelp.impl.net.IPushMessage;
import kr.or.yongin.transporthelp.impl.net.json.IJSONPageMethod;
import kr.or.yongin.transporthelp.impl.net.json.JSONMessageEvent;
import kr.or.yongin.transporthelp.impl.net.json.RetrofitProcessManager;
import kr.or.yongin.transporthelp.impl.net.json.rep.REP_JSONCallInfo;
import kr.or.yongin.transporthelp.impl.net.json.rep.REP_JSONNoticeDetail;
import kr.or.yongin.transporthelp.impl.net.json.req.JSON_REQCallInfo;
import kr.or.yongin.transporthelp.impl.net.json.req.JSON_REQNoticeDetail;
import kr.or.yongin.transporthelp.impl.preference.ConfigPreference;
import kr.or.yongin.transporthelp.impl.preference.OrderPreference;

/**
 * Created by IONEMAX on 2017-01-18.
 */

public class MainServiceProcess extends Service
{
    private final String THIS_TAG = "MainServiceProcess";

    private final int MSG_HTTP_FAILED = 0;
    private final int MSG_PUSH_READ = 100;
    private final int MSG_RECEIVE_DATA = 200;

    private final int MSG_DEBUG = 3000;

    private ConfigPreference mConifg;
    private OrderPreference  mOrderPreference;
    private RetofitRequestReceiver mRetrofitReceiver = null;
    private BroadcastReceiver mPushReceiver;
    @Override
    public void onCreate()
    {
        super.onCreate();
        Logview.Logwrite(THIS_TAG, "onCreate");

        // 서비스 디버깅을 하기 위하여 넣는다.
        // 디버깅을 하지 않을 경우 막아야 한다.
        //android.os.Debug.waitForDebugger();

        mConifg = ConfigPreference.getInstance();
        mConifg.setPreference(getBaseContext());

        mOrderPreference = OrderPreference.getInstance();
        mOrderPreference.setPreference(getBaseContext());

        if (mRetrofitReceiver == null)
            setReceiverRegistry ();

        mPushReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                Logview.Logwrite(THIS_TAG, "Push main receive....");
                if (intent != null) {
                    String type = intent.getStringExtra("type");
                    if (type != null && type.trim().toUpperCase().equalsIgnoreCase("PUSH")) {
                        String payload = intent.getStringExtra("payload");
                        Logview.Logwrite(THIS_TAG, "PUSH Message : " + payload);

                        if (payload != null && payload.length() > 3) {
                            if (mRetrofitReceiver == null)
                                setReceiverRegistry();

                            handler.obtainMessage(MSG_PUSH_READ, payload).sendToTarget();
                        }
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalValues.ACTION_BROADCAST_PUSH);

        registerReceiver(mPushReceiver, filter);

        //handler.sendEmptyMessageDelayed(MSG_DEBUG, 30 * 1000);
        Logview.Logwrite(THIS_TAG, "onCreate finished");
    }

    @Override
    public void onDestroy()
    {
        Logview.Logwrite(THIS_TAG, "onDestroy");

        if (mRetrofitReceiver != null)
            unregisterReceiver(mRetrofitReceiver);
        mRetrofitReceiver = null;

        if (mPushReceiver != null)
            unregisterReceiver(mPushReceiver);
        mPushReceiver = null;

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logview.Logwrite(THIS_TAG, "onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Logview.Logwrite(THIS_TAG, "onStartCommand");
        if (intent != null) {
            String type = intent.getStringExtra("type");
            if (type != null && type.trim().toUpperCase().equalsIgnoreCase("PUSH")) {
                String payload = intent.getStringExtra("payload");
                Logview.Logwrite(THIS_TAG, "PUSH Message : " + payload);

                if (payload != null && payload.length() > 3) {
                    if (mRetrofitReceiver == null)
                        setReceiverRegistry();

                    handler.obtainMessage(MSG_PUSH_READ, payload).sendToTarget();
                }
            }
        }

        return START_STICKY;
    }

    /**
     * Broadcast receiver registry
     */
    private void setReceiverRegistry ()
    {
        mRetrofitReceiver = new RetofitRequestReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalValues.ACTION_BROADCAST_SERVER);

        registerReceiver(mRetrofitReceiver, filter);
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
                case MSG_PUSH_READ:
                    pushMessageProcess ((String)msg.obj);
                    break;
                case MSG_RECEIVE_DATA:
                    doMessageEventProcess ((JSONMessageEvent)msg.obj);
                    break;
                case MSG_DEBUG:
                    Logview.Logwrite(THIS_TAG, "나는 살아 있다.");
                    handler.sendEmptyMessageDelayed(MSG_DEBUG, 30 * 1000);
                    break;
            }
        }
    };

    /**
     * HTTP 요청 수신 처리
     *
     * @param event
     */
    public void doMessageEventProcess(JSONMessageEvent event) {
        Logview.Logwrite(THIS_TAG, "jsonMessageEvent 수신 상태 : " + event.getMessageStatus());
        if (event.getMessageStatus()) {
            String pageName = event.getPageName();
            Logview.Logwrite(THIS_TAG, "jsonMessageEvent page : " + pageName);

            if (pageName != null) {
                if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.NoticeDetail.name().toLowerCase())) {
                    receiveNoticeDetail(event);
                }
                else if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.CallInfo.name().toLowerCase())) {
                    receiveCallInfo(event);
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
     * PUSH Message Processing
     * @param paylod
     */
    private void pushMessageProcess(String paylod)
    {
        String[] push = paylod.split(",");

        //setNotification(IntroActivity.class, push[2], push[3], "callid", push[1]);
        // PUSH Message 구조
        // 0. Message ID
        // 1. Call id / notice_seq
        // 2. Message title
        // 3. Message note
        if (push[0] != null && push.length > 1)
        {
            String msgID = push[0].trim().toUpperCase();
            String callID="";
            if (push[1] != null && push[1].length() > 0)
            {
                try {
                    int nCallid = Integer.parseInt(push[1].trim());
                    callID = String.valueOf(nCallid);
                    Logview.Logwrite(THIS_TAG, "Call id : " + nCallid);
                } catch (NumberFormatException ne) {
                    ne.printStackTrace();
                }
            }

            if (msgID.equalsIgnoreCase(IPushMessage.PUSH_NOTICE))
            {
                // 공지사항
                if (mConifg.getUserNotification()) {
                    setNotification(NoticeDetailActivity.class,
                            push[2], push[3], "notice_seq", callID);
                }
            }
            else if (msgID.equalsIgnoreCase(IPushMessage.PUSH_ALLOC_SUCCESS) ) {
                // 배차성공
                String oldCallid = mOrderPreference.getOrderCallid();
                if (oldCallid != null && oldCallid.equalsIgnoreCase(callID)) {
                    Logview.Logwrite(THIS_TAG, "배차성공 Call id : " + callID);
                    BusEventProvider.getInstance().post(
                            new ServiceMessageEvent("PUSH", GlobalValues.CALL_STATE_ALLOC, callID));
                }

                setNotification(IntroActivity.class,
                        push[2], push[3], "callid", callID);
            }
            else if (msgID.equalsIgnoreCase(IPushMessage.PUSH_ALLOC_FAILED) )
            {
                // 배차실패
                String oldCallid = mOrderPreference.getOrderCallid();
                if (oldCallid != null && oldCallid.equalsIgnoreCase(callID)) {
                    Logview.Logwrite(THIS_TAG, "배차실패 Call id : " + callID);
                    BusEventProvider.getInstance().post(
                            new ServiceMessageEvent("PUSH", GlobalValues.CALL_STATE_FAILED, callID));
                }

                setNotification(IntroActivity.class,
                        push[2], push[3], "callid", callID);
            }
            else if (msgID.equalsIgnoreCase(IPushMessage.PUSH_GET_ON) )
            {
                // 승차
                String oldCallid = mOrderPreference.getOrderCallid();
                if (oldCallid != null && oldCallid.equalsIgnoreCase(callID)) {
                    Logview.Logwrite(THIS_TAG, "승차 Call id : " + callID);
                    BusEventProvider.getInstance().post(
                            new ServiceMessageEvent("PUSH", GlobalValues.CALL_STATE_GETON, callID));
                }

                setNotification(IntroActivity.class,
                        push[2], push[3], "callid", callID);
            }
            else if (msgID.equalsIgnoreCase(IPushMessage.PUSH_CALL_CANCEL) )
            {
                // 승객 취소
                String oldCallid = mOrderPreference.getOrderCallid();
                if (oldCallid != null && oldCallid.equalsIgnoreCase(callID)) {
                    Logview.Logwrite(THIS_TAG, "승객취소 Call id : " + callID);
                    BusEventProvider.getInstance().post(
                            new ServiceMessageEvent("PUSH", GlobalValues.CALL_STATE_CANCEL, callID));
                }

                setNotification(IntroActivity.class,
                        push[2], push[3], "callid", callID);
            }
            else if (msgID.equalsIgnoreCase(IPushMessage.PUSH_ALOC_CANCEL) )
            {
                // 기사 취소
                String oldCallid = mOrderPreference.getOrderCallid();
                if (oldCallid != null && oldCallid.equalsIgnoreCase(callID)) {
                    Logview.Logwrite(THIS_TAG, "기사취소 Call id : " + callID);
                    BusEventProvider.getInstance().post(
                            new ServiceMessageEvent("PUSH", GlobalValues.CALL_STATE_CANCEL, callID));
                }

                setNotification(IntroActivity.class,
                        push[2], push[3], "callid", callID);
            }
            else {
                setNotification(IntroActivity.class,
                        push[2], push[3], "callid", callID);
                Logview.Logwrite(THIS_TAG, "PUSH failed : " + paylod);
            }
        } else {
            Toast.makeText(getBaseContext(), paylod, Toast.LENGTH_LONG).show();
            Logview.Logwrite(THIS_TAG, "PUSH : " + paylod);
        }
    }

    /**
     * 공지상세 요청
     */
    private void doNoticeDetail(final String notice_seq)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSON_REQNoticeDetail json = new JSON_REQNoticeDetail(mConifg.getAuthkey(), notice_seq);
                RetrofitProcessManager.doRetrofitRequestService(getBaseContext(), JSON_REQNoticeDetail.PAGE_NAME, json.getParams());
            }
        }).start();
    }

    private String mTempCallid="";
    /**
     * 콜 인포 요청
     */
    private void doCallInfo(final String callid)
    {
        mTempCallid = callid;
        new Thread(new Runnable() {
            @Override
            public void run() {
                ConfigPreference config = ConfigPreference.getInstance();
                config.setPreference(getBaseContext());
                JSON_REQCallInfo json = new JSON_REQCallInfo(config.getAuthkey(), callid);
                RetrofitProcessManager.doRetrofitRequestService(getBaseContext(), JSON_REQCallInfo.PAGE_NAME, json.getParams());
            }
        }).start();

    }

    /**
     * 공지사항 상세
     *
     * @param event
     */
    private void receiveNoticeDetail(JSONMessageEvent event)
    {
        Gson gson = new Gson();

        REP_JSONNoticeDetail json = gson.fromJson(event.getMessageBody(), REP_JSONNoticeDetail.class);

        if (json.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success))) {
            Logview.Logwrite(THIS_TAG, "receiveNoticeDetail success");
        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receiveNoticeDetail false : " + json.getBody().getCause());
            Logview.Logwrite(THIS_TAG, "receiveNoticeDetail false : " +
                    ErrorcodeToString.getError(json.getBody().getResult(), json.getBody().getCause()));
       }

        Logview.Logwrite(THIS_TAG, "receiveNoticeDetail title : " + json.getBody().getTitle());
        Logview.Logwrite(THIS_TAG, "receiveNoticeDetail seq : " + json.getBody().getNoticeSeq());

        setNotification(NoticeDetailActivity.class, "공지사항", json.getBody().getTitle(),
                "notice_seq", json.getBody().getNoticeSeq());
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
        String callStatus = json.getBody().getCallState();
        String callCancelType = json.getBody().getCall_canceltype();

        Logview.Logwrite(THIS_TAG, "Call calltatus : " + json.getBody().getCallState());
        Logview.Logwrite(THIS_TAG, "Call startDetail : " + json.getBody().getCallState());
        Logview.Logwrite(THIS_TAG, "Call endDetail : " + json.getBody().getCallState());
        Logview.Logwrite(THIS_TAG, "Call drvName : " + json.getBody().getCallState());
        Logview.Logwrite(THIS_TAG, "Call carNumber : " + json.getBody().getCarNumber());
        Logview.Logwrite(THIS_TAG, "Call CANCELUSER : " + json.getBody().getCall_canceltype());

        if (callStatus != null) {
            String msg = "";
            if (callStatus.trim().toUpperCase().equalsIgnoreCase(GlobalValues.CALL_STATE_ALLOC))
            {
                msg = "차량번호 " + json.getBody().getCarNumber() + " ";
                msg += json.getBody().getDrvName();
                msg += " 기사님께 배차 되었습니다.";
                setNotification(TraceMapActivity.class, "배차성공", msg,
                        "callid", mTempCallid);
            }
            else if (callStatus.trim().toUpperCase().equalsIgnoreCase(GlobalValues.CALL_STATE_GETON))
            {
                msg = "차량번호 " + json.getBody().getCarNumber() + "에 탑승하셨습니다. ";
                msg += " 이용해 주셔서 감사합니다.";
                setNotification(GetONMessageActivity.class, "승차성공", msg,
                        "callid", mTempCallid);
            }
            else if (callStatus.trim().toUpperCase().equalsIgnoreCase(GlobalValues.CALL_STATE_GETOFF))
            {
                msg = "이용해 주셔서 감사합니다.";
                setNotification(IntroActivity.class, "하차성공", msg,
                        "callid", mTempCallid);
            }
            else if (callStatus.trim().toUpperCase().equalsIgnoreCase(GlobalValues.CALL_STATE_CANCEL))
            {
                if (callCancelType != null && callCancelType.toUpperCase().trim().equalsIgnoreCase("S"))
                {
                    msg = json.getBody().getStartDetail() + " 출발 ";
                    msg += json.getBody().getEndDetail() + " 도착 요청이 실패되었습니다.";
                    setNotification(OrderWaitActivity.class, "배차실패", msg,
                            "view_state", GlobalValues.ORDER_WAIT_VIEW_FAILED);
                } else {
                    msg = json.getBody().getStartDetail() + " 출발 ";
                    msg += json.getBody().getEndDetail() + " 도착 요청이 취소되었습니다.";
                    setNotification(OrderWaitActivity.class, "배차취소", msg,
                            "view_state", GlobalValues.ORDER_WAIT_VIEW_CANCLED);
                }
            }
            else if (callStatus.trim().toUpperCase().equalsIgnoreCase(GlobalValues.CALL_STATE_FAILED))
            {
                msg = json.getBody().getStartDetail() + " 출발 ";
                msg += json.getBody().getEndDetail() +" 도착 요청이 실패되었습니다.";
                setNotification(OrderWaitActivity.class, "배차실패", msg,
                        "view_state", GlobalValues.ORDER_WAIT_VIEW_FAILED);
            }
        }
    }

    /**
     * Notifiation bar를 만든다.
     * @param title
     * @param value
     */
    private void setNotification(Class cls, String title, String subTitel, String key, String value) {
        Resources res = getResources();

        Intent notificationIntent = new Intent(MainServiceProcess.this, cls);
        notificationIntent.putExtra(key, value); //전달할 값
        PendingIntent contentIntent = PendingIntent.getActivity(MainServiceProcess.this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentTitle(res.getString(R.string.app_name) + "   " + title)     // 상태바 드래그시 보이는 타이틀
                .setContentText(subTitel)                                           // 드래그시 보이는 서브 타이틀
                .setTicker(res.getString(R.string.app_name) + " " + title)          // 상태바 한줄 메시지
                .setSmallIcon(R.mipmap.icon3)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.icon3))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify((int) (System.currentTimeMillis() / 1000), builder.build());
    }



    class RetofitRequestReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action != null && action.equalsIgnoreCase(GlobalValues.ACTION_BROADCAST_SERVER))
            {
                boolean flag = intent.getBooleanExtra("flag", false);
                if (flag)
                {
                    JSONMessageEvent receiveMessage = new JSONMessageEvent(flag, intent.getByteArrayExtra("data"));
                    handler.obtainMessage(MSG_RECEIVE_DATA, receiveMessage).sendToTarget();
                }
            }
        }
    }
}


