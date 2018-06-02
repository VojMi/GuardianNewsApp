package com.example.vojmi.guardiannewsapp;

/**
 * {@link News} contains the content of the article composed from available resources..
 * It is defined by particular news title, category, author and name
 */
class News {
    private final String section;
    private final String date;
    private final String url;
    private final String title;
    private final String author;

    /**
     * Creates a new News object
     *
     * @param section defines the section name of the news.
     * @param title   defines the title of the news.
     * @param date    defines the publication date of the news.
     * @param url     defines the url of the news.
     * @param author  defines the author of the news.
     */
    public News(String section, String title, String date, String url, String author) {
        this.section = section;
        this.title = title;
        this.date = date;
        this.url = url;
        this.author = author;
    }

    /**
     * @return the string for the name of the section.
     */
    public String getSection() {
        return section;
    }

    /**
     * @return the string of the title of the news.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the string of publication the date.
     */
    public String getDate() {
        return date;
    }

    /**
     * @return the string of the Url.
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return the string of the author.
     */
    public String getAuthor() {
        return author;
    }


}
