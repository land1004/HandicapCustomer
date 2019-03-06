package kr.or.hsnarae.transporthelp.common.SMS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.ionemax.iomlibrarys.log.Logview;

import kr.or.hsnarae.transporthelp.common.util.BusEventProvider;

/**
 * Created by IONEMAX on 2017-02-01.
 */

public class SMSBroadcastReceiver extends BroadcastReceiver
{
    private final String THIS_TAG = "SMSBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // 수신되었을 때 호출되는 콜백 메서드
        // 매개변수 intent의 액션에 방송의 '종류'가 들어있고
        // 필드에는 '추가정보' 가 들어 있습니다.

        // SMS 메시지를 파싱합니다.
        Bundle bundle = intent.getExtras();
        String str = ""; // 출력할 문자열 저장
        if (bundle != null) {
            // 수신된 내용이 있으면
            // 실제 메세지는 Object타입의 배열에 PDU 형식으로 저장됨

            Object[] pdus = (Object[]) bundle.get("pdus");

            SmsMessage[] msgs = new SmsMessage[pdus.length];
            String sms, code;
            for (int i = 0; i < msgs.length; i++) {
                // PDU 포맷으로 되어 있는 메시지를 복원합니다.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String format = bundle.getString("format");
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                }
                else {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }

                //msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                str += msgs[i].getOriginatingAddress()
                        + "에게 문자왔음, " +
                        msgs[i].getMessageBody().toString()
                        + "\n";
                sms = msgs[i].getMessageBody().toString();
                Logview.Logwrite(THIS_TAG, "SMS Receive no." + i +" : " + sms);
                if (sms.indexOf("[화성나래]") > 0)
                {
                    BusEventProvider.getInstance().post(new SMSMessageEvent(sms));
                }
            }

            Toast.makeText(context, str, Toast.LENGTH_LONG).show();
        }
    }
}
