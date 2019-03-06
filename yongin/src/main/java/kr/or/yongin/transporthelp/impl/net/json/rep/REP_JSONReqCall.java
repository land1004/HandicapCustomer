package kr.or.yongin.transporthelp.impl.net.json.rep;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public class REP_JSONReqCall
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
        @SerializedName("CALLID")
        String callid;
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

        public String getResult ()
        {
            return this.result;
        }
        public String getCause ()
        {
            return this.cause;
        }

        public String getCallid () {return this.callid;}
        //콜의 진행 상태(R 접수, A 배차, T 승차, G 하차, S 완료, C 취소, W 대기, F 실패)
        public String getCallState () {return this.callState;}
        public String getDrvSEQ() {return this.drvSEQ;}
        public String getDrvName() {return this.drvName;}
        public String getDrvMinno() {return this.drvMinno;}
        public String getCarNumber() {return this.carNumber;}
        //다음번 풀링 요청을 서버로 보낼 시간. 값이 없을 경우 30초(CarPos를 호출하는 주기값임)
        public String getIntervalTime() {return this.intervalTime;}

    }
}
