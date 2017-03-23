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

import Adapter.BrokeragesAdapter;
import DataAccessLayer.RecentlyViewedLayer;
import Entity.AppGlobal;
import Entity.Brokerages;
import Event.IHttpResponse;
import Event.IProperty;
import JSOINParsing.parseBrokerage;
import ServiceInvoke.HttpRequest;

/**
 * Created by iBaax on 4/3/16.
 */
public class FragmentRecentlyViewedBrokerage extends Fragment {


    ProgressBar progress;
    ListView list;
    List<Brokerages> brokeragelist = new ArrayList<>();
    RecentlyViewedLayer recentlyViewedLayer;
    List<Integer> brokerageIDList = new ArrayList<>();
    TextView lblNoRecentlyViewed;
    Context context;

    public FragmentRecentlyViewedBrokerage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        recentlyViewedLayer = new RecentlyViewedLayer(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recently_viewed, container, false);
        progress = (ProgressBar) rootView.findViewById(R.id.progress_recentlyViewed);
        list = (ListView) rootView.findViewById(R.id.list_recentlyViewed);
        list.setPadding(10,10,10,10);
        lblNoRecentlyViewed = (TextView) rootView.findViewById(R.id.lbl_NoRecentlyViewedMessage);
        lblNoRecentlyViewed.setText("You currently don't have any recently viewed brokerages.");
        if (brokeragelist.size() > 0) {
            Bind();
        } else {
            HttpGetRecentlyViewed();
        }
        return rootView;
    }

    void HttpGetRecentlyViewed() {
        try {
            brokerageIDList.clear();
            brokerageIDList = recentlyViewedLayer.getRecentlyViewed("Brokerages");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < brokerageIDList.size(); i++) {
                sb.append(brokerageIDList.get(i) + ",");
            }
            if (sb.toString().length() > 0) {
                sb.delete(sb.length() - 1, sb.length());
            }
            String PIDs = sb.toString();
            sb.delete(0, sb.length());
            String URL = AppGlobal.localHost + "/api/MCompany/GetRecentlyViewAgency?companyIds=" + PIDs +
                    "&clientWebOrigin=http://www.rebaax.com&userId=" + new AppPrefs(context).getUserID();
            Log.v("url", URL);
            new HttpRequest(context, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {
                    brokeragelist.clear();
                    brokeragelist = new parseBrokerage().parse((String) response, brokeragelist);
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
        if (brokeragelist.size() > 0) {
            Collections.reverse(brokeragelist);
            list.setAdapter(new BrokeragesAdapter(context, brokeragelist, new IProperty() {
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
        brokeragelist.clear();
    }

}

