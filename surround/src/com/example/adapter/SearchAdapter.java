package com.example.adapter;

import java.util.List;

import com.example.bean.PointBean;
import com.example.surround.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchAdapter extends BaseAdapter {

	private List<PointBean> mList;

	private LayoutInflater mInflater;

	public SearchAdapter(Context context, List<PointBean> list) {
		mInflater = LayoutInflater.from(context);
		mList = list;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int positon, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.search_list, null);
			viewHolder.mName = (TextView) convertView.findViewById(R.id.name);
			viewHolder.mAddress = (TextView) convertView
					.findViewById(R.id.address);
			viewHolder.mTelephone = (TextView) convertView
					.findViewById(R.id.telephone);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.mName.setText(mList.get(positon).name);
		viewHolder.mAddress.setText(mList.get(positon).address);
		viewHolder.mTelephone.setText(mList.get(positon).telephone);
		
		
		return convertView;
	}

	class ViewHolder {
		public TextView mName;
		public TextView mAddress;
		public TextView mTelephone;
	}

}
