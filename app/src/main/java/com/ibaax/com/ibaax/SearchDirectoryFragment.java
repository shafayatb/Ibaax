package com.ibaax.com.ibaax;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Adapter.DirectoryListAdapter;
import Adapter.DirectoryPagerAgentAdapter;
import Entity.Agent;
import Entity.AppGlobal;
import Entity.Country;
import Entity.SearchDirectoryFilters;
import Event.IHttpResponse;
import Event.IProperty;
import JSOINParsing.ParseDirectory;
import Plugins.TextBoxHandler;
import ServiceInvoke.GetAllLocationInfoFromLatLon;
import ServiceInvoke.GetLocationFormLatLon;
import ServiceInvoke.HttpRequest;
import sensor.GPSTracker;

public class SearchDirectoryFragment extends Fragment {

    private final static int DIRECTORY_FILTER_REQUEST = 1331;
    public static Boolean IS_LOCATION_FOUND_FROM_GPS = false;
    static String agentcountryID;
    MarkerOptions usermarker;
    List<Agent> list = new ArrayList<>();
    //List<Brokerages> BroList = new ArrayList<>();
    //List<Developer> DevList = new ArrayList<>();

    DirectoryListAdapter adapter;
    //BrokeragesAdapter broAdapter;
    //DevelopersAdapter devAdapter;

    Context context;
    int pageNumber = 0;

    ProgressBar progressBarDirectory;
    ListView listview;

    LinearLayout btnFlter, lnrMap, lnrPagers;
    ImageView imgMapList, imgMyLocation, imgSatteliteView;
    TextView lblMapList, lblTotalAmount;
    ImageButton btnLeft, btnRight;

    ViewPager agentPager;

    AppPrefs pref;
    MapView mapView;
    GoogleMap gmap;
    GPSTracker gps;
    Boolean IsMapVisible = true, IsSatteliteView = false;
    SearchDirectoryFilters filters;
    double CURRENT_latitude;
    double CURRENT_longitude;

    String countryTicker;
    String SearchParams;
    String LOCATION_NAME = "";
    String CityName = "";
    String LocalityName = "";
    String TotalNumber = "";
    int CurrentNumber = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        View rootView = inflater.inflate(R.layout.fragment_directory, container, false);
        pref = new AppPrefs(context);
        mapView = (MapView) rootView.findViewById(R.id.map_finddirectory);
        mapView.onCreate(savedInstanceState);
        filters = new SearchDirectoryFilters();
        initFilters();
        init(rootView);
        gps = new GPSTracker(getActivity());

