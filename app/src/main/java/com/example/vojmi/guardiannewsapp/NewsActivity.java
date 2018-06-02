package com.example.vojmi.guardiannewsapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderCallbacks<List<News>> {
    // List of static variables
    static final int NEWS_LOADER_ID = 1;
    static final String API_REQUEST_URL = "https://content.guardianapis.com/search";
    static final String SHOW_FIELDS = "show-fields";
    static final String BY_LINE_THUMBNAIL = "byline,thumbnail";
    static final String Q = "q";
    static final String FORMAT = "format";
    static final String JSON = "json";
    static final String PAGE_SIZE = "page-size";
    static final String NUMBER = "10";
    static final String KEY = "api-key";
    static final String API = "ee58275b-92b5-4c68-9256-c6253b16e662";
    static final String ORDER_BY = "order-by";

    // Declaration of the Array adapter.
    private ArrAdptNews adapter;
    // Declaration of the current network status.
    private NetworkInfo networkInfo;

    /**
     * creates the Loader for the list of news
     *
     * @param i      defines the parameter to receive the ID.
     * @param bundle defines the parameter to receive the Url for data fetching.
     * @return a new loader
     */
    @SuppressWarnings("SpellCheckingInspection")
    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String keyword = sharedPrefs.getString(
                getString(R.string.keyword),
                getString(R.string.keyword_default));
        String orderBy = sharedPrefs.getString(
                getString(R.string.order_by),
                getString(R.string.order_by_default));

        // Definition of the new object to be filled with requested data from The Guardian. 
        Uri baseUri = Uri.parse(API_REQUEST_URL);

        // buildUpon prepares the baseUri which was just parsed to get the parameters added. 
        Uri.Builder uriBuilder = baseUri.buildUpon();
        // Particural parameters. 
        uriBuilder.appendQueryParameter(SHOW_FIELDS, BY_LINE_THUMBNAIL);
        uriBuilder.appendQueryParameter(Q, keyword);
        uriBuilder.appendQueryParameter(FORMAT, JSON);
        uriBuilder.appendQueryParameter(PAGE_SIZE, NUMBER);
        uriBuilder.appendQueryParameter(KEY, API);
        uriBuilder.appendQueryParameter(ORDER_BY, orderBy);

        // The actual build of URI. 
        return new LoaderNews(this, uriBuilder.build().toString());

    }

    /**
     * loads the Loader for the list of news
     *
     * @param loader defines the current loader.
     * @param news   defines the News list.
     */
    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        // Clears the adapter from previously used data.
        adapter.clear();
        // Add the news to the adapter, the ListView updates.
        if (news != null && !news.isEmpty()) {
            adapter.addAll(news);
        }
        // Prepare emptyView in case of no internet connection.
        TextView emptyView = findViewById(R.id.emptyView);
        emptyView.setText(R.string.empty_view_message);
        // Hides the progress bar.
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * in case of interruption, this reloads the Loader for the list of news
     *
     * @param loader defines the current loader
     */
    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, clearing existing data.
        adapter.clear();
        Log.d("Reset loader", "Reset loader");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);
        // Gets the Internet connection status.
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            assert connectivityManager != null;
            networkInfo = connectivityManager.getActiveNetworkInfo();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        // Checks if Internet connection works, if not, displays the particular message and hides the progressbar.
        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            TextView emptyView = findViewById(R.id.emptyView);
            emptyView.setText(R.string.no_internet_connection);
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            // Adapter initialisation.
            adapter = new ArrAdptNews(this, new ArrayList<News>());

            // Set the adapter on the List View.
            ListView newsListView = findViewById(R.id.list);
            newsListView.setAdapter(adapter);
            // Retrieves the empty View.
            newsListView.setEmptyView(findViewById(R.id.emptyView));
            // On click listener for items in adapter to open appropriate news online.
            newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Gets the Url for current news.
                    News currentNews = adapter.getItem(position);
                    String url = null;
                    try {
                        assert currentNews != null;
                        url = currentNews.getUrl();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    // New intent to open the concrete news online.
                    Intent detailsNews = new Intent();
                    detailsNews.setAction(Intent.ACTION_VIEW);
                    detailsNews.setData(Uri.parse(url));
                    // Verify that the intent works.
                    if (detailsNews.resolveActivity(getPackageManager()) != null) {
                        startActivity(detailsNews);
                    }
                }
            });

            // Preparation and inflation of the FooterView to display the list of news.
            ViewGroup footerView = (ViewGroup) getLayoutInflater().inflate(R.layout.list_item,
                    newsListView, false);
            newsListView.addFooterView(footerView);


            // The loader manager.
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
            Log.d("InitLoader", "init" + loaderManager);

        }
    }

    @Override
    // This method initialize the contents of the Activity's options main.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // This method is called every time an item in the options menu is selected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

