package com.cs499.ryan.everyalcoholpercentage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    private List<Beverage> beverageList;
    private ListView listView;
    private BeverageArrayAdapter beverageArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initBeverages();

        listView = (ListView) findViewById(R.id.mainListView);
        beverageArrayAdapter = new BeverageArrayAdapter(
                this, R.layout.listview_item, beverageList);

        listView.setAdapter(beverageArrayAdapter);
    }

    private void initBeverages() {
        beverageList = new ArrayList<Beverage>() {{
            add(new Beverage("IPA", "Lagunitas", 6.5, false));
            add(new Beverage("IPA", "Lagunitas", 6.5, true));
            add(new Beverage("IPA", "Lagunitas", 6.5, false));
            add(new Beverage("IPA", "Lagunitas", 6.5, false));
            add(new Beverage("IPA", "Lagunitas", 6.5, false));
            add(new Beverage("IPA", "Lagunitas", 6.5, false));
            add(new Beverage("IPA", "Lagunitas", 6.5, false));
            add(new Beverage("IPA", "Lagunitas", 6.5, false));
            add(new Beverage("IPA", "Lagunitas", 6.5, false));
        }};
    }
}
