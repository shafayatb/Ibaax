package com.ibaax.com.ibaax;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.bluejamesbond.text.DocumentView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Adapter.ActiveListiningAdapter;
import Adapter.AgentInformationAdapter;
import Adapter.AgentReviewAdapter;
import Adapter.TextViewBackgroundGridAdapter;
import DataAccessLayer.RecentlyViewedLayer;
import Entity.ActiveListing;
import Entity.AgentInformation;
import Entity.AgentReview;
import Entity.AppGlobal;
import Event.IEvent;
import Event.IHttpResponse;
import Event.IProperty;
import JSOINParsing.parseAgent;
import Plugins.JSONParser;
import Popup.ReportAgentDialog;
import ServiceInvoke.AddfavoriteAgentAgency;
import ServiceInvoke.DownloadBitmap;
import ServiceInvoke.HttpRequest;
import UI.MessageBox;

public class DeveloperProfileActivity extends AppCompatActivity {

    List<ActiveListing> ActiveListingList = new ArrayList<>();
    List<String> coverageares = new ArrayList<>();
    List<String> specialities = new ArrayList<>();
    List<AgentInformation> ProInfoList = new ArrayList<>();
    List<AgentInformation> ConInfoList = new ArrayList<>();
    List<AgentReview> ReviewList = new ArrayList<>();

    ImageView img;
    NetworkImageView netYoutube;
    Context context;
    TextView lblPosition, lblID;
    TextView lblMoreActiveListing, lblMoreReview;
    UI.ExpandableHeightGridView gridActiveListings, gridServiceArea, gridSpecialities, gridConInfo, gridReview;

