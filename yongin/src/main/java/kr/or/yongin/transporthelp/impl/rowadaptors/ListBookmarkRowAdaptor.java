package kr.or.yongin.transporthelp.impl.rowadaptors;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ionemax.iomlibrarys.log.Logview;

import java.util.ArrayList;
import java.util.HashMap;

import kr.or.yongin.transporthelp.R;

public class ListBookmarkRowAdaptor extends ArrayAdapter<HashMap<String, String>>
{
	private final String THIS_TAG="ListBookmarkRowAdaptor";

	public final static int MAX_VIEW_ROW_ITEM = 3;	// Row를 표현하는 XML 종료

	private Context context;
	private ViewHolder m_holder;

	private ArrayList<HashMap<String, String>> m_items;
	private final int tvResourceID;
	private View m_preview = null;

	private int mItemSelect = -1;

	public ListBookmarkRowAdaptor(Context context, int textViewResourceId, ArrayList<HashMap<String, String>> objects)
	{
		super(context, textViewResourceId, objects);

		this.context = context;
		this.m_items = objects;

		this.tvResourceID = textViewResourceId;
		mItemSelect = -1;
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
		Logview.Logwrite(THIS_TAG, "getView : " + position);
		View row = convertView;

		if (convertView == null)
		{
			LayoutInflater inflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflator.inflate(tvResourceID, null);

			m_holder = new ViewHolder();
			m_holder.list_item0 = (TextView)row.findViewById(R.id.list_item0);
			m_holder.list_item1 = (TextView)row.findViewById(R.id.list_item1);
			m_holder.list_item2 = (TextView)row.findViewById(R.id.list_item2);
			m_holder.list_item3 = (TextView)row.findViewById(R.id.list_item3);
			m_holder.list_item4 = (TextView)row.findViewById(R.id.list_item4);

			row.setTag(m_holder);
		}
		else
		{
			m_holder = (ViewHolder)convertView.getTag();
		}
		
		HashMap<String, String> item = (HashMap<String, String>)m_items.get(position);
		
		if (item != null)
		{
			m_holder.list_item0.setText(item.get("list_item0"));
			m_holder.list_item1.setText(item.get("list_item1"));
			m_holder.list_item2.setText(item.get("list_item2"));
			m_holder.list_item3.setText(item.get("list_item3"));
			m_holder.list_item4.setText(item.get("list_item4"));
		}

		int color = 0;
		if (mItemSelect != position) {
			if (position % 2 == 0)
				color = Color.WHITE;
			else
				color = Color.rgb(238, 238, 238);
		} else {
			color = Color.rgb(209,205,253);
		}

		row.setBackgroundColor(color);

		return row;
	}

	public void setItemSelectIndex(int index)
	{
		this.mItemSelect = index;
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
		TextView  list_item0;
		TextView  list_item1;
		TextView  list_item2;
		TextView  list_item3;
		TextView  list_item4;
	}
}
