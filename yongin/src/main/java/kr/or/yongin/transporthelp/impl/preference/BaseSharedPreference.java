package kr.or.yongin.transporthelp.impl.preference;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

abstract class BaseSharedPreference
{
	private Context m_context = null;
	private String PRF_NAME = "";
	
    public void put(String key, String value) 
    {
        SharedPreferences pref = m_context.getSharedPreferences(PRF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
 
        editor.putString(key, value);
        editor.commit();
    }
 
    public void put(String key, boolean value) 
    {
        SharedPreferences pref = m_context.getSharedPreferences(PRF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
 
        editor.putBoolean(key, value);
        editor.commit();
    }
 
    public void put(String key, int value) 
    {
        SharedPreferences pref = m_context.getSharedPreferences(PRF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
 
        editor.putInt(key, value);
        editor.commit();
    }

    public void put(String key, float value)
    {
        SharedPreferences pref = m_context.getSharedPreferences(PRF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putFloat(key, value);
        editor.commit();
    }

    public void put(String key, double value)
    {
        SharedPreferences pref = m_context.getSharedPreferences(PRF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putFloat(key, (float)value);
        editor.commit();
    }


    public String getValue(String key, String dftValue) 
    {
        SharedPreferences pref = m_context.getSharedPreferences(PRF_NAME,
                Activity.MODE_PRIVATE);
 
        try {
            return pref.getString(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
 
    }
 
    public int getValue(String key, int dftValue) 
    {
        SharedPreferences pref = m_context.getSharedPreferences(PRF_NAME,
                Activity.MODE_PRIVATE);
 
        try {
            return pref.getInt(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
 
    }
 
    public boolean getValue(String key, boolean dftValue) 
    {
        SharedPreferences pref = m_context.getSharedPreferences(PRF_NAME,
                Activity.MODE_PRIVATE);
 
        try {
            return pref.getBoolean(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }

    public float getValue(String key, float dftValue)
    {
        SharedPreferences pref = m_context.getSharedPreferences(PRF_NAME,
                Activity.MODE_PRIVATE);

        try {
            return pref.getFloat(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }

    public double getValue(String key, double dftValue)
    {
        SharedPreferences pref = m_context.getSharedPreferences(PRF_NAME,
                Activity.MODE_PRIVATE);

        try {
            return pref.getFloat(key, (float)dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }


    protected void setPreference(Context context, String name)
    {
    	m_context = context;
    	PRF_NAME = name;
    }
    
    public abstract void setPreference(Context context);
}
