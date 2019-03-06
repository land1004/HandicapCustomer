package kr.or.yongin.transporthelp;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.ionemax.iomlibrarys.log.Logview;

import kr.or.yongin.transporthelp.common.db.DBControlManager;
import kr.or.yongin.transporthelp.impl.preference.ConfigPreference;

/**
 * Created by IONEMAX on 2016-12-02.
 */

public class YINuriApplication extends Application
{
    private final String THIS_TAG = "YINuriApplication";
    private static YINuriApplication instance;

    private ConfigPreference mPreference;
    private DBControlManager dbControlManager;

    public static YINuriApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        Logview.setTagDefault("YINuri");
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
//        intent.putExtra("class", YINuriApplication.class.getName());
//        ComponentName cn = new ComponentName("kr.or.yongin.transporthelp", "kr.or.yongin.transporthelp.MainServiceProcess");
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
}
