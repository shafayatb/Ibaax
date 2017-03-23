package com.ibaax.com.ibaax;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Adapter.SaveSearchListAdapter;
import Entity.AppGlobal;
import Entity.Dictunary;
import Event.IHttpResponse;
import Plugins.HelperFunctions;
import Plugins.JSONParser;
import ServiceInvoke.HttpRequest;
import UI.GridSpacingItemDecoration;


/**
 * A simple {@link Fragment} subclass.
 */
public class SaveSearchListFragment extends Fragment {

    Context context;
    RecyclerView recycleSaveSearch;
    List<Dictunary> SaveSearchList = new ArrayList<>();
    int PageNumber = 0;
    ProgressBar progressBar;
    TextView lblErrorMessage;
    SaveSearchListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        View rootView = inflater.inflate(R.layout.fragment_save_search_list, container, false);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_save_search);
        lblErrorMessage = (TextView) rootView.findViewById(R.id.lbl_savesearch_error);

        recycleSaveSearch = (RecyclerView) rootView.findViewById(R.id.recycle_save_search);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 1);
        recycleSaveSearch.setLayoutManager(mLayoutManager);
        recycleSaveSearch.addItemDecoration(new GridSpacingItemDecoration(1, HelperFunctions.dpToPx(context, 5), true));
        recycleSaveSearch.setItemAnimator(new DefaultItemAnimator());
        HttpGetSavedSearches(PageNumber);

        return rootView;
    }

    void HttpGetSavedSearches(int pageno) {

        String URL = AppGlobal.localHost + "/api/MSavedSearch/GetSavedSearchList?pageSize=100&pageNo=" + pageno;

        new HttpRequest(context, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {

                try {
                    JSONObject jsonObject = new JSONObject((String) response);
                    JSONArray jsonArray = jsonObject.getJSONArray("list");
                    int length = jsonArray.length();
                    for (int i = 0; i < length; i++) {
                        JSONObject jo = jsonArray.getJSONObject(i);
                        Dictunary save = new Dictunary();
                        save.Name = JSONParser.parseString(jo, "SavedSearchName");
                        save.ID = JSONParser.parseInt(jo, "SavedSearchID");
                        save.URL = JSONParser.parseString(jo, "SavedSearchUrl");
                        if (JSONParser.parseString(jo, "Keywords").length() > 0) {
                            save.Title = JSONParser.parseString(jo, "Keywords");
                        } else {
                            save.Title = "N/A";
                        }
                        save.ShortCuts = HelperFunctions.ChangeDateFormat(JSONParser.parseString(jo, "CreateDate"));
                        SaveSearchList.add(save);
                    }

                    Bind();

                } catch (Exception e) {
                    progressBar.setVisibility(View.GONE);
                    lblErrorMessage.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void RequestFailed(String response) {

            }
        }).JSONObjectGetRequest(null, URL);


    }

    void Bind() {

        if (SaveSearchList.size() > 0) {
            adapter = new SaveSearchListAdapter(context, SaveSearchList);
            recycleSaveSearch.setAdapter(adapter);
            progressBar.setVisibility(View.GONE);
            lblErrorMessage.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            lblErrorMessage.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (context != null && !hidden) {
            String URL = AppGlobal.localHost + "/api/MSavedSearch/GetSavedSearchList?pageSize=20&pageNo=0";
            new HttpRequest(context, new IHttpResponse() {
                @Override
                public void RequestSuccess(Object response) {

                    try {
                        List<Dictunary> changeList = new ArrayList<Dictunary>();
                        JSONObject jsonObject = new JSONObject((String) response);
                        JSONArray jsonArray = jsonObject.getJSONArray("list");
                        int length = jsonArray.length();
                        for (int i = 0; i < length; i++) {
                            JSONObject jo = jsonArray.getJSONObject(i);
                            Dictunary save = new Dictunary();
                            save.Name = JSONParser.parseString(jo, "SavedSearchName");
                            save.ID = JSONParser.parseInt(jo, "SavedSearchID");
                            save.URL = JSONParser.parseString(jo, "SavedSearchUrl");
                            if (JSONParser.parseString(jo, "Keywords").length() > 0) {
                                save.Title = JSONParser.parseString(jo, "Keywords");
                            } else {
                                save.Title = "N/A";
                            }
                            save.ShortCuts = HelperFunctions.ChangeDateFormat(JSONParser.parseString(jo, "CreateDate"));
                            changeList.add(save);
                        }
                        if (SaveSearchList.size() > 0) {
                            if (changeList.get(0).ID != SaveSearchList.get(0).ID) {
                                SaveSearchList.clear();
                                SaveSearchList.addAll(changeList);
                                adapter.notifyDataSetChanged();
                            }
                        }


                    } catch (Exception e) {
                        progressBar.setVisibility(View.GONE);
                        lblErrorMessage.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void RequestFailed(String response) {

                }
            }).JSONObjectGetRequest(null, URL);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        String URL = AppGlobal.localHost + "/api/MSavedSearch/GetSavedSearchList?pageSize=20&pageNo=0";
        new HttpRequest(context, new IHttpResponse() {
            @Override
            public void RequestSuccess(Object response) {

                try {
                    if (SaveSearchResultActivity.IS_DELETED || SaveSearchResultActivity.IS_RENAMED) {
                        List<Dictunary> changeList = new ArrayList<Dictunary>();
                        JSONObject jsonObject = new JSONObject((String) response);
                        JSONArray jsonArray = jsonObject.getJSONArray("list");
                        int length = jsonArray.length();
                        for (int i = 0; i < length; i++) {
                            JSONObject jo = jsonArray.getJSONObject(i);
                            Dictunary save = new Dictunary();
                            save.Name = JSONParser.parseString(jo, "SavedSearchName");
                            save.ID = JSONParser.parseInt(jo, "SavedSearchID");
                            save.URL = JSONParser.parseString(jo, "SavedSearchUrl");
                            if (JSONParser.parseString(jo, "Keywords").length() > 0) {
                                save.Title = JSONParser.parseString(jo, "Keywords");
                            } else {
                                save.Title = "N/A";
                            }
                            save.ShortCuts = HelperFunctions.ChangeDateFormat(JSONParser.parseString(jo, "CreateDate"));
                            changeList.add(save);
                        }
                        if (SaveSearchList.size() > 0) {

                            SaveSearchList.clear();
                            SaveSearchList.addAll(changeList);
                            adapter.notifyDataSetChanged();

                        }
                    }

                } catch (Exception e) {
                    progressBar.setVisibility(View.GONE);
                    lblErrorMessage.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void RequestFailed(String response) {

            }
        }).JSONObjectGetRequest(null, URL);

    }
}
