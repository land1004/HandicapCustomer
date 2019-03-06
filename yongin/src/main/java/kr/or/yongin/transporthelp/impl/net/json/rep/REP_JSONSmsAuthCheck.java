package kr.or.yongin.transporthelp.impl.net.json.rep;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public class REP_JSONSmsAuthCheck
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
        @SerializedName("AUTHKEY")
        String authKey;
        @SerializedName("EXISTCTMYN")
        String existCtmYN;

        public String getResult ()
        {
            return this.result;
        }

        public String getCause ()
        {
            return this.cause;
        }

        // 로그인 인증키
        public String getAuthKey () {return this.authKey;}

        // 기존고객여부 확인, 기존고객임: Y, 신규고객임: N
        public String getExistCtmYN () {return this.existCtmYN;}
    }
}
