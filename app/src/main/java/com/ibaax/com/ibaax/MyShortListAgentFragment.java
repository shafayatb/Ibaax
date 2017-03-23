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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Adapter.AgentAdapter;
import Entity.Agent;
import Entity.AppGlobal;
import Event.IHttpResponse;
import Event.IProperty;
import JSOINParsing.parseSearchAgent;
import ServiceInvoke.HttpRequest;
import UI.MessageBox;

/**
 * Created by iBaax on 4/4/16.
 */
public class MyShortListAgentFragment extends Fragment {

    static List<Agent> agentlist = new ArrayList<>();
    ListView listview;
    ProgressBar progress;
    Context context;
    AppPrefs pref;
    AgentAdapter adapter;
    View NoFavorites;

    public MyShortListAgentFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        pref = new AppPrefs(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_my_short_list, container, false);
        try {
            listview = (ListView) rootView.findViewById(R.id.listView_Favorite);
            listview.setPadding(10, 10, 10, 10);
            progress = (ProgressBar) rootView.findViewById(R.id.progressBar_favorite);
            NoFavorites = (View) rootView.findViewById(R.id.include_nofavoriteslayout);
            context = getActivity();
            pref = new AppPrefs(context);
            if (agentlist.size() > 0) {
                Bind();
            } else {
                HttpGetAgentFavorites();
            }

        } catch (Exception ex) {
            MessageBox.Show(getActivity(), ex.getMessage());
        }
        return rootView;
    }

    void HttpGetAgentFavorites() {
        try {

            String URL = AppGlobal.localHost + "/api/MContacts/GetShortListedContacts?userId=" + new AppPrefs(context).getUserID()
                    + "&clientWebOrigin=http://www.rebaax.com";
            new HttpRequest(context, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {
                    try {
                        agentlist.clear();
                        JSONObject jsonObject = new JSONObject((String) response);
                        agentlist = new parseSearchAgent().parse(jsonObject.getString("list"), agentlist);
                        Bind();
                    } catch (JSONException e) {
                        Log.e("error", e.getMessage());
                    }
                }

                @Override
                public void RequestFailed(String response) {

                }
            }).JSONObjectGetRequest(null,URL);

        } catch (Exception e) {
            Log.e("error", "AgentFavorites Error: " + e.getMessage());
        }
    }

    void Bind() {
        if (agentlist.size() > 0) {

            adapter = new AgentAdapter(context, agentlist, new IProperty() {
                @Override
                public void btnFavorite_click(Object obj) {

                }

                @Override
                public void onClick(Object obj) {
                    String ID = (String) obj;
                    for (int i = 0; i < agentlist.size(); i++) {
                        if (ID.equals(String.valueOf(agentlist.get(i).ContactID))) {
                            agentlist.remove(i);
                            adapter.notifyDataSetChanged();
                            if (agentlist.size() < 1) {
                                NoFavorites.setVisibility(View.VISIBLE);
                                listview.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            });
            listview.setAdapter(adapter);
            progress.setVisibility(View.GONE);
            listview.setVisibility(View.VISIBLE);
        } else {
            listview.setVisibility(View.GONE);
            NoFavorites.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            String UserID = pref.getUserID();
            String URL = AppGlobal.localHost + "/api/MContacts/GetShortListedContacts?userId=" + UserID
                    + "&clientWebOrigin=http://www.rebaax.com";
            Log.v("esty", URL);
            new HttpRequest(context, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {
                    List<Agent> favlist = new ArrayList<>();
                    favlist = new parseSearchAgent().parse((String) response, favlist);
                    if (agentlist.size() != favlist.size()) {
                        agentlist.clear();
                        agentlist.addAll(favlist);
                        Bind();
                    }
                }

                @Override
                public void RequestFailed(String response) {

                }
            }).MakeJsonArrayReq(URL);
        } catch (Exception ex) {

            MessageBox.Show(context, "getData:" + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        agentlist.clear();
    }
}
