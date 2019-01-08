package com.innovation.biz.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.innovation.animalInsurance.R;
import com.innovation.base.GlobalConfigure;
import com.innovation.utils.CaptureImageItem;
import com.innovation.utils.FileUtils;
import com.innovation.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haojie on 2018/6/6.
 */

public class ReviewVideoDialog extends Dialog {
    private String TAG = "ReviewVideoDialog";
    private Activity mActivity;
    private Context mContext;
    private String mPath;
    RecyclerView recyclerView;
    private videoAdapter myAdapter;
    List<CaptureImageItem> videoList = new ArrayList<>();
    List<String> str = new ArrayList<>();
    private ShowVideoDialog mShowVideoDialog = null; //

    public ReviewVideoDialog(Activity activity, View view, String dir) {
        super(activity, R.style.Alert_Dialog_Style);
        setContentView(view);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.alpha = 1.0f;
        params.height = (int) (ScreenUtil.getScreenHight() - 35 * ScreenUtil.getDensity());
        params.width = (int) (ScreenUtil.getScreenWidth() - 35 * ScreenUtil.getDensity());
        window.setAttributes(params);
        setCanceledOnTouchOutside(false);

        mActivity = activity;
        initVideoDialog();
        mPath = dir;
        recyclerView = (RecyclerView)view.findViewById(R.id.image_recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        myAdapter = new videoAdapter();
        recyclerView.setAdapter(myAdapter);
        // 设置item及item中控件的点击事件
        myAdapter.setOnItemClickListener(onItemClickListener);

    }

    /**
     * Item点击监听
     */
    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position , View v) {
            CaptureImageItem item = videoList.get(position);
            String path = item.getUrl();
            switch (v.getId()){
                case R.id.video_view:
                    Log.d(TAG, "position====:" + position);
                    if (mShowVideoDialog == null)
                        initVideoDialog();
                    mShowVideoDialog.updateView(path);
                    mShowVideoDialog.show();
                    break;

                case R.id.video_del:
                    Log.d(TAG, "del position====:" + position);
                    //删除内存中对应文件
                    FileUtils.deleteFile(path);
                    //更新列表
                    videoList.remove(position);
                    myAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    public void setData() {
        videoList.clear();
        String suff = "";
        int index = GlobalConfigure.VIDEO_SUFFIX.indexOf(".");
        if(index < 0)
            suff = GlobalConfigure.VIDEO_SUFFIX;
        else
            suff = GlobalConfigure.VIDEO_SUFFIX.substring(index+1);

        List<String> list_videos = FileUtils.GetFilesAll(mPath, suff, true);

        for (int i = 0; i < list_videos.size(); i++)
        {
            CaptureImageItem itme = new CaptureImageItem();
            String tmp = list_videos.get(i);
            itme.setUrl(tmp);
            videoList.add(itme);
        }
    }

    public void updateView()
    {
        setData();
        myAdapter.notifyDataSetChanged();
    }

    private void initVideoDialog() {
        mShowVideoDialog = new ShowVideoDialog(mActivity);
        mShowVideoDialog.setTitle(R.string.dialog_title);
    }


    public interface OnItemClickListener {
        void onItemClick(int position, View v);
    }
    private class videoAdapter extends RecyclerView.Adapter<videoAdapter.MyViewHolder>{
        private OnItemClickListener mOnItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener){
            this.mOnItemClickListener = onItemClickListener;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.review_video_layout, viewGroup,
                    false));
            myViewHolder.mOnItemClickListener = mOnItemClickListener;
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder myViewHolder, int position) {
            /**
             * 将position保存在itemView的Tag中以便点击时获取
             */
            myViewHolder.itemView.setTag(position);
            //用来设置要播放的mp4文件
            String path = videoList.get(position).getUrl();
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource(path);
            Bitmap bitmap = media.getFrameAtTime();
            myViewHolder.video_icon.setImageBitmap(bitmap);

            int index = path.lastIndexOf("/");
            if(index != -1)
                path = path.substring(index + 1);
            myViewHolder.video_url.setText(path);
        }

        @Override
        public int getItemCount() {
            return videoList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            OnItemClickListener mOnItemClickListener;
            ImageView video_icon;
            TextView video_url;
            TextView video_view;
            TextView video_del;


            public MyViewHolder(View view) {
                super(view);
                video_icon = (ImageView) view.findViewById(R.id.video_icon);
                video_url = (TextView) view.findViewById(R.id.video_url);
                video_view = (TextView) view.findViewById(R.id.video_view);
                video_del = (TextView) view.findViewById(R.id.video_del);
                video_view.setOnClickListener(this);
                video_del.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(getAdapterPosition() , v);
                }
            }
        }
    }



}
