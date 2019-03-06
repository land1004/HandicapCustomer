package kr.or.hsnarae.transporthelp.impl.Retrofit;

import com.google.gson.JsonObject;

import kr.or.hsnarae.transporthelp.impl.net.NaverPositionToAddress;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by IONEMAX on 2016-12-09.
 */

public interface ApiService
{
    public static final String API_URL = "https://openapi.naver.com/";

//    Host: openapi.naver.com
//    User-Agent: curl/7.43.0
//    Accept: */*
//    Content-Type: application/json
//    X-Naver-Client-Id: {애플리케이션 등록 시 발급받은 client id 값}
//    X-Naver-Client-Secret: {애플리케이션 등록 시 발급받은 secret값}

    // GET https://openapi.naver.com/
    //           v1/map/reversegeocode?encoding=utf-8&coordType=latlng&query=127.1052133,37.3595316
    @Headers({
            "Accept: */*",
            "User-Agent: curl/7.43.0",
            "Content-Type: application/json"
    })

    @GET("v1/map/reversegeocode")
    Call<NaverPositionToAddress>getToAddress(
            @Header("X-Naver-Client-Id") String clientID,
            @Header("X-Naver-Client-Secret") String secretKey,
            @Query("encoding") String encoding,
            @Query("coordType") String coordType,
            @Query("query") String query
    );
}
