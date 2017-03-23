package com.ibaax.com.ibaax;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import Adapter.NumberofBedBathAdapter;
import Adapter.PropertyTypeGridAdapter2;
import Adapter.SearchDropDownAdapter;
import Cache.FilterPrefs;
import DataAccessLayer.PropertyTypeLayer;
import Entity.Dictunary;
import Entity.NoBedBath;
import Entity.SearchFilters;
import Event.IEvent;
import Event.IPropertyType;
import Plugins.HelperFunctions;
import Plugins.TextBoxHandler;
import UI.ExpandableHeightGridView;

public class SearchPropertyForResultActivity extends AppCompatActivity {

    static List<Dictunary> PropertyList = new ArrayList<>();
    static List<Dictunary> ListingTyplist = new ArrayList<>();
    static List<Dictunary> RentTypeList = new ArrayList<>();
    static List<Dictunary> PropertyListCommercial = new ArrayList<>();

    ExpandableHeightGridView gridviewSearchpropertyCategory;
    ExpandableHeightGridView gridviewSearchpropertyRenttype;
    ExpandableHeightGridView gridPropertyTypeResidential;
    ExpandableHeightGridView gridPostpropertyNoofbedrooms;
    ExpandableHeightGridView gridPostpropertyNoofbathrooms;
    ExpandableHeightGridView gridPropertyTypeCommercial;

    Switch switchSale;
    Switch switchRent;

    Spinner spnMinPrice;
    Spinner spnMaxPrice;
    Spinner spnRentMinPrice;
    Spinner spnRentMaxPrice;
    Spinner spnMinYearBuilt;
    Spinner spnMaxYearBuilt;
    Spinner spnMinArea;
    Spinner spnMaxArea;
    Spinner spnMinLot;
    Spinner spnMaxLot;


    CheckBox chkResidential;
    CheckBox chkCommercial;
    CheckBox chkOwner;
    CheckBox chkAgent;
    CheckBox chkDeveloper;
    CheckBox chkBrokerage;

    Button btnSearchMore;
    Button btnFilter;
    Button btnMoreProperty;

    LinearLayout lnrMoreSearch, lnrSearchSale, lnrSearchRent;
    Boolean IsMore = false;
    Boolean IsExpanded = false;

    List<String> MinYearList = new ArrayList<>();
    List<String> MaxYearList = new ArrayList<>();
    List<String> MinPrice = new ArrayList<>();
    List<String> MaxPrice = new ArrayList<>();
    List<Dictunary> MaxArea = new ArrayList<>();
    List<Dictunary> MinArea = new ArrayList<>();
    List<Dictunary> MaxLot = new ArrayList<>();
    List<Dictunary> MinLot = new ArrayList<>();
    List<NoBedBath> NumberBedList = new ArrayList<>();
    List<NoBedBath> NumberBathList = new ArrayList<>();

    String[] MinArray = {"Any Price", "$ 0", "$ 50,000+", "$ 100,000+", "$ 150,000+", "$ 200,000+", "$ 250,000+",
            "$ 300,000+", "$ 350,000+", "$ 400,000+", "$ 450,000+", "$ 500,000+"};

    String[] MaxArray = {"Any Price", "$ 550,000+", "$ 600,000+", "$ 650,000+", "$ 700,000+", "$ 750,000+",
            "$ 800,000+", "$ 850,000+", "$ 900,000+", "$ 950,000+"};


    String NoBed, NoBath, BathString, BedString, MinAreaString, MaxAreaString, MinLotString, MaxLotString;

    NumberofBedBathAdapter BedRoomAdapter;
    NumberofBedBathAdapter BathRoomAdapter;
    PropertyTypeGridAdapter2 CategoryAdapter;
    PropertyTypeGridAdapter2 RentTypeAdapter;
    PropertyTypeGridAdapter2 PropertyTypeAdapter;
    PropertyTypeGridAdapter2 PropertyCommercialAdapter;

    SearchFilters filters;
    FilterPrefs prefs;
    AppPrefs appPrefs;

