package kr.or.hsnarae.transporthelp.impl.net.json.req;

import com.google.gson.JsonObject;
import com.ionemax.iomlibrarys.log.Logview;

import kr.or.hsnarae.transporthelp.common.util.GlobalValues;
import kr.or.hsnarae.transporthelp.common.util.Utils;

/**
 * Created by IONEMAX on 2016-12-29.
 */

public class JSON_REQUserRegistry extends JSON_REQHeader
{
    private final String THIS_TAG = "JSON_REQUserRegistry";

    public static final String PAGE_NAME= "RegMember";
    public static final String PROTOCOL_VERSION = "1";

    private JsonObject m_body = null;

    public JSON_REQUserRegistry()
    {
        super(PROTOCOL_VERSION, GlobalValues.SERVICE_CODE);
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

    public void setUserName (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("NAME", value);
    }

    public void setUserEmail (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("EMAIL", value);
    }

    public void setUserSex (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("SEX", value);
    }

    public void setUserBirth (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("BIRTH", value);
    }

    public void setPoiName (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("POINAME", value);
    }

    public void setRoadFullAddress (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("ROADFULLADDR", value);
    }

    public void setJibunFullAddress (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("JIBUNFULLADDR", value);
    }

    public void setSiDo (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("SIDO", value);
    }

    public void setSiGuGun (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("SIGUGUN", value);
    }

    public void setAdminDong (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("ADMINDONG", value);
    }

    public void setAdminDongCode (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("ADMINDONGCODE", value);
    }

    public void setLegalDong (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("LEGALDONG", value);
    }

    public void setLegalDongCode (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("LEGALDONGCODE", value);
    }

    public void setRi (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("RI", value);
    }

    public void setRoadName (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("ROADNAME", value);
    }

    public void setBuildingIndex (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("BUILDINGINDEX", value);
    }

    public void setBuildingName (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("BUILDINGNAME", value);
    }

    public void setRoadCode (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("ROADCODE", value);
    }

    public void setBunJi (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("BUNJI", value);
    }

    public void setDetailAddress (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("DETAILADDRESS", value);
    }

    public void setZipcode (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("ZIPCODE", value);
    }

    public void setUserID (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("ID", value);
    }

    public void setUserPW (String value)
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

    public void setWheelchiarYN (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("WHEELCHIARYN", value);
    }

    public void setAddPhonenumber (String value)
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

    /* 복지사본 전송
    public void setWelFareFile (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("WELFAREFILE", value);
    }
    */


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
