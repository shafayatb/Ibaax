package com.ibaax.com.ibaax;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
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

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import Adapter.OverviewPropertyAdapter;
import Adapter.PropertyImageSwipeAdapter;
import Adapter.PropertySpecificationAdapter;
import DataAccessLayer.RecentlyViewedLayer;
import Entity.Agent;
import Entity.AppGlobal;
import Entity.OverViewProperty;
import Entity.PropertyGallary;
import Entity.PropertySerialize;
import Entity.PropertySpecification;
import Event.IEvent;
import Event.IHttpResponse;
import Event.IProperty;
import JSOINParsing.ParseProperty;
import Plugins.JSONParser;
import Plugins.PhoneCall;
import Popup.ReportDialog;
import ServiceInvoke.AddToFavorite;
import ServiceInvoke.DownloadBitmap;
import ServiceInvoke.HttpRequest;
import UI.ExpandableHeightGridView;
import UI.MessageBox;

public class PropertyDetailActivity2 extends AppCompatActivity {
    PropertySerialize property;

    TextView lblAddress, lblName, lblPropertyType, lblPropertyCategoryType,
            lblDescription, lblPrice;
    TextView lblContUser, lblUserMobile, lblUserCompany_Position, lblUserAddress, lblUserListing;
    ImageView imgUserImage;
    ImageView imgMain, imgProType;
    WebView webdetails;
    GoogleMap googleMap;
    ExpandableHeightGridView gridViewOutdooor, gridViewFurnishing, gridViewParking, gridViewIndoor, gridViewView, gridViewLocalAmn, gridPropertyOverview;
    Context context;
    View MoreInfo;
    LinearLayout lnrMap, lnrSpecification, lnrOutdoor, lnrFurnishing, lnrParking, lnrIndoor, lnrView, lnrLocalAmn;
    String Address, AgentEmail;
    String PHONE_NUMBER = null;
    List<PropertyGallary> gallarylist = new ArrayList<>();
    List<PropertySpecification> specificationlist = new ArrayList<>();
    List<OverViewProperty> OverviewList = new ArrayList<>();
    View DescView;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_detail2);
        getWindow().setBackgroundDrawable(null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        property = (PropertySerialize) getIntent().getSerializableExtra("Property");
        toolBarLayout.setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        init();
        PropertyViewed();

    }

    private void BindMap() {
        try {
            if (googleMap == null) {
                googleMap = ((MapFragment) getFragmentManager()
                        .findFragmentById(R.id.map)).getMap();
            }
            if (property.longitude == 0.0 && property.latitude == 0.0) {
                lnrMap.setVisibility(View.GONE);

            } else {
                final MarkerOptions marker = new MarkerOptions().position(new LatLng(property.latitude, property.longitude)).title(property.PropertyName);
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_icon_forsale));
                googleMap.addMarker(marker).showInfoWindow();

                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(property.latitude, property.longitude)).zoom(15).build();//zoom level was 15. changed to 19
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.getUiSettings().setScrollGesturesEnabled(false);
                googleMap.getUiSettings().setZoomGesturesEnabled(false);
            }

        } catch (Exception ex) {

            Log.v("error", "BindMap Property Detail: " + ex.getMessage());
            lnrMap.setVisibility(View.GONE);

        }
    }

    void init() {
        //Headers
        imgMain = (ImageView) findViewById(R.id.img_propertyDetail_main_image);

        lblAddress = (TextView) findViewById(R.id.lbl_propertydetail_address);
        lblPropertyType = (TextView) findViewById(R.id.lbl_propertyDetailtype);
        lblPropertyCategoryType = (TextView) findViewById(R.id.lbl_propertytdetail_PropertyType);
        lblPrice = (TextView) findViewById(R.id.lbl_propertydetal_price);
        lblName = (TextView) findViewById(R.id.lbl_propertydetail_title);
        lblName.setText(property.PropertyName);
        Address = property.LocalityName + ", " + property.CityName + ", " + property.CountryName;
        lblPropertyType.setText("" + property.PropertyTypeName);
        imgProType = (ImageView) findViewById(R.id.img_list_pro_type);
        lblPropertyCategoryType.setText(property.PropertyTransactionTypeName.toUpperCase());
        if (property.PropertyTransactionTypeCategoryID == 1) {
            imgProType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_map_icon_forsale));
        } else {
            imgProType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_map_icon_forrent));
        }

        lblAddress.setText(property.FullAddress);
        if (property.Price == 0) {
            lblPrice.setText("Contact for price");
        } else {
            DecimalFormat df = new DecimalFormat("#,###");
            lblPrice.setText(property.CurrencyName + " "
                    + String.valueOf(df.format((int) property.Price)));
        }

        DownloadImage();
        getGallary(property.PropertyID + "");
        OpenGallery();

        //Body
        //Overview
        gridPropertyOverview = (UI.ExpandableHeightGridView) findViewById(R.id.gridview_overview_propertydetail);
        BindOverview();

        //Description
        DescView = (View) findViewById(R.id.view_desc);
        lblDescription = (TextView) findViewById(R.id.lbl_propertydetail_description);
        webdetails = (WebView) findViewById(R.id.web_propertydetail_description);
        if (property.Description.trim().length() > 0) {
            webdetails.getSettings().setJavaScriptEnabled(true);
            webdetails.loadDataWithBaseURL("", property.Description, "text/html", "UTF-8", "");
        } else {
            lblDescription.setVisibility(View.GONE);
            webdetails.setVisibility(View.GONE);
            DescView.setVisibility(View.GONE);
        }

        //Specification
        gridViewIndoor = (ExpandableHeightGridView) findViewById(R.id.gridview_indoor_propertydetail);
        gridViewOutdooor = (ExpandableHeightGridView) findViewById(R.id.gridview_outdoor_propertydetail);
        gridViewFurnishing = (ExpandableHeightGridView) findViewById(R.id.gridview_Furnishing_propertydetail);
        gridViewParking = (ExpandableHeightGridView) findViewById(R.id.gridview_Parking_propertydetail);
        gridViewView = (ExpandableHeightGridView) findViewById(R.id.gridview_view_propertydetail);
        gridViewLocalAmn = (ExpandableHeightGridView) findViewById(R.id.gridview_localamn_propertydetail);

        lnrSpecification = (LinearLayout) findViewById(R.id.lnrPropertyDetailSpecification);
        lnrIndoor = (LinearLayout) findViewById(R.id.lnrPropertyDetailIndoor);
        lnrOutdoor = (LinearLayout) findViewById(R.id.lnrPropertyDetailOutdoor);
        lnrFurnishing = (LinearLayout) findViewById(R.id.lnrPropertyDetailFurnishing);
        lnrParking = (LinearLayout) findViewById(R.id.lnrPropertyDetailParking);
        lnrView = (LinearLayout) findViewById(R.id.lnrPropertyDetailView);
        lnrLocalAmn = (LinearLayout) findViewById(R.id.lnrPropertyDetailLocalamn);

        getPropertyDetail();

        //Location
        lnrMap = (LinearLayout) findViewById(R.id.mapview_propertydetail);
        BindMap();

        //ReqMoreInfo
        MoreInfo = (View) findViewById(R.id.view_more_info);
        lblContUser = (TextView) MoreInfo.findViewById(R.id.txt_propertydetail_postedby);
        lblUserCompany_Position = (TextView) MoreInfo.findViewById(R.id.txt_propertydetail_usertype);
        lblUserMobile = (TextView) MoreInfo.findViewById(R.id.txt_propertydetail_userphn);
        lblUserAddress = (TextView) MoreInfo.findViewById(R.id.txt_propertydetail_useraddress);
        lblUserListing = (TextView) MoreInfo.findViewById(R.id.txt_propertydetail_listing);
        imgUserImage = (ImageView) MoreInfo.findViewById(R.id.img_propertydetail_userimage);

        MoreInfo.setVisibility(View.GONE);
        getContactInfo();

    }

    void BindOverview() {

        OverviewList.add(new OverViewProperty(R.drawable.bed, "Bedroom:",
                String.valueOf(property.PropertyBedroomNumber)));
        OverviewList.add(new OverViewProperty(R.drawable.bath, "Bathroom:",
                String.valueOf(property.PropertyBathroomNumber)));
        OverviewList.add(new OverViewProperty(R.drawable.area, "Area:",
                String.valueOf(property.PropertyBuildUpArea) + " " + property.PropertyBuildUpAreaSizeTypeName));
        OverviewList.add(new OverViewProperty(R.drawable.area, "Plot:",
                String.valueOf(property.PropertyPlotArea) + " " + property.PropertyPlotAreaSizeTypeName));
        OverviewList.add(new OverViewProperty(R.drawable.calndr, "Built-Year:",
                "N/A"));
        OverviewList.add(new OverViewProperty(R.drawable.list, "DisplayID:",
                String.valueOf(property.DisplayID)));
        OverviewList.add(new OverViewProperty(R.drawable.point, "Posted By",
                String.valueOf(property.PostedBy)));
        String[] DateArr = property.CreateDate.split("T");
        OverviewList.add(new OverViewProperty(R.drawable.calndr, "Posted On:",
                DateArr[0]));


        gridPropertyOverview.setAdapter(new OverviewPropertyAdapter(context, OverviewList, new IEvent() {
            @Override
            public void onClick(Object obj) {

            }
        }));
        gridPropertyOverview.setExpanded(true);

    }

    public void OpenGallery() {
        imgMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //gallarylist.clear();

                PopSwipeGallery();
            }
        });


    }

    private void getGallary(String id) {

        String URL = AppGlobal.localHost + "/api/MProperty/GetPropertyPhotoPortfolio?pid=" + id;
        Log.v("url", "home getFeaturedAgent  : " + URL);

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please Wait");
        pDialog.show();
        new HttpRequest(context, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {
                gallarylist = new ParseProperty().parseGallary((String) response, gallarylist);

                //PopSwipeGallery();
                pDialog.dismiss();
            }

            @Override
            public void RequestFailed(String response) {
                pDialog.dismiss();
            }
        }).MakeJsonArrayReq(URL);

    }

    private void PopSwipeGallery() {
        final Dialog dialog = new Dialog(context, R.style.MyCustomDialog);

        WindowManager.LayoutParams lp = new android.view.WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_viewpager);
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        LinearLayout lnrDismisDialog = (LinearLayout) dialog.findViewById(R.id.lnr_back);
        ViewPager ImagePager = (ViewPager) dialog.findViewById(R.id.image_slider);

        PropertyImageSwipeAdapter adapter = new PropertyImageSwipeAdapter(context, gallarylist, null);

        ImagePager.setAdapter(adapter);
        lnrDismisDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void DownloadImage() {
        Log.v("url", "ImageURL: " + property.ImagePath);
        new DownloadBitmap
                (context, new IHttpResponse() {
                    @Override
                    public void RequestSuccess(Object response) {


                        Bitmap bitmap = (Bitmap) response;
                        imgMain.setImageBitmap(bitmap);
                    }

                    @Override
                    public void RequestFailed(String response) {

                        String responselocal = response;

                    }
                }).Download(property.ImagePath.replace(" ", "%20"));

    }

    private void BindSpefification() {

        if (specificationlist.size() > 0) {
            lnrSpecification.setVisibility(View.VISIBLE);
            List<PropertySpecification> Indoorlist = new ArrayList<>();
            List<PropertySpecification> Outdoorlist = new ArrayList<>();
            List<PropertySpecification> Furnishinglist = new ArrayList<>();
            List<PropertySpecification> Parkinglist = new ArrayList<>();
            List<PropertySpecification> Viewlist = new ArrayList<>();
            List<PropertySpecification> LocalAmnlist = new ArrayList<>();
            for (PropertySpecification r : specificationlist) {
                if (r.CategoryName.equals("Outdoor")) {
                    Outdoorlist.add(r);
                } else if (r.CategoryName.equals("Furnishing")) {
                    Furnishinglist.add(r);
                } else if (r.CategoryName.equals("Parking")) {
                    Parkinglist.add(r);
                } else if (r.CategoryName.equals("Indoor")) {
                    Indoorlist.add(r);
                } else if (r.CategoryName.equals("View")) {
                    Viewlist.add(r);
                } else if (r.CategoryName.equals("LocalAmenities")) {
                    LocalAmnlist.add(r);
                }
            }
            if (Indoorlist.size() > 0) {
                gridViewIndoor.setAdapter(new PropertySpecificationAdapter(context, Indoorlist));
                gridViewIndoor.setExpanded(true);
                lnrIndoor.setVisibility(View.VISIBLE);
            }
            if (Outdoorlist.size() > 0) {
                gridViewOutdooor.setAdapter(new PropertySpecificationAdapter(context, Outdoorlist));
                gridViewOutdooor.setExpanded(true);
                lnrOutdoor.setVisibility(View.VISIBLE);
            }
            if (Furnishinglist.size() > 0) {
                gridViewFurnishing.setAdapter(new PropertySpecificationAdapter(context, Furnishinglist));
                gridViewFurnishing.setExpanded(true);
                lnrFurnishing.setVisibility(View.VISIBLE);
            }
            if (Parkinglist.size() > 0) {
                gridViewParking.setAdapter(new PropertySpecificationAdapter(context, Parkinglist));
                gridViewParking.setExpanded(true);
                lnrParking.setVisibility(View.VISIBLE);
            }
            if (Viewlist.size() > 0) {
                gridViewView.setAdapter(new PropertySpecificationAdapter(context, Viewlist));
                gridViewView.setExpanded(true);
                lnrView.setVisibility(View.VISIBLE);
            }
            if (LocalAmnlist.size() > 0) {
                gridViewLocalAmn.setAdapter(new PropertySpecificationAdapter(context, LocalAmnlist));
                gridViewLocalAmn.setExpanded(true);
                lnrLocalAmn.setVisibility(View.GONE);
            }
        }

    }

    private void getPropertyDetail() {
        String URL = AppGlobal.localHost + "/api/MProperty/GetPropertyDetails?pid=" + property.PropertyID;
        Log.v("url", URL);
        new HttpRequest(context, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {
                try {
                    JSONArray array = new JSONArray((String) response);
                    for (int i = 0; i < array.length(); i++) {
                        try {
                            JSONObject jo = array.getJSONObject(i);
                            PropertySpecification p = new PropertySpecification();
                            p.PropertyID = JSONParser.parseInt(jo, "PropertyID");
                            p.CategoryName = JSONParser.parseString(jo, "CategoryName");
                            p.SpecificationName = JSONParser.parseString(jo, "SpecificationName");
                            p.Data = JSONParser.parseString(jo, "Data");
                            p.InputDataType = JSONParser.parseInt(jo, "InputDataType");
                            specificationlist.add(p);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    BindSpefification();
                } catch (Exception ex) {

                }

            }

            @Override
            public void RequestFailed(String response) {

            }
        }).MakeJsonArrayReq(URL);
    }

    public void getContactInfo() {
        String ContactInfoUrl = "";
        if (property.CompanyID == 0) {
            ContactInfoUrl = AppGlobal.localHost + "/api/MProperty/GetPropertyContactInfoDetails?contactId=" + property.ContactID;
        } else if (property.ContactID.equals("0") || property.ContactID.equals("")) {
            ContactInfoUrl = AppGlobal.localHost + "/api/MProperty/GetPropertyContactInfoDetails?contactId=" + property.CompanyID;
        }
        final List<Agent> ContactInfo = new ArrayList<Agent>();
        new HttpRequest(context, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {
                try {
                    //ContactInfo = new ArrayList<Agent>();
                    ContactInfo.clear();
                    JSONArray array = new JSONArray((String) response);
                    for (int i = 0; i < array.length(); i++) {
                        try {
                            JSONObject jo = array.getJSONObject(i);
                            Agent p = new Agent();

                            p.agentId = JSONParser.parseInt(jo, "Id");
                            p.agentThumbnailUrl = "http://" + JSONParser.parseString(jo, "LogoUrl");
                            p.name = JSONParser.parseString(jo, "Name");
                            p.CompanyName = JSONParser.parseString(jo, "CompanyName");
                            p.mobile = JSONParser.parseString(jo, "Mobile");
                            p.country = JSONParser.parseString(jo, "Country");
                            p.state = JSONParser.parseString(jo, "State");
                            p.city = JSONParser.parseString(jo, "City");
                            p.locality = JSONParser.parseString(jo, "Locality");
                            p.address = JSONParser.parseString(jo, "Address");
                            p.zipCode = JSONParser.parseString(jo, "ZipCode");
                            p.verified = JSONParser.parseBoolien(jo, "Verified");
                            p.PersonalBlogAddress = JSONParser.parseString(jo, "PersonalBlogAddress");
                            p.ProfileSummary = JSONParser.parseString(jo, "ProfileSummary");
                            p.AgentLicenseNumber = JSONParser.parseString(jo, "AgentLicenseNumber");
                            p.OfficePhone = JSONParser.parseString(jo, "OfficePhone");
                            p.email = JSONParser.parseString(jo, "Email");
                            p.Twitter = JSONParser.parseString(jo, "Twitter");
                            p.LinkedIn = JSONParser.parseString(jo, "LinkedIn");
                            p.GooglePlus = JSONParser.parseString(jo, "GooglePlus");
                            p.activeListing = JSONParser.parseInt(jo, "ActiveListingCount");
                            p.agentPermaLink = JSONParser.parseString(jo, "PermaLink");
                            p.Position = JSONParser.parseString(jo, "Position");
                            p.MemberSince = JSONParser.parseString(jo, "MemberSince");

                            List<String> AddressList = new ArrayList<>();
                            AddressList.add(p.locality);
                            AddressList.add(p.city);
                            AddressList.add(p.state);
                            AddressList.add(p.country);
                            StringBuilder sb = new StringBuilder();
                            for (int j = 0; j < AddressList.size(); j++) {
                                if (AddressList.get(j).length() > 0) {
                                    sb.append(AddressList.get(j) + ", ");
                                }
                            }
                            if (sb.toString().length() > 0) {
                                sb.delete(sb.length() - 2, sb.length());
                            }
                            p.fulladdress = sb.toString();

                            ContactInfo.add(p);
                            AddressList.clear();
                            if (sb.toString().length() > 0) {
                                sb.delete(0, sb.length());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    MoreInfo.setVisibility(View.VISIBLE);
                    AgentEmail = ContactInfo.get(0).email;
                    PHONE_NUMBER = ContactInfo.get(0).mobile;
                    lblContUser.setText(ContactInfo.get(0).name);
                    if (ContactInfo.get(0).CompanyName.equals("N/A")) {
                        lblUserCompany_Position.setText(ContactInfo.get(0).Position);
                    } else {
                        lblUserCompany_Position.setText(ContactInfo.get(0).CompanyName);
                    }
                    lblUserMobile.setText(ContactInfo.get(0).mobile);
                    lblUserAddress.setText(ContactInfo.get(0).fulladdress);
                    lblUserListing.setText("Listing: " + ContactInfo.get(0).activeListing);

                    //MoreInfo.setVisibility(View.VISIBLE);

                    DLUserImage(ContactInfo.get(0).agentThumbnailUrl);


                } catch (Exception ex) {
                    Log.e("esty", "More Ifno Error: " + ex.getMessage());
                }
            }

            @Override
            public void RequestFailed(String response) {

            }
        }).MakeJsonArrayReq(ContactInfoUrl);

        MoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AgentProfileActivity2.class);

                intent.putExtra("Agent", (Serializable) ContactInfo.get(0));
                context.startActivity(intent);
            }
        });
    }

    public void DLUserImage(String imgURL) {
        new DownloadBitmap
                (context, new IHttpResponse() {
                    @Override
                    public void RequestSuccess(Object response) {
                        Bitmap bitmap = (Bitmap) response;
                        imgUserImage.setImageBitmap(bitmap);
                    }

                    @Override
                    public void RequestFailed(String response) {

                        String responselocal = response;

                    }
                }).Download(imgURL.replace(" ", "%20"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_agent, menu);
        return true;
    }

    public void makecall_call2(View view) {


        if (PHONE_NUMBER != null) {
            PhoneCall.Call(this, PHONE_NUMBER);
        } else {
            MessageBox.Show(context, "No phone number found");
        }

    }


    public void sendMessage_click2(View view) {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("Name", property.PropertyName);
        intent.putExtra("DisplayID", property.DisplayID);
        intent.putExtra("ID", property.PropertyID);
        intent.putExtra("Address", Address);
        intent.putExtra("AgentEmail", AgentEmail);
        intent.putExtra("ContactID", property.ContactID);
        startActivity(intent);

    }

    public void btnPropertySMS_click(View view) {
        if (PHONE_NUMBER != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + PHONE_NUMBER));
            intent.putExtra("sms_body", "");
            startActivity(intent);
        } else {
            MessageBox.Show(context, "Sorry, this property has no phone number attached to it.");
        }

    }

    public void showStreetView(View view) {

        Intent intent = new Intent(this, PropertyStreetViewActivity.class);
        intent.putExtra("Latitude", property.latitude);
        intent.putExtra("Longitude", property.longitude);
        startActivity(intent);


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        if (property.IsLiked) {
            menu.findItem(R.id.action_favorite).setIcon(getResources().getDrawable(R.drawable.ic_action_favorite));
        } else {
            menu.findItem(R.id.action_favorite).setIcon(getResources().getDrawable(R.drawable.ic__action_favorite_outline));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                finish();
                return true;
            case R.id.action_share:
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                share.putExtra(Intent.EXTRA_SUBJECT, "");
                share.putExtra(Intent.EXTRA_TEXT, property.PropertyPermaLink);
                startActivity(Intent.createChooser(share, "Share property!"));

                /*PackageManager pm = getPackageManager();
                List<ResolveInfo> activityList = pm.queryIntentActivities(share, 0);
                for (final ResolveInfo app : activityList) {
                    if (app.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {

                        ShareLinkContent content = new ShareLinkContent.Builder()
                                .setContentTitle(property.PropertyName)
                                .setImageUrl(Uri.parse(property.ImagePath.replace(" ", "%20")))
                                .setContentUrl(Uri.parse(property.PropertyPermaLink))
                                .build();

                        ShareDialog shareDialog = new ShareDialog(this);

                        shareDialog.canShow(content);

                        break;
                    } else {
                        share.setType("text/plain");
                        share.addFlags(share.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        share.putExtra(Intent.EXTRA_SUBJECT, "");
                        share.putExtra(Intent.EXTRA_TEXT, property.PropertyPermaLink);
                        startActivity(Intent.createChooser(share, "Share property!"));
                    }
                }*/


                return true;
            case R.id.action_favorite:
                try {
                    AppPrefs pref = new AppPrefs(context);
                    String UserID = pref.getUserID();
                    if (UserID.length() > 1) {
                        new AddToFavorite(context, new IProperty() {
                            @Override
                            public void btnFavorite_click(Object obj) {

                            }

                            @Override
                            public void onClick(Object obj) {

                            }
                        }).Post(property.PropertyID + "",
                                UserID,
                                pref.getCompanyID() + "",
                                pref.getFirstName(),
                                pref.getLastName());
                    } else {
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                        return true;
                    }
                    if (property.IsLiked) {
                        property.IsLiked = false;
                        menu.findItem(R.id.action_favorite).setIcon(getResources().getDrawable(R.drawable.ic__action_favorite_outline));
                        //imgIsLiked.setImageResource(R.drawable.ic__action_favorite_outline);
                    } else {
                        property.IsLiked = true;
                        menu.findItem(R.id.action_favorite).setIcon(getResources().getDrawable(R.drawable.ic_action_favorite));
                        //imgIsLiked.setImageResource(R.drawable.ic_action_favorite);
                    }
                } catch (Exception ex) {
                    Log.v("error", "Featured Property Adapter Addto Faverite: " + ex.getMessage());
                }
                return true;
            case R.id.action_flag:
                new ReportDialog(this, (int) property.PropertyID, new IEvent() {
                    @Override
                    public void onClick(Object obj) {

                    }
                }).PopupReport();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    void PropertyViewed() {
        try {
            RecentlyViewedLayer recentlyViewedLayer = new RecentlyViewedLayer(this);
            List<Integer> propertyIDList = new ArrayList<>();
            propertyIDList = recentlyViewedLayer.getRecentlyViewed("Property");
            boolean IDAlreadyExists = false;
            for (int i = 0; i < propertyIDList.size(); i++) {
                if ((int) property.PropertyID == propertyIDList.get(i)) {
                    IDAlreadyExists = true;
                    break;
                }
                Log.v("viewed", "PropertyDetailActivity2/PropertyViewed->Data: " + propertyIDList.get(i));
            }
            if (!IDAlreadyExists) {

                if (propertyIDList.size() < 10) {
                    recentlyViewedLayer.Insert((int) property.PropertyID, "Property");
                } else {
                    recentlyViewedLayer.deleteLastItem(propertyIDList.get(0));
                    recentlyViewedLayer.Insert((int) property.PropertyID, "Property");
                }
            }
            Log.v("viewed", "PropertyDetailActivity2/PropertyViewed->Success: Data Inserted Successfully");
        } catch (Exception e) {
            Log.e("viewed", "PropertyDetailActivity2/PropertyViewed->Error: " + e.getMessage());
        }

    }

}
