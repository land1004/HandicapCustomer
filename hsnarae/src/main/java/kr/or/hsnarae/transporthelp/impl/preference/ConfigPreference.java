package kr.or.hsnarae.transporthelp.impl.preference;

import android.content.Context;

public class ConfigPreference extends BaseSharedPreference
{
	private static ConfigPreference mConfigPreference = new ConfigPreference();

	private final String CONFIG_PREFERENCE = "kr.or.hsnarae.transporthelp.preference.config";


	public static final String VERSION = "kr.or.hsnarae.transporthelp.preference.config.version";
	public static final String NEW_VERSION = "kr.or.hsnarae.transporthelp.preference.config.new_version";
	public static final String PHONENUMBER = "kr.or.hsnarae.transporthelp.preference.config.phonenum";
	public static final String AUTHKEY = "kr.or.hsnarae.transporthelp.preference.config.authkey";
	public static final String PUSHKEY = "kr.or.hsnarae.transporthelp.preference.config.pushkey";

	public static final String USER_ID = "kr.or.hsnarae.transporthelp.preference.config.user_id";
	public static final String USER_NAME = "kr.or.hsnarae.transporthelp.preference.config.user_name";
	public static final String USER_EMAIL = "kr.or.hsnarae.transporthelp.preference.config.user_email";

	public static final String USER_NOTIFICATION = "kr.or.hsnarae.transporthelp.preference.config.user_notification";
	public static final String USER_REAGREEMENT = "kr.or.hsnarae.transporthelp.preference.config.user_reagreement";

	private ConfigPreference()
	{
		
	}
	
	public static ConfigPreference getInstance()
	{
		if (mConfigPreference == null )
			mConfigPreference = new ConfigPreference();

		return mConfigPreference;
	}
	
	@Override
	public void setPreference(Context context)
	{
		setPreference (context, CONFIG_PREFERENCE);
	}

	/**
	 * App 버전 정보
	 * @param value
     */
	public void setVersion(String value)
	{
		put(VERSION, value);
	}
	public String getVersion()
	{
		return getValue(VERSION, "");
	}

	/**
	 * 사용자 폰 번호
	 * @param value
     */
	public void setPhonenumber(String value)
	{
		put(PHONENUMBER, value);
	}
	public String getPhonenumber ()
	{
		return getValue(PHONENUMBER, "");
	}

	/**
	 * 인증키
	 * @param value
     */
	public void setAuthkey(String value)
	{
		put(AUTHKEY, value);
	}
	public String getAuthkey()
	{
		return getValue(AUTHKEY, "");
	}

	/**
	 * PUSH Key
	 * @param value
     */
	public void setPushkey(String value)
	{
		put(PUSHKEY, value);
	}
	public String getPushkey()
	{
		return getValue(PUSHKEY, "");
	}

	/**
	 * 사용자 아이디
	 * @param value
     */
	public void setUserId(String value)
	{
		put(USER_ID, value);
	}
	public String getUserId()
	{
		return getValue(USER_ID, "");
	}

	public void setUserName(String value)
	{
		put(USER_NAME, value);
	}
	public String getUserName()
	{
		return getValue(USER_NAME, "");
	}

	public void setUserEmail(String value)
	{
		put(USER_EMAIL, value);
	}
	public String getUserEmail()
	{
		return getValue(USER_EMAIL, "");
	}

	public void setNewVersion(String value)
	{
		put(NEW_VERSION, value);
	}
	public String getNewVersion()
	{
		return getValue(NEW_VERSION, "");
	}

	public void setUserNotification(boolean value)
	{
		put(USER_NOTIFICATION, value);
	}
	public boolean getUserNotification()
	{
		return getValue(USER_NOTIFICATION, false);
	}

	public void setUserReagreement(boolean value)
	{
		put(USER_REAGREEMENT, value);
	}
	public boolean getUserReagreement()
	{
		return getValue(USER_REAGREEMENT, false);
	}
}
