package kr.or.hsnarae.transporthelp.common.FCM;

import java.util.Map;

/**
 * Created by IONEMAX on 2016-12-29.
 */

public class PushMessageEvent
{
    private final String THIS_TAG = "PushKeyTokenEvent";

    private Map<String, String> pushMessage;

    public PushMessageEvent()
    {
    }

    public PushMessageEvent(Map<String, String> msg)
    {
        this.pushMessage = msg;
    }

    /**
     * 수신된 메시지 데이터 영역
     * @return
     */
    public Map<String, String> getPushMessage()
    {
        return this.pushMessage;
    }

}
