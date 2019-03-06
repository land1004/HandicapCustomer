package kr.or.yongin.transporthelp.impl.net.json.rep;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public class REP_JSONFindRoadInfo
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
        @SerializedName("DPTIME")
        String dtTime;

        public String getResult ()
        {
            return this.result;
        }
        public String getCause ()
        {
            return this.cause;
        }
        //출발지 -> 도착지 예상소요시간 (분)
        public String getDtTime () {return this.dtTime;}
    }
}
