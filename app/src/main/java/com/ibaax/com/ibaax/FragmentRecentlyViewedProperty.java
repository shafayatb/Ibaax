package com.ibaax.com.ibaax;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Adapter.FeaturedPropertyAdapter;
import DataAccessLayer.RecentlyViewedLayer;
import Entity.AppGlobal;
import Entity.Property;
import Event.IHttpResponse;
import Event.IProperty;
import JSOINParsing.ParseProperty;
import Plugins.HelperFunctions;
import ServiceInvoke.HttpRequest;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentRecentlyViewedProperty extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    static List<Property> propertylist = new ArrayList<>();
    ProgressBar progress;
    ListView list;
    List<Integer> propertyIDList = new ArrayList<>();
    RecentlyViewedLayer recentlyViewedLayer;
    TextView lblNoRecentlyViewed;
    Context context;
    AppPrefs prefs;

    public FragmentRecentlyViewedProperty() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        recentlyViewedLayer = new RecentlyViewedLayer(context);
        prefs = new AppPrefs(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recently_viewed, container, false);
        progress = (ProgressBar) rootView.findViewById(R.id.progress_recentlyViewed);
        list = (ListView) rootView.findViewById(R.id.list_recentlyViewed);
        lblNoRecentlyViewed = (TextView) rootView.findViewById(R.id.lbl_NoRecentlyViewedMessage);
        if (propertylist.size() > 0) {
            Bind();
        } else {
            HttpGetRecentlyViewed();
        }
        return rootView;
    }

    void HttpGetRecentlyViewed() {
        try {
            propertyIDList.clear();
            propertyIDList = recentlyViewedLayer.getRecentlyViewed("Property");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < propertyIDList.size(); i++) {
                sb.append(propertyIDList.get(i) + ",");
            }
            if (sb.toString().length() > 0) {
                sb.delete(sb.length() - 1, sb.length());
            }
            String PIDs = sb.toString();
            sb.delete(0, sb.length());
            String URL = AppGlobal.localHost + "/api/MProperty/GetRecentlyViewProperty?pids=" + PIDs;
            Log.v("url", URL);
            new HttpRequest(context, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {
                    propertylist.clear();
                    propertylist = new ParseProperty().parse((String) response, propertylist);
                    propertylist = HelperFunctions.ChangeCurrency(propertylist, prefs);
                    Bind();
                }

                @Override
                public void RequestFailed(String response) {

                }
            }).MakeJsonArrayReq(URL);

        } catch (Exception e) {
            Log.e("error", "RecentlyViewed Error: " + e.getMessage());
        }


    }

    void Bind() {
        if (propertylist.size() > 0) {
            Collections.reverse(propertylist);
            list.setAdapter(new FeaturedPropertyAdapter(context, propertylist, new IProperty() {
                @Override
                public void btnFavorite_click(Object obj) {

                }

                @Override
                public void onClick(Object obj) {

                }
            }));


            progress.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
        } else {
            lblNoRecentlyViewed.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        propertylist.clear();
    }
}
