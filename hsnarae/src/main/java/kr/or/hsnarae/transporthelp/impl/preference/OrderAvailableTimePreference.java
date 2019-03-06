package kr.or.hsnarae.transporthelp.impl.preference;

import android.content.Context;

public class OrderAvailableTimePreference  extends BaseSharedPreference
{
    private static OrderAvailableTimePreference mOrderAvailablePreference = new OrderAvailableTimePreference();

    private final String ORDERABAILABLE_PREFERENCE = "kr.or.hsnarae.transporthelp.preference.orderavailabletime";

    public static final String ORDERABAILABLE_STARTDATE = "kr.or.hsnarae.transporthelp.preference.orderavailabletime.startdate";
    public static final String ORDERABAILABLE_STARTTIME = "kr.or.hsnarae.transporthelp.preference.orderavailabletime.starttime";
    public static final String ORDERABAILABLE_ENDDATE = "kr.or.hsnarae.transporthelp.preference.orderavailabletime.enddate";
    public static final String ORDERABAILABLE_ENDTIME = "kr.or.hsnarae.transporthelp.preference.orderavailabletime.endtime";

    public static final String ORDERABAILABLE_RESERVECD_STARTDATE = "kr.or.hsnarae.transporthelp.preference.orderavailabletime.reserved_startdate";
    public static final String ORDERABAILABLE_RESERVECD_STARTTIME = "kr.or.hsnarae.transporthelp.preference.orderavailabletime.reserved_starttime";
    public static final String ORDERABAILABLE_RESERVECD_ENDDATE = "kr.or.hsnarae.transporthelp.preference.orderavailabletime.reserved_enddate";
    public static final String ORDERABAILABLE_RESERVECD_ENDTIME = "kr.or.hsnarae.transporthelp.preference.orderavailabletime.reserved_endtime";

    private OrderAvailableTimePreference()
    {

    }

    public static OrderAvailableTimePreference getInstance()
    {
        if (mOrderAvailablePreference == null )
            mOrderAvailablePreference = new OrderAvailableTimePreference();

        return mOrderAvailablePreference;
    }

    @Override
    public void setPreference(Context context)
    {
        setPreference (context, ORDERABAILABLE_PREFERENCE);
    }

    /**
     * 즉시 배차가능 시작 날짜
     * @param value
     */
    public void setOrderabailableStartdate(String value) {
        put(ORDERABAILABLE_STARTDATE, value);
    }

    public String getOrderabailableStartdate () {
        return getValue(ORDERABAILABLE_STARTDATE, "");
    }

    /**
     * 즉시배차 가능 시작 시간
     * @param value
     */
    public void setOrderabailableStarttime(String value) {
        put(ORDERABAILABLE_STARTTIME, value);
    }

    public String getOrderabailableStarttime() {
        return getValue(ORDERABAILABLE_STARTTIME, "");
    }

    /**
     * 즉시배차 가능 종료 날짜
     * @param value
     */
    public void setOrderabailableEnddate(String value) {
        put(ORDERABAILABLE_ENDDATE, value);
    }

    public String getOrderabailableEnddate() {
        return getValue(ORDERABAILABLE_ENDDATE, "");
    }

    /**
     * 즉시배차 가능 종료 시간
     * @param value
     */
    public void setOrderabailableEndtime(String value) {
        put(ORDERABAILABLE_ENDTIME, value);
    }

    public String getOrderabailableEndtime () {
        return getValue(ORDERABAILABLE_ENDTIME, "");
    }

    /**
     * 예약가능 시작 날짜
     * @return
     */
    public void setOrderabailableReservecdStartdate(String value) {
        put(ORDERABAILABLE_RESERVECD_STARTDATE, value);
    }

    public String getOrderabailableReservecdStartdate() {
        return getValue(ORDERABAILABLE_RESERVECD_STARTDATE, "");
    }

    /**
     * 예약가능 시작 시간
     * @param value
     */
    public void setOrderabailableReservecdStarttime(String value){
        put(ORDERABAILABLE_RESERVECD_STARTTIME, value);
    }

    public String getOrderabailableReservecdStarttime() {
        return getValue(ORDERABAILABLE_RESERVECD_STARTTIME, "");
    }

    /**
     * 예약가능 종료 날짜
     * @param value
     */
    public void setOrderabailableReservecdEnddate(String value) {
        put(ORDERABAILABLE_RESERVECD_ENDDATE, value);
    }

    public String getOrderabailableReservecdEnddate() {
        return getValue(ORDERABAILABLE_RESERVECD_ENDDATE, "");
    }

    /**
     * 예약가능 종료 시간
     * @param value
     */
    public void setOrderabailableReservecdEndtime(String value) {
        put(ORDERABAILABLE_RESERVECD_ENDTIME, value);
    }

    public String getOrderabailableReservecdEndtime() {
        return getValue(ORDERABAILABLE_RESERVECD_ENDTIME, "");
    }
}
