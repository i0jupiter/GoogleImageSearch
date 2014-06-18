package com.codepath.googleimagesearch;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ImageResult implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String fullUrl;
	private String tbUrl;
	
	public ImageResult(JSONObject json) {
		try {
			this.fullUrl = json.getString("url");
			this.tbUrl = json.getString("tbUrl");
		} catch (JSONException e) {
			this.fullUrl = null;
			this.tbUrl = null;
		}
	}
	
	public static ArrayList<ImageResult> fromJSONArray(JSONArray imageJsonResults) {
		
		final ArrayList<ImageResult> imageResults = new ArrayList<ImageResult>();
		for (int i = 0; i < imageJsonResults.length(); i++) {
			try {
				final ImageResult imageResult = new ImageResult(imageJsonResults.getJSONObject(i));
				Log.d("DEBUG", imageResult.getFullUrl());
				imageResults.add(imageResult);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return imageResults;
	}
	
	public String getFullUrl() {
		return fullUrl;
	}
	
	public String getTbUrl() {
		return tbUrl;
	}
	
	@Override
	public String toString() {
		return "ImageResult [fullUrl=" + fullUrl + ", tbUrl=" + tbUrl + "]";
	}
}
