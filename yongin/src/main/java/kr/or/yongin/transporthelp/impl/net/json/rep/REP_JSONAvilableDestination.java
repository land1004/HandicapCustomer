package kr.or.yongin.transporthelp.impl.net.json.rep;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public class REP_JSONAvilableDestination
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
        @SerializedName("DESTINATIONVERSION")
        String distinationVer;
        @SerializedName("DESTINATIONCOUNT")
        String count;
        @SerializedName("DESTINATIONLIST")
        List<Destination> destinations;

        public String getResult ()
        {
            return this.result;
        }
        public String getCause ()
        {
            return this.cause;
        }

        public String getDistinationVer() {return this.distinationVer;}
        public String getCount() {return this.count;}
        public List<Destination> getDestinations() {return this.destinations;}

        public class Destination
        {
            @SerializedName("DESTINATIONSEQ")
            String seq;
            @SerializedName("DESTINATIONSI")
            String si;
            @SerializedName("DESTINATIONGU")
            String gu;
            @SerializedName("DESTINATIONDONG")
            String dong;

            public String getSeq() {return this.seq;}
            public String getSi() {return this.si;}
            public String getGu() {return this.gu;}
            public String getDong() {return this.dong;}
        }
    }
}
