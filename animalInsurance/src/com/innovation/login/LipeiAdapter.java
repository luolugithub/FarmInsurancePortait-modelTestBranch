package com.innovation.login;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innovation.animalInsurance.R;
import com.innovation.bean.QueryBaodanBean;

import java.util.ArrayList;

public class LipeiAdapter extends RecyclerView.Adapter<LipeiAdapter.ViewHolder> {
    private ArrayList<QueryBaodanBean> newsBeanArrayList;
    private Context context;
    private RecyclerViewOnItemClickListener mOnItemClickListener;
    private RecyclerViewOnItemClickListener mOnItemClickListenerZiliao;

    private String result;
    private String TAG = "LipeiAdapter";

    public LipeiAdapter(ArrayList<QueryBaodanBean> newsBeanArrayList, Context mContext) {
        this.newsBeanArrayList = newsBeanArrayList;
        this.context = mContext;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lipei_item, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        viewHolder.lipei_num.setText(newsBeanArrayList.get(position).baodanNo);
        viewHolder.lipei_name.setText(newsBeanArrayList.get(position).name);
        String mtoubao_date;
         mtoubao_date = newsBeanArrayList.get(position).createtime.substring(0,newsBeanArrayList.get(position).createtime.trim().length()-11);
        viewHolder.lipei_date.setText(mtoubao_date);
        if(newsBeanArrayList.get(position).cardNo !=null) {
            viewHolder.lipei_idcard.setText(newsBeanArrayList.get(position).cardNo);
        }

        viewHolder.lipei_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.putExtra("baodanNumber",newsBeanArrayList.get(position).baodanNo);
                intent.setClass(context, LipeiDetailActivity.class);
                context.startActivity(intent);

            }
        });

    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return newsBeanArrayList.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView lipei_num;
        public TextView lipei_name;
        public TextView isnot_lipei;
        public TextView lipei_date;
        public TextView lipei_idcard;
        public TextView lipei_detail;

        public ViewHolder(View view) {
            super(view);
            lipei_num = (TextView) view.findViewById(R.id.lipei_num);
            lipei_name = (TextView) view.findViewById(R.id.lipei_name);
            lipei_date = (TextView) view.findViewById(R.id.lipei_date);
            lipei_idcard = (TextView) view.findViewById(R.id.lipei_idcard);
            isnot_lipei = (TextView) view.findViewById(R.id.isnot_lipei);
            lipei_detail = (TextView) view.findViewById(R.id.lipei_detail);
        }
    }


    /**
     * 设置点击事件
     */
    public void setRecyclerViewOnItemClickListener(RecyclerViewOnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    /**
     * 设置点击事件
     */
    public void setRecyclerViewOnItemClickListenerZiliao(RecyclerViewOnItemClickListener onItemClickListener) {
        this.mOnItemClickListenerZiliao = onItemClickListener;
    }

    /**
     * 点击事件接口
     */
    public interface RecyclerViewOnItemClickListener {
        void onItemClickListener(View view, int position);
    }



}
