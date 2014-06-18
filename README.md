Update: Something went wrong with Gitbug repo. Trying to fix it.

This is an Android application that does image search on Google. 

Time spent: 25 hours

Completed User Stories:

Required:

 - User can enter a search query that will display a grid of image results from the Google Image API.
 - User can click on "settings" which allows selection of advanced search options to filter results
 - User can configure advanced search filters such as:
   - Size (small, medium, large, extra-large)
   - Color filter (black, blue, brown, gray, green, etc...)
   - Type (faces, photo, clip art, line art)
   - Site (espn.com)
 - Subsequent searches will have any filters applied to the search results
 - User can tap on any image in results to see the image full-screen
 - User can scroll down “infinitely” to continue loading more image results (up to 8 pages)

Advanced: Basically everything but sharing of images.

 - Robust error handling, check if internet is available, handle error cases, network failures
 - Use the ActionBar SearchView or custom layout as the query box instead of an EditText
 - Replace Filter Settings Activity with a lightweight modal overlay
 - Improve the user interface and experiment with image assets and/or styling and coloring
