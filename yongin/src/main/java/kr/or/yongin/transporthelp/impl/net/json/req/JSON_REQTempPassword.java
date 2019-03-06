package kr.or.yongin.transporthelp.impl.net.json.req;

import com.google.gson.JsonObject;
import com.ionemax.iomlibrarys.log.Logview;

import kr.or.yongin.transporthelp.common.util.GlobalValues;
import kr.or.yongin.transporthelp.common.util.Utils;

/**
 * Created by IONEMAX on 2016-12-29.
 */

public class JSON_REQTempPassword extends JSON_REQHeader
{
    private final String THIS_TAG = "JSON_REQTempPassword";

    public static final String PAGE_NAME= "TempPassword";
    public static final String PROTOCOL_VERSION = "1";

    private JsonObject m_body = null;

    public JSON_REQTempPassword(String phonenum, String pushkey, String authkey, String reqType)
    {
        super(PROTOCOL_VERSION, GlobalValues.SERVICE_CODE);

        // set Body
        setPhonenum (phonenum);
        setPushkey (pushkey);
        setAuthkey (authkey);
        //요청타입- I: 아이디찾기, P: 비밀번호 찾기 요청
        setReqType (reqType);
    }

    public void setPhonenum (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("PHONENUMBER", value);
    }

    public void setPushkey (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("PUSHKEY", value);
    }

    public void setAuthkey (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("AUTHKEY", value);
    }

    public void setReqType (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("REQTYPE", value);
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
