package kr.or.hsnarae.transporthelp.impl.net.json.req;

import com.google.gson.JsonObject;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public class JSON_REQHeader
{
    private JsonObject m_header = null;

    public JSON_REQHeader (String ver, String service)
    {
        setVersion(ver);
        setServiceKey(service);
    }

    /**
     * 프로토콜 버전
     * @param version
     */
    public void setVersion(String version)
    {
        if (m_header == null)
            m_header = new JsonObject();
        m_header.addProperty("VERSION", version);
    }

    /**
     * 서비스 키
     * @param key
     */
    public void setServiceKey (String key)
    {
        if (m_header == null)
            m_header = new JsonObject();
        m_header.addProperty("KEY", key);
    }

    public JsonObject getREQHeader()
    {
        return m_header;
    }
}

