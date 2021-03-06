package com.wills.help.person.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.wills.help.R;
import com.wills.help.assist.ui.AssistInfoActivity;
import com.wills.help.base.App;
import com.wills.help.base.BaseActivity;
import com.wills.help.base.BaseListAdapter;
import com.wills.help.net.HttpMap;
import com.wills.help.person.adapter.OrderAdapter;
import com.wills.help.release.model.OrderInfo;
import com.wills.help.release.model.OrderList;
import com.wills.help.release.presenter.ReleaseListPresenterImpl;
import com.wills.help.release.ui.AppraiseActivity;
import com.wills.help.release.view.ReleaseListView;
import com.wills.help.utils.IntentUtils;
import com.wills.help.widget.MyItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * com.wills.help.person.ui
 * Created by lizhaoyong
 * 2016/12/5.
 */

public class OrderListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener , BaseListAdapter.LoadMoreListener , ReleaseListView ,BaseListAdapter.BaseItemClickListener,OrderAdapter.ButtonClickListener{

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    OrderAdapter orderAdapter;
    LinearLayoutManager linearLayoutManager;
    List<OrderInfo> orderArrayList = new ArrayList<>();
    private ReleaseListPresenterImpl releaseListPresenter;
    private int page = 1;
    private int action = 0;
    private int type = 0;//0发布1接单
    private int count=0;
    private OrderInfo orderInfo;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setBaseView(R.layout.page_list);
        Bundle bundle = getIntent().getExtras();
        setBaseTitle(bundle.getString("title"));
        action = bundle.getInt("action");
        type = bundle.getInt("type");
        recyclerView = (RecyclerView) findViewById(R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,R.color.colorPrimary, R.color.colorPrimaryLight,R.color.colorAccent);
        initData();
    }

    private void initData() {
        releaseListPresenter = new ReleaseListPresenterImpl(this);
        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyItemDecoration(context,5));
        orderAdapter = new OrderAdapter(context,orderArrayList,recyclerView,linearLayoutManager,type,action);
        orderAdapter.setBaseItemClickListener(this);
        orderAdapter.setButtonClickListener(this);
        recyclerView.setAdapter(orderAdapter);
        orderAdapter.setLoadMoreListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        onRefresh();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        if(orderArrayList != null) {
            orderArrayList.clear();
            page=1;
        }
        releaseListPresenter.getReleaseList(getMap());
    }

    @Override
    public void loadMore() {
        if (count>orderArrayList.size()){
            releaseListPresenter.getReleaseList(getMap());
        }else {
            orderAdapter.setLoadMore(orderAdapter.EMPTY);
        }
    }

    private Map<String ,String> getMap(){
        HttpMap map = new HttpMap();
        if (type == 0){
            map.put("releaseuserid", App.getApp().getUser().getUserid());
        }else if (type==1){
            map.put("acceptuserid", App.getApp().getUser().getUserid());
        }
        map.put("action", action+"");
        map.put("page", page+"");
        return map.getMap();
    }

    private Map<String , String> getMap(OrderInfo releaseInfo){
        HttpMap map = new HttpMap();
        map.put("releaseuserid", App.getApp().getUser().getUserid());
        map.put("orderid", releaseInfo.getOrderid());
        return map.getMap();
    }


    private Map<String , String> getExecMap(OrderInfo releaseInfo){
        HttpMap map = new HttpMap();
        map.put("acceptuserid", App.getApp().getUser().getUserid());
        map.put("orderid", releaseInfo.getOrderid());
        return map.getMap();
    }

    @Override
    public void setReleaseList(OrderList releaseList) {
        if (swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
        count = releaseList.getCount();
        if (orderArrayList == null ){
            orderArrayList = new ArrayList<>();
        }
        if (releaseList.getData().size()>0){
            orderAdapter.setLoadMore(orderAdapter.SUCCESS);
            orderArrayList.addAll(releaseList.getData());
            orderAdapter.setList(orderArrayList);
            page++;
        }else {
            orderAdapter.setEmpty();
        }
    }

    @Override
    public void confirm() {
        onRefresh();
        Bundle bundle = new Bundle();
        bundle.putSerializable("orderId",orderInfo.getOrderid());
        IntentUtils.startActivity(context,AppraiseActivity.class,bundle);
    }

    @Override
    public void exec() {
        onRefresh();
    }

    @Override
    public void onItemClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("orderInfo",orderArrayList.get(position));
        bundle.putString("from","orderList");
        IntentUtils.startActivityForResult(OrderListActivity.this, AssistInfoActivity.class,bundle,501);
    }
    private void showOk(final OrderInfo releaseInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.release_state_ok))
                .setMessage(getString(R.string.release_state_ok_confirm))
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        releaseListPresenter.confirm(getMap(releaseInfo),1);
                    }
                }).show();
    }

    @Override
    public void buttonClick(int state, OrderInfo releaseInfo) {
        this.orderInfo = releaseInfo;
        switch (state){
            case 1:
                releaseListPresenter.exec(getExecMap(releaseInfo),1);
                break;
            case 2:
            showOk(releaseInfo);
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 401 && resultCode == RESULT_OK){
            onRefresh();
        }
    }
}
