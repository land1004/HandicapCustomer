package kr.or.yongin.transporthelp.impl.net.json.req;

import com.google.gson.JsonObject;
import com.ionemax.iomlibrarys.log.Logview;

import kr.or.yongin.transporthelp.common.util.GlobalValues;
import kr.or.yongin.transporthelp.common.util.Utils;

/**
 * Created by IONEMAX on 2016-12-29.
 */

public class JSON_REQReqCall extends JSON_REQHeader
{
    private final String THIS_TAG = "JSON_REQReqCall";

    public static final String PAGE_NAME= "ReqCall";
    public static final String PROTOCOL_VERSION = "1";

    private JsonObject m_body = null;

    public JSON_REQReqCall(String authkey)
    {
        super(PROTOCOL_VERSION, GlobalValues.SERVICE_CODE);

        // set Body
        setAuthKey(authkey);
    }

    public void setAuthKey (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("AUTHKEY", value);
    }

    public void setStart (String detail, String x, String y)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("STARTDETAILADDRESS", detail);
        m_body.addProperty("STARTPOSX", x);
        m_body.addProperty("STARTPOSY", y);
    }

    public void setEnd (String detail, String x, String y)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("ENDDETAILADDRESS", detail);
        m_body.addProperty("ENDPOSX", x);
        m_body.addProperty("ENDPOSY", y);
    }

    /**
     * 동승인원
     * 숫자형식 (0,1,2)
     * @param value
     */
    public void setCompanion (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("COMPANION", value);
    }

    public void setUseTypeCode (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("USETYPECODE", value);
    }

    /**
     * 휠체어 사용여부 사용:Y , 미사용:N
     * @param value
     */
    public void setWheelchaiarYN (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("WHEELCHIARYN", value);
    }

    public String getParams ()
    {
        JsonObject temp = new JsonObject();
        temp.add("BODY", m_body);
        temp.add("HEADER", getREQHeader());

        // JSON data base64로 인코딩 한다.
        String content = temp.toString();
        Logview.Logwrite(THIS_TAG, "getHttpParams : " + content);

        return Utils.convertBase64(content);
    }


}
