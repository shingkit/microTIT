package com.gyxy.sns.ui.fragment;

import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRole;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.gyxy.sns.MyApplication;
import com.gyxy.sns.R;
import com.gyxy.sns.model.MoreInfo;
import com.gyxy.sns.model.User;
import com.gyxy.sns.ui.widget.BezelImageView;
import com.gyxy.sns.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 管理员列表fragment
 * 
 * @author sj
 * 
 */
public class ManagerFragment extends Fragment {

	private List<MoreInfo> infos;
	private ListView listview;
	private ManagerAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new ManagerAdapter();
		fetchData();
	}
	/**
	 * 获取管理员数据
	 */
	private void fetchData() {
		BmobQuery<MoreInfo>query1=new BmobQuery<MoreInfo>();
		query1.include("user");
		query1.addWhereEqualTo("userRole", MoreInfo.ADMIN);
		query1.findObjects(getActivity(), new FindListener<MoreInfo>(){
			@Override
			public void onError(int code, String msg) {
				ToastUtils.makeErrorText(getActivity(), code, msg);
			}

			@Override
			public void onSuccess(List<MoreInfo> arg0) {
				// TODO Auto-generated method stub
				infos = arg0;
				listview.setAdapter(adapter);
			}
			
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_manager, container,
				false);
		initView(rootView);
		return rootView;
	}

	private void initView(View rootView) {

		listview = (ListView) rootView.findViewById(R.id.listview);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int position, long arg3) {
				final MoreInfo info = (MoreInfo) listview.getItemAtPosition(position);
				AlertDialog dialog = new Builder(getActivity())
						.setTitle("提醒")
						.setMessage("移除" + info.getUser().getUsername() + "的管理员身份？")
						.setNegativeButton(android.R.string.cancel,
								new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								})
						.setPositiveButton(android.R.string.ok,
								new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										deleteAdmin(info);
									}

									
								}).create();
				dialog.show();
			}

		});
	}

	private void deleteAdmin(MoreInfo info) {
		 
		info.setUserRole(MoreInfo.MEMBER);
		info.update(getActivity(),new UpdateListener() {
			
			@Override
			public void onSuccess() {
				ToastUtils.makeText(getActivity(), "移除成功");
				fetchData();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});

	}
	private class ManagerAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return infos.size();
		}

		@Override
		public Object getItem(int position) {
			return infos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(getActivity(), R.layout.item_user,
						null);
				holder.biv_head = (BezelImageView) convertView
						.findViewById(R.id.biv_head);
				holder.tv_username = (TextView) convertView
						.findViewById(R.id.tv_username);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			MoreInfo info = infos.get(position);
			User user = info.getUser();
			holder.tv_username.setText(user.getUsername());
			if (!TextUtils.isEmpty(user.getAvatar())) {
				ImageLoader.getInstance().displayImage(user.getAvatar(),
						holder.biv_head, MyApplication.getHeadOptions());
			}
			return convertView;
		}

	}

	private static class ViewHolder {
		TextView tv_username;
		BezelImageView biv_head;
	}
}
