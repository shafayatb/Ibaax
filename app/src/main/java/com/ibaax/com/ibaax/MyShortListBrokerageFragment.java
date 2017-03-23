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

import Adapter.BrokeragesAdapter;
import Entity.AppGlobal;
import Entity.Brokerages;
import Event.IHttpResponse;
import Event.IProperty;
import JSOINParsing.parseBrokerage;
import ServiceInvoke.HttpRequest;
import UI.MessageBox;

/**
 * Created by iBaax on 4/4/16.
 */
public class MyShortListBrokerageFragment extends Fragment {

    static List<Brokerages> list = new ArrayList<>();
    ListView listview;
    ProgressBar progress;
    Context context;
    AppPrefs pref;
    BrokeragesAdapter adapter;
    View NoFavorites;

    public MyShortListBrokerageFragment() {

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
            if (list.size() > 0) {
                Bind();
            } else {
                HttpGetBrokeragesFavorites();
            }

        } catch (Exception ex) {
            MessageBox.Show(getActivity(), ex.getMessage());
        }
        return rootView;
    }

    void HttpGetBrokeragesFavorites() {
        try {

            String URL2 = AppGlobal.localHost + "/api/MCompany/GetShortListedCompanies?userId=" + new AppPrefs(context).getUserID() +
                    "&userCategoryId=2&clientWebOrigin=" + AppGlobal.ImageHost;
            new HttpRequest(context, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {

                    try {
                        list.clear();
                        JSONObject jsonObject = new JSONObject((String) response);
                        list = new parseBrokerage().parse(jsonObject.getString("list"), list);
                        Bind();
                    } catch (JSONException e) {
                        Log.e("error", e.getMessage());
                    }

                }

                @Override
                public void RequestFailed(String response) {

                }
            }).JSONObjectGetRequest(null,URL2);

        } catch (Exception e) {
            Log.e("error", "AgentFavorites Error: " + e.getMessage());
        }
    }

    void Bind() {
        if (list.size() > 0) {

            adapter = new BrokeragesAdapter(context, list, new IProperty() {
                @Override
                public void btnFavorite_click(Object obj) {

                }

                @Override
                public void onClick(Object obj) {
                    String ID = (String) obj;
                    for (int i = 0; i < list.size(); i++) {
                        if (ID.equals(String.valueOf(list.get(i).companyId))) {
                            list.remove(i);
                            adapter.notifyDataSetChanged();
                            if (list.size() < 1) {
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
            String URL = AppGlobal.localHost + "/api/MCompany/GetShortListedCompanies?userId=" + UserID
                    + "&clientWebOrigin=http://www.rebaax.com";
            ;
            new HttpRequest(context, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {
                    List<Brokerages> favlist = new ArrayList<>();
                    List<Brokerages> brolist = new ArrayList<>();
                    brolist = new parseBrokerage().parse((String) response, brolist);
                    for (int i = 0; i < brolist.size(); i++) {
                        if (brolist.get(i).UserCategoryID == 2) {
                            favlist.add(brolist.get(i));
                        }
                    }
                    if (list.size() != favlist.size()) {
                        list.clear();
                        list.addAll(favlist);
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
        list.clear();
    }


}