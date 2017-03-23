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

import Adapter.AgentAdapter;
import DataAccessLayer.RecentlyViewedLayer;
import Entity.Agent;
import Entity.AppGlobal;
import Event.IHttpResponse;
import Event.IProperty;
import JSOINParsing.parseSearchAgent;
import ServiceInvoke.HttpRequest;

/**
 * Created by iBaax on 4/3/16.
 */
public class FragmentRecentlyViewedAgent extends Fragment {


    static List<Agent> agentlist = new ArrayList<>();
    ProgressBar progress;
    ListView list;
    RecentlyViewedLayer recentlyViewedLayer;
    List<Integer> agentIDList = new ArrayList<>();
    TextView lblNoRecentlyViewed;
    Context context;

    public FragmentRecentlyViewedAgent() {
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
        list.setPadding(10, 10, 10, 10);
        lblNoRecentlyViewed = (TextView) rootView.findViewById(R.id.lbl_NoRecentlyViewedMessage);
        lblNoRecentlyViewed.setText("You currently don't have any recently viewed agents.");
        if (agentlist.size() > 0) {
            Bind();
        } else {
            HttpGetRecentlyViewed();
        }

        return rootView;
    }

    void HttpGetRecentlyViewed() {
        try {
            agentIDList.clear();
            agentIDList = recentlyViewedLayer.getRecentlyViewed("Agent");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < agentIDList.size(); i++) {
                sb.append(agentIDList.get(i) + ",");
            }
            if (sb.toString().length() > 0) {
                sb.delete(sb.length() - 1, sb.length());
            }
            String PIDs = sb.toString();
            sb.delete(0, sb.length());
            String URL = AppGlobal.localHost + "/api/MContacts/GetRecentlyViewAgents?contactIds=" + PIDs +
                    "&clientWebOrigin=" + AppGlobal.ImageHost + "&userId=" + new AppPrefs(context).getUserID();
            Log.v("url", URL);
            new HttpRequest(context, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {
                    agentlist.clear();
                    agentlist = new parseSearchAgent().parse((String) response, agentlist);
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
        if (agentlist.size() > 0) {
            Collections.reverse(agentlist);
            list.setAdapter(new AgentAdapter(context, agentlist, new IProperty() {
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
        agentlist.clear();
    }
}
