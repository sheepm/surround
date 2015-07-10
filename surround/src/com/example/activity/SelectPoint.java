package com.example.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.Tip;
import com.example.surround.R;

public class SelectPoint extends Activity{
	
	private AutoCompleteTextView mSelect;
	private ListView mList;
	private Button mBtnSubmit;
	private List<String> list;
	private String textmsg = null;
	private int request;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.selectpoint);
		initview();
		updateSelect();
		getValue();
		selectItem();
	}

	/**
	 * 
	 */
	private void getValue() {
		textmsg = this.getIntent().getStringExtra("textmsg");
		request= this.getIntent().getIntExtra("request", 102);
		//设置AutoCompleteTextView的hint值
		mSelect.setHint(textmsg);
	}

	/**
	 * 设置listview item的点击事件
	 */
	private void selectItem() {
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long id) {
				String location = list.get(position).toString().trim();
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("MSG", location);
				intent.putExtras(bundle);
				SelectPoint.this.setResult(request, intent);
				SelectPoint.this.finish();
			}
		});
		
		mBtnSubmit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				String location = mSelect.getText().toString().trim();
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("MSG", location);
				intent.putExtras(bundle);
				SelectPoint.this.setResult(request, intent);
				SelectPoint.this.finish();
			}
		});
	}


	/**
	 * 初始化layout中的控件
	 */
	private void initview() {
		mSelect = (AutoCompleteTextView) findViewById(R.id.autoselect);
		mList = (ListView) findViewById(R.id.list_select);
		mBtnSubmit = (Button) findViewById(R.id.btn_submit);
	}
	
	/**
	 * 设置动态更新数据
	 */
	private void updateSelect(){
		mSelect.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String nexText = s.toString();
				if (s != null) {
					mBtnSubmit.setVisibility(View.VISIBLE);
				}
				Inputtips inputtips = new Inputtips(SelectPoint.this, new InputtipsListener() {
					
					@Override
					public void onGetInputtips(List<Tip> tipList, int rCode) {
						if (rCode ==  0) {
							list = new ArrayList<String>();
							for (int i = 0; i < tipList.size(); i++) {
								list.add(tipList.get(i).getName());
							}
							ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
									getApplicationContext(),
									R.layout.route_inputs, list);
							mList.setAdapter(aAdapter);
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
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				
			}
		});
	}
	
	

}
