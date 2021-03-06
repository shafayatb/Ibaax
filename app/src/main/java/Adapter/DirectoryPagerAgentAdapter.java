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
import com.ibaax.com.ibaax.AgentProfileActivity2;
import com.ibaax.com.ibaax.AppController;
import com.ibaax.com.ibaax.AppPrefs;
import com.ibaax.com.ibaax.DeveloperProfileActivity;
import com.ibaax.com.ibaax.LoginActivity;
import com.ibaax.com.ibaax.R;

import java.io.Serializable;
import java.util.List;

import Entity.Agent;
import Entity.AppGlobal;
import Event.IProperty;
import ServiceInvoke.AddfavoriteAgentAgency;

/**
 * Created by iBaax on 5/3/16.
 */
public class DirectoryPagerAgentAdapter extends PagerAdapter {

    Context context;
    LayoutInflater inflater;
    List<Agent> list;
    IProperty listener;

    public DirectoryPagerAgentAdapter(Context context, List<Agent> list, IProperty listener) {

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
        View itemView = inflater.inflate(R.layout.row_agent, container,
                false);
        try {
            final int index = position;
            TextView lblName = (TextView) itemView.findViewById(R.id.lbl_rowfindanagent_name);
            TextView lblCompanyName = (TextView) itemView.findViewById(R.id.lbl_rowfindanagent_companyname);
            TextView lblPhone = (TextView) itemView.findViewById(R.id.lbl_rowfindanagent_phone);
            final ImageView FavAgent = (ImageView) itemView.findViewById(R.id.img_fav_agent);
            TextView lblAddress = (TextView) itemView.findViewById(R.id.lbl_rowfindanagent_address);
            TextView lblListening = (TextView) itemView.findViewById(R.id.lbl_rowfindanagent_listening);
            RelativeLayout relaphonrlayout = (RelativeLayout) itemView.findViewById(R.id.rel_agent_list_phone_layout);
            NetworkImageView profile = (NetworkImageView) itemView.findViewById(R.id.img_rowfindanagent_profile);
            if (list.get(position).UserCategoryID == 1) {
                lblName.setText(list.get(position).name);
                lblCompanyName.setVisibility(View.GONE);
                if (list.get(position).OfficePhone.length() > 0) {
                    relaphonrlayout.setVisibility(View.VISIBLE);
                } else {
                    relaphonrlayout.setVisibility(View.GONE);
                }
                lblPhone.setText(list.get(position).OfficePhone);
                lblAddress.setText(list.get(position).fulladdress);
                lblListening.setText(list.get(position).DisplayID);
                try {
                    profile.setDefaultImageResId(R.drawable.avater);
                    profile.setImageUrl(list.get(position).agentThumbnailUrl.replace(" ", "%20"), AppController.getInstance().getImageLoader());
                } catch (Exception ex) {

                    Log.v("error", "profile " + ex.getMessage());
                }
                if (list.get(index).IsLiked) {
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
                                    }
                                }).Post(list.get(index).ContactID + "",
                                        "Agent",
                                        AppGlobal.localHost + "/api/MContacts/AddAgentFavourite");

                            } else {

                                Intent intent = new Intent(context, LoginActivity.class);
                                context.startActivity(intent);
                                return;

                            }