    LinearLayout lnrSpecialities, lnrAreas, lnrSummery, lnrActiveListing, lnrContentDev, lnrMap, lnrConInfo, lnrVideo, lnrReview;
    String website;
    String phone, FullAddress, membersince, ActiveAgents, ActiveListings;
    String companyID;
    String companyName;
    String ShortListParam;
    String RecentlyViewedType;
    double Latitude, Longitude;
    Boolean Favorite;
    RelativeLayout relYoutube;
    GoogleMap googleMap;
    String YoutubeURL = "";
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_brokerage_profile);

        context = this;

        companyName = getIntent().getStringExtra("companyName");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("Name");
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.developer_profile_toolbar);
        collapsingToolbar.setTitle(companyName);
        init();
        BindMap();
        DeveloperViewed();

    }

    void BindMap() {

        try {
            if (googleMap == null) {
                googleMap = ((MapFragment) getFragmentManager()
                        .findFragmentById(R.id.map_developerprofile)).getMap();
            }
            if (Longitude == 0.0 && Latitude == 0.0) {
                lnrMap.setVisibility(View.GONE);

            } else {
                lnrMap.setVisibility(View.VISIBLE);
                final MarkerOptions marker = new MarkerOptions().position(new LatLng(Latitude, Longitude)).title(companyName);
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_icon_agents));
                googleMap.addMarker(marker).showInfoWindow();

                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Latitude, Longitude)).zoom(15).build();//zoom level was 15. changed to 19
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.getUiSettings().setScrollGesturesEnabled(false);
                googleMap.getUiSettings().setZoomGesturesEnabled(false);
            }

        } catch (Exception ex) {

            Log.v("error", "BindMap Property Detail: " + ex.getMessage());
            lnrMap.setVisibility(View.GONE);

        }


    }

    private void init() {
        try {
            Intent intent = getIntent();
            img = (ImageView) findViewById(R.id.img_developer_image);

            lnrSpecialities = (LinearLayout) findViewById(R.id.lnr_developerprofile_specialities);
            lnrAreas = (LinearLayout) findViewById(R.id.lnr_developerprofile_areas);
            lnrSummery = (LinearLayout) findViewById(R.id.lnr_developerprofile_summary);
            lnrActiveListing = (LinearLayout) findViewById(R.id.lnr_developerprofile_activelisting);
            lblMoreActiveListing = (TextView) findViewById(R.id.lbl_developerprofile_moreactivelisting);
            lnrContentDev = (LinearLayout) findViewById(R.id.lnr_content_developer);
            lnrMap = (LinearLayout) findViewById(R.id.lnr_developerprofile_map);

            lnrConInfo = (LinearLayout) findViewById(R.id.lnr_developerprofile_coninfo);
            lnrVideo = (LinearLayout) findViewById(R.id.lnr_developerprofile_video);
            lnrReview = (LinearLayout) findViewById(R.id.lnr_developerprofile_reviews);
            netYoutube = (NetworkImageView) findViewById(R.id.img_developer_youtube);
            relYoutube = (RelativeLayout) findViewById(R.id.rel_developerprofile_yoitube);

            phone = intent.getStringExtra("phone");
            String AboutTheCompany = intent.getStringExtra("AboutTheCompany");
            String URL = intent.getStringExtra("ImageUrl");
            companyID = intent.getStringExtra("companyID");
            Favorite = intent.getBooleanExtra("favorite", false);
            FullAddress = intent.getStringExtra("fulladdress");
            membersince = intent.getStringExtra("membersince");
            ActiveAgents = intent.getStringExtra("agntcount");
            ActiveListings = intent.getStringExtra("actvlisting");
            ShortListParam = intent.getStringExtra("ShortListParam");
            String disID = intent.getStringExtra("dispID");
            Latitude = intent.getDoubleExtra("lat", 0.0);
            Longitude = intent.getDoubleExtra("lon", 0.0);
            website = intent.getStringExtra("website");
            RecentlyViewedType = intent.getStringExtra("Recent");

            lblID = (TextView) findViewById(R.id.lbl_developer_profile_ibaaxid);
            lblID.setText("iBaax ID : " + disID);

            if (AboutTheCompany.trim().length() > 0) {
                lnrSummery.setVisibility(View.VISIBLE);
                DocumentView Description;
                Description = (DocumentView) findViewById(R.id.docview_developer_desc);
                Description.setText(Html.fromHtml(AboutTheCompany).toString());
            }

            DownloadImage(URL);

            lblPosition = (TextView) findViewById(R.id.lbl_developer_location);
            lblPosition.setText("Real Estate Company");

            gridActiveListings = (UI.ExpandableHeightGridView) findViewById(R.id.gridview_developer_property);
            gridServiceArea = (UI.ExpandableHeightGridView) findViewById(R.id.gridview_developerprofile_area);
            gridSpecialities = (UI.ExpandableHeightGridView) findViewById(R.id.gridview_developerprofile_specialites);

            gridConInfo = (UI.ExpandableHeightGridView) findViewById(R.id.gridview_developerprofile_coninfo);
            gridReview = (UI.ExpandableHeightGridView) findViewById(R.id.gridview_developerprofile_reviews);

            DisplayMetrics displaymetrics = new DisplayMetrics();
            this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int screenHeight = displaymetrics.heightPixels;

            int actionBarHeight = 48;
            TypedValue tv = new TypedValue();
            if (this.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics()) + 80;
            }

            lnrContentDev.setMinimumHeight(screenHeight - actionBarHeight);
            HttpAgentArea();
            HttpAgentSpecialities();
            HttpActiveListing();
            BindConInfo();
            HttpVideo();
            HttpReview();
        } catch (Exception ex) {

            MessageBox.Show(this, ex.getMessage());
        }
    }

    void HttpActiveListing() {
        String URL = AppGlobal.localHost + "/api/Mcontacts/GetBrokerageActiveListing?companyID=" + companyID + "&userId=" +
                new AppPrefs(this).getUserID() + "&lang='en'";
        Log.v("url", URL);
        new HttpRequest(this, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {

                ActiveListingList = new parseAgent().parseActiveListining((String) response, ActiveListingList);

                BindActiveListing();
            }

            @Override
            public void RequestFailed(String response) {

            }
        }).MakeJsonArrayReq(URL);

    }

    void BindActiveListing() {
        try {
            if (ActiveListingList.size() > 0) {
                lnrActiveListing.setVisibility(View.VISIBLE);
                List<ActiveListing> list = new ArrayList<>();
                if (ActiveListingList.size() > 2) {
                    list = ActiveListingList.subList(0, 2);
                    ActiveListiningAdapter adapter = new ActiveListiningAdapter(this, list, new IEvent() {
                        @Override
                        public void onClick(Object obj) {

                        }
                    }, "For Sale");
                    gridActiveListings.setAdapter(adapter);
                    gridActiveListings.setExpanded(true);
                } else {
                    ActiveListiningAdapter adapter = new ActiveListiningAdapter(this, ActiveListingList, new IEvent() {
                        @Override
                        public void onClick(Object obj) {

                        }
                    }, "For Sale");
                    gridActiveListings.setAdapter(adapter);
                    gridActiveListings.setExpanded(true);
                    lblMoreActiveListing.setVisibility(View.GONE);
                }

            }
        } catch (Exception e) {
            Log.e("esty", e.getMessage());
        }
    }

    void HttpAgentArea() {
        String URL = AppGlobal.localHost + "/api/Mcontacts/GetBrokerageAreas?companyID=" + companyID + "&lang='en'";
        Log.v("url", URL);
        new HttpRequest(context, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {

                try {
                    JSONArray array = new JSONArray((String) response);
                    for (int i = 0; i < array.length(); i++) {
                        try {
                            JSONObject jo = array.getJSONObject(i);
                            String CompetencyName = JSONParser.parseString(jo, "ServiceAreas");
                            coverageares.add(CompetencyName);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception ex) {

                }
                if (coverageares.size() > 0) {
                    lnrAreas.setVisibility(View.VISIBLE);
                    gridServiceArea.setAdapter(new TextViewBackgroundGridAdapter(context, coverageares));
                    gridServiceArea.setExpanded(true);
                }
            }

            @Override
            public void RequestFailed(String response) {

            }
        }).MakeJsonArrayReq(URL);


    }

    void HttpAgentSpecialities() {
        //String URL = AppGlobal.localHost + "/api/MContacts/GetBrokerageSpecialities?companyID=" + companyID + "&lang='en'";
        String URL = AppGlobal.localHost + "/api/MContacts/GetCompanySpecialities?companyID=" + companyID + "&lang='en'";
        Log.v("url", URL);
        new HttpRequest(context, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {

                //specialities = new parseAgent().Specialites((String) response, specialities);
                try {
                    JSONArray array = new JSONArray((String) response);
                    for (int i = 0; i < array.length(); i++) {
                        try {
                            JSONObject jo = array.getJSONObject(i);
                            if (JSONParser.parseString(jo, "IsSelected").equals("True")) {
                                String CompetencyName = JSONParser.parseString(jo, "Name");
                                specialities.add(CompetencyName);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                } catch (Exception ex) {

                }
                if (specialities.size() > 0) {
                    lnrSpecialities.setVisibility(View.VISIBLE);
                    gridSpecialities.setAdapter(new TextViewBackgroundGridAdapter(context, specialities));
                    gridSpecialities.setExpanded(true);
                }
            }

            @Override
            public void RequestFailed(String response) {

            }
        }).MakeJsonArrayReq(URL);


    }


    void BindConInfo() {
        lnrConInfo.setVisibility(View.VISIBLE);
        ConInfoList.add(new AgentInformation("Member Since", membersince, "Big"));
        ConInfoList.add(new AgentInformation("Active Listings", ActiveListings, "Big"));
        ConInfoList.add(new AgentInformation("Active Agents", ActiveAgents, "Big"));
        if (phone.length() > 0) {
            ConInfoList.add(new AgentInformation("Phone No", phone, "Call"));
        }
        if (FullAddress.length() > 0) {
            ConInfoList.add(new AgentInformation("Address", FullAddress, "Small"));
        }

        gridConInfo.setAdapter(new AgentInformationAdapter(this, ConInfoList));
        gridConInfo.setExpanded(true);
    }

    void HttpVideo() {

        String URL = AppGlobal.localHost + "/api/MContacts/GetCompanyVideo?companyId=" + companyID + "&lang=en";

        new HttpRequest(context, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {
                try {
                    JSONArray VidArr = new JSONArray((String) response);
                    if (VidArr.length() > 0) {
                        JSONObject VidOBJ = VidArr.getJSONObject(0);
                        YoutubeURL = VidOBJ.getString("YouTubeVideoLink");
                        netYoutube.setImageUrl(VidOBJ.getString("YouTubeVideoThumbnailLink"), AppController.getInstance().getImageLoader());
                        lnrVideo.setVisibility(View.VISIBLE);
                        relYoutube.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(YoutubeURL)));
                            }
                        });
                    }


                } catch (JSONException e) {

                }
            }

            @Override
            public void RequestFailed(String response) {

            }
        }).MakeJsonArrayReq(URL);

    }

    void HttpReview() {
        String URL = AppGlobal.localHost + "/api/MContacts/GetReviewByCompanyId?companyId=" + companyID + "&lang=en";

        new HttpRequest(context, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {
                try {
                    ReviewList = new parseAgent().Reviews((String) response, ReviewList);

                    BindReview();
                } catch (Exception e) {

                }
            }

            @Override
            public void RequestFailed(String response) {

            }
        }).MakeJsonArrayReq(URL);
    }

    void BindReview() {

        try {
            if (ReviewList.size() > 0) {
                lnrReview.setVisibility(View.VISIBLE);
                List<AgentReview> list = new ArrayList<>();
                if (ReviewList.size() > 2) {
                    list = ReviewList.subList(0, 2);
                    AgentReviewAdapter adapter = new AgentReviewAdapter(this, list);
                    gridReview.setAdapter(adapter);
                    gridReview.setExpanded(true);
                } else {
                    AgentReviewAdapter adapter = new AgentReviewAdapter(this, ReviewList);
                    gridReview.setAdapter(adapter);
                    gridReview.setExpanded(true);
                    lblMoreReview.setVisibility(View.GONE);
                }

            }
        } catch (Exception e) {
            Log.e("esty", e.getMessage());
        }

    }

    public void btnSMS_click(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phone));
        intent.putExtra("sms_body", "");
        startActivity(intent);

    }

    public void btnPhone_click(View view) {

        String number = "tel:" + phone;
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
        startActivity(callIntent);
    }

    public void btnEmail_click(View view) {
        Intent intent = new Intent(this, MessageActivity.class);
        startActivity(intent);

    }

    private void DownloadImage(String path) {

        new DownloadBitmap
                (context, new IHttpResponse() {
                    @Override
                    public void RequestSuccess(Object response) {


                        Bitmap bitmap = (Bitmap) response;
                        img.setImageBitmap(bitmap);
                    }

                    @Override
                    public void RequestFailed(String response) {

                        String responselocal = response;

                    }
                }).Download(path);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_agent, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        if (Favorite) {
            menu.findItem(R.id.action_favorite).setIcon(getResources().getDrawable(R.drawable.ic_action_favorite));
        } else {
            menu.findItem(R.id.action_favorite).setIcon(getResources().getDrawable(R.drawable.ic__action_favorite_outline));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

            share.putExtra(Intent.EXTRA_SUBJECT, "");
            share.putExtra(Intent.EXTRA_TEXT, website);

            startActivity(Intent.createChooser(share, "Share Company Profile"));
            return true;
        }
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.action_favorite) {
            try {
                AppPrefs pref = new AppPrefs(context);
                String UserID = pref.getUserID();

                if (UserID.length() > 1) {
                    new AddfavoriteAgentAgency(context, new IProperty() {
                        @Override
                        public void btnFavorite_click(Object obj) {

                        }

                        @Override
                        public void onClick(Object obj) {
                            //listener.onClick(obj);
                        }
                    }).Post(companyID + "",
                            ShortListParam,
                            AppGlobal.localHost + "/api/MContacts/AddAgencyFavourite");

                } else {

                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                    finish();
                }
                if (Favorite) {
                    Favorite = false;
                    menu.findItem(R.id.action_favorite).setIcon(getResources().getDrawable(R.drawable.ic__action_favorite_outline));
                    //imgIsLiked.setImageResource(R.drawable.ic__action_favorite_outline);
                } else {
                    Favorite = true;
                    menu.findItem(R.id.action_favorite).setIcon(getResources().getDrawable(R.drawable.ic_action_favorite));
                    //imgIsLiked.setImageResource(R.drawable.ic_action_favorite);
                }

            } catch (Exception e) {

            }

            return true;
        }
        if (id == R.id.action_flag) {
            new ReportAgentDialog(this, Integer.parseInt(companyID), "CompanyID",
                    "AgencyReportTypeID", "AgencyReportID", AppGlobal.localHost + "/api/MContacts/ReportAgency", new IEvent() {
                @Override
                public void onClick(Object obj) {

                }
            }).PopupReport();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void DeveloperViewed() {
        try {
            RecentlyViewedLayer recentlyViewedLayer = new RecentlyViewedLayer(this);
            List<Integer> developerIDList = new ArrayList<>();
            developerIDList = recentlyViewedLayer.getRecentlyViewed(RecentlyViewedType);
            boolean IDAlreadyExists = false;
            for (int i = 0; i < developerIDList.size(); i++) {
                if (Integer.parseInt(companyID) == developerIDList.get(i)) {
                    IDAlreadyExists = true;
                    break;
                }
                Log.v("viewed", "DeveloperProfileActivity2/PropertyViewed->Data: " + developerIDList.get(i));
            }
            if (!IDAlreadyExists) {

                if (developerIDList.size() < 10) {
                    recentlyViewedLayer.Insert(Integer.parseInt(companyID), RecentlyViewedType);
                } else {
                    recentlyViewedLayer.deleteLastItem(developerIDList.get(0));
                    recentlyViewedLayer.Insert(Integer.parseInt(companyID), RecentlyViewedType);
                }
            }
            Log.v("viewed", "DeveloperProfileActivity2/AgentViewed->Success: Data Inserted Successfully");
        } catch (Exception e) {
            Log.e("viewed", "DeveloperProfileActivity2/AgentViewed->Error: " + e.getMessage());
        }

    }


}
