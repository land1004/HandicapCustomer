package kr.or.hsnarae.transporthelp.impl.net.json.req;

import android.util.Log;

import com.google.gson.JsonObject;
import com.ionemax.iomlibrarys.log.Logview;

import kr.or.hsnarae.transporthelp.common.util.GlobalValues;
import kr.or.hsnarae.transporthelp.common.util.Utils;

/**
 * Created by IONEMAX on 2016-12-29.
 */

public class JSON_REQPOISearch extends JSON_REQHeader
{
    private final String THIS_TAG = "JSON_REQPOISearch";

    public static final String PAGE_NAME= "PoiSearch";
    public static final String PROTOCOL_VERSION = "1";

    private JsonObject m_body = null;

    public JSON_REQPOISearch(String addrname, String display, String start)
    {
        super(PROTOCOL_VERSION, GlobalValues.SERVICE_CODE);

        // set Body
        setAddrName (addrname);
        setDisplay (display);
        setStart (start);
    }

    public void setAddrName (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("ADDRNAME", value);
    }

    public void setDisplay (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("DISPLAY", value);
    }

    public void setStart (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("START", value);
    }


    public String getParams ()
    {
        JsonObject temp = new JsonObject();
        temp.add("BODY", m_body);
        temp.add("HEADER", getREQHeader());

        // JSON data base64로 인코딩 한다.
        String content = temp.toString();
        Logview.Logwrite(THIS_TAG, "getHttpParams : " + content);
        Log.d(THIS_TAG, "getHttpParams : " + content);

        return Utils.convertBase64(content);
    }


}
