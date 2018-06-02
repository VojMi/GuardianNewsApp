package com.example.vojmi.guardiannewsapp;

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

final class Query {

    private static final String LOG_TAG = Query.class.getSimpleName();

    /**
     * Queries the server and returns the List<News> object to represent a single news.
     */
    public static ArrayList<News> fetchNewsData(String requestUrl) {

        // Declaration of URL object.
        URL url = createUrl(requestUrl);

        // HTTP request to get the URL and receive a JSON response.
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extracts relevant fields from JSON response and creates the News ArrayList
        return extractFeatureFromJson(jsonResponse);
    }

    /**
     * Returns a new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        // SDeclaration of specific connection statuses timeouts.
        final int READ_TIMEOUT = 10000;
        final int CONNECT_TIMEOUT = 15000;
        final int RESPONSE_CODE_200 = 200;
        String jsonResponse = "";

        // In case of null url, don't wait.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT /** milliseconds */);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT /** milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // In case of successful request (response code 200) then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == RESPONSE_CODE_200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Construction of the string containing the whole JSON response from the server via StringBuilder.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
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
     * Returns the List<News> object by parsing out information about the first news
     * from the input newsJSON string.
     */
    private static ArrayList<News> extractFeatureFromJson(String newsJSON) {
        //JSON Response keys
        final String JSON_RESPONSE = "response";
        final String JSON_WEB_URL = "webUrl";
        final String JSON_RESULTS = "results";
        final String JSON_WEB_TITLE = "webTitle";
        final String JSON_SECTION_NAME = "sectionName";
        final String JSON_WEB_PUBLICATION_DATE = "webPublicationDate";
        final String JSON_FIELDS = "fields";
        final String JSON_BY_LINE = "byline";
        // If the JSON string is empty or null, don't wait.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        // Creates the empty ArrayList to add the news to it.
        ArrayList<News> articles = new ArrayList<>();

        // Try to parse the JSON response. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and send to logs.
        try {
            JSONObject jsonObject = new JSONObject(newsJSON);
            JSONObject resultsJSONObject = jsonObject.getJSONObject(JSON_RESPONSE);
            JSONArray articlesArray = resultsJSONObject.getJSONArray(JSON_RESULTS);

            for (int i = 0; i < articlesArray.length(); i++) {
                JSONObject objectNewsResults = articlesArray.getJSONObject(i);
                String sectionName = objectNewsResults.getString(JSON_SECTION_NAME);
                String webTitle = objectNewsResults.getString(JSON_WEB_TITLE);
                String webPublicationDate = objectNewsResults.getString(JSON_WEB_PUBLICATION_DATE);
                String webUrl = objectNewsResults.getString(JSON_WEB_URL);
                //Tries to get the author if available.
                String author = null;
                JSONObject objectFields = objectNewsResults.getJSONObject(JSON_FIELDS);
                if (objectFields.has(JSON_BY_LINE)) {
                    author = objectFields.getString(JSON_BY_LINE);
                }

                //New news object, add it to the ArrayList
                News news = new News(sectionName, webTitle, webPublicationDate, webUrl, author);
                articles.add(news);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("Query", "Problem parsing the news JSON results", e);
        }
        // Return the list of articles.
        return articles;
    }
}
