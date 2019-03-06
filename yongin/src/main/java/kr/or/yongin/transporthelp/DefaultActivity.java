package kr.or.yongin.transporthelp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;

/**
 * Created by IONEMAX on 2017-01-02.
 */

public class DefaultActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState)
    {
        super.onCreate(savedInstanceState, persistentState);
    }

    /**
     * Dialog message view
     * @param context
     * @param title
     * @param msg
     */
    public void doAlertDialogView (Context context, String title, String msg)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
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
}
