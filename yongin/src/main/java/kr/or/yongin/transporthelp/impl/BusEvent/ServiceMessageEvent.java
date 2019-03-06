package kr.or.yongin.transporthelp.impl.BusEvent;

/**
 * Created by IONEMAX on 2016-12-29.
 */

public class ServiceMessageEvent
{
    private final String THIS_TAG = "ServiceMessageEvent";

    private String type="";     // CALL, SERVER
    private String callstate="";   //
    private String msg="";

    public ServiceMessageEvent()
    {
    }

    public ServiceMessageEvent(String type, String callstate, String msg)
    {
        this.type = type;
        this.callstate = callstate;
        this.msg = msg;
    }

    public String getCallstate()
    {
        return this.callstate;
    }

    public String getMessage()
    {
        return this.msg;
    }
}
