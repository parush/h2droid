package com.frankcalise.h2droid;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EntryListAdapter extends BaseAdapter {
	
	private List<Entry> entryList;
	private Context context;
	
	public EntryListAdapter(List<Entry> _entryList, Context _context) {
		this.entryList = _entryList;
		this.context = _context;
	}

	@Override
	public int getCount() {
		return entryList.size();
	}

	@Override
	public Entry getItem(int position) {
		return entryList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// need to add list item reuse
		Entry e = entryList.get(position);
		
		LinearLayout itemLayout = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.entry_list_item, parent, false);
		
		TextView dateTextView = (TextView)itemLayout.findViewById(R.id.entry_date_textview);
		dateTextView.setText(e.getDate());
		
		TextView amountTextView = (TextView)itemLayout.findViewById(R.id.entry_amount_textview);
		amountTextView.setText(String.format("%.1f", e.getNonMetricAmount()));
		
		return itemLayout;
	}

}
