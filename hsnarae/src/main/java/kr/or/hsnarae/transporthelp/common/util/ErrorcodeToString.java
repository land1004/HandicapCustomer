package kr.or.hsnarae.transporthelp.common.util;

import java.util.HashMap;

/**
 * Created by IONEMAX on 2017-01-17.
 */

public class ErrorcodeToString
{
    private static HashMap<String, String>mErrorCode = null;
    public ErrorcodeToString ()
    {
    }

    /**
     * 에러 스트링 리턴
     * @param code
     * @param error_string
     * @return
     */
    public static String getError (String code, String error_string)
    {
        String ret = "";
        if (code.trim().toLowerCase().equalsIgnoreCase("f7000"))
            ret = error_string;
        else {
            if (mErrorCode == null)
                setErrorCode ();

            ret = mErrorCode.get(code);
        }

        return ret;
    }

    /**
     * 에러코드 설정
     */
    public static void setErrorCode ()
    {
        if (mErrorCode == null)
            mErrorCode = new HashMap<String, String>();
        mErrorCode.clear();

        //////////////////////////////////////////////
        mErrorCode.put("F0000", "실패 : 논리적인 실패 (로그인시 없는 id입니다 등)");
        mErrorCode.put("F1000", "인증관련 조회실패");
        mErrorCode.put("F1001", "인증요청에 실패했습니다");
        mErrorCode.put("F1002", "존재하지 않는 아이디입니다");
        mErrorCode.put("F1003", "존재하지 않는 전화번호입니다 ");
        mErrorCode.put("F1004", "비밀번호가 틀립니다");
        mErrorCode.put("F1005", "유효하지 않은 인증키입니다.");
        mErrorCode.put("F1006", "가입신청 진행중입니다.");
        mErrorCode.put("F1007", "회원가입 승인이 거부되었습니다.");
        mErrorCode.put("F1008", "아이디 또는 비밀번호가 올바르지 않습니다.");
        mErrorCode.put("F1009", "이미 등록되어 있는 번호입니다.");
        mErrorCode.put("F1010", "휴대폰 인증 제한 횟수가 초과하였습니다.");
        mErrorCode.put("F1011", "유효하지 않은 SMS 인증키입니다.");
        mErrorCode.put("F1012", "인증타입이 올바르지 않습니다.");
        mErrorCode.put("F1013", "배차실패-배차요청이 실패하였습니다.");
        mErrorCode.put("F1014", "배차실패-이미 배차되어 있는 시간입니다.");
        mErrorCode.put("F1014", "배차실패-이미 배차되어 있는 시간입니다.");
        mErrorCode.put("F1014", "배차실패-이미 배차되어 있는 시간입니다.");
        mErrorCode.put("F1015", "예약시간이 올바르지 않습니다. 예약은 현시점으로부터 24시간 이후부터 가능합니다.");
        mErrorCode.put("F1016", "해당 콜의 상태가 이미 변경되었습니다.");
        mErrorCode.put("F1017", "취소실패");
        mErrorCode.put("F1018", "콜상태가 배차상태가 아닙니다.");
        mErrorCode.put("F1019", "부득이한 사정으로 인하여 다시 배차 요청중입니다.");
        mErrorCode.put("F1020", "호출이력이 없습니다.");
        mErrorCode.put("F1021", "비정상적인 가입정보입니다.\n센터에 문의하세요.(웹DB에는 고객정보가 유효하나, 택시DB에 있는 고객정보가 유효하지 않은 경우)");
        mErrorCode.put("F1022", "가입된 전화번호와 정보가 다릅니다.");
        mErrorCode.put("F1023", "요청타입이 올바르지 않습니다.");
        mErrorCode.put("F1024", "가입된 정보가 없습니다.");
        mErrorCode.put("F1025", "비밀번호를 5회 잘못 입력하셨습니다. 5분 이후 다시 이용해주세요.");

        mErrorCode.put("F2000", "데이터 유효성 실패");
        mErrorCode.put("F2001", "숫자값에 문자가 있습니다");

        mErrorCode.put("F3000", "응답데이터 관련오류");
        mErrorCode.put("F3001", "응답데이터가 없음");

        mErrorCode.put("F4000", "콜관련 정보 에러");
        mErrorCode.put("F4001", "현재 진행중인콜이 있습니다");
        mErrorCode.put("F4002", "취소횟수초과 잠시후에 다시하세요");
        mErrorCode.put("F4005", "취소된 콜입니다");
        mErrorCode.put("F4006", "주변기사가 없어 배차에 실패했습니다");
        mErrorCode.put("F4008", "완료된 콜입니다");
        mErrorCode.put("F4009", "유효하지 않은 콜id입니다");

        mErrorCode.put("F7000", "서버에서 정의하는 에러 error_string을 그대로 표시 해 준다.");

        mErrorCode.put("E0000", "에러 : 시스템적인 에러 (db접속실패등)");
    }


}
