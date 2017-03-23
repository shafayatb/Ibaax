package Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.ibaax.com.ibaax.AppController;
import com.ibaax.com.ibaax.AppPrefs;
import com.ibaax.com.ibaax.DeveloperProfileActivity;
import com.ibaax.com.ibaax.LoginActivity;
import com.ibaax.com.ibaax.R;

import java.util.List;

import Entity.AppGlobal;
import Entity.Brokerages;
import Event.IProperty;
import ServiceInvoke.AddfavoriteAgentAgency;

/**
 * Created by iBaax on 5/4/16.
 */
public class DirectoryPagerBrokerageAdapter extends PagerAdapter {

    Context context;
    LayoutInflater inflater;
    List<Brokerages> list;
    IProperty listener;

    public DirectoryPagerBrokerageAdapter(Context context, List<Brokerages> list, IProperty listener) {

        this.context = context;
        this.list = list;
        this.listener = listener;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.row_developers, container,
                false);
        try {
            final int index = position;
            TextView lblName = (TextView) itemView.findViewById(R.id.lbl_rowfindandevelopers_name);
            TextView lblPhone = (TextView) itemView.findViewById(R.id.lbl_rowfindandevelopers_phone);
            final ImageView FavAgent = (ImageView) itemView.findViewById(R.id.img_sendMessage);
            TextView lblAddress = (TextView) itemView.findViewById(R.id.lbl_rowfindandevelopers_address);
            TextView lblListening = (TextView) itemView.findViewById(R.id.lbl_rowfindandevelopers_listening);
            RelativeLayout relaphonrlayout = (RelativeLayout) itemView.findViewById(R.id.rel_developers_list_phone_layout);
            NetworkImageView profile = (NetworkImageView) itemView.findViewById(R.id.img_rowfindandevelopers_profile);

            lblName.setText(list.get(position).companyName);

            if (list.get(position).phone.length() > 0) {
                relaphonrlayout.setVisibility(View.VISIBLE);
            } else {
                relaphonrlayout.setVisibility(View.GONE);
            }
            lblPhone.setText(list.get(position).phone);
            lblAddress.setText(list.get(position).FullAdress);
            lblListening.setText(list.get(position).displayID);
            try {
                profile.setDefaultImageResId(R.drawable.developer);
                profile.setImageUrl(list.get(position).companyLogoUrl.replace(" ", "%20"), AppController.getInstance().getImageLoader());
            } catch (Exception ex) {

                Log.v("error", "profile " + ex.getMessage());
            }
            if (list.get(index).IsFavorite) {
                FavAgent.setImageResource(R.drawable.heart2);
            } else {
                FavAgent.setImageResource(R.drawable.heart);
            }
            ((ViewPager) container).addView(itemView);
            FavAgent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        AppPrefs pref = new AppPrefs(context);
                        String UserID = pref.getUserID();

                        if (UserID.length() > 1) {
                            new AddfavoriteAgentAgency(context, new IProperty() {
                                @Override
                                public void btnFavorite_click(Object obj) {

                                }

                                @Override
                                public void onClick(Object obj) {
                                    listener.onClick(obj);
                                    if (obj == "false") {
                                        list.get(index).IsFavorite = false;
                                        FavAgent.setImageResource(R.drawable.heart);
                                    }
                                }
                            }).Post(list.get(index).companyId + "",
                                    "RealEstateBrokerage",
                                    AppGlobal.localHost + "/api/MContacts/AddAgencyFavourite");

                        } else {

                            Intent intent = new Intent(context, LoginActivity.class);
                            context.startActivity(intent);
                            return;

                        }


                        if (list.get(index).IsFavorite) {
                            list.get(index).IsFavorite = false;

                            FavAgent.setImageResource(R.drawable.heart);
                        } else {
                            list.get(index).IsFavorite = true;
                            FavAgent.setImageResource(R.drawable.heart2);
                        }
                    } catch (Exception e) {

                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Brokerages dev = list.get(index);
                    String ImageUrl = dev.companyLogoUrl.replace(" ", "%20");
                    Intent intent = new Intent(context, DeveloperProfileActivity.class);
                    intent.putExtra("companyName", dev.companyName);
                    intent.putExtra("phone", dev.phone);
                    intent.putExtra("AboutTheCompany", dev.AboutTheCompany);
                    intent.putExtra("country", dev.country);
                    intent.putExtra("lat", dev.Latitude);
                    intent.putExtra("lon", dev.Longitude);
                    intent.putExtra("dispID", dev.displayID);
                    intent.putExtra("state", dev.state);
                    intent.putExtra("city", dev.city);
                    intent.putExtra("actvlisting", String.valueOf(dev.activeListing));
                    intent.putExtra("agntcount", String.valueOf(dev.activeAgents));
                    intent.putExtra("locality", dev.locality);
                    intent.putExtra("favorite", dev.IsFavorite);
                    intent.putExtra("fulladdress", dev.FullAdress);
                    intent.putExtra("membersince", dev.MemberSince);
                    intent.putExtra("website", dev.companyPermaLink);
                    intent.putExtra("companyID", String.valueOf(dev.companyId));
                    intent.putExtra("ImageUrl", ImageUrl);
                    intent.putExtra("Recent", "Brokerages");
                    context.startActivity(intent);

                }
            });
        } catch (Exception e) {
            Log.v("esty", "MyCustomPagerAdater/intantiateItem/Error: " + e.getMessage());
        }
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((LinearLayout) object);

    }
}