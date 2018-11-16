package com.example.android.newsappstage2;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving newsItem data from GUARDIAN API.
 */
public final class QueryUtils {

    // Tag for the log message
    private static final String LOG_TAG = QueryUtils.class.getName();

    /* Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils(){

    }

    /**
     *  Query the GUARDIAN API dataset and return a list of {@Link NewsItem} objects.
     */
    public static List<NewsItem> getNewsData(String requestUrl){

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform the network request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the Http request");
        }

        // Extract relevant fields from the JSON response and create a list of (@Link NewsItem} object
        List<NewsItem> newsItems = extractFieldsFromJson(jsonResponse);

        // Return the {@link NewsItem}
        return newsItems;
    }

    /**
     *Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl){
        URL newUrl = null;
        try {
            newUrl = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL", e);
        }
        return newUrl;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null then return early.
        if (url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds*/);
            urlConnection.setConnectTimeout(15000 /* milliseconds*/);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error Response Code : " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the newsItem JSON results", e);
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (inputStream != null){
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();

        if (inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link NewsItem} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<NewsItem> extractFieldsFromJson(String newsItemJSON){

        // If the JSON string is empty or null then return early.
        if (TextUtils.isEmpty(newsItemJSON)){
            return null;
        }

        // Create an empty ArrayList that we can start adding newItems to
        ArrayList<NewsItem> newsItems = new ArrayList<>();

        // Try to parse the JSON_RESPONSE string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSON object from the JSON response string
            JSONObject rootObject = new JSONObject(newsItemJSON);

            // Extract the JSONObject associated with the key called "response",
            JSONObject responseObject = rootObject.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of results (or newsItems).
            JSONArray resultsArray = responseObject.getJSONArray("results");

            // For each newsItem in the resultsArray, create an {@link newsItem} object
            for (int i=0; i<resultsArray.length(); i++){

                // Get a single newsItem at position i within the list of newsItems
                JSONObject currentNewsItem = resultsArray.getJSONObject(i);

                // Extract the value for the key called "sectionName"
                String sectionName = currentNewsItem.getString("sectionName");

                // Extract the value for the key called "webPublicationDate"
                String date = currentNewsItem.getString("webPublicationDate");

                // Extract the value for the key called "webTitle"
                String title = currentNewsItem.getString("webTitle");

                // Extract the value for the key called "webUrl"
                String url = currentNewsItem.getString("webUrl");

                // Extract the JSONObject associated with the key called "fields",
                JSONObject fieldsObject = currentNewsItem.getJSONObject("fields");

                // Check whether the fieldObject has byline field, if it has then
                // extract the value for the key called "byline"
                String author = null;
                if (fieldsObject.has("byline")) {
                    author = fieldsObject.getString("byline");
                }

                // Check whether the fieldObject has thumbnail, if it has then
                // extract the value for the key called "thumbnail"
                String thumbnail = null;
                if (fieldsObject.has("thumbnail")) {
                    thumbnail = fieldsObject.getString("thumbnail");
                }

                // Create a new {@link NewsItem} object with the sectionName, date, title, author, thumbnail
                // and url from the JSON response.
                NewsItem newsItem = new NewsItem(sectionName, title, author, date, url, thumbnail);

                // Add the new {@link NewsItem} to the list of newsItems.
                newsItems.add(newsItem);
            }
        } catch (JSONException e){
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the newsItem JSON results", e);
        }

        // Return the list of newsItems
        return newsItems;
    }
}
