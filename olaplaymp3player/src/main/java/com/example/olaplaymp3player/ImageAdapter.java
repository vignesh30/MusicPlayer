package com.example.olaplaymp3player;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageAdapter extends PagerAdapter 
{
	Context context;
	int position;
	private int[] GalImages = new int[] 
	{
        R.drawable.landing,
		R.drawable.one,
		R.drawable.two,
		R.drawable.three
	};
	
	ImageAdapter(Context context)
	{
		this.context=context;
	}
	
	@Override
	public int getCount() 
	{
		return GalImages.length;
	}
	 
	@Override
	public boolean isViewFromObject(View view, Object object) 
	{
		return view == ((ImageView) object);
	}
	
	public int getposition()
	{
		return position;
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) 
	{
		this.position=position;
		ImageView imageView = new ImageView(context);
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		imageView.setImageResource(GalImages[position]);
		((ViewPager) container).addView(imageView, 0);
		return imageView;
	}
	 
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) 
	{
		((ViewPager) container).removeView((ImageView) object);
	}
}