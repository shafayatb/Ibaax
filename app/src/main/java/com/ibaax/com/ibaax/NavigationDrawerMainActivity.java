package com.ibaax.com.ibaax;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;

import Entity.AppGlobal;

public class NavigationDrawerMainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener, View.OnClickListener {

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int HOME = 0;
    private static final int DIRECTORY = 1;
    private static final int RECENT = 2;
    private static final int SHORTLIST = 3;
    private static final int SAVESEARCH = 4;
    private static final int NOTIFICATIONS = 5;
    private static final int SELLRENT = 6;
    private static final int POSTPROPERTY = 7;
    private static final int MYPROPERTY = 8;
    private static final int MORE = 9;
    private static final int SETTINGS = 10;
    private static final int RATE = 11;
    private static final int SHARE = 12;
    private static final int FRAGMENT_COUNT = SHARE + 1;
    public static int LastSelectedTab = 0;
    Context context;
    TextView NavTitle;
    PlaceAutocompleteFragment autocompleteFragment;
    AutocompleteFilter typeFilter;
    LinearLayout lnrSearchBar;
    ImageButton PlaceSearch, PlaceCancel;
    String Lat, Lon;
    EditText SearchText;
    View GooglePlacesLayout;
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private HomeFragment homeFrag;
    private SearchDirectoryFragment directFrag;
    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppGlobal.countryList.size() > 0) {

        } else {
            AppGlobal.setCountry();
        }
        setContentView(R.layout.activity_navigation_drawer_main);
        getWindow().setBackgroundDrawable(null);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_drawer);
        context = this;
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);// set your own icon
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragments[HOME] = fragmentManager.findFragmentById(R.id.homeFragment);
        fragments[DIRECTORY] = fragmentManager.findFragmentById(R.id.directoryFragment);
        fragments[RECENT] = fragmentManager.findFragmentById(R.id.recentFragment);
        fragments[SHORTLIST] = fragmentManager.findFragmentById(R.id.shortlistFragment);
        fragments[SAVESEARCH] = fragmentManager.findFragmentById(R.id.savesearchFragment);
        fragments[NOTIFICATIONS] = fragmentManager.findFragmentById(R.id.notificationsFragment);
        fragments[SELLRENT] = fragmentManager.findFragmentById(R.id.postpropertyFragment);
        fragments[POSTPROPERTY] = fragmentManager.findFragmentById(R.id.postpropertyFragment);
        fragments[MYPROPERTY] = fragmentManager.findFragmentById(R.id.mypropertiesFragment);
        fragments[MORE] = fragmentManager.findFragmentById(R.id.settingsFragment);
        fragments[SETTINGS] = fragmentManager.findFragmentById(R.id.settingsFragment);
        fragments[RATE] = fragmentManager.findFragmentById(R.id.brokerageFragment);
        fragments[SHARE] = fragmentManager.findFragmentById(R.id.brokerageFragment);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        for (int i = 0; i < fragments.length; i++) {
            fragmentTransaction.hide(fragments[i]);
        }
        fragmentTransaction.commit();
        NavTitle = (TextView) findViewById(R.id.lbl_drawer_label);
        lnrSearchBar = (LinearLayout) findViewById(R.id.lnr_google_searchbar);
        GooglePlacesLayout = (View) findViewById(R.id.home_place_autocomplete);
        SearchText = (EditText) findViewById(R.id.txt_place_search);
        PlaceSearch = (ImageButton) findViewById(R.id.imgbtn_search_places);
        PlaceCancel = (ImageButton) findViewById(R.id.imgbtn_cancel_places);

        SearchText.setOnClickListener(this);
        PlaceSearch.setOnClickListener(this);
        PlaceCancel.setOnClickListener(this);

        if (autocompleteFragment == null) {
            autocompleteFragment = (PlaceAutocompleteFragment)
                    getFragmentManager().findFragmentById(R.id.place_autocompletehome_fragment);
        }
        typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_REGIONS)
                .build();
        autocompleteFragment.setFilter(typeFilter);
        homeFrag = (HomeFragment)
                getSupportFragmentManager().findFragmentById(R.id.homeFragment);
        directFrag = (SearchDirectoryFragment)
                getSupportFragmentManager().findFragmentById(R.id.directoryFragment);

        /*autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                try {
                    Log.i("esty", "Place: " + place.getName());

                    String PlaceLatLon = place.getLatLng().toString();
                    String[] LatLonArr = PlaceLatLon.split(",");
                    String[] LatArr = LatLonArr[0].split("\\(");
                    Lat = LatArr[1];
                    String[] LonArr = LatLonArr[1].split("\\)");
                    Lon = LonArr[0];
                    if (LastSelectedTab == 0) {
                        if (homeFrag != null) {
                            homeFrag.ChangeLocation(Double.parseDouble(Lat), Double.parseDouble(Lon));
                        }
                    } else if (LastSelectedTab == 1) {
                        if (directFrag != null) {
                            directFrag.ChangeLocation(Double.parseDouble(Lat), Double.parseDouble(Lon));
                        }
                    }
                } catch (Exception e) {
                    Log.e("esty", "Error: " + e.getMessage());
                }

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.e("esty", "An error occurred: " + status);
            }
        });*/

        /*if (autocompleteFragment.getView() != null) {
            ((EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input))
                    .setTextSize(15.0f);
            (autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input))
                    .setPadding(15, 10, 5, 2);
            (autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_button))
                    .setPadding(5, 10, 0, 2);
            (autocompleteFragment.getView().findViewById(R.id.place_autocomplete_clear_button))
                    .setPadding(0, 10, 5, 2);
        }*/
        displayView(LastSelectedTab);
    }

    private void showFragment(int fragmentIndex, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i == fragmentIndex) {
                transaction.show(fragments[i]);

            } else {
                transaction.hide(fragments[i]);
            }
        }
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void createAndShowAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?");
        builder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO

                        dialog.dismiss();
                        moveTaskToBack(true);
                        finish();
                    }
                });
        builder.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            if (LastSelectedTab == 0) {
                if (homeFrag != null) {
                    if (homeFrag.CheckHomeInfoWindow()) {
                        createAndShowAlertDialog();
                    }
                } else {
                    createAndShowAlertDialog();
                }
            } else {
                if (LastSelectedTab == 1) {
                    if (directFrag != null) {
                        if (directFrag.CheckInfoWindow()) {
                            displayView(0);
                        }
                    }
                } else {
                    displayView(0);
                }
            }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                displayView(7);
                break;

            default:
                break;
        }
        return false;
    }

    private void displayView(int position) {
        switch (position) {
            case 0:
                showFragment(HOME, false);
                LastSelectedTab = 0;
                if (homeFrag != null) {
                    homeFrag.isHomePressed();
                }
                break;
            case 1:
                showFragment(DIRECTORY, false);
                LastSelectedTab = 1;
                break;
            case 2:
                Intent recentIntent = new Intent(context, TabRecentlyViewedActivity.class);
                startActivity(recentIntent);
                return;
            case 3:
                if (AppGlobal.IsLoggedIn(context)) {
                    Intent intent2 = new Intent(context, LoginActivity.class);
                    startActivity(intent2);

                    return;
                } else {

                    Intent shortlistIntent = new Intent(context, TabShorlistActivity.class);
                    startActivity(shortlistIntent);
                    return;
                }
            case 4:
                if (AppGlobal.IsLoggedIn(context)) {
                    Intent intent3 = new Intent(context, LoginActivity.class);
                    startActivity(intent3);
                    return;
                } else {
                    NavTitle.setText("My Saved Searches");
                    showFragment(SAVESEARCH, false);
                    LastSelectedTab = 4;
                }
                break;
            case 5:
                if (AppGlobal.IsLoggedIn(context)) {
                    Intent intent4 = new Intent(context, LoginActivity.class);
                    startActivity(intent4);
                    return;
                } else {
                    NavTitle.setText("Notifications");
                    showFragment(NOTIFICATIONS, false);
                    LastSelectedTab = 5;
                }
                break;
            case 6:
                return;
            case 7:
                if (AppGlobal.IsLoggedIn(context)) {
                    Intent intent5 = new Intent(context, LoginActivity.class);
                    startActivity(intent5);
                    //finish();
                    return;
                } else {
                    //fragment = new PostPropertyFragment();
                    NavTitle.setText("Post Property");
                    showFragment(POSTPROPERTY, false);
                    LastSelectedTab = 7;
                }
                break;

            case 8:
                if (AppGlobal.IsLoggedIn(context)) {
                    Intent intent6 = new Intent(context, LoginActivity.class);
                    startActivity(intent6);
                    //finish();
                    return;
                } else {
                    NavTitle.setText("My Properties");
                    //fragment = new MyPostedPropertiesFragment();
                    showFragment(MYPROPERTY, false);
                    LastSelectedTab = 8;
                }
                break;
            case 9:
                return;

            case 10:
                NavTitle.setText("Settings");
                showFragment(SETTINGS, false);
                LastSelectedTab = 10;
                break;

            case 11:
                //showFragment(HOME, false);
                Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
                }
                LastSelectedTab = 0;
                break;

            case 12:
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                share.putExtra(Intent.EXTRA_SUBJECT, "Checkout iBaax Real Estate Marketplace App");
                share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.ibaax.com.ibaax&hl=en");

                startActivity(Intent.createChooser(share, "Share this app."));
                LastSelectedTab = 0;
                break;

            default:
                //fragment = new HomeFragment();
                showFragment(HOME, false);
                LastSelectedTab = 0;
                break;
        }

        getSupportActionBar().setTitle("");
        if (position == 0 || position == 1) {
            GooglePlacesLayout.setVisibility(View.VISIBLE);
            NavTitle.setVisibility(View.GONE);
        } else {
            GooglePlacesLayout.setVisibility(View.GONE);
            NavTitle.setVisibility(View.VISIBLE);
        }
        //}

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            //displayView(LastSelectedTab);


        } catch (Exception ex) {

            Log.v("error", "error mainpaing onresume: " + ex.getMessage());
        }
    }

    public void setPlaceText(String city, String locality) {
        if (city.length() > 0 && locality.length() > 0) {
            SearchText.setText(locality + ", " + city);
        } else {
            SearchText.setText(city);
        }
    }

    @Override
    public void onClick(View view) {

        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        if (view.getId() == R.id.txt_place_search) {
            OpenGooglePlaces();
        }
        if (view.getId() == R.id.imgbtn_search_places) {
            OpenGooglePlaces();
        }
        if (view.getId() == R.id.imgbtn_cancel_places) {
            SearchText.setText("");
        }

    }

    void OpenGooglePlaces() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    String PlaceLatLon = place.getLatLng().toString();
                    String[] LatLonArr = PlaceLatLon.split(",");
                    String[] LatArr = LatLonArr[0].split("\\(");
                    Lat = LatArr[1];
                    String[] LonArr = LatLonArr[1].split("\\)");
                    Lon = LonArr[0];
                    if (LastSelectedTab == 0) {
                        if (homeFrag != null) {
                            homeFrag.ChangeLocation(Double.parseDouble(Lat), Double.parseDouble(Lon));
                        }
                    } else if (LastSelectedTab == 1) {
                        if (directFrag != null) {
                            directFrag.ChangeLocation(Double.parseDouble(Lat), Double.parseDouble(Lon));
                        }
                    }
                } catch (Exception e) {
                    Log.e("esty", "Error: " + e.getMessage());
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("esty", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

    }
}
