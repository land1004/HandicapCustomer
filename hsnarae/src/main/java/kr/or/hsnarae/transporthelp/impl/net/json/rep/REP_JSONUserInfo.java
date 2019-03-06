package kr.or.hsnarae.transporthelp.impl.net.json.rep;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public class REP_JSONUserInfo
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

        @SerializedName("USERNAME")
        String username;
        @SerializedName("PHONENUMBER")
        String userphone;
        @SerializedName("EMAIL")
        String useremail;
        @SerializedName("CALLID")
        String callid;
        @SerializedName("CALLSTATE")
        String callState;
        @SerializedName("AUTHKEY")
        String authkey;
        @SerializedName("NOTICEALERTYN")
        String noticealert;
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

        public String getUsername () {return this.username;}
        public String getUserphone () {return this.userphone;}
        public String getUseremail () {return this.useremail;}
        public String getCallid () {return this.callid;}
        public String getAuthkey () {return this.authkey;}
        public String getCallState() {return this.callState;}

        // 공지사항 알림여부, 알림: 'Y', 미알림: 'N'
        public boolean getNoticealert()
        {
            boolean bret = false;
            String yn = this.noticealert;

            if (yn != null && yn.toUpperCase().trim().equalsIgnoreCase("Y"))
                bret = true;

            return bret;
        }

        public boolean getCall_canceltype ()
        {
            boolean bret = false;
            if (this.call_canceltype != null && this.call_canceltype.toUpperCase().trim().equalsIgnoreCase("S"))
                bret = true;

            return bret;
        }
    }
}
