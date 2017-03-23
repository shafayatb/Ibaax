package Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.ibaax.com.ibaax.AppController;
import com.ibaax.com.ibaax.AppPrefs;
import com.ibaax.com.ibaax.LoginActivity;
import com.ibaax.com.ibaax.PropertyDetailActivity2;
import com.ibaax.com.ibaax.R;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

import Entity.Property;
import Entity.PropertySerialize;
import Event.IProperty;
import ServiceInvoke.AddToFavorite;

/**
 * Created by iBaax on 5/11/16.
 */
public class PropertyPager extends PagerAdapter {

    Context context;
    LayoutInflater inflater;
    List<Property> list;
    IProperty listener;
    AppPrefs prefs;

    public PropertyPager(Context context, List<Property> list, IProperty listener) {

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
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int i) {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.map_info_window, container,
                false);
        try {
            final int index = i;

            NetworkImageView img = (NetworkImageView) itemView.findViewById(R.id.img_map_image);
            TextView lblPropertyType = (TextView) itemView.findViewById(R.id.lbl_map_property_type);
            TextView lblBath = (TextView) itemView.findViewById(R.id.lbl_map_bath);
            TextView lblBed = (TextView) itemView.findViewById(R.id.lbl_map_bed);
            TextView lblAddress = (TextView) itemView.findViewById(R.id.lbl_map_address);
            TextView ProertyType = (TextView) itemView.findViewById(R.id.lbl_map_pro_type);
            TextView PropertyName = (TextView) itemView.findViewById(R.id.lbl_map_title);
            TextView lblArea = (TextView) itemView.findViewById(R.id.lbl_map_area);
            TextView lblPrice = (TextView) itemView.findViewById(R.id.lbl_map_price);
            LinearLayout lnrPager = (LinearLayout) itemView.findViewById(R.id.lnr_next_prev);
            ImageButton btnLeft = (ImageButton) itemView.findViewById(R.id.btn_left);
            ImageButton btnRight = (ImageButton) itemView.findViewById(R.id.btn_right);
            TextView lblTotalNumber = (TextView) itemView.findViewById(R.id.lbl_totalnumber);
            final ImageView btnFavorite = (ImageView) itemView.findViewById(R.id.img_map_favorite);
            ImageView imgPropertyType = (ImageView) itemView.findViewById(R.id.img_map_pro_type);

            ((ViewPager) container).addView(itemView);

            lnrPager.setVisibility(View.VISIBLE);
            lblTotalNumber.setText((i + 1) + " of " + list.size());

            PropertyName.setText(list.get(i).PropertyName);
            lblPropertyType.setText(list.get(i).PropertyTypeName);

            lblAddress.setText(list.get(i).HalfAddress);

            if (list.get(i).PropertyBedroomNumber > 0) {
                lblBed.setVisibility(View.VISIBLE);
                lblBed.setText("Bed: " + list.get(i).PropertyBedroomNumber);
            } else {
                lblBed.setVisibility(View.GONE);
            }
            if (list.get(i).PropertyBathroomNumber > 0) {
                lblBath.setVisibility(View.VISIBLE);
                lblBath.setText("Bath: " + list.get(i).PropertyBathroomNumber);
            } else {
                lblBath.setVisibility(View.GONE);
            }
            if (list.get(i).BuiltUpAreaSize > 0) {
                lblArea.setVisibility(View.VISIBLE);
                if (prefs.getUserMeasurementType() == 1) {
                    lblArea.setText("Area: " + (int) Math.ceil(list.get(i).BuiltUpAreaSize) + " " + list.get(i).PropertyBuildUpAreaSizeTypeName);
                } else {
                    DecimalFormat df = new DecimalFormat("#.##");
                    lblArea.setText("Area: " + df.format((double) Math.round(list.get(i).BuiltUpAreaSize)) + " " + list.get(i).PropertyBuildUpAreaSizeTypeName);
                }
            } else {
                lblArea.setVisibility(View.GONE);
            }
            ProertyType.setText(list.get(i).PropertyTransactionTypeName.toUpperCase());

            if (list.get(i).PropertyTransactionTypeCategoryID == 1) {
                imgPropertyType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_map_icon_forsale));
            } else {
                imgPropertyType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_map_icon_forrent));
            }
            if (list.get(i).Price == 0) {

                lblPrice.setText("Contact  for price");
            } else {
                DecimalFormat df = new DecimalFormat("#,###");
                lblPrice.setText(list.get(i).CurrencyName + " "
                        + String.valueOf(df.format((int) list.get(i).Price)));
            }
            String imageUrl = list.get(i).ImagePath.replace(" ", "%20");

            img.setDefaultImageResId(R.drawable.property_no_photo); // image for loading...
            img.setImageUrl(imageUrl, AppController.getInstance().getImageLoader());

            if (list.get(i).IsLiked) {
                btnFavorite.setImageResource(R.drawable.ic_hearth_fill);
            } else {
                btnFavorite.setImageResource(R.drawable.ic_heart);
            }

            btnFavorite.setOnClickListener(new View.OnClickListener() {
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

                                }
                            }).Post(list.get(index).PropertyID + "",
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

                            btnFavorite.setImageResource(R.drawable.ic_heart);
                        } else {
                            list.get(index).IsLiked = true;
                            btnFavorite.setImageResource(R.drawable.ic_hearth_fill);


                        }
                    } catch (Exception ex) {
                        Log.v("error", "Featured Property Adapter Addto Faverite: " + ex.getMessage());
                    }


                }
            });

            btnLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (index > 0) {
                        listener.btnFavorite_click(index);
                    }
                }
            });

            btnRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (index < list.size()) {
                        listener.onClick(index);
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Property p = (Property) list.get(index);
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
                    _serialize.CountryName = p.CountryName;
                    _serialize.PropertyID = p.PropertyID;
                    _serialize.CreateDate = p.CreateDate;
                    _serialize.PostedBy = p.PostedBy;
                    _serialize.DisplayID = p.DisplayId;
                    _serialize.Description = p.Description;
                    _serialize.Price = p.Price;
                    _serialize.FullAddress = p.FullAddress;
                    _serialize.ImagePath = p.ImagePath;
                    _serialize.PropertyTypeName = p.PropertyTypeName;
                    _serialize.PropertyTransactionTypeName = p.PropertyTransactionTypeName;
                    _serialize.PropertyTransactionTypeCategoryID = p.PropertyTransactionTypeCategoryID;
                    _serialize.IsLiked = p.IsLiked;
                    _serialize.PropertyPermaLink = p.PropertyPermalink;
                    _serialize.latitude = p.Latitude;
                    _serialize.longitude = p.Longitude;
                    _serialize.Phone = p.ContactID;
                    _serialize.CompanyID = p.CompanyID;
                    _serialize.ContactID = p.ContactID;
                    intent.putExtra("Property", (Serializable) _serialize);

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
