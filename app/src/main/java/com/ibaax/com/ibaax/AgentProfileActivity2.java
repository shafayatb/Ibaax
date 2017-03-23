package com.ibaax.com.ibaax;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
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
import Entity.Agent;
import Entity.AgentInformation;
import Entity.AgentReview;
import Entity.AppGlobal;
import Event.IEvent;
import Event.IHttpResponse;
import Event.IProperty;
import JSOINParsing.parseAgent;
import Popup.ReportAgentDialog;
import ServiceInvoke.AddfavoriteAgentAgency;
import ServiceInvoke.DownloadBitmap;
import ServiceInvoke.HttpRequest;
import UI.MessageBox;

public class AgentProfileActivity2 extends AppCompatActivity {

    List<ActiveListing> ActiveListingList = new ArrayList<>();
    List<AgentReview> ReviewList = new ArrayList<>();
    List<AgentInformation> ProInfoList = new ArrayList<>();
    List<AgentInformation> ConInfoList = new ArrayList<>();
    List<String> coverageares = new ArrayList<>();
    List<String> specialities = new ArrayList<>();

    Agent agent;
    Context context;
    TextView lblPosition, lblID;
    TextView lblMoreActiveListing, lblMoreReview;
    ImageView imgProfile;
    NetworkImageView netYoutube;
    UI.ExpandableHeightGridView gridActiveListings, gridServiceArea, gridSpecialities, gridProInfo, gridConInfo, gridReview;

