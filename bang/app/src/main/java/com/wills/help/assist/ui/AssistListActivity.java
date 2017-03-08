package com.wills.help.assist.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.wills.help.R;
import com.wills.help.assist.adapter.AssistAdapter;
import com.wills.help.assist.presenter.AssistPresenterImpl;
import com.wills.help.assist.view.AssistView;
import com.wills.help.base.BaseActivity;
import com.wills.help.base.BaseListAdapter;
import com.wills.help.listener.BaseListLoadMoreListener;
import com.wills.help.net.HttpMap;
import com.wills.help.release.model.OrderInfo;
import com.wills.help.release.model.OrderList;
import com.wills.help.utils.IntentUtils;
import com.wills.help.widget.MyItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * com.wills.help.assist.ui
 * Created by lizhaoyong
 * 2016/11/16.
 */

public class AssistListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, BaseListLoadMoreListener.LoadMoreListener, AssistView, BaseListAdapter.BaseItemClickListener {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    AssistAdapter assistAdapter;
    LinearLayoutManager linearLayoutManager;
    List<OrderInfo> assistList = new ArrayList<>();
    private int page = 1;
    private int count = 0;
    private AssistPresenterImpl assistPresenter;
    private int srcId;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setBaseView(R.layout.page_list);
        setBaseTitle(getString(R.string.tab_assist));
        srcId = getIntent().getExtras().getInt("srcid");
        recyclerView = (RecyclerView) findViewById(R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimary, R.color.colorPrimaryLight, R.color.colorAccent);
        initData();
    }

    private void initData() {
        assistPresenter = new AssistPresenterImpl(this);
        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyItemDecoration(context, 5));
        assistAdapter = new AssistAdapter(context, assistList);
        assistAdapter.setBaseItemClickListener(this);
        recyclerView.setAdapter(assistAdapter);
        BaseListLoadMoreListener listLoadMore = new BaseListLoadMoreListener(linearLayoutManager, assistAdapter);
        recyclerView.addOnScrollListener(listLoadMore);
        listLoadMore.setLoadMoreListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        onRefresh();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        if (assistList != null) {
            assistList.clear();
            page = 1;
        }
        assistPresenter.getAssistList(getMap(0, 0));
    }

    /**
     * @param type     0获取列表 1接单
     * @param position
     * @return
     */
    private Map<String, String> getMap(int type, int position) {
        HttpMap map = new HttpMap();
        map.put("srcid", srcId+"");
        map.put("page", page + "");
        return map.getMap();
    }

    @Override
    public void loadMore() {
        if (count > assistList.size()) {
            assistPresenter.getAssistList(getMap(0, 0));
        } else {
            assistAdapter.setLoadMore(assistAdapter.EMPTY);
        }

    }

    @Override
    public void accept() {

    }

    @Override
    public void setAssistList(OrderList orderList) {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        count = orderList.getCount();
        if (assistList == null) {
            assistList = new ArrayList<>();
        }
        if (orderList.getData().size() > 0) {
            assistAdapter.setLoadMore(assistAdapter.SUCCESS);
            assistList.addAll(orderList.getData());
            assistAdapter.setList(assistList);
            page++;
        }else {
            assistAdapter.setEmpty();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 501&&resultCode == RESULT_OK){
            onRefresh();
        }
    }

    @Override
    public void onItemClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("orderInfo",assistList.get(position));
        bundle.putString("from","accept");
        IntentUtils.startActivityForResult(AssistListActivity.this, AssistInfoActivity.class,bundle,501);
    }

}
