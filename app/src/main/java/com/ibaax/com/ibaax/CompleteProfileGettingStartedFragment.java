package com.ibaax.com.ibaax;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by iBaax on 5/5/16.
 */
public class CompleteProfileGettingStartedFragment extends Fragment {

    Context context;
    public CompleteProfileGettingStartedFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_getting_started, container, false);
        context=getActivity();
        Button getStarted=(Button)rootView.findViewById(R.id.btn_get_started);

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CompleteProfileActivity)context).changePager();
            }
        });

        return rootView;
    }
}

