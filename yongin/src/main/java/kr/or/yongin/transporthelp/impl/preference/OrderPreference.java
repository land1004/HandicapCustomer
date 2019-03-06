package kr.or.yongin.transporthelp.impl.preference;

import android.content.Context;

public class OrderPreference extends BaseSharedPreference
{
	private static OrderPreference mOrderPreference = new OrderPreference();
	
	private final String ORDER_PREFERENCE = "kr.or.yongin.transporthelp.preference.order";


	public static final String ORDER_TYPE = "kr.or.yongin.transporthelp.preference.order.type";

	public static final String ORDER_FROM_SELECT = "kr.or.yongin.transporthelp.preference.order.from_select";
	public static final String ORDER_FROM_DETAIL = "kr.or.yongin.transporthelp.preference.order.from_detail";
	public static final String ORDER_FROM_ADDR = "kr.or.yongin.transporthelp.preference.order.from_addr";
	public static final String ORDER_FROM_X= "kr.or.yongin.transporthelp.preference.order.from_x";
	public static final String ORDER_FROM_Y= "kr.or.yongin.transporthelp.preference.order.from_y";

	public static final String ORDER_TO_SELECT = "kr.or.yongin.transporthelp.preference.order.to_select";
	public static final String ORDER_TO_DETAIL = "kr.or.yongin.transporthelp.preference.order.to_detail";
	public static final String ORDER_TO_ADDR = "kr.or.yongin.transporthelp.preference.order.to_addr";
	public static final String ORDER_TO_X= "kr.or.v.transporthelp.preference.order.to_x";
	public static final String ORDER_TO_Y= "kr.or.yongin.transporthelp.preference.order.to_y";

	public static final String ORDER_DISTANCE = "kr.or.yongin.transporthelp.preference.order.distance";
	public static final String ORDER_GETON_COUNT = "kr.or.yongin.transporthelp.preference.order.geton_count";
	public static final String ORDER_USE_CAUSE = "kr.or.yongin.transporthelp.preference.order.use_cause";
	public static final String ORDER_BOOKING_DATE= "kr.or.yongin.transporthelp.preference.order.booking_date";
	public static final String ORDER_BOOKING_DATE_SEND= "kr.or.yongin.transporthelp.preference.order.booking_date_send";
	public static final String ORDER_BOOKING_TIME= "kr.or.yongin.transporthelp.preference.order.booking_time";
	public static final String ORDER_BOOKING_TIME_SEND= "kr.or.yongin.transporthelp.preference.order.booking_time_send";
	public static final String ORDER_USE_WHEEL= "kr.or.yongin.transporthelp.preference.order.use_wheel";
	public static final String ORDER_BOOKING_TURN= "kr.or.yongin.transporthelp.preference.order.booking_turn";
	public static final String ORDER_BOOKING_TURN_DATE= "kr.or.yongin.transporthelp.preference.order.booking_turn_date";
	public static final String ORDER_BOOKING_TURN_DATE_SEND= "kr.or.yongin.transporthelp.preference.order.booking_turn_date_send";
	public static final String ORDER_BOOKING_TURN_TIME= "kr.or.yongin.transporthelp.preference.order.booking_turn_time";
	public static final String ORDER_BOOKING_TURN_TIME_SEND= "kr.or.yongin.transporthelp.preference.order.booking_turn_time_send";

	public static final String ORDER_BOOKING_AVAILABLE_STIME= "kr.or.yongin.transporthelp.preference.order.booking_available_stime";
	public static final String ORDER_BOOKING_AVAILABLE_ETIME= "kr.or.yongin.transporthelp.preference.order.booking_available_etime";

	public static final String ORDER_STATE= "kr.or.yongin.transporthelp.preference.order.state";
	public static final String ORDER_CALLID= "kr.or.yongin.transporthelp.preference.order.callid";

	public static final String ORDER_DRIVER_NAME= "kr.or.yongin.transporthelp.preference.order.driver_name";
	public static final String ORDER_DRIVER_SEQ= "kr.or.yongin.transporthelp.preference.order.driver_seq";
	public static final String ORDER_DRIVER_PHONE= "kr.or.yongin.transporthelp.preference.order.driver_phone";
	public static final String ORDER_DRIVER_CAR= "kr.or.yongin.transporthelp.preference.order.driver_car";


	private OrderPreference()
	{
		
	}
	
	public static OrderPreference getInstance()
	{
		if (mOrderPreference == null )
			mOrderPreference = new OrderPreference();

		return mOrderPreference;
	}
	
	@Override
	public void setPreference(Context context)
	{
		setPreference (context, ORDER_PREFERENCE);
	}

