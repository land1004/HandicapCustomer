package kr.or.yongin.transporthelp.impl.net.json.rep;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public class REP_JSONLogin
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
        @SerializedName("AUTHKEY")
        String authKey;
        @SerializedName("MEMBERINFOCHGYN")
        String changeInfo;
        @SerializedName("REAGREEMENTYN")
        String reagrement;
        @SerializedName("WEBPASSWORDRECHANGEYN")
        String passwrodchange;
        @SerializedName("WEBPASSWORDFAILYN")
        String passwrodfail;

        public String getResult ()
        {
            return this.result;
        }
        public String getCause ()
        {
            return this.cause;
        }
        public String getAuthKey () {return this.authKey; }

        //회원정보수정대상: Y, 회원정보수정비대상: N
        public boolean getChangeInfo()
        {
            if (this.changeInfo != null && this.changeInfo.length() > 0)
            {
                if (this.changeInfo.toUpperCase().equalsIgnoreCase("Y"))
                    return true;
            }

            return false;
        }

        // 재동의대상: Y, 재동의비대상: N
        // 개인정보수집 재동의 여부가 'Y'인 경우 :
        // '개인정보수집에 동의하신 지 2년이 경과하였습니다. 재동의 후 이용해주세요.' → 약관동의 화면으로 이동함.
        public boolean getReagrement()
        {
            if (this.reagrement != null && this.reagrement.length() > 0)
            {
                if (this.reagrement.toUpperCase().equalsIgnoreCase("Y"))
                    return true;
            }

            return false;
        }

        // 변경대상: Y, 변경비대상: N
        // 비밀번호 재변경 여부가 'Y'인 경우 : '비밀번호를 변경하신 지 6개월이 경과하였습니다.
        // 홈페이지를 통해서 비밀번호를 변경해주세요.' (비밀번호가 변경될때까지 알림 창으로 알려주며, 로그인은 가능하다.)
        public boolean getPasswrodchange()
        {
            if (this.passwrodchange != null && this.passwrodchange.length() > 0)
            {
                if (this.passwrodchange.toUpperCase().equalsIgnoreCase("Y"))
                    return true;
            }

            return false;
        }

        // 비밀번호 입력 실패로 인한 일시 중지 여부, 중지대상: Y, 중지비대상: N
        // 비밀번호 실패 중지 여부가 'Y'인 경우 : '비밀번호를 5회 잘못 입력하셨습니다. 5분 이후 다시 이용해주세요.'
        public boolean getPasswrodfail()
        {
            if (this.passwrodfail != null && this.passwrodfail.length() > 0)
            {
                if (this.passwrodfail.toUpperCase().equalsIgnoreCase("Y"))
                    return true;
            }

            return false;
        }
    }
}
