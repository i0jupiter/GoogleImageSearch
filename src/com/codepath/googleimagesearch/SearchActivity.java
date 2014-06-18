package com.codepath.googleimagesearch;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.codepath.googleimagesearch.ImageSearchSettingsDialogFragment.ImageSearchSettingsDialogListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * The main search activity screen for the Google Search image search app. -
 * Shows the results in a grid - Pulls the Settings Dialog from the action bar -
 * Gets the filters from the Settings Dialog and applies them to the search
 * query - Clicking on a single image shows it in full
 * 
 * @author shine
 * 
 */
public class SearchActivity extends BasicActivity implements
		ImageSearchSettingsDialogListener {

	private static final int DEFAULT_NUM_RESULTS_PER_PAGE = 8;
	public static final String IMAGE_KEY = "singleImage";
	public static final String SEARCH_SETTINGS_DIALOG_TITLE = "Filter Your Search";
	public static final String SEARCH_BASE_URL = "https://ajax.googleapis.com/ajax/services/search/images?rsz=8&v=1.0&q=";

	// private EditText etQuery;
	private GridView gvSearchResults;
	private ImageResultArrayAdapter imageAdapter;
	private SearchView miActionSearch;

	private ArrayList<ImageResult> imageResults;
	private ImageSearchSettingsDialogFragment settingsDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		gvSearchResults = (GridView) findViewById(R.id.gvSearchResults);
		imageResults = new ArrayList<ImageResult>();
		imageAdapter = new ImageResultArrayAdapter(this, imageResults);
		gvSearchResults.setAdapter(imageAdapter);
		setupListenersOnResultGrid();
	}

	// Create the search settings folder in the action bar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_search_menu, menu);

		final MenuItem searchItem = menu.findItem(R.id.miActionSearch);
		miActionSearch = (SearchView) MenuItemCompat.getActionView(searchItem);
		miActionSearch.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {

				// If no query is entered, the soft keyboard stays and nothing
				// happens

				// When a valid query is entered,
				// clear out the old adapter before issuing a new query
				imageAdapter.clear();

				// Escape the query and start with the first page of results
				getImageSearchResults("&start=0");

				// Hide the soft keyboard
				miActionSearch.clearFocus();

				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	// Show the dialog when the Settings button is pressed in the action bar
	public void onImageSearchSettings(MenuItem mi) {

		final FragmentManager fragmentManager = getSupportFragmentManager();
		settingsDialog = ImageSearchSettingsDialogFragment
				.newInstance(SEARCH_SETTINGS_DIALOG_TITLE);
		// Set the same font as the main activity of the dialog
		settingsDialog.setFont(getFont());
		settingsDialog.show(fragmentManager, "activity_image_search_menu");
	}

	// Close the dialog when 'Dons' is pressed in the Settings dialog
	@Override
	public void onFinishSettingsDialog(ImageSearchFilters searchFilters) {

		// Persist the selected search filters
		ImageSearchFilters.writeSearchFiltersToFile(getApplicationContext(),
				searchFilters);

		// Set the focus to the search box in ActionBar
		// in case the user wants to change their query.
		miActionSearch.requestFocus();
	}

	// Setup listeners when the search results are scrolled in the grid or a
	// single image is clicked
	private void setupListenersOnResultGrid() {

		// For infinite scroll
		gvSearchResults.setOnScrollListener(new EndlessScrollListener() {

			@Override
			public void onLoadMore(int page, int totalItemsCount) {

				getMoreImageSearchResults(page);

				// Hide the soft keyboard to allow a full screen of search
				// results
				final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(gvSearchResults.getWindowToken(), 0);
			}
		});

		// For viewing a single image
		gvSearchResults.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				final Intent singleImageIntent = new Intent(
						getApplicationContext(), ImageDisplayActivity.class);
				final ImageResult imageResult = imageResults.get(position);
				singleImageIntent.putExtra(IMAGE_KEY, imageResult);
				startActivity(singleImageIntent);
			}
		});
	}

	// Get the image search results from Google using the given query and URL
	// parameters
	private void getImageSearchResults(final String urlParams) {

		// Get the query and it can't be empty at this point
		final String query = miActionSearch.getQuery().toString().trim();

		// Construct the final URL and run the query
		final StringBuilder sbQuery = new StringBuilder(SEARCH_BASE_URL);
		sbQuery.append(Uri.encode(query));
		sbQuery.append(urlParams);

		final ImageSearchFilters imageSearchFilters = ImageSearchFilters
				.readSearchFiltersFromFile(getApplicationContext());
		if (imageSearchFilters != null) {
			sbQuery.append(ImageSearchFilters
					.getUrlFromFilters(imageSearchFilters));
		}

		final String url = sbQuery.toString();
		Log.d("DEBUG", "FINAL URL for search: " + url);

		// Check network availability and run query
		if (!isNetworkAvailable()) {
			Toast.makeText(this, "Please check your Internet connection.",
					Toast.LENGTH_LONG).show();
			Log.d("INFO",
					"Google image search attempted when Internet was not available.");
			return;
		}

		runQuery(url);
	}

	// Issue an HTTP query with the given URL
	private void runQuery(final String url) {

		final AsyncHttpClient client = new AsyncHttpClient();

		client.get(url, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(JSONObject response) {
				JSONArray imageJsonResults = null;
				try {
					imageJsonResults = response.getJSONObject("responseData")
							.getJSONArray("results");
					final ArrayList<ImageResult> imageResultsArrayList = ImageResult
							.fromJSONArray(imageJsonResults);
					
					// Set the results in the Adapter
					imageAdapter.addAll(imageResultsArrayList);
					
					// Alert the user if no results were found.
					if (imageResultsArrayList.size() == 0) {
						if (imageAdapter.isEmpty()) {
							Toast.makeText(getApplicationContext(),
									"No results found.", Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(getApplicationContext(),
									"No more results found.", Toast.LENGTH_LONG).show();
						}
					}
					
				} catch (JSONException e) {
					Log.d("WARN", "JsonException: " + e);
				}
			}

			@Override
			public void onFailure(int statusCode, Throwable e,
					JSONArray errorResponse) {

				Log.d("INFO", "Status code: " + statusCode
						+ " returned for URL: " + url);
				super.onFailure(statusCode, e, errorResponse);
			}
		});
	}

	// Check network availability
	private boolean isNetworkAvailable() {

		final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return (activeNetworkInfo != null && activeNetworkInfo
				.isConnectedOrConnecting());
	}

	// Get more image search results for infinite scrolling
	private void getMoreImageSearchResults(int page) {

		// Set the result start index of the new search query for infinite
		// scrolling
		Log.d("INFO", "Getting more results with page: " + page);
		final int startResult = (page - 1) * DEFAULT_NUM_RESULTS_PER_PAGE;

		final String urlParams = "&start=" + startResult;
		Log.d("DEBUG", "Starting result: " + urlParams);
		getImageSearchResults(urlParams);
	}
}