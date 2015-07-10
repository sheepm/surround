package com.example.activity;

import java.util.List;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.overlay.BusRouteOverlay;
import com.amap.api.maps2d.overlay.DrivingRouteOverlay;
import com.amap.api.maps2d.overlay.WalkRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.RouteSearch.BusRouteQuery;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.amap.api.services.route.WalkRouteResult;
import com.example.surround.R;
import com.example.util.RouteSearchPoiDialog;
import com.example.util.RouteSearchPoiDialog.OnListItemClick;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

@SuppressLint({ "NewApi", "ResourceAsColor" }) public class RouteActivity extends Activity implements OnClickListener,
		OnRouteSearchListener, OnPoiSearchListener {

	private AMap aMap;
	private PoiSearch.Query startSearchQuery;
	private PoiSearch.Query endSearchQuery;
	private MapView mapView;
	private String start = null;
	private String end = null;
	private ProgressDialog progDialog = null;// ����ʱ������
	private int routeType = 2;// 1������ģʽ��2����ݳ�ģʽ��3������ģʽ
	private LatLonPoint startPoint = null;
	private LatLonPoint endPoint = null;
	private RouteSearch routeSearch;
	private int busMode = RouteSearch.BusDefault;// ����Ĭ��ģʽ
	private int drivingMode = RouteSearch.DrivingDefault;// �ݳ�Ĭ��ģʽ
	private int walkMode = RouteSearch.WalkDefault;// ����Ĭ��ģʽ
	private BusRouteResult busRouteResult;// ����ģʽ��ѯ���
	private DriveRouteResult driveRouteResult;// �ݳ�ģʽ��ѯ���
	private WalkRouteResult walkRouteResult;// ����ģʽ��ѯ���

	private LinearLayout mGongjiao;
	private LinearLayout mJiache;
	private LinearLayout mBuxing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_route);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// �˷���������д
		init();
		registerListener();
		startSearchResult();

	}

	/**
	 * ��ʾ���ȿ�
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage("��������");
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
	 * 
	 */
	private void clearBg() {
		mGongjiao.setBackground(null);
		mJiache.setBackground(null);
		mBuxing.setBackground(null);
	}

	/**
	 * ��ѯ·���滮���
	 */
	public void startSearchResult() {
		start = this.getIntent().getStringExtra("startpoint");
		if (startPoint != null && start.equals("��ͼ�ϵ����")) {
			endSearchResult();
		} else {
			showProgressDialog();
			startSearchQuery = new PoiSearch.Query(start, "", "010"); // ��һ��������ʾ��ѯ�ؼ��֣��ڶ�������ʾpoi�������ͣ�������������ʾ�������Ż��߳�����
			startSearchQuery.setPageNum(0);// ���ò�ѯ�ڼ�ҳ����һҳ��0��ʼ
			startSearchQuery.setPageSize(10);// ����ÿҳ���ض���������
			PoiSearch poiSearch = new PoiSearch(RouteActivity.this,
					startSearchQuery);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.searchPOIAsyn();// �첽poi��ѯ
		}
	}

	/**
	 * ��ѯ·���滮�յ�
	 */
	public void endSearchResult() {
		end = this.getIntent().getStringExtra("endpoint");
		if (endPoint != null && end.equals("��ͼ�ϵ��յ�")) {
			searchRouteResult(startPoint, endPoint);
		} else {
			showProgressDialog();
			endSearchQuery = new PoiSearch.Query(end, "", "010"); // ��һ��������ʾ��ѯ�ؼ��֣��ڶ�������ʾpoi�������ͣ�������������ʾ�������Ż��߳�����
			endSearchQuery.setPageNum(0);// ���ò�ѯ�ڼ�ҳ����һҳ��0��ʼ
			endSearchQuery.setPageSize(20);// ����ÿҳ���ض���������

			PoiSearch poiSearch = new PoiSearch(RouteActivity.this,
					endSearchQuery);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.searchPOIAsyn(); // �첽poi��ѯ
		}
	}

	/**
	 * ��ʼ����·���滮����
	 */
	public void searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint) {
		showProgressDialog();
		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
				startPoint, endPoint);
		if (routeType == 1) {// ����·���滮
			BusRouteQuery query = new BusRouteQuery(fromAndTo, busMode, "�Ϻ�", 0);// ��һ��������ʾ·���滮�������յ㣬�ڶ���������ʾ������ѯģʽ��������������ʾ������ѯ�������ţ����ĸ�������ʾ�Ƿ����ҹ�೵��0��ʾ������
			routeSearch.calculateBusRouteAsyn(query);// �첽·���滮����ģʽ��ѯ
		} else if (routeType == 2) {// �ݳ�·���滮
			DriveRouteQuery query = new DriveRouteQuery(fromAndTo, drivingMode,
					null, null, "");// ��һ��������ʾ·���滮�������յ㣬�ڶ���������ʾ�ݳ�ģʽ��������������ʾ;���㣬���ĸ�������ʾ�������򣬵����������ʾ���õ�·
			routeSearch.calculateDriveRouteAsyn(query);// �첽·���滮�ݳ�ģʽ��ѯ
		} else if (routeType == 3) {// ����·���滮
			WalkRouteQuery query = new WalkRouteQuery(fromAndTo, walkMode);
			routeSearch.calculateWalkRouteAsyn(query);// �첽·���滮����ģʽ��ѯ
		}
	}

	/**
	 * ѡ�񹫽�ģʽ
	 */
	private void busRoute() {
		routeType = 1;// ��ʶΪ����ģʽ
		busMode = RouteSearch.BusDefault;

	}

	/**
	 * ѡ��ݳ�ģʽ
	 */
	private void drivingRoute() {
		routeType = 2;// ��ʶΪ�ݳ�ģʽ
		drivingMode = RouteSearch.DrivingDefault;
	}

	/**
	 * ѡ����ģʽ
	 */
	private void walkRoute() {
		routeType = 3;// ��ʶΪ����ģʽ
		walkMode = RouteSearch.WalkMultipath;
	}

	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			
		}
		mGongjiao = (LinearLayout) findViewById(R.id.road_gongjiao);
		mJiache = (LinearLayout) findViewById(R.id.road_jiache);
		mBuxing = (LinearLayout) findViewById(R.id.road_buxing);
		routeSearch = new RouteSearch(this);
		routeSearch.setRouteSearchListener(this);
	}

	private void registerListener() {
		mGongjiao.setOnClickListener(this);
		mJiache.setOnClickListener(this);
		mBuxing.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {// ���سɹ�
			if (result != null && result.getQuery() != null
					&& result.getPois() != null && result.getPois().size() > 0) {// ����poi�Ľ��
				if (result.getQuery().equals(startSearchQuery)) {
					List<PoiItem> poiItems = result.getPois();// ȡ��poiitem����
					RouteSearchPoiDialog dialog = new RouteSearchPoiDialog(
							RouteActivity.this, poiItems);
					dialog.setTitle("��Ҫ�ҵ������:");
					dialog.show();
					dialog.setOnListClickListener(new OnListItemClick() {
						@Override
						public void onListItemClick(
								RouteSearchPoiDialog dialog,
								PoiItem startpoiItem) {
							startPoint = startpoiItem.getLatLonPoint();
							// strStart = startpoiItem.getTitle();
							// startTextView.setText(strStart);
							endSearchResult();// ��ʼ���յ�
						}

					});
				} else if (result.getQuery().equals(endSearchQuery)) {
					List<PoiItem> poiItems = result.getPois();// ȡ��poiitem����
					RouteSearchPoiDialog dialog = new RouteSearchPoiDialog(
							RouteActivity.this, poiItems);
					dialog.setTitle("��Ҫ�ҵ��յ���:");
					dialog.show();
					dialog.setOnListClickListener(new OnListItemClick() {
						@Override
						public void onListItemClick(
								RouteSearchPoiDialog dialog, PoiItem endpoiItem) {
							endPoint = endpoiItem.getLatLonPoint();
							// strEnd = endpoiItem.getTitle();
							// endTextView.setText(strEnd);
							searchRouteResult(startPoint, endPoint);// ����·���滮����
						}

					});
				}
			} else {
				Toast.makeText(RouteActivity.this, "û�н��", 0).show();
			}
		} else if (rCode == 27) {
			Toast.makeText(RouteActivity.this, "�������", 0).show();
		} else if (rCode == 32) {
			Toast.makeText(RouteActivity.this, "������", 0).show();
		} else {
			Toast.makeText(RouteActivity.this, "��������", 0).show();
		}

	}

	/**
	 * ������ѯ�ص�
	 */
	@Override
	public void onBusRouteSearched(BusRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				busRouteResult = result;
				BusPath busPath = busRouteResult.getPaths().get(0);
				aMap.clear();// �����ͼ�ϵ����и�����
				BusRouteOverlay routeOverlay = new BusRouteOverlay(this, aMap,
						busPath, busRouteResult.getStartPos(),
						busRouteResult.getTargetPos());
				routeOverlay.removeFromMap();
				routeOverlay.addToMap();
				routeOverlay.zoomToSpan();
			} else {
				Toast.makeText(RouteActivity.this, "û�н��", 0).show();
			}
		} else if (rCode == 27) {
			Toast.makeText(RouteActivity.this, "�������", 0).show();
		} else if (rCode == 32) {
			Toast.makeText(RouteActivity.this, "������", 0).show();
		} else {
			Toast.makeText(RouteActivity.this, "��������", 0).show();
		}

	}

	/**
	 * ������ѯ�ص�
	 */
	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				driveRouteResult = result;
				DrivePath drivePath = driveRouteResult.getPaths().get(0);
				aMap.clear();// �����ͼ�ϵ����и�����
				DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
						this, aMap, drivePath, driveRouteResult.getStartPos(),
						driveRouteResult.getTargetPos());
				drivingRouteOverlay.removeFromMap();
				drivingRouteOverlay.addToMap();
				drivingRouteOverlay.zoomToSpan();
			} else {
				Toast.makeText(RouteActivity.this, "û�н��", 0).show();
			}
		} else if (rCode == 27) {
			Toast.makeText(RouteActivity.this, "�������", 0).show();
		} else if (rCode == 32) {
			Toast.makeText(RouteActivity.this, "������", 0).show();
		} else {
			Toast.makeText(RouteActivity.this, "��������", 0).show();
		}

	}

	/**
	 * ���в�ѯ�ص�
	 */
	@Override
	public void onWalkRouteSearched(WalkRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				walkRouteResult = result;
				WalkPath walkPath = walkRouteResult.getPaths().get(0);
				aMap.clear();// �����ͼ�ϵ����и�����
				WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(this,
						aMap, walkPath, walkRouteResult.getStartPos(),
						walkRouteResult.getTargetPos());
				walkRouteOverlay.removeFromMap();
				walkRouteOverlay.addToMap();
				walkRouteOverlay.zoomToSpan();
			} else {
				Toast.makeText(RouteActivity.this, "û�н��", 0).show();
			}
		} else if (rCode == 27) {
			Toast.makeText(RouteActivity.this, "�������", 0).show();
		} else if (rCode == 32) {
			Toast.makeText(RouteActivity.this, "������", 0).show();
		} else {
			Toast.makeText(RouteActivity.this, "��������", 0).show();
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.road_gongjiao:
			clearBg();
			mGongjiao.setBackgroundColor(getResources().getColor(
					R.color.linear_press));
			busRoute();
			searchRouteResult(startPoint,endPoint);
			break;
		case R.id.road_jiache:
			clearBg();
			mJiache.setBackgroundColor(getResources().getColor(
					R.color.linear_press));
			drivingRoute();
			searchRouteResult(startPoint,endPoint);
			break;

		case R.id.road_buxing:
			clearBg();
			mBuxing.setBackgroundColor(getResources().getColor(
					R.color.linear_press));
			walkRoute();
			searchRouteResult(startPoint,endPoint);
			break;

		default:
			break;
		}
	}

	

}
