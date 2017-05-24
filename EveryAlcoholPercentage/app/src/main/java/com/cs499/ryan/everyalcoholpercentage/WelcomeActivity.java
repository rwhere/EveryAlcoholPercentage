package com.cs499.ryan.everyalcoholpercentage;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.HashSet;

public class WelcomeActivity extends AppCompatActivity {

    private List<Beverage> beerList;
    private List<Beverage> wineList;
    private List<Beverage> liquorList;
    private List<Beverage> beverageHolder;
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
        //display local data, start with beer
        beerList = Paper.book().read("beerList");
        if(beerList == null) {
            beerList = new ArrayList<Beverage>();
            if(!isNetworkAvailable()) {
                Toast.makeText(this, "No internet connectivity", Toast.LENGTH_SHORT).show();
            }
        }
        beverageHolder = new ArrayList<>(beerList);
        beverageArrayAdapter = new BeverageArrayAdapter(
                WelcomeActivity.this, R.layout.listview_item, beverageHolder);
        listView.setAdapter(beverageArrayAdapter);

        final AsyncTask<Void, Void, Void> pullFromDataBaseIfNeeded = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
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
                                    beerList.clear();
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

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        };

        pullFromDataBaseIfNeeded.execute();

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
                        if(item.getItemId() == R.id.sortNameAscending) {
                            Collections.sort(beverageHolder, new Comparator<Beverage>() {
                                @Override
                                public int compare(Beverage lhs, Beverage rhs) {
                                    return lhs.getName().compareTo(rhs.getName());
                                }
                            });
                        } else if (item.getItemId() == R.id.sortNameDescending) {
                            Collections.sort(beverageHolder, new Comparator<Beverage>() {
                                @Override
                                public int compare(Beverage lhs, Beverage rhs) {
                                    return -1 * lhs.getName().compareTo(rhs.getName());
                                }
                            });
                        } else if(item.getItemId() == R.id.sortAscending) {
                            Collections.sort(beverageHolder, new Comparator<Beverage>() {
                                @Override
                                public int compare(Beverage lhs, Beverage rhs) {
                                    double d = (Double.valueOf(lhs.getAlcoholPercentage()) -
                                            Double.valueOf(rhs.getAlcoholPercentage()));
                                    if(d > 0 ) return 1;
                                    if(d < 0) return -1;
                                    return 0;
                                }
                            });
                        } else if(item.getItemId() == R.id.sortDescending) {
                            Collections.sort(beverageHolder, new Comparator<Beverage>() {
                                @Override
                                public int compare(Beverage lhs, Beverage rhs) {
                                    double d = (Double.valueOf(rhs.getAlcoholPercentage()) -
                                            Double.valueOf(lhs.getAlcoholPercentage()));
                                    if(d > 0 ) return 1;
                                    if(d < 0) return -1;
                                    return 0;
                                }
                            });
                        }
                        beverageArrayAdapter.notifyDataSetChanged();
                        if(currentView == VIEW.BEER) {
                            beerList.clear();
                            beerList.addAll(beverageHolder);
                        } else if(currentView == VIEW.FAVORITES) {
                            favoritesList.clear();
                            favoritesList.addAll(beverageHolder);
                        } else if(currentView == VIEW.WINE) {
                            wineList.clear();
                            wineList.addAll(beverageHolder);
                        } else if(currentView == VIEW.LIQUOR) {
                            liquorList.clear();
                            liquorList.addAll(beverageHolder);
                        }
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
