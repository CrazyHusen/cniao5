package pers.husen.highdsa.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import pers.husen.highdsa.CNiaoApplication;
import pers.husen.highdsa.R;
import pers.husen.highdsa.activity.AddressListActivity;
import pers.husen.highdsa.activity.LoginActivity;
import pers.husen.highdsa.activity.MyFavoriteActivity;
import pers.husen.highdsa.activity.MyOrdersActivity;
import pers.husen.highdsa.bean.User;
import pers.husen.highdsa.constants.HttpConstants;
import pers.husen.highdsa.constants.Constants;
import pers.husen.highdsa.utils.GlideUtils;
import pers.husen.highdsa.utils.LogUtil;
import pers.husen.highdsa.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Description "我的"栏目 fragment
 * <p>
 * Author 何明胜
 * <p>
 * Created at 2018/05/15 22:25
 * <p>
 * Version 1.0.2
 */
public class MineFragment extends BaseFragment {
    @BindView(R.id.img_head)
    CircleImageView mImageHead;
    @BindView(R.id.txt_username)
    TextView mTxtUserName;
    //退出按钮
    @BindView(R.id.btn_logout)
    Button mbtnLogout;

    @Override
    protected void init() {
        User user = CNiaoApplication.getInstance().getUser();
        showUser(user);
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.fragment_mine;
    }

    @OnClick({R.id.txt_my_address, R.id.txt_my_favorite, R.id.txt_my_orders, R.id.txt_username, R
            .id.img_head, R.id.btn_logout})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.txt_my_address:      //收货地址
                startActivity(new Intent(getActivity(), AddressListActivity.class), true);
                break;
            case R.id.txt_my_favorite:    //我的收藏
                startActivity(new Intent(getActivity(), MyFavoriteActivity.class), true);
                break;
            case R.id.txt_my_orders:     //我的点单
                startActivity(new Intent(getActivity(), MyOrdersActivity.class), true);
                break;
            case R.id.txt_username:
            case R.id.img_head:
                User user = CNiaoApplication.getInstance().getUser();
                if (user == null) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, Constants.REQUEST_CODE);
                } else {
                    ToastUtils.showDebugSafeToast(getContext(), "更换头像或修改昵称");
                }
                break;
            case R.id.btn_logout:
                CNiaoApplication.getInstance().clearUser();
                showUser(null);
                remoteLogout();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        User user = CNiaoApplication.getInstance().getUser();
        showUser(user);
    }

    private void showUser(User user) {
        if (user != null) {
            mTxtUserName.setText(user.getUsername());
            GlideUtils.load(getContext(), user.getLogo_url(), mImageHead);
        } else {
            mTxtUserName.setText("请登陆");
        }
    }

    //退出时远程也退出,会话清除
    private void remoteLogout() {
        OkHttpUtils.post().url(HttpConstants.URL_LOGOUT).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.e("退出", "失败", true);
                ToastUtils.showDebugSafeToast(MineFragment.super.getContext(), "退出失败");
            }

            @Override
            public void onResponse(String response, int id) {
                ToastUtils.showDebugSafeToast(MineFragment.super.getContext(), "退出成功");
            }
        });
    }
}