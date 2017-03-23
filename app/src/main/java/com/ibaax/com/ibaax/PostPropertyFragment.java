package com.ibaax.com.ibaax;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

public class PostPropertyFragment extends Fragment {

    Context context;

    Button btnPropertySell, btnPropertyRent;

    String PropertyTransactionTypeCategoryID;



    AppPrefs prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_postproperty_front_screen, container, false);
        context = getActivity();
        prefs = new AppPrefs(context);
        init(rootView);

        return rootView;
    }


    private void init(View rootView) {
        //stepinit Views
        //buttons
        btnPropertySell = (Button) rootView.findViewById(R.id.btn_postproperty_post_sale);
        btnPropertyRent = (Button) rootView.findViewById(R.id.btn_postproperty_post_rent);

        //Stepinit
        /*btnPropertySell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PropertyTransactionTypeCategoryID = "1";
                Intent intent = new Intent(context, PostPropertyActivity.class);
                intent.putExtra("PropertyTransactionTypeCategoryID", PropertyTransactionTypeCategoryID);

                startActivity(intent);
            }
        });*/
        btnPropertyRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PropertyTransactionTypeCategoryID = "1";
                Intent intent = new Intent(context, PostPropertyActivity.class);
                intent.putExtra("PropertyTransactionTypeCategoryID", PropertyTransactionTypeCategoryID);
                
                startActivity(intent);

            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
    }


}