    public static int convertDpToPixels(float dp, Context context) {
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                resources.getDisplayMetrics());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_property_for_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        prefs = new FilterPrefs(this);
        filters = new SearchFilters();
        appPrefs = new AppPrefs(this);
        findViews();
    }

    private void findViews() {
        switchSale = (Switch) findViewById(R.id.switch_sale);
        switchRent = (Switch) findViewById(R.id.switch_rent);

        gridviewSearchpropertyCategory = (ExpandableHeightGridView) findViewById(R.id.gridview_searchproperty_category);
        gridviewSearchpropertyRenttype = (ExpandableHeightGridView) findViewById(R.id.gridview_searchproperty_renttype);
        gridPropertyTypeResidential = (ExpandableHeightGridView) findViewById(R.id.gridview_searchproperty_residentialtype);
        gridPostpropertyNoofbedrooms = (ExpandableHeightGridView) findViewById(R.id.grid_postproperty_noofbedrooms);
        gridPostpropertyNoofbathrooms = (ExpandableHeightGridView) findViewById(R.id.grid_postproperty_noofbathrooms);
        gridPropertyTypeCommercial = (ExpandableHeightGridView) findViewById(R.id.gridview_searchproperty_commercialtype);

        spnMinPrice = (Spinner) findViewById(R.id.spn_min_price);
        spnMaxPrice = (Spinner) findViewById(R.id.spn_max_price);
        spnRentMinPrice = (Spinner) findViewById(R.id.spn_rent_min_price);
        spnRentMaxPrice = (Spinner) findViewById(R.id.spn_rent_max_price);
        spnMinArea = (Spinner) findViewById(R.id.spn_min_area);
        spnMaxArea = (Spinner) findViewById(R.id.spn_max_area);
        spnMinLot = (Spinner) findViewById(R.id.spn_min_lot);
        spnMaxLot = (Spinner) findViewById(R.id.spn_max_lot);
        spnMinYearBuilt = (Spinner) findViewById(R.id.spn_min_year_built);
        spnMaxYearBuilt = (Spinner) findViewById(R.id.spn_max_year_built);


        chkResidential = (CheckBox) findViewById(R.id.chk_residential);
        chkCommercial = (CheckBox) findViewById(R.id.chk_commercial);
        chkOwner = (CheckBox) findViewById(R.id.chk_owner);
        chkAgent = (CheckBox) findViewById(R.id.chk_agent);
        chkDeveloper = (CheckBox) findViewById(R.id.chk_developer);
        chkBrokerage = (CheckBox) findViewById(R.id.chk_brokerage);

        btnFilter = (Button) findViewById(R.id.btn_filter);
        btnSearchMore = (Button) findViewById(R.id.btn_search_more);
        btnMoreProperty = (Button) findViewById(R.id.btn_more_property);

        lnrMoreSearch = (LinearLayout) findViewById(R.id.lnr_search_more);
        lnrSearchSale = (LinearLayout) findViewById(R.id.lnr_searchsale);
        lnrSearchRent = (LinearLayout) findViewById(R.id.lnr_search_rent);

        getListingType();
        getRentType();
        SpinnerOperations();
        RequestProperty();
        RequestPropertyCommercial();
        bindBedBathAdapter();
        YearSpinner();
        setValues();
        events();
    }


    void setValues() {

        switchSale.setChecked(prefs.getSwitchSale());
        switchRent.setChecked(prefs.getSwitchRent());

        if (switchSale.isChecked()) {
            lnrSearchSale.setVisibility(View.VISIBLE);
        } else {
            lnrSearchSale.setVisibility(View.GONE);
        }

        if (switchRent.isChecked()) {
            lnrSearchRent.setVisibility(View.VISIBLE);
        } else {
            lnrSearchRent.setVisibility(View.GONE);
        }

        spnMinPrice.setSelection(prefs.getSaleMinPrice());
        spnMaxPrice.setSelection(prefs.getSaleMax());


        for (int i = 0; i < NumberBedList.size(); i++) {
            if (NumberBedList.get(i).Numbers.equals(prefs.getBedNo())) {
                NumberBedList.get(i).IsSelected = true;
                NoBed = stripNonDigits(NumberBedList.get(i).Numbers);
                BedString = NumberBedList.get(i).Numbers;
            }
        }
        BedRoomAdapter.notifyDataSetChanged();

        for (int i = 0; i < NumberBathList.size(); i++) {
            if (NumberBathList.get(i).Numbers.equals(prefs.getBathNo())) {
                NumberBathList.get(i).IsSelected = true;
                NoBath = stripNonDigits(NumberBathList.get(i).Numbers);
                BathString = NumberBathList.get(i).Numbers;
            }
        }
        BathRoomAdapter.notifyDataSetChanged();

        spnMinYearBuilt.setSelection(prefs.getYear());
        spnMaxYearBuilt.setSelection(prefs.getMaxYear());
        spnMinArea.setSelection(prefs.getMinBuild());
        spnMaxArea.setSelection(prefs.getMaxBuild());
        spnMaxLot.setSelection(prefs.getMaxLot());
        spnMinLot.setSelection(prefs.getMinLot());

        for (Dictunary d : PropertyList) {
            if (d.IsSelected) {
                chkResidential.setChecked(true);
            } else {
                chkResidential.setChecked(false);
                break;
            }
        }

        for (Dictunary d : PropertyListCommercial) {
            if (d.IsSelected) {
                chkCommercial.setChecked(true);
            } else {
                chkCommercial.setChecked(false);
                break;
            }
        }

    }

    private void getListingType() {
        if (ListingTyplist.size() > 0) {
            BindListingType();

        } else {
            ListingTyplist.add(new Dictunary(1, "For Sale", "For-Sale".toLowerCase()));
            ListingTyplist.add(new Dictunary(2, "New Project", "Projects".toLowerCase()));
            ListingTyplist.add(new Dictunary(3, "Upcoming", "Upcoming".toLowerCase()));
            ListingTyplist.add(new Dictunary(4, "Foreclosures", "FClosures".toLowerCase()));
            ListingTyplist.add(new Dictunary(5, "Short-Sale", "Short-Sale".toLowerCase()));
            ListingTyplist.add(new Dictunary(6, "Auction", "Auction".toLowerCase()));
            BindListingType();
        }


    }

    private void BindListingType() {
        CategoryAdapter = new PropertyTypeGridAdapter2(this, ListingTyplist,
                new IPropertyType() {
                    @Override
                    public void OnClick(Object obj) {

                    }
                });
        gridviewSearchpropertyCategory.setAdapter(CategoryAdapter);
        gridviewSearchpropertyCategory.setExpanded(true);

    }

    void getRentType() {
        if (RentTypeList.size() > 0) {
            BindRentType();
        } else {
            RentTypeList.add(new Dictunary(1, "Yearly", true));
            RentTypeList.add(new Dictunary(2, "Monthly", true));
            RentTypeList.add(new Dictunary(4, "Weekly", true));
            RentTypeList.add(new Dictunary(5, "Daily", true));
            BindRentType();
        }
    }

    void BindRentType() {
        RentTypeAdapter = new PropertyTypeGridAdapter2(this, RentTypeList,
                new IPropertyType() {
                    @Override
                    public void OnClick(Object obj) {

                    }
                });
        gridviewSearchpropertyRenttype.setAdapter(RentTypeAdapter);
        gridviewSearchpropertyRenttype.setExpanded(true);
    }

    void SpinnerOperations() {
        MaxPrice = Arrays.asList(MaxArray);
        ArrayAdapter<String> BedRoomadp = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, MaxPrice);
        BedRoomadp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMaxPrice.setAdapter(BedRoomadp);
        spnRentMaxPrice.setAdapter(BedRoomadp);

        spnMaxPrice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextSize(15.0f);
                adapterView.setPadding(0, 2, 0, 2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //NoBathroom Spinner//
        MinPrice = Arrays.asList(MinArray);
        //NoBedroom Spinner//
        ArrayAdapter<String> BathRoomadp = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, MinPrice);
        BathRoomadp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMinPrice.setAdapter(BathRoomadp);
        spnRentMinPrice.setAdapter(BathRoomadp);
        spnMinPrice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextSize(15.0f);
                adapterView.setPadding(0, 2, 0, 2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        MaxArea.add(new Dictunary(1, "Any Size", ""));
        MaxArea.add(new Dictunary(1, "500", "Sq Ft"));
        MaxArea.add(new Dictunary(1, "600", "Sq Ft"));
        MaxArea.add(new Dictunary(1, "700", "Sq Ft"));
        MaxArea.add(new Dictunary(1, "800", "Sq Ft"));
        MaxArea.add(new Dictunary(1, "900", "Sq Ft"));
        MaxArea.add(new Dictunary(1, "1000", "Sq Ft"));
        MaxArea.add(new Dictunary(1, "1100", "Sq Ft"));
        MaxArea.add(new Dictunary(1, "1200", "Sq Ft"));
        MaxArea.add(new Dictunary(1, "1300", "Sq Ft"));
        MaxArea.add(new Dictunary(1, "1400", "Sq Ft"));
        MaxArea.add(new Dictunary(1, "1500", "Sq Ft"));
        MaxArea.add(new Dictunary(1, "1600", "Sq Ft"));
        MaxArea.add(new Dictunary(1, "1700", "Sq Ft"));
        MaxArea.add(new Dictunary(1, "1800", "Sq Ft"));
        MaxArea.add(new Dictunary(1, "1900", "Sq Ft"));
        MaxArea.add(new Dictunary(1, "2000", "Sq Ft"));
        MaxArea.add(new Dictunary(1, "2500", "Sq Ft"));
        MaxArea.add(new Dictunary(1, "3000", "Sq Ft"));
        MaxArea.add(new Dictunary(1, "3500", "Sq Ft"));
        MaxArea.add(new Dictunary(1, "4000", "Sq Ft"));
        MaxArea.add(new Dictunary(1, "4500", "Sq Ft"));
        MaxArea.add(new Dictunary(1, "5000", "Sq Ft"));

        MaxArea = HelperFunctions.ChangeDropdownUnits(MaxArea, appPrefs);


        SearchDropDownAdapter MaxAreaadp = new SearchDropDownAdapter(this, android.R.layout.simple_list_item_1, MaxArea);
        BedRoomadp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMaxArea.setAdapter(MaxAreaadp);

        spnMaxArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                adapterView.setPadding(0, 2, 0, 2);
                if (i != 0) {
                    MaxAreaString = MaxArea.get(i).Title;
                } else {
                    MaxAreaString = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        MinArea.add(new Dictunary(1, "Any Size", ""));
        MinArea.add(new Dictunary(1, "400", "Sq Ft"));
        MinArea.add(new Dictunary(1, "500", "Sq Ft"));
        MinArea.add(new Dictunary(1, "600", "Sq Ft"));
        MinArea.add(new Dictunary(1, "700", "Sq Ft"));
        MinArea.add(new Dictunary(1, "800", "Sq Ft"));
        MinArea.add(new Dictunary(1, "900", "Sq Ft"));
        MinArea.add(new Dictunary(1, "1000", "Sq Ft"));
        MinArea.add(new Dictunary(1, "1100", "Sq Ft"));
        MinArea.add(new Dictunary(1, "1200", "Sq Ft"));
        MinArea.add(new Dictunary(1, "1300", "Sq Ft"));
        MinArea.add(new Dictunary(1, "1400", "Sq Ft"));
        MinArea.add(new Dictunary(1, "1500", "Sq Ft"));
        MinArea.add(new Dictunary(1, "1600", "Sq Ft"));
        MinArea.add(new Dictunary(1, "1700", "Sq Ft"));
        MinArea.add(new Dictunary(1, "1800", "Sq Ft"));
        MinArea.add(new Dictunary(1, "1900", "Sq Ft"));
        MinArea.add(new Dictunary(1, "2000", "Sq Ft"));
        MinArea.add(new Dictunary(1, "2500", "Sq Ft"));
        MinArea.add(new Dictunary(1, "3000", "Sq Ft"));
        MinArea.add(new Dictunary(1, "3500", "Sq Ft"));
        MinArea.add(new Dictunary(1, "4000", "Sq Ft"));
        MinArea.add(new Dictunary(1, "4500", "Sq Ft"));
        MinArea.add(new Dictunary(1, "5000", "Sq Ft"));

        MinArea = HelperFunctions.ChangeDropdownUnits(MinArea, appPrefs);

        SearchDropDownAdapter MinAreaadp = new SearchDropDownAdapter(this, android.R.layout.simple_list_item_1, MinArea);
        spnMinArea.setAdapter(MinAreaadp);
        spnMinArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                adapterView.setPadding(0, 2, 0, 2);
                if (i != 0) {
                    MinAreaString = MinArea.get(i).Title;
                } else {
                    MinAreaString = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        MaxLot.add(new Dictunary(1, "Any Size", ""));
        MaxLot.add(new Dictunary(1, "3000", "Sq Ft"));
        MaxLot.add(new Dictunary(1, "4000", "Sq Ft"));
        MaxLot.add(new Dictunary(1, "5000", "Sq Ft"));
        MaxLot.add(new Dictunary(1, "7500", "Sq Ft"));
        MaxLot.add(new Dictunary(1, "10890", "Sq Ft"));
        MaxLot.add(new Dictunary(1, "21780", "Sq Ft"));
        MaxLot.add(new Dictunary(1, "32670", "Sq Ft"));
        MaxLot.add(new Dictunary(1, "43560", "Sq Ft"));
        MaxLot.add(new Dictunary(1, "87120", "Sq Ft"));
        MaxLot.add(new Dictunary(1, "130680", "Sq Ft"));
        MaxLot.add(new Dictunary(1, "174240", "Sq Ft"));
        MaxLot.add(new Dictunary(1, "217800", "Sq Ft"));
        MaxLot.add(new Dictunary(1, "435600", "Sq Ft"));
        MaxLot.add(new Dictunary(1, "653400", "Sq Ft"));
        MaxLot.add(new Dictunary(1, "871200", "Sq Ft"));

        MaxLot = HelperFunctions.ChangeDropdownUnits(MaxLot, appPrefs);


        SearchDropDownAdapter MaxLotadp = new SearchDropDownAdapter(this, android.R.layout.simple_list_item_1, MaxLot);
        spnMaxLot.setAdapter(MaxLotadp);
        spnMaxLot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                adapterView.setPadding(0, 2, 0, 2);
                if (i != 0) {
                    MaxLotString = MaxLot.get(i).Title;
                } else {
                    MaxLotString = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        MinLot.add(new Dictunary(1, "Any Size", ""));
        MinLot.add(new Dictunary(1, "2000", "Sq Ft"));
        MinLot.add(new Dictunary(1, "3000", "Sq Ft"));
        MinLot.add(new Dictunary(1, "4000", "Sq Ft"));
        MinLot.add(new Dictunary(1, "5000", "Sq Ft"));
        MinLot.add(new Dictunary(1, "7500", "Sq Ft"));
        MinLot.add(new Dictunary(1, "10890", "Sq Ft"));
        MinLot.add(new Dictunary(1, "21780", "Sq Ft"));
        MinLot.add(new Dictunary(1, "32670", "Sq Ft"));
        MinLot.add(new Dictunary(1, "43560", "Sq Ft"));
        MinLot.add(new Dictunary(1, "87120", "Sq Ft"));
        MinLot.add(new Dictunary(1, "130680", "Sq Ft"));
        MinLot.add(new Dictunary(1, "174240", "Sq Ft"));
        MinLot.add(new Dictunary(1, "217800", "Sq Ft"));
        MinLot.add(new Dictunary(1, "435600", "Sq Ft"));
        MinLot.add(new Dictunary(1, "653400", "Sq Ft"));
        MinLot.add(new Dictunary(1, "871200", "Sq Ft"));

        MinLot = HelperFunctions.ChangeDropdownUnits(MinLot, appPrefs);

        SearchDropDownAdapter MinLotadp = new SearchDropDownAdapter(this, android.R.layout.simple_list_item_1, MinLot);
        spnMinLot.setAdapter(MinLotadp);
        spnMinLot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                adapterView.setPadding(0, 2, 0, 2);
                if (i != 0) {
                    MinLotString = MinLot.get(i).Title;
                } else {
                    MinLotString = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    void RequestProperty() {
        if (PropertyList.size() > 0) {
            BindPropertyType();
        } else {
            PropertyList = new PropertyTypeLayer(this).getPropertyType();
            BindPropertyType();
        }
    }

    void BindPropertyType() {
        PropertyTypeAdapter = new PropertyTypeGridAdapter2(this, PropertyList,
                new IPropertyType() {
                    @Override
                    public void OnClick(Object obj) {
                        Boolean ISALLCCHECKED = false;
                        for (Dictunary p : PropertyList) {
                            if (p.IsSelected) {
                                ISALLCCHECKED = true;
                            } else {
                                ISALLCCHECKED = false;
                                break;
                            }
                        }
                        if (ISALLCCHECKED) {
                            chkResidential.setChecked(true);
                        } else {
                            chkResidential.setChecked(false);
                        }
                    }
                });
        gridPropertyTypeResidential.setAdapter(PropertyTypeAdapter);
        gridPropertyTypeResidential.setExpanded(true);

    }

    void RequestPropertyCommercial() {
        if (PropertyListCommercial.size() > 0) {
            BindPropertyCommercial();
        } else {
            PropertyListCommercial = new PropertyTypeLayer(this).getPropertyTypeCommercial();
            BindPropertyCommercial();
        }
    }

    void BindPropertyCommercial() {
        PropertyCommercialAdapter = new PropertyTypeGridAdapter2(this, PropertyListCommercial,
                new IPropertyType() {
                    @Override
                    public void OnClick(Object obj) {
                        Boolean ISALLCCHECKED = false;
                        for (Dictunary p : PropertyListCommercial) {
                            if (p.IsSelected) {
                                ISALLCCHECKED = true;
                            } else {
                                ISALLCCHECKED = false;
                                break;
                            }
                        }
                        if (ISALLCCHECKED) {
                            chkCommercial.setChecked(true);
                        } else {
                            chkCommercial.setChecked(false);
                        }
                    }
                });
        gridPropertyTypeCommercial.setAdapter(PropertyCommercialAdapter);
        gridPropertyTypeCommercial.setExpanded(true);
    }

    public void btn_More_Property(View v) {

        if (IsExpanded) {
            ViewGroup.LayoutParams layoutParams = gridPropertyTypeResidential
                    .getLayoutParams();
            final float height = 135;
            layoutParams.height = convertDpToPixels(height, this);
            gridPropertyTypeResidential.setLayoutParams(layoutParams);
            IsExpanded = false;
            btnMoreProperty.setText(R.string.more_property_type);

        } else {
            ViewGroup.LayoutParams layoutParams = gridPropertyTypeResidential
                    .getLayoutParams();
            final float height = 415;
            layoutParams.height = convertDpToPixels(height, this);
            gridPropertyTypeResidential.setLayoutParams(layoutParams);
            IsExpanded = true;
            btnMoreProperty.setText(R.string.less_property_type);
        }

    }

    private void bindBedBathAdapter() {

        NumberBedList.add(new NoBedBath("Any", false));
        NumberBedList.add(new NoBedBath("1+", false));
        NumberBedList.add(new NoBedBath("2+", false));
        NumberBedList.add(new NoBedBath("3+", false));
        NumberBedList.add(new NoBedBath("4+", false));
        NumberBedList.add(new NoBedBath("5+", false));
        NumberBedList.add(new NoBedBath("6+", false));
        NumberBedList.add(new NoBedBath("7+", false));


        BedRoomAdapter = new NumberofBedBathAdapter(this, NumberBedList, new IEvent() {
            @Override
            public void onClick(Object obj) {
                NoBedBath bed = (NoBedBath) obj;
                for (int i = 0; i < NumberBedList.size(); i++) {
                    NumberBedList.get(i).IsSelected = false;
                }

                bed.IsSelected = true;
                BedString = bed.Numbers;
                NoBed = stripNonDigits(bed.Numbers);
                BedRoomAdapter.notifyDataSetChanged();

            }
        });
        gridPostpropertyNoofbedrooms.setAdapter(BedRoomAdapter);

        NumberBathList.add(new NoBedBath("Any", false));
        NumberBathList.add(new NoBedBath("1+", false));
        NumberBathList.add(new NoBedBath("2+", false));
        NumberBathList.add(new NoBedBath("3+", false));
        NumberBathList.add(new NoBedBath("4+", false));
        NumberBathList.add(new NoBedBath("5+", false));
        NumberBathList.add(new NoBedBath("6+", false));
        NumberBathList.add(new NoBedBath("7+", false));


        BathRoomAdapter = new NumberofBedBathAdapter(this, NumberBathList, new IEvent() {
            @Override
            public void onClick(Object obj) {
                NoBedBath bath = (NoBedBath) obj;
                for (int i = 0; i < NumberBathList.size(); i++) {
                    NumberBathList.get(i).IsSelected = false;
                }

                bath.IsSelected = true;
                BathString = bath.Numbers;
                NoBath = stripNonDigits(bath.Numbers);
                BathRoomAdapter.notifyDataSetChanged();

            }
        });

        gridPostpropertyNoofbathrooms.setAdapter(BathRoomAdapter);
    }

    void YearSpinner() {

        Calendar c = Calendar.getInstance();
        MaxYearList.add("Select");
        for (int i = 0; i <= 100; i++) {
            MaxYearList.add(String.valueOf(c.get(Calendar.YEAR) - i));
        }
        ArrayAdapter<String> Yearadp = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, MaxYearList);
        Yearadp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMaxYearBuilt.setAdapter(Yearadp);
        spnMaxYearBuilt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        MinYearList.addAll(MaxYearList);
        MinYearList.add("Select");
        Collections.reverse(MinYearList);
        MinYearList.remove(MinYearList.size() - 1);
        ArrayAdapter<String> MinYearadp = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, MinYearList);
        Yearadp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMinYearBuilt.setAdapter(MinYearadp);
        spnMinYearBuilt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    void events() {

        switchSale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    lnrSearchSale.setVisibility(View.VISIBLE);
                } else {

                    lnrSearchSale.setVisibility(View.GONE);
                }
            }
        });

        switchRent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    lnrSearchRent.setVisibility(View.VISIBLE);
                } else {

                    lnrSearchRent.setVisibility(View.GONE);
                }
            }
        });

        btnSearchMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IsMore) {
                    IsMore = false;
                    lnrMoreSearch.setVisibility(View.GONE);
                    btnSearchMore.setText(R.string.show_more);
                } else {
                    IsMore = true;
                    lnrMoreSearch.setVisibility(View.VISIBLE);
                    btnSearchMore.setText(R.string.less);
                }

            }
        });

        chkResidential.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chkResidential.isChecked()) {
                    for (Dictunary d : PropertyList) {
                        d.IsSelected = true;
                        PropertyTypeAdapter.notifyDataSetChanged();
                    }
                } else {
                    for (Dictunary d : PropertyList) {
                        d.IsSelected = false;
                        PropertyTypeAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        chkCommercial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chkCommercial.isChecked()) {
                    for (Dictunary d : PropertyListCommercial) {
                        d.IsSelected = true;
                        PropertyCommercialAdapter.notifyDataSetChanged();
                    }
                } else {
                    for (Dictunary d : PropertyListCommercial) {
                        d.IsSelected = false;
                        PropertyCommercialAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    public void btn_filter(View view) {
        if (switchRent.isChecked() || switchSale.isChecked()) {
            List<Dictunary> AllList = new ArrayList<>();
            AllList.addAll(PropertyList);
            AllList.addAll(PropertyListCommercial);
            StringBuilder sb = new StringBuilder();
            StringBuilder protypesb = new StringBuilder();
            StringBuilder listtypesb = new StringBuilder();
            for (int i = 0; i < AllList.size(); i++) {
                if (AllList.get(i).IsSelected) {
                    sb.append(AllList.get(i).ID).append(",");
                    protypesb.append(AllList.get(i).ShortCuts).append(",");
                }
            }
            filters.PropertyTypeIdsIn = TextBoxHandler.IsNullOrEmpty(sb.toString());
            if (sb.toString().length() > 0) {
                sb.delete(sb.length() - 1, sb.length());
                protypesb.delete(protypesb.length() - 1, protypesb.length());
            }


            filters.BedRoomSize = TextBoxHandler.IsNullOrEmpty(NoBed);
            filters.BathRoomSize = TextBoxHandler.IsNullOrEmpty(NoBath);
            filters.BuiltUpAreaMinSize = TextBoxHandler.IsNullOrEmpty(MinAreaString);
            filters.BuiltUpAreaMaxSize = TextBoxHandler.IsNullOrEmpty(MaxAreaString);
            filters.MinPrice = TextBoxHandler.IsNullOrEmpty(stripNonDigits(spnMinPrice.getSelectedItem().toString()));
            filters.MaxPrice = TextBoxHandler.IsNullOrEmpty(stripNonDigits(spnMaxPrice.getSelectedItem().toString()));
            filters.LotMinSize = TextBoxHandler.IsNullOrEmpty(MinLotString);
            filters.LotMaxSize = TextBoxHandler.IsNullOrEmpty(MaxLotString);
            filters.FromBuiltYear = TextBoxHandler.IsNullOrEmpty(stripNonDigits(spnMaxYearBuilt.getSelectedItem().toString()));
            if (switchSale.isChecked() && switchRent.isChecked()) {
                filters.PropertyTransactionTypeCategoryId = "1,2";
                StringBuilder Transb = new StringBuilder();
                for (int i = 0; i < ListingTyplist.size(); i++) {
                    if (ListingTyplist.get(i).IsSelected) {
                        Transb.append(ListingTyplist.get(i).ID).append(",");
                        listtypesb.append(ListingTyplist.get(i).Name).append(",");
                    }
                }

                filters.PropertyTransactionTypeIdsIn = TextBoxHandler.IsNullOrEmpty(Transb.toString()) + "7";
                listtypesb.append("for-rent");
                Transb.delete(0, sb.length());
            } else if (switchRent.isChecked()) {
                filters.PropertyTransactionTypeCategoryId = "2";
                filters.PropertyTransactionTypeIdsIn = "7";
                listtypesb.append("for-rent");
            } else {
                StringBuilder Transb = new StringBuilder();
                for (int i = 0; i < ListingTyplist.size(); i++) {
                    if (ListingTyplist.get(i).IsSelected) {
                        Transb.append(ListingTyplist.get(i).ID).append(",");
                        listtypesb.append(ListingTyplist.get(i).Name).append(",");
                    }
                }

                if (Transb.toString().length() > 0) {
                    Transb.delete(Transb.length() - 1, Transb.length());
                    listtypesb.delete(listtypesb.length() - 1, listtypesb.length());
                }
                filters.PropertyTransactionTypeIdsIn = TextBoxHandler.IsNullOrEmpty(Transb.toString());
                Transb.delete(0, sb.length());
                filters.PropertyTransactionTypeCategoryId = "1";
            }

            prefs.setSwitchSale(switchSale.isChecked());
            prefs.setSwitchRent(switchRent.isChecked());
            prefs.setSaleMinPrice(spnMinPrice.getSelectedItemPosition());
            prefs.setSaleMax(spnMaxPrice.getSelectedItemPosition());
            prefs.setYear(spnMinYearBuilt.getSelectedItemPosition());
            prefs.setBedNo(TextBoxHandler.IsNullOrEmpty(BedString));
            prefs.setBathNo(TextBoxHandler.IsNullOrEmpty(BathString));
            prefs.setMaxYear(spnMaxYearBuilt.getSelectedItemPosition());
            prefs.setMinBuild(spnMinArea.getSelectedItemPosition());
            prefs.setMaxBuild(spnMaxArea.getSelectedItemPosition());
            prefs.setMinLot(spnMinLot.getSelectedItemPosition());
            prefs.setMaxLot(spnMaxLot.getSelectedItemPosition());

            BuildWebURL(protypesb, listtypesb);

            Intent intent = new Intent();
            intent.putExtra("FilterResults", (Serializable) filters);
            setResult(1221, intent);

            sb.delete(0, sb.length());
            protypesb.delete(0, protypesb.length());
            listtypesb.delete(0, listtypesb.length());
            finish();
        } else {
            Toast.makeText(SearchPropertyForResultActivity.this, "Please check Sale or Rent to filter properties", Toast.LENGTH_LONG).show();
        }
    }

    void BuildWebURL(StringBuilder pt_sb, StringBuilder lt_sb) {

        if (pt_sb.toString().length() > 0) {
            filters.WebFilters = filters.WebFilters + "/" + pt_sb.toString() + "_ptype";
        }
        if (lt_sb.toString().length() > 0) {
            filters.WebFilters = filters.WebFilters + "/" + lt_sb.toString() + "_ltype";
        }
        if (filters.BedRoomSize.length() > 0) {
            filters.WebFilters = filters.WebFilters + "/" + filters.BedRoomSize + "_bd";
        }
        if (filters.BathRoomSize.length() > 0) {
            filters.WebFilters = filters.WebFilters + "/" + filters.BathRoomSize + "_bt";
        }
        if (filters.BuiltUpAreaMinSize.length() > 0 || filters.BuiltUpAreaMaxSize.length() > 0) {
            filters.WebFilters = filters.WebFilters + "/" + filters.BuiltUpAreaMinSize + "-" + filters.BuiltUpAreaMaxSize + "_area";
        }
        if (filters.LotMinSize.length() > 0 || filters.LotMaxSize.length() > 0) {
            filters.WebFilters = filters.WebFilters + "/" + filters.LotMinSize + "-" + filters.LotMaxSize + "_lot";
        }
        if (filters.FromBuiltYear.length() > 0) {
            filters.WebFilters = filters.WebFilters + "/" + filters.FromBuiltYear + "-_ybuilt";
        }
        if (filters.MinPrice.length() > 0 || filters.MaxPrice.length() > 0) {
            filters.WebFilters = filters.WebFilters + "/" + filters.MinPrice + "-" + filters.MaxPrice + "_price";
        }
        Log.v("esty", "WEBURL: " + filters.WebFilters);
    }


    public String stripNonDigits(final CharSequence input) {
        final StringBuilder sb = new StringBuilder(
                input.length());
        for (int i = 0; i < input.length(); i++) {
            final char c = input.charAt(i);
            if (c > 47 && c < 58) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        setResult(0, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_reset) {
            prefs.clear();

            switchSale.setChecked(true);
            switchRent.setChecked(true);
            chkOwner.setChecked(true);
            chkAgent.setChecked(true);
            chkDeveloper.setChecked(true);
            chkBrokerage.setChecked(true);
            chkCommercial.setChecked(true);
            chkResidential.setChecked(true);
            spnMinPrice.setSelection(0);
            spnMaxPrice.setSelection(0);
            spnRentMinPrice.setSelection(0);
            spnRentMaxPrice.setSelection(0);
            spnMinYearBuilt.setSelection(0);
            spnMaxYearBuilt.setSelection(0);
            spnMinArea.setSelection(0);
            spnMaxArea.setSelection(0);
            spnMinLot.setSelection(0);
            spnMaxLot.setSelection(0);
            NoBed = "";
            NoBath = "";
            BathString = "";
            BedString = "";

            for (int i = 0; i < ListingTyplist.size(); i++) {
                ListingTyplist.get(i).IsSelected = true;
            }
            CategoryAdapter.notifyDataSetChanged();

            for (int i = 0; i < PropertyList.size(); i++) {
                PropertyList.get(i).IsSelected = true;
            }
            PropertyTypeAdapter.notifyDataSetChanged();

            for (int i = 0; i < RentTypeList.size(); i++) {
                RentTypeList.get(i).IsSelected = true;
            }
            RentTypeAdapter.notifyDataSetChanged();

            for (int i = 0; i < NumberBedList.size(); i++) {
                NumberBedList.get(i).IsSelected = false;

            }
            BedRoomAdapter.notifyDataSetChanged();

            for (int i = 0; i < NumberBathList.size(); i++) {
                NumberBathList.get(i).IsSelected = false;
            }
            BathRoomAdapter.notifyDataSetChanged();


        }

        if (id == android.R.id.home) {
            Intent intent = new Intent();
            setResult(0, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filters, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
