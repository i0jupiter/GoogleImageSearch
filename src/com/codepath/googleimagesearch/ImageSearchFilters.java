package com.codepath.googleimagesearch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import android.content.Context;
import android.net.Uri;

/**
 * Enum that maps the search filters in the different views in Settings Dialog to
 * their text in the view and Google Image Search URL options.
 * 
 * Also persists filters to a file and reads from it.
 * 
 * @author shine
 *
 */
public class ImageSearchFilters {
	
	private static final String SEARCH_FILTER_FILE = "GoogleImageSearchFilters.txt";
	
	public enum ImageSize {
		ALL ("All sizes", ""),
		ICON ("Icon", "icon"),
		SMALL ("Small", "small"),
		MEDIUM ("Medium", "medium"),
		LARGE ("Large", "large"),
		EXTRA_LARGE ("Extra large", "xlarge");
		
		private String optionName;
		private String urlArgumentName;
		
		ImageSize(String optionName, String urlArgumentName) {
			this.optionName = optionName;
			this.urlArgumentName = urlArgumentName;
		}

		public String getOptionName() {
			return optionName;
		}
		
		public String getUrlArgumentName() {
			return urlArgumentName;
		}
		
		public static ImageSize getByOptionName(String optionName){
		    for (ImageSize is : ImageSize.values()) {
		    	if (is.optionName.equalsIgnoreCase(optionName)) {
		    		return is;
		    	}
		    }
		    throw new IllegalArgumentException("Enum " + optionName + " not defined!");
	    }
		
		//XXX This should be a generic method that can take any type of Enum and return a list
		// Buy my Java Generic concepts are not robust right now =)
		public static List<String> getListOfValues(List<String> existingList) {
			
	        for (ImageSearchFilters.ImageSize size : ImageSearchFilters.ImageSize.values()) {
	        	if (!existingList.contains(size.getOptionName())) {
	        		existingList.add(size.getOptionName());
	        	}
	        }
	        return existingList;
		}
	};

	public enum ImageColor {
		ALL ("All colors", ""),
		BLACK ("Black", "black"),
		BLUE ("Blue", "blue"),
		BROWN ("Brown", "brown"),
		GRAY ("Gray", "gray"),
		GREEN ("Green", "green"),
		ORANGE ("Orange", "orange"),
		PINK ("Pink", "pink"),
		PURPLE ("Purple", "purple"),
		RED ("Red", "red"),
		TEAL ("Teal", "teal"),
		WHITE ("White", "white"),
		YELLOW ("Yellow", "yellow");
		
		private String optionName;
		private String urlArgumentName;
		
		ImageColor(String optionName, String urlArgumentName) {
			this.optionName = optionName;
			this.urlArgumentName = urlArgumentName;
		}
		
		public String getOptionName() {
			return optionName;
		}
		
		public String getUrlArgumentName() {
			return urlArgumentName;
		}
		
		public static ImageColor getByOptionName(String optionName){
		    for (ImageColor ic : ImageColor.values()) {
		    	if (ic.optionName.equalsIgnoreCase(optionName)) {
		    		return ic;
		    	}
		    }
		    throw new IllegalArgumentException("Enum " + optionName + " not defined!");
	    }
		
		public static List<String> getListOfValues(List<String> existingList) {
			
	        for (ImageSearchFilters.ImageColor color : ImageSearchFilters.ImageColor.values()) {
	        	if (!existingList.contains(color.getOptionName())) {
	        		existingList.add(color.getOptionName());
	        	}
	        }
	        return existingList;
		}
	};
	
	public enum ImageType {
		ALL ("All types", ""),
		FACE ("Faces", "face"),
		PHOTO ("Photo", "photo"),
		CLIP_ART ("Clip art", "clipart"),
		LINE_ART ("Line art", "lineart");
		
		private String optionName;
		private String urlArgumentName;
		
		ImageType(String optionName, String urlArgumentName) {
			this.optionName = optionName;
			this.urlArgumentName = urlArgumentName;
		}
		
		public String getOptionName() {
			return optionName;
		}
		
		public String getUrlArgumentName() {
			return urlArgumentName;
		}

		public static ImageType getByOptionName(String optionName){
		    for (ImageType it : ImageType.values()) {
		    	if (it.optionName.equalsIgnoreCase(optionName)) {
		    		return it;
		    	}
		    }
		    throw new IllegalArgumentException("Enum " + optionName + " not defined!");
	    }
		
		public static List<String> getListOfValues(List<String> existingList) {
			
	        for (ImageSearchFilters.ImageType type : ImageSearchFilters.ImageType.values()) {
	        	if (!existingList.contains(type.getOptionName())) {
	        		existingList.add(type.getOptionName());
	        	}
	        }
	        return existingList;
		}
	};
	
