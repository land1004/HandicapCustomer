package kr.or.hsnarae.transporthelp.impl.net.json.rep;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public class REP_JSONHandTypeList
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

        // 장애유형 리스트
        @SerializedName("HANDITYPELIST")
        public List<HandiType> handiTypes = new ArrayList<>();

        public String getResult ()
        {
            return this.result;
        }

        public String getCause ()
        {
            return this.cause;
        }

        public List<HandiType> getHandiTypes () {return this.handiTypes;}


        public class HandiType
        {
            @SerializedName("HANDITYPE")
            String handiType;
            @SerializedName("HANDITYPECODE")
            String handiTypeCode;

            public String getHandiType () {return this.handiType;}

            public String getHandiTypeCode () {return this.handiTypeCode;}
        }

    }
}