    LinearLayout lnrSpecialities, lnrAreas, lnrSummery, lnrActiveListing, lnrContentAgent, lnrMap, lnrProInfo, lnrConInfo, lnrVideo, lnrReview;
    RelativeLayout relYoutube;
    GoogleMap googleMap;
    String YoutubeURL = "";
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_profile2);
        getWindow().setBackgroundDrawable(null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("Name");
        agent = (Agent) getIntent().getSerializableExtra("Agent");
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.agent_profile_toolbar);
        collapsingToolbar.setTitle(agent.name);
        //((TextView)collapsingToolbar.getRootView().findViewById(android.R.id.title)).setText(agent.name);

        context = this;
        imgProfile = (ImageView) findViewById(R.id.img_agent_profile_image);
        init();

        DownloadProfile();
        AgentViewed();
    }

    private void DownloadProfile() {

        new DownloadBitmap
                (context, new IHttpResponse() {
                    @Override
                    public void RequestSuccess(Object response) {

                        Bitmap bitmap = (Bitmap) response;
                        imgProfile.setImageBitmap(bitmap);
                    }

                    @Override
                    public void RequestFailed(String response) {

                        String responselocal = response;

                    }
                }).Download(agent.agentThumbnailUrl.replace(" ", "%20"));

    }

    void BindMap() {

        try {
            if (googleMap == null) {
                googleMap = ((MapFragment) getFragmentManager()
                        .findFragmentById(R.id.map_agentprofile)).getMap();
            }
            if (agent.Longitude == 0.0 && agent.Latitude == 0.0) {
                lnrMap.setVisibility(View.GONE);

            } else {
                lnrMap.setVisibility(View.VISIBLE);
                final MarkerOptions marker = new MarkerOptions().position(new LatLng(agent.Latitude, agent.Longitude)).title(agent.name);
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_icon_agents));
                googleMap.addMarker(marker).showInfoWindow();

                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(agent.Latitude, agent.Longitude)).zoom(15).build();//zoom level was 15. changed to 19
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
            lnrSpecialities = (LinearLayout) findViewById(R.id.lnr_agentprofile_specialities);
            lnrAreas = (LinearLayout) findViewById(R.id.lnr_agentprofile_areas);
            lnrSummery = (LinearLayout) findViewById(R.id.lnr_agentprofile_summary);
            lnrActiveListing = (LinearLayout) findViewById(R.id.lnr_agentprofile_activelisting);
            lnrContentAgent = (LinearLayout) findViewById(R.id.lnr_content_agent);
            lnrProInfo = (LinearLayout) findViewById(R.id.lnr_agentprofile_proinfo);
            lnrConInfo = (LinearLayout) findViewById(R.id.lnr_agentprofile_coninfo);
            lnrVideo = (LinearLayout) findViewById(R.id.lnr_agentprofile_video);
            lnrReview = (LinearLayout) findViewById(R.id.lnr_agentprofile_reviews);
            relYoutube = (RelativeLayout) findViewById(R.id.rel_yoitube);

            lblMoreActiveListing = (TextView) findViewById(R.id.lbl_agentprofile_moreactivelisting);
            lblMoreReview = (TextView) findViewById(R.id.lbl_agentprofile_morereviews);

            lblPosition = (TextView) findViewById(R.id.lbl_agent_profile_position);
            lblPosition.setText(agent.Position);

            lblID = (TextView) findViewById(R.id.lbl_agent_profile_ibaaxid);
            lblID.setText("iBaax ID : " + agent.DisplayID);

            gridServiceArea = (UI.ExpandableHeightGridView) findViewById(R.id.gridview_agent_profile_service);
            gridSpecialities = (UI.ExpandableHeightGridView) findViewById(R.id.gridview_agent_profile_specialites);
            gridActiveListings = (UI.ExpandableHeightGridView) findViewById(R.id.gridview_agent_property);
            gridProInfo = (UI.ExpandableHeightGridView) findViewById(R.id.gridview_agent_profile_proinfo);
            gridConInfo = (UI.ExpandableHeightGridView) findViewById(R.id.gridview_agent_profile_coninfo);
            gridReview = (UI.ExpandableHeightGridView) findViewById(R.id.gridview_agent_reviews);

            if (agent.ProfileSummary.trim().length() > 0) {
                lnrSummery.setVisibility(View.VISIBLE);

                DocumentView Description;
                Description = (DocumentView) findViewById(R.id.docview_desc);
                Description.setText(Html.fromHtml(agent.ProfileSummary).toString());
            }
            DisplayMetrics displaymetrics = new DisplayMetrics();
            this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int screenHeight = displaymetrics.heightPixels;

            int actionBarHeight = 48;
            TypedValue tv = new TypedValue();
            if (this.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics()) + 80;
            }

            lnrContentAgent.setMinimumHeight(screenHeight - actionBarHeight);
            lnrMap = (LinearLayout) findViewById(R.id.lnr_agentprofile_map);

            netYoutube = (NetworkImageView) findViewById(R.id.img_agent_youtube);

            BindProInfo();
            BindConInfo();
            BindMap();

            HttpAgentArea();
            HttpAgentSpecialities();
            HttpActiveListing();
            HttpVideo();
            HttpReview();


        } catch (Exception ex) {

            MessageBox.Show(this, ex.getMessage());
        }
    }

    void HttpActiveListing() {
        String URL = AppGlobal.localHost + "/api/Mcontacts/GetAgentActiveListing?contactID=" + agent.ContactID + "&userId=" +
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
        String URL = AppGlobal.localHost + "/api/User/GetMyServiceArea?contactId=" + agent.ContactID + "&lang='en'";
        Log.v("url", URL);
        new HttpRequest(context, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {

                coverageares = new parseAgent().CoverageArea((String) response, coverageares);
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
        String URL = AppGlobal.localHost + "/api/Mcontacts/GetContactSpecialities?contactID=" + agent.ContactID + "&lang='en'";
        Log.v("url", URL);
        new HttpRequest(context, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {

                specialities = new parseAgent().Specialites((String) response, specialities);
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

    void HttpVideo() {

        String URL = AppGlobal.localHost + "/api/MContacts/GetContactVideo?contactId=" + agent.ContactID + "&lang=en";

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
        String URL = AppGlobal.localHost + "/api/MContacts/GetReviewByContactId?contactId=" + agent.ContactID + "&lang=en";

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

    void BindProInfo() {
        lnrProInfo.setVisibility(View.VISIBLE);
        // ProInfoList.add(new AgentInformation("Experience (Years)", "", "Big"));
        ProInfoList.add(new AgentInformation("Member Since", agent.MemberSince, "Big"));
        ProInfoList.add(new AgentInformation("Active Listings", String.valueOf(agent.activeListing), "Big"));
        //ProInfoList.add(new AgentInformation("Past Sales", "", "Big"));
        if (agent.AgentLicenseNumber.length() > 0) {
            ProInfoList.add(new AgentInformation("Licenses", agent.AgentLicenseNumber, "Small"));
        }
        //ProInfoList.add(new AgentInformation("Language", "", "Small"));
        gridProInfo.setAdapter(new AgentInformationAdapter(this, ProInfoList));
        gridProInfo.setExpanded(true);
    }

    void BindConInfo() {
        lnrConInfo.setVisibility(View.VISIBLE);
        if (agent.mobile.length() > 0) {
            ConInfoList.add(new AgentInformation("Phone No", agent.mobile, "Call"));
        }
        if (agent.fulladdress.length() > 0) {
            ConInfoList.add(new AgentInformation("Address", agent.fulladdress, "Small"));
        }
        //ConInfoList.add(new AgentInformation("Skype", "", "Small"));
        if (agent.agentPermaLink.length() > 0) {
            ConInfoList.add(new AgentInformation("Website", agent.agentPermaLink, "Small"));
        }
        gridConInfo.setAdapter(new AgentInformationAdapter(this, ConInfoList));
        gridConInfo.setExpanded(true);
    }

    public void btnSMS_click(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + agent.mobile));
        intent.putExtra("sms_body", "");
        startActivity(intent);

    }

    public void btnPhone_click(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Call " + agent.mobile + " ?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do some thing here which you need
                        String number = "tel:" + agent.mobile;
                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                        startActivity(callIntent);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    public void btnEmail_click(View view) {
        Intent intent = new Intent(this, MessageActivity.class);
        startActivity(intent);

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
        if (agent.IsLiked) {
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
            share.putExtra(Intent.EXTRA_TEXT, agent.agentPermaLink);

            startActivity(Intent.createChooser(share, "Share Agent Profile"));
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
                    }).Post(agent.ContactID + "",
                            "Agent",
                            AppGlobal.localHost + "/api/MContacts/AddAgentFavourite");

                } else {

                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                    finish();
                }
                if (agent.IsLiked) {
                    agent.IsLiked = false;
                    menu.findItem(R.id.action_favorite).setIcon(getResources().getDrawable(R.drawable.ic__action_favorite_outline));
                    //imgIsLiked.setImageResource(R.drawable.ic__action_favorite_outline);
                } else {
                    agent.IsLiked = true;
                    menu.findItem(R.id.action_favorite).setIcon(getResources().getDrawable(R.drawable.ic_action_favorite));
                    //imgIsLiked.setImageResource(R.drawable.ic_action_favorite);
                }

            } catch (Exception e) {

            }

            return true;
        }
        if (id == R.id.action_flag) {

            new ReportAgentDialog(this, agent.ContactID, "ContactID",
                    "AgentReportTypeID", "AgentReportID", AppGlobal.localHost + "/api/MContacts/ReportAgent", new IEvent() {
                @Override
                public void onClick(Object obj) {

                }
            }).PopupReport();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void AgentViewed() {
        try {
            RecentlyViewedLayer recentlyViewedLayer = new RecentlyViewedLayer(this);
            List<Integer> agentIDList = new ArrayList<>();
            agentIDList = recentlyViewedLayer.getRecentlyViewed("Agent");
            boolean IDAlreadyExists = false;
            for (int i = 0; i < agentIDList.size(); i++) {
                if ((int) agent.ContactID == agentIDList.get(i)) {
                    IDAlreadyExists = true;
                    break;
                }
                Log.v("viewed", "AgentProfileActivity2/PropertyViewed->Data: " + agentIDList.get(i));
            }
            if (!IDAlreadyExists) {

                if (agentIDList.size() < 10) {
                    recentlyViewedLayer.Insert((int) agent.ContactID , "Agent");
                } else {
                    recentlyViewedLayer.deleteLastItem(agentIDList.get(0));
                    recentlyViewedLayer.Insert((int) agent.ContactID , "Agent");
                }
            }
            Log.v("viewed", "AgentProfileActivity2/AgentViewed->Success: Data Inserted Successfully");
        } catch (Exception e) {
            Log.e("viewed", "AgentProfileActivity2/AgentViewed->Error: " + e.getMessage());
        }

    }
}
