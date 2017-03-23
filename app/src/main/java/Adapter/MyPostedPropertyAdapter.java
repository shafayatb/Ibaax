package Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.ibaax.com.ibaax.AppController;
import com.ibaax.com.ibaax.MyPostedPropertyDetailActivity2;
import com.ibaax.com.ibaax.R;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

import Entity.AppGlobal;
import Entity.Property;
import Entity.PropertySerialize;
import Event.IEvent;

/**
 * Created by iBaax on 2/8/16.
 */
public class MyPostedPropertyAdapter extends BaseAdapter {

    List<Property> list;
    Context context;
    IEvent listener;


    public MyPostedPropertyAdapter(Context context, List<Property> list, IEvent listener) {

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
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.row_property, null);
                holder = new ViewHolder();
                holder.lblPropertyType = (TextView) v.findViewById(R.id.lbl_rowproperty_type);
                holder.lblBed = (TextView) v.findViewById(R.id.lbl_row_property_bed);
                holder.lblBath = (TextView) v.findViewById(R.id.lbl_row_property_bath);
                holder.lblArea = (TextView) v.findViewById(R.id.lbl_row_property_area);
                holder.lblAddress = (TextView) v.findViewById(R.id.lbl_row_property_address);
                holder.PropertyName = (TextView) v.findViewById(R.id.lbl_rowproperty_name);
                holder.img = (NetworkImageView) v.findViewById(R.id.img_property_image);
                holder.lblPrice = (TextView) v.findViewById(R.id.lbl_rpw_property_price);
                holder.btnFavorite = (ImageButton) v.findViewById(R.id.btn_rowproperty_favorite);
                holder.ProertyType = (TextView) v.findViewById(R.id.lbl_rowproperty_PropertyTransactionTypeName);
                holder.NoOfBath = (LinearLayout) v.findViewById(R.id.lnr_featureprop_bath);
                holder.NoOfBed = (LinearLayout) v.findViewById(R.id.lnr_featureprop_bed);
                holder.NoArea = (LinearLayout) v.findViewById(R.id.lnr_featureprop_area);
                holder.imgProType = (ImageView) v.findViewById(R.id.img_list_pro_type);
                v.setTag(holder);

            } else {
                holder = (ViewHolder) v.getTag();
            }

            holder.btnFavorite.setVisibility(View.GONE);

            holder.PropertyName.setText(list.get(i).PropertyName);
            holder.lblAddress.setText(list.get(i).FullAddress);
            holder.lblPropertyType.setText(list.get(i).PropertyTypeName);
            if (list.get(i).Price == 0) {
                holder.lblPrice.setText("Contact for price");
            } else {
                DecimalFormat df = new DecimalFormat("#,###");
                holder.lblPrice.setText(list.get(i).CurrencyName + " "
                        + String.valueOf(df.format((int) list.get(i).Price)));
            }


            holder.lblBed.setText("" + list.get(i).PropertyBedroomNumber);
            holder.lblBath.setText("" + list.get(i).PropertyBathroomNumber);
            holder.lblArea.setText("" + list.get(i).BuiltUpAreaSize + " " + list.get(i).PropertyBuildUpAreaSizeTypeName);
            String imageUrl = AppGlobal.ImageHost + "/property/ShowMarketplaceImage/" + list.get(i).PropertyID + "?PhotoFileName=" + list.get(i).PhotoFileName.replace(" ", "%20");

            holder.ProertyType.setText(list.get(i).PropertyTransactionTypeName.toUpperCase());
            if (list.get(i).PropertyTransactionTypeCategoryName.equals("Sale")) {
                holder.imgProType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_map_icon_forsale));
            } else {
                holder.imgProType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_map_icon_forrent));
            }
            //  String imageUrl=list.get(i).ThumbnailUrl;

            //String imageUrl = AppGlobal.ImageHost + "/property/ShowMarketplaceImage/" + list.get(i).PropertyID;
            holder.img.setDefaultImageResId(R.drawable.property_no_photo); // image for loading...
            holder.img.setImageUrl(imageUrl, AppController.getInstance().getImageLoader());


            /*holder.btnRemve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppPrefs pref = new AppPrefs(context);
                    listener.onClick(list.get(index));

                }
            });*/

            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //   listener.onClick(list.get(index));

                    Property p = (Property) list.get(index);
                    Intent intent = new Intent(context, MyPostedPropertyDetailActivity2.class);
                    PropertySerialize _serialize = new PropertySerialize();

                    _serialize.PropertyTransactionTypeCategoryID = p.PropertyTransactionTypeCategoryID;
                    _serialize.PropertyBedroomNumber = p.PropertyBedroomNumber;
                    _serialize.PropertyBathroomNumber = p.PropertyBathroomNumber;
                    _serialize.PropertyBuildUpArea = p.BuiltUpAreaSize;
                    _serialize.PropertyBuildUpAreaSizeTypeName = p.PropertyBuildUpAreaSizeTypeName;
                    _serialize.PropertyPlotArea = p.PropertyLotSize;
                    _serialize.BuildingName = p.BuildingName;
                    _serialize.PropertyPlotAreaSizeTypeName = p.PropertyPlotAreaSizeTypeName;
                    _serialize.LocalityName = p.LocalityName;
                    _serialize.RecentPublishStatus = p.RecentPublishStatus;
                    _serialize.PropertyTypeName = p.PropertyTypeName;
                    _serialize.FullAddress = p.FullAddress;
                    _serialize.BuildYearMonth = p.BuildYearMonth;
                    _serialize.DisplayID = p.DisplayId;
                    _serialize.CityName = p.CityName;
                    _serialize.PropertyName = p.PropertyName;
                    _serialize.CurrencyName = p.CurrencyName;
                    _serialize.CountryName = p.CountryName;
                    _serialize.PropertyID = p.PropertyID;
                    _serialize.CreateDate = p.CreateDate;
                    _serialize.PostedBy = p.PostedBy;
                    _serialize.Description = p.Description;
                    _serialize.Price = p.Price;
                    _serialize.ImagePath = p.ImagePath;
                    _serialize.PropertyTransactionTypeName = p.PropertyTransactionTypeName;
                    _serialize.IsLiked = p.IsLiked;
                    _serialize.PropertyPermaLink = p.PropertyPermalink;
                    _serialize.PropertyTransactionTypeID = p.PropertyTransactionTypeID;
                    _serialize.PropertyTypeID = p.PropertyTypeID;
                    _serialize.ZipCode = p.ZipCode;
                    _serialize.AptSuiteNo = p.AptSuiteNo;
                    _serialize.Address = p.Address;
                    _serialize.latitude = p.Latitude;
                    _serialize.longitude = p.Longitude;
                    _serialize.Phone = p.ContactID;
                    _serialize.CompanyID = p.CompanyID;
                    _serialize.ContactID = p.ContactID;
                    intent.putExtra("Property", (Serializable) _serialize);

                    context.startActivity(intent);


                }
            });


        } catch (Exception ex) {
            Log.v("error", ex.getMessage());
        }

        return v;
    }

    static class ViewHolder {
        /*public TextView lblTitle;
        public TextView lblPrice;
        public TextView lblArea;
        public TextView lblAddress;
        public TextView lblBed;
        public TextView lblBath;
        public TextView lblTranType;
        public NetworkImageView img;
        public ImageButton btnRemve;*/

        public NetworkImageView img;
        public TextView lblPropertyType;
        public TextView lblBath;
        public TextView lblBed;
        public TextView lblAddress;
        public TextView ProertyType;
        public TextView PropertyName;
        public TextView lblArea;
        public TextView lblPrice;
        public LinearLayout NoOfBed, NoOfBath, NoArea;
        ImageButton btnFavorite;
        ImageView imgProType;

    }


}
