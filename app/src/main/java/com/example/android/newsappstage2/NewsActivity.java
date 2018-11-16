package com.example.android.newsappstage2;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsActivity extends AppCompatActivity
       implements LoaderCallbacks<List<NewsItem>> {

    private static final String LOG_TAG = NewsActivity.class.getName();

    /*
     ** URL for the news data from the guardian API
     */
    private final String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search";

    // Loader ID
    private static final int NEWS_LOADER_ID = 1;

    // Adapter for the list of news
    private NewsAdapter mAdapter;

    // TextView that is displayed when the list is empty
    private TextView mEmptyStateTextView;

    // A reference to the progressbar in the layout using @BindView
    @BindView(R.id.loading_spinner)
    ProgressBar loadingSpinner;

    // A reference to the {@Link ListView) in the layout using @BindView
    @BindView(R.id.list)
    ListView newsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);
        // bind view using butter knife
        ButterKnife.bind(this);

        // Create a new adapter that takes an empty list of NewsItem as input
        mAdapter = new NewsAdapter(this, new ArrayList<NewsItem>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected news
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Find the current newsItem that was clicked on
                NewsItem currentNewsItem = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsItemUri = Uri.parse(currentNewsItem.getUrl());

                // Create a new intent to view the newsItem URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsItemUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        mEmptyStateTextView = (TextView)findViewById(R.id.empty_list);
        newsListView.setEmptyView(mEmptyStateTextView);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager conn_manager =(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo activeNetwork = conn_manager.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            LoaderManager loaderManager = getLoaderManager();

            loaderManager.initLoader(NEWS_LOADER_ID, null,this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            loadingSpinner.setVisibility(View.GONE);

            // Update empty state textview with no internet connection error message
            mEmptyStateTextView.setText(R.string.no_internet);
        }
    }

    @Override
    public Loader<List<NewsItem>> onCreateLoader(int id, Bundle bundle) {
        Log.e(LOG_TAG, "Loader is created");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences.
        // The second parameter is the default value for this preference.
        String userChoice = sharedPrefs.getString(
                getString(R.string.category_key),
                getString(R.string.category_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.order_by_key),
                getString(R.string.order_by_default));

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value.
        // For example, "page-size=50"
        uriBuilder.appendQueryParameter("page-size","50");
        uriBuilder.appendQueryParameter("show-fields","thumbnail,byline");
        uriBuilder.appendQueryParameter("section",userChoice);
        uriBuilder.appendQueryParameter("order-by",orderBy);
        uriBuilder.appendQueryParameter("api-key","5ccf4c32-aff6-433d-9091-626fd9cd813e");

        // Return the completed uri
        // https://content.guardianapis.com/search?page-size=50&show-fields=thumbnail,byline&section=politics&api-key=test&order-by=newest
        return new NewsLoader(this,uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<NewsItem>> loader, List<NewsItem> newsItems) {

        Log.e(LOG_TAG, "Loading is finished");

        // Hide loading indicator because the data has been loaded
        loadingSpinner.setVisibility(View.GONE);

        // Set empty state textView to display "No news!"
        mEmptyStateTextView.setText(R.string.no_news);

        // Clear the adapter of previous newsItem data
        mAdapter.clear();

        // If there is a valid list of {@link newsItem}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (newsItems != null && !newsItems.isEmpty()) {
            mAdapter.addAll(newsItems);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsItem>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    // This method initialize the contents of the Activity's options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    // This method is called whenever an item in the options menu is selected.
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
