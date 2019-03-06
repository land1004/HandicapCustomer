package kr.or.yongin.transporthelp.impl.net.json.rep;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public class REP_JSONHandDegreeList
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

        // 장애등급 리스트
        @SerializedName("HANDIDEGREELIST")
        public List<HandiDegree> handiDegree = new ArrayList<>();


        public String getResult ()
        {
            return this.result;
        }

        public String getCause ()
        {
            return this.cause;
        }

        public List<HandiDegree> getHandiDegree () {return this.handiDegree;}

        public class HandiDegree
        {
            @SerializedName("HANDIDEGREE")
            String handiDegree;
            @SerializedName("HANDIDEGREECODE")
            String handiDegreeCode;

            public String getHandiDegree () {return this.handiDegree;}

            public String getHandiDegreeCode () {return this.handiDegreeCode;}
        }
    }
}
