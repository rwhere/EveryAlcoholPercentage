package com.cs499.ryan.everyalcoholpercentage;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

/**
 * Created by Ryan on 5/14/17.
 */

public class BeverageArrayAdapter extends ArrayAdapter<Beverage> {

    private Context context;
    private int layoutResource;
    private List<Beverage> beverages;
    private List<Beverage> ogBeveragesList;

    public BeverageArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Beverage> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutResource = resource;
        this.beverages = objects;
        this.ogBeveragesList = objects;
    }

    @Override
    public int getCount() {
        return beverages.size();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layoutResource, parent, false);

        final ImageView imageView = (ImageView) view.findViewById(R.id.heartImageButton);

        //tag value 0 = favorited, tag value 1 = not favorited
        if(WelcomeActivity.favorites.contains(beverages.get(position).getId())) {
            imageView.setImageResource(R.drawable.heartwithfill);
            imageView.setTag(0);
        } else {
            imageView.setImageResource(R.drawable.heartnofill);
            imageView.setTag(1);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((Integer)imageView.getTag()).equals(new Integer(1))) {
                    imageView.setTag(0);
                    imageView.setImageResource(R.drawable.heartwithfill);
                    WelcomeActivity.favorites.add(beverages.get(position).getId());
                    WelcomeActivity.favoritesList.add(beverages.get(position));
                    notifyDataSetChanged();
                    Paper.book().write("favorites", WelcomeActivity.favorites);
                    Paper.book().write("favoritesList", WelcomeActivity.favoritesList);
                } else {
                    imageView.setTag(1);
                    imageView.setImageResource(R.drawable.heartnofill);
                    WelcomeActivity.favorites.remove(beverages.get(position).getId());
                    WelcomeActivity.favoritesList.remove(beverages.get(position));
                    notifyDataSetChanged();
                    Paper.book().write("favorites", WelcomeActivity.favorites);
                    Paper.book().write("favoritesList", WelcomeActivity.favoritesList);
                }
            }
        });

        TextView textViewName = (TextView) view.findViewById(R.id.nameTextView);
        textViewName.setText(beverages.get(position).getName());

        TextView textViewMakerName = (TextView) view.findViewById(R.id.makerNameTextView);
        textViewMakerName.setText(beverages.get(position).getMakerName());

        TextView textViewPercent = (TextView) view.findViewById(R.id.percentTextView);
        textViewPercent.setText(beverages.get(position).getAlcoholPercentage());

        return view;
    }

    public void resetData() {
        beverages = ogBeveragesList;
    }

    public List<Beverage> getData() {
        return beverages;
    }

    public Beverage getItem(int position) {
        return beverages.get(position);
    }

    public long getItemId(int position) {
        return beverages.get(position).hashCode();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                // Now we have to inform the adapter about the new list filtered
                beverages = (List<Beverage>) results.values;
                notifyDataSetChanged();

            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                // We implement here the filter logic
                if (constraint == null || constraint.length() == 0) {
                    // No filter implemented we return all the list
                    results.values = ogBeveragesList;
                    results.count = ogBeveragesList.size();
                }
                else {
                    // We perform filtering operation
                    List<Beverage> filteredResults = getFilteredResults(constraint);

                    results.values = filteredResults;
                    results.count = filteredResults.size();

                }
                return results;

            }
        };
    }

    public List<Beverage> getFilteredResults(CharSequence constraint) {
        List<Beverage> results = new ArrayList<>();
        Beverage temp;
        int counter = 0;
        for(int i = 0; i < this.getCount(); i++) {
            temp = getItem(i);
            if(temp.getName().toLowerCase().contains(constraint.toString().toLowerCase())
                    || temp.getMakerName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                results.add(temp);
                ++counter;
            }
        }
        return results;
    }

    public void updateData(List<Beverage> list) {
        beverages = list;
    }
}
