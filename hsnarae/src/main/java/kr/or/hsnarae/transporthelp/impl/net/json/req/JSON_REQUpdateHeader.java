package kr.or.hsnarae.transporthelp.impl.net.json.req;

import com.google.gson.JsonObject;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public class JSON_REQUpdateHeader
{
    private JsonObject m_header = null;

    public JSON_REQUpdateHeader(String ver, String service, String minno)
    {
        setVersion(ver);
        setServiceKey(service);
        setTargetSystem ("A");
        setMinno(minno);
        setSerialno ("");
    }

    /**
     * 프로토콜 버전
     * @param version
     */
    public void setVersion(String value)
    {
        if (m_header == null)
            m_header = new JsonObject();
        m_header.addProperty("VERSION", value);
    }

    /**
     * 서비스 키
     * @param key
     */
    public void setServiceKey (String value)
    {
        if (m_header == null)
            m_header = new JsonObject();
        m_header.addProperty("SERVICE", value);
    }

    public void setTargetSystem (String value)
    {
        if (m_header == null)
            m_header = new JsonObject();
        m_header.addProperty("TARGETSYSTEM", value);
    }

    public void setMinno (String value)
    {
        if (m_header == null)
            m_header = new JsonObject();
        m_header.addProperty("MINNO", value);
    }

    public void setSerialno (String value)
    {
        if (m_header == null)
            m_header = new JsonObject();
        m_header.addProperty("SERIALNO", value);
    }

    public JsonObject getREQHeader()
    {
        return m_header;
    }
}

