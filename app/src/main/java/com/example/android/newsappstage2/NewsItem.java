package com.example.android.newsappstage2;

public class NewsItem {

    // News section name
    private String mSection;

    // Title of the article
    private String mTitle;

    // Author of the article
    private String mAuthor;

    // Date of the article published
    private String mDate;

    // URL of the article
    private String mUrl;

    private String mThumbnail;

    /*
     ** Create a new NewsItem object
     * @param section
     * @param title
     * @param author
     * @param date
     * @param url
     * @param thumbnail
     */
    public NewsItem(String section, String title, String author, String date, String url, String thumbnail){
        mSection = section;
        mTitle = title;
        mAuthor = author;
        mDate = date;
        mUrl = url;
        mThumbnail = thumbnail;
    }

    // Creates the method getSection that returns the string mSection
    public String getSection(){
        return mSection;
    }

    // Return the Title
    public String getTitle(){
        return mTitle;
    }

    // Return the Author
    public String getAuthor(){
        return mAuthor;
    }

    // Return the Date
    public String getDate(){
        return mDate;
    }

    // Return the URL
    public String getUrl(){
        return mUrl;
    }

    // Return the thumbnail
    public String getThumbnail(){
        return mThumbnail;
    }
}

