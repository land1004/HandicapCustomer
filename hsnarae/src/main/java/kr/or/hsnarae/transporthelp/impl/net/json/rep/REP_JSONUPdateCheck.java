package kr.or.hsnarae.transporthelp.impl.net.json.rep;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public class REP_JSONUPdateCheck
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

        @SerializedName("APPVER")
        String appVersion;
        @SerializedName("APPPATH")
        String appPath;
        @SerializedName("APPNAME")
        String appName;
        @SerializedName("APPUPDATEYN")
        String appUpdateYN;
        @SerializedName("APPUPDATEF")
        String appUpdateF;
        @SerializedName("LOGFTPADDR")
        String logFTPAddr;
        @SerializedName("LOGFTPPORT")
        String logFTPPort;
        @SerializedName("LOGFTPPATH")
        String logFTPPath;
        @SerializedName("LOGFTPID")
        String logFTPID;
        @SerializedName("LOGFTPPWD")
        String logFTPPW;

        public String getResult ()
        {
            return this.result;
        }
        public String getCause ()
        {
            return this.cause;
        }

        public String getAppVersion (){return this.appVersion;}
        public String getAppPath () {return this.appPath;}
        public String getAppName () {return this.appName;}
        public boolean getAppUpdateYN ()
        {
            boolean ret = false;
            if (appUpdateYN != null && appUpdateYN.length() > 0)
            {
                if (appUpdateYN.trim().toUpperCase(Locale.KOREA).equalsIgnoreCase("Y"))
                    ret = true;
            }

            return ret;
        }

        //강제업데이트 여부 ‘F’ 일 경우 강제 업데이트
        public String getAppUpdateF () {return this.appUpdateF;}
        public String getLogFTPAddr () {return this.logFTPAddr;}
        public String getLogFTPPort () {return this.logFTPPort;}
        public String getLogFTPPath () {return this.logFTPPath;}
        public String getLogFTPID () {return this.logFTPID;}
        public String getLogFTPPW () {return this.logFTPPW;}

    }
}
