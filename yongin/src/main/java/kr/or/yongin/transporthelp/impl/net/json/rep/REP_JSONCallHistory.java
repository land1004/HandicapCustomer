package kr.or.yongin.transporthelp.impl.net.json.rep;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public class REP_JSONCallHistory
{
    @SerializedName("BODY")
    Body body;
    public Body getBody() {return this.body;}

    public class Body
    {
        @SerializedName("RESULT")
        String result;
        @SerializedName("CAUSE")
        String cause;
        @SerializedName("CALLLIST")
        List<CallInfoItem> callHistory;

        public String getResult() {
            return this.result;
        }
        public String getCause() {
            return this.cause;
        }
        public List<CallInfoItem> getCallHistory () {return this.callHistory;}

        public class CallInfoItem
        {
            @SerializedName("CALLSTORAGESTATE")
            String callTotalState;
            @SerializedName("STARTDETAILADDRESS")
            String startDetail;
            @SerializedName("ENDDETAILADDRESS")
            String endDetail;
            @SerializedName("BOARDDATE")
            String boardDate;
            @SerializedName("TCTIME")
            String tcTime;
            @SerializedName("OFFCARTIME")
            String offTime;
            @SerializedName("CALLSTATE")
            String callState;
            @SerializedName("DRVNAME")
            String drvName;
            @SerializedName("CARNUMBER")
            String carNumber;
            @SerializedName("CALLID")
            String callid;
            @SerializedName("BOOKINGTIME")
            String bookingtime;


            public String getCallTotalState () {return this.callTotalState;}
            public String getStartDetail() {
                return this.startDetail;
            }
            public String getEndDetail() {
                return this.endDetail;
            }
            //탑승날짜 - 탑승날짜, 표시 형식 예)  2016.10.01
            public String getBoardDate () {return this.boardDate;}
            //출발시간 - 출발시간(승차시간) 표시 형식 예) 22:20
            public String getTcTime() {
                return this.tcTime;
            }
            //도착시간 - 도착시간(하차시간) 표시 형식 예) 22:20
            public String getOffTime() {
                return this.offTime;
            }
            //콜의 진행 상태(R 접수, A 배차, T 승차, G 하차, S 완료, C 취소, W 대기, F 실패)
            public String getCallState() {
                return this.callState;
            }
            public String getDrvName() {
                return this.drvName;
            }
            public String getCarNumber() {
                return this.carNumber;
            }
            public String getCallid () {return this.callid;}
            public String getBookingtime () {return this.bookingtime;}
        }
    }
}
