package kr.co.ionemax.fileuploadtest;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ionemax.iomlibrarys.log.Logview;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends Activity implements View.OnClickListener
{
    private final String THIS_TAG = "MainActivity";

    private final int MSG_SELECT_IMAGE = 9090;
    private String mImagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button)findViewById(R.id.btnFileSelect);
        btn.setOnClickListener(this);
        btn = (Button)findViewById(R.id.btnFileSend);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnFileSelect:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, MSG_SELECT_IMAGE);

                break;
            case R.id.btnFileSend:
                doUploadFile(mImagePath);
                break;
        }
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_SELECT_IMAGE:
                    viewSelectImage();
                    break;
            }
        }
    };

    private void viewSelectImage ()
    {
        TextView tv = (TextView)findViewById(R.id.txtSelectImage);
        tv.setText(mImagePath);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK) {
            if (requestCode == MSG_SELECT_IMAGE) {
                //try {
                mImagePath = getImagePath(data.getData());
                handler.sendEmptyMessage(MSG_SELECT_IMAGE);

                    //Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                    //mPhotoView.setImageBitmap (image_bitmap);

//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

            }
        }
    }

    private String getImagePath(Uri uri)
    {
        String[] proj = { MediaStore.Images.Media.DATA };

        CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
    private final String URL_UPLOAD_IMAGE = "http://210.220.248.175:8080/passenger/RegMember";
    private void doUploadFile (final String filePath)
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
                            .addFormDataPart("title", "WELFAREFILE")
                            .addFormDataPart("WELFAREFILE", "WELFAREFILE.jpg",
                                    RequestBody.create(MEDIA_TYPE_JPG, sourceFile))
                            .build();

                    Request request = new Request.Builder()
                            .url(URL_UPLOAD_IMAGE)
                            .post(requestBody)
                            .build();

                    OkHttpClient client = new OkHttpClient();
                    Response response = client.newCall(request).execute();

                    String str = response.body().string();
                    Logview.Logwrite(THIS_TAG, "send success : " + str);

                } catch (UnknownHostException e) {
                    Logview.Logwrite(THIS_TAG, "Error: UnknownHostException " );
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    Logview.Logwrite(THIS_TAG, "Error: UnsupportedEncodingException");
                    e.printStackTrace();
                } catch (Exception e) {
                    Logview.Logwrite(THIS_TAG, "Other Error: Exception" );
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
