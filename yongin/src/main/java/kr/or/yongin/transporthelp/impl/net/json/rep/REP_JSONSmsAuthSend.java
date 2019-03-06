package kr.or.yongin.transporthelp.impl.net.json.rep;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public class REP_JSONSmsAuthSend
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
        @SerializedName("AUTHLIMITFLAG")
        String authLimitFlag;

        public String getResult ()
        {
            return this.result;
        }

        public String getCause ()
        {
            return this.cause;
        }

        // 인증제한(횟수제한)
        public String getAuthLimitFlag () {return this.authLimitFlag;}
    }
}
