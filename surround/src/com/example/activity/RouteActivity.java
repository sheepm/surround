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
	private ProgressDialog progDialog = null;// 搜索时进度条
	private int routeType = 2;// 1代表公交模式，2代表驾车模式，3代表步行模式
	private LatLonPoint startPoint = null;
	private LatLonPoint endPoint = null;
	private RouteSearch routeSearch;
	private int busMode = RouteSearch.BusDefault;// 公交默认模式
	private int drivingMode = RouteSearch.DrivingDefault;// 驾车默认模式
	private int walkMode = RouteSearch.WalkDefault;// 步行默认模式
	private BusRouteResult busRouteResult;// 公交模式查询结果
	private DriveRouteResult driveRouteResult;// 驾车模式查询结果
	private WalkRouteResult walkRouteResult;// 步行模式查询结果

	private LinearLayout mGongjiao;
	private LinearLayout mJiache;
	private LinearLayout mBuxing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_route);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
		registerListener();
		startSearchResult();

	}

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage("正在搜索");
		progDialog.show();
	}

	/**
	 * 隐藏进度框
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
	 * 查询路径规划起点
	 */
	public void startSearchResult() {
		start = this.getIntent().getStringExtra("startpoint");
		if (startPoint != null && start.equals("地图上的起点")) {
			endSearchResult();
		} else {
			showProgressDialog();
			startSearchQuery = new PoiSearch.Query(start, "", "010"); // 第一个参数表示查询关键字，第二参数表示poi搜索类型，第三个参数表示城市区号或者城市名
			startSearchQuery.setPageNum(0);// 设置查询第几页，第一页从0开始
			startSearchQuery.setPageSize(10);// 设置每页返回多少条数据
			PoiSearch poiSearch = new PoiSearch(RouteActivity.this,
					startSearchQuery);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.searchPOIAsyn();// 异步poi查询
		}
	}

	/**
	 * 查询路径规划终点
	 */
	public void endSearchResult() {
		end = this.getIntent().getStringExtra("endpoint");
		if (endPoint != null && end.equals("地图上的终点")) {
			searchRouteResult(startPoint, endPoint);
		} else {
			showProgressDialog();
			endSearchQuery = new PoiSearch.Query(end, "", "010"); // 第一个参数表示查询关键字，第二参数表示poi搜索类型，第三个参数表示城市区号或者城市名
			endSearchQuery.setPageNum(0);// 设置查询第几页，第一页从0开始
			endSearchQuery.setPageSize(20);// 设置每页返回多少条数据

			PoiSearch poiSearch = new PoiSearch(RouteActivity.this,
					endSearchQuery);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.searchPOIAsyn(); // 异步poi查询
		}
	}

	/**
	 * 开始搜索路径规划方案
	 */
	public void searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint) {
		showProgressDialog();
		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
				startPoint, endPoint);
		if (routeType == 1) {// 公交路径规划
			BusRouteQuery query = new BusRouteQuery(fromAndTo, busMode, "上海", 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
			routeSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
		} else if (routeType == 2) {// 驾车路径规划
			DriveRouteQuery query = new DriveRouteQuery(fromAndTo, drivingMode,
					null, null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
			routeSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
		} else if (routeType == 3) {// 步行路径规划
			WalkRouteQuery query = new WalkRouteQuery(fromAndTo, walkMode);
			routeSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
		}
	}

	/**
	 * 选择公交模式
	 */
	private void busRoute() {
		routeType = 1;// 标识为公交模式
		busMode = RouteSearch.BusDefault;

	}

	/**
	 * 选择驾车模式
	 */
	private void drivingRoute() {
		routeType = 2;// 标识为驾车模式
		drivingMode = RouteSearch.DrivingDefault;
	}

	/**
	 * 选择步行模式
	 */
	private void walkRoute() {
		routeType = 3;// 标识为步行模式
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
		if (rCode == 0) {// 返回成功
			if (result != null && result.getQuery() != null
					&& result.getPois() != null && result.getPois().size() > 0) {// 搜索poi的结果
				if (result.getQuery().equals(startSearchQuery)) {
					List<PoiItem> poiItems = result.getPois();// 取得poiitem数据
					RouteSearchPoiDialog dialog = new RouteSearchPoiDialog(
							RouteActivity.this, poiItems);
					dialog.setTitle("您要找的起点是:");
					dialog.show();
					dialog.setOnListClickListener(new OnListItemClick() {
						@Override
						public void onListItemClick(
								RouteSearchPoiDialog dialog,
								PoiItem startpoiItem) {
							startPoint = startpoiItem.getLatLonPoint();
							// strStart = startpoiItem.getTitle();
							// startTextView.setText(strStart);
							endSearchResult();// 开始搜终点
						}

					});
				} else if (result.getQuery().equals(endSearchQuery)) {
					List<PoiItem> poiItems = result.getPois();// 取得poiitem数据
					RouteSearchPoiDialog dialog = new RouteSearchPoiDialog(
							RouteActivity.this, poiItems);
					dialog.setTitle("您要找的终点是:");
					dialog.show();
					dialog.setOnListClickListener(new OnListItemClick() {
						@Override
						public void onListItemClick(
								RouteSearchPoiDialog dialog, PoiItem endpoiItem) {
							endPoint = endpoiItem.getLatLonPoint();
							// strEnd = endpoiItem.getTitle();
							// endTextView.setText(strEnd);
							searchRouteResult(startPoint, endPoint);// 进行路径规划搜索
						}

					});
				}
			} else {
				Toast.makeText(RouteActivity.this, "没有结果", 0).show();
			}
		} else if (rCode == 27) {
			Toast.makeText(RouteActivity.this, "网络错误", 0).show();
		} else if (rCode == 32) {
			Toast.makeText(RouteActivity.this, "错误码", 0).show();
		} else {
			Toast.makeText(RouteActivity.this, "其他错误", 0).show();
		}

	}

	/**
	 * 公交查询回调
	 */
	@Override
	public void onBusRouteSearched(BusRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				busRouteResult = result;
				BusPath busPath = busRouteResult.getPaths().get(0);
				aMap.clear();// 清理地图上的所有覆盖物
				BusRouteOverlay routeOverlay = new BusRouteOverlay(this, aMap,
						busPath, busRouteResult.getStartPos(),
						busRouteResult.getTargetPos());
				routeOverlay.removeFromMap();
				routeOverlay.addToMap();
				routeOverlay.zoomToSpan();
			} else {
				Toast.makeText(RouteActivity.this, "没有结果", 0).show();
			}
		} else if (rCode == 27) {
			Toast.makeText(RouteActivity.this, "网络错误", 0).show();
		} else if (rCode == 32) {
			Toast.makeText(RouteActivity.this, "错误码", 0).show();
		} else {
			Toast.makeText(RouteActivity.this, "其他错误", 0).show();
		}

	}

	/**
	 * 汽车查询回调
	 */
	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				driveRouteResult = result;
				DrivePath drivePath = driveRouteResult.getPaths().get(0);
				aMap.clear();// 清理地图上的所有覆盖物
				DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
						this, aMap, drivePath, driveRouteResult.getStartPos(),
						driveRouteResult.getTargetPos());
				drivingRouteOverlay.removeFromMap();
				drivingRouteOverlay.addToMap();
				drivingRouteOverlay.zoomToSpan();
			} else {
				Toast.makeText(RouteActivity.this, "没有结果", 0).show();
			}
		} else if (rCode == 27) {
			Toast.makeText(RouteActivity.this, "网络错误", 0).show();
		} else if (rCode == 32) {
			Toast.makeText(RouteActivity.this, "错误码", 0).show();
		} else {
			Toast.makeText(RouteActivity.this, "其他错误", 0).show();
		}

	}

	/**
	 * 步行查询回调
	 */
	@Override
	public void onWalkRouteSearched(WalkRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				walkRouteResult = result;
				WalkPath walkPath = walkRouteResult.getPaths().get(0);
				aMap.clear();// 清理地图上的所有覆盖物
				WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(this,
						aMap, walkPath, walkRouteResult.getStartPos(),
						walkRouteResult.getTargetPos());
				walkRouteOverlay.removeFromMap();
				walkRouteOverlay.addToMap();
				walkRouteOverlay.zoomToSpan();
			} else {
				Toast.makeText(RouteActivity.this, "没有结果", 0).show();
			}
		} else if (rCode == 27) {
			Toast.makeText(RouteActivity.this, "网络错误", 0).show();
		} else if (rCode == 32) {
			Toast.makeText(RouteActivity.this, "错误码", 0).show();
		} else {
			Toast.makeText(RouteActivity.this, "其他错误", 0).show();
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
