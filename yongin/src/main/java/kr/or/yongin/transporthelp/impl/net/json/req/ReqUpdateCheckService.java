package kr.or.yongin.transporthelp.impl.net.json.req;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public interface ReqUpdateCheckService
{
    public static final String BASE_URL = "http://au.api.callinone.co.kr/";

    //http://ctmappitf.yongin.or.kr:38080/CTMAppITF/passenger/메시지명?q=[WebBase64인코딩 json 데이터]

    @Headers({
            "Accept: */*",
            "User-Agent: curl/7.43.0",
            "Content-Type: application/x-www-form-urlencoded",
            "charset: utf-8"
    })

    @POST("Update/{page}")
    Call<ResponseBody>repRequest(
            @Path("page") String pageName,
            @Query("q") String query
    );
}
