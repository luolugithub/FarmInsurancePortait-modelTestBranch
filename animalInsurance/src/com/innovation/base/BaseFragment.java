package com.innovation.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import butterknife.Unbinder;

/**
 * Created by lxr on 2018/4/27.
 */

public abstract class BaseFragment extends Fragment{
    protected Activity mActivity ;
    Unbinder unbinder;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (Activity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  BaseFragment.this.createSuccessView();
    }

    public abstract View createSuccessView() ;

    @Override
    public void onDestroy() {
        if (this.unbinder != null) {
            this.unbinder.unbind();
        }
        super.onDestroy();
    }
}
