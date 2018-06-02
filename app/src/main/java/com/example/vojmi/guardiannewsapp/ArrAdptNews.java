package com.example.vojmi.guardiannewsapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * {@link ArrAdptNews} is a custom {@link ArrayAdapter} providing the layout for list items based on resources from News.class.*
 */
class ArrAdptNews extends ArrayAdapter<News> {

    /**
     * Create a new News Array Adapter
     *
     * @param context represents the context of the app.
     * @param news    ArrayList
     */
    public ArrAdptNews(Activity context, ArrayList<News> news) {
        super(context, 0, news);
    }

    /**
     * Creating the object {@link ArrAdptNews} with following parameters:
     *
     * @param position    current position
     * @param convertView according to listItemView
     * @param parent      have not to be null.
     * @return inflates the listItemView
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        // Checks the existing view for being reused, if no inflate the view.
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        // Finds the news at the particular position.
        final News currentNews = getItem(position);
        // Sets and displays the section at a particular position.
        assert currentNews != null;
        String sectionName = currentNews.getSection();
        TextView sectionTv = listItemView.findViewById(R.id.textViewSection);
        sectionTv.setText(sectionName);
        // Sets and displays the news date at current position.
        String dateString = currentNews.getDate();
        // Split the string when the "T" chart is found.
        String findSeparatorOf = "T";
        String[] splitText = dateString.split(findSeparatorOf);
        String date = splitText[0];
        // Date conversion.
        Date convertedDate = new Date();
        Locale current = getContext().getResources().getConfiguration().locale;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", current);

        try {
            convertedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dateToDisplay = dateFormat.format(convertedDate);

        TextView textViewDate = listItemView.findViewById(R.id.textViewDate);
        textViewDate.setText(dateToDisplay);
        // Sets and displays the title of the news for the particular position
        String webTitle = currentNews.getTitle();
        TextView textViewTitle = listItemView.findViewById(R.id.textViewTitle);
        textViewTitle.setText(webTitle);
        // Sets and displays the author of the news for the particular position.
        TextView textViewAuthor = listItemView.findViewById(R.id.textViewAuthor);
        if (currentNews.getAuthor() != null) {
            textViewAuthor.setText(currentNews.getAuthor());
        } else {
            textViewAuthor.setVisibility(View.GONE);
        }
        // Returns the whole list item layout.
        return listItemView;
    }
}
