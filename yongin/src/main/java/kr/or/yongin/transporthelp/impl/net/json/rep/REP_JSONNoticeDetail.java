package kr.or.yongin.transporthelp.impl.net.json.rep;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public class REP_JSONNoticeDetail
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
        @SerializedName("NOTICESEQ")
        String noticeSeq;
        @SerializedName("NOTICETITLE")
        String title;
        @SerializedName("NOTICEDATE")
        String date;
        @SerializedName("NOTICEBODY")
        String noticeBody;
        @SerializedName("LINKHTTP")
        String link;

        public String getResult ()
        {
            return this.result;
        }
        public String getCause ()
        {
            return this.cause;
        }

        public String getNoticeSeq () {return this.noticeSeq;}
        public String getTitle () {return this.title;}
        public String getDate () {return this.date;}
        public String getNoticeBody () {return this.noticeBody;}
        // http링크
        public String getLink () {return this.link;}
    }
}
