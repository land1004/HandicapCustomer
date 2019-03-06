package kr.or.hsnarae.transporthelp.impl.net.json.req;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public interface ReqService
{
    public static final String BASE_URL = "http://ctmappitf.hsnarae.or.kr:38080/";
    // 유경이 로컬
    //public static final String BASE_URL = "http://210.220.248.175:8080/";
    //http://ctmappitf.hsnarae.or.kr:38080/CTMAppITF/passenger/메시지명?q=[WebBase64인코딩 json 데이터]

    @Headers({
            "Accept: */*",
            "User-Agent: curl/7.43.0",
            "Content-Type: application/json",
            "charset: utf-8"
    })

    @POST("CTMAppITF/passenger/{page}")
    Call<ResponseBody>repRequest(
            @Path("page") String pageName,
            @Query("q") String query
    );
}
