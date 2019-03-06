package kr.or.hsnarae.transporthelp.impl.net.json.req;

import com.google.gson.JsonObject;
import com.ionemax.iomlibrarys.log.Logview;

import kr.or.hsnarae.transporthelp.common.util.GlobalValues;
import kr.or.hsnarae.transporthelp.common.util.Utils;

/**
 * Created by IONEMAX on 2016-12-29.
 */

public class JSON_REQChgMemberInfo extends JSON_REQHeader
{
    private final String THIS_TAG = "JSON_REQChgMemberInfo";

    public static final String PAGE_NAME= "ChgMemberInfo";
    public static final String PROTOCOL_VERSION = "1";

    private JsonObject m_body = null;

    public JSON_REQChgMemberInfo(String authKey)
    {
        super(PROTOCOL_VERSION, GlobalValues.SERVICE_CODE);

        // body
        setAuthKey(authKey);
    }

    public void setAuthKey (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("AUTHKEY", value);
    }

    public void setUserEmail (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("EMAIL", value);
    }

    public void setZipcode (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("ZIPCODE", value);
    }

    public void setSido (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("SIDO", value);
    }

    public void setSigugun (String value1, String value2)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("SIGUGUNA", value1);
        m_body.addProperty("SIGUGUNB", value2);
    }

    public void setDong (String value1, String value2)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("DONG1", value1);
        m_body.addProperty("DONG2", value2);
    }

    public void setAddr (String value1, String value2)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("ADDR1", value1);
        m_body.addProperty("ADDR2", value2);
    }

    public void setRoadAddr (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("ROADFULLADDR", value);
    }

    public void setWheelChiarYN (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("WHEELCHIARYN", value);
    }

    public void setAddPhone (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("ADDPHONENUMBER", value);
    }

    public void setGuardian (String value1, String value2)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("GUARDIANNAME", value1);
        m_body.addProperty("GUARDIANPHONE", value2);
    }

    public void setUserPW (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("PW", value);
    }

    public void setCommunication (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("COMMUNICATIONYN", value);
    }

    public void setHelpType (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("HELPTYPE", value);
    }

    public void setAssistant (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("ASSISTANTYN", value);
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
