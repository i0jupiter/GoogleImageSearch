package com.codepath.googleimagesearch;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Dialog shown when the settings in the ActionBar on SearchActivity is clicked.
 * Loads preset filters from a file or from {@link ImageSearchFilters}.
 * 
 * @author shine
 *
 */
public class ImageSearchSettingsDialogFragment 
		extends DialogFragment 
				implements OnClickListener {
	
	private Button btnSettingsDone;
	private Spinner spImageSize;
	private Spinner spImageColor;
	private Spinner spImageType;
	private Spinner spImageFromSite;
	private ImageSearchFilters searchFilters;
	private TextView tvImageSearchSettingsDialogTitle;
	private Typeface font;
	
	private ArrayAdapter<String> imageSizeAdapter;
	private ArrayAdapter<String> imageColorAdapter;
	private ArrayAdapter<String> imageTypeAdapter;
	private ArrayAdapter<String> imageFromSiteAdapter;
	
	// Implement this interface to handle events in the Settings Dialog views
	public interface ImageSearchSettingsDialogListener {
        void onFinishSettingsDialog(ImageSearchFilters searchFilters);
    }
	
	private ImageSearchSettingsDialogListener listener;
	
	public ImageSearchSettingsDialogFragment() {
		
	}
	
	// Create a new instance of the Settings Dialog
	public static ImageSearchSettingsDialogFragment newInstance(String title) {
		
		final ImageSearchSettingsDialogFragment settingsDialog = new ImageSearchSettingsDialogFragment();
		settingsDialog.setStyle(STYLE_NO_FRAME|STYLE_NORMAL, android.R.style.Theme_Translucent);
        final Bundle args = new Bundle();
        args.putString("title", title);
        settingsDialog.setArguments(args);
        return settingsDialog;
    }
	
	// Create the custom layout in the Settings Dialog view
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
        final View view = inflater.inflate(R.layout.fragment_image_search_settings, container);
        final String title = getArguments().getString("title", SearchActivity.SEARCH_SETTINGS_DIALOG_TITLE);
        getDialog().setTitle(title);
        
        // Apply custom font to dialog title
        // XXX Would love to set the font on spinners too, but that's more complicated
        tvImageSearchSettingsDialogTitle = (TextView) view.findViewById(R.id.tvImageSearchSettingsDialogTitle);
        tvImageSearchSettingsDialogTitle.setTypeface(font);
        
        populateSearchFilters(view);
        
        spImageSize = (Spinner) view.findViewById(R.id.spImageSize);
        spImageSize.setAdapter(imageSizeAdapter);
        
        spImageColor = (Spinner) view.findViewById(R.id.spImageColor);
        spImageColor.setAdapter(imageColorAdapter);
        
        spImageType = (Spinner) view.findViewById(R.id.spImageType);
        spImageType.setAdapter(imageTypeAdapter);
        
        spImageFromSite = (Spinner) view.findViewById(R.id.spImageFromSite);
        spImageFromSite.setAdapter(imageFromSiteAdapter);
        
        btnSettingsDone = (Button) view.findViewById(R.id.btnSettingsDone);
        btnSettingsDone.setOnClickListener(this);
        btnSettingsDone.setTypeface(font);
        
        return view;
    }

	// Attach the dialog to the main activity -- SearchActivity
	@Override
	public void onAttach(Activity activity) {
		
		super.onAttach(activity);
		
		try {
			listener = (ImageSearchSettingsDialogListener) getActivity();
		} catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ImageSearchSettingsDialogListener");
        }
	}

	// Get the selected items from the Spinners and dismiss the dialog
	@Override
	public void onClick(View v) {
		
		try {
			final ImageSearchFilters.ImageSize imageSize = 
					ImageSearchFilters.ImageSize.getByOptionName(spImageSize.getSelectedItem().toString());
			final ImageSearchFilters.ImageColor imageColor = 
					ImageSearchFilters.ImageColor.getByOptionName(spImageColor.getSelectedItem().toString());
			final ImageSearchFilters.ImageType imageType = 
					ImageSearchFilters.ImageType.getByOptionName(spImageType.getSelectedItem().toString());
			final ImageSearchFilters.ImageFromSite imageFromSite = 
					ImageSearchFilters.ImageFromSite.getByOptionName(spImageFromSite.getSelectedItem().toString());
			
			searchFilters = 
					new ImageSearchFilters(imageSize, imageColor, imageType, imageFromSite);
			
		} catch (Exception e) {
			// Do nothing if a setting can't be mapped to its enum
		}
		
		if (searchFilters == null) {
			searchFilters = new ImageSearchFilters();
		}
		listener.onFinishSettingsDialog(searchFilters);
		
		try {
			dismiss();
		} catch (Exception e) {
			// Swallow the exception and do nothing!
		}
	}
	
	// If preset filters exist, show the preset filter in each category as the first entry
	// Then add all the other entries to the adapter
	private void populateSearchFilters(View view) {
		
		final List<String> imageSizes = new ArrayList<String>();
		final List<String> imageColors = new ArrayList<String>();
		final List<String> imageTypes = new ArrayList<String>();
		final List<String> imageFromSites = new ArrayList<String>();
		
		final ImageSearchFilters presentImageSearchFilters = 
				ImageSearchFilters.readSearchFiltersFromFile(view.getContext());
		
		if (presentImageSearchFilters != null) {
			
			imageSizes.add(presentImageSearchFilters.getImageSize().getOptionName());
			imageColors.add(presentImageSearchFilters.getImageColor().getOptionName());
			imageTypes.add(presentImageSearchFilters.getImageType().getOptionName());
			imageFromSites.add(presentImageSearchFilters.getImageFromSite().getOptionName());
		}
		
		imageSizeAdapter = new ArrayAdapter<String>(view.getContext(), R.layout.spinner_search_filter, 
				ImageSearchFilters.ImageSize.getListOfValues(imageSizes));
		imageColorAdapter = new ArrayAdapter<String>(view.getContext(), R.layout.spinner_search_filter, 
				ImageSearchFilters.ImageColor.getListOfValues(imageColors));
		imageTypeAdapter = new ArrayAdapter<String>(view.getContext(), R.layout.spinner_search_filter, 
				ImageSearchFilters.ImageType.getListOfValues(imageTypes));
		imageFromSiteAdapter = new ArrayAdapter<String>(view.getContext(), R.layout.spinner_search_filter, 
				ImageSearchFilters.ImageFromSite.getListOfValues(imageFromSites));
	}
	
	// This will be called from the parent activity so that 
	// the dialog can use the same font as its parent
	public void setFont(Typeface font) {
		this.font = font;
	}
}
