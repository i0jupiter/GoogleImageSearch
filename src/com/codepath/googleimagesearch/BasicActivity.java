package com.codepath.googleimagesearch;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

/**
 * The Activity class to setup some attributes application-wide.
 * 
 * @author shine
 *
 */
public class BasicActivity extends FragmentActivity {

	private Typeface font;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		// Use this font throughout the application
		font = Typeface.createFromAsset(getAssets(), "fonts/KaushanScript-Regular.otf");
		
		// Set it in the ActionBar
		final int actionBarTitle = 
				Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
		final TextView actionBarTitleView = 
				(TextView) getWindow().findViewById(actionBarTitle);
		if (actionBarTitleView != null) {
		    actionBarTitleView.setTypeface(font);
		}
	}
	
	public Typeface getFont() {
		return this.font;
	}
}
