package kr.or.yongin.transporthelp.common.SMS;

import com.ionemax.iomlibrarys.log.Logview;

/**
 * Created by IONEMAX on 2016-12-29.
 */

public class SMSMessageEvent
{
    private final String THIS_TAG = "SMSMessageEvent";

    private String sms ="";
    private String confirm_code = "";

    public SMSMessageEvent()
    {
    }

    public SMSMessageEvent(String msg)
    {
        this.sms= msg;

        if (sms.indexOf("본인인증번호") > 0) {
            int last = sms.lastIndexOf('[');
            this.confirm_code = sms.substring(last + 1, last + 7);
            Logview.Logwrite(THIS_TAG, "SMS Code : " + this.confirm_code);
        }

    }

    /**
     * 수신된 메시지 데이터 영역
     * @return
     */
    public String getSMSMessage()
    {
        return this.sms;
    }

    public String getConfirmCode ()
    {
        return this.confirm_code;
    }

}
