package com.innovation.biz.dialog;

import android.app.Activity;
import android.app.Dialog;
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

import com.bumptech.glide.Glide;
import com.innovation.animalInsurance.R;
import com.innovation.base.GlobalConfigure;
import com.innovation.utils.CaptureImageItem;
import com.innovation.utils.FileUtils;
import com.innovation.utils.ScreenUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by haojie on 2018/6/6.
 */

public class ReviewImageDialog extends Dialog {
    private String TAG = "ReviewImageDialog";
    private Activity mActivity;
    private String mPath;
    RecyclerView recyclerView;
    private ImageAdapter myAdapter;
    List<CaptureImageItem> imageList = new ArrayList<>();
    List<String> str = new ArrayList<>();
    private ShowImageDialog mShowImageDialog = null; //

    public ReviewImageDialog(Activity activity, View view, String dir) {
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
        initImageDialog();
        mPath = dir;
        recyclerView = (RecyclerView)view.findViewById(R.id.image_recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        //setData();
        myAdapter = new ImageAdapter();
        recyclerView.setAdapter(myAdapter);
        // 设置item及item中控件的点击事件
        myAdapter.setOnItemClickListener(onItemClickListener);

//        myAdapter.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                Log.d("s", "f");
//            }
//        });
    }

    /**
     * Item点击监听
     */
    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position , View v) {
            CaptureImageItem item = imageList.get(position);
            String path = item.getUrl();
            switch (v.getId()){
                case R.id.image_view:
                    Log.i(TAG, "position:" + position);
                    if (mShowImageDialog == null)
                        initImageDialog();
                    mShowImageDialog.updateView(path);
                    mShowImageDialog.show();

                    break;

                case R.id.image_del:
                    //删除内存中对应文件
                    FileUtils.deleteFile(path);
                    //更新列表
                    imageList.remove(position);
                    myAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    public void setData() {
        imageList.clear();
        String suff = "";
        int index = GlobalConfigure.IMAGE_SUFFIX.indexOf(".");
        if(index < 0)
            suff = GlobalConfigure.IMAGE_SUFFIX;
        else
            suff = GlobalConfigure.IMAGE_SUFFIX.substring(index+1);

        List<String> list_images = FileUtils.GetFilesAll(mPath, suff, true);

        String findangel = "Angle-";
        String angle = "";
        for (int i = 0; i < list_images.size(); i++)
        {
            CaptureImageItem itme = new CaptureImageItem();
            String tmp = list_images.get(i);
            index = tmp.indexOf(findangel);
            if(index < 0)
                list_images.remove(i);
            else {
                int start = index+findangel.length();
                angle = tmp.substring(start, start+2);
                if(angle.indexOf("0")==0)
                {
                    angle = angle.substring(1);
                }
            }
            itme.setUrl(tmp);
            itme.setAngle("角度："+ angle);
            imageList.add(itme);
        }
    }

    public void updateView()
    {
        setData();
        myAdapter.notifyDataSetChanged();
    }

    private void initImageDialog() {
        mShowImageDialog = new ShowImageDialog(mActivity);
        mShowImageDialog.setTitle(R.string.dialog_title);
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View v);
    }
    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder>{
        private OnItemClickListener mOnItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener){
            this.mOnItemClickListener = onItemClickListener;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.review_image_layout, viewGroup,
                    false));
            // View view = LayoutInflater.from(mActivity).inflate(R.layout.review_image_layout, viewGroup,false);
            myViewHolder.mOnItemClickListener = mOnItemClickListener;
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder myViewHolder, int position) {
            /**
             * 将position保存在itemView的Tag中以便点击时获取
             */
            myViewHolder.itemView.setTag(position);

            File file = new File(imageList.get(position).getUrl());
            Glide.with(mActivity).load(file).into(myViewHolder.image_icon);
            myViewHolder.image_angle.setText(imageList.get(position).getAngle());
            String path = imageList.get(position).getUrl();
            int index = path.lastIndexOf("/");
            if(index != -1)
                path = path.substring(index + 1);
            myViewHolder.image_url.setText(path);
        }

        @Override
        public int getItemCount() {
            return imageList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            OnItemClickListener mOnItemClickListener;
            ImageView image_icon;
            TextView image_angle;
            TextView image_url;
            TextView image_view;
            TextView image_del;


            public MyViewHolder(View view) {
                super(view);
                image_icon = (ImageView) view.findViewById(R.id.image_icon);
                image_angle = (TextView) view.findViewById(R.id.image_angle);
                image_url = (TextView) view.findViewById(R.id.image_url);
                image_view = (TextView) view.findViewById(R.id.image_view);
                image_del = (TextView) view.findViewById(R.id.image_del);
                image_view.setOnClickListener(this);
                image_del.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(getAdapterPosition() , v);
                }
            }
        }
    }


//    public interface OnItemClickListener {
//        void onItemClick(View view, int position);
//    }
//    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder>{
//        private OnItemClickListener mOnItemClickListener;//声明接口
//
//        @Override
//        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//            MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.review_image_layout, viewGroup,
//                    false));
//            View view = LayoutInflater.from(mActivity).inflate(R.layout.review_image_layout, viewGroup,false);
//            return viewHolder;
//        }
//
//        @Override
//        public void onBindViewHolder(final MyViewHolder myViewHolder, int position) {
//            /**
//             * 将position保存在itemView的Tag中以便点击时获取
//             */
//            myViewHolder.itemView.setTag(position);
//
//            File file = new File(imageList1.get(position).getUrl());
//            Glide.with(mActivity).load(file).into(myViewHolder.image_icon);
//            myViewHolder.image_angle.setText(imageList1.get(position).getAngle());
//
//
//            View itemView = ((RelativeLayout) myViewHolder.itemView).getChildAt(0);
//            if (mOnItemClickListener != null) {
//                itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        int position = myViewHolder.getLayoutPosition();
//                        mOnItemClickListener.onItemClick(myViewHolder.itemView, position);
//                    }
//                });
//            }
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return imageList1.size();
//        }
//
//        public void setOnItemClickListener(OnItemClickListener listener) {
//            mOnItemClickListener = listener;
//        }
//
//        class MyViewHolder extends RecyclerView.ViewHolder {
//            ImageView image_icon;
//            TextView image_angle;
//            //LinearLayout image_tab;
//
//            public MyViewHolder(View view) {
//                super(view);
//                image_icon = (ImageView) view.findViewById(R.id.image_icon);
//                image_angle = (TextView) view.findViewById(R.id.image_angle);
////                image_tab = (LinearLayout) view.findViewById(R.id.image_tab);
//            }
//        }
//    }

}
