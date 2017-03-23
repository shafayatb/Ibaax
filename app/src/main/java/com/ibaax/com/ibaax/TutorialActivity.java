package com.ibaax.com.ibaax;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import Adapter.MyCustomPagerAdapter;
import Entity.Tutorial;

public class TutorialActivity extends AppCompatActivity {

    ViewPager tutorialPager;

    List<Tutorial> tutorialList = new ArrayList<Tutorial>();
    OnClickEvent listener = new OnClickEvent() {
        @Override
        public void onNextClick(int position) {

            tutorialPager.setCurrentItem(position + 1, true);


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        getSupportActionBar().hide();
        try {
            tutorialPager = (ViewPager) findViewById(R.id.tutorial_silder);
            TutorialListInit();
            MyCustomPagerAdapter adapter = new MyCustomPagerAdapter(this, tutorialList, listener);
            tutorialPager.setAdapter(adapter);


        } catch (Exception e) {
            Log.e("esty", "TutorialActivity/OnCreate/Error: " + e.getMessage());
        }

    }

    void TutorialListInit() {

        tutorialList.add(new Tutorial(R.drawable.tutorial_search, "Find your next home",
                "Quickly search for properties for sale or rent in your location"));
        tutorialList.add(new Tutorial(R.drawable.tutorial_people, "Find Real Estate Agents",
                "Search for Agents to help you buy, sell or rent your property"));
        tutorialList.add(new Tutorial(R.drawable.tutorial_fav, "Favorites & Saved Searches",
                "Easily add your favorite properties to your personal shortlist and saved searches"));
        tutorialList.add(new Tutorial(R.drawable.tutorial_home, "Sell or Rent your home",
                "Post your property for sale or rent on the iBaax Marketplace for great results"));

    }

    public interface OnClickEvent {
        public void onNextClick(int position);
    }

}
