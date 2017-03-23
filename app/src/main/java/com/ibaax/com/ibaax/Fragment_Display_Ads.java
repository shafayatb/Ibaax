package com.ibaax.com.ibaax;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by iBaax on 3/24/16.
 */
public class Fragment_Display_Ads extends Fragment {


    public Fragment_Display_Ads(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_display_ads, container, false);

        Button LearnMore=(Button)v.findViewById(R.id.btn_displayads_learnmore);

        LearnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.rebaax.com/display-ads"));
                startActivity(browserIntent);
            }
        });

        return v;
    }
}
