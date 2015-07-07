package com.gyxy.sns.ui.fragment;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.gyxy.sns.R;
import com.gyxy.sns.adapter.BaseListAdapter;
import com.gyxy.sns.adapter.ViewHolder;
import com.gyxy.sns.model.Panel;
import com.gyxy.sns.utils.LogUtils;
import com.gyxy.sns.utils.ToastUtils;

public class PanelManageFragment extends Fragment {
	private String TAG = "PanelManageFragment";
	private ListView listview;
	private List<Panel> panels;
	private Button btn_add;
	protected PanelAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_panel_manage,
				container, false);
		initviews(rootView);
		return rootView;
	}
	/**
	 * 显示修改版块名称对话框
	 * @param panel
	 */
	private void showModifyDialog(final Panel panel) {
		final EditText editText = new EditText(getActivity());
		editText.setTextColor(getResources().getColor(android.R.color.black));
		AlertDialog dialog = new AlertDialog.Builder(getActivity())
				.setTitle("修改版块名称").setView(editText)
				.setPositiveButton("修改", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String name = editText.getText().toString().trim();
						if (TextUtils.isEmpty(name)) {
							ToastUtils.makeText(getActivity(), "版块名称不能为空");
							return;
						}
						modifyName(name);
					}

					private void modifyName(String name) {

						panel.setName(name);
						panel.update(getActivity(), new UpdateListener() {
							
							@Override
							public void onSuccess() {
								// TODO Auto-generated method stub
								ToastUtils.makeText(getActivity(), "修改成功！");
								fetData();
							}
							
							@Override
							public void onFailure(int code, String msg) {
								// TODO Auto-generated method stub
								LogUtils.E(TAG, code, msg);
							}
						});
						 
					}

				}).create();
		dialog.show();
	}

	private void initviews(View rootView) {
		listview = (ListView) rootView.findViewById(R.id.listview);
		 
		// 添加版块
		btn_add = (Button) rootView.findViewById(R.id.btn_add);
		btn_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showAddPanelDialog();
			}
		});
	}
	/**
	 * 显示添加版块对话框
	 */
	private void showAddPanelDialog(){


		final EditText editText = new EditText(getActivity());
		editText.setTextColor(getResources().getColor(android.R.color.black));
		AlertDialog dialog = new AlertDialog.Builder(getActivity())
				.setTitle("添加版块")
				.setView(editText)
				.setPositiveButton("添加",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String name = editText.getText()
										.toString().trim();
								if (TextUtils.isEmpty(name)) {
									ToastUtils.makeText(getActivity(),
											"不能添加空版块");
									return;
								}
								addPanel(name);
							}

							private void addPanel(String name) {
								Panel panel = new Panel();
								panel.setName(name);
								panel.save(getActivity(),
										new SaveListener() {

											@Override
											public void onSuccess() {
												ToastUtils.makeText(
														getActivity(),
														"添加成功！");
												fetData();
											}

											@Override
											public void onFailure(
													int code, String msg) {
												LogUtils.E(TAG, code,
														msg);
											}
										});
							}

						}).create();
		dialog.show();
	
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fetData();
	}

	/**
	 * 获取非预设版块的数据
	 */
	private void fetData() {
		BmobQuery<Panel> query = new BmobQuery<Panel>();
		query.addWhereEqualTo("isPreset", false);
		query.findObjects(getActivity(), new FindListener<Panel>() {

			@Override
			public void onSuccess(List<Panel> arg0) {
				for(int i=0;i<arg0.size();i++){
					Log.i(TAG, "PANEL "+arg0.size()+arg0.get(i).toString());
				}
				panels = arg0;
//				if (adapter == null) {
					adapter = new PanelAdapter(getActivity(), panels);
					listview.setAdapter(adapter);
//				} else {
//					adapter.notifyDataSetChanged();
//				}
			}

			@Override
			public void onError(int code, String msg) {
				ToastUtils.makeErrorText(getActivity(), code, msg);
			}
		});
	}

	private class PanelAdapter extends BaseListAdapter<Panel> {

		public PanelAdapter(Context context, List<Panel> list) {
			super(context, list);
		}

		@Override
		public View bindView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_panel, parent,
						false);
			}
			TextView tv_id = ViewHolder.get(convertView, R.id.tv_id);
			TextView tv_name = ViewHolder.get(convertView, R.id.tv_name);

			final Panel panel = list.get(position);
			tv_id.setText(panel.getObjectId());
			tv_name.setText(panel.getName());
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showModifyDialog(panel);
				}
			});
			return convertView;
		}

	}

}
