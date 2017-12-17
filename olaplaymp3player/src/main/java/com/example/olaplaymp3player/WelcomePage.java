package com.example.olaplaymp3player;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class WelcomePage extends Activity {
	RadioGroup group;
	RadioButton[] radio;
	FloatingActionButton fab;
    int i=0;
    ViewPager viewPager;
	@Override
	protected void onCreate(Bundle instance)
	{
		super.onCreate(instance);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.welcome);

		 fab = (FloatingActionButton) findViewById(R.id.fab);

		 viewPager = (ViewPager) findViewById(R.id.view_pager);
        ImageAdapter adapter = new ImageAdapter(this);
        viewPager.setAdapter(adapter);
        
        group=(RadioGroup) findViewById(R.id.radioGroup1);
        radio=new RadioButton[4];
        
        radio[0]=(RadioButton) findViewById(R.id.radio0);
        radio[1]=(RadioButton) findViewById(R.id.radio1);
        radio[2]=(RadioButton) findViewById(R.id.radio2);
        radio[3]=(RadioButton) findViewById(R.id.radio3);
        
        for(int i=0;i<4;i++)
        {
        	radio[i].setEnabled(false);
        }

        viewPager.setOnPageChangeListener(new OnPageChangeListener() 
        {
			
			@Override
			public void onPageSelected(int arg0)
			{
                i = arg0;
				radio[arg0].setChecked(true);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) 
			{
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) 
			{
				
			}
		});
        
        fab.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v) 
			{
                if(i==3) {
                    finish();
                    startActivity(new Intent(WelcomePage.this, MainActivity.class));
                }else{
                    viewPager.setCurrentItem(i+1, true);
                }
			}
		});
	}
}
