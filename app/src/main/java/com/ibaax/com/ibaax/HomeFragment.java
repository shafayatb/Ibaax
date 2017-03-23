package com.ibaax.com.ibaax;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Adapter.FeaturedPropertyAdapter;
import Adapter.PropertyPager;
import Entity.AppGlobal;
import Entity.Country;
import Entity.Property;
import Entity.SearchFilters;
import Event.IHttpResponse;
import Event.IProperty;
import JSOINParsing.parseProperty2;
import Plugins.HelperFunctions;
import ServiceInvoke.GetAllLocationInfoFromLatLon;
import ServiceInvoke.GetLocationFormLatLon;
import ServiceInvoke.HttpRequest;
import ServiceInvoke.getCountryFormLatLon;
import UI.MessageBox;
import sensor.GPSTracker;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private final static int FILTER_REQUEST = 1221;
    public static Boolean IS_LOCATION_FOUND_FROM_GPS = false;
    private static int CAMERA_MOVE_REACT_THRESHOLD_MS = 1000;
    LinearLayout lnrViewType, lnrMapView, lnrFilter, lnrSaveSearch;
    ListView listview_property;
    TextView lblFeaturedProperties, lblText;
    ImageView imgIcon, imgMyLocation, imgSatelliteView;
    ViewPager pagerProperty;
    ProgressBar progress;
    View rootView;
    SupportMapFragment mapFragment;
    Context context;
    AppPrefs pref;
    SearchFilters searchFilters;
    List<Property> propertylist = new ArrayList<>();
    String countryTicker;
    double CURRENT_latitude;
    double CURRENT_longitude;
    String LOCATION_NAME = "Me";
    Boolean IS_MAP_SHOWING = true, IsSatteliteView = true;
    FeaturedPropertyAdapter adapter;
    GoogleMap gmap;
    GPSTracker gps;
    String CityName = "";
    String LocalityName = "";
    String SaveSearchLink = "";
    MarkerOptions marker;
    PropertyPager pagerAdapter;
    private HashMap<Integer, Marker> visibleMarkers = new HashMap<>();
    private LatLngBounds currentCameraBounds;
    private long lastCallMs = Long.MIN_VALUE;


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {

                gps = new GPSTracker(context);

            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
            context = getActivity();
            pref = new AppPrefs(context);
            searchFilters = new SearchFilters();
            init();
            initSearchFilters();
        } catch (Exception e) {
            Log.e("esty", "HomeFragment/OnCreateView/FirstCatchBlock->Error: " + e.getMessage());
        }

        gps = new GPSTracker(getActivity());
        if (gps.canGetLocation()) {
            if (gps.getLatitude() != 0 && gps.getLongitude() != 0) {

                propertylist.clear();
                CURRENT_latitude = gps.getLatitude();
                CURRENT_longitude = gps.getLongitude();
                IS_LOCATION_FOUND_FROM_GPS = true;
                new getCountryFormLatLon(context, CURRENT_latitude, CURRENT_longitude, new IHttpResponse() {
                    @Override
                    public void RequestSuccess(Object response) {
                        pref.setUserCountry(AppGlobal.getCurrentCountry((String) response));

                        AppGlobal.getCountryTicker(context);
                        for (Country c : AppGlobal.countryList) {
                            if (c.CountryID.equals(pref.getUserCountry())) {

                                countryTicker = c.CountryTicker;
                                pref.setUserCurrency(c.CurrencyTicker);
                                break;
                            }
                        }
                    }

                    @Override
                    public void RequestFailed(String response) {

                    }
                }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                try {

                    mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.homemap));
                    mapFragment.getMapAsync(this);

                } catch (Exception e) {
                    Log.e("esty", "HomeFragment/OnCreate->Error" + e.getMessage());
                }
            }
            Log.v("esty", "Longitude traker :: " + CURRENT_latitude + " " + CURRENT_longitude);

        } else {

            propertylist.clear();
            AppGlobal.getCountryTicker(context);
            String CurrentCountry = AppGlobal.getUserCountry(context);
            pref.setUserCountry(AppGlobal.getCurrentCountry(CurrentCountry));

            for (Country c : AppGlobal.countryList) {
                if (c.CountryID.equals(pref.getUserCountry())) {
                    lblFeaturedProperties.setText("Properties in " + c.Name);
                    countryTicker = c.CountryTicker;
                    CURRENT_latitude = c.lat;
                    CURRENT_longitude = c.lon;
                    LOCATION_NAME = c.Name;
                    pref.setUserCurrency(c.CurrencyTicker);
                    try {
                        mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.homemap));
                        mapFragment.getMapAsync(this);
                    } catch (Exception e) {
                        Log.e("esty", "HomeFragment/OnCreate->Error" + e.getMessage());
                    }
                    break;
                }
            }
            gps.showSettingsAlert();
        }

        Events();

        return rootView;
    }

    void init() {
        listview_property = (ListView) rootView.findViewById(R.id.listvie_properties);
        lblText = (TextView) rootView.findViewById(R.id.lbl_Text);
        lblText.setText(R.string.list);

        imgIcon = (ImageView) rootView.findViewById(R.id.img_icon);

        lblFeaturedProperties = (TextView) rootView.findViewById(R.id.lbl_featured_property);

        progress = (ProgressBar) rootView.findViewById(R.id.progress_home);
        lnrMapView = (LinearLayout) rootView.findViewById(R.id.lnrMapView);
        lnrViewType = (LinearLayout) rootView.findViewById(R.id.btn_maplist);
        lnrSaveSearch = (LinearLayout) rootView.findViewById(R.id.btn_home_save_search);

        imgSatelliteView = (ImageView) rootView.findViewById(R.id.img_Satellite_location);
        imgMyLocation = (ImageView) rootView.findViewById(R.id.img_gps_location);
        lnrFilter = (LinearLayout) rootView.findViewById(R.id.btn_home_filter);

        pagerProperty = (ViewPager) rootView.findViewById(R.id.pager_property_info_window);
    }

    void initSearchFilters() {
        searchFilters.PropertyTypeIdsIn = "5,6,7,8,9,10,11,12,13,14,19,20,21,22,23,24,25";
        searchFilters.BedRoomSize = "";
        searchFilters.BathRoomSize = "";
        searchFilters.BuiltUpAreaMinSize = "";
        searchFilters.BuiltUpAreaMaxSize = "";
        searchFilters.MinPrice = "";
        searchFilters.MaxPrice = "";
        searchFilters.LotMinSize = "";
        searchFilters.LotMaxSize = "";
        searchFilters.FromBuiltYear = "";
        searchFilters.FromPostDate = "";
        searchFilters.IBaaxId = "";
        searchFilters.QueryString = "";
        searchFilters.IsPostedByOwner = "";
        searchFilters.IsPostedByAgent = "";
        searchFilters.HasPhoto = "";
        searchFilters.HasVideo = "";
        searchFilters.PropertyTransactionTypeCategoryId = "1,2";
        searchFilters.PropertyTransactionTypeIdsIn = "1,2,3,4,5,6,7";
    }

    void Events() {
        imgMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gps = new GPSTracker(context);
                if (gps.canGetLocation()) {

                    gps.getLocation();
                    if (gps.getLatitude() != 0.0 && gps.getLongitude() != 0.0) {
                        Log.v("location", "location" + gps.getLatitude() + " " + gps.getLongitude());
                        CURRENT_latitude = gps.getLatitude();
                        CURRENT_longitude = gps.getLongitude();
                        IS_LOCATION_FOUND_FROM_GPS = true;
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(gps.getLatitude(), gps.getLongitude())).zoom(15).build();
                        gmap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    } else {
                        Toast.makeText(context, "Please wait.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    CURRENT_latitude = 0;
                    CURRENT_longitude = 0;
                    IS_LOCATION_FOUND_FROM_GPS = false;
                    gps.showSettingsAlert();
                }

            }
        });
        lnrViewType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (IS_MAP_SHOWING) {

                    listview_property.setVisibility(View.VISIBLE);
                    lnrMapView.setVisibility(View.GONE);
                    lblText.setText(R.string.map);
                    pagerProperty.setVisibility(View.GONE);
                    imgIcon.setImageResource(R.drawable.ic_locationbtn);
                    imgMyLocation.setVisibility(View.GONE);
                    imgSatelliteView.setVisibility(View.GONE);
                    IS_MAP_SHOWING = false;
                } else {
                    lnrMapView.setVisibility(View.VISIBLE);
                    listview_property.setVisibility(View.GONE);
                    lblText.setText(R.string.list);
                    imgMyLocation.setVisibility(View.VISIBLE);
                    imgSatelliteView.setVisibility(View.VISIBLE);
                    imgIcon.setImageResource(R.drawable.ic_listwhite);
                    IS_MAP_SHOWING = true;
                }
            }
        });
        imgSatelliteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IsSatteliteView) {
                    gmap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    IsSatteliteView = false;
                    imgSatelliteView.setImageResource(R.drawable.ic_satellite_on);
                } else {
                    gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    IsSatteliteView = true;
                    imgSatelliteView.setImageResource(R.drawable.ic_satellite_off);
                }
            }
        });

        lnrFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SearchPropertyForResultActivity.class);
                startActivityForResult(intent, FILTER_REQUEST);
            }
        });

        lnrSaveSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppGlobal.IsLoggedIn(context)) {
                    Intent intent2 = new Intent(context, LoginActivity.class);
                    startActivity(intent2);
                } else {
                    MessageBox.SaveSearchDialog(context, SaveSearchLink, searchFilters.WebURL, LocalityName + ", " + CityName);
                }
            }
        });

    }

    private void getFeaturedProperty(int page, String countryCode) {
        float zoom = gmap.getCameraPosition().zoom;
        lblFeaturedProperties.setVisibility(View.VISIBLE);
        if (zoom > 11) {
            progress.setVisibility(View.VISIBLE);
            String URL;
            LatLngBounds bounds = this.gmap.getProjection().getVisibleRegion().latLngBounds;
            LatLng northEast = bounds.northeast;
            LatLng southWest = bounds.southwest;
            URL = AppGlobal.localHost + "/api/Mproperty/GetSearchPropertyResult/" +
                    "?OriginAddress=" + AppGlobal.ImageHost + "&PageNo=" + String.valueOf(page) + "&PageSize=100" +
                    "&SearchParams={\"LogedInUserId\":\"" + pref.getUserID() + "\"," +
                    "\"IsTerritorySearch\":\"" + "False" + "\"," +
                    "\"NELat\":\"" + String.valueOf(northEast.latitude) + "\"," +
                    "\"NELng\":\"" + String.valueOf(northEast.longitude) + "\"," +
                    "\"SWLat\":\"" + String.valueOf(southWest.latitude) + "\"," +
                    "\"SWLng\":\"" + String.valueOf(southWest.longitude) + "\"," +
                    "\"PropertyTypeIdsIn\":\"" + searchFilters.PropertyTypeIdsIn + "\"," +
                    "\"BedRoomSize\":\"" + searchFilters.BedRoomSize + "\"," +
                    "\"BathRoomSize\":\"" + searchFilters.BathRoomSize + "\"," +
                    "\"MinPrice\":\"" + searchFilters.MinPrice + "\"," +
                    "\"MaxPrice\":\"" + searchFilters.MaxPrice + "\"," +
                    "\"BuiltUpAreaMinSize\":\"" + searchFilters.BuiltUpAreaMinSize + "\"," +
                    "\"BuiltUpAreaMaxSize\":\"" + searchFilters.BuiltUpAreaMaxSize + "\"," +
                    "\"LotMinSize\":\"" + searchFilters.LotMinSize + "\"," +
                    "\"LotMaxSize\":\"" + searchFilters.LotMaxSize + "\"," +
                    "\"PropertyTransactionTypeIdsIn\":\"" + searchFilters.PropertyTransactionTypeIdsIn + "\"}";

            SaveSearchLink = AppGlobal.localHost + "/api/MSavedSearch/InsertOrUpdateSavedSearch/" +
                    "?OriginAddress=" + AppGlobal.ImageHost + "&PageNo=0&PageSize=100" +
                    "&SearchParams={\"LogedInUserId\":\"" + pref.getUserID() + "\"," +
                    "\"IsTerritorySearch\":\"" + "False" + "\"," +
                    "\"NELat\":\"" + String.valueOf(northEast.latitude) + "\"," +
                    "\"NELng\":\"" + String.valueOf(northEast.longitude) + "\"," +
                    "\"SWLat\":\"" + String.valueOf(southWest.latitude) + "\"," +
                    "\"SWLng\":\"" + String.valueOf(southWest.longitude) + "\"," +
                    "\"PropertyTypeIdsIn\":\"" + searchFilters.PropertyTypeIdsIn + "\"," +
                    "\"BedRoomSize\":\"" + searchFilters.BedRoomSize + "\"," +
                    "\"BathRoomSize\":\"" + searchFilters.BathRoomSize + "\"," +
                    "\"MinPrice\":\"" + searchFilters.MinPrice + "\"," +
                    "\"MaxPrice\":\"" + searchFilters.MaxPrice + "\"," +
                    "\"BuiltUpAreaMinSize\":\"" + searchFilters.BuiltUpAreaMinSize + "\"," +
                    "\"BuiltUpAreaMaxSize\":\"" + searchFilters.BuiltUpAreaMaxSize + "\"," +
                    "\"LotMinSize\":\"" + searchFilters.LotMinSize + "\"," +
                    "\"LotMaxSize\":\"" + searchFilters.LotMaxSize + "\"," +
                    "\"PropertyTransactionTypeIdsIn\":\"" + searchFilters.PropertyTransactionTypeIdsIn + "\"}" +
                    "&isUpdate=false";
            Log.v("url", "URL: " + URL);
            Log.v("url", "SaveSearchURL: " + SaveSearchLink);
            searchFilters.WebURL = AppGlobal.ImageHost + "/homes" + searchFilters.WebFilters + "/" + String.valueOf(northEast.latitude)
                    + "," + String.valueOf(northEast.longitude) + ","
                    + String.valueOf(southWest.latitude) + ","
                    + String.valueOf(southWest.longitude) + "__rect"
                    + "/" + String.valueOf(zoom) + "_zm";

            Log.v("esty", "WebURL: " + searchFilters.WebURL);
            new HttpRequest(context, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {

                    try {
                        visibleMarkers.clear();
                        gmap.clear();
                        propertylist.clear();
                        propertylist = new parseProperty2(context).parse((String) response, propertylist);
                        Log.v("esty", "ListSize: " + String.valueOf(propertylist.size()));

                        BindFeaturdProduct();
                        MyLocation();
                        if (propertylist.size() > 0) {
                            lblFeaturedProperties.setText(String.valueOf(propertylist.size()) + " properties showing");
                        } else {
                            lblFeaturedProperties.setText("No properties found in this area");
                        }


                    } catch (Exception ex) {
                        Log.e("error", "RequestSuccess error: " + ex.getMessage());

                    }

                }

                @Override
                public void RequestFailed(String response) {

                }
            }).MakeJsonArrayReq(URL);


        } else {
            visibleMarkers.clear();
            gmap.clear();
            propertylist.clear();
            progress.setVisibility(View.GONE);
            lblFeaturedProperties.setText(R.string.content_warning_zoom_filter);
            MyLocation();

        }

    }

    void MyLocation() {
        final MarkerOptions usermarker = new MarkerOptions().position(new LatLng(CURRENT_latitude, CURRENT_longitude)).title(LOCATION_NAME);
        usermarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_me));
        if (CURRENT_latitude > 0 && CURRENT_longitude > 0 && IS_LOCATION_FOUND_FROM_GPS) {
            new GetLocationFormLatLon(context, CURRENT_latitude, CURRENT_longitude, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {
                    usermarker.title((String) response);
                    gmap.addMarker(usermarker);
                }

                @Override
                public void RequestFailed(String response) {

                }
            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

    }

    private void BindMap() {

        try {
            if (propertylist != null && propertylist.size() > 0) {

                //region Unused
                for (int i = 0; i < propertylist.size(); i++) {


                    marker = new MarkerOptions().position(
                            new LatLng(propertylist.get(i).Latitude,
                                    propertylist.get(i).Longitude)).title(
                            propertylist.get(i).PropertyName);
                    if (propertylist.get(i).PropertyTransactionTypeCategoryID == 1) {
                        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_icon_forsale));
                    } else {
                        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_icon_forrent));
                    }

                    //visibleMarkers.put()
                    gmap.addMarker(marker);
                    //Marker mar=googleMap.addMarker(marker);

                }
                pagerAdapter = new PropertyPager(context, propertylist, new IProperty() {
                    @Override
                    public void btnFavorite_click(Object obj) {
                        pagerProperty.setCurrentItem((int) obj - 1, true);
                    }

                    @Override
                    public void onClick(Object obj) {
                        pagerProperty.setCurrentItem((int) obj + 1, true);
                    }
                });
                pagerProperty.setAdapter(pagerAdapter);

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ((NavigationDrawerMainActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lblFeaturedProperties.setVisibility(View.GONE);
                            }
                        });

                    }
                }, 3000);

                //endregion
                // addItemsToMap(propertylist);

            }
        } catch (Exception e) {
            Log.e("esty", "HomeFragment/BindMap-> error: " + e.getMessage());
        }

    }

    /*private void addItemsToMap(List<Property> items) {
        if (this.gmap != null) {
            //This is the current user-viewable region of the map
            LatLngBounds bounds = this.gmap.getProjection().getVisibleRegion().latLngBounds;
            //Loop through all the items that are available to be placed on the map
            for (Property item : items) {
                if (bounds.contains(new LatLng(item.Latitude, item.Longitude))) {
                    //If the item is within the the bounds of the screen
                    if (!visibleMarkers.containsKey((int) item.PropertyID)) {
                        visibleMarkers.put((int) item.PropertyID, gmap.addMarker(GetMarkerFromMap(item)));
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
    }*/

   /* MarkerOptions GetMarkerFromMap(Property item) {
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
    }*/

    private void BindFeaturdProduct() {

        try {
            adapter = new FeaturedPropertyAdapter(context, propertylist, new IProperty() {
                @Override
                public void btnFavorite_click(Object obj) {
                }

                @Override
                public void onClick(Object obj) {

                }
            });
            listview_property.setAdapter(adapter);
            progress.setVisibility(View.GONE);
            BindMap();
        } catch (Exception e) {
            Log.e("esty", "HomeFragment/BindFreaturedProduct->Error: " + e.getMessage());
        }

    }


    public void isHomePressed() {

        if (!IS_MAP_SHOWING) {
            lnrMapView.setVisibility(View.VISIBLE);
            listview_property.setVisibility(View.GONE);
            lblText.setText(R.string.list);
            imgMyLocation.setVisibility(View.VISIBLE);
            imgSatelliteView.setVisibility(View.VISIBLE);
            pagerProperty.setVisibility(View.GONE);
            imgIcon.setImageResource(R.drawable.ic_listwhite);
            IS_MAP_SHOWING = true;

        }
    }

    @Override
    public void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.location.PROVIDERS_CHANGED");
        getActivity().registerReceiver(this.receiver, filter);
        //gps = new GPSTracker(getActivity());
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        try {
            if (!hidden) {
                ((NavigationDrawerMainActivity) context).setPlaceText(CityName, LocalityName);
                if (pref != null && adapter != null && pagerAdapter != null) {
                    propertylist = HelperFunctions.ChangeCurrency(propertylist, pref);
                    propertylist = HelperFunctions.ChangeUnits(propertylist, pref);
                    adapter.notifyDataSetChanged();
                    gmap.clear();
                    BindMap();
                }
            }
        } catch (Exception e) {

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            Log.v("esty", "map loding complate....");
            gmap = googleMap;

            Log.v("esty", "Longitude: " + CURRENT_latitude + " " + CURRENT_longitude);

            final MarkerOptions usermarker = new MarkerOptions().position(new LatLng(CURRENT_latitude, CURRENT_longitude)).title(LOCATION_NAME);
            usermarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_me));

            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(CURRENT_latitude, CURRENT_longitude)).zoom(13).build();//zoom level was 15. changed to 19
            gmap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            if (CURRENT_latitude != 0 && CURRENT_longitude != 0 && IS_LOCATION_FOUND_FROM_GPS) {
                new GetLocationFormLatLon(context, CURRENT_latitude, CURRENT_longitude, new IHttpResponse() {
                    @Override
                    public void RequestSuccess(Object response) {


                        usermarker.title((String) response);
                        gmap.addMarker(usermarker);
                    }

                    @Override
                    public void RequestFailed(String response) {

                    }
                }).execute();
            }
            gmap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {


                @Override
                public void onCameraChange(CameraPosition c) {
                    // TODO Auto-generated method stub
                    //googleMap.clear();
                    try {

                        LatLngBounds bounds = gmap.getProjection().getVisibleRegion().latLngBounds;
                        if (currentCameraBounds != null) {
                            if (currentCameraBounds.northeast.latitude == bounds.northeast.latitude
                                    && currentCameraBounds.northeast.longitude == bounds.northeast.longitude
                                    && currentCameraBounds.southwest.latitude == bounds.southwest.latitude
                                    && currentCameraBounds.southwest.longitude == bounds.southwest.longitude) {
                                return;
                            }
                        }

                        final long snap = System.currentTimeMillis();
                        if (lastCallMs + CAMERA_MOVE_REACT_THRESHOLD_MS > snap) {
                            lastCallMs = snap;
                            return;
                        }
                        lblFeaturedProperties.setText(R.string.content_text_loading);
                        double latitude = c.target.latitude;
                        double longitude = c.target.longitude;
                        getSetMapLocations(String.valueOf(latitude), String.valueOf(longitude));

                        getFeaturedProperty(0, AppGlobal.CurrentCountryTicker);

                        lastCallMs = snap;
                        currentCameraBounds = bounds;

                        pagerProperty.setVisibility(View.GONE);
                        imgMyLocation.setVisibility(View.VISIBLE);
                        imgSatelliteView.setVisibility(View.VISIBLE);

                        //BindMap();

                    } catch (Exception ex) {

                        Log.v("error", "setOnCameraChangeListener: " + ex.getMessage());
                    }


                }
            });
            gmap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if (marker.getTitle().equals(usermarker.getTitle())) {
                        return true;
                    } else {
                        PagerInfoWindow(marker.getTitle());
                        return true;
                    }
                }
            });
            gmap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    pagerProperty.setVisibility(View.GONE);
                    imgMyLocation.setVisibility(View.VISIBLE);
                    imgSatelliteView.setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception ex) {

            MessageBox.Show(context, ex.getMessage());
        }

    }

    void PagerInfoWindow(String name) {

        for (int i = 0; i < propertylist.size(); i++) {
            if (propertylist.get(i).PropertyName.equals(name)) {
                pagerProperty.setVisibility(View.VISIBLE);
                imgMyLocation.setVisibility(View.GONE);
                imgSatelliteView.setVisibility(View.GONE);
                pagerProperty.setCurrentItem(i, false);
            }
        }

    }


    void getSetMapLocations(String latitude, String longitude) {

        String URL = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + String.valueOf(latitude) + "," + String.valueOf(longitude) + "&sensor=true";
        new HttpRequest(context, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {
                new GetAllLocationInfoFromLatLon(context, (String) response, new IHttpResponse() {
                    @Override
                    public void RequestSuccess(Object response) {
                        try {
                            JSONObject jo = new JSONObject((String) response);
                            String CountryName = jo.getString("CountryName");
                            for (Country cc : AppGlobal.countryList) {
                                if (cc.Name.toLowerCase().equals(CountryName.toLowerCase())) {
                                    if (!AppGlobal.CurrentCountryTicker.equals(cc.CountryTicker)) {
                                        AppGlobal.CurrentCountryTicker = cc.CountryTicker;
                                        AppGlobal.CurrentCountryID = cc.CountryID;
                                        AppGlobal.CurrentCountryName = cc.Name;
                                        propertylist.clear();

                                    }
                                    break;
                                }
                            }
                            CityName = jo.getString("CityName");
                            LocalityName = jo.getString("LocalityName");
                            ((NavigationDrawerMainActivity) context).setPlaceText(CityName, LocalityName);
                        } catch (JSONException e) {
                            Log.e("esty", "JSONError: " + response);
                        }
                    }

                    @Override
                    public void RequestFailed(String response) {
                        Log.e("esty", "InnerError: " + response);
                    }
                }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }

            @Override
            public void RequestFailed(String response) {
                Log.e("esty", "Error: " + response);
            }
        }).JSONObjectGetRequest(null, URL);

    }

    public boolean CheckHomeInfoWindow() {
        if (pagerProperty.getVisibility() == View.GONE) {
            return true;
        } else {
            pagerProperty.setVisibility(View.GONE);
            return false;
        }

    }

    @Override
    public void onStop() {
        context.unregisterReceiver(receiver);
        super.onStop();
    }

    public void ChangeLocation(double lat, double lon) {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat, lon)).zoom(15).build();//zoom level was 15. changed to 19
        gmap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_REQUEST) {
            if (resultCode == FILTER_REQUEST) {
                searchFilters = (SearchFilters) data.getSerializableExtra("FilterResults");
                getFeaturedProperty(0, "");
            }
        }
    }


}
