package com.ibaax.com.ibaax;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Adapter.OverviewPropertyAdapter;
import Adapter.PropertyImageSwipeAdapter;
import Adapter.PropertySpecificationAdapter;
import Entity.AppGlobal;
import Entity.OverViewProperty;
import Entity.PropertyGallary;
import Entity.PropertySerialize;
import Entity.PropertySpecification;
import Event.IEvent;
import Event.IHttpResponse;
import JSOINParsing.ParseProperty;
import Plugins.JSONParser;
import ServiceInvoke.DownloadBitmap;
import ServiceInvoke.HttpRequest;
import UI.ExpandableHeightGridView;

/**
 * Created by iBaax on 3/28/16.
 */
public class MyPostedPropertyDetailActivity2 extends AppCompatActivity {

    PropertySerialize property;
    ImageView CoverImage, imgProType;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    TextView lblTitle, lblAddress, lblDescription;
    WebView webdetails;
    GoogleMap googleMap;
    ExpandableHeightGridView gridViewOutdooor, gridViewFurnishing, gridViewParking,
            gridViewIndoor, gridViewView, gridViewLocalAmn, gridPropertyOverview;
    LinearLayout lnrMap;
    LinearLayout lnrSpecification, lnrOutdoor, lnrFurnishing, lnrParking, lnrIndoor, lnrView, lnrLocalAmn;
    Context context;
    TextView lblPropertyType, lblPropertyTranType, lblPrice;
    View DescView;

