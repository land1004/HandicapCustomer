package kr.or.hsnarae.transporthelp.impl.net.json;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.ionemax.iomlibrarys.log.Logview;
import java.io.UnsupportedEncodingException;

import kr.or.hsnarae.transporthelp.impl.net.json.rep.REP_JSONHeader;

/**
 * Created by IONEMAX on 2016-12-29.
 */

public class JSONMessageEvent
{
    private final String THIS_TAG = "JSONMessageEvent";

    private final int HEADER_SIZE = 8;
    private final int UPDADE_HEADER_SIZE = 6;

    private boolean success = false;
    private String msg="";
    private byte[] btMsg;

    public JSONMessageEvent ()
    {
    }

    public JSONMessageEvent (boolean flag, byte[] msg)
    {
        this.success = flag;
        if (flag) {
            this.btMsg = new byte[msg.length];
            System.arraycopy(msg, 0, this.btMsg, 0, msg.length);

            this.msg = new String(this.btMsg);
        }
    }

    /**
     * 메시지 수신 상태
     * @return
     */
    public boolean getMessageStatus ()
    {
        return this.success;
    }

    /**
     * 수신된 메시지 데이터 영역
     * @return
     */
    public String getMessageBody()
    {
        String body = "";

        if (!this.success) return body;

        // size
        String temp = this.msg.substring(0, HEADER_SIZE);
        if (temp != null && temp.length() > 0) {
            try
            {
                int size = Integer.parseInt(temp.trim());
                if (this.msg.length() >= (size + HEADER_SIZE)) {
                    Logview.Logwrite(THIS_TAG, "getMessageBody > size : " + temp);
                    Log.d("테스트","getMessageBody > size" + temp);
                    Log.d("테스트","getMessageBody > msg" + msg.length());
                    String base64 = this.msg.substring(HEADER_SIZE, size + HEADER_SIZE);
                    Logview.Logwrite(THIS_TAG, "base64 > " + base64);

                    // Base64
                    try {
                        body = new String(Base64.decode(base64.getBytes(), Base64.NO_WRAP), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    Logview.Logwrite(THIS_TAG, "수신된 메시지 사이즈가 맞지 않음. msg : " + this.msg.length() );
                    Log.d("테스트", "수신된 메시지 사이즈가 맞지 않음. msg : " + this.msg.length() );
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Logview.Logwrite(THIS_TAG, "수신메시지 : " + msg);
                Log.d("테스트", "수신메시지 : " + msg);

            }
        }

        return body;
    }

    /**
     * update message
     * @return
     */
    public String getUpdateMessageBody()
    {
        String body = "";

        if (!this.success) return body;

        // size
        String temp = this.msg.substring(0, UPDADE_HEADER_SIZE);
        if (temp != null && temp.length() > 0) {
            try
            {
                int size = Integer.parseInt(temp.trim());
                if (this.msg.length() >= (size + UPDADE_HEADER_SIZE)) {
                    Logview.Logwrite(THIS_TAG, "getMessageBody > size : " + temp);
                    String base64 = this.msg.substring(UPDADE_HEADER_SIZE, size + UPDADE_HEADER_SIZE);
                    Logview.Logwrite(THIS_TAG, "base64 > " + base64);

                    // Base64
                    try {
                        body = new String(Base64.decode(base64.getBytes(), Base64.NO_WRAP), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    Logview.Logwrite(THIS_TAG, "수신된 메시지 사이즈가 맞지 않음. msg : " + this.msg.length() );
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        return body;
    }

    /**
     * 수신된 메시지에서 페이지 이름
     * @return
     */
    public String getPageName ()
    {
        if (!this.success) return null;

        String page = "";
        String json = getMessageBody ();
        Logview.Logwrite(THIS_TAG, "Message : " + json);

        if (json != null && json.length() > 10)
        {
            Gson gson = new Gson();
            REP_JSONHeader header = gson.fromJson(json, REP_JSONHeader.class);
            page = header.getHeader().getPageName();
        } else {
            // update 헤더 사이즈가 달라서
            json = getUpdateMessageBody ();
            Logview.Logwrite(THIS_TAG, "Message : " + json);

            if (json != null && json.length() > 10)
            {
                Gson gson = new Gson();
                REP_JSONHeader header = gson.fromJson(json, REP_JSONHeader.class);
                page = header.getHeader().getPageName();
            }
        }

        return page;
    }

    /**
     * 수신된 메시지
     * @return
     */
    public String toString()
    {
        return msg;
    }
}
