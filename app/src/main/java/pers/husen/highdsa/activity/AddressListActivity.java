package pers.husen.highdsa.activity;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import pers.husen.highdsa.CNiaoApplication;
import pers.husen.highdsa.R;
import pers.husen.highdsa.adapter.AddressListAdapter;
import pers.husen.highdsa.constants.Constants;
import pers.husen.highdsa.constants.HttpContants;
import pers.husen.highdsa.utils.LogUtil;
import pers.husen.highdsa.widget.CNiaoToolBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import okhttp3.Call;

/**
 * Created by 高磊华
 * Time  2017/8/10
 * Describe: 收货地址
 */

public class AddressListActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    CNiaoToolBar mToolBar;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerview;

    private AddressListAdapter mAdapter;

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_address_list;
    }

    @Override
    protected void init() {
        initToolbar();
        initAddress();
    }

    /**
     * 初始化地址信息
     */
    private void initAddress() {

        Long userId = CNiaoApplication.getInstance().getUser().getId();
        LogUtil.e("地址列表", "失败" + userId, true);                  //失败 -1
        String url = HttpContants.ADDRESS_LIST + "?user_id=" + userId;

        OkHttpUtils.get().url(url).build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.e("地址列表", "失败" + e, true);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtil.e("地址列表", "成功", true);
            }
        });

    }

    /**
     * 标题的初始化
     */
    private void initToolbar() {
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mToolBar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到添加地址界面
                Intent intent = new Intent(AddressListActivity.this, AddressAddActivity.class);
                startActivityForResult(intent, Constants.Addresslist2Addressadd);
            }
        });
    }


}
