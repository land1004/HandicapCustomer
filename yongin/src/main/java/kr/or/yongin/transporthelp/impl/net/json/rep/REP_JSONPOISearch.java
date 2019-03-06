package kr.or.yongin.transporthelp.impl.net.json.rep;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public class REP_JSONPOISearch
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

        @SerializedName("TOTALCOUNT")
        String totalCount;
        @SerializedName("ITEMLIST")
        List<ITEM> itemList;

        public String getResult ()
        {
            return this.result;
        }
        public String getCause ()
        {
            return this.cause;
        }

        public String getTotalCount () {return this.totalCount;}
        public List<ITEM> getItemList () {return this.itemList;}

        public class ITEM
        {
            @SerializedName("DETAILADDRESS")
            public String poiName;
            @SerializedName("ROADFULLADDR")
            public String roadFullAddr;
            @SerializedName("JIBUNFULLADDR")
            public String jibunFullAddr;
            @SerializedName("LONGITUDE")
            public String longitude;
            @SerializedName("LATITUDE")
            public String latitude;
        }
        public class ITEM2
        {
            @SerializedName("POINAME")
            public String poiName;
            @SerializedName("ROADFULLADDR")
            public String roadFullAddr;
            @SerializedName("JIBUNFULLADDR")
            public String jibunFullAddr;
            @SerializedName("SIDO")
            public String sido;
            @SerializedName("SIGUGUN")
            public String sigugun;
            @SerializedName("ADMINDONG")
            public String adiminDong;
            @SerializedName("ADMINDONGCODE")
            public String adminDongCode;
            @SerializedName("LEGALDONG")
            public String legalDong;
            @SerializedName("LEGALDONGCODE")
            public String legalDongCode;
            @SerializedName("RI")
            public String ri;
            @SerializedName("ROADNAME")
            public String roadName;
            @SerializedName("BUILDINGINDEX")
            public String buildingIndex;
            @SerializedName("BUILDINGNAME")
            public String buildingName;
            @SerializedName("ROADCODE")
            public String roadCode;
            @SerializedName("BUNJI")
            public String bunJi;
            @SerializedName("DETAILADDRESS")
            public String detailAddress;
            @SerializedName("ZIPCODE")
            public String zipcode;
            @SerializedName("LONGITUDE")
            public String longitude;
            @SerializedName("LATITUDE")
            public String latitude;
        }
    }
}
