package kr.or.hsnarae.transporthelp.impl.net.json.rep;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public class REP_JSONMemberInfo
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
        @SerializedName("NAME")
        String name;
        @SerializedName("PHONENUMBER")
        String phoneNumber;
        @SerializedName("EMAIL")
        String email;
        @SerializedName("SEX")
        String sex;
        @SerializedName("BIRTH")
        String birthDay;
        @SerializedName("ZIPCODE")
        String zipcode;
        @SerializedName("SIDO")
        String sido;
        @SerializedName("SIGUGUNA")
        String siguGunA;
        @SerializedName("SIGUGUNB")
        String siguGunB;
        @SerializedName("DONG1")
        String dong1;
        @SerializedName("DONG2")
        String dong2;
        @SerializedName("ADDR1")
        String addr1;
        @SerializedName("ADDR2")
        String addr2;
        @SerializedName("ROADFULLADDR")
        String roadAddr;
        @SerializedName("ID")
        String id;
        @SerializedName("WHEELCHIARYN")
        String wheelYN;
        @SerializedName("ADDPHONENUMBER")
        String addPhoneNumber;
        @SerializedName("GUARDIANNAME")
        String guadianName;
        @SerializedName("GUARDIANPHONE")
        String guadianPhone;
        @SerializedName("COMMUNICATIONYN")
        String communication;
        @SerializedName("HELPTYPE")
        String helptype;
        @SerializedName("ASSISTANTYN")
        String assistant;

        public String getResult ()
        {
            return this.result;
        }
        public String getCause ()
        {
            return this.cause;
        }

        public String getName () {return this.name;}
        public String getPhoneNumber () {return this.phoneNumber;}
        public String getEmail () {return this.email;}
        //남성 : M , 여성 : F
        public String getSex () {return this.sex;}
        public String getBirthDay () {return this.birthDay;}
        //국가기초구역번호. 2015년 8월 1일부터 시행될 새 우편번호.
        public String getZipcode () {return this.zipcode;}
        //개별 주소가 속한 특별시/광역시/도에 해당되는 정보
        public String getSido () {return this.sido;}
        //개별 주소가 속한 일반시/구/군에 해당되는 정보A, 예) 수원시
        public String getSiguGunA () {return this.siguGunA;}
        //개별 주소가 속한 일반시/구/군에 해당되는 정보B, 예) 팔달구
        public String getSiguGunB () {return this.siguGunB;}
        //동, 읍, 면, 리 등으로로 구성된 주소가 두 어구 이상인 경우 동1, 2 로 나눈 값, 예) 향남읍
        public String getDong1 () {return this.dong1;}
        //동, 읍, 면, 리 등으로로 구성된 주소가 두 어구 이상인 경우 동1, 2 로 나눈 값, 예) 행정리
        public String getDong2 () {return this.dong2;}
        //동, 읍, 면, 리 등까지의 주소, 예) 경기도 화성시 봉담읍 수영리
        public String getAddr1 () {return this.addr1;}
        //동, 읍, 면, 리 등 이후의 주소(서버 제공 jsp 반환 값의 ADDR2와 DETAILADDRESS 값을 합친 문자), 예) 202동 103호
        public String getAddr2 () {return this.addr2;}
        //우편번호주소검색결과의 도로명주소에 상세주소가 포함된 값(서버 제공 jsp 반환 값의 ROADFULLADDR와 DETAILADDRESS 값을 합친 문자)
        public String getRoadAddr () {return this.roadAddr;}
        public String getId () {return this.id;}
        //자동: A, 수동 : M, 미사용:N
        public String getWheelYN () {return this.wheelYN;}
        public String getAddPhoneNumber () {return this.addPhoneNumber;}
        public String getGuadianName () {return this.guadianName;}
        public String getGuadianPhone () {return this.guadianPhone;}

        //의사소통가능 : Y, 의사소통불가능 : N
        public String getCommunication () {return this.communication;}
        //도움여부 (N - 무도움, I - 부분도움, Y - 완전도움)
        public String getHelptype () {return this.helptype;}
        //보조인여부(있음 : Y, 없음 : N)
        public String getAssistant () {return this.assistant;}
    }
}
