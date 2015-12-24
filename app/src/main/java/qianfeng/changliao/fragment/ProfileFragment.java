package qianfeng.changliao.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import org.w3c.dom.Text;

import qianfeng.changliao.R;
import qianfeng.changliao.app.UserManager;
import qianfeng.changliao.net.http.VolleyImageLoader;

/**
 * Created by wukai on 15/12/16.
 */
public class ProfileFragment extends Fragment {


	private TextView mTV_Name,mTV_Id;
	private NetworkImageView mIV_avart;

	private String userid;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.frag_profile,null);
		mTV_Name = (TextView) view.findViewById(R.id.tv_profile_name);
		mTV_Id = (TextView)view.findViewById(R.id.tv_account);
		//填充id号
		userid = UserManager.getCurrentUserid(getActivity());
		mTV_Id.setText(userid);

		//填充用户名
		mTV_Name.setText(UserManager.getInstance().getChatUser(userid).username);

		mIV_avart = (NetworkImageView)view.findViewById(R.id.iv_avatar);
		mIV_avart.setDefaultImageResId(R.drawable.default_useravatar);

		VolleyImageLoader.getInstance(getActivity()).loadImage(mIV_avart,
				UserManager.getInstance().getChatUser(userid).avart
				);
		return view;
	}



}
