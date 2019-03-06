package kr.or.yongin.transporthelp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ionemax.iomlibrarys.log.Logview;
import com.squareup.otto.Subscribe;

import kr.or.yongin.transporthelp.common.FontAwesome.CFontAwesomeManager;
import kr.or.yongin.transporthelp.common.util.BusEventProvider;
import kr.or.yongin.transporthelp.impl.net.json.IJSONPageMethod;
import kr.or.yongin.transporthelp.impl.net.json.JSONMessageEvent;
import kr.or.yongin.transporthelp.impl.net.json.RetrofitProcessManager;
import kr.or.yongin.transporthelp.impl.net.json.rep.REP_JSONFindRoadInfo;
import kr.or.yongin.transporthelp.impl.net.json.req.JSON_REQFindRoadInfo;
import kr.or.yongin.transporthelp.impl.preference.ConfigPreference;
import kr.or.yongin.transporthelp.impl.preference.OrderPreference;

public class GetONMessageActivity extends Activity
{
    private final String THIS_TAG = "GetOffMessageActivity";

    private final int MSG_HTTP_FAILED = 0;
    private final int MSG_FIND_SUCCESS = 10;

    private OrderPreference mOrderPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_off_message);

        mOrderPreference = OrderPreference.getInstance();
        mOrderPreference.setPreference(getBaseContext());

        // 기본 설정
        TextView title = (TextView)findViewById(R.id.txtTitle);
        title.setText("차량 탑승 완료");

        TextView btnBack = (TextView)findViewById(R.id.btnBack);
        btnBack.setTypeface(CFontAwesomeManager.getTypeface(this, CFontAwesomeManager.FONTAWESOME));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnBack.setVisibility(View.GONE);

        LinearLayout layoutBTNLeft = (LinearLayout)findViewById(R.id.layoutBTNLeft);
        LinearLayout layoutBTNCenter = (LinearLayout)findViewById(R.id.layoutBTNCenter);
        LinearLayout layoutBTNRight = (LinearLayout)findViewById(R.id.layoutBTNRight);

        Button bstBTNLeft = (Button)findViewById(R.id.btnLeft);
        Button bstBTNCenter = (Button)findViewById(R.id.btnCenter);
        Button bstBTNRight = (Button)findViewById(R.id.btnRight);

        layoutBTNLeft.setVisibility(View.GONE);
        layoutBTNCenter.setVisibility(View.VISIBLE);
        layoutBTNRight.setVisibility(View.GONE);

        bstBTNCenter.setText("종료하기");
        bstBTNCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //////////////////////////////////////////////////////////////
        setStartEndDetail ();
        doFindRoadInfo ();
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
        super.onStop();
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_FIND_SUCCESS:
                    break;
            }
        }
    };

    private void setStartEndDetail()
    {
        String msg = mOrderPreference.getOrderFromDetail();
        msg += "에서 " + mOrderPreference.getOrderToDetail();
        msg += "으로 가는 차량을 탑승하셨습니다.";

        TextView tv = (TextView)findViewById(R.id.txtGetoffPos);
        tv.setText(msg);
    }

    /**
     * 예상 소요 신간
     * @param dtTime
     */
    private void setDurationTime(String dtTime)
    {
        String msg = mOrderPreference.getOrderDriverCar() + "\n";
        msg += "예상 소요 시간은 " + dtTime + "분 입니다.";

        TextView tv = (TextView)findViewById(R.id.txtGetOffCar);
        tv.setText(msg);
    }

    /**
     * 출/도착지간의 예정 시간
     */
    private void doFindRoadInfo()
    {
        Logview.Logwrite(THIS_TAG, "doFindRoadInfo");

        new Thread(new Runnable() {
            @Override
            public void run() {
                ConfigPreference config = ConfigPreference.getInstance();
                config.setPreference(getBaseContext());

                JSON_REQFindRoadInfo reqvalue = new JSON_REQFindRoadInfo(config.getAuthkey(),
                        String.valueOf(mOrderPreference.getOrderFromX()), String.valueOf(mOrderPreference.getOrderFromY()),
                        String.valueOf(mOrderPreference.getOrderToX()), String.valueOf(mOrderPreference.getOrderToY()));
                RetrofitProcessManager.doRetrofitRequest(JSON_REQFindRoadInfo.PAGE_NAME, reqvalue.getParams());
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
                if (pageName.trim().toLowerCase().equalsIgnoreCase(IJSONPageMethod.JSON_PAGE_METHOD.FindRoadInfo.name().toLowerCase())) {
                    receiveFindRoadInfo(event);
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
    private void receiveFindRoadInfo(JSONMessageEvent event)
    {
        Gson gson = new Gson();

        REP_JSONFindRoadInfo json = gson.fromJson(event.getMessageBody(), REP_JSONFindRoadInfo.class);

        if (json.getBody().getResult().equalsIgnoreCase(getResources().getString(R.string.success)))
        {
            Logview.Logwrite(THIS_TAG, "receiveFindRoadInfo success");
        } else {
            // 실패 사유
            Logview.Logwrite(THIS_TAG, "receiveFindRoadInfo false : " + json.getBody().getCause());
            handler.sendEmptyMessage(MSG_HTTP_FAILED);
        }

        String dtTime = json.getBody().getDtTime();
        Logview.Logwrite(THIS_TAG, "receiveFindRoadInfo dtTime : " + dtTime);

        handler.obtainMessage(MSG_FIND_SUCCESS, dtTime).sendToTarget();
    }

}
