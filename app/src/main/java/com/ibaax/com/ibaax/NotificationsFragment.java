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

import Plugins.HelperFunctions;
import UI.GridSpacingItemDecoration;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends Fragment {

    Context context;
    RecyclerView recycleNotifications;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);

        recycleNotifications = (RecyclerView) rootView.findViewById(R.id.recycle_notifications);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 1);
        recycleNotifications.setLayoutManager(mLayoutManager);
        recycleNotifications.addItemDecoration(new GridSpacingItemDecoration(1, HelperFunctions.dpToPx(context, 5), true));
        recycleNotifications.setItemAnimator(new DefaultItemAnimator());

        return rootView;
    }


}