	public enum ImageFromSite {
		ALL ("All sites"),
		ESPN_COM ("espn.com"),
		PHOTOBUCKET_COM ("photobucket.com"),
		YAHOO_COM ("yahoo.com");
		
		private String optionName;
		
		ImageFromSite(String optionName) {
			this.optionName = optionName;
		}
		
		public String getOptionName() {
			return optionName;
		}
		
		public static ImageFromSite getByOptionName(String optionName){
		    for (ImageFromSite ifs : ImageFromSite.values()) {
		    	if (ifs.optionName.equalsIgnoreCase(optionName)) {
		    		return ifs;
		    	}
		    }
		    throw new IllegalArgumentException("Enum " + optionName + " not defined!");
	    }
		
		public static List<String> getListOfValues(List<String> existingList) {
			
	        for (ImageSearchFilters.ImageFromSite site : ImageSearchFilters.ImageFromSite.values()) {
	        	if (!existingList.contains(site.getOptionName())) {
	        		existingList.add(site.getOptionName());
	        	}
	        }
	        return existingList;
		}
	};
	
	private ImageSize imageSize;
	private ImageColor imageColor;
	private ImageType imageType;
	private ImageFromSite imageFromSite;
	
	public ImageSearchFilters() { }
	
	public ImageSearchFilters(ImageSize imageSize, 
			ImageColor imageColor, 
			ImageType imageType, 
			ImageFromSite imageFromSite) {
		
		this.imageSize = imageSize;
		this.imageColor = imageColor;
		this.imageType = imageType;
		this.imageFromSite = imageFromSite;
	}

	public ImageSize getImageSize() {
		return imageSize;
	}

	public ImageColor getImageColor() {
		return imageColor;
	}

	public ImageType getImageType() {
		return imageType;
	}

	public ImageFromSite getImageFromSite() {
		return imageFromSite;
	}

	public static String getUrlFromFilters(ImageSearchFilters selectedSearchFilters) {
		
		final StringBuilder sb = new StringBuilder();
		if (selectedSearchFilters.getImageSize() != ImageSize.ALL) {
			sb.append("&imgsz=").append(Uri.encode(selectedSearchFilters.getImageSize().getUrlArgumentName()));
		}
		if (selectedSearchFilters.getImageColor() != ImageColor.ALL) {
			sb.append("&imgcolor=").append(Uri.encode(selectedSearchFilters.getImageColor().getUrlArgumentName()));
		}
		if (selectedSearchFilters.getImageType() != ImageType.ALL) {
			sb.append("&imgtype=").append(Uri.encode(selectedSearchFilters.getImageType().getUrlArgumentName()));
		}
		if (selectedSearchFilters.getImageFromSite() != ImageFromSite.ALL) {
			sb.append("&as_sitesearch=").append(Uri.encode(selectedSearchFilters.getImageFromSite().getOptionName()));
		}
		
		return sb.toString();
	}
	
	// Persist the selected search filters to a file
	public static void writeSearchFiltersToFile(Context context,
			ImageSearchFilters selectedSearchFilters) {
		
		final File searchFilterFile = new File(context.getFilesDir(), SEARCH_FILTER_FILE);
		final List<String> searchFilters = new ArrayList<String>();
		searchFilters.add(selectedSearchFilters.getImageSize().getOptionName());
		searchFilters.add(selectedSearchFilters.getImageColor().getOptionName());
		searchFilters.add(selectedSearchFilters.getImageType().getOptionName());
		searchFilters.add(selectedSearchFilters.getImageFromSite().getOptionName());
		
		try {
			FileUtils.writeLines(searchFilterFile, searchFilters);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	// Read the present search filters from a file
	public static ImageSearchFilters readSearchFiltersFromFile(Context context) {
		
		final File searchFiltersFile = new File(context.getFilesDir(), SEARCH_FILTER_FILE);
		if (!searchFiltersFile.exists()) {
			return null;
		}
		
		try {
			final List<String> filters = FileUtils.readLines(searchFiltersFile);
			if (filters.size() == 4) {
				return new ImageSearchFilters(ImageSize.getByOptionName(filters.get(0)), 
						ImageColor.getByOptionName(filters.get(1)), 
						ImageType.getByOptionName(filters.get(2)),
						ImageFromSite.getByOptionName(filters.get(3)));
			}
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return "ImageSearchFilters [imageSize=" + imageSize + ", imageColor="
				+ imageColor + ", imageType=" + imageType + ", imageFromSite="
				+ imageFromSite + "]";
	}
}
