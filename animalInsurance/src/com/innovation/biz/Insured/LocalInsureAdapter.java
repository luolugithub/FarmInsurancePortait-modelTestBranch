package com.innovation.biz.Insured;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.innovation.animalInsurance.R;
import com.innovation.base.Model;
import com.innovation.data.source.InsuredNos;

import es.dmoral.prefs.Prefs;
import org.tensorflow.demo.DetectorActivity;
import com.innovation.base.GlobalConfigure;

public class LocalInsureAdapter extends RecyclerView.Adapter<LocalInsureAdapter.ViewHolder> {
    InsuredNos insuredNos;
    Context mContext;
    public LocalInsureAdapter(Context context) {
        mContext = context;
        Prefs prefs = Prefs.with(context, "insured_nos");
        String jsonString = prefs.read("insured_nos");
        Gson gson = new Gson();
        insuredNos = gson.fromJson(jsonString, InsuredNos.class);
        if (insuredNos == null) {
            insuredNos = new InsuredNos();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.local_insured_recycle_view_layout, parent, false);
        RecyclerView.ViewHolder viewHolder = new ViewHolder(view);
        return (ViewHolder) viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object insNos = insuredNos.get(position);
        holder.localInsuredNos.setText(insNos.toString());
        holder.button.setOnClickListener(v -> {
            // TODO: 2018/8/7
            Toast.makeText(mContext, "离线投保", Toast.LENGTH_SHORT).show();

            GlobalConfigure.model = Model.BUILD.value();
            Intent intent = new Intent();
            intent.putExtra("ToubaoTempNumber", (String)insNos);
            intent.setClass(mContext, DetectorActivity.class);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return insuredNos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView localInsuredNos;
        public Button button;
        public ViewHolder(View itemView) {
            super(itemView);
            localInsuredNos = itemView.findViewById(R.id.insured_no_text);
            button = itemView.findViewById(R.id.insured_button);
        }
    }
}
