package Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
 * Created by iBaax on 6/7/16.
 */
public class DirectoryListAdapter extends BaseAdapter {

    List<Agent> list;
    Context context;
    IProperty listener;

    public DirectoryListAdapter(Context context, List<Agent> list, IProperty listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v = view;
        final ViewHolder holder;


        try {
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.row_agent, null);
                holder = new ViewHolder();

                holder.lblName = (TextView) v.findViewById(R.id.lbl_rowfindanagent_name);
                holder.lblCompanyName = (TextView) v.findViewById(R.id.lbl_rowfindanagent_companyname);
                holder.lblPhone = (TextView) v.findViewById(R.id.lbl_rowfindanagent_phone);
                holder.lblAddress = (TextView) v.findViewById(R.id.lbl_rowfindanagent_address);
                holder.FavAgent = (ImageView) v.findViewById(R.id.img_fav_agent);
                holder.lblListening = (TextView) v.findViewById(R.id.lbl_rowfindanagent_listening);
                holder.profile = (NetworkImageView) v.findViewById(R.id.img_rowfindanagent_profile);
                holder.relaphonrlayout = (RelativeLayout) v.findViewById(R.id.rel_agent_list_phone_layout);
                v.setTag(holder);

            } else {
                holder = (ViewHolder) v.getTag();
            }
            if (list.get(i).UserCategoryID == 1) {
                holder.lblName.setText(list.get(i).name);
                holder.lblCompanyName.setVisibility(View.VISIBLE);
                if (list.get(i).CompanyName.length() > 0) {
                    holder.lblCompanyName.setText(list.get(i).CompanyName);
                } else {
                    holder.lblCompanyName.setText("N/A");
                }
                if (list.get(i).mobile.length() > 0) {
                    holder.relaphonrlayout.setVisibility(View.VISIBLE);
                } else {
                    holder.relaphonrlayout.setVisibility(View.GONE);
                }
                holder.lblPhone.setText(list.get(i).mobile);

                holder.lblAddress.setText(list.get(i).fulladdress);
                holder.lblListening.setText(list.get(i).DisplayID);
                try {
                    holder.profile.setDefaultImageResId(R.drawable.avater);
                    holder.profile.setImageUrl(list.get(i).agentThumbnailUrl.replace(" ", "%20"), AppController.getInstance().getImageLoader());
                } catch (Exception ex) {

                    Log.v("error", "holder.profile " + ex.getMessage());
                }
                if (list.get(i).IsLiked) {
                    holder.FavAgent.setImageResource(R.drawable.heart2);
                } else {
                    holder.FavAgent.setImageResource(R.drawable.heart);
                }
                holder.FavAgent.setOnClickListener(new View.OnClickListener() {
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
                                }).Post(list.get(i).ContactID + "",
                                        "Agent",
                                        AppGlobal.localHost + "/api/MContacts/AddAgentFavourite");

                            } else {

                                Intent intent = new Intent(context, LoginActivity.class);
                                context.startActivity(intent);
                                return;

                            }


                            if (list.get(i).IsLiked) {
                                list.get(i).IsLiked = false;

                                holder.FavAgent.setImageResource(R.drawable.heart);
                            } else {
                                list.get(i).IsLiked = true;
                                holder.FavAgent.setImageResource(R.drawable.heart2);
                            }
                        } catch (Exception e) {

                        }
                    }
                });
                v.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //   listener.onClick(list.get(index));


                        Intent intent = new Intent(context, AgentProfileActivity2.class);

                        intent.putExtra("Agent", (Serializable) list.get(i));
                        context.startActivity(intent);

                    }
                });
            } else {
                holder.lblName.setText(list.get(i).CompanyName);
                holder.lblCompanyName.setVisibility(View.GONE);
                if (list.get(i).OfficePhone.length() > 0) {
                    holder.relaphonrlayout.setVisibility(View.VISIBLE);

                } else {
                    holder.relaphonrlayout.setVisibility(View.GONE);
                }
                holder.lblPhone.setText(list.get(i).OfficePhone);
                holder.lblAddress.setText(list.get(i).fulladdress);
                holder.lblListening.setText(list.get(i).DisplayID);

                String ImageUrl = list.get(i).PhotoFileName.replace(" ", "%20");
                holder.profile.setDefaultImageResId(R.drawable.developer);
                if (list.get(i).IsLiked) {
                    holder.FavAgent.setImageResource(R.drawable.heart2);
                } else {
                    holder.FavAgent.setImageResource(R.drawable.heart);
                }
                holder.profile.setImageUrl(ImageUrl, AppController.getInstance().getImageLoader());

                holder.FavAgent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            AppPrefs pref = new AppPrefs(context);
                            String UserID = pref.getUserID();

                            if (UserID.length() > 1) {
                                String FavParam = "";
                                if (list.get(i).UserCategoryID == 2) {
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
                                        if (obj == "false") {
                                            list.get(i).IsLiked = false;
                                            holder.FavAgent.setImageResource(R.drawable.heart);
                                        }
                                    }
                                }).Post(list.get(i).CompanyID + "",
                                        FavParam,
                                        AppGlobal.localHost + "/api/MContacts/AddAgencyFavourite");

                            } else {

                                Intent intent = new Intent(context, LoginActivity.class);
                                context.startActivity(intent);
                                return;

                            }


                            if (list.get(i).IsLiked) {
                                list.get(i).IsLiked = false;

                                holder.FavAgent.setImageResource(R.drawable.heart);
                            } else {
                                list.get(i).IsLiked = true;
                                holder.FavAgent.setImageResource(R.drawable.heart2);
                            }
                        } catch (Exception e) {

                        }
                    }
                });
                v.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (list.get(i).UserCategoryID == 4) {
                            Agent dev = list.get(i);
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
                            intent.putExtra("companyID", String.valueOf(dev.CompanyID));
                            intent.putExtra("ImageUrl", ImageUrl);
                            intent.putExtra("ShortListParam", "BuilderDeveloper");
                            intent.putExtra("Recent", "Developers");
                            context.startActivity(intent);
                        } else {
                            Agent dev = list.get(i);
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
                            intent.putExtra("website", dev.agentPermaLink);
                            intent.putExtra("ShortListParam", "RealEstateBrokerage");
                            intent.putExtra("companyID", String.valueOf(dev.CompanyID));
                            intent.putExtra("ImageUrl", ImageUrl);
                            intent.putExtra("Recent", "Brokerages");
                            context.startActivity(intent);
                        }

                    }
                });
            }


        } catch (Exception ex) {
            Log.v("error", ex.getMessage());
        }


        return v;
    }

    static class ViewHolder {

        public TextView lblName;
        public TextView lblCompanyName;

        public TextView lblPhone;
        public ImageView FavAgent;
        public TextView lblAddress;
        public TextView lblListening;
        RelativeLayout relaphonrlayout;
        NetworkImageView profile;

    }


}
