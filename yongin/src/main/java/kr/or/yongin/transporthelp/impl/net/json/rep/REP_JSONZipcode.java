package kr.or.yongin.transporthelp.impl.net.json.rep;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public class REP_JSONZipcode
{
    @SerializedName("ADDRESS")
    Address address;
    @SerializedName("DETAILADDRESS")
    String detailAddress;

    public Address getAddress () {return this.address;}
    public String getDetailAddress () {return this.detailAddress;}

    public class Address
    {
        @SerializedName("ZIPCODE")
        public String zipcode;
        @SerializedName("SIDO")
        public String sido;
        @SerializedName("SIGUGUNA")
        public String sigungunA;
        @SerializedName("SIGUGUNB")
        public String sigungunB;
        @SerializedName("DONG1")
        public String dong1;
        @SerializedName("DONG2")
        public String dong2;
        @SerializedName("ADDR1")
        public String addr1;
        @SerializedName("ADDR2")
        public String addr2;
        @SerializedName("ROADFULLADDR")
        public String roadAddress;
    }
}
