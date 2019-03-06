package kr.or.yongin.transporthelp.impl.net.json.req;

import com.google.gson.JsonObject;
import com.ionemax.iomlibrarys.log.Logview;

import kr.or.yongin.transporthelp.common.util.GlobalValues;
import kr.or.yongin.transporthelp.common.util.Utils;

/**
 * Created by IONEMAX on 2016-12-29.
 */

public class JSON_REQSmsAuthSend extends JSON_REQHeader
{
    private final String THIS_TAG = "JSON_REQSmsAuthSend";

    public static final String PAGE_NAME= "SmsAuthSend";
    public static final String PROTOCOL_VERSION = "1";

    private JsonObject m_body = null;

    public JSON_REQSmsAuthSend(String phonenmuber, String authtype, String pushkey)
    {
        super(PROTOCOL_VERSION, GlobalValues.SERVICE_CODE);

        // set Body
        setPhonenumber(phonenmuber);
        setAuthType(authtype);
        setPushKey(pushkey);
    }

    public void setPhonenumber (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("PHONENUMBER", value);
    }

    public void setAuthType (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("AUTHTYPE", value);
    }

    public void setPushKey (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("PUSHKEY", value);
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
