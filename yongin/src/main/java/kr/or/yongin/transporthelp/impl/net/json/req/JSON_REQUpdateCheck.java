package kr.or.yongin.transporthelp.impl.net.json.req;

import com.google.gson.JsonObject;
import com.ionemax.iomlibrarys.log.Logview;

import kr.or.yongin.transporthelp.common.util.GlobalValues;
import kr.or.yongin.transporthelp.common.util.Utils;

/**
 * Created by IONEMAX on 2016-12-29.
 */

public class JSON_REQUpdateCheck extends JSON_REQUpdateHeader
{
    private final String THIS_TAG = "JSON_REQUpdateCheck";

    public static final String PAGE_NAME= "Check";
    public static final String PROTOCOL_VERSION = "1";

    private JsonObject m_body = null;

    public JSON_REQUpdateCheck(String appver, String minno)
    {
        super(PROTOCOL_VERSION, GlobalValues.SERVICE_CODE, minno);

        // set Body
        setAppVer (appver);
        serValue1 ("");
        serValue2 ("");
    }

    public void setAppVer (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("APPVER", value);
    }

    public void serValue1 (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("VALUE1", value);
    }

    public void serValue2 (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("VALUE2", value);
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
