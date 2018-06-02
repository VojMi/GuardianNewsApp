package com.example.vojmi.guardiannewsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class LoaderNews extends AsyncTaskLoader<List<News>> {

    private final String mUrl;

    /**
     * Construction a new NewsLoader.
     *
     * @param context is the current activity to which loader belongs to
     * @param url     url resource to load data from
     */
    public LoaderNews(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * The loading is being performed background.
     */
    @Override
    public List<News> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Performs the request, parses the data and extracts the list of news.
        return Query.fetchNewsData(mUrl);
    }
}
