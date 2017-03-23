package com.ibaax.com.ibaax;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
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

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.bluejamesbond.text.DocumentView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import Adapter.ActiveListiningAdapter;
import Adapter.AgentInformationAdapter;
import Adapter.TextViewBackgroundGridAdapter;
import Entity.ActiveListing;
import Entity.AgentInformation;
import Entity.AppGlobal;
import Entity.User;
import Event.IEvent;
import Event.IHttpResponse;
import JSOINParsing.parseAgent;
import JSOINParsing.parseUser;
import Plugins.TextBoxHandler;
import ServiceInvoke.HttpRequest;

public class MyProfileActivity2 extends AppCompatActivity {

    List<User> UserInfoList = new ArrayList<>();
    Context context;
    AppPrefs pref;
    ImageLoader loader = AppController.getInstance().getImageLoader();
    GoogleMap googleMap;
    ImageView ProfilePic, CoverPic;
    RelativeLayout relCoverImage;
    TextView myPosition, myIbaaxID;
    LinearLayout lnrSpecialities, lnrAreas, lnrSummery, lnrActiveListing, lnrContentAgent, lnrMap,
            lnrProInfo, lnrConInfo, lnrCompanyInfo;
    UI.ExpandableHeightGridView gridActiveListings, gridServiceArea, gridSpecialities, gridProInfo, gridConInfo,
            gridCompanyInfo;
    List<ActiveListing> ActiveListingList = new ArrayList<>();
    List<AgentInformation> ProInfoList = new ArrayList<>();
    List<AgentInformation> ConInfoList = new ArrayList<>();
    List<String> coverageares = new ArrayList<>();
    List<String> specialities = new ArrayList<>();
    ProgressDialog progressDialog;
    CollapsingToolbarLayout collapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.myprofile_toolbar);
        getWindow().setBackgroundDrawable(null);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        pref = new AppPrefs(context);
        progressDialog = new ProgressDialog(context);
        collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.my_profile_toolbar);

        ProfilePic = (ImageView) findViewById(R.id.img_my_profile_image);
        CoverPic = (ImageView) findViewById(R.id.my_backdrop);
        relCoverImage = (RelativeLayout) findViewById(R.id.upload_cover_image);
        //getMyImage();

    }

    void HttpGetUser() {

        if (pref.getUserCategory().equals("2") || pref.getUserCategory().equals("4")) {
            String URL = AppGlobal.localHost + "/api/MCompany/GetCompanyInfoDetails?companyId=" +
                    pref.getCompanyID() + "&userCategoryId=" + pref.getUserCategory();
            progressDialog.show();
            new HttpRequest(context, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {
                    UserInfoList = new parseUser().parse((String) response, UserInfoList, context);
                    init();
                    BindMap();
                    getMyImage();
                    progressDialog.hide();

                }

                @Override
                public void RequestFailed(String response) {

                }
            }).MakeJsonArrayReq(URL);
        } else {
            String URL = AppGlobal.localHost + "/api/User/GetUserPersonalInfo?userId=" + pref.getUserID()
                    + "&lang=en";
            progressDialog.show();
            new HttpRequest(context, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {
                    UserInfoList = new parseUser().parse((String) response, UserInfoList, context);
                    init();
                    BindMap();
                    getMyImage();
                    progressDialog.hide();

                }

                @Override
                public void RequestFailed(String response) {

                }
            }).MakeJsonArrayReq(URL);
        }

    }

    void BindMap() {

        try {
            lnrMap = (LinearLayout) findViewById(R.id.lnr_myprofile_map);
            if (googleMap == null) {
                googleMap = ((MapFragment) getFragmentManager()
                        .findFragmentById(R.id.map_myprofile)).getMap();
            }
            if (UserInfoList.get(0).Longitude == 0.0 && UserInfoList.get(0).Latitude == 0.0) {
                lnrMap.setVisibility(View.GONE);

            } else {
                lnrMap.setVisibility(View.VISIBLE);
                final MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(UserInfoList.get(0).Latitude, UserInfoList.get(0).Longitude)).title(pref.getName());
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_me));
                googleMap.addMarker(marker);

                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        new LatLng(UserInfoList.get(0).Latitude, UserInfoList.get(0).Longitude)).zoom(15).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.getUiSettings().setScrollGesturesEnabled(false);
                googleMap.getUiSettings().setZoomGesturesEnabled(false);
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        return true;
                    }
                });
            }

        } catch (Exception ex) {

            Log.v("error", "BindMap Property Detail: " + ex.getMessage());
            lnrMap.setVisibility(View.GONE);

        }


    }

    void init() {
        try {
            myPosition = (TextView) findViewById(R.id.lbl_my_profile_position);
            myIbaaxID = (TextView) findViewById(R.id.lbl_my_profile_ibaaxid);
            myPosition.setText(UserInfoList.get(0).UserCategoryName);
            myIbaaxID.setText("iBaax ID : " + UserInfoList.get(0).DisplayId);
            pref.setFirstName(UserInfoList.get(0).FirstName);
            pref.setLastName(UserInfoList.get(0).LastName);
            pref.setCompanyName(UserInfoList.get(0).FirstName);
            collapsingToolbar.setTitle(pref.getFirstName() + " " + pref.getLastName());
            gridServiceArea = (UI.ExpandableHeightGridView) findViewById(R.id.gridview_my_profile_service);
            gridSpecialities = (UI.ExpandableHeightGridView) findViewById(R.id.gridview_my_profile_specialites);
            gridActiveListings = (UI.ExpandableHeightGridView) findViewById(R.id.gridview_my_property);
            gridProInfo = (UI.ExpandableHeightGridView) findViewById(R.id.gridview_my_profile_proinfo);
            gridConInfo = (UI.ExpandableHeightGridView) findViewById(R.id.gridview_my_profile_coninfo);
            gridCompanyInfo = (UI.ExpandableHeightGridView) findViewById(R.id.gridview_my_profile_companyinfo);

            lnrSpecialities = (LinearLayout) findViewById(R.id.lnr_myprofile_specialities);
            lnrAreas = (LinearLayout) findViewById(R.id.lnr_myprofile_areas);
            lnrSummery = (LinearLayout) findViewById(R.id.lnr_myprofile_summary);
            lnrActiveListing = (LinearLayout) findViewById(R.id.lnr_myprofile_activelisting);
            lnrProInfo = (LinearLayout) findViewById(R.id.lnr_myprofile_proinfo);
            lnrConInfo = (LinearLayout) findViewById(R.id.lnr_myprofile_coninfo);
            lnrCompanyInfo = (LinearLayout) findViewById(R.id.lnr_myprofile_companyinfo);
            lnrContentAgent = (LinearLayout) findViewById(R.id.lnr_content_profile);

            if (UserInfoList.get(0).ProfileSummary.trim().length() > 0) {

                lnrSummery.setVisibility(View.VISIBLE);
                DocumentView Description;
                Description = (DocumentView) findViewById(R.id.mydocview_desc);
                Description.setText(Html.fromHtml(UserInfoList.get(0).ProfileSummary).toString());
            }
            DisplayMetrics displaymetrics = new DisplayMetrics();
            this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int screenHeight = displaymetrics.heightPixels;

            int actionBarHeight = 48;
            TypedValue tv = new TypedValue();
            if (this.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics()) + 40;
            }

            lnrContentAgent.setMinimumHeight(screenHeight - actionBarHeight);
            if (pref.getUserCategory().equals("2") || pref.getUserCategory().equals("4")) {
                BindCompanyInfo();
            } else {
                BindProInfo();
                BindConInfo();
            }

            //HttpAgentArea();
            HttpAgentSpecialities();
            HttpActiveListing();
            HttpAgentArea();
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
    }

    void getMyImage() {
        if (pref.getUserCategory().equals("2") || pref.getUserCategory().equals("4")) {

            if (UserInfoList.get(0).CoverPhotoFileName.length() > 0) {

                String SDCARD_COVERPATH = Environment.getExternalStorageDirectory().getPath() + "/" + "ibaax_cache" + "/" + TextBoxHandler.IsNullOrEmpty(CropCoverImageActivity.COVERNAME);
                final File Cover_f = new File(SDCARD_COVERPATH);
                CoverPic.setImageBitmap(BitmapFactory.decodeFile(Cover_f.getAbsolutePath()));
                loader.get(AppGlobal.ImageHost + "/CRM/Company/ShowCompanyCoverPhoto/" + pref.getCompanyID() + "?CoverPhotoFileName="
                                + UserInfoList.get(0).CoverPhotoFileName.replace(" ", "%20"),
                        new ImageLoader.ImageListener() {
                            @Override
                            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                Cover_f.delete();
                                if (response.getBitmap() != null) {
                                    CoverPic.setImageBitmap(response.getBitmap());
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });
            }
            String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath() + "/" + "ibaax_cache" + "/" + TextBoxHandler.IsNullOrEmpty(CropImageActivity.filename);
            final File f = new File(SDCARD_PATH);
            ProfilePic.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
            loader.get(AppGlobal.ImageHost + "/agency/showcompanylogo/" + pref.getCompanyID() + "?logofilename="
                            + TextBoxHandler.IsNullOrEmpty(CropImageActivity.filename).replace(" ", "%20"),
                    new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                            f.delete();
                            if (response.getBitmap() != null) {
                                ProfilePic.setImageBitmap(response.getBitmap());
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });


        } else {

            String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath() + "/" + "ibaax_cache" + "/" + TextBoxHandler.IsNullOrEmpty(CropImageActivity.filename);
            final File f = new File(SDCARD_PATH);
            ProfilePic.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
            loader.get(AppGlobal.ImageHost + "/CRM/Contact/ShowContactPhoto?contactId=" + pref.getContactID() + "&contactPhotoFileName="
                            + TextBoxHandler.IsNullOrEmpty(CropImageActivity.filename).replace(" ", "%20"),
                    new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                            f.delete();
                            if (response.getBitmap() != null) {
                                ProfilePic.setImageBitmap(response.getBitmap());
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

            if (UserInfoList.get(0).CoverPhotoFileName.length() > 0) {
                String SDCARD_COVERPATH = Environment.getExternalStorageDirectory().getPath() + "/" + "ibaax_cache" + "/" + TextBoxHandler.IsNullOrEmpty(CropCoverImageActivity.COVERNAME);
                final File Cover_f = new File(SDCARD_COVERPATH);
                CoverPic.setImageBitmap(BitmapFactory.decodeFile(Cover_f.getAbsolutePath()));
                loader.get(AppGlobal.ImageHost + "/CRM/Contact/ShowContactCoverPhoto/" + pref.getContactID() + "?CoverPhotoFileName="
                                + UserInfoList.get(0).CoverPhotoFileName.replace(" ", "%20"),
                        new ImageLoader.ImageListener() {
                            @Override
                            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                Cover_f.delete();
                                if (response.getBitmap() != null) {
                                    CoverPic.setImageBitmap(response.getBitmap());
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        });
            }
        }


        ProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder photodialog = new AlertDialog.Builder(context);

                photodialog.setMessage("Take a new photo or select one from gallery.")
                        .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(MyProfileActivity2.this, CropImageActivity.class);
                                intent.putExtra("Type", "Camera");
                                startActivity(intent);
                            }
                        }).setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MyProfileActivity2.this, CropImageActivity.class);
                        intent.putExtra("Type", "Gallery");
                        startActivity(intent);
                    }
                });
                AlertDialog alert = photodialog.create();
                alert.show();
            }
        });
        relCoverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder coverdialog = new AlertDialog.Builder(context);

                coverdialog.setTitle("Change Cover Photo").setMessage("Take a new photo or select one from gallery.")
                        .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(context, CropCoverImageActivity.class);
                                intent.putExtra("Type", "Camera");
                                startActivity(intent);
                            }
                        }).setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(context, CropCoverImageActivity.class);
                        intent.putExtra("Type", "Gallery");
                        startActivity(intent);
                    }
                });
                AlertDialog alert = coverdialog.create();
                alert.show();
            }
        });

    }

    void BindProInfo() {
        lnrProInfo.setVisibility(View.VISIBLE);

        String[] DateArr = UserInfoList.get(0).MemberSince.split("T");
        String MemberSince = DateArr[0];
        ProInfoList.add(new AgentInformation("Member Since", MemberSince, "Big"));
        if (UserInfoList.get(0).ActiveListing > 0) {
            ProInfoList.add(new AgentInformation("Active Listings", String.valueOf(UserInfoList.get(0).ActiveListing), "Big"));
        }

        if (UserInfoList.get(0).License.length() > 0) {
            ProInfoList.add(new AgentInformation("Licenses", "66236", "Small"));
        }
        if (UserInfoList.get(0).Languages.length() > 0) {
            ProInfoList.add(new AgentInformation("Language", "English, Bangla", "Small"));
        }
        gridProInfo.setAdapter(new AgentInformationAdapter(this, ProInfoList));
        gridProInfo.setExpanded(true);
    }

    void BindConInfo() {
        lnrConInfo.setVisibility(View.VISIBLE);
        ConInfoList.add(new AgentInformation("Phone No", UserInfoList.get(0).MobileCode + "" + UserInfoList.get(0).Phone, "Small"));
        ConInfoList.add(new AgentInformation("Address", UserInfoList.get(0).Address, "Small"));
        if (UserInfoList.get(0).SkypeAddress.length() > 0) {
            ConInfoList.add(new AgentInformation("Skype", UserInfoList.get(0).SkypeAddress, "Small"));
        }
        if (UserInfoList.get(0).PersonalBlogAddress.length() > 0) {
            ConInfoList.add(new AgentInformation("Website", UserInfoList.get(0).PersonalBlogAddress, "Small"));
        }
        gridConInfo.setAdapter(new AgentInformationAdapter(this, ConInfoList));
        gridConInfo.setExpanded(true);
    }

    void BindCompanyInfo() {
        lnrCompanyInfo.setVisibility(View.VISIBLE);
        String[] DateArr = UserInfoList.get(0).MemberSince.split("T");
        String MemberSince = DateArr[0];
        ConInfoList.add(new AgentInformation("Member Since", MemberSince, "Big"));
        if (UserInfoList.get(0).ActiveListing > 0) {
            ConInfoList.add(new AgentInformation("Active Listings", String.valueOf(UserInfoList.get(0).ActiveListing), "Big"));
        }
        if (UserInfoList.get(0).License.length() > 0) {
            ConInfoList.add(new AgentInformation("Licenses", "66236", "Small"));
        }
        if (UserInfoList.get(0).Languages.length() > 0) {
            ConInfoList.add(new AgentInformation("Language", "English, Bangla", "Small"));
        }
        ConInfoList.add(new AgentInformation("Phone No", UserInfoList.get(0).MobileCode + "" + UserInfoList.get(0).Phone, "Small"));
        ConInfoList.add(new AgentInformation("Address", UserInfoList.get(0).Address, "Small"));
        if (UserInfoList.get(0).SkypeAddress.length() > 0) {
            ConInfoList.add(new AgentInformation("Skype", UserInfoList.get(0).SkypeAddress, "Small"));
        }
        if (UserInfoList.get(0).PersonalBlogAddress.length() > 0) {
            ConInfoList.add(new AgentInformation("Website", UserInfoList.get(0).PersonalBlogAddress, "Small"));
        }

        gridCompanyInfo.setAdapter(new AgentInformationAdapter(this, ConInfoList));
        gridCompanyInfo.setExpanded(true);
    }

    void HttpActiveListing() {
        String URL = AppGlobal.localHost + "/api/Mcontacts/GetAgentActiveListing?contactID=" + pref.getUserID() + "&userId=" +
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
                    //lblMoreActiveListing.setVisibility(View.GONE);
                }

            }
        } catch (Exception e) {
            Log.e("esty", e.getMessage());
        }
    }

    void HttpAgentArea() {
        if (pref.getUserCategory().equals("2") || pref.getUserCategory().equals("4")) {
            String URL = AppGlobal.localHost + "/api/MCompany/GetCompanyAreas?companyID=" + pref.getCompanyID() + "&lang='en'";

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
        } else {
            String URL = AppGlobal.localHost + "/api/User/GetMyServiceArea?contactId=" + pref.getContactID() + "&lang='en'";

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


    }

    void HttpAgentSpecialities() {
        if (pref.getUserCategory().equals("2") || pref.getUserCategory().equals("4")) {

            String URL = AppGlobal.localHost + "/api/MContacts/GetCompanySpecialities?companyID=" + pref.getCompanyID() + "&lang='en'";
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

        } else {
            String URL = AppGlobal.localHost + "/api/Mcontacts/GetContactSpecialities?contactID=" + pref.getContactID() + "&lang='en'";
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


    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            UserInfoList.clear();
            ProInfoList.clear();
            ConInfoList.clear();
            specialities.clear();
            coverageares.clear();
            HttpGetUser();


        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_myprofile_info) {
            Intent intent = new Intent(MyProfileActivity2.this, EditMyProfileActivity.class);
            intent.putExtra("UserInfo", UserInfoList.get(0));
            startActivity(intent);
        }
        if (id == R.id.action_myprofile_speciality) {
            Intent intent = new Intent(MyProfileActivity2.this, MyProfileSetSpecialityActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_myprofile_video) {
            Intent intent = new Intent(MyProfileActivity2.this, SaveVideoActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_myprofile_area) {
            Intent intent = new Intent(MyProfileActivity2.this, MyProfileSetServiceAreaActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_myprofile_share) {
            if (UserInfoList.get(0).Permalink.length() > 0) {
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                share.putExtra(Intent.EXTRA_SUBJECT, "");
                share.putExtra(Intent.EXTRA_TEXT, UserInfoList.get(0).Permalink);

                startActivity(Intent.createChooser(share, "Share Your Profile"));
            }
            return true;
        }
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.hide();
    }
}
