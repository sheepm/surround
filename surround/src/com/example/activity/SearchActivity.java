package com.example.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.adapter.SearchAdapter;
import com.example.bean.LocationBean;
import com.example.bean.PointBean;
import com.example.surround.R;
import com.example.util.Constants;
import com.example.util.JsonUtil;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

public class SearchActivity extends Activity {
	


	private ListView mList;
	private TextView mTxSearch;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search);
		initview();
		String search = this.getIntent().getStringExtra("request");
		String tx_search = this.getIntent().getStringExtra("text");
		mTxSearch.setText(tx_search);
		new request().execute(Constants.httpUrl , search);
	
	}

	/**
	 * �õ�������json����
	 */
	private List<PointBean>  getValue(String httpurl , String httparg) {
		String jsonstr = JsonUtil.request(httpurl , httparg);
		List<PointBean> mPointBeans = new ArrayList<PointBean>();
		JSONObject jsonObject = null ;
		try {
			jsonObject = new JSONObject(jsonstr);
			JSONArray pointarray = jsonObject.optJSONArray("pointList");
			for (int i = 0; i < pointarray.length(); i++) {
				PointBean bean = new PointBean();
//				LocationBean bean2 = null;
				JSONObject jsonObject2 = pointarray.optJSONObject(i);
				bean.setName(jsonObject2.optString("name"));
				bean.setAddress(jsonObject2.optString("address")); 
				
//				JSONObject location = new JSONObject(jsonObject2.optString("location"));
//				bean2.setLat(location.optString("lat"));
//				bean2.setLng(location.optString("lng"));
				
				JSONObject information=new JSONObject(jsonObject2.optString("additionalInformation"));
					String name = information.optString("name");
					bean.setName(name);
					String tel = information.optString("telephone");
					bean.setTelephone(tel);
		
				mPointBeans.add(bean);
			}
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return mPointBeans;
		
	}


	private void initview() {
		mList = (ListView) findViewById(R.id.list_search);
		mTxSearch = (TextView) findViewById(R.id.tx_search);
	}
	
class request extends AsyncTask<String, Void, List<PointBean>>{
		
		

		@Override
		protected List<PointBean> doInBackground(String... arg0) {
			
			return getValue(arg0[0],arg0[1]);
			
		}

		@Override
		protected void onPostExecute(List<PointBean> result) {
		super.onPostExecute(result);
		SearchAdapter adapter = new SearchAdapter(SearchActivity.this, result);
		mList.setAdapter(adapter);
		}
		
		
	}

}
