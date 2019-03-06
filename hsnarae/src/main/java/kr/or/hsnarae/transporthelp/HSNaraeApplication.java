package kr.or.hsnarae.transporthelp;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.telephony.TelephonyManager;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.ionemax.iomlibrarys.log.Logview;

import java.util.Date;

import kr.or.hsnarae.transporthelp.common.db.DBControlManager;
import kr.or.hsnarae.transporthelp.common.util.GlobalValues;
import kr.or.hsnarae.transporthelp.common.util.PermissionRequest;
import kr.or.hsnarae.transporthelp.impl.preference.ConfigPreference;
import kr.or.hsnarae.transporthelp.impl.preference.OrderPreference;

/**
 * Created by IONEMAX on 2016-12-02.
 */

public class HSNaraeApplication extends Application
{
    private final String THIS_TAG = "HSNaraeApplication";
    private static HSNaraeApplication instance;

    private ConfigPreference mPreference;
    private DBControlManager dbControlManager;

    public static HSNaraeApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        Logview.setTagDefault("HSNarae");
        Logview.setDebugMode(false);

        // setup default typefaces
        TypefaceProvider.registerDefaultIconSets();

       // initializeDB ();

        mPreference = ConfigPreference.getInstance();
        mPreference.setPreference(getBaseContext());

        // 사용자 전화번호를 가져온다.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            initializeDB ();
            mPreference.setPhonenumber(doPhoneNumber());
        }

        // PUSH 서비스를 처리하기 위하여 메인 서비스 시작

//        // Main service start
//        Intent intent = new Intent();
//        intent.putExtra("class", HSNaraeApplication.class.getName());
//        ComponentName cn = new ComponentName("kr.or.hsnarae.transporthelp", "kr.or.hsnarae.transporthelp.MainServiceProcess");
//        intent.setComponent(cn);
//        ComponentName svcName = getBaseContext().startService(intent);
//
//        if (svcName == null)
//        {
//            Logview.Logwrite(THIS_TAG, "Main process service can not nunning....");
//        }
//        else
//        {
//            Logview.Logwrite(THIS_TAG, "Main process service nunning....");
//        }

    }

    @Override
    public void onTerminate()
    {
        if (dbControlManager != null)
            dbControlManager.dbTerminate();
        dbControlManager = null;

        super.onTerminate();
    }

    /**
     * 사용자 전화번호를 가져온다.
     */
    public String doPhoneNumber()
    {
        String strMinno = "";
        TelephonyManager telephony = (TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (telephony  != null)
        {
            strMinno = telephony.getLine1Number();
            Logview.Logwrite(THIS_TAG, "telephony.getLine1Number : " + strMinno);
            if(strMinno != null && (strMinno.indexOf("+") > -1)) // || strMinno.indexOf("010") == 0))
            {
                if(strMinno.length()> 10)
                {
                    int nSkipIndex = strMinno.lastIndexOf("+82");
                    String strTemp = strMinno.substring(nSkipIndex+3, strMinno.length());

                    String strTemp2 = strTemp.substring(0, 1);
                    if (strTemp2.equalsIgnoreCase("1"))
                        strMinno = "0" + strTemp;
                }
            }
        }

//        // 테스트 번호
//        if (strMinno == null || strMinno.length() < 1)
//            strMinno = "01051745111";

        return strMinno;
    }

    public void initializeDB ()
    {
        // DB Create
        if (dbControlManager == null) {
            Logview.Logwrite(THIS_TAG, "Do initializeDB");
            dbControlManager = new DBControlManager();
            dbControlManager.dbInitialize(getBaseContext());
        } else {
            Logview.Logwrite(THIS_TAG, "Already initializeDB");
        }
    }

    /**
     * 재요청 방지
     * @return
     */
    private int MAX_REORDER_COUNT = 3;
    public boolean checkReorder()
    {
        boolean bret = false;
        OrderPreference preference = OrderPreference.getInstance();
        preference.setPreference(getBaseContext());

        long oldOrderTime = preference.getOrderReorderTime();
        int count = preference.getOrderReorderCount();
        long TIME_LIMIT = oldOrderTime + 1000 * 60 * 30;
        long current = System.currentTimeMillis();

        if (  current < TIME_LIMIT) {
            count += 1;
        } else {
            if (count == 0 || count > MAX_REORDER_COUNT) {
                count = 1;
                preference.setOrderReorderTime(current);
            } else {
                count += 1;
            }
        }
        preference.setOrderReorderCount(count);

        if (count > MAX_REORDER_COUNT)
            bret = true;

        return bret;
    }

}
