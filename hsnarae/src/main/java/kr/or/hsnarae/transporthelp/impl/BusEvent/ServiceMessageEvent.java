package kr.or.hsnarae.transporthelp.impl.BusEvent;

import android.util.Base64;

import com.google.gson.Gson;
import com.ionemax.iomlibrarys.log.Logview;

import java.io.UnsupportedEncodingException;

import kr.or.hsnarae.transporthelp.impl.net.json.rep.REP_JSONHeader;

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
