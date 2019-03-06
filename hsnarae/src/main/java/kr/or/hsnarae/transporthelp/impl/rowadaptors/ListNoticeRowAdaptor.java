package kr.or.hsnarae.transporthelp.impl.rowadaptors;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import kr.or.hsnarae.transporthelp.R;
import kr.or.hsnarae.transporthelp.common.db.DBControlManager;
import kr.or.hsnarae.transporthelp.common.db.DBSchema;
import kr.or.hsnarae.transporthelp.common.db.SelectHelper;

public class ListNoticeRowAdaptor extends ArrayAdapter<HashMap<String, String>> {
    public final static int MAX_VIEW_ROW_ITEM = 3;    // Row를 표현하는 XML 종료

    private Context context;
    private ViewHolder m_holder;

    private ArrayList<HashMap<String, String>> m_items;
    private final int tvResourceID;
    private View m_preview = null;

    public ListNoticeRowAdaptor(Context context, int textViewResourceId, ArrayList<HashMap<String, String>> objects) {
        super(context, textViewResourceId, objects);

        this.context = context;
        this.m_items = objects;

        this.tvResourceID = textViewResourceId;
    }

    @Override
    protected void finalize() throws Throwable {
        free();
        super.finalize();
    }

    @Override
    public int getCount() {
        return (m_items == null ? 0 : m_items.size());
    }

    @Override
    public HashMap<String, String> getItem(int position) {
        return (m_items == null ? null : m_items.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return MAX_VIEW_ROW_ITEM;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflator.inflate(tvResourceID, null);

            m_holder = new ViewHolder();
            m_holder.list_1st_item = (TextView) row.findViewById(R.id.list_1st_item);
            //m_holder.list_2nd_item = (TextView)row.findViewById(R.id.list_2nd_item);
            //m_holder.list_3rd_item = (BootstrapButton)row.findViewById(R.id.list_3rd_item);
            //m_holder.list_4th_item = (TextView)row.findViewById(R.id.list_4th_item);

            row.setTag(m_holder);
        } else {
            m_holder = (ViewHolder) convertView.getTag();
        }

        HashMap<String, String> item = (HashMap<String, String>) m_items.get(position);

        if (item != null) {
            m_holder.list_1st_item.setText(item.get("list_1st_item"));
//			m_holder.list_2nd_item.setText(item.get("list_2nd_item"));
//			m_holder.list_3rd_item.setText(item.get("list_3rd_item"));
//			m_holder.list_3rd_item.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View view) {
//					//Logview.Logwrite("ListPOISearchRow", "btnClick : " + view.getTag());
//					BusEventProvider.getInstance().post(new SelectMessageEvent((int)(view.getTag())));
//				}
//			});
//			m_holder.list_3rd_item.setTag(position);
            //m_holder.list_4th_item.setText(item.get("list_4th_item"));
            int colorIndex = 0;

            //180206 송명진 - 공지사항 개편으로 인하여 주석처리함.
//			if (position %2 ==0)
//				colorIndex = Color.BLACK;
//			else
//				colorIndex = Color.GRAY;

            if (item.get("readYN").equalsIgnoreCase("N"))
                colorIndex = Color.BLACK;
            else
                colorIndex = Color.GRAY;


                String sql = "SELECT * FROM " + DBSchema.TABLE_NOTICE_LIST + ";";
            SelectHelper select = DBControlManager.dbSelect(sql);


//            Log.d("테스트", "값 확인하즈아ㅏㅏㅏㅏㅏ" + m_items.get(1));
//            Log.d("테스트", "값 확인하즈아ㅏㅏㅏㅏㅏ" + m_items.get(2));
//            Log.d("테스트", "값 확인하즈아ㅏㅏㅏㅏㅏ" + m_items.get(3));
//            Log.d("테스트", "값 확인하즈아ㅏㅏㅏㅏㅏ" + m_items.get(4));


            m_holder.list_1st_item.setTextColor(colorIndex);
        }

        return row;
    }

    private void free() {
        if (m_items != null)
            m_items.clear();
        m_items = null;

        context = null;
        if (m_holder != null)
            m_holder = null;
    }

    class ViewHolder {
        TextView list_1st_item;
        //TextView  list_2nd_item;
        //BootstrapButton list_3rd_item;
        //TextView  list_3rd_item;
        //TextView  list_4th_item;
    }
}
