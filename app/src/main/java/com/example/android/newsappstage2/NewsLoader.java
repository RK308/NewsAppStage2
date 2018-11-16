package com.example.android.newsappstage2;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<NewsItem>> {

    // Tag for log message
    private static final String LOG_TAG = NewsLoader.class.getName();

    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link NewsLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.e(LOG_TAG, "Loading has started");
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<NewsItem> loadInBackground() {
        Log.e(LOG_TAG, "Loading is finished");

        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of newsItems.
        List<NewsItem> newsItems = QueryUtils.getNewsData(mUrl);
        return newsItems;
    }
}
