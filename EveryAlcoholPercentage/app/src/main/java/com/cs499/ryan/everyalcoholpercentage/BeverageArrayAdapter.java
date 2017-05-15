package com.cs499.ryan.everyalcoholpercentage;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ryan on 5/14/17.
 */

public class BeverageArrayAdapter extends ArrayAdapter<Beverage> {

    private Context context;
    private int layoutResource;
    private List<Beverage> beverages;

    public BeverageArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Beverage> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutResource = resource;
        this.beverages = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layoutResource, parent, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.heartImageButton);
        if(beverages.get(position).isFavorite()) {
            imageView.setImageResource(R.drawable.heartwithfill);
        } else {
            imageView.setImageResource(R.drawable.heartnofill);
        }

        TextView textViewName = (TextView) view.findViewById(R.id.nameTextView);
        textViewName.setText(beverages.get(position).getName());

        TextView textViewMakerName = (TextView) view.findViewById(R.id.makerNameTextView);
        textViewMakerName.setText(beverages.get(position).getMakerName());

        TextView textViewPercent = (TextView) view.findViewById(R.id.percentTextView);
        textViewPercent.setText(Double.toString(beverages.get(position).getAlcoholPercentage()));

        return view;
    }
}
