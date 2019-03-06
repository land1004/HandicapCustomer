package kr.or.yongin.transporthelp.common.util;

import android.util.Base64;

/**
 * Created by IONEMAX on 2016-12-29.
 */

public class Utils
{
    public Utils()
    {

    }

    /**
     * Base64로 변환
     * @param src
     * @return
     */
    public static String convertBase64(String src)
    {
        byte[] outdata = Base64.encode(src.getBytes(), Base64.NO_WRAP);
        String base64 = new String (outdata);

        // BASE64 변환해서 전송시 문제 때문에 처리된 것
        String temp1 = base64.replace("+", "-");
        base64 = temp1.replace("/", "_");

        return base64;
    }
}
