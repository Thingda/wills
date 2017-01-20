package com.wills.help.assist.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wills.help.R;
import com.wills.help.base.App;
import com.wills.help.base.BaseListAdapter;
import com.wills.help.db.bean.OrderTypeInfo;
import com.wills.help.db.manager.OrderTypeInfoHelper;
import com.wills.help.message.EaseConstant;
import com.wills.help.message.ui.ChatActivity;
import com.wills.help.release.model.OrderInfo;
import com.wills.help.utils.GlideUtils;
import com.wills.help.utils.IntentUtils;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * com.wills.help.assist.adapter
 * Created by lizhaoyong
 * 2016/11/16.
 */

public class AssistAdapter extends BaseListAdapter<OrderInfo>{
    private Context context;
    private List<OrderInfo> list;
    public AssistAdapter(Context context, List<OrderInfo> list) {
        super(context, list);
        this.context=context;
        this.list=list;
    }

    @Override
    protected RecyclerView.ViewHolder CreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assist, parent, false);
        return new AssistHolder(view);
    }

    @Override
    protected void BindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof AssistHolder){
            final OrderInfo orderInfo = list.get(position);
            GlideUtils.getInstance().displayCircleImage(context,orderInfo.getReleaseavatar(),((AssistHolder)holder).imageView);
            OrderTypeInfoHelper.getInstance().queryById(orderInfo.getOrdertype())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<OrderTypeInfo>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable throwable) {

                        }

                        @Override
                        public void onNext(OrderTypeInfo orderTypeInfo) {
                            ((AssistHolder)holder).tv_assist_state.setText(context.getString(R.string.help)+orderTypeInfo.getOrdertype());
                        }
                    });
            Drawable drawable =null;
            if (orderInfo.getReleasesex().equals("1")){
                drawable = context.getResources().getDrawable(R.drawable.sex_girl);
            }else if (orderInfo.getReleasesex().equals("2")){
                drawable = context.getResources().getDrawable(R.drawable.sex_boy);
            }
            drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
            ((AssistHolder)holder).tv_name.setCompoundDrawables(drawable,null,null,null);
            ((AssistHolder)holder).tv_name.setText(orderInfo.getReleaseusername());
            ((AssistHolder)holder).tv_assist_time.setText(orderInfo.getCreatetime());
            ((AssistHolder)holder).tv_assist_location.setText(orderInfo.getSrcname()+orderInfo.getSrcdetail());
            ((AssistHolder)holder).tv_assist_address.setText(orderInfo.getDesname()+orderInfo.getDesdetail());
            ((AssistHolder)holder).tv_assist_money.setText(orderInfo.getMoney());
            ((AssistHolder)holder).tv_assist_progress.setVisibility(View.INVISIBLE);
            if (orderInfo.getReleaseusername().equals(App.getApp().getUser().getUsername())){
                ((AssistHolder)holder).iv_assist_msg.setVisibility(View.INVISIBLE);
            }else {
                ((AssistHolder)holder).iv_assist_msg.setVisibility(View.VISIBLE);
                ((AssistHolder)holder).iv_assist_msg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString(EaseConstant.EXTRA_USER_ID,orderInfo.getReleaseusername());
                        bundle.putString(EaseConstant.EXTRA_USER_AVATAR,orderInfo.getReleaseavatar());
                        bundle.putString(EaseConstant.EXTRA_USER_NAME,orderInfo.getReleasenickname());
                        IntentUtils.startActivity(context,ChatActivity.class,bundle);
                    }
                });
            }
            ((AssistHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    baseItemClickListener.onItemClick(position);
                }
            });
        }
    }

    public class AssistHolder extends RecyclerView.ViewHolder{
        public View itemView;
        public ImageView imageView ,iv_assist_msg;
        public TextView tv_name,tv_school,tv_assist_state,tv_assist_address,tv_assist_time,
                tv_assist_money,tv_assist_location,tv_assist_progress;
        public AssistHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.iv);
            iv_assist_msg = (ImageView) itemView.findViewById(R.id.iv_assist_msg);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_school = (TextView) itemView.findViewById(R.id.tv_school);
            tv_assist_state = (TextView) itemView.findViewById(R.id.tv_assist_state);
            tv_assist_address = (TextView) itemView.findViewById(R.id.tv_assist_address);
            tv_assist_time = (TextView) itemView.findViewById(R.id.tv_assist_time);
            tv_assist_money = (TextView) itemView.findViewById(R.id.tv_assist_money);
            tv_assist_location = (TextView) itemView.findViewById(R.id.tv_assist_location);
            tv_assist_progress = (TextView) itemView.findViewById(R.id.tv_assist_progress);
        }
    }
}
