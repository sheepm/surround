package com.example.fragment;

import java.util.ArrayList;
import java.util.List;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.Tip;
import com.example.activity.SearchActivity;
import com.example.surround.R;
import com.example.util.Constants;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

public class ZhoubianFragment extends Fragment implements OnClickListener{

	private View view;
	private AutoCompleteTextView mCompleteTextView;
	private ImageView mWangba;
	private ImageView mCanyin;
	private ImageView mJiudian;
	private ImageView mYiyuan;
	private ImageView mXuexiao;
	private ImageView mChaoshi;
	private ImageView mDianyingyuan;
	private ImageView mYinhang;
	private ImageView mJingdian;
	private ImageView mKTV;
	private ImageView mJiayouzhan;
	private ImageView mDitie;


	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.zhoubian_fragment, container, false);
		initView();
		setonclicklistener();
		return view;
	}


	/**
	 * 设置点击事件
	 */
	private void setonclicklistener() {
		mWangba.setOnClickListener(this);
		mCanyin.setOnClickListener(this);
		mJiudian.setOnClickListener(this);
		mYiyuan.setOnClickListener(this);
		mXuexiao.setOnClickListener(this);
		mChaoshi.setOnClickListener(this);
		mDianyingyuan.setOnClickListener(this);
		mYinhang.setOnClickListener(this);
		mJingdian.setOnClickListener(this);
		mKTV.setOnClickListener(this);
		mJiayouzhan.setOnClickListener(this);
		mDitie.setOnClickListener(this);
	}


	private void initView() {
		mCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.autocomplete);
		mCompleteTextView.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String nexText = s.toString();
				Inputtips inputtips = new Inputtips(getActivity(), new InputtipsListener() {
					
					@Override
					public void onGetInputtips(List<Tip> tipList, int rCode) {
						if (rCode ==  0) {
							List<String> list = new ArrayList<String>();
							for (int i = 0; i < tipList.size(); i++) {
								list.add(tipList.get(i).getName());
							}
							ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
									getActivity().getApplicationContext(),
									R.layout.route_inputs, list);
							mCompleteTextView.setAdapter(aAdapter);
							aAdapter.notifyDataSetChanged();
						}
						
					}
				});
				try {
					inputtips.requestInputtips(nexText, "");
				} catch (AMapException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable editable) {
				
			}
		});
		
		
		mWangba = (ImageView) view.findViewById(R.id.zhoubian_wangba);
		mCanyin = (ImageView) view.findViewById(R.id.canyin);
		mJiudian = (ImageView) view.findViewById(R.id.jiudian);
		mYiyuan = (ImageView) view.findViewById(R.id.yiyuan);
		mXuexiao = (ImageView) view.findViewById(R.id.xuexiao);
		mChaoshi = (ImageView) view.findViewById(R.id.chaoshi);
		mDianyingyuan = (ImageView) view.findViewById(R.id.dianyingyuan);
		mYinhang = (ImageView) view.findViewById(R.id.bank);
		mJingdian = (ImageView) view.findViewById(R.id.jingdian);
		mKTV = (ImageView) view.findViewById(R.id.ktv);
		mJiayouzhan = (ImageView) view.findViewById(R.id.jiayouzhan);
		mDitie = (ImageView) view.findViewById(R.id.ditie);
	}


	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.zhoubian_wangba:
			Intent intent = new Intent();
			intent.setClass(getActivity(),SearchActivity.class);
			intent.putExtra("request", Constants.httpArgWangba);
			intent.putExtra("text", "网吧");
			startActivity(intent);
			break;
		case R.id.canyin:
			Intent intent2 = new Intent();
			intent2.setClass(getActivity(),SearchActivity.class);
			intent2.putExtra("request", Constants.httpArgCanyin);
			intent2.putExtra("text", "餐饮");
			startActivity(intent2);
			break;
		case R.id.jiudian:
			Intent intent3 = new Intent();
			intent3.setClass(getActivity(),SearchActivity.class);
			intent3.putExtra("request", Constants.httpArgJiudian);
			intent3.putExtra("text", "酒吧");
			startActivity(intent3);
			break;
		case R.id.yiyuan:
			Intent intent4 = new Intent();
			intent4.setClass(getActivity(),SearchActivity.class);
			intent4.putExtra("request", Constants.httpArgYiyuan);
			intent4.putExtra("text", "医院");
			startActivity(intent4);
			break;
		case R.id.xuexiao:
			Intent intent5 = new Intent();
			intent5.setClass(getActivity(),SearchActivity.class);
			intent5.putExtra("request", Constants.httpArgXuexiao);
			intent5.putExtra("text", "学校");
			startActivity(intent5);
			break;
		case R.id.chaoshi:
			Intent intent6 = new Intent();
			intent6.setClass(getActivity(),SearchActivity.class);
			intent6.putExtra("request", Constants.httpArgCaoshi);
			intent6.putExtra("text", "超市");
			startActivity(intent6);
			break;
		case R.id.dianyingyuan:
			Intent intent7 = new Intent();
			intent7.setClass(getActivity(),SearchActivity.class);
			intent7.putExtra("request", Constants.httpArgDianyingyuan);
			intent7.putExtra("text", "电影院");
			startActivity(intent7);
			break;
		case R.id.bank:
			Intent intent8 = new Intent();
			intent8.setClass(getActivity(),SearchActivity.class);
			intent8.putExtra("request", Constants.httpArgYinhang);
			intent8.putExtra("text", "银行");
			startActivity(intent8);
			break;
		case R.id.jingdian:
			Intent intent9 = new Intent();
			intent9.setClass(getActivity(),SearchActivity.class);
			intent9.putExtra("request", Constants.httpArgJingdian);
			intent9.putExtra("text", "景点");
			startActivity(intent9);
			break;
		case R.id.ktv:
			Intent intent10 = new Intent();
			intent10.setClass(getActivity(),SearchActivity.class);
			intent10.putExtra("request", Constants.httpArgKTV);
			intent10.putExtra("text", "KTV");
			startActivity(intent10);
			break;
			
		case R.id.jiayouzhan:
			Intent intent11 = new Intent();
			intent11.setClass(getActivity(),SearchActivity.class);
			intent11.putExtra("request", Constants.httpArgJiayouzhan);
			intent11.putExtra("text", "加油站");
			startActivity(intent11);
			break;
		case R.id.ditie:
			Intent intent12 = new Intent();
			intent12.setClass(getActivity(),SearchActivity.class);
			intent12.putExtra("request", Constants.httpArgDitie);
			intent12.putExtra("text", "地铁");
			startActivity(intent12);
			break;

		default:
			break;
		}
	}
	
	


}
