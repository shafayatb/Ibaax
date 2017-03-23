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
import com.ibaax.com.ibaax.LoginActivity;
import com.ibaax.com.ibaax.R;

import java.io.Serializable;
import java.util.List;

import Entity.Agent;
import Entity.AppGlobal;
import Event.IEvent;
import Event.IProperty;
import ServiceInvoke.AddToFavorite;
import ServiceInvoke.AddfavoriteAgentAgency;

/**
 * Created by S.R Rain on 1/11/2016.
 */
public class AgentAdapter extends BaseAdapter {

    List<Agent> list;
    Context context;
    IProperty listener;

    public AgentAdapter(Context context, List<Agent> list, IProperty listener) {
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

        final int index = i;

        try {
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.row_agent, null);
                holder = new ViewHolder();

                holder.lblName = (TextView) v.findViewById(R.id.lbl_rowfindanagent_name);
                holder.lblCompanyName = (TextView) v.findViewById(R.id.lbl_rowfindanagent_companyname);
                holder.lblPhone = (TextView) v.findViewById(R.id.lbl_rowfindanagent_phone);
                holder.lblAddress = (TextView) v.findViewById(R.id.lbl_rowfindanagent_address);
                holder.FavAgent=(ImageView)v.findViewById(R.id.img_fav_agent);
                holder.lblListening = (TextView) v.findViewById(R.id.lbl_rowfindanagent_listening);
                holder.profile = (NetworkImageView) v.findViewById(R.id.img_rowfindanagent_profile);
                holder.relaphonrlayout = (RelativeLayout) v.findViewById(R.id.rel_agent_list_phone_layout);
                v.setTag(holder);

            } else {
                holder = (ViewHolder) v.getTag();
            }


            holder.lblName.setText(list.get(i).name);
            holder.lblCompanyName.setText(list.get(i).CompanyName);
            if (list.get(i).OfficePhone.length() > 0) {
                holder.relaphonrlayout.setVisibility(View.VISIBLE);

            } else {
                holder.relaphonrlayout.setVisibility(View.GONE);
            }
            holder.lblPhone.setText(list.get(i).OfficePhone);

            //holder.lblAddress.setText(list.get(i).city + ", " + list.get(i).state + ", " + list.get(i).country);
            holder.lblAddress.setText(list.get(i).fulladdress);
            holder.lblListening.setText(list.get(i).DisplayID);
            try {
                holder.profile.setDefaultImageResId(R.drawable.avater);
                holder.profile.setImageUrl(list.get(i).agentThumbnailUrl.replace(" ", "%20"), AppController.getInstance().getImageLoader());
            } catch (Exception ex) {

                Log.v("error", "holder.profile " + ex.getMessage());
            }
            if (list.get(index).IsLiked) {
                holder.FavAgent.setImageResource(R.drawable.heart2);
            } else {
                holder.FavAgent.setImageResource(R.drawable.heart);
            }
            holder.FavAgent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
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
                                    AppGlobal.localHost+"/api/MContacts/AddAgentFavourite");

                        } else {

                            Intent intent = new Intent(context, LoginActivity.class);
                            context.startActivity(intent);
                            return;

                        }


                        if (list.get(index).IsLiked) {
                            list.get(index).IsLiked = false;

                            holder.FavAgent.setImageResource(R.drawable.heart);
                        } else {
                            list.get(index).IsLiked = true;
                            holder.FavAgent.setImageResource(R.drawable.heart2);
                        }
                    }catch (Exception e){

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
