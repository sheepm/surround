package com.example.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.overlay.PoiOverlay;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Tip;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.example.surround.R;
import com.example.util.AMapUtil;

public class MapFragment extends Fragment implements LocationSource,
		AMapLocationListener ,OnClickListener,OnPoiSearchListener {

	private View view;
	private MapView mapView;
	private AutoCompleteTextView mCompleteTextView;
	private AMap aMap;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private String keyWord = "";// Ҫ�����poi�����ؼ���
	private ProgressDialog progDialog = null;// ����ʱ������
	private PoiSearch.Query query;
	private PoiSearch poiSearch;
	private PoiResult poiResult;
	private Button mBtnSearch;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.map_fragment, container, false);
		mapView = (MapView) view.findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		initView();
		return view;
	}

	private void initView() {
		if (aMap == null) {
			aMap = mapView.getMap();
		}
		setUpMap();
		mBtnSearch =(Button) view.findViewById(R.id.title_search);
		mBtnSearch.setOnClickListener(this);
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
		
	}
	
	/**
	 * ���������ť
	 */
	public void searchButton() {
		keyWord = AMapUtil.checkEditText(mCompleteTextView);
		if ("".equals(keyWord)) {
			Toast.makeText(getActivity(), "��������Ҫ����������",Toast.LENGTH_SHORT).show();
			return;
		} else {
			doSearchQuery();
		}
	}
	
	/**
	 * ��ʼ����poi����
	 */
	protected void doSearchQuery() {
		showProgressDialog();// ��ʾ���ȿ�
		int currentPage = 0;
		query = new PoiSearch.Query(keyWord, "", "�Ϻ�");// ��һ��������ʾ�����ַ������ڶ���������ʾpoi�������ͣ�������������ʾpoi�������򣨿��ַ�������ȫ����
		query.setPageSize(10);// ����ÿҳ��෵�ض�����poiitem
		query.setPageNum(currentPage);// ���ò��һҳ

		poiSearch = new PoiSearch(getActivity(), query);
		poiSearch.setOnPoiSearchListener(this);
		poiSearch.searchPOIAsyn();
	}
	
	/**
	 * ��ʾ���ȿ�
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(getActivity());
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(false);
		progDialog.setMessage("��������:\n" + keyWord);
		progDialog.show();
	}
	
	/**
	 * ���ؽ��ȿ�
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	/**
	 * poiû�����������ݣ�����һЩ�Ƽ����е���Ϣ
	 */
	private void showSuggestCity(List<SuggestionCity> cities) {
		String infomation = "�Ƽ�����\n";
		for (int i = 0; i < cities.size(); i++) {
			infomation += "��������:" + cities.get(i).getCityName() + "��������:"
					+ cities.get(i).getCityCode() + "���б���:"
					+ cities.get(i).getAdCode() + "\n";
		}
		Toast.makeText(getActivity(), infomation, Toast.LENGTH_SHORT).show();

	}
	

	private void setUpMap() {
		aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
		aMap.setLocationSource(this);// ���ö�λ����
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// ����Ĭ�϶�λ��ť�Ƿ���ʾ
		aMap.setMyLocationEnabled(true);// ����Ϊtrue��ʾ��ʾ��λ�㲢�ɴ�����λ��false��ʾ���ض�λ�㲢���ɴ�����λ��Ĭ����false
	}

	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	@Override
	public void onLocationChanged(Location arg0) {

	}

	@Override
	public void onProviderDisabled(String arg0) {

	}

	@Override
	public void onProviderEnabled(String arg0) {

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (mListener != null && amapLocation != null) {
            if (amapLocation.getAMapException().getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// ��ʾϵͳС����
            }
        }
	}

	@Override
	public void activate(OnLocationChangedListener listener) {
		 mListener = listener;
	        if (mAMapLocationManager == null) {
	            mAMapLocationManager = LocationManagerProxy.getInstance(getActivity());
	            mAMapLocationManager.requestLocationData(
	                    LocationProviderProxy.AMapNetwork, 60*1000, 2, this);
	        }
	}

	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destroy();
		}
		mAMapLocationManager = null;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.title_search:
			searchButton();
			break;

		default:
			break;
		}
	}

	@Override
	public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getQuery() != null) {
				if (result.getQuery().equals(query)) {
					poiResult = result;
					List<PoiItem> poiItems = poiResult.getPois();
					List<SuggestionCity> suggestionCities = poiResult
							.getSearchSuggestionCitys();

					if (poiItems != null && poiItems.size() > 0) {
						aMap.clear();
						PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
						poiOverlay.removeFromMap();
						poiOverlay.addToMap();
						poiOverlay.zoomToSpan();
					} else if (suggestionCities != null
							&& suggestionCities.size() > 0) {
						showSuggestCity(suggestionCities);
					} else {
						Toast.makeText(getActivity(), "û�н��", Toast.LENGTH_SHORT).show();
					}
				}
			} else {
				Toast.makeText(getActivity(), "û�н��", Toast.LENGTH_SHORT).show();
				
			}
		} else if (rCode == 27) {
			Toast.makeText(getActivity(), "�������", Toast.LENGTH_SHORT).show();
			
		} else if (rCode == 32) {
			Toast.makeText(getActivity(), "invalid key", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getActivity(), "��������", Toast.LENGTH_SHORT).show();
		
		}
		
	}
	

}
