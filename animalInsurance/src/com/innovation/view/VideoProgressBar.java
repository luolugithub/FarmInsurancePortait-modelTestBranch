package com.innovation.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.innovation.animalInsurance.R;

/**
 * Created by Wanbo on 2016/12/7.
 */

public class VideoProgressBar extends View {

    private static final String TAG = "BothWayProgressBar";
    private boolean isCancel = false;
    private Paint mRecordPaint, mBgPaint;
    private RectF mRectF;
    public int progress;
    private OnProgressEndListener mOnProgressEndListener;
    private int width, height;
    private int MaxProgress = 150; //haojie add 录像时间改为15秒

    private static final int MSG_UPDATE_PROGRESS = 1;

    public VideoProgressBar(Context context) {
        super(context, null);
    }

    public VideoProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mRecordPaint = new Paint();
        mBgPaint = new Paint();
        mRectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        width = this.getWidth();
        height = this.getHeight();

        if (width != height) {
            int min = Math.min(width, height);
            width = min;
            height = min;
        }

        // 设置画笔相关属性
        int mCircleLineStrokeWidth = 10;
        mRecordPaint.setAntiAlias(true);
        mRecordPaint.setStrokeWidth(mCircleLineStrokeWidth);
        mRecordPaint.setStyle(Paint.Style.STROKE);
        // 位置
        mRectF.left = mCircleLineStrokeWidth / 2 + .8f;
        mRectF.top = mCircleLineStrokeWidth / 2 + .8f;
        mRectF.right = width - mCircleLineStrokeWidth / 2 - 1.5f;
        mRectF.bottom = height - mCircleLineStrokeWidth / 2 - 1.5f;

        // 实心圆
        mBgPaint.setAntiAlias(true);
        mBgPaint.setStrokeWidth(mCircleLineStrokeWidth);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setColor(getResources().getColor(R.color.btn_bg));
        canvas.drawCircle(width / 2, width / 2, width / 2 - .5f, mBgPaint);
        // 绘制圆圈，进度条背景
        if (isCancel) {
            mRecordPaint.setColor(Color.TRANSPARENT);
            canvas.drawArc(mRectF, -90, 360, false, mRecordPaint);
            isCancel = false;
            return;
        }
        //int mMaxProgress = 100;
        int mMaxProgress = MaxProgress; //haojie add
        if (progress > 0 && progress < mMaxProgress) {
            mRecordPaint.setColor(Color.rgb(0x03, 0xa9, 0xf4));
            canvas.drawArc(mRectF, -90, ((float) progress / mMaxProgress) * 360, false, mRecordPaint);
        } else if (progress == 0) {
            mRecordPaint.setColor(Color.TRANSPARENT);
            canvas.drawArc(mRectF, -90, 360, false, mRecordPaint);
        } else if (progress >= mMaxProgress) {
            mRecordPaint.setColor(Color.rgb(0x03, 0xa9, 0xf4));
            canvas.drawArc(mRectF, -90, 360, false, mRecordPaint);
            if (mOnProgressEndListener != null) {
                mOnProgressEndListener.onProgressEndListener();
            }
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            int action = msg.what;
            switch (action) {
                case MSG_UPDATE_PROGRESS:
                    VideoProgressBar.this.invalidate();
                    if (isCancel || progress > MaxProgress) { //if (isCancel || progress > 100) {
                        mHandler.removeMessages(MSG_UPDATE_PROGRESS);
                        return;
                    }
                    progress += 0.1;
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PROGRESS, 200);//0.2秒发送一次消息

                    break;
            }
        }
    };

    public void startAnimation() {
//        GlobalConfigure.VIDEO_PROCESS = true;
        isCancel = false;
        progress = 2;
        invalidate();
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PROGRESS, 200);//0.2秒发送一次消息

    }

    public void stopAnimation() {
//        GlobalConfigure.VIDEO_PROCESS = false;
        mHandler.removeMessages(MSG_UPDATE_PROGRESS);
        progress = 0;
        invalidate();
    }

    public void setOnProgressEndListener(OnProgressEndListener onProgressEndListener) {
        mOnProgressEndListener = onProgressEndListener;
    }

    public interface OnProgressEndListener {
        void onProgressEndListener();
    }

}
