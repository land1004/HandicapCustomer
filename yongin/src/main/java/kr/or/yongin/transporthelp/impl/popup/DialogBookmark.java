package kr.or.yongin.transporthelp.impl.popup;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ionemax.iomlibrarys.log.Logview;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import kr.or.yongin.transporthelp.R;
import kr.or.yongin.transporthelp.common.db.DBControlManager;
import kr.or.yongin.transporthelp.common.db.DBSchema;
import kr.or.yongin.transporthelp.common.db.SelectHelper;
import kr.or.yongin.transporthelp.impl.rowadaptors.ListBookmarkRowAdaptor;

/**
 * Created by IONEMAX on 2017-02-09.
 */

public class DialogBookmark extends Dialog
{
    private final String THIS_TAG = "DialogBookmark";

    private final int MSG_LIST_SELECT = 0;

    private Context mContext;
    private DialogReceiveHandler mParentListener;
    private ArrayList<BOOKMARKS> mBookMarkList;
    private ListView mListview;
    private ListBookmarkRowAdaptor mListAdaptor;
    private int mSelectItem=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.dialog_bookmark);

        // 기본 설정
        TextView title = (TextView)findViewById(R.id.txtTitle);
        title.setText("즐겨찾기");

        TextView btnBack = (TextView)findViewById(R.id.btnBack);
        btnBack.setVisibility(View.GONE);

        LinearLayout layoutBTNLeft = (LinearLayout)findViewById(R.id.layoutBTNLeft);
        LinearLayout layoutBTNCenter = (LinearLayout)findViewById(R.id.layoutBTNCenter);
        LinearLayout layoutBTNRight = (LinearLayout)findViewById(R.id.layoutBTNRight);

        Button bstBTNLeft = (Button)findViewById(R.id.btnLeft);
        Button bstBTNCenter = (Button)findViewById(R.id.btnCenter);
        Button bstBTNRight = (Button)findViewById(R.id.btnRight);

        layoutBTNLeft.setVisibility(View.VISIBLE);
        layoutBTNCenter.setVisibility(View.VISIBLE);
        layoutBTNRight.setVisibility(View.VISIBLE);

        bstBTNLeft.setText("즉시");
        bstBTNCenter.setText("예약");
        bstBTNRight.setText("취소");

        bstBTNLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 즉시
                if (mSelectItem > -1)
                    doSendParent(true, false);
            }
        });

        bstBTNCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 예약
                if (mSelectItem > -1)
                    doSendParent(true, true);
            }
        });

        bstBTNRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSendParent(false, true);
            }
        });

        mListview = (ListView)findViewById(R.id.list_bookmark);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (mListAdaptor != null) {
                    mSelectItem = position;
                    mListAdaptor.setItemSelectIndex(position);
                    mListAdaptor.notifyDataSetChanged();
                }
            }
        });

        mSelectItem=-1;
    }

    @Override
    protected void onStop() {
        Logview.Logwrite(THIS_TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onStart() {
        Logview.Logwrite(THIS_TAG, "onStart");
        doBookmark();
        super.onStart();
    }

    public DialogBookmark(Context context)
    {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        mContext = context;
    }

    public DialogBookmark(Context context, DialogReceiveHandler parent)
    {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        Logview.Logwrite(THIS_TAG, "DialogBookmark");
        mContext = context;
        mParentListener = parent;
    }

    /**
     * 선택된 것으로 접수데이터를 입력하도록 한다.
     * @param flag
     * @param ordertype
     */
    private void doSendParent (boolean flag, boolean ordertype)
    {
        Intent intent = new Intent();

        if (flag) {
            BOOKMARKS item = mBookMarkList.get(mSelectItem);
            Bundle bundle = new Bundle();

            bundle.putString("startDetail", item.startDetail);
            bundle.putString("startAddr", item.startAddr);
            bundle.putString("endDetail", item.endDetail);
            bundle.putString("endAddr", item.endAddr);
            bundle.putString("startPosx", item.startPosx);
            bundle.putString("startPosy", item.startPosy);
            bundle.putString("endPosx", item.endPosx);
            bundle.putString("endPosy", item.endPosy);
            bundle.putString("companion", item.companion);
            bundle.putString("usetype", item.usetype);
            bundle.putString("usetypecode", item.usetypecode);
            bundle.putString("wheelyn", item.wheelyn);

            intent.putExtra("flag", flag);
            intent.putExtra("ordertype", (ordertype ? "B": "S"));
            intent.putExtra("data", bundle);

        } else {
            intent.putExtra("flag", flag);
        }

        mParentListener.handlerReceive(intent);
    }

    private void viewList()
    {
        Logview.Logwrite(THIS_TAG, "viewList");
        if (mBookMarkList == null) return;

        ArrayList<HashMap<String,String>> arrayList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map;

        for (BOOKMARKS item : mBookMarkList)
        {
            map = new HashMap<String, String>();
            map.put("list_item0", item.startDetail);
            map.put("list_item1", item.endDetail);
            map.put("list_item2", item.companion + " 명");
            map.put("list_item3", (item.wheelyn.equalsIgnoreCase("Y") ? "사용" : "미사용"));
            map.put("list_item4", item.usetype);

            arrayList.add(map);
        }

        mListAdaptor = new ListBookmarkRowAdaptor(mContext, R.layout.listbookmark_items_row, arrayList);
        mListview.setAdapter(mListAdaptor);
    }

    private void doBookmark ()
    {
        Logview.Logwrite(THIS_TAG, "doBookmark");
        if (mBookMarkList == null)
            mBookMarkList = new ArrayList<BOOKMARKS>();
        mBookMarkList.clear();

        String sql = "SELECT * FROM " + DBSchema.TABLE_CALL_HISTORY + " ORDER BY _id DESC;";
        SelectHelper selectHelper = DBControlManager.dbSelect(sql);

        BOOKMARKS item;
        if (selectHelper != null && selectHelper.getCount() > 0)
        {

            selectHelper.moveFirst();

            do{
                item = new BOOKMARKS();
                item.startDetail = selectHelper.getValue(DBSchema.COL_BM_STARTDETAIL);
                item.startAddr = selectHelper.getValue(DBSchema.COL_BM_STARTADDR);
                item.endDetail = selectHelper.getValue(DBSchema.COL_BM_ENDDETAIL);
                item.endAddr = selectHelper.getValue(DBSchema.COL_BM_ENDADDR);
                item.startPosx = selectHelper.getValue(DBSchema.COL_BM_STARTX);
                item.startPosy = selectHelper.getValue(DBSchema.COL_BM_STARTY);
                item.endPosx = selectHelper.getValue(DBSchema.COL_BM_ENDX);
                item.endPosy = selectHelper.getValue(DBSchema.COL_BM_ENDY);
                item.companion = selectHelper.getValue(DBSchema.COL_BM_COMPANION);
                item.usetype = selectHelper.getValue(DBSchema.COL_BM_USETYPE);
                item.usetypecode = selectHelper.getValue(DBSchema.COL_BM_USETYPE_CODE);
                item.wheelyn = selectHelper.getValue(DBSchema.COL_BM_WHEEL);
                if ( item.wheelyn == null ||  item.wheelyn.length() < 1)
                    item.wheelyn = "N";

                mBookMarkList.add(item);

            } while(selectHelper.moveNext());
        } else {
            item = new BOOKMARKS();
            item.startDetail = "즐겨찾기가 없습니다.";
            item.startAddr = "";
            item.endDetail = "";
            item.endAddr = "";
            item.startPosx = "";
            item.startPosy = "";
            item.endPosx = "";
            item.endPosy = "";
            item.companion = "0";
            item.usetype = "";
            item.usetypecode = "";
            item.wheelyn = "N";

            mBookMarkList.add(item);
        }

        viewList ();
    }

    private void selectItem(Message msg)
    {
        switch (msg.what)
        {
            case MSG_LIST_SELECT:
                break;
        }
    }

    private final DialogHandler handler = new DialogHandler (this);

    private static class DialogHandler extends Handler
    {
        private final WeakReference<DialogBookmark> mDialog;

        public DialogHandler(DialogBookmark activity)
        {
            mDialog = new WeakReference<DialogBookmark>(activity);
        }

        @Override
        public void handleMessage(Message msg)
        {
            DialogBookmark activity = mDialog.get();
            if (activity != null)
            {
                activity.selectItem(msg);
            }
        }
    }

    public class BOOKMARKS
    {
        public String startDetail="";
        public String startAddr="";
        public String endDetail="";
        public String endAddr="";
        public String startPosx="";
        public String startPosy="";
        public String endPosx="";
        public String endPosy="";
        public String companion="";
        public String wheelyn="";
        public String usetype="";
        public String usetypecode="";

        public BOOKMARKS ()
        {

        }
    }
}
