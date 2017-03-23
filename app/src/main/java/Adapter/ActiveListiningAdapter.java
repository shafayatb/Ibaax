package Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.ibaax.com.ibaax.AppController;
import com.ibaax.com.ibaax.PropertyDetailActivity2;
import com.ibaax.com.ibaax.R;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

import Entity.ActiveListing;
import Entity.AppGlobal;
import Entity.PropertySerialize;
import Event.IEvent;

/**
 * Created by iBaax on 2/3/16.
 */
public class ActiveListiningAdapter extends BaseAdapter {

    List<ActiveListing> list;
    Context context;
    IEvent listener;
    String Title;

    public ActiveListiningAdapter(Context context, List<ActiveListing> list, IEvent listener, String Title) {

        this.context = context;
        this.list = list;
        this.listener = listener;
        this.Title = Title;


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
                v = inflater.inflate(R.layout.row_property_for_sale, null);
                holder = new ViewHolder();
                holder.lblTitle = (TextView) v.findViewById(R.id.lbl_propertysale_title);
                holder.lblAddress = (TextView) v.findViewById(R.id.lbl_row_sale_property_address);
                holder.lblBed = (TextView) v.findViewById(R.id.lbl_row_sale_property_bed);
                holder.lblBath = (TextView) v.findViewById(R.id.lbl_row_sale_property_bath);
                holder.lblArea = (TextView) v.findViewById(R.id.lbl_row_sale_AreaSize);
                holder.lblPrice = (TextView) v.findViewById(R.id.lbl_row_sale_price);
                holder.lblTranType = (TextView) v.findViewById(R.id.lbl_row_sale_trantype);
                holder.img = (NetworkImageView) v.findViewById(R.id.img_sale);
                holder.btnFavorite = (ImageButton) v.findViewById(R.id.btn_sale_favorite);
                v.setTag(holder);

            } else {
                holder = (ViewHolder) v.getTag();
            }
            String Address = list.get(i).LocalityName + ", " + list.get(i).CityName;

            holder.lblAddress.setText(Address);
            holder.lblTitle.setText(list.get(i).PropertyTypeName);
            if (list.get(i).Price == 0) {

                holder.lblPrice.setText("Contact  for price");
            } else {
                DecimalFormat df = new DecimalFormat("#,###");
                holder.lblPrice.setText(list.get(i).CurrencyName + " "
                        + String.valueOf(df.format((int) list.get(i).Price)));
            }
            holder.lblTranType.setText(list.get(i).PropertyTransactionTypeName.toUpperCase());
            if (list.get(i).PropertyTransactionTypeCategoryName.equals("Sale")) {
                holder.lblTranType.setBackgroundColor(Color.parseColor("#ED0000"));
            } else {
                holder.lblTranType.setBackgroundColor(Color.parseColor("#78AE1B"));
            }
            holder.lblBed.setText("" + list.get(i).PropertyBedroomNumber);
            holder.lblBath.setText("" + list.get(i).PropertyBathroomNumber);
            holder.lblArea.setText("" + list.get(i).BuiltUpAreaSize + " " + list.get(i).BuiltUpAreaSizeName);
            String imageUrl = AppGlobal.ImageHost + "/property/ShowMarketplaceImage/" + list.get(i).PropertyID + "?PhotoFileName=" + Uri.encode(list.get(i).PhotoFileName);
            holder.img.setDefaultImageResId(R.drawable.property_no_photo); // image for loading...
            holder.img.setImageUrl(imageUrl, AppController.getInstance().getImageLoader());
            list.get(i).ImagePath = imageUrl;

            holder.btnFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (list.get(index).IsLiked == true) {
                        list.get(index).IsLiked = false;

                        holder.btnFavorite.setImageResource(R.drawable.heart);
                    } else {
                        list.get(index).IsLiked = true;
                        holder.btnFavorite.setImageResource(R.drawable.heart2);
                    }
                }
            });
            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        ActiveListing p = (ActiveListing) list.get(index);
                        Intent intent = new Intent(context, PropertyDetailActivity2.class);
                        PropertySerialize _serialize = new PropertySerialize();

                        _serialize.PropertyName = p.PropertyName;
                        _serialize.PropertyBedroomNumber = p.PropertyBedroomNumber;
                        _serialize.PropertyBathroomNumber = p.PropertyBathroomNumber;
                        _serialize.PropertyBuildUpArea = p.BuiltUpAreaSize;
                        _serialize.PropertyBuildUpAreaSizeTypeName = p.BuiltUpAreaSizeName;
                        _serialize.PropertyPlotArea = Integer.parseInt("0");
                        _serialize.PropertyPlotAreaSizeTypeName = p.LotSizeName;
                        _serialize.LocalityName = p.LocalityName;
                        _serialize.CurrencyName = p.CurrencyName;
                        _serialize.CityName = p.CityName;
                        _serialize.BuildYearMonth = p.BuildYearMonth;
                        _serialize.CountryName = p.CountryName;
                        _serialize.PropertyID = p.PropertyID;
                        _serialize.DisplayID = String.valueOf(p.DisplayId);
                        _serialize.CreateDate = p.CreateDate;
                        _serialize.PostedBy = p.PostedBy;
                        _serialize.Description = p.Description;
                        _serialize.Price = p.Price;
                        _serialize.ImagePath = p.ImagePath;
                        _serialize.PropertyTransactionTypeName = p.PropertyTransactionTypeName;
                        _serialize.PropertyTransactionTypeCategoryID = p.PropertyTransactionTypeCategoryID;
                        _serialize.PropertyTypeName = p.PropertyTypeName;
                        _serialize.IsLiked = p.IsLiked;
                        _serialize.PropertyPermaLink = p.Permalink;
                        _serialize.latitude = p.Latitude;
                        _serialize.longitude = p.Longitude;
                        //_serialize.Phone = p.ContactID;
                        _serialize.CompanyID = p.CompanyID;
                        _serialize.ContactID = String.valueOf(p.ContactID);
                        intent.putExtra("Property", (Serializable) _serialize);

                        context.startActivity(intent);
                    } catch (Exception ex) {
                        Log.v("error", "ActiveListing property adapter onclick :" + ex.getMessage());
                    }
                }
            });


        } catch (Exception ex) {
            Log.v("error", ex.getMessage());
        }

        return v;
    }

    static class ViewHolder {
        public TextView lblTitle;
        public TextView lblPrice;
        public TextView lblArea;
        public TextView lblAddress;
        public TextView lblBed;
        public TextView lblBath;
        public TextView lblTranType;
        public NetworkImageView img;
        public ImageButton btnFavorite;
    }
}

