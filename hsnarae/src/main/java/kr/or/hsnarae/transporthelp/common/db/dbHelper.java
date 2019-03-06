package kr.or.hsnarae.transporthelp.common.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ionemax.iomlibrarys.log.Logview;
import com.ionemax.iomlibrarys.util.Util;

public class dbHelper extends SQLiteOpenHelper
{
	private final String THIS_TAG = "dbHelper";
	private static final int DATABASE_VERSION =2;		// 테이블 정보가 수정되면 버전을 조정한다.
	private static String DATAVASE_NAME = "";
	
//	 private static final String FILE_PATH = Environment.getExternalStorageDirectory()
//             													.getAbsolutePath() + File.separator +"customer/";
	private static final String FILE_PATH = Util.getWorkingFolder("hanarae");
	
	public dbHelper(Context context, String dbname) 
	{
		super(context, FILE_PATH + dbname, null, DATABASE_VERSION);
		
		// 데이터 백업
		//backupDB(context);
		DATAVASE_NAME = dbname;
		Logview.Logwrite(THIS_TAG, "SQLite onCreate : " + FILE_PATH + DATAVASE_NAME);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		System.out.println("SQLite > onCreate");
		if (DATAVASE_NAME.equals(DBSchema.DB_NAME))
		{
			// 기본정보
			db.execSQL(DBSchema.TABLE_CREAT_HDNDITYPE);
			db.execSQL(DBSchema.TABLE_CREAT_HDNDIDEGREE);
			db.execSQL(DBSchema.TABLE_CREAT_USETYPE);

			// 위치 조회 결과 검색
			db.execSQL(DBSchema.TABLE_CREAT_POIHISTORY);
			// 사용이력
			db.execSQL(DBSchema.TABLE_CREAT_CALLHISTORY);
			// 공지사항 리스트 관련 180205 송명진
			db.execSQL(DBSchema.TABLE_CREATE_NOTICELIST);

			System.out.println("SQLite onCreate success");
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		System.out.println("onUpgrade > oldVersion : " + oldVersion + " / new :" + newVersion);
		if (DATAVASE_NAME.equals(DBSchema.DB_NAME))
		{
			// 2015.06.23 CKS
			// DB 변경처리 수정
			db.beginTransaction();
			switch(oldVersion)
			{
				case 1:
					// 사용이력
					db.execSQL(DBSchema.TABLE_CREAT_CALLHISTORY);
					break;
				default:
					break;
			}
			db.setTransactionSuccessful();
			db.endTransaction();
			// 전체를 삭제하고 새로 만들 경우 처리
			//allTableDrop (db); 
		}
		else
		{
			// Table이 없을 경우를 위하여
			onCreate(db);	
		}
	}
	
	private void allTableDrop (SQLiteDatabase db)
	{
		// 전체를 삭제하고 새로 만들 경우 처리
		db.execSQL("DROP TABLE IF EXITS downloaddate");
		
		onCreate(db);	
	}
	
	public void deleteOldData(SQLiteDatabase db)
	{
		// 현재 날짜에서 5일 이전 데이터는 모두 삭제처리
		Logview.Logwrite(THIS_TAG, "deleteOldData");
		
		try
		{
			db.beginTransaction();
			
			// 배달전
			db.execSQL("delete from delivery_before where scandate < strftime('%Y%m%d', 'now', '-4 day')");
			
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}
}
