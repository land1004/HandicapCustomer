package kr.or.yongin.transporthelp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import kr.or.yongin.transporthelp.common.FontAwesome.CFontAwesomeManager;
import kr.or.yongin.transporthelp.impl.preference.ConfigPreference;

public class NotiSettingActivity extends Activity
{
    private final String THIS_TAG = "NotiSettingActivity";

    private ConfigPreference mConfig;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti_setting);

        mConfig = ConfigPreference.getInstance();
        mConfig.setPreference(getBaseContext());

        // 기본 설정
        TextView title = (TextView)findViewById(R.id.txtTitle);
        title.setText("공지사항 알림 설정");

        TextView btnBack = (TextView)findViewById(R.id.btnBack);
        btnBack.setTypeface(CFontAwesomeManager.getTypeface(this, CFontAwesomeManager.FONTAWESOME));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //////////////////////////////////////////////////////////////

        ToggleButton tg = (ToggleButton)findViewById(R.id.toggleNotice);
        tg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mConfig.setUserNotification(b);
            }
        });

        tg.setChecked(mConfig.getUserNotification());
    }
}
