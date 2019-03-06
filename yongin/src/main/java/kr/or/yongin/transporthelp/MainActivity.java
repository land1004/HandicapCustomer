package kr.or.yongin.transporthelp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ionemax.iomlibrarys.log.Logview;

import kr.or.yongin.transporthelp.common.FontAwesome.CFontAwesomeManager;


public class MainActivity extends Activity implements View.OnClickListener
{
    private final String THIS_TAG = "MainActivity";

    private DrawerLayout drawer;
    private View drawerView;
    private boolean mToggle = false;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToggle = false;

        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerView = (View)findViewById(R.id.drawer);

        TextView btnBack = (TextView)findViewById(R.id.btnBack);
        btnBack.setTypeface(CFontAwesomeManager.getTypeface(this, CFontAwesomeManager.FONTAWESOME));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mToggle)
                    drawer.openDrawer(drawerView);
                else
                    drawer.closeDrawers();

                mToggle ^= mToggle;
            }
        });

        drawer.setDrawerListener(myDrawerListener);

        TextView tv = (TextView)findViewById(R.id.menuItemInfo);
        tv.setOnClickListener(this);
        tv = (TextView)findViewById(R.id.menuItemEtc);
        tv.setOnClickListener(this);
        tv = (TextView)findViewById(R.id.menuItemCallHistory);
        tv.setOnClickListener(this);
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
            case R.id.menuItemInfo:
                LinearLayout layoutInfo = (LinearLayout)findViewById(R.id.layoutItemInfo);
                if (layoutInfo.getVisibility() == View.VISIBLE)
                    layoutInfo.setVisibility(View.GONE);
                else
                    layoutInfo.setVisibility(View.VISIBLE);

                Logview.Logwrite(THIS_TAG, "OnClick menuItemInfo");
                break;
            case R.id.menuItemEtc:
                LinearLayout layoutEtc = (LinearLayout)findViewById(R.id.layoutItemEtc);
                if (layoutEtc.getVisibility() == View.VISIBLE)
                    layoutEtc.setVisibility(View.GONE);
                else
                    layoutEtc.setVisibility(View.VISIBLE);
                Logview.Logwrite(THIS_TAG, "OnClick menuItemEtc");
                break;
            case R.id.menuItemCallHistory:
                Logview.Logwrite(THIS_TAG, "OnClick menuItemCallHistory");
                break;
        }
    }

    ////////////////////////////////////////////////////////////
    // side menu control
    //
    DrawerLayout.DrawerListener myDrawerListener = new DrawerLayout.DrawerListener()
    {
        public void onDrawerClosed(View drawerView)
        {
            Logview.Logwrite(THIS_TAG, "onDrawerClosed");
        }

        public void onDrawerOpened(View drawerView)
        {
            Logview.Logwrite(THIS_TAG, "onDrawerOpened");
        }

        public void onDrawerSlide(View drawerView, float slideOffset)
        {
            Logview.Logwrite(THIS_TAG, "onDrawerSlide: "
                    + String.format("%.2f", slideOffset));
        }

        public void onDrawerStateChanged(int newState)
        {
            String state;
            switch (newState) {
                case DrawerLayout.STATE_IDLE:
                    state = "STATE_IDLE";
                    break;
                case DrawerLayout.STATE_DRAGGING:
                    state = "STATE_DRAGGING";
                    break;
                case DrawerLayout.STATE_SETTLING:
                    state = "STATE_SETTLING";
                    break;
                default:
                    state = "unknown!";
            }

            Logview.Logwrite(THIS_TAG, state);
        }
    };
}
