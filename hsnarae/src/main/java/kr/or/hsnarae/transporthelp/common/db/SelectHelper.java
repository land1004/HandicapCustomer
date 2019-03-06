package kr.or.hsnarae.transporthelp.common.db;

import android.util.SparseArray;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class SelectHelper 
{
	private SparseArray<ArrayList<BasicNameValuePair>> m_select = null;
	private String[] m_columns = null;
	private int m_nIndex = 0, m_nSize = 0;
	
	public SelectHelper()
	{
		clear();
	}
	
	public void clear()
	{
		m_nIndex = 0;
		m_select = null;
		m_columns = null;
	}
	
	public int getCount ()
	{
		return (m_select == null ? 0 : m_select.size());
	}
	
	public int getColumnCount()
	{
		int nCount = 0;
		
		if (m_select != null)
		{
			ArrayList<BasicNameValuePair> row = m_select.get(0);
			nCount = row.size();
		}
		
		return nCount;
	}
	
	public void moveFirst()
	{
		m_nIndex = 0;
	}
	
	public void moveLast()
	{
		m_nIndex = m_select.size()-1;
	}
	
	public boolean moveNext()
	{
		m_nIndex ++;
		
		return (m_nIndex < m_select.size() ? true : false);
	}
	
	public boolean movePrevious()
	{
		m_nIndex --;
		
		return (m_nIndex < m_select.size() ? true : false);
	}
	
	public boolean setRowIndex(int index)
	{
		if (index >= m_select.size()) return false;
		
		m_nIndex = index;
		
		return true;
	}
	
	public String getValue(int nItem) {
		ArrayList<BasicNameValuePair> row = m_select.get(m_nIndex);

		try {
			if (nItem >= row.size()) return "";

			BasicNameValuePair item = row.get(nItem);

			return item.getValue();
		} catch (Exception e) {
			return "";
		}
	}

	public String getValue(String column)
	{
		ArrayList<BasicNameValuePair> row = m_select.get(m_nIndex);

		try
		{
			for (BasicNameValuePair item : row)
			{
				if (item.getName().equalsIgnoreCase(column))
					return item.getValue();
			}
		}
		catch(Exception e)
		{
			return "";
		}

		return "";
	}
	
	public BasicNameValuePair getKeyValue(int nItem)
	{
		ArrayList<BasicNameValuePair> row = m_select.get(m_nIndex);
		
		if (nItem >= row.size()) return null;
		
		BasicNameValuePair item = row.get(nItem); 
		
		return item;
	}
	
	public void setColumns(String[] columns)
	{
		if (columns != null)
		{
			m_columns = new String[columns.length];
			int index = 0;
			for (String str : columns)
				m_columns[index ++] = str;
		}
	}
	
	public void setValue(ArrayList<BasicNameValuePair> value)
	{
		if (m_select == null)
		{
			m_select = new SparseArray<ArrayList<BasicNameValuePair>>();
			m_nSize = 0;
		}
		
		m_select.put(m_nSize++, value);
	}
	
}
