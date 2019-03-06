package kr.or.hsnarae.transporthelp.impl.net.json.rep;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public class REP_JSONNoticeList
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
        @SerializedName("NOTICECOUNT")
        String noticeCount;
        @SerializedName("NOTICELIST")
        List<NOTICE> noticeList;

        public String getResult ()
        {
            return this.result;
        }
        public String getCause ()
        {
            return this.cause;
        }

        public String getNoticeCount () {return this.noticeCount;}
        public List<NOTICE> getNoticeList () {return this.noticeList;}

        public class NOTICE
        {
            @SerializedName("NOTICESEQ")
            public String noticeSeq;
            @SerializedName("NOTICETITLE")
            public String noticeTitle;
            @SerializedName("NOTICEDATE")
            public String noticeDate;
        }
    }
}
