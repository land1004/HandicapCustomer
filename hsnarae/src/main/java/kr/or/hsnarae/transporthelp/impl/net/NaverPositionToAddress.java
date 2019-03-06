package kr.or.hsnarae.transporthelp.impl.net;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public class NaverPositionToAddress
{
    @SerializedName("result")
    Result result;

    public Result getResult () {return this.result;}

    public class Result
    {
        @SerializedName("total")
        String total;
        public int getTotal()
        {
            return (this.total != null ? Integer.parseInt(this.total) : 0);
        }

        @SerializedName("items")
        public List<Item> AddressList = new ArrayList<>();

        public List<Item> getAddressList () { return AddressList;}

        public class Item
        {
            @SerializedName("address")
            String address;
            @SerializedName("isRoadAddress")
            String isRoadAddress;
            @SerializedName("addrdetail")
            AddressDetail addrdetail;

            public String getAddress ()
            {
                return this.address;
            }

            /**
             * false : 구주소, true : 신주소 (도로명 주소)
             * @return
             */
            public boolean getIsRoadAddress ()
            {
                boolean bret = false;

                if (this.isRoadAddress != null && this.isRoadAddress.length() > 0)
                {
                    if (this.isRoadAddress.trim().toUpperCase(Locale.KOREA).equalsIgnoreCase("TRUE"))
                        bret = true;
                }

                return bret;
            }

            public AddressDetail getAddrdetail (){ return this.addrdetail;}

            public class AddressDetail
            {
                @SerializedName("country")
                String country;
                @SerializedName("sido")
                String sido;
                @SerializedName("sigugun")
                String sigugun;
                @SerializedName("dongmyun")
                String dongmyun;
                @SerializedName("rest")
                String rest;

                public String getCountry ()
                {
                    return this.country;
                }

                public String getSido ()
                {
                    return this.sido;
                }

                public String getSigugun ()
                {
                    return this.sigugun;
                }

                public String getDongmyun ()
                {
                    return this.dongmyun;
                }

                public String getRest ()
                {
                    return this.rest;
                }
            }
        }
    }
}

/* 네이버 응답예시
{
    "result": {
        "total": 2,
        "userquery": "127.1052133,37.3595316",
        "items": [
            {
                "address": "경기도 성남시 분당구 정자동 178-1",
                "addrdetail": {
                    "country": "대한민국",
                    "sido": "경기도",
                    "sigugun": "성남시 분당구",
                    "dongmyun": "정자동",
                    "rest": "178-1"
                },
                "isRoadAddress": false,
                "point": {
                    "x": 127.1052208,
                    "y": 37.3595122
                }
            },
            {
                "address": "경기도 성남시 분당구 불정로 6 그린팩토리",
                "addrdetail": {
                    "country": "대한민국",
                    "sido": "경기도",
                    "sigugun": "성남시 분당구",
                    "dongmyun": "불정로",
                    "rest": "6 그린팩토리"
                },
                "isRoadAddress": true,
                "point": {
                    "x": 127.1052133,
                    "y": 37.3595316
                }
            }
        ]
    }
}
 */