	/**
	 * 접수 타입 (S:즉시, B:예약)
	 * @param value
     */
	public void setOrderType(String value)
	{
		put(ORDER_TYPE, value);
	}
	public String getOrderType()
	{
		return getValue(ORDER_TYPE, "");
	}

	/**
	 * 출발지 설정
	 * @param value
     */
	public void setOrderFromSelect(boolean value)
	{
		put(ORDER_FROM_SELECT, value);
	}
	public boolean getOrderFromSelect()
	{
		return getValue(ORDER_FROM_SELECT, false);
	}

	/**
	 * 접수 출발지 상세
	 * @param value :
     */
	public void setOrderFromDetail(String value)
	{
		put(ORDER_FROM_DETAIL, value);
	}
	public String getOrderFromDetail()
	{
		return getValue(ORDER_FROM_DETAIL, "");
	}

	/**
	 * 접수 출발지 주소
	 * @param value :
     */
	public void setOrderFromAddr(String value)
	{
		put(ORDER_FROM_ADDR, value);
	}
	public String getOrderFromAddr()
	{
		return getValue(ORDER_FROM_ADDR, "");
	}

	/**
	 * 도착지 설정
	 * @param value
     */
	public void setOrderToSelect(boolean value)
	{
		put(ORDER_TO_SELECT, value);
	}
	public boolean getOrderToSelect()
	{
		return getValue(ORDER_TO_SELECT, false);
	}

	/**
	 * 접수 도착지 상세
	 * @param value
     */
	public void setOrderToDetail(String value)
	{
		put(ORDER_TO_DETAIL, value);
	}
	public String getOrderToDetail()
	{
		return getValue(ORDER_TO_DETAIL, "");
	}

	/**
	 * 접수 도착지 주소
	 * @param value
     */
	public void setOrderToAddr(String value)
	{
		put(ORDER_TO_ADDR, value);
	}
	public String getOrderToAddr()
	{
		return getValue(ORDER_TO_ADDR, "");
	}

	/**
	 * 출발지에서 도착지까지 직선 거리
	 * @param value
     */
	public void setOrderDistance(float value)
	{
		put(ORDER_DISTANCE, value);
	}
	public float getOrderDistance()
	{
		return getValue(ORDER_DISTANCE, 0.0f);
	}

	/**
	 * 승차인원 (기본 0)
	 * @param value
     */
	public void setOrderGetonCount(int value)
	{
		put(ORDER_GETON_COUNT, value);
	}
	public int getOrderGetonCount()
	{
		return getValue(ORDER_GETON_COUNT, 0);
	}

	/**
	 * 이용목적
	 * @param value
     */
	public void setOrderUseCause(int value)
	{
		put(ORDER_USE_CAUSE, value);
	}
	public int getOrderuseCause()
	{
		return getValue(ORDER_USE_CAUSE, 0);
	}

	/**
	 * 예약날짜
	 * @param value
     */
	public void setOrderBookingDate(String value)
	{
		put(ORDER_BOOKING_DATE, value);
	}
	public String getOrderBookingDate()
	{
		return getValue(ORDER_BOOKING_DATE, "");
	}

	public void setOrderBookingDateSend(String value)
	{
		put(ORDER_BOOKING_DATE_SEND, value);
	}
	public String getOrderBookingDateSend()
	{
		return getValue(ORDER_BOOKING_DATE_SEND, "");
	}

	/**
	 * 예약시간
	 * @param value
     */
	public void setOrderBookingTime(String value)
	{
		put(ORDER_BOOKING_TIME, value);
	}
	public String getOrderBookingTime()
	{
		return getValue(ORDER_BOOKING_TIME, "");
	}

	public void setOrderBookingTimeSend(String value)
	{
		put(ORDER_BOOKING_TIME_SEND, value);
	}
	public String getOrderBookingTimeSend()
	{
		return getValue(ORDER_BOOKING_TIME_SEND, "");
	}

	/**
	 * 휠체어 사용여부
	 * @param value
     */
	public void setOrderUseWheel(boolean value)
	{
		put(ORDER_USE_WHEEL, value);
	}
	public boolean getOrderUseWheel()
	{
		return getValue(ORDER_USE_WHEEL, false);
	}

	/**
	 * 왕복여부
	 * @param value
     */
	public void setOrderBookingTurn(boolean value)
	{
		put(ORDER_BOOKING_TURN, value);
	}
	public boolean getOrderBookingTurn()
	{
		return getValue(ORDER_BOOKING_TURN, false);
	}

