package kr.or.yongin.transporthelp.impl.rowadaptors;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;

import java.util.ArrayList;
import java.util.HashMap;

import kr.or.yongin.transporthelp.R;


public class ListHistoryRowAdaptor extends ArrayAdapter<HashMap<String, String>>
{
	public final static int MAX_VIEW_ROW_ITEM = 3;	// Row를 표현하는 XML 종료

	private Context context;
	private ViewHolder m_holder;

	private ArrayList<HashMap<String, String>> m_items;
	private final int tvResourceID;
	private View m_preview = null;
	private View.OnClickListener mButtonClick;

	public ListHistoryRowAdaptor(Context context, int textViewResourceId, ArrayList<HashMap<String, String>> objects, View.OnClickListener onbuttnClick)
	{
		super(context, textViewResourceId, objects);

		this.context = context;
		this.m_items = objects;

		this.tvResourceID = textViewResourceId;
		this.mButtonClick = onbuttnClick;
	}

	@Override
	protected void finalize() throws Throwable {
		free();
		super.finalize();
	}

	@Override
	public int getCount() 
	{
		return (m_items == null?0:m_items.size());
	}
	
	@Override
	public HashMap<String, String> getItem(int position) 
	{
		return (m_items == null?null:m_items.get(position));
	}
	
	@Override
	public long getItemId(int position) 
	{
		return position;
	}
	
	@Override
	public int getViewTypeCount() 
	{
		return MAX_VIEW_ROW_ITEM;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View row = convertView;
		
		if (convertView == null)
		{
			LayoutInflater inflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflator.inflate(tvResourceID, null);

			m_holder = new ViewHolder();
			m_holder.list_group_item = (TextView)row.findViewById(R.id.list_group_item);
			m_holder.btnBookingCancel = (BootstrapButton)row.findViewById(R.id.btnBookingCancel);
			m_holder.list_item1 = (TextView)row.findViewById(R.id.list_item1);
			m_holder.list_item2 = (TextView)row.findViewById(R.id.list_item2);
			m_holder.list_item3 = (TextView)row.findViewById(R.id.list_item3);
			m_holder.list_item4 = (TextView)row.findViewById(R.id.list_item4);
			m_holder.list_item5 = (TextView)row.findViewById(R.id.list_item5);
			m_holder.list_item6 = (TextView)row.findViewById(R.id.list_item6);
			m_holder.list_item7 = (TextView)row.findViewById(R.id.list_item7);
			m_holder.list_item8 = (TextView)row.findViewById(R.id.list_item8);
			m_holder.list_item9 = (TextView)row.findViewById(R.id.list_item9);
			m_holder.list_item10 = (TextView)row.findViewById(R.id.list_item10);
			m_holder.list_item11 = (TextView)row.findViewById(R.id.list_item11);
//			m_holder.list_item12 = (TextView)row.findViewById(R.id.list_item12);

			row.setTag(m_holder);
		}
		else
		{
			m_holder = (ViewHolder)convertView.getTag();
		}
		
		HashMap<String, String> item = (HashMap<String, String>)m_items.get(position);
		
		if (item != null)
		{
			m_holder.list_item1.setText(item.get("list_item1"));
			m_holder.list_item2.setText(item.get("list_item2"));
			m_holder.list_item3.setText(item.get("list_item3"));
			m_holder.list_item4.setText(item.get("list_item4"));
			m_holder.list_item5.setText(item.get("list_item5"));
			m_holder.list_item6.setText(item.get("list_item6"));
			m_holder.list_item7.setText(item.get("list_item7"));
			m_holder.list_item8.setText(item.get("list_item8"));
			m_holder.list_item9.setText(item.get("list_item9"));
			m_holder.list_item10.setText(item.get("list_item10"));
			m_holder.list_item11.setText(item.get("list_item11"));
//			m_holder.list_item12.setText(item.get("list_item12"));

			String groupItem = item.get("list_group_item");
			m_holder.list_group_item.setText(groupItem);

			if (item.get("list_item9").toUpperCase().trim().equalsIgnoreCase("대기") ||
					item.get("list_item9").toUpperCase().trim().equalsIgnoreCase("접수")	)
			{
				m_holder.btnBookingCancel.setVisibility(View.VISIBLE);
				m_holder.btnBookingCancel.setTag(String.valueOf(position));
				m_holder.btnBookingCancel.setOnClickListener(this.mButtonClick);
			} else {
				m_holder.btnBookingCancel.setVisibility(View.GONE);
				m_holder.btnBookingCancel.setTag("");
			}

			LinearLayout layout = (LinearLayout)row.findViewById(R.id.layoutHistoryGroup);
			if (groupItem == null || groupItem.length() < 1) {
				layout.setVisibility(View.GONE);
			} else {
				layout.setVisibility(View.VISIBLE);

				if (groupItem.equalsIgnoreCase("예약배차"))
					m_holder.list_group_item.setBackgroundColor(Color.YELLOW);
				else if (groupItem.equalsIgnoreCase("진행배차"))
					m_holder.list_group_item.setBackgroundColor(Color.GREEN);
				else if (groupItem.equalsIgnoreCase("배차이력"))
					m_holder.list_group_item.setBackgroundColor(Color.GRAY);
			}
		}

		return row;
	}

	private void free()
	{
		if (m_items != null)
			m_items.clear();
		m_items = null;

		context = null;
		if (m_holder != null)
			m_holder = null;
	}

	class ViewHolder
	{
		BootstrapButton btnBookingCancel;
		TextView  list_group_item;
		TextView  list_item1;
		TextView  list_item2;
		TextView  list_item3;
		TextView  list_item4;
		TextView  list_item5;
		TextView  list_item6;
		TextView  list_item7;
		TextView  list_item8;
		TextView  list_item9;
		TextView  list_item10;
		TextView  list_item11;
//		TextView  list_item12;
	}
}
