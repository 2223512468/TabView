package com.tabview;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	private LinearLayout mLayout;
	private LinearLayout mLayoutHidden;

	private int screenWidth;
	float density;
	private Animation expandAnim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mLayoutHidden = (LinearLayout)findViewById(R.id.hidden);
		mLayout = (LinearLayout)findViewById(R.id.container);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		screenWidth = metric.widthPixels;
		density = metric.density;
		screenWidth -= 40 * density;
		String[] skills = getResources().getStringArray(R.array.skill);
		View view = null;
		for (int i = 0; i < skills.length; i++) {
			view = initView(skills[i]);
			mLayoutHidden.addView(view);
		}
		
		expandAnim = AnimationUtils.loadAnimation(this, R.anim.expand);

		new TabTask().execute();
	}

	private LinearLayout initLayout() {
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setGravity(Gravity.CENTER_HORIZONTAL);
		layout.setAnimationCacheEnabled(true);
		layout.setLayoutAnimation(new LayoutAnimationController(expandAnim));
		return layout;
	}

	private View initView(String str) {
		View view = LayoutInflater.from(this).inflate(R.layout.item_main, null);
		TextView textView = (TextView) view.findViewById(R.id.text);
		textView.setText(str);
		return view;
	}
	
	private void addLayout(List<TextView> items){
		LinearLayout layout = initLayout();
		for (TextView textView : items) {
			layout.addView(initView(textView.getText().toString()));
		}
		mLayout.addView(layout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	class TabTask extends AsyncTask<Void, TextView, Boolean> {

		int lineWidth = 0;
		LinearLayout lineLayout = null;
		List<TextView> items = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			items = new ArrayList<TextView>();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			for (int i = 0; i < mLayoutHidden.getChildCount(); i++) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				View view = mLayoutHidden.getChildAt(i);
				publishProgress((TextView) view.findViewById(R.id.text));
			}
			return true;
		}

		@Override
		protected void onProgressUpdate(TextView... values) {
			super.onProgressUpdate(values);
			lineWidth += values[0].getWidth() + 20 * density;
			if(lineWidth > screenWidth){
				addLayout(items);
				items.clear();
				items.add(values[0]);
				lineWidth = values[0].getWidth();
			} else {
				items.add(values[0]);
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			addLayout(items);
			mLayoutHidden.removeAllViews();
		}
	}
}
