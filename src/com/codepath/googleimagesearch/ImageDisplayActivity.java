package com.codepath.googleimagesearch;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;

/**
 * View a single image from the search results
 * 
 * @author shine
 *
 */
public class ImageDisplayActivity extends BasicActivity {

	private SmartImageView ivImageDisplay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		//requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_display);
		
		ivImageDisplay = (SmartImageView) findViewById(R.id.ivImageDisplay);
		final ImageResult imageResult = 
				(ImageResult) getIntent().getSerializableExtra(SearchActivity.IMAGE_KEY);
		Log.d("DEBUG", "Viewing image with url: " + imageResult.getFullUrl());
		
		// Check if image is available
		if (!checkForImage(imageResult.getFullUrl())) {
			Toast.makeText(getApplicationContext(), "Sorry! Image not available. "
					+ "Please view another image from results.", Toast.LENGTH_LONG).show();
			return;
		}
		
		ivImageDisplay.setImageUrl(imageResult.getFullUrl());
	}
	
	// Checks if a given image URL is valid
	private boolean checkForImage(String imageUrl) {
		
		try {
			final AsyncTask<URL, Integer, Long> viewTask = 
					new ViewImageTask().execute(new URL(imageUrl));
			if (viewTask.get().longValue() == 0L) {
				return true;
			}
			
		} catch (MalformedURLException mue) {
			Log.d("INFO", "URL malformed: " + imageUrl + " " + mue.getMessage());
			mue.printStackTrace();
		} catch (Exception ee) {
			Log.d("INFO", "Could not execute ViewImageTask for URL: " + imageUrl + " " + ee.getMessage());
			ee.printStackTrace();
		}
		return false;
	}
}

// Use an AsyncTask to avoid android.os.NetworkOnMainThreadException
class ViewImageTask extends AsyncTask<URL, Integer, Long> {

	@Override
	protected Long doInBackground(URL... urls) {
		
		// There is only one URL in the parameter list
		if (isImageAvailableFromUrl(urls[0])) {
			Log.d("DEBUG", "URL available: " + urls[0].toString());
			return 0L;
		}
		return -1L;
	}
	
	private static boolean isImageAvailableFromUrl(final URL url) {
		
		try {
	    	final InputStream is = (InputStream) url.getContent();
	    	is.close();
	    	return true;
		} catch (IOException io) {
			Log.d("INFO", "URL stream not available: " + url.toString() + " " + io.getMessage());
			io.printStackTrace();
	    } catch (Exception e) {
	    	Log.d("INFO", "Couldn't get URL stream: " + url.toString() + " " + e.getMessage());
	    }
		return false;
	}
}