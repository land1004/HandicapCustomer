package kr.or.yongin.transporthelp.common.FontAwesome;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ionemax.iomlibrarys.log.Logview;

import java.util.Hashtable;

/**
 * Created by IONEMAX on 2016-11-17.
 */

public class CFontAwesomeManager
{
    private static final String THIS_TAG = "CFontAwesomeManager";
    public static final String ROOT = "fonts/";
    public static final String FONTAWESOME = ROOT + "fontawesome-webfont.ttf";

    private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

    public static Typeface getTypeface(Context context, String font)
    {
        synchronized (cache) {
            if (!cache.containsKey(font)) {
                try {
                    Typeface t = Typeface.createFromAsset(context.getAssets(), font);
                    cache.put(font, t);
                } catch (Exception e) {
                    Logview.Logwrite(THIS_TAG, "Could not get typeface '" + font + "' because " + e.getMessage());
                    return null;
                }
            }
            return cache.get(font);
        }
       // return Typeface.createFromAsset(context.getAssets(), font);
    }

    public static void applyTypeFace(View v, Typeface typeface)
    {
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                applyTypeFace(child,typeface);
            }
        } else if (v instanceof TextView) {
            ((TextView) v).setTypeface(typeface);
        }
    }
}
