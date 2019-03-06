package kr.or.yongin.transporthelp.impl.net.json.rep;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public class REP_JSONCallAvailableTime
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

        @SerializedName("REQCALLlAVAILABLESTRTDATE")
        String startDate;
        @SerializedName("REQCALLlAVAILABLESTRTTIME")
        String startTime;
        @SerializedName("REQCALLlAVAILABLEENDDATE")
        String endDate;
        @SerializedName("REQCALLlAVAILABLEENDTIME")
        String endTime;
        @SerializedName("RSRVREQCALLlAVAILABLESTRTDATE")
        String reservedStartDate;
        @SerializedName("RSRVREQCALLlAVAILABLESTRTTIME")
        String reservedStartTime;
        @SerializedName("RSRVREQCALLlAVAILABLEENDDATE")
        String reservedEndDate;
        @SerializedName("RSRVREQCALLlAVAILABLEENDTIME")
        String reservedEndTime;

        public String getResult ()
        {
            return this.result;
        }
        public String getCause ()
        {
            return this.cause;
        }

        //즉시 배차 이용가능 시작 일
        public String getStartDate() {return this.startDate;}
        //즉시 배차 이용가능 시작 시간
        public String getStartTime() {return this.startTime;}
        //즉시 배차 이용가능 마지막 일
        public String getEndDate() {return this.endDate;}
        //즉시 배차 이용가능 마지막 시간
        public String getEndTime() {return this.endTime;}

        // 예약배차 이용가능 시작 일
        public String getReservedStartDate() {return this.reservedStartDate;}
        // 예약배차 이용가능 시작 시간
        public String getReservedStartTime() {return this.reservedStartTime;}
        // 예약배차 이용가능 마지막 일
        public String getReservedEndDate() {return this.reservedEndDate;}
        // 예약배차 이용가능 마지막 시간
        public String getReservedEndTime() {return this.reservedEndTime;}
    }
}
