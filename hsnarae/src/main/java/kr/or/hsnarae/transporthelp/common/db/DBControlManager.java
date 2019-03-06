package kr.or.hsnarae.transporthelp.common.db;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.ionemax.iomlibrarys.log.Logview;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class DBControlManager 
{
	private static final String THIS_TAG = "DBControlManager";
	
	private dbHelper sql2dbhelper = null;
	private static SQLiteDatabase sql2db = null;
	
	private Context context = null;

	public DBControlManager()
	{
		
	}
	
	public void dbInitialize(Context context)
	{
		this.context = context;
		
		if (sql2dbhelper == null)
		{
			sql2dbhelper = new dbHelper(context, DBSchema.DB_NAME);
			sql2db = sql2dbhelper.getWritableDatabase();
		}

		Logview.Logwrite(THIS_TAG, "dbInitialize");
	}
	
	public void dbTerminate()
	{
		if (sql2db != null)
			sql2db.close();
		sql2db = null;
		
		if (sql2dbhelper != null)
			sql2dbhelper.close();
		sql2dbhelper = null;
		
		Logview.Logwrite(THIS_TAG, "dbTerminate");
	}
	
	public static SelectHelper dbSelect(String sql)
	{
		Logview.Logwrite( THIS_TAG, "dbSelect");

		SelectHelper map = new SelectHelper ();
		if (sql2db == null) {
			Logview.Logwrite( THIS_TAG, "dbSelect can not open");
			return map;
		}
		
		Cursor cursor = null;
		try
		{
			cursor = sql2db.rawQuery(sql, null);
		
			if (cursor != null)
			{
				cursor.moveToFirst();
				if (cursor.getCount() > 0)
				{
					map.setColumns(cursor.getColumnNames());
					
					String key = "", value = "";
					do
					{
						ArrayList<BasicNameValuePair> row = new ArrayList<BasicNameValuePair>();
						
						for(int i=0; i<cursor.getColumnCount(); i++)
						{
							key = cursor.getColumnName(i);
							value = cursor.getString(i);
							
							row.add(new BasicNameValuePair(key, value));
						}
						
						map.setValue(row);
						
					} while(cursor.moveToNext());
				}
			}
			
		} catch (SQLException  e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return map;
	}
	
	public static boolean dbTransaction(String[] sql)
	{
		Logview.Logwrite(THIS_TAG, "dbTransaction");
		
		boolean bret = false;
		
		if (sql2db == null || sql == null || sql.length < 1)
			return bret;

		sql2db.beginTransaction();
		try
		{
			for (String szSQL : sql)
			{
				try
				{
					sql2db.execSQL(szSQL);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			bret = true;
			sql2db.setTransactionSuccessful();
		} catch (SQLException  e) {
			e.printStackTrace();
		} finally {
			sql2db.endTransaction();
        }

		return bret;
	}
	
	public static boolean dbTransaction(List<String> sql)
	{
		Logview.Logwrite(THIS_TAG, "dbTransaction");
		
		boolean bret = false;
		
		if (sql2db == null || sql == null || sql.size() < 1)
			return bret;

		sql2db.beginTransaction();
		try
		{
			for (String szSQL : sql)
			{
				try
				{
					sql2db.execSQL(szSQL);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			bret = true;
			sql2db.setTransactionSuccessful();
		} catch (SQLException  e) {
			e.printStackTrace();
		} finally {
			sql2db.endTransaction();
        }

		return bret;
	}

	public static boolean dbTransaction(String sql)
	{
		Logview.Logwrite(THIS_TAG, "dbTransaction");
		
		boolean bret = false;
		
		if (sql2db == null || sql == null || sql.length() < 1)
			return bret;

		sql2db.beginTransaction();
		try
		{
			sql2db.execSQL(sql);
			
			bret = true;
			sql2db.setTransactionSuccessful();
		} catch (SQLException  e) {
			e.printStackTrace();
		} finally {
			sql2db.endTransaction();
        }
		
    	return bret;
	}

	public static boolean dbTableClear(String tablename)
	{
		Logview.Logwrite(THIS_TAG, "dbTableClear");

		boolean bret = false;

		if (sql2db == null || tablename == null || tablename.length() < 1)
			return bret;

		String sql = "delete from " + tablename;
		sql2db.beginTransaction();
		try
		{
			sql2db.execSQL(sql);

			bret = true;
			sql2db.setTransactionSuccessful();
		} catch (SQLException  e) {
			e.printStackTrace();
		} finally {
			sql2db.endTransaction();
		}

		return bret;

	}
}
