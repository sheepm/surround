package com.example.fragment;

import com.example.activity.RouteActivity;
import com.example.activity.SelectPoint;
import com.example.surround.R;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class RoadFragment extends Fragment implements OnClickListener, OnFocusChangeListener{
	
	private int REQUEST_CODE = 100;

	private View view;
	private EditText mQidian;
	private EditText mZhongdian;
	
	private ImageView mSearch;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.road_fragment, container, false);
		initview();
		setOnClickListener();
		return view;
	}

	private void setOnClickListener() {
		mQidian.setOnFocusChangeListener(this);
		mZhongdian.setOnFocusChangeListener(this);
		mSearch.setOnClickListener(this);
	}

	private void initview() {
		mQidian = (EditText) view.findViewById(R.id.road_qidian);
		mZhongdian = (EditText) view.findViewById(R.id.road_zhongdian);
		mSearch = (ImageView) view.findViewById(R.id.route_search);
	}

	@Override
	public void onFocusChange(View view, boolean hasFocus) {
		switch (view.getId()) {
		case R.id.road_qidian:
			if (hasFocus) {
				Intent intent = new Intent();
				intent.putExtra("textmsg", "输入起点...");
				intent.putExtra("request", 101);
				intent.setClass(getActivity(), SelectPoint.class);
				this.startActivityForResult(intent, REQUEST_CODE);
			}
			break;

		case R.id.road_zhongdian:
			if (hasFocus) {
				Intent intent = new Intent();
				intent.putExtra("textmsg", "输入终点...");
				intent.putExtra("request", 102);
				intent.setClass(getActivity(), SelectPoint.class);
				this.startActivityForResult(intent, REQUEST_CODE);
			}
			break;

		default:
			break;
		}
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (REQUEST_CODE == requestCode) {
			switch (resultCode) {
			case 101:
				Bundle bundle = data.getExtras();
				String strReturn = bundle.getString("MSG").trim();
				mQidian.setText(strReturn);
				break;
			case 102:
				Bundle bundle2 = data.getExtras();
				String strReturn2 = bundle2.getString("MSG").trim();
				mZhongdian.setText(strReturn2);
				break;

			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.route_search:
			if (TextUtils.isEmpty(mZhongdian.getText()) ) {
				Toast.makeText(getActivity(), "请输入完整的起点终点", 0).show();
			}
			Intent intent = new Intent();
			intent.setClass(getActivity(),RouteActivity.class);
			intent.putExtra("startpoint", mQidian.getText().toString());
			intent.putExtra("endpoint", mZhongdian.getText().toString());
			startActivity(intent);
			break;

		default:
			break;
		}
	}

}
