package kr.or.yongin.transporthelp.impl.net.json.rep;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public class REP_JSONReqCallCancel
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
        @SerializedName("CANCELTYPE")
        String candeltype;

        public String getResult ()
        {
            return this.result;
        }
        public String getCause ()
        {
            return this.cause;
        }

        public String getCallid () {return this.callid;}
        // D 기사 취소, P 승객취소, M 관리자
        public String getCandeltype () {return this.candeltype;}
    }
}
