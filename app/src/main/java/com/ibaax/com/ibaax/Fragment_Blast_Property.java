package com.ibaax.com.ibaax;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class Fragment_Blast_Property extends Fragment {


    public Fragment_Blast_Property() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment__blast__property, container, false);

        Button LearnMore=(Button)v.findViewById(R.id.btn_property_blast_learnmore);

        LearnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.rebaax.com/property-email-blast"));
                startActivity(browserIntent);
            }
        });

        return v;
    }


}
