package kr.or.hsnarae.transporthelp.impl.net.json.rep;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public class REP_JSONIntro
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
        @SerializedName("UPDATEURL")
        String updateUrl;
        @SerializedName("AUTHKEYYN")
        String authKeyYN;

        public String getResult ()
        {
            return this.result;
        }

        public String getCause ()
        {
            return this.cause;
        }

        public String getUpdateUrl () {return this.updateUrl;}

        // 단말기에 가지고 있는 인증키의 유효성 체크 값, Y:사용가능, N:사용불가능
        public String getAuthKeyYN () {return this.authKeyYN;}
    }
}
