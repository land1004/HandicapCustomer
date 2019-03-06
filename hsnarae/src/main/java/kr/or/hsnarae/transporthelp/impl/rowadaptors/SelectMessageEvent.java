package kr.or.hsnarae.transporthelp.impl.rowadaptors;

import android.util.Base64;

import com.google.gson.Gson;
import com.ionemax.iomlibrarys.log.Logview;

import java.io.UnsupportedEncodingException;

import kr.or.hsnarae.transporthelp.impl.net.json.rep.REP_JSONHeader;

/**
 * Created by IONEMAX on 2016-12-29.
 */

public class SelectMessageEvent
{
    private final String THIS_TAG = "SelectMessageEvent";

    private int selectIndex = 0;

    public SelectMessageEvent()
    {
    }

    public SelectMessageEvent(int select)
    {
        this.selectIndex = select;
    }

    /**
     * 메시지 수신 상태
     * @return
     */
    public int getSelectIndex () {return this.selectIndex;}
}
