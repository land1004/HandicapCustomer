package kr.or.hsnarae.transporthelp.impl.net;

/**
 * Created by IONEMAX on 2017-01-18.
 */

public interface IPushMessage
{
    public static final String PUSH_NOTICE = "MN0";         // 공지사항
    public static final String PUSH_ALLOC_SUCCESS = "CS0";  // 배차성공
    public static final String PUSH_ALLOC_FAILED = "CF0";   // 배차실패
    public static final String PUSH_RESRVED_MOVE_S = "CM1"; // 예약배차 이동 시작 (시스템 자동)
    public static final String PUSH_RESRVED_MOVE_D = "CM2"; // 예약배차 이동 시작 (기사 요청)
    public static final String PUSH_GET_ON = "CT0";         // 승차

    public static final String PUSH_CALL_CANCEL = "CC1";    // 승객 취소
    public static final String PUSH_ALOC_CANCEL = "CC2";    // 기사 취소
    public static final String PUSH_ALOC_CANCEL2 = "CC3";   // 기사 요청 취소
}