	/**
	 * 예약 왕복 일자
	 * @param value
     */
	public void setOrderBookingTurnDate(String value)
	{
		put(ORDER_BOOKING_TURN_DATE, value);
	}
	public String getOrderBookingTurnDate()
	{
		return getValue(ORDER_BOOKING_TURN_DATE, "");
	}

	public void setOrderBookingTurnDateSend(String value)
	{
		put(ORDER_BOOKING_TURN_DATE_SEND, value);
	}
	public String getOrderBookingTurnDateSend()
	{
		return getValue(ORDER_BOOKING_TURN_DATE_SEND, "");
	}

	/**
	 * 예약 왕복 시간
	 * @param value
     */
	public void setOrderBookingTurnTime(String value)
	{
		put(ORDER_BOOKING_TURN_TIME, value);
	}
	public String getOrderBookingTurnTime()
	{
		return getValue(ORDER_BOOKING_TURN_TIME, "");
	}

	public void setOrderBookingTurnTimeSend(String value)
	{
		put(ORDER_BOOKING_TURN_TIME_SEND, value);
	}
	public String getOrderBookingTurnTimeSend()
	{
		return getValue(ORDER_BOOKING_TURN_TIME_SEND, "");
	}

	/**
	 * 출발지 좌표
	 * x (latitude)  - Naver longitude
	 * y (longitude) - naver latitude
	 * @param value
     */
	public void setOrderFromX(double value)
	{
		put(ORDER_FROM_X, value);
	}
	public double getOrderFromX()
	{
		return getValue(ORDER_FROM_X, 0.0f);
	}

	public void setOrderFromY(double value)
	{
		put(ORDER_FROM_Y, value);
	}
	public double getOrderFromY()
	{
		return getValue(ORDER_FROM_Y, 0.0f);
	}

	/**
	 * 도착지 좌표
	 * @param value
     */
	public void setOrderToX(double value)
	{
		put(ORDER_TO_X, value);
	}
	public double getOrderToX()
	{
		return getValue(ORDER_TO_X, 0.0f);
	}

	public void setOrderToY(double value)
	{
		put(ORDER_TO_Y, value);
	}
	public double getOrderToY()
	{
		return getValue(ORDER_TO_Y, 0.0);
	}

	/**
	 * 접수 상태
	 * @param value
     */
	public void setOrderState(String value)
	{
		put(ORDER_STATE, value);
	}
	public String getOrderState()
	{
		return getValue(ORDER_STATE, "");
	}

	/**
	 * 배차 콜 아이디
	 * @param value
     */
	public void setOrderCallid(String value)
	{
		put(ORDER_CALLID, value);
	}
	public String getOrderCallid()
	{
		return getValue(ORDER_CALLID, "");
	}

	/**
	 * 기사명
	 * @param value
     */
	public void setOrderDriverName(String value)
	{
		put(ORDER_DRIVER_NAME, value);
	}
	public String getOrderDriverName()
	{
		return getValue(ORDER_DRIVER_NAME, "");
	}

	/**
	 * 기사 SEQ
	 * @param value
     */
	public void setOrderDriverSeq(String value)
	{
		put(ORDER_DRIVER_SEQ, value);
	}
	public String getOrderDriverSeq()
	{
		return getValue(ORDER_DRIVER_SEQ, "");
	}

	/**
	 * 기사전화번호
	 * @param value
     */
	public void setOrderDriverPhone(String value)
	{
		put(ORDER_DRIVER_PHONE, value);
	}
	public String getOrderDriverPhone()
	{
		return getValue(ORDER_DRIVER_PHONE, "");
	}

	/**
	 * 차량번호
	 * @param value
     */
	public void setOrderDriverCar(String value)
	{
		put(ORDER_DRIVER_CAR, value);
	}
	public String getOrderDriverCar()
	{
		return getValue(ORDER_DRIVER_CAR, "");
	}

	/**
	 * 예약배차 접수가능 시작시간
	 * @param value
	 */
	public void setOrderBookingAvailableStime(int value)
	{
		put(ORDER_BOOKING_AVAILABLE_STIME, value);
	}
	public int getOrderBookingAvailableStime()
	{
		return getValue(ORDER_BOOKING_AVAILABLE_STIME, 9);
	}

	/**
	 * 예약배차 접수가능 종료시간
	 * @param value
	 */
	public void setOrderBookingAvailableEtime(int value)
	{
		put(ORDER_BOOKING_AVAILABLE_ETIME, value);
	}
	public int getOrderBookingAvailableEtime()
	{
		return getValue(ORDER_BOOKING_AVAILABLE_ETIME, 21);
	}

}
