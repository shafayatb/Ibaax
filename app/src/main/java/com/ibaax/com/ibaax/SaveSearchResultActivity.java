package com.ibaax.com.ibaax;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Adapter.FeaturedPropertyAdapter;
import Adapter.PropertyPager;
import Entity.AppGlobal;
import Entity.Property;
import Event.IEvent;
import Event.IHttpResponse;
import Event.IProperty;
import JSOINParsing.parseProperty2;
import ServiceInvoke.HttpRequest;
import UI.MessageBox;

public class SaveSearchResultActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static boolean IS_DELETED = false, IS_RENAMED = false;
    List<Property> SaveSearchList = new ArrayList<>();
    ListView listSaveSearch;
    LinearLayout lnrMap;
    GoogleMap gMap;
    SupportMapFragment mapFragment;
    ViewPager saveSearchPager;
    ProgressBar progressBar;
    String SaveID, URL;
    boolean ISMAPVISIBLE = false;
    Button btnMapLIst;
    PropertyPager pagerAdapter;
    private HashMap<Integer, Marker> visibleMarkers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_search_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getIntent().getStringExtra("Name"));
        SaveID = getIntent().getStringExtra("ID");
        URL = getIntent().getStringExtra("URL");
        IS_DELETED = false;
        IS_RENAMED = false;
        findViews();

    }

    void findViews() {
        listSaveSearch = (ListView) findViewById(R.id.listview_save_search);
        saveSearchPager = (ViewPager) findViewById(R.id.pager_save_search_info_window);
        progressBar = (ProgressBar) findViewById(R.id.progress_save_search);
        lnrMap = (LinearLayout) findViewById(R.id.lnr_map_save_search);
        btnMapLIst = (Button) findViewById(R.id.btn_save_search_maplist);
        initGoogleMaps();
    }

    void initGoogleMaps() {

        mapFragment = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.save_search_map));

        mapFragment.getMapAsync(this);
    }

    void GetSavedSearchResult() {

        String URL = AppGlobal.localHost + "/api/MSavedSearch/GetSavedSearchResult?savedSearchId=" + SaveID;
        new HttpRequest(this, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {
                try {

                    JSONObject jsonObject = new JSONObject((String) response);
                    JSONArray jsonArray = jsonObject.getJSONArray("list");
                    SaveSearchList = new parseProperty2(SaveSearchResultActivity.this).parse(jsonArray.toString(), SaveSearchList);
                    Bind();
                } catch (Exception e) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void RequestFailed(String response) {

            }
        }).JSONObjectGetRequest(null, URL);
    }

    void Bind() {
        if (SaveSearchList.size() > 0) {

            listSaveSearch.setAdapter(new FeaturedPropertyAdapter(this, SaveSearchList, new IProperty() {
                @Override
                public void btnFavorite_click(Object obj) {

                }

                @Override
                public void onClick(Object obj) {

                }
            }));
            PopulateMap();
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_savesearch_rename:
                String RenURL = AppGlobal.localHost + "/api/MSavedSearch/RenameSavedSearch?id=" + SaveID
                        + "&name=";

                MessageBox.SaveSearchRename(this, RenURL, getIntent().getStringExtra("Name"), new IEvent() {
                    @Override
                    public void onClick(Object obj) {
                        getSupportActionBar().setTitle((String) obj);
                        IS_RENAMED = true;
                    }
                });
                return true;
            case R.id.action_savesearch_share:
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                share.putExtra(Intent.EXTRA_SUBJECT, "");
                share.putExtra(Intent.EXTRA_TEXT, URL);

                startActivity(Intent.createChooser(share, "Share Saved Search"));
                return true;
            case R.id.action_savesearch_delete:
                String DelURL = AppGlobal.localHost + "/api/MSavedSearch/DeleteSavedSearch?id=" + SaveID;
                new HttpRequest(this, new IHttpResponse() {
                    @Override
                    public void RequestSuccess(Object response) {
                        try {
                            JSONObject jo = new JSONObject((String) response);
                            if (jo.getInt("flag") == 1) {
                                IS_DELETED = true;
                                finish();
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void RequestFailed(String response) {

                    }
                }).HttpPostProperty(null, DelURL);
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        GetSavedSearchResult();

    }

    void PagerInfoWindow(String name) {

        for (int i = 0; i < SaveSearchList.size(); i++) {
            if (SaveSearchList.get(i).PropertyName.equals(name)) {
                saveSearchPager.setVisibility(View.VISIBLE);
                saveSearchPager.setCurrentItem(i, false);
            }
        }

    }

    void PopulateMap() {
        if (SaveSearchList.size() > 0) {

            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(SaveSearchList.get(0).Latitude, SaveSearchList.get(0).Longitude)).zoom(13).build();//zoom level was 15. changed to 19
            gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            gMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    addItemsToMap(SaveSearchList);
                }
            });
            gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    PagerInfoWindow(marker.getTitle());
                    return true;
                }
            });
            gMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    addItemsToMap(SaveSearchList);
                }
            });
            gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    saveSearchPager.setVisibility(View.GONE);
                }
            });
            pagerAdapter = new PropertyPager(this, SaveSearchList, new IProperty() {
                @Override
                public void btnFavorite_click(Object obj) {
                    saveSearchPager.setCurrentItem((int) obj - 1, true);
                }

                @Override
                public void onClick(Object obj) {
                    saveSearchPager.setCurrentItem((int) obj + 1, true);
                }
            });
            saveSearchPager.setAdapter(pagerAdapter);

        }

    }

    private void addItemsToMap(List<Property> items) {
        if (this.gMap != null) {
            //This is the current user-viewable region of the map
            LatLngBounds bounds = this.gMap.getProjection().getVisibleRegion().latLngBounds;
            //Loop through all the items that are available to be placed on the map
            for (Property item : items) {
                if (bounds.contains(new LatLng(item.Latitude, item.Longitude))) {
                    //If the item is within the the bounds of the screen
                    if (!visibleMarkers.containsKey((int) item.PropertyID)) {
                        visibleMarkers.put((int) item.PropertyID, gMap.addMarker(GetMarkerFromMap(item)));
                    }
                } else {
                    if (visibleMarkers.containsKey((int) item.PropertyID)) {
                        //1. Remove the Marker from the GoogleMap
                        visibleMarkers.get((int) item.PropertyID).remove();

                        //2. Remove the reference to the Marker from the HashMap
                        visibleMarkers.remove((int) item.PropertyID);
                    }
                }
            }
        }
    }

    MarkerOptions GetMarkerFromMap(Property item) {
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(item.Latitude,
                        item.Longitude)).title(
                item.PropertyName);
        if (item.PropertyTransactionTypeCategoryID == 1) {
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_icon_forsale));
        } else {
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_icon_forrent));
        }

        return marker;
    }

    public void btn_Map_List(View view) {
        if (ISMAPVISIBLE) {
            listSaveSearch.setVisibility(View.VISIBLE);
            lnrMap.setVisibility(View.GONE);
            saveSearchPager.setVisibility(View.GONE);
            btnMapLIst.setText(R.string.map);
            ISMAPVISIBLE = false;
        } else {
            lnrMap.setVisibility(View.VISIBLE);
            listSaveSearch.setVisibility(View.GONE);
            btnMapLIst.setText(R.string.list);
            ISMAPVISIBLE = true;
        }
    }
}
