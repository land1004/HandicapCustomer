package kr.or.hsnarae.transporthelp.impl.net.json.rep;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public class REP_JSONIDCheck
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
        @SerializedName("EXISTYN")
        String existYN;

        public String getResult ()
        {
            return this.result;
        }

        public String getCause ()
        {
            return this.cause;
        }

        // 중복여부, 존재함: Y, 존재하지 않음: N
        public String getExistYN () {return this.existYN;}
    }
}
