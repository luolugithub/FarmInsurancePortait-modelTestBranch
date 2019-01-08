package com.innovation.biz.insurance.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.innovation.animalInsurance.R;
import com.innovation.bean.BaoDanBeanNew;

import java.util.List;

public class YanBiaoAdapter extends BaseAdapter {

    private final List<BaoDanBeanNew> baoDanBeanNews;
    private Context context;


    public YanBiaoAdapter(Context context, List<BaoDanBeanNew> baoDanBeanNews) {
        this.context = context;
        this.baoDanBeanNews = baoDanBeanNews;
    }

    @Override
    public int getCount() {
        return baoDanBeanNews.size();
    }

    @Override
    public Object getItem(int position) {
        return baoDanBeanNews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.yaobiao_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.yan_name = (TextView) convertView.findViewById(R.id.yan_name);
            viewHolder.yan_time = (TextView) convertView.findViewById(R.id.yan_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String bankName = baoDanBeanNews.get(position).bankName;
        Log.i("bankName", bankName + "");
        viewHolder.yan_name.setText(bankName);
        viewHolder.yan_time.setText(baoDanBeanNews.get(position).createtime);
       /* viewHolder.yan_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferencesUtils.saveKeyValue(HttpUtils.baodanType, baoDanBeanNews.get(position).baodanType + "", InnApplication.getAppContext());
                PreferencesUtils.saveKeyValue(HttpUtils.id, baoDanBeanNews.get(position).id + "", InnApplication.getAppContext());
                BaseActivity activity = (YanBiaoDanActivity) YanBiaoAdapter.this.context;
                activity.goToActivity(HomeActivity.class, null);
            }
        });*/
     /*   viewHolder.lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferencesUtils.saveKeyValue(HttpUtils.baodanType, baoDanBeanNews.get(position).baodanType + "", InnApplication.getAppContext());
                PreferencesUtils.saveKeyValue(HttpUtils.id, baoDanBeanNews.get(position).id + "", InnApplication.getAppContext());
                BaseActivity activity = (YanBiaoDanActivity) YanBiaoAdapter.this.context;
                // activity.goToActivity(HomeActivity.class, null);
                activity.goToActivity(CreateYanActivity.class, null);
            }
        });*/
        return convertView;
    }

    class ViewHolder {
        TextView yan_name, yan_time;
    }
}
