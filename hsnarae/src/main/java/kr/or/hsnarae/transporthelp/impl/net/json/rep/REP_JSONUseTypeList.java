package kr.or.hsnarae.transporthelp.impl.net.json.rep;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public class REP_JSONUseTypeList
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

        // 이용목적 리스트
        @SerializedName("USETYPELIST")
        public List<UseType> useTypes = new ArrayList<>();

        public String getResult ()
        {
            return this.result;
        }

        public String getCause ()
        {
            return this.cause;
        }

        public List<UseType> getUseTypes () {return this.useTypes;}


        public class UseType
        {
            @SerializedName("USETYPE")
            String usetype;
            @SerializedName("USETYPECODE")
            String usetypecode;

            public String getUsetype () {return this.usetype;}

            public String getUsetypecode () {return this.usetypecode;}
        }

    }
}
