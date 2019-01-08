package com.innovation.login.presenter;


import android.content.Context;

import com.innovation.login.model.TouBaoModel;
import com.innovation.login.view.ITouBao;

import java.util.TreeMap;

public class TouBaoPresenter implements ITouBaoPresenter {
    private Context context;
    private ITouBao iTouBao;

    private TouBaoModel touBaoModel;

    public TouBaoPresenter(ITouBao iTouBao) {
        this.iTouBao = iTouBao;
        touBaoModel = new TouBaoModel(this);
    }



    public void setparameter(String insurDetailQueryUrl, TreeMap query) {
        touBaoModel.setparameter(insurDetailQueryUrl, query);
    }


    @Override
    public void success(String s) {
        iTouBao.success(s);
    }

    @Override
    public void error(String errStr) {
        iTouBao.error(errStr);
    }
}
