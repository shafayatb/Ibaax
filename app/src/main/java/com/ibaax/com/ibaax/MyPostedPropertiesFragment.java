package com.ibaax.com.ibaax;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Adapter.MyPostedPropertyAdapter;
import Entity.AppGlobal;
import Entity.Property;
import Event.IEvent;
import Event.IHttpResponse;
import JSOINParsing.ParseProperty;
import ServiceInvoke.HttpRequest;

public class MyPostedPropertiesFragment extends Fragment {


    LinearLayout lnrBusy;
    ListView listview;
    Context context;
    List<Property> propertylist = new ArrayList<>();
    AppPrefs prefs;
    MyPostedPropertyAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        prefs = new AppPrefs(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_my_posted_properties, container, false);
        context = getActivity();
        lnrBusy = (LinearLayout) rootView.findViewById(R.id.busy_myposted_property);
        listview = (ListView) rootView.findViewById(R.id.listview_myposted_property);

        prefs = new AppPrefs(context);
        getFeaturedData(prefs.getUserID(), "en");
        return rootView;
    }

    private void getFeaturedData(String UserID, String lang) {


        lnrBusy.setVisibility(View.VISIBLE);

        String URL = AppGlobal.localHost + "/api/Mproperty/GetMyPostedProperty?userID=" + UserID +
                "&lang='en'";
        new HttpRequest(context, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {
                propertylist = new ParseProperty().parse((String) response, propertylist);
                Bind();

                lnrBusy.setVisibility(View.GONE);
            }

            @Override
            public void RequestFailed(String response) {


                lnrBusy.setVisibility(View.GONE);
            }
        }).MakeJsonArrayReq(URL);

    }

    @Override
    public void onResume() {
        super.onResume();
        String URL = AppGlobal.localHost + "/api/Mproperty/GetMyPostedProperty?userID=" + prefs.getUserID() +
                "&lang='en'";
        new HttpRequest(context, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {
                List<Property> list = new ArrayList<Property>();
                list = new ParseProperty().parse((String) response, list);

               
                propertylist.clear();
                propertylist.addAll(list);
                Bind();
            }

            @Override
            public void RequestFailed(String response) {
                //lnrBusy.setVisibility(View.GONE);
            }
        }).MakeJsonArrayReq(URL);
    }

    public void Bind() {
        adapter = new MyPostedPropertyAdapter(context, propertylist, new IEvent() {
            @Override
            public void onClick(Object obj) {
                final Property p = (Property) obj;

                final String URL = AppGlobal.localHost + "/api/Mproperty/PropertyArchive?propertyId="
                        + String.valueOf(p.PropertyID);


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
                                                propertylist.remove(p);
                                                adapter.notifyDataSetChanged();
                                            }
                                        } catch (Exception e) {
                                            Log.e("esty", "Delete Success Error: " + e.getMessage());
                                        }
                                    }

                                    @Override
                                    public void RequestFailed(String response) {
                                        Log.e("esty", "Delete Failed: " + response);
                                    }
                                }).HttpPostProperty(null, URL);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        listview.setAdapter(adapter);
        listview.setVisibility(View.VISIBLE);

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        //if(!hidden){
        try {
            String URL = AppGlobal.localHost + "/api/Mproperty/GetMyPostedProperty?userID=" + prefs.getUserID() +
                    "&lang='en'";
            Log.v("url", URL);
            new HttpRequest(context, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {
                    List<Property> list = new ArrayList<Property>();
                    list = new ParseProperty().parse((String) response, list);

                    propertylist.clear();
                    propertylist.addAll(list);
                    Bind();

                }


                @Override
                public void RequestFailed(String response) {
                    //lnrBusy.setVisibility(View.GONE);
                }
            }).MakeJsonArrayReq(URL);
        } catch (Exception e) {

        }
        //}
    }
}
