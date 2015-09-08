package com.chenchong.sortlistview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chenchong.mysortlistview.R;
import com.chenchong.sortlistview.SideBar.OnTouchingLetterChangedListener;
import com.chenchong.utils.NetworkUtils;
import com.chenchong.utils.NetworkUtils.VolleyJsonCallback;

/**
 * 
 * 类名SortListViewAct
 *	实现的主要功能。
 *	创建日期2015-9-6
 *	创建人 chenchong <br/>
 *	联系QQ:695933593
 */
public class SortListViewAct extends Activity {
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	private ClearEditText mClearEditText;
	
	//要请求的API地址
	private static final String CITY_URL = "XXXXXXXXXX";
	
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<SortModel> SourcedataList;
	
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sortlistview);
		initViews();
	}

	private void initViews() {
		//实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		
		pinyinComparator = new PinyinComparator();
		
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);
		
		//设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			
			@Override
			public void onTouchingLetterChanged(String s) {
				//该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if(position != -1){
					sortListView.setSelection(position);
				}
			}
		});
		
		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//这里要利用adapter.getItem(position)来获取当前position所对应的对象
				Toast.makeText(getApplication(), ((SortModel)adapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
			}
		});
		
//		//此处为listview获取数据
//		final List<String> nameStrs = new ArrayList<String>();
//		NetworkUtils.getJSONObject(CITY_URL, new VolleyJsonCallback() {
//			@Override
//			public void onSuccess(JSONObject result) {
//				//获取数据成功
//				try {
//					JSONArray json = result.getJSONObject("res").getJSONArray("province");
//					for (int i = 0; i < json.length(); i++) {
//						String name = json.getJSONObject(i).getString("name");
//						nameStrs.add(name);
//					}
//					//填充数据，排序，配adapter
//					setData(nameStrs);
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//		}, getApplicationContext());
		
		SourcedataList = filledData(getResources().getStringArray(R.array.data));
		
		// 根据a-z进行排序源数据
		Collections.sort(SourcedataList, pinyinComparator);
		adapter = new SortAdapter(this, SourcedataList);
		sortListView.setAdapter(adapter);
		
		
		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
		//根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	//将填充数据、排序、设置adapter封装。
//	private void setData(final List<String> strs) {
//		SourcedataList = filledData(strs);
//		
//		//用xml文件获取的数据是String数据，如果用接口动态获取，建议改用list
////		SourcedataList = filledData(getResources().getStringArray(R.array.data));
//		
//		// 根据a-z进行排序源数据
//		Collections.sort(SourcedataList, pinyinComparator);
//		adapter = new SortAdapter(this, SourcedataList);
//		sortListView.setAdapter(adapter);
//	}

	/**
	 * 为ListView填充数据
	 * @param strings
	 * @return
	 */
	private List<SortModel> filledData(String[] strings){
		List<SortModel> mSortList = new ArrayList<SortModel>();
		
		for(int i=0; i<strings.length; i++){
			SortModel sortModel = new SortModel();
			sortModel.setName(strings[i]);
			//汉字转换成拼音
			String pinyin = characterParser.getSelling(strings[i]);
			String sortString = pinyin.substring(0, 1).toUpperCase();
			// 正则表达式，判断首字母是否是英文字母
			if(sortString.matches("[A-Z]")){
				sortModel.setSortLetters(sortString.toUpperCase());
			}else{
				sortModel.setSortLetters("#");
			}
			mSortList.add(sortModel);
		}
		return mSortList;
	}
	
	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * @param filterStr
	 */
	private void filterData(String filterStr){
		List<SortModel> filterDataList = new ArrayList<SortModel>();
		
		if(TextUtils.isEmpty(filterStr)){
			filterDataList = SourcedataList;
		}else{
			filterDataList.clear();
			for(SortModel sortModel : SourcedataList){
				String name = sortModel.getName();
				if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
					filterDataList.add(sortModel);
				}
			}
		}
		
		// 根据a-z进行排序
		Collections.sort(filterDataList, pinyinComparator);
		adapter.updateListView(filterDataList);
	}
	
}
