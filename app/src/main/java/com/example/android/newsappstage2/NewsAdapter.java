package com.example.android.newsappstage2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A {@link NewsAdapter} knows how to create a list item layout for each news
 * in the data source (a list of {@link NewsItem} objects).
 */
public class NewsAdapter extends ArrayAdapter<NewsItem> {

    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the list is the data we want
     * to populate into the lists.
     *
     * @param context   The current context. Used to inflate the layout file.
     * @param newsItems A List of newsItem objects to display in a list
     */
    public NewsAdapter(Context context, ArrayList<NewsItem> newsItems) {
        super(context, 0, newsItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View ListItemView = convertView;
        if (ListItemView == null) {
            ListItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item, parent, false);
        }

        // Get the NewsItem object located at this position in the list
        NewsItem currentNewsItem = getItem(position);

        TextView sectionTextView = (TextView) ListItemView.findViewById(R.id.section_label);

        // Set the section name on the section textview
        sectionTextView.setText("Section : "+currentNewsItem.getSection());

        ImageView thumbnailImageView = (ImageView) ListItemView.findViewById(R.id.article_image);

        // To check whether the image for the current news item is not null
        // If not null will load the image for the current news item
        // else will set the default image.
        if (currentNewsItem.getThumbnail() != null) {
            Picasso.with(getContext())
                    .load(currentNewsItem.getThumbnail())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_newsimage)
                    .resize(300, 200)
                    .into(thumbnailImageView);
        } else {
            thumbnailImageView.setImageResource(R.drawable.ic_newsimage);
        }

        TextView titleTextView = (TextView) ListItemView.findViewById(R.id.article_title);

        // Set the title on the title textView
        titleTextView.setText(currentNewsItem.getTitle());

        TextView authorTextView = (TextView) ListItemView.findViewById(R.id.article_author);

        // To check whether the author for the current news item exists
        // If exist, set the article author on the author textView
        // else disable the textView visibility
        if (currentNewsItem.getAuthor() != null) {
            authorTextView.setText("Author : " + currentNewsItem.getAuthor());
        } else {
            authorTextView.setVisibility(View.GONE);
        }

        TextView dateTextView = (TextView) ListItemView.findViewById(R.id.article_date);

        // Get the date and time for the current newsItem and store it in a variable.
        String rawDate = currentNewsItem.getDate();
        String date = formattedDate(rawDate);
        dateTextView.setText("Published on : " + date);

        return ListItemView;
    }

    private static String formattedDate(String rawDate) {
        if (rawDate == null) {
            return null;
        }

        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(rawDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormat.format(date);
    }
}
