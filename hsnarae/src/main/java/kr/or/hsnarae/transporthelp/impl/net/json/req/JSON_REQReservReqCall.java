package kr.or.hsnarae.transporthelp.impl.net.json.req;

import com.google.gson.JsonObject;
import com.ionemax.iomlibrarys.log.Logview;

import kr.or.hsnarae.transporthelp.common.util.GlobalValues;
import kr.or.hsnarae.transporthelp.common.util.Utils;

/**
 * Created by IONEMAX on 2016-12-29.
 */

public class JSON_REQReservReqCall extends JSON_REQHeader
{
    private final String THIS_TAG = "JSON_REQReservReqCall";

    public static final String PAGE_NAME= "ReservReqCall";
    public static final String PROTOCOL_VERSION = "1";

    private JsonObject m_body = null;

    public JSON_REQReservReqCall(String authkey)
    {
        super(PROTOCOL_VERSION, GlobalValues.SERVICE_CODE);

        // set Body
        setAuthKey(authkey);
    }

    public void setAuthKey (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("AUTHKEY", value);
    }

    public void setStart (String detail, String x, String y)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("STARTDETAILADDRESS", detail);
        m_body.addProperty("STARTPOSX", x);
        m_body.addProperty("STARTPOSY", y);
    }

    public void setEnd (String detail, String x, String y)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("ENDDETAILADDRESS", detail);
        m_body.addProperty("ENDPOSX", x);
        m_body.addProperty("ENDPOSY", y);
    }

    /**
     * 동승인원
     * 숫자형식 (0,1,2)
     * @param value
     */
    public void setCompanion (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("COMPANION", value);
    }

    public void setUseTypeCode (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("USETYPECODE", value);
    }

    /**
     * 휠체어 사용여부 사용:Y , 미사용:N
     * @param value
     */
    public void setWheelchaiarYN (String value)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("WHEELCHIARYN", value);
    }

    /**
     * 예약날짜
     * 표시 예약날짜 예) 2016-10-01
     * 표시 예약시간 (24시간제로 구분한다) 예) 22:20 (분은 10분 단위로 선택할 수 있게 한다)
     * @param date
     * @param time
     */
    public void setStartDate(String date, String time)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("STARTDATE", date);
        m_body.addProperty("STARTTIME", time);
    }

    /**
     * 왕복여부 및 날짜
     * 왕복:Y , 왕복안함:N
     * 표시 예약날짜 예) 2016-10-01
     * 표시 예약시간 (24시간제로 구분한다) 예) 22:20 (분은 10분 단위로 선택할 수 있게 한다)
     * @param roundyn
     * @param date
     * @param time
     */
    public void setRoundDate (String roundyn, String date, String time)
    {
        if (m_body == null)
            m_body = new JsonObject();
        m_body.addProperty("ROUNDYN", roundyn);
        m_body.addProperty("ROUNDDATE", date);
        m_body.addProperty("ROUNDTIME", time);
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