                            if (list.get(index).IsLiked) {
                                list.get(index).IsLiked = false;

                                FavAgent.setImageResource(R.drawable.heart);
                            } else {
                                list.get(index).IsLiked = true;
                                FavAgent.setImageResource(R.drawable.heart2);
                            }
                        } catch (Exception e) {

                        }
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, AgentProfileActivity2.class);

                        intent.putExtra("Agent", (Serializable) list.get(index));
                        context.startActivity(intent);

                    }
                });
            } else {
                lblName.setText(list.get(position).CompanyName);
                lblCompanyName.setVisibility(View.GONE);
                if (list.get(position).OfficePhone.length() > 0) {
                    relaphonrlayout.setVisibility(View.VISIBLE);
                } else {
                    relaphonrlayout.setVisibility(View.GONE);
                }
                lblPhone.setText(list.get(position).OfficePhone);
                lblAddress.setText(list.get(position).fulladdress);
                lblListening.setText(list.get(position).DisplayID);
                try {
                    profile.setDefaultImageResId(R.drawable.developer);
                    profile.setImageUrl(list.get(position).PhotoFileName.replace(" ", "%20"), AppController.getInstance().getImageLoader());
                } catch (Exception ex) {

                    Log.v("error", "profile " + ex.getMessage());
                }
                if (list.get(index).IsLiked) {
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
                                String FavParam = "";
                                if (list.get(index).UserCategoryID == 2) {
                                    FavParam = "RealEstateBrokerage";
                                } else {
                                    FavParam = "BuilderDeveloper";
                                }
                                new AddfavoriteAgentAgency(context, new IProperty() {
                                    @Override
                                    public void btnFavorite_click(Object obj) {

                                    }

                                    @Override
                                    public void onClick(Object obj) {
                                        listener.onClick(obj);
                                    }
                                }).Post(list.get(index).CompanyID + "",
                                        FavParam,
                                        AppGlobal.localHost + "/api/MContacts/AddAgencyFavourite");

                            } else {

                                Intent intent = new Intent(context, LoginActivity.class);
                                context.startActivity(intent);
                                return;

                            }


                            if (list.get(index).IsLiked) {
                                list.get(index).IsLiked = false;

                                FavAgent.setImageResource(R.drawable.heart);
                            } else {
                                list.get(index).IsLiked = true;
                                FavAgent.setImageResource(R.drawable.heart2);
                            }
                        } catch (Exception e) {

                        }
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (list.get(index).UserCategoryID == 4) {
                            Agent dev = list.get(index);
                            String ImageUrl = dev.PhotoFileName.replace(" ", "%20");
                            Intent intent = new Intent(context, DeveloperProfileActivity.class);
                            intent.putExtra("companyName", dev.CompanyName);
                            intent.putExtra("phone", dev.OfficePhone);
                            intent.putExtra("AboutTheCompany", dev.ProfileSummary);
                            intent.putExtra("country", dev.country);
                            intent.putExtra("lat", dev.Latitude);
                            intent.putExtra("lon", dev.Longitude);
                            intent.putExtra("dispID", dev.DisplayID);
                            intent.putExtra("state", dev.state);
                            intent.putExtra("city", dev.city);
                            intent.putExtra("actvlisting", String.valueOf(dev.activeListing));
                            intent.putExtra("agntcount", String.valueOf(dev.ActiveAgentCount));
                            intent.putExtra("locality", dev.locality);
                            intent.putExtra("favorite", dev.IsLiked);
                            intent.putExtra("fulladdress", dev.fulladdress);
                            intent.putExtra("membersince", dev.MemberSince);
                            intent.putExtra("website", dev.agentPermaLink);
                            intent.putExtra("ShortListParam", "BuilderDeveloper");
                            intent.putExtra("companyID", String.valueOf(dev.CompanyID));
                            intent.putExtra("ImageUrl", ImageUrl);
                            intent.putExtra("Recent", "Developers");
                            context.startActivity(intent);
                        } else {
                            Agent dev = list.get(index);
                            String ImageUrl = dev.PhotoFileName.replace(" ", "%20");
                            Intent intent = new Intent(context, DeveloperProfileActivity.class);
                            intent.putExtra("companyName", dev.CompanyName);
                            intent.putExtra("phone", dev.OfficePhone);
                            intent.putExtra("AboutTheCompany", dev.ProfileSummary);
                            intent.putExtra("country", dev.country);
                            intent.putExtra("favorite", dev.IsLiked);
                            intent.putExtra("lat", dev.Latitude);
                            intent.putExtra("lon", dev.Longitude);
                            intent.putExtra("dispID", dev.DisplayID);
                            intent.putExtra("actvlisting", String.valueOf(dev.activeListing));
                            intent.putExtra("agntcount", String.valueOf(dev.ActiveAgentCount));
                            intent.putExtra("state", dev.state);
                            intent.putExtra("city", dev.city);
                            intent.putExtra("locality", dev.locality);
                            intent.putExtra("fulladdress", dev.fulladdress);
                            intent.putExtra("membersince", dev.MemberSince);
                            intent.putExtra("ShortListParam", "RealEstateBrokerage");
                            intent.putExtra("website", dev.agentPermaLink);
                            intent.putExtra("companyID", String.valueOf(dev.CompanyID));
                            intent.putExtra("ImageUrl", ImageUrl);
                            intent.putExtra("Recent", "Brokerages");
                            context.startActivity(intent);
                        }

                    }
                });
            }
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
