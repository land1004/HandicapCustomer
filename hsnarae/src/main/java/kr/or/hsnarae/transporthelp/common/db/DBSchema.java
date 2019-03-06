package kr.or.hsnarae.transporthelp.common.db;

import android.provider.BaseColumns;

public class DBSchema implements BaseColumns
{
	public static final String DB_NAME = "hsnarae.db";

	public static final String TABLE_NAME_HANDITYPE = "tblHanditype";
	public static final String TABLE_NAME_HANDIDEGREE = "tblHandidegree";
	public static final String TABLE_NAME_USETYPE = "tblUsetype";
	public static final String TABLE_POI_HISTORY = "tblPOIHistory";
	public static final String TABLE_CALL_HISTORY = "tblCallHistory";
	public static final String TABLE_NOTICE_LIST = "tblNoticeList";

	public static final String COL_NAME = "name";
	public static final String COL_CODE = "code";

	public static final String COL_DETAIL_ADDR = "detailAddress";
	public static final String COL_ROAD_ADDR = "roadFullAddress";
	public static final String COL_JIBUN_ADDR = "jibunFullAddress";
	public static final String COL_POS_X = "posx";
	public static final String COL_POS_Y = "posy";
	public static final String COL_TYPE = "type";
	public static final String COL_REGDATE = "regdate";

	public static final String COL_BM_STARTDETAIL = "startdetail";
	public static final String COL_BM_STARTADDR = "startaddr";
	public static final String COL_BM_ENDDETAIL = "enddetail";
	public static final String COL_BM_ENDADDR = "endaddr";
	public static final String COL_BM_COMPANION = "companion";
	public static final String COL_BM_STARTX = "startx";
	public static final String COL_BM_STARTY = "starty";
	public static final String COL_BM_ENDX = "endx";
	public static final String COL_BM_ENDY = "endy";
	public static final String COL_BM_USETYPE = "usetype";
	public static final String COL_BM_USETYPE_CODE = "usetypecod";
	public static final String COL_BM_WHEEL = "wheelyn";

	public static final String COL_NOTICE_SEQ = "noticeSeq";  //공지사항 고유번호
	public static final String COL_NOTICE_TITLE = "noticeTitle"; //공지사항 제목
	public static final String COL_NOTICE_DATE = "noticeDate"; //공지사항 날짜
	public static final String COL_READYN = "readYN";


	public static final String TABLE_CREAT_HDNDITYPE = "CREATE TABLE " + TABLE_NAME_HANDITYPE +
			"(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "name" + " text, "
			+ "code" + " text "
			+ ");" ;

	public static final String TABLE_CREAT_HDNDIDEGREE = "CREATE TABLE " + TABLE_NAME_HANDIDEGREE +
			"(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "name" + " text, "
			+ "code" + " text "
			+ ");" ;

	public static final String TABLE_CREAT_USETYPE = "CREATE TABLE " + TABLE_NAME_USETYPE +
			"(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "name" + " text, "
			+ "code" + " text "
			+ ");" ;

	public static final String TABLE_CREAT_POIHISTORY = "CREATE TABLE " + TABLE_POI_HISTORY +
			"(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COL_DETAIL_ADDR + " text, "
			+ COL_ROAD_ADDR + " text, "
			+ COL_JIBUN_ADDR + " text, "
			+ COL_POS_X + " text, "
			+ COL_POS_Y + " text, "
			+ COL_TYPE + " text, "
			+ COL_REGDATE + " text "
			+ ");" ;

	public static final String TABLE_CREAT_CALLHISTORY = "CREATE TABLE " + TABLE_CALL_HISTORY +
			"(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COL_BM_STARTDETAIL + " text, "
			+ COL_BM_STARTADDR + " text, "
			+ COL_BM_ENDDETAIL + " text, "
			+ COL_BM_ENDADDR + " text, "
			+ COL_BM_COMPANION + " text, "
			+ COL_BM_STARTX + " text, "
			+ COL_BM_STARTY + " text, "
			+ COL_BM_ENDX + " text, "
			+ COL_BM_ENDY + " text, "
			+ COL_BM_USETYPE + " text, "
			+ COL_BM_USETYPE_CODE + " text, "
			+ COL_BM_WHEEL + " text, "
			+ "regdate" + " text "
			+ ");" ;

	public static final String CALL_HISTORY_COLUMN =" ('" + COL_BM_STARTDETAIL + "','" + COL_BM_STARTADDR + "','"
			+ COL_BM_ENDDETAIL + "','" + COL_BM_ENDADDR + "','" + COL_BM_COMPANION + "','" + COL_BM_STARTX + "','"
			+ COL_BM_STARTY + "','" + COL_BM_ENDX + "','" + COL_BM_ENDY + "','" + COL_BM_USETYPE + "','"
			+ COL_BM_USETYPE_CODE + "','" + COL_BM_WHEEL +"','" + "regdate') ";

	//180206 송명진 - 공지사항 리스트 테이블이 있으면 생성하지 않는다.
	public static final String TABLE_CREATE_NOTICELIST = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTICE_LIST +
			"(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COL_NOTICE_SEQ + " text, "
			+ COL_NOTICE_TITLE + " text, "
			+ COL_NOTICE_DATE + " text, "
			+ COL_READYN + " text "
			+ ");" ;
}
