package kr.or.yongin.transporthelp.impl.net.json.rep;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IONEMAX on 2016-12-29.
 */

public class REP_JSONHeader
{
    @SerializedName("HEADER")
    Header header;
    public Header getHeader() {return this.header;}

    public class Header
    {
        @SerializedName("VERSION")
        String protocolVersion;
        @SerializedName("MESSAGE")
        String pageName;

        public String getProtocolVersion ()
        {
            return this.protocolVersion;
        }

        public String getPageName ()
        {
            return this.pageName;
        }
    }
}
