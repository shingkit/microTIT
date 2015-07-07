package com.gyxy.sns;

import java.io.File;

import android.app.Application;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class MyApplication extends Application {
	private static MyApplication myApplication = null;

	public static MyApplication getInstance() {
		return myApplication;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		myApplication = this;
		initImageLoader();
	}

	/**
	 * ≥ı ºªØimageLoader
	 */
	public void initImageLoader() {
		File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.memoryCache(new LruMemoryCache(5 * 1024 * 1024))
				.memoryCacheSize(10 * 1024 * 1024)
				.diskCache(new UnlimitedDiscCache(cacheDir))
				.diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
				.build();
		ImageLoader.getInstance().init(config);
	}

	public DisplayImageOptions getOptions(int drawableId) {
		return new DisplayImageOptions.Builder().showImageOnLoading(drawableId)
				.showImageForEmptyUri(drawableId).showImageOnFail(drawableId)
				.resetViewBeforeLoading(true).cacheInMemory(true)
				.cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	public static DisplayImageOptions getHeadOptions() {
		return new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.person_image_empty)
				.showImageForEmptyUri(R.drawable.person_image_empty)
				.showImageOnFail(R.drawable.person_image_empty)
				.resetViewBeforeLoading(true).cacheInMemory(true)
				.cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	public static DisplayImageOptions getThumbnailOptions() {
		return new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.tit)
				.showImageForEmptyUri(R.drawable.tit)
				.showImageOnFail(R.drawable.tit).resetViewBeforeLoading(true)
				.cacheInMemory(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	public static DisplayImageOptions getDefaultOptions() {
		return new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.tit)
				.showImageForEmptyUri(R.drawable.tit)
				.showImageOnFail(R.drawable.tit).resetViewBeforeLoading(true)
				.cacheInMemory(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}
}
