package com.ibaax.com.ibaax;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import Adapter.NavigationListDrawerAdapter;
import Entity.AppGlobal;
import Entity.Drawer;
import Plugins.TextBoxHandler;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDrawer extends Fragment {

    RelativeLayout relHeader;
    LinearLayout lnrLogin;
    LinearLayout lnrProfile;
    AppPrefs prefs;
    TextView lblUserName, lblEmail;
    UI.CircleImageView imgUserImage;
    ImageLoader loader = AppController.getInstance().getImageLoader();
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View containerView;
    private ListView mDrawerList;
    private FragmentDrawerListener listener;

    public FragmentDrawer() {
        // Required empty public constructor
    }

    public static List<Drawer> getData() {
        List<Drawer> mydrawer = new ArrayList<Drawer>();

        mydrawer.add(new Drawer("Search Property", R.drawable.ic_drawer_home, "two"));
        mydrawer.add(new Drawer("Search Directory", R.drawable.ic_drawer_agent, "two"));
        mydrawer.add(new Drawer("Recently Viewed", R.drawable.ic_drawer_viewed, "two"));
        mydrawer.add(new Drawer("My Shortlist", R.drawable.ic_drawer_heart, "two"));
        mydrawer.add(new Drawer("Saved Searches", R.drawable.ic_drawer_saved_search, "two"));
        mydrawer.add(new Drawer("Notifications", R.drawable.ic_drawer_notifications, "two"));
        mydrawer.add(new Drawer("SELL/RENT", "noitem"));
        mydrawer.add(new Drawer("Post a Property", R.drawable.ic_drawer_add, "two"));
        mydrawer.add(new Drawer("My Properties", R.drawable.ic_drawer_myproperties, "two"));
        mydrawer.add(new Drawer("MORE", "noitem"));
        mydrawer.add(new Drawer("Settings", R.drawable.ic_drawer_settings, "two"));
        mydrawer.add(new Drawer("Rate the app", R.drawable.ic_drawer_rate, "two"));
        mydrawer.add(new Drawer("Share the app", R.drawable.ic_drawer_share, "two"));
        return mydrawer;
    }

    @Override
    public void onResume() {
        HeaderType();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_drawer, container, false);
        prefs = new AppPrefs(getActivity());
        mDrawerList = (ListView) layout.findViewById(R.id.drawerList);
        NavigationListDrawerAdapter adapter = new NavigationListDrawerAdapter(getActivity(), getData());
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        lnrLogin = (LinearLayout) layout.findViewById(R.id.lnr_drawer_signup);
        lnrProfile = (LinearLayout) layout.findViewById(R.id.lnr_drawer_usrprofile);
        relHeader = (RelativeLayout) layout.findViewById(R.id.navigation_header_container);
        lblUserName = (TextView) layout.findViewById(R.id.lbl_user_name);
        lblEmail = (TextView) layout.findViewById(R.id.lbl_user_email);
        imgUserImage = (UI.CircleImageView) layout.findViewById(R.id.img_user_profileimage);
        HeaderType();
        relHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        lnrLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);

            }
        });
        lnrProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyProfileActivity2.class);
                startActivity(intent);

            }
        });
        return layout;
    }

    void HeaderType() {

        if (prefs.gettOAuthToken().length() == 0) {
            lnrLogin.setVisibility(View.VISIBLE);
            lnrProfile.setVisibility(View.GONE);
        } else {
            lnrLogin.setVisibility(View.GONE);
            lnrProfile.setVisibility(View.VISIBLE);
            if (prefs.getUserCategory().equals("2") || prefs.getUserCategory().equals("4")) {
                lblUserName.setText(prefs.getCompanyName());

                loader.get(AppGlobal.ImageHost + "/agency/showcompanylogo/" + prefs.getCompanyID() + "?logofilename="
                                + TextBoxHandler.IsNullOrEmpty(CropImageActivity.filename).replace(" ", "%20"),
                        new ImageLoader.ImageListener() {
                            @Override
                            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                if (response.getBitmap() != null) {
                                    imgUserImage.setImageBitmap(response.getBitmap());
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });

            } else {
                loader.get(AppGlobal.ImageHost + "/CRM/Contact/ShowContactPhoto?contactId=" + prefs.getContactID() + "&contactPhotoFileName="
                                + TextBoxHandler.IsNullOrEmpty(CropImageActivity.filename).replace(" ", "%20"),
                        new ImageLoader.ImageListener() {
                            @Override
                            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                if (response.getBitmap() != null) {
                                    imgUserImage.setImageBitmap(response.getBitmap());
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });

                lblUserName.setText(prefs.getFirstName() + " " + prefs.getLastName());
            }
            lblEmail.setText(prefs.getEmail());


        }
    }

    public void setUp(int fragmentID, DrawerLayout drawerLayout, final Toolbar toolbar) {

        containerView = getActivity().findViewById(fragmentID);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }


        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
                toolbar.setNavigationIcon(R.drawable.ic_drawer);
            }
        });

    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.listener = listener;
    }

    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int position);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        /*This following method handles the Navigation drawer ItemClickEvent*/
        @Override
        public void onItemClick(AdapterView<?> parent, final View view, int position,
                                long id) {

            try {
                // if (dataList.get(position).getTitle() == null)
                {
                    final int pos = position;

                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            listener.onDrawerItemSelected(view, pos);
                            mDrawerLayout.closeDrawer(containerView);
                            //SelectItem(pos);
                        }
                    }, 0);


                    Log.v("esty2", "Drawer selected: " + position);
                }
            } catch (Exception ex) {
                //SelectItem(0);
                listener.onDrawerItemSelected(view, position);
                mDrawerLayout.closeDrawer(containerView);
                Log.v("error", "dd; " + ex.getMessage());
            }

        }
    }

}
