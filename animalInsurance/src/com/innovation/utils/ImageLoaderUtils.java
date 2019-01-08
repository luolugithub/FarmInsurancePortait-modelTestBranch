package com.innovation.utils;

import android.content.Context;

import com.innovation.animalInsurance.BuildConfig;
import com.innovation.animalInsurance.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * @author wbs on 12/16/17.
 */

public class ImageLoaderUtils {
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.diskCache(new UnlimitedDiskCache(StorageUtils.getCacheDir(context)));

        if (BuildConfig.DEBUG) {
            config.writeDebugLogs(); // 发布release包时，移除log信息
        }

        // 用configuration初始化ImageLoader
        ImageLoader.getInstance().init(config.build());
    }

    public static DisplayImageOptions getDefaultOptions() {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_load_image)
                .showImageForEmptyUri(R.drawable.default_load_image)
                .showImageOnFail(R.drawable.default_load_image)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }
}
