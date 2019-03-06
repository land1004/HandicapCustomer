package kr.or.yongin.transporthelp.impl.net.json;

import android.content.Context;
import android.content.Intent;

import com.ionemax.iomlibrarys.log.Logview;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import kr.or.yongin.transporthelp.common.util.BusEventProvider;
import kr.or.yongin.transporthelp.common.util.GlobalValues;
import kr.or.yongin.transporthelp.impl.net.json.req.ReqMultiPartService;
import kr.or.yongin.transporthelp.impl.net.json.req.ReqService;
import kr.or.yongin.transporthelp.impl.net.json.req.ReqUpdateCheckService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by IONEMAX on 2016-12-29.
 */

public class RetrofitProcessManager
{
    private static final String THIS_TAG = "RetrofitProcessManager";

    public static final int CONNECT_TIMEOUT = 30;
    public static final int WRITE_TIMEOUT = 30;
    public static final int READ_TIMEOUT = 30;

    public RetrofitProcessManager ()
    {

    }

    public static synchronized void doRetrofitRequest(String pageName, String req)
    {
        Logview.Logwrite(THIS_TAG, "doRetrofitRequest : " + pageName );

        //OkHttpClient를 생성합니다.
        OkHttpClient client = new OkHttpClient().newBuilder() //인증서 무시
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS) //연결 타임아웃 시간 설정
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS) //쓰기 타임아웃 시간 설정
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS) //읽기 타임아웃 시간 설정
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ReqService.BASE_URL)
                .client(client)
                //.addConverterFactory(GsonConverterFactory.create())
                .build();

        ReqService reqService = retrofit.create(ReqService.class);
        final Call<ResponseBody> result = reqService.repRequest(pageName, req);

        result.enqueue(new Callback<ResponseBody>()
        {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
            {
                Logview.Logwrite(THIS_TAG, "onResponse message : " + response.message());
                try {
                    if (response.body() != null) {
                        BusEventProvider.getInstance().post(new JSONMessageEvent(true, response.body().bytes()));
                        Logview.Logwrite(THIS_TAG, "onResponse success ");
                    } else {
                        Logview.Logwrite(THIS_TAG, "onResponse message : body null");
                        BusEventProvider.getInstance().post(new JSONMessageEvent(false, null));
                    }
                } catch (IOException ie) {
                    ie.printStackTrace();
                    BusEventProvider.getInstance().post(new JSONMessageEvent(false, null));
                } catch (Exception e) {
                    e.printStackTrace();
                    BusEventProvider.getInstance().post(new JSONMessageEvent(false, null));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t)
            {
                Logview.Logwrite(THIS_TAG, "onFailure");
                t.printStackTrace();
                BusEventProvider.getInstance().post(new JSONMessageEvent(false, null));
            }
        });
    }


    public static synchronized void doRetrofitRequestService(final Context context, String pageName, String req)
    {
        Logview.Logwrite(THIS_TAG, "doRetrofitRequestService : " + pageName );

        //OkHttpClient를 생성합니다.
        OkHttpClient client = new OkHttpClient().newBuilder() //인증서 무시
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS) //연결 타임아웃 시간 설정
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS) //쓰기 타임아웃 시간 설정
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS) //읽기 타임아웃 시간 설정
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ReqService.BASE_URL)
                .client(client)
                //.addConverterFactory(GsonConverterFactory.create())
                .build();

        ReqService reqService = retrofit.create(ReqService.class);
        final Call<ResponseBody> result = reqService.repRequest(pageName, req);

        result.enqueue(new Callback<ResponseBody>()
        {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
            {
                Intent intent = new Intent();
                Logview.Logwrite(THIS_TAG, "onResponse message : " + response.message());
                try {
                    if (response.body() != null) {
                        //BusEventProvider.getInstance().post(new JSONMessageEvent(true, response.body().bytes()));
                        intent.setAction(GlobalValues.ACTION_BROADCAST_SERVER);
                        intent.putExtra("flag", true);
                        intent.putExtra("data", response.body().bytes());
                        context.sendBroadcast(intent);
                        Logview.Logwrite(THIS_TAG, "onResponse success ");
                    } else {
                        Logview.Logwrite(THIS_TAG, "onResponse message : body null");
                        //BusEventProvider.getInstance().post(new JSONMessageEvent(false, null));
                        intent.setAction(GlobalValues.ACTION_BROADCAST_SERVER);
                        intent.putExtra("flag", false);
                        context.sendBroadcast(intent);

                    }
                } catch (IOException ie) {
                    ie.printStackTrace();
                    //BusEventProvider.getInstance().post(new JSONMessageEvent(false, null));
                    intent.setAction(GlobalValues.ACTION_BROADCAST_SERVER);
                    intent.putExtra("flag", false);
                    context.sendBroadcast(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                    //BusEventProvider.getInstance().post(new JSONMessageEvent(false, null));
                    intent.setAction(GlobalValues.ACTION_BROADCAST_SERVER);
                    intent.putExtra("flag", false);
                    context.sendBroadcast(intent);

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t)
            {
                Logview.Logwrite(THIS_TAG, "onFailure");
                t.printStackTrace();
                //BusEventProvider.getInstance().post(new JSONMessageEvent(false, null));
                Intent intent = new Intent();
                intent.setAction(GlobalValues.ACTION_BROADCAST_SERVER);
                intent.putExtra("flag", false);
                context.sendBroadcast(intent);

            }
        });
    }


    /**
     * UPDate check
     * @param pageName
     * @param req
     */
    public static void doRetrofitUpdateRequest(String pageName, String req)
    {
        Logview.Logwrite(THIS_TAG, "doRetrofitUpdateRequest : " + pageName);
        //OkHttpClient를 생성합니다.
        OkHttpClient client = new OkHttpClient().newBuilder() //인증서 무시
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS) //연결 타임아웃 시간 설정
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS) //쓰기 타임아웃 시간 설정
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS) //읽기 타임아웃 시간 설정
                .build();
        Logview.Logwrite(THIS_TAG, "Request OkHttpClient build finish...............");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ReqUpdateCheckService.BASE_URL)
                .client(client)
                //.addConverterFactory(GsonConverterFactory.create())
                .build();
        Logview.Logwrite(THIS_TAG, "Request Retrofit build finish...............");
        ReqUpdateCheckService reqService = retrofit.create(ReqUpdateCheckService.class);
        final Call<ResponseBody> result = reqService.repRequest(pageName, req);
        Logview.Logwrite(THIS_TAG, "Request end...............");
        result.enqueue(new Callback<ResponseBody>()
        {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
            {
                Logview.Logwrite(THIS_TAG, "onResponse message : " + response.message());
                try {
                    if (response.body() != null) {
                        BusEventProvider.getInstance().post(new JSONUpdateMessageEvent(true, response.body().bytes()));
                        Logview.Logwrite(THIS_TAG, "onResponse success ");
                    } else {
                        Logview.Logwrite(THIS_TAG, "onResponse message : body null");
                        BusEventProvider.getInstance().post(new JSONMessageEvent(false, null));
                    }
                } catch (IOException ie) {
                    ie.printStackTrace();
                    BusEventProvider.getInstance().post(new JSONMessageEvent(false, null));
                } catch (Exception e) {
                    e.printStackTrace();
                    BusEventProvider.getInstance().post(new JSONMessageEvent(false, null));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t)
            {
                Logview.Logwrite(THIS_TAG, "onFailure");
                t.printStackTrace();
                BusEventProvider.getInstance().post(new JSONMessageEvent(false, null));
            }
        });
    }

    /**
     * File upload
     * @param context
     * @param pageName
     * @param req
     */
    public static synchronized void doRetrofitMultiPartRequestService(final Context context, String pageName, String req, String filepath)
    {
        Logview.Logwrite(THIS_TAG, "doRetrofitMultiPartRequestService : " + pageName );
        Logview.Logwrite(THIS_TAG, "doRetrofitMultiPartRequestService : " + filepath );

        //
        File file = new File(filepath);
        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/jpg"),
                        file
                );

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("WELFAREFILE", file.getName(), requestFile);

        //OkHttpClient를 생성합니다.
        OkHttpClient client = new OkHttpClient().newBuilder() //인증서 무시
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS) //연결 타임아웃 시간 설정
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS) //쓰기 타임아웃 시간 설정
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS) //읽기 타임아웃 시간 설정
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ReqService.BASE_URL)
                .client(client)
                //.addConverterFactory(GsonConverterFactory.create())
                .build();

        // add another part within the multipart request
        RequestBody query =
                RequestBody.create(
                        MultipartBody.FORM, req);

        ReqMultiPartService reqService = retrofit.create(ReqMultiPartService.class);
        final Call<ResponseBody> result = reqService.repRequest(pageName, query, body);

        result.enqueue(new Callback<ResponseBody>()
        {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
            {
                Intent intent = new Intent();
                Logview.Logwrite(THIS_TAG, "onResponse message : " + response.message());
                try {
                    if (response.body() != null) {
                        BusEventProvider.getInstance().post(new JSONMessageEvent(true, response.body().bytes()));
                        Logview.Logwrite(THIS_TAG, "onResponse success ");
                    } else {
                        Logview.Logwrite(THIS_TAG, "onResponse message : body null");
                        //BusEventProvider.getInstance().post(new JSONMessageEvent(false, null));
                        intent.setAction(GlobalValues.ACTION_BROADCAST_SERVER);
                        intent.putExtra("flag", false);
                        context.sendBroadcast(intent);

                    }
                } catch (IOException ie) {
                    ie.printStackTrace();
                    BusEventProvider.getInstance().post(new JSONMessageEvent(false, null));

                } catch (Exception e) {
                    e.printStackTrace();
                    BusEventProvider.getInstance().post(new JSONMessageEvent(false, null));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t)
            {
                Logview.Logwrite(THIS_TAG, "onFailure");
                t.printStackTrace();
                BusEventProvider.getInstance().post(new JSONMessageEvent(false, null));
            }
        });
    }

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
    // 유경이 로컬
    //private static final String URL_UPLOAD_IMAGE = "http://210.220.248.175:8080/passenger/RegMember";
    // 운영 서버
    private static final String URL_UPLOAD_IMAGE = "http://CTMAppITF.yonginnuri.or.kr:38080/CTMAppITF/passenger/RegMember";
    public static synchronized void doUploadFile(final String req, final String filePath)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File sourceFile = new File(filePath);

                    Logview.Logwrite(THIS_TAG, "File...::::" + sourceFile + " : " + sourceFile.exists());

                    final MediaType MEDIA_TYPE = filePath.endsWith("jpg") ?
                            MediaType.parse("image/png") : MediaType.parse("image/jpeg");


                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("q", req)
                            .addFormDataPart("WELFAREFILE", "WELFAREFILE.jpg",
                                    RequestBody.create(MEDIA_TYPE_JPG, sourceFile))
                            .build();

                    Request request = new Request.Builder()
                            .url(URL_UPLOAD_IMAGE)
                            .post(requestBody)
                            .build();

                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS) //연결 타임아웃 시간 설정
                            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS) //쓰기 타임아웃 시간 설정
                            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS) //읽기 타임아웃 시간 설정
                            .build();

                    okhttp3.Response response = client.newCall(request).execute();

                    String str = response.body().string();
                    Logview.Logwrite(THIS_TAG, "send success : " + str);
                    BusEventProvider.getInstance().post(new JSONMessageEvent(true, str.getBytes()));
                } catch (UnknownHostException e) {
                    Logview.Logwrite(THIS_TAG, "Error: UnknownHostException " );
                    e.printStackTrace();
                    BusEventProvider.getInstance().post(new JSONMessageEvent(false, null));
                } catch (UnsupportedEncodingException e) {
                    Logview.Logwrite(THIS_TAG, "Error: UnsupportedEncodingException");
                    e.printStackTrace();
                    BusEventProvider.getInstance().post(new JSONMessageEvent(false, null));
                } catch (Exception e) {
                    Logview.Logwrite(THIS_TAG, "Other Error: Exception" );
                    e.printStackTrace();
                    BusEventProvider.getInstance().post(new JSONMessageEvent(false, null));
                }
            }
        }).start();
    }

}
