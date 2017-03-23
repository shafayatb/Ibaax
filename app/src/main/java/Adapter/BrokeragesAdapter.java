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
 * Created by S.R Rain on 1/25/2016.
 */
public class BrokeragesAdapter extends BaseAdapter {

    List<Brokerages> list;
    Context context;
    IProperty listener;

    public BrokeragesAdapter(Context context, List<Brokerages> list, IProperty listener) {

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
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        final ViewHolder holder;

        final int index = i;

        try {
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.row_developers, viewGroup, false);
                holder = new ViewHolder();
                holder.lblName = (TextView) v.findViewById(R.id.lbl_rowfindandevelopers_name);
                holder.lblPhone = (TextView) v.findViewById(R.id.lbl_rowfindandevelopers_phone);
                holder.lblAddress = (TextView) v.findViewById(R.id.lbl_rowfindandevelopers_address);
                holder.lblListening = (TextView) v.findViewById(R.id.lbl_rowfindandevelopers_listening);
                holder.profile = (NetworkImageView) v.findViewById(R.id.img_rowfindandevelopers_profile);
                holder.relaphonrlayout = (RelativeLayout) v.findViewById(R.id.rel_developers_list_phone_layout);
                holder.imgSendMessage = (ImageView) v.findViewById(R.id.img_sendMessage);
                v.setTag(holder);

            } else {
                holder = (ViewHolder) v.getTag();
            }
            holder.lblName.setText(list.get(i).companyName);

            if (list.get(i).phone.length() > 0) {
                holder.relaphonrlayout.setVisibility(View.VISIBLE);

            } else {
                holder.relaphonrlayout.setVisibility(View.GONE);
            }
            if (list.get(i).IsFavorite) {
                holder.imgSendMessage.setImageResource(R.drawable.heart2);
            } else {
                holder.imgSendMessage.setImageResource(R.drawable.heart);
            }
            holder.lblPhone.setText(list.get(i).phone);
            holder.lblAddress.setText(list.get(i).FullAdress);
            holder.lblListening.setText(list.get(i).displayID);
            try {
                holder.profile.setDefaultImageResId(R.drawable.developer);
                holder.profile.setImageUrl(list.get(i).companyLogoUrl.replace(" ", "%20"), AppController.getInstance().getImageLoader());
            } catch (Exception ex) {
                Log.v("error", "     holder.profile " + ex.getMessage());
            }

            holder.imgSendMessage.setOnClickListener(new View.OnClickListener() {
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
                                        holder.imgSendMessage.setImageResource(R.drawable.heart);
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

                            holder.imgSendMessage.setImageResource(R.drawable.heart);
                        } else {
                            list.get(index).IsFavorite = true;
                            holder.imgSendMessage.setImageResource(R.drawable.heart2);
                        }
                    } catch (Exception e) {

                    }
                }
            });

            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Brokerages dev = list.get(index);
                    String ImageUrl = dev.companyLogoUrl.replace(" ", "%20");
                    Intent intent = new Intent(context, DeveloperProfileActivity.class);
                    intent.putExtra("companyName", dev.companyName);
                    intent.putExtra("phone", dev.phone);
                    intent.putExtra("AboutTheCompany", dev.AboutTheCompany);
                    intent.putExtra("country", dev.country);
                    intent.putExtra("favorite", dev.IsFavorite);
                    intent.putExtra("lat", dev.Latitude);
                    intent.putExtra("lon", dev.Longitude);
                    intent.putExtra("dispID", dev.displayID);
                    intent.putExtra("actvlisting", String.valueOf(dev.activeListing));
                    intent.putExtra("agntcount", String.valueOf(dev.activeAgents));
                    intent.putExtra("state", dev.state);
                    intent.putExtra("city", dev.city);
                    intent.putExtra("locality", dev.locality);
                    intent.putExtra("fulladdress", dev.FullAdress);
                    intent.putExtra("membersince", dev.MemberSince);
                    intent.putExtra("website", dev.companyPermaLink);
                    intent.putExtra("companyID", String.valueOf(dev.companyId));
                    intent.putExtra("ImageUrl", ImageUrl);
                    intent.putExtra("Recent", "Brokerages");
                    intent.putExtra("ShortListParam", "RealEstateBrokerage");
                    context.startActivity(intent);

                }
            });


        } catch (Exception ex) {
            Log.v("error", ex.getMessage());
        }

        return v;
    }

    static class ViewHolder {

        public TextView lblName;

        public TextView lblPhone;

        public TextView lblAddress;
        public TextView lblListening;
        public ImageView imgSendMessage;
        RelativeLayout relaphonrlayout;
        NetworkImageView profile;

    }
}
