package kr.or.yongin.transporthelp.common.FCM;

/**
 * Created by IONEMAX on 2016-12-29.
 */

public class PushKeyTokenEvent
{
    private final String THIS_TAG = "PushKeyTokenEvent";

    private String newTokey="";

    public PushKeyTokenEvent()
    {
    }

    public PushKeyTokenEvent(String token)
    {
        this.newTokey = token;
    }

    /**
     * 수신된 메시지 데이터 영역
     * @return
     */
    public String getPushkey()
    {
        return this.newTokey;
    }

}