    List<PropertySpecification> specificationlist = new ArrayList<>();
    List<OverViewProperty> OverviewList = new ArrayList<>();
    List<PropertyGallary> gallarylist = new ArrayList<>();
    Menu menu;
    OverviewPropertyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posted_property_detail_2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.mytoolbar_layout);
        toolBarLayout.setTitle("");
        property = (PropertySerialize) getIntent().getSerializableExtra("Property");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        init();
        BindMap();
    }

    private void init() {
        try {
            CoverImage = (ImageView) findViewById(R.id.img_mypropertyDetail_main_image);
            lblTitle = (TextView) findViewById(R.id.lbl_mypropertydetail_title);
            lblTitle.setText(property.PropertyName);
            lblAddress = (TextView) findViewById(R.id.lbl_mypropertydetail_address);

            lblAddress.setText(property.FullAddress);
            lblPropertyTranType = (TextView) findViewById(R.id.lbl_mypropertyDetailtype);
            lblPropertyTranType.setText(property.PropertyTypeName);
            lblPrice = (TextView) findViewById(R.id.lbl_mypropertydetail_price);
            if (property.Price > 0) {
                DecimalFormat df = new DecimalFormat("#,###");
                lblPrice.setText(property.CurrencyName + " "
                        + String.valueOf(df.format((int) property.Price)));
            } else {
                lblPrice.setText("Contact for price");
            }
            lblPropertyType = (TextView) findViewById(R.id.lbl_mypropertytdetail_PropertyType);
            imgProType = (ImageView) findViewById(R.id.img_list_pro_type);
            lblPropertyType.setText(property.PropertyTransactionTypeName);
            if (property.PropertyTransactionTypeCategoryID == 1) {
                imgProType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_map_icon_forsale));
            } else {
                imgProType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_map_icon_forrent));
            }

            gridPropertyOverview = (UI.ExpandableHeightGridView) findViewById(R.id.gridview_overview_mypropertydetail);
            BindOverview();

            DescView = (View) findViewById(R.id.myview_desc);
            lblDescription = (TextView) findViewById(R.id.lblMyPropertyDesc);
            webdetails = (WebView) findViewById(R.id.web_mypropertydetail_description);
            if (property.Description.length() > 0) {
                webdetails.getSettings().setJavaScriptEnabled(true);
                webdetails.loadDataWithBaseURL("", property.Description, "text/html", "UTF-8", "");
            } else {
                webdetails.setVisibility(View.GONE);
                lblDescription.setVisibility(View.GONE);
                DescView.setVisibility(View.GONE);
            }

            getGallary(property.PropertyID + "");
            OpenGallery();

            gridViewOutdooor = (ExpandableHeightGridView) findViewById(R.id.gridview_outdoor_mypropertydetail);
            gridViewFurnishing = (ExpandableHeightGridView) findViewById(R.id.gridview_Furnishing_mypropertydetail);
            gridViewParking = (ExpandableHeightGridView) findViewById(R.id.gridview_Parking_mypropertydetail);
            gridViewIndoor = (ExpandableHeightGridView) findViewById(R.id.gridview_Indoor_mypropertydetail);
            gridViewView = (ExpandableHeightGridView) findViewById(R.id.gridview_View_mypropertydetail);
            gridViewLocalAmn = (ExpandableHeightGridView) findViewById(R.id.gridview_Locanamn_mypropertydetail);

            lnrMap = (LinearLayout) findViewById(R.id.mapview_mypropertydetail);
            lnrSpecification = (LinearLayout) findViewById(R.id.lnrMyPropertySpecification);
            lnrOutdoor = (LinearLayout) findViewById(R.id.lnrMyPropertyDetail_outdoor);
            lnrFurnishing = (LinearLayout) findViewById(R.id.lnrMyPropertyDetail_furnishing);
            lnrParking = (LinearLayout) findViewById(R.id.lnrMyPropertyDetail_parking);
            lnrIndoor = (LinearLayout) findViewById(R.id.lnrMyPropertyDetail_indoor);
            lnrView = (LinearLayout) findViewById(R.id.lnrMyPropertyDetail_view);
            lnrLocalAmn = (LinearLayout) findViewById(R.id.lnrMyPropertyDetail_localamn);

            getPropertyDetail();
            DownloadImage(property.ImagePath);
        } catch (Exception e) {
            Log.e("esty", "MyPostedPropertyDetailActivity2/init-> Error" + e.getMessage());
        }
    }

    private void DownloadImage(String IMGURL) {
        Log.v("url", "ImageURL: " + property.ImagePath);
        new DownloadBitmap
                (context, new IHttpResponse() {
                    @Override
                    public void RequestSuccess(Object response) {

                        Bitmap bitmap = (Bitmap) response;
                        CoverImage.setImageBitmap(bitmap);
                    }

                    @Override
                    public void RequestFailed(String response) {


                    }
                }).Download(IMGURL.replace(" ", "%20"));

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
        OverviewList.add(new OverViewProperty(R.drawable.ic_publish, "Status:",
                property.RecentPublishStatus));
        adapter = new OverviewPropertyAdapter(context, OverviewList, new IEvent() {
            @Override
            public void onClick(Object obj) {

            }
        });

        gridPropertyOverview.setAdapter(adapter);
        gridPropertyOverview.setExpanded(true);

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

                pDialog.dismiss();
            }

            @Override
            public void RequestFailed(String response) {
                pDialog.dismiss();
            }
        }).MakeJsonArrayReq(URL);

    }

    public void OpenGallery() {
        CoverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopSwipeGallery();
            }
        });


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

    void setMenuItems(Menu menus) {

        if (property.RecentPublishStatus.equals("Published")) {
            menus.findItem(R.id.action_publish_staus).setIcon(getResources().getDrawable(R.drawable.ic_action_published));
            menus.findItem(R.id.action_publish).setVisible(false);
            menus.findItem(R.id.action_unpublish).setVisible(true);
        } else {
            menus.findItem(R.id.action_publish_staus).setIcon(getResources().getDrawable(R.drawable.ic_action_unpublished));
            menus.findItem(R.id.action_publish).setVisible(true);
            menus.findItem(R.id.action_unpublish).setVisible(false);
        }
    }

    private void BindMap() {
        try {
            if (googleMap == null) {
                googleMap = ((MapFragment) getFragmentManager()
                        .findFragmentById(R.id.mymap)).getMap();
            }
            if (property.latitude == 0.0 && property.longitude == 0.0) {
                lnrMap.setVisibility(View.GONE);
            } else {
                final MarkerOptions marker = new MarkerOptions().position(new LatLng(property.latitude, property.longitude)).title(property.PropertyName);
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_icon_forsale));
                googleMap.addMarker(marker).showInfoWindow();

                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(property.latitude, property.longitude)).zoom(15).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.getUiSettings().setZoomGesturesEnabled(false);
                googleMap.getUiSettings().setScrollGesturesEnabled(false);
            }

        } catch (Exception ex) {

            Log.v("error", "BindMap Property Detail: " + ex.getMessage());
            lnrMap.setVisibility(View.GONE);

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

    private void BindSpefification() {
        if (specificationlist.size() > 0) {
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
            } else {
                lnrIndoor.setVisibility(View.GONE);
            }
            if (Outdoorlist.size() > 0) {
                gridViewOutdooor.setAdapter(new PropertySpecificationAdapter(context, Outdoorlist));
                gridViewOutdooor.setExpanded(true);
            } else {
                lnrOutdoor.setVisibility(View.GONE);
            }
            if (Furnishinglist.size() > 0) {
                gridViewFurnishing.setAdapter(new PropertySpecificationAdapter(context, Furnishinglist));
                gridViewFurnishing.setExpanded(true);
            } else {
                lnrFurnishing.setVisibility(View.GONE);
            }
            if (Parkinglist.size() > 0) {
                gridViewParking.setAdapter(new PropertySpecificationAdapter(context, Parkinglist));
                gridViewParking.setExpanded(true);
            } else {
                lnrParking.setVisibility(View.GONE);
            }
            if (Viewlist.size() > 0) {
                gridViewView.setAdapter(new PropertySpecificationAdapter(context, Viewlist));
                gridViewView.setExpanded(true);
            } else {
                lnrView.setVisibility(View.GONE);
            }
            if (LocalAmnlist.size() > 0) {
                gridViewLocalAmn.setAdapter(new PropertySpecificationAdapter(context, LocalAmnlist));
                gridViewLocalAmn.setExpanded(true);
            } else {
                lnrLocalAmn.setVisibility(View.GONE);
            }
        } else {
            lnrSpecification.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (EditPhotoActivity.PhotoFileName.length() > 0) {
            String URL = AppGlobal.ImageHost + "/property/ShowMarketplaceImage/" + property.PropertyID
                    + "?PhotoFileName=" + EditPhotoActivity.PhotoFileName;
            DownloadImage(URL);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EditPhotoActivity.PhotoFileName = "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menus) {
        getMenuInflater().inflate(R.menu.menu_property, menus);
        setMenuItems(menus);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;

        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                finish();
                return true;
            case R.id.action_basic:
                Intent intent = new Intent(this, MyPropertyEditBasicInfoActivity.class);
                intent.putExtra("Property", property);
                startActivity(intent);
                return true;
            case R.id.action_photos:
                Intent intent1 = new Intent(this, AddPhotoActivity.class);
                intent1.putExtra("ID", String.valueOf(property.PropertyID));
                startActivity(intent1);
                return true;
            case R.id.action_specifications:
                Intent intent2 = new Intent(this, PropertySetSpecificationActivity.class);
                intent2.putExtra("ID", String.valueOf(property.PropertyID));
                startActivity(intent2);
                return true;
            case R.id.action_video:
                Intent postVideoIntent = new Intent(this, MyPropertySaveVideo.class);
                postVideoIntent.putExtra("PID", String.valueOf(property.PropertyID));
                startActivity(postVideoIntent);

                return true;
            case R.id.action_publish:

                String URL = AppGlobal.localHost + "/api/Mproperty/PropertyPublishStatusSave/";

                HashMap<String, String> jsonParams = new HashMap<>();
                jsonParams.put("propertyId", String.valueOf(property.PropertyID));
                jsonParams.put("isPublish", "true");

                new HttpRequest(this, new IHttpResponse() {
                    @Override
                    public void RequestSuccess(Object response) {
                        try {
                            JSONObject jo = new JSONObject((String) response);
                            if (jo.getBoolean("success")) {
                                //PublishStatus.setText(jo.getString("SuccessMessage"));
                                Toast.makeText(MyPostedPropertyDetailActivity2.this, jo.getString("SuccessMessage"), Toast.LENGTH_SHORT).show();
                                menu.findItem(R.id.action_publish_staus).setIcon(getResources().getDrawable(R.drawable.ic_action_published));
                                menu.findItem(R.id.action_publish).setVisible(false);
                                menu.findItem(R.id.action_unpublish).setVisible(true);
                                OverviewList.get(8).OverViewValue = "Published";
                                adapter.notifyDataSetChanged();
                            } else {
                                //PublishStatus.setText(jo.getString("SuccessMessage"));
                            }
                        } catch (JSONException e) {

                        }
                    }

                    @Override
                    public void RequestFailed(String response) {

                    }
                }).HttpPostProperty(jsonParams, URL);


                return true;
            case R.id.action_unpublish:


                String URL1 = AppGlobal.localHost + "/api/Mproperty/PropertyPublishStatusSave/";

                HashMap<String, String> jsonParams1 = new HashMap<>();
                jsonParams1.put("propertyId", String.valueOf(property.PropertyID));
                jsonParams1.put("isPublish", "false");

                new HttpRequest(this, new IHttpResponse() {
                    @Override
                    public void RequestSuccess(Object response) {
                        try {
                            JSONObject jo = new JSONObject((String) response);
                            if (jo.getBoolean("success")) {
                                Toast.makeText(MyPostedPropertyDetailActivity2.this, jo.getString("SuccessMessage"), Toast.LENGTH_SHORT).show();
                                menu.findItem(R.id.action_publish_staus).setIcon(getResources().getDrawable(R.drawable.ic_action_unpublished));
                                menu.findItem(R.id.action_publish).setVisible(true);
                                menu.findItem(R.id.action_unpublish).setVisible(false);
                                OverviewList.get(8).OverViewValue = "Unpublished";
                                adapter.notifyDataSetChanged();
                            } else {

                            }
                        } catch (JSONException e) {

                        }
                    }

                    @Override
                    public void RequestFailed(String response) {

                    }
                }).HttpPostProperty(jsonParams1, URL1);


                return true;
            case R.id.action_share:
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                share.putExtra(Intent.EXTRA_SUBJECT, "");
                share.putExtra(Intent.EXTRA_TEXT, property.PropertyPermaLink);

                startActivity(Intent.createChooser(share, "Share your property!"));
                return true;
            case R.id.action_delete:
                final String URL2 = AppGlobal.localHost + "/api/Mproperty/PropertyArchive?propertyId="
                        + String.valueOf(property.PropertyID);


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete this record?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do some thing here which you need
                                new HttpRequest(context, new IHttpResponse() {
                                    @Override
                                    public void RequestSuccess(Object response) {
                                        try {
                                            JSONObject jo = new JSONObject((String) response);

                                            if (jo.getBoolean("success")) {
                                                Toast.makeText(context, jo.getString("Message"), Toast.LENGTH_LONG).show();

                                                finish();
                                            }
                                        } catch (Exception e) {
                                            Log.e("esty", "Delete Success Error: " + e.getMessage());
                                        }
                                    }

                                    @Override
                                    public void RequestFailed(String response) {
                                        Log.e("esty", "Delete Failed: " + response);
                                    }
                                }).HttpPostProperty(null, URL2);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
