package kr.or.hsnarae.transporthelp.impl.net.json.rep;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public class REP_JSONCallInfo
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

        @SerializedName("STARTDETAILADDRESS")
        String startDetail;
        @SerializedName("ENDDETAILADDRESS")
        String endDetail;
        @SerializedName("TCTIME")
        String tcTime;
        @SerializedName("OFFCARTIME")
        String offTime;
        @SerializedName("TCRESERVEDTIME")
        String reservedTime;
        @SerializedName("CANCELTIME")
        String cancelTime;
        @SerializedName("COMPANION")
        String companion;
        @SerializedName("USETYPECODE")
        String useTypeCode;
        @SerializedName("USETYPENAME")
        String useTypeName;
        @SerializedName("STARTPOSX")
        String strarPosX;
        @SerializedName("STARTPOSY")
        String startPosY;
        @SerializedName("ENDPOSX")
        String endPosX;
        @SerializedName("ENDPOSY")
        String endPosY;
        @SerializedName("WHEELCHIARYN")
        String wheelchaiarYN;
        @SerializedName("CALLSTATE")
        String callState;
        @SerializedName("DRVSEQ")
        String drvSEQ;
        @SerializedName("DRVNAME")
        String drvName;
        @SerializedName("DRVMINNO")
        String drvMinno;
        @SerializedName("CARNUMBER")
        String carNumber;
        @SerializedName("INTERVALTIME")
        String intervalTime;
        @SerializedName("CANCELUSER")
        String call_canceltype;

        public String getResult ()
        {
            return this.result;
        }
        public String getCause ()
        {
            return this.cause;
        }

        public String getStartDetail() {return this.startDetail;}
        public String getEndDetail() {return this.endDetail;}
        //출발시간
        public String getTcTime() {return this.tcTime;}
        //도착시간
        public String getOffTime() {return this.offTime;}
        //예약시간
        public String getReservedTime() {return this.reservedTime;}
        //취소시간
        public String getCancelTime() {return this.cancelTime;}
        public String getCompanion() {return this.companion;}
        public String getUseTypeCode() {return this.useTypeCode;}
        public String getStrarPosX() {return this.strarPosX;}
        public String getStartPosY() {return this.startPosY;}
        public String getEndPosX() {return this.endPosX;}
        public String getEndPosY() {return this.endPosY;}
        //사용:Y , 미사용:N
        public String getWheelchaiarYN() {return this.wheelchaiarYN;}
        //콜의 진행 상태(R 접수, A 배차, T 승차, G 하차, S 완료, C 취소, W 대기, F 실패)
        public String getCallState() {return this.callState;}

        public String getDrvSEQ() {return this.drvSEQ;}
        public String getDrvName() {return this.drvName;}
        public String getDrvMinno() {return this.drvMinno;}
        public String getCarNumber() {return this.carNumber;}
        //다음번 풀링 요청을 서버로 보낼 시간. 값이 없을 경우 30초(CarPos를 호출하는 주기값임)
        public String getIntervalTime() {return this.intervalTime;}
        public String getUseTypeName () {return this.useTypeName;}
        //시스템: S, 사용자 : U
        public String getCall_canceltype () {return this.call_canceltype;}
    }
}
