package kr.or.yongin.transporthelp.impl.net.json.req;

import com.google.gson.JsonObject;
import com.ionemax.iomlibrarys.log.Logview;

import kr.or.yongin.transporthelp.common.util.GlobalValues;
import kr.or.yongin.transporthelp.common.util.Utils;

/**
 * Created by IONEMAX on 2016-12-29.
 */

public class JSON_REQFindRoadInfo extends JSON_REQHeader
{
    private final String THIS_TAG = "JSON_REQFindRoadInfo";

    public static final String PAGE_NAME= "FindRoadInfo";
    public static final String PROTOCOL_VERSION = "1";

    private JsonObject m_body = null;

    public JSON_REQFindRoadInfo(String authkey, String startx, String starty, String endx, String endy)
    {
        super(PROTOCOL_VERSION, GlobalValues.SERVICE_CODE);

        // set Body
        setAuthkey (authkey);
        setStartPos(startx, starty);
        setEndPos(endx, endy);
    }

    public void setAuthkey (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("AUTHKEY", value);
    }


    public void setStartPos (String value1, String value2)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("STARTPOSX", value1);
        m_body.addProperty("STARTPOSY", value2);
    }

    public void setEndPos (String value1, String value2)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("ENDPOSX", value1);
        m_body.addProperty("ENDPOSY", value2);
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
