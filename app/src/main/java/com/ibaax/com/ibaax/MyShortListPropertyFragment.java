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

import java.util.ArrayList;
import java.util.List;

import Adapter.FeaturedPropertyAdapter;
import Entity.AppGlobal;
import Entity.Property;
import Event.IHttpResponse;
import Event.IProperty;
import JSOINParsing.ParseProperty;
import Plugins.HelperFunctions;
import ServiceInvoke.HttpRequest;
import UI.MessageBox;

public class MyShortListPropertyFragment extends Fragment {

    static List<Property> list = new ArrayList<>();
    ListView listview;
    ProgressBar progress;
    Context context;
    AppPrefs pref;
    FeaturedPropertyAdapter adapter;
    View NoFavorites;

    public MyShortListPropertyFragment() {

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
            progress = (ProgressBar) rootView.findViewById(R.id.progressBar_favorite);
            NoFavorites = (View) rootView.findViewById(R.id.include_nofavoriteslayout);
            context = getActivity();
            pref = new AppPrefs(context);
            if (list.size() > 0) {
                Bind();
            } else {
                getData();
            }

        } catch (Exception ex) {
            MessageBox.Show(getActivity(), ex.getMessage());
        }
        return rootView;
    }

    private void getData() {
        try {
            progress.setVisibility(View.VISIBLE);
            String UserID = pref.getUserID();
            String URL = AppGlobal.localHost + "/api/MProperty/GetMyShortListedProperty?UserID=" + UserID + "&lang='en'";
            Log.v("esty", URL);
            new HttpRequest(context, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {
                    list.clear();
                    list = new ParseProperty().parse((String) response, list);
                    list = HelperFunctions.ChangeCurrency(list, pref);
                    Bind();
                }

                @Override
                public void RequestFailed(String response) {
                    progress.setVisibility(View.GONE);
                }
            }).MakeJsonArrayReq(URL);
        } catch (Exception ex) {
            MessageBox.Show(context, "getData:" + ex.getMessage());
        }

    }


    private void Bind() {
        if (list.size() > 0) {

            adapter = new FeaturedPropertyAdapter(context, list, new IProperty() {
                @Override
                public void btnFavorite_click(Object obj) {

                }

                @Override
                public void onClick(Object obj) {

                    String ID = (String) obj;
                    for (int i = 0; i < list.size(); i++) {
                        if (ID.equals(String.valueOf(list.get(i).PropertyID))) {
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
            listview.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            NoFavorites.setVisibility(View.GONE);
        } else {
            progress.setVisibility(View.GONE);
            listview.setVisibility(View.GONE);
            NoFavorites.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            String UserID = pref.getUserID();
            String URL = AppGlobal.localHost + "/api/MProperty/GetMyShortListedProperty?UserID=" + UserID + "&lang='en'";
            Log.v("esty", URL);
            new HttpRequest(context, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {
                    List<Property> favlist = new ArrayList<>();
                    favlist = new ParseProperty().parse((String) response, favlist);
                    favlist = HelperFunctions.ChangeCurrency(favlist, pref);
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
