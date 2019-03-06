package kr.or.hsnarae.transporthelp.impl.net.json.req;

import com.google.gson.JsonObject;
import com.ionemax.iomlibrarys.log.Logview;

import kr.or.hsnarae.transporthelp.common.util.GlobalValues;
import kr.or.hsnarae.transporthelp.common.util.Utils;
import kr.or.hsnarae.transporthelp.impl.net.json.rep.REP_JSONZipcode;

/**
 * Created by IONEMAX on 2016-12-29.
 */

public class JSON_REQRegistry extends JSON_REQHeader
{
    private final String THIS_TAG = "JSON_REQRegistry";

    public static final String PAGE_NAME= "RegMember";
    public static final String PROTOCOL_VERSION = "1";

    private JsonObject m_body = null;

    public JSON_REQRegistry(String minno, String authkey)
    {
        super(PROTOCOL_VERSION, GlobalValues.SERVICE_CODE);

        // set body
        setPhoneNumber (minno);
        setAuthKey (authkey);
    }

    public void setPhoneNumber (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("PHONENUMBER", value);
    }

    public void setAuthKey (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("AUTHKEY", value);
    }

    public void setName (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("NAME", value);
    }

    public void setEmail (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("EMAIL", value);
    }

    /**
     * 성별
     * 남성 : M , 여성 : F
     * @param value
     */
    public void setSex (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("SEX", value);
    }

    /**
     * 생일
     * YYYY-MM-DD
     * @param value
     */
    public void setBirth (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("BIRTH", value);
    }

    public void setAddress (REP_JSONZipcode.Address address, String detail)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("ZIPCODE", address.zipcode);
        m_body.addProperty("SIDO", address.sido);
        m_body.addProperty("SIGUGUNA", address.sigungunA);
        m_body.addProperty("SIGUGUNB", address.sigungunB);
        m_body.addProperty("DONG1", address.dong1);
        m_body.addProperty("DONG2", address.dong2);
        m_body.addProperty("ADDR1", address.addr1);
        m_body.addProperty("ADDR2", detail);
        m_body.addProperty("ROADFULLADDR", address.roadAddress + " " + detail);
    }

    public void setID (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("ID", value);
    }

    /**
     * 단말기에서 비밀번호를 SHA 256 암호화하여 서버로 보냄
     * @param value
     */
    public void setPW (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("PW", value);
    }

    public void setHandiTypeCode (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("HANDITYPECODE", value);
    }

    public void setHandiDegreeCode (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("HANDIDEGREECODE", value);
    }

    /**
     * 자동: A, 수동 : M, 미사용:N
     * @param value
     */
    public void setWheelchaiar (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("WHEELCHIARYN", value);
    }

    public void setAddPhoneNumber (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("ADDPHONENUMBER", value);
    }

    public void setGuardianName (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("GUARDIANNAME", value);
    }

    public void setGuardianPhone (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("GUARDIANPHONE", value);
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

    /**
     * 이미지 데이터. 멀티파트로 전송.
     * @param value
     */
    public void setWelFarefile (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("WELFAREFILE", value);
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
