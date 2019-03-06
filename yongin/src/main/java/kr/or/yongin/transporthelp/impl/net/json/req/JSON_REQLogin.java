package kr.or.yongin.transporthelp.impl.net.json.req;

import com.google.gson.JsonObject;
import com.ionemax.iomlibrarys.log.Logview;

import kr.or.yongin.transporthelp.common.util.GlobalValues;
import kr.or.yongin.transporthelp.common.util.Utils;

/**
 * Created by IONEMAX on 2016-12-29.
 */

public class JSON_REQLogin extends JSON_REQHeader
{
    private final String THIS_TAG = "JSON_REQLogin";

    public static final String PAGE_NAME= "Login";
    public static final String PROTOCOL_VERSION = "1";

    private JsonObject m_body = null;

    public JSON_REQLogin (String id, String pw, String minno, String agreement)
    {
        super(PROTOCOL_VERSION, GlobalValues.SERVICE_CODE);

        // set Body
        setUserID(id);
        setUserPW(pw);
        setMinno(minno);

        setReagreement(agreement);
    }

    public void setUserID (String userid)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("ID", userid);
    }

    public void setUserPW (String userpw)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("PW", userpw);
    }

    public void setMinno (String minno)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("PHONENUMBER", minno);
    }

    public void setReagreement (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("REAGREEMENTCOMPLETE", value);
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
