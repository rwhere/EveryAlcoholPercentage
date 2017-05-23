package com.cs499.ryan.everyalcoholpercentage;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.widget.PopupMenu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;

public class WelcomeActivity extends AppCompatActivity {

    private List<Beverage> beerList;
    private List<Beverage> wineList;
    private List<Beverage> liquorList;
    public static List<Beverage> favoritesList;
    private ListView listView;
    private BeverageArrayAdapter beverageArrayAdapter;
    public static HashSet<Long> favorites;
    private Integer beerVersion;
    private Integer wineVersion;
    private Integer liquorVersion;
    private ImageButton favButton;
    private ImageButton beerButton;
    private ImageButton liquorButton;
    private ImageButton wineButton;
    private VIEW currentView = VIEW.BEER;

    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Paper.init(WelcomeActivity.this);
        database = FirebaseDatabase.getInstance();

        listView = (ListView) findViewById(R.id.mainListView);
        favButton = (ImageButton)findViewById(R.id.favButton);
        beerButton = (ImageButton)findViewById(R.id.beerButton);
        liquorButton = (ImageButton)findViewById(R.id.liquorButton);
        wineButton = (ImageButton)findViewById(R.id.wineButton);

        //grab user's favorites
        favorites = Paper.book().read("favorites");
        if(favorites == null) {
            favorites = new HashSet<Long>();
        }
        favoritesList = Paper.book().read("favoritesList");
        if(favoritesList == null) {
            favoritesList = new ArrayList<Beverage>();
        }

        //grab version numbers
        beerVersion = Paper.book().read("beerVersion");
        wineVersion = Paper.book().read("wineVersion");
        liquorVersion = Paper.book().read("liquorVersion");

        if(beerVersion == null) {
            beerVersion = new Integer(0);
            Paper.book().write("beerVersion", beerVersion);
        }
        if(wineVersion == null) {
            wineVersion = new Integer(0);
            Paper.book().write("wineVersion", wineVersion);
        }
        if(liquorVersion == null) {
            liquorVersion = new Integer(0);
            Paper.book().write("liquorVersion", liquorVersion);
        }
        //display local data
        beerList = Paper.book().read("beerList");
        if(beerList == null) {
            beerList = new ArrayList<Beverage>();
            if(!isNetworkAvailable()) {
                Toast.makeText(this, "No internet connectivity", Toast.LENGTH_SHORT).show();
            }
        }
        beverageArrayAdapter = new BeverageArrayAdapter(
                WelcomeActivity.this, R.layout.listview_item, beerList);
        listView.setAdapter(beverageArrayAdapter);

        // 1.) check if beer version is old and pull from db if so
        myRef = database.getReference("beverages/beerVersion");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final int dbVersion = ((Long)dataSnapshot.getValue()).intValue();
                if(beerVersion.compareTo(dbVersion) != 0) {
                    Log.d("firebase", "pulling from db");
                    DatabaseReference beerRef = database.getReference("beverages/beers");

                    beerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            beerList = new ArrayList<>();
                            for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                Beverage b = ds.getValue(Beverage.class);
                                beerList.add(b);
                            }
                            beverageArrayAdapter.clear();
                            beverageArrayAdapter.addAll(beerList);
                            Paper.book().write("beerList", beerList);
                            Paper.book().write("beerVersion", beerVersion = dbVersion);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        beerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentView != VIEW.BEER) {
                    beverageArrayAdapter.clear();
                    beverageArrayAdapter.addAll(beerList);
                    currentView = VIEW.BEER;
                }
            }
        });

        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentView != VIEW.FAVORITES) {
                    beverageArrayAdapter.clear();
                    beverageArrayAdapter.addAll(favoritesList);
                    currentView = VIEW.FAVORITES;
                }
            }
        });

        wineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentView != VIEW.WINE) {
                    beverageArrayAdapter.clear();
                    beverageArrayAdapter.addAll(wineList);
                    currentView = VIEW.WINE;
                }
            }
        });

        liquorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentView != VIEW.LIQUOR) {
                    beverageArrayAdapter.clear();
                    beverageArrayAdapter.addAll(liquorList);
                    currentView = VIEW.LIQUOR;
                }
            }
        });

        final Button sortButton = (Button)findViewById(R.id.sortButton);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(WelcomeActivity.this, sortButton);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(WelcomeActivity.this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public enum VIEW {
        BEER, FAVORITES, LIQUOR, WINE
    }
}
