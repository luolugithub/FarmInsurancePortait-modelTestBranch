package com.innovation.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.innovation.animalInsurance.R;
import com.innovation.utils.UIUtils;

/**
 * Created by wanbo on 2017/1/20.
 */

public class SendView extends RelativeLayout {

    public RelativeLayout backLayout, selectLayout;

    public SendView(Context context) {
        super(context);
        init(context);
    }

    public SendView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SendView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        //LayoutParams params = new LayoutParams(UIUtils.getWidthPixels(context), UIUtils.dp2px(context, 180f));//haojie del
        LayoutParams params = new LayoutParams(UIUtils.getHeightPixels(context), UIUtils.dp2px(context, 180f));//haojie changed
        setLayoutParams(params);
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.send_layout, null, false);
        layout.setLayoutParams(params);
        backLayout = (RelativeLayout) layout.findViewById(R.id.cancel);
        selectLayout = (RelativeLayout) layout.findViewById(R.id.save);
        addView(layout);
        setVisibility(GONE);
    }

    public void startAnim() {
        setVisibility(VISIBLE);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(backLayout, "translationX", 0, -360),
                ObjectAnimator.ofFloat(backLayout, "alpha", 0, 1),
                ObjectAnimator.ofFloat(selectLayout, "translationX", 0, 360),
                ObjectAnimator.ofFloat(selectLayout, "alpha", 0, 1)
        );
        set.setDuration(300).start();
    }

    public void stopAnim() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(backLayout, "translationX", -360, 0),
                ObjectAnimator.ofFloat(selectLayout, "translationX", 360, 0)
        );
        set.setDuration(250).start();
        setVisibility(GONE);
    }

}
