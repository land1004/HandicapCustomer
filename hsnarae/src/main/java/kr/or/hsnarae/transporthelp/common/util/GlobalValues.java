package kr.or.hsnarae.transporthelp.common.util;

/**
 * Created by IONEMAX on 2016-12-29.
 */

public class GlobalValues
{
    public static String SERVICE_CODE = "HTHSCA";    // 화성교통약자 서비스 코드

    // 콜의 진행 상태(R 접수, A 배차, T 승차, G 하차, S 완료, C 취소, W 대기, F 실패)
    public static String CALL_STATE_ORDER = "R";
    public static String CALL_STATE_ALLOC="A";
    public static String CALL_STATE_GETON="T";
    public static String CALL_STATE_GETOFF="G";
    public static String CALL_STATE_SUCCESS="S";
    public static String CALL_STATE_CANCEL="C";
    public static String CALL_STATE_CUSTOMER_CANCEL="P";
    public static String CALL_STATE_WAIT = "W";
    public static String CALL_STATE_FAILED="F";

    // 배차대기
    public static String ORDER_WAIT_VIEW_ORDER="order_view";
    public static String ORDER_WAIT_VIEW_FAILED="failed_view";
    public static String ORDER_WAIT_VIEW_CANCLED="cancel_view";


    // 정보 화면처리
    public static String INFO_VIEW_USE_NOTICE = "use_notice";           // 이용안내
    public static String INFO_VIEW_SERVICE_NOTICE = "service_notice";   // 서비스문의
    public static String INFO_VIEW_VERSION_NOTICE = "version_notice";   // 버전정보

    // Main service start
    public static String ACTION_MAIN_SERVICE = "kr.or.hsnarae.transporthelp.service.MAINSERVICEPROCESS";

    // Broadcast receiver
    public static String ACTION_BROADCAST_SERVER= "kr.or.hsnarae.transporthelp.service.BROADCAST";
    public static String ACTION_BROADCAST_PUSH= "kr.or.hsnarae.transporthelp.service.PUSH";
}
