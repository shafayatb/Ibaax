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

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.ibaax.com.ibaax.AppController;
import com.ibaax.com.ibaax.AppPrefs;
import com.ibaax.com.ibaax.LoginActivity;
import com.ibaax.com.ibaax.PropertyDetailActivity2;
import com.ibaax.com.ibaax.R;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

import Cache.LruBitmapCache;
import Entity.Property;
import Entity.PropertySerialize;
import Event.IProperty;
import ServiceInvoke.AddToFavorite;

/**
 * Created by S.R Rain on 1/11/2016.
 */
public class FeaturedPropertyAdapter extends BaseAdapter {

    List<Property> list;
    Context context;
    IProperty listener;
    AppPrefs prefs;

    public FeaturedPropertyAdapter(Context context, List<Property> list, IProperty listener) {

        this.context = context;
        this.list = list;
        this.listener = listener;
        prefs = new AppPrefs(context);

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
            holder.PropertyName.setText(list.get(i).PropertyName);
            holder.lblPropertyType.setText(list.get(i).PropertyTypeName);

            holder.lblAddress.setText(list.get(i).HalfAddress);

            if (list.get(i).PropertyBedroomNumber > 0) {
                holder.NoOfBed.setVisibility(View.VISIBLE);
                holder.lblBed.setText("" + list.get(i).PropertyBedroomNumber);
            } else {
                holder.NoOfBed.setVisibility(View.GONE);
            }
            if (list.get(i).PropertyBathroomNumber > 0) {
                holder.NoOfBath.setVisibility(View.VISIBLE);
                holder.lblBath.setText("" + list.get(i).PropertyBathroomNumber);
            } else {
                holder.NoOfBath.setVisibility(View.GONE);
            }
            if (list.get(i).BuiltUpAreaSize > 0) {
                holder.NoArea.setVisibility(View.VISIBLE);
                if (prefs.getUserMeasurementType() == 1) {
                    holder.lblArea.setText("" + (int) Math.ceil(list.get(i).BuiltUpAreaSize) + " " + list.get(i).PropertyBuildUpAreaSizeTypeName);
                } else {
                    DecimalFormat df = new DecimalFormat("##.00");
                    holder.lblArea.setText("Area: " + df.format(list.get(i).BuiltUpAreaSize) + " " + list.get(i).PropertyBuildUpAreaSizeTypeName);
                }

            } else {
                holder.NoArea.setVisibility(View.GONE);
            }
            holder.ProertyType.setText(list.get(i).PropertyTransactionTypeName.toUpperCase());

            if (list.get(i).PropertyTransactionTypeCategoryID == 1) {
                holder.imgProType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_map_icon_forsale));
            } else {
                holder.imgProType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_map_icon_forrent));
            }
            if (list.get(i).Price == 0) {

                holder.lblPrice.setText("Contact  for price");
            } else {
                DecimalFormat df = new DecimalFormat("#,###");

                holder.lblPrice.setText(list.get(i).CurrencyName + " "
                        + String.valueOf(df.format(Math.ceil(list.get(i).Price))));

            }

            ImageLoader.ImageCache imageCache = new LruBitmapCache();
            ImageLoader imageLoader = new ImageLoader(AppController.getInstance().getRequestQueue(), imageCache);
            String imageUrl = list.get(i).ImagePath.replace(" ", "%20");

            holder.img.setDefaultImageResId(R.drawable.property_no_photo); // image for loading...
            holder.img.setImageUrl(imageUrl, AppController.getInstance().getImageLoader());


            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    try {

                        Property p = list.get(index);
                        Intent intent = new Intent(context, PropertyDetailActivity2.class);
                        PropertySerialize _serialize = new PropertySerialize();

                        _serialize.PropertyName = p.PropertyName;
                        _serialize.PropertyBedroomNumber = p.PropertyBedroomNumber;
                        _serialize.PropertyBathroomNumber = p.PropertyBathroomNumber;
                        _serialize.PropertyBuildUpArea = p.BuiltUpAreaSize;
                        _serialize.PropertyBuildUpAreaSizeTypeName = p.PropertyBuildUpAreaSizeTypeName;
                        _serialize.PropertyPlotArea = p.PropertyLotSize;
                        _serialize.PropertyPlotAreaSizeTypeName = p.PropertyPlotAreaSizeTypeName;
                        _serialize.LocalityName = p.LocalityName;
                        _serialize.CurrencyName = p.CurrencyName;
                        _serialize.CityName = p.CityName;
                        _serialize.FullAddress = p.FullAddress;
                        _serialize.BuildYearMonth = p.BuildYearMonth;
                        _serialize.CountryName = p.CountryName;
                        _serialize.PropertyID = p.PropertyID;
                        _serialize.DisplayID = p.DisplayId;
                        _serialize.CreateDate = p.CreateDate;
                        _serialize.PostedBy = p.PostedBy;
                        _serialize.Description = p.Description;
                        _serialize.Price = p.Price;
                        _serialize.ImagePath = p.ImagePath;
                        _serialize.PropertyTransactionTypeName = p.PropertyTransactionTypeName;
                        _serialize.PropertyTransactionTypeCategoryID = p.PropertyTransactionTypeCategoryID;
                        _serialize.PropertyTypeName = p.PropertyTypeName;
                        _serialize.IsLiked = p.IsLiked;
                        _serialize.PropertyPermaLink = p.PropertyPermalink;
                        _serialize.latitude = p.Latitude;
                        _serialize.longitude = p.Longitude;
                        _serialize.Phone = p.ContactID;
                        _serialize.CompanyID = p.CompanyID;
                        _serialize.ContactID = p.ContactID;
                        intent.putExtra("Property", (Serializable) _serialize);

                        context.startActivity(intent);
                    } catch (Exception ex) {

                        Log.v("error", "Featured property adapter onclick :" + ex.getMessage());
                    }


                }
            });

            if (list.get(i).IsLiked) {
                holder.btnFavorite.setImageResource(R.drawable.ic_hearth_fill);
            } else {
                holder.btnFavorite.setImageResource(R.drawable.ic_heart);
            }

            holder.btnFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        AppPrefs pref = new AppPrefs(context);
                        String UserID = pref.getUserID();

                        if (UserID.length() > 1) {

                            new AddToFavorite(context, new IProperty() {
                                @Override
                                public void btnFavorite_click(Object obj) {

                                }

                                @Override
                                public void onClick(Object obj) {
                                    listener.onClick(obj);
                                }
                            }).Post(list.get(i).PropertyID + "",
                                    UserID,
                                    pref.getCompanyID() + "",
                                    pref.getFirstName(),
                                    pref.getLastName());

                        } else {

                            Intent intent = new Intent(context, LoginActivity.class);
                            context.startActivity(intent);
                            return;

                        }


                        if (list.get(index).IsLiked) {
                            list.get(index).IsLiked = false;

                            holder.btnFavorite.setImageResource(R.drawable.ic_heart);
                        } else {
                            list.get(index).IsLiked = true;
                            holder.btnFavorite.setImageResource(R.drawable.ic_hearth_fill);


                        }
                    } catch (Exception ex) {
                        Log.v("error", "Featured Property Adapter Addto Faverite: " + ex.getMessage());
                    }


                }
            });


        } catch (Exception ex) {
            Log.v("error", ex.getMessage());
        }

        return v;
    }


    static class ViewHolder {


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
