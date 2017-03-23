package com.ibaax.com.ibaax;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import UI.LockableViewPager;

public class CompleteProfileActivity extends AppCompatActivity {

    SectionsPagerAdapter mSectionsPagerAdapter;

    private LockableViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_complete_profile);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (LockableViewPager) findViewById(R.id.pager_complete_profile);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setSwippable(false);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position) {
                    case 0:
                        toolbar.setVisibility(View.GONE);

                        break;
                    case 1:
                        toolbar.setVisibility(View.VISIBLE);
                        getSupportActionBar().setTitle("Upload Picture");
                        break;
                    case 2:
                        getSupportActionBar().setTitle("About");
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    public void changePager() {

        switch (mViewPager.getCurrentItem()) {
            case 0:
                mViewPager.setCurrentItem(1, true);
                break;
            case 1:
                mViewPager.setCurrentItem(2, true);
                break;
            case 2:
                Intent intent = new Intent(this, NavigationDrawerMainActivity.class);
                NavigationDrawerMainActivity.LastSelectedTab = 0;
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_complete_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new CompleteProfileGettingStartedFragment();
                case 1:
                    return new CompleteProfileUploadPictureFragment();
                case 2:
                    return new CompleteProfileAboutFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (getSupportActionBar() != null) {
                switch (position) {
                    case 0:
                        return "SECTION 1";
                    case 1:

                        return "SECTION 2";
                    case 2:

                        return "SECTION 3";
                }
            }
            return null;
        }
    }
}
