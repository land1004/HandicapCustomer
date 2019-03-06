package kr.or.yongin.transporthelp.impl.net.json.rep;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public class REP_JSONUseTypeList2
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

        // 이용목적 리스트
        @SerializedName("USETYPELIST")
        public List<UseType> useTypes = new ArrayList<>();

        public String getResult ()
        {
            return this.result;
        }

        public String getCause ()
        {
            return this.cause;
        }

        public List<UseType> getUseTypes () {return this.useTypes;}


        public class UseType
        {
            @SerializedName("USETYPE")
            String usetype;
            @SerializedName("USETYPECODE")
            String usetypecode;
            @SerializedName("REQCALLlAVAILABLESTRTDATE")
            String realStartDate;
            @SerializedName("REQCALLlAVAILABLEENDDATE")
            String realEndDate;
            @SerializedName("REQCALLlAVAILABLESTRTTIME")
            String realStartTime;
            @SerializedName("REQCALLlAVAILABLEENDTIME")
            String realEndTime;
            @SerializedName("RSRVREQCALLlAVAILABLESTRTDATE")
            String bookingStartDate;
            @SerializedName("RSRVREQCALLlAVAILABLEENDDATE")
            String bookingEndDate;
            @SerializedName("RSRVREQCALLlAVAILABLESTRTTIME")
            String bookingStartTime;
            @SerializedName("RSRVREQCALLlAVAILABLEENDTIME")
            String bookingEndTime;
            @SerializedName("ROUNDAVAILABLEYN")
            String roundAvilable;

            public String getUsetype () {return this.usetype;}

            public String getUsetypecode () {return this.usetypecode;}

            //즉시배차이용가능시작일
            public String getRealStartDate () {return this.realStartDate;}
            //즉시배차이용가능마지막일
            public String getRealEndDate() {return this.realEndDate;}
            //즉시배차이용가능시작시간
            public String getRealStartTime() {return this.realStartTime;}
            //즉시배차이용가능마지막시간
            public String getRealEndTime() {return this.realEndTime;}

            //예약배차이용가능시작일
            public String getBookingStartDate() {return this.bookingStartDate;}
            //예약배차이용가능마지막일
            public String getBookingEndDate() {return this.bookingEndDate;}
            //예약배차이용가능시작시간
            public String getBookingStartTime() {return this.bookingStartTime;}
            //예약배차이용가능마지막시간
            public String getBookingEndTime() {return this.bookingEndTime;}
            //왕복이용가능여부
            public String getRoundAvilable() {return this.roundAvilable;}
        }
    }
}