        if (gps.canGetLocation()) {
            if (gps.getLatitude() != 0 && gps.getLongitude() != 0) {
                CURRENT_latitude = gps.getLatitude();
                CURRENT_longitude = gps.getLongitude();
                IS_LOCATION_FOUND_FROM_GPS = true;
                initMap();
            }

        } else {
            AppGlobal.getCountryTicker(context);
            String CurrentCountryTicker = AppGlobal.getUserCountry(context);
            String CurrentCountry = AppGlobal.getCurrentCountry(CurrentCountryTicker);
            for (Country c : AppGlobal.countryList) {
                if (c.CountryID.equals(CurrentCountry)) {
                    CURRENT_latitude = c.lat;
                    CURRENT_longitude = c.lon;
                    break;
                }
            }
            initMap();
        }
        event();
        return rootView;
    }

    void init(View rootView) {
        listview = (ListView) rootView.findViewById(R.id.listView_finddirectory);
        progressBarDirectory = (ProgressBar) rootView.findViewById(R.id.progressBarDirectory);
        btnFlter = (LinearLayout) rootView.findViewById(R.id.btn_finddirectory_Filter);
        lnrMap = (LinearLayout) rootView.findViewById(R.id.btn_finddirectory_maplist);
        lnrPagers = (LinearLayout) rootView.findViewById(R.id.lnr_directory_pagers);
        imgMapList = (ImageView) rootView.findViewById(R.id.img_directory_maplist);
        imgMyLocation = (ImageView) rootView.findViewById(R.id.img_directory_gps_location);
        imgSatteliteView = (ImageView) rootView.findViewById(R.id.img_directory_Satellite_location);
        lblMapList = (TextView) rootView.findViewById(R.id.lbl_directory_maplist);
        agentPager = (ViewPager) rootView.findViewById(R.id.pager_agent_slider);

        btnLeft = (ImageButton) rootView.findViewById(R.id.btn_left);
        btnRight = (ImageButton) rootView.findViewById(R.id.btn_right);
        lblTotalAmount = (TextView) rootView.findViewById(R.id.lbl_totalnumber);
    }

    void initFilters() {

        filters.DirectoryType = "1,2,4";
        filters.QueryString = "";
        filters.AreaCoverage = "";
        filters.Specialities = "";
        filters.SpokenLanguages = "";
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        try {
            if (!hidden) {
                ((NavigationDrawerMainActivity) context).setPlaceText(CityName, LocalityName);
            }
        } catch (Exception e) {

        }

    }


    private void event() {


        btnFlter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, SearchDirectoryForResultActivity.class);
                startActivityForResult(intent, DIRECTORY_FILTER_REQUEST);
            }
        });


        lnrMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IsMapVisible) {
                    mapView.setVisibility(View.GONE);
                    listview.setVisibility(View.VISIBLE);
                    lnrPagers.setVisibility(View.GONE);
                    IsMapVisible = false;
                    imgMyLocation.setVisibility(View.GONE);
                    imgSatteliteView.setVisibility(View.GONE);
                    imgMapList.setImageDrawable(getResources().getDrawable(R.drawable.ic_locationbtn));
                    lblMapList.setText(R.string.map);
                } else {
                    mapView.setVisibility(View.VISIBLE);
                    listview.setVisibility(View.GONE);
                    imgMyLocation.setVisibility(View.VISIBLE);
                    imgSatteliteView.setVisibility(View.VISIBLE);
                    IsMapVisible = true;
                    imgMapList.setImageDrawable(getResources().getDrawable(R.drawable.ic_listwhite));
                    lblMapList.setText(R.string.list);
                }
            }
        });

        imgSatteliteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IsSatteliteView) {
                    gmap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    IsSatteliteView = false;
                    imgSatteliteView.setImageResource(R.drawable.ic_satellite_on);
                } else {
                    gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    IsSatteliteView = true;
                    imgSatteliteView.setImageResource(R.drawable.ic_satellite_off);
                }
            }
        });

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

    }

    private void Bind() {
        if (list.size() > 0) {

            adapter = new DirectoryListAdapter(context, list, new IProperty() {
                @Override
                public void btnFavorite_click(Object obj) {

                }

                @Override
                public void onClick(Object obj) {

                }
            });
            listview.setAdapter(adapter);


            DrawPoints();

        } else {

        }
        progressBarDirectory.setVisibility(View.GONE);

        // region Unused
        /*if (list.size() > 0 || BroList.size() > 0 || DevList.size() > 0) {
            if (filters.DirectoryType.equals("1")) {
                adapter = new AgentAdapter(context, list, new IProperty() {
                    @Override
                    public void btnFavorite_click(Object obj) {

                    }

                    @Override
                    public void onClick(Object obj) {

                    }
                });
                listview.setAdapter(adapter);
            } else if (filters.DirectoryType.equals("2")) {
                broAdapter = new BrokeragesAdapter(context, BroList, new IProperty() {
                    @Override
                    public void btnFavorite_click(Object obj) {

                    }

                    @Override
                    public void onClick(Object obj) {

                    }
                });
                listview.setAdapter(broAdapter);
            } else if (filters.DirectoryType.equals("4")) {
                devAdapter = new DevelopersAdapter(context, DevList, new IProperty() {
                    @Override
                    public void btnFavorite_click(Object obj) {

                    }

                    @Override
                    public void onClick(Object obj) {

                    }
                });
                listview.setAdapter(devAdapter);
            }
            lnrNoItem.setVisibility(View.GONE);
            DrawPoints();

        } else {
            lnrNoItem.setVisibility(View.VISIBLE);
        }
        progressBarDirectory.setVisibility(View.GONE);*/
        //endregion
    }

    private void getFeaturedData(int page, String countryCode) {

        progressBarDirectory.setVisibility(View.VISIBLE);
        try {

            LatLngBounds bounds = this.gmap.getProjection().getVisibleRegion().latLngBounds;
            LatLng northEast = bounds.northeast;
            LatLng southWest = bounds.southwest;
            JSONObject agentSearchParams = new JSONObject();
            agentSearchParams.put("TypeIds", filters.DirectoryType);
            agentSearchParams.put("IsTerritorySearch", "False");
            agentSearchParams.put("NELat", String.valueOf(northEast.latitude));
            agentSearchParams.put("NELng", String.valueOf(northEast.longitude));
            agentSearchParams.put("SWLat", String.valueOf(southWest.latitude));
            agentSearchParams.put("SWLng", String.valueOf(southWest.longitude));
            agentSearchParams.put("LogedInUserId", String.valueOf(pref.getUserID()));
            agentSearchParams.put("Zoom", "6");
            agentSearchParams.put("IsMapList", "True");
            //agentSearchParams.put("CountryDomain", TextBoxHandler.IsNullOrEmpty(countryTicker));
            // agentSearchParams.put("QueryString", filters.QueryString);
            SearchParams = agentSearchParams.toString();
        } catch (JSONException e) {
            Log.e("esty", "JSONErrorError: " + e.getMessage());
        }
        String URL = AppGlobal.localHost + "/api/MCompany/GetDirectorySearchResult/?OriginAddress=http://www.rebaax.com&PageNo=" + page + "" +
                "&PageSize=1000&SearchParams=" + SearchParams;
        Log.v("url", URL);
        agentcountryID = countryCode;
        new HttpRequest(context, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {
                list.clear();
                //BroList.clear();
                //DevList.clear();
                gmap.clear();
                MyLocation();
                try {
                    //region Unused
                    /*switch (filters.DirectoryType) {
                        case "1":
                            list = new parseSearchAgent().parse((String) response, list);
                            TotalNumber = TextBoxHandler.IsNullOrEmpty(list.get(0).TotalCount);
                            break;
                        case "2":
                            BroList = new parseBrokerage().parse((String) response, BroList);
                            TotalNumber = TextBoxHandler.IsNullOrEmpty(String.valueOf(BroList.get(0).TotalCount));
                            break;
                        case "4":
                            DevList = new ParseDeveloper().parse((String) response, DevList);
                            TotalNumber = TextBoxHandler.IsNullOrEmpty(String.valueOf(DevList.get(0).TotalCount));
                            break;
                        default:
                            list = new parseSearchAgent().parse((String) response, list);
                            TotalNumber = TextBoxHandler.IsNullOrEmpty(list.get(0).TotalCount);
                            break;
                    }*/
                    //endregion
                    list = new ParseDirectory().parse((String) response, list);
                    TotalNumber = TextBoxHandler.IsNullOrEmpty(list.get(0).TotalCount);
                    Bind();
                    progressBarDirectory.setVisibility(View.GONE);
                } catch (Exception e) {
                    progressBarDirectory.setVisibility(View.GONE);
                    MyLocation();
                    Log.e("error", e.getMessage());
                }
            }

            @Override
            public void RequestFailed(String response) {
                progressBarDirectory.setVisibility(View.GONE);

            }
        }).MakeJsonArrayReq(URL);
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    void initMap() {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gmap = googleMap;

                usermarker = new MarkerOptions().position(new LatLng(CURRENT_latitude, CURRENT_longitude)).title(LOCATION_NAME);
                usermarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_me));

                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(CURRENT_latitude, CURRENT_longitude)).zoom(13).build();//zoom level was 15. changed to 19
                gmap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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
                    }).execute();
                }

                gmap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        double latitude = cameraPosition.target.latitude;
                        double longitude = cameraPosition.target.longitude;
                        getSetMapLocations(String.valueOf(latitude), String.valueOf(longitude));

                        lnrPagers.setVisibility(View.GONE);
                        imgMyLocation.setVisibility(View.VISIBLE);
                        imgSatteliteView.setVisibility(View.VISIBLE);

                    }
                });

                gmap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (!marker.getTitle().equals(usermarker.getTitle())) {
                            DirectoryInfoWindow(marker.getTitle());
                        }


                        return true;
                    }
                });
                gmap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {

                        lnrPagers.setVisibility(View.GONE);
                        imgMyLocation.setVisibility(View.VISIBLE);
                        imgSatteliteView.setVisibility(View.VISIBLE);

                    }
                });
            }
        });

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
                                    countryTicker = cc.CountryTicker;
                                    LOCATION_NAME = cc.Name;
                                }
                            }
                            CityName = jo.getString("CityName");
                            LocalityName = jo.getString("LocalityName");
                            getFeaturedData(0, "");
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


    void MyLocation() {
        usermarker = new MarkerOptions().position(new LatLng(CURRENT_latitude, CURRENT_longitude)).title(LOCATION_NAME);
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


    public void ChangeLocation(double lat, double lon) {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat, lon)).zoom(15).build();//zoom level was 15. changed to 19
        gmap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public boolean CheckInfoWindow() {
        if (lnrPagers.getVisibility() == View.GONE) {
            return true;
        } else {
            lnrPagers.setVisibility(View.GONE);
            imgMyLocation.setVisibility(View.VISIBLE);
            imgSatteliteView.setVisibility(View.VISIBLE);
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DIRECTORY_FILTER_REQUEST) {
            if (resultCode == DIRECTORY_FILTER_REQUEST) {
                filters = (SearchDirectoryFilters) data.getParcelableExtra("DirectoryFilterResults");
                getFeaturedData(pageNumber, AppGlobal.CurrentCountryID);
            }
        }
    }


    void DrawPoints() {
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).Latitude > 0.0) {
                    MarkerOptions marker = new MarkerOptions().position(
                            new LatLng(list.get(i).Latitude,
                                    list.get(i).Longitude)).title(
                            list.get(i).name);
                    /*if (list.get(i).UserCategoryID == 1) {
                        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_icon_agents));
                    }*/
                    gmap.addMarker(getMarker(marker, i));
                }
            }
            agentPager.setAdapter(new DirectoryPagerAgentAdapter(context, list, new IProperty() {
                @Override
                public void btnFavorite_click(Object obj) {
                }

                @Override
                public void onClick(Object obj) {
                }
            }));
        }
    }

    MarkerOptions getMarker(MarkerOptions marker, int position) {

        switch (list.get(position).UserCategoryID) {
            case 1:
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_icon_agents));
                break;
            case 2:
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_icon_brokerage));
                break;
            case 4:
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_icon_developers));
                break;
            default:
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_icon_agents));
                break;
        }

        return marker;
    }

    void DirectoryInfoWindow(String Name) {
        agentPager.setVisibility(View.VISIBLE);
        //brokeragePager.setVisibility(View.GONE);
        //developerPager.setVisibility(View.GONE);
        imgMyLocation.setVisibility(View.GONE);
        imgSatteliteView.setVisibility(View.GONE);
        lnrPagers.setVisibility(View.VISIBLE);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).name.equals(Name)) {
                agentPager.setCurrentItem(i, false);
                CurrentNumber = i;
            }
        }
        lblTotalAmount.setText((CurrentNumber + 1) + " of " + TotalNumber);
        agentPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                CurrentNumber = position;
                lblTotalAmount.setText((CurrentNumber + 1) + " of " + TotalNumber);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CurrentNumber <= Integer.valueOf(TotalNumber)) {
                    CurrentNumber++;
                    agentPager.setCurrentItem(CurrentNumber, true);
                } else if (CurrentNumber >= Integer.valueOf(TotalNumber)) {
                    CurrentNumber = Integer.valueOf(TotalNumber);
                    agentPager.setCurrentItem(CurrentNumber, true);
                }
            }
        });
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CurrentNumber >= 0) {
                    CurrentNumber--;
                    agentPager.setCurrentItem(CurrentNumber, true);
                } else if (CurrentNumber < 0) {
                    CurrentNumber = 0;
                    agentPager.setCurrentItem(CurrentNumber, true);
                }
            }
        });
    }


}
