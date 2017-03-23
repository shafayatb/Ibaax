package com.ibaax.com.ibaax;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCRM extends Fragment {


    public FragmentCRM() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v=inflater.inflate(R.layout.fragment_crm, container, false);

        ImageView imgYoutube=(ImageView)v.findViewById(R.id.img_crm_youtube);
        Button btnLearnMore=(Button)v.findViewById(R.id.btn_crm_learnmore);

        imgYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=7OOy0gNKE2I")));
            }
        });

        btnLearnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.rebaax.com/real-estate-crm"));
                startActivity(browserIntent);
            }
        });

        return v;
    }


}
