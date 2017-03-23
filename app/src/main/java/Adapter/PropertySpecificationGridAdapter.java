package Adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ibaax.com.ibaax.R;

import java.util.List;

import Entity.PropertySpecificationData;
import Event.IEvent;
import Plugins.TextBoxHandler;

/**
 * Created by iBaax on 2/27/16.
 */
public class PropertySpecificationGridAdapter extends BaseAdapter {

    List<PropertySpecificationData> list;
    Context context;
    IEvent listener;

    public PropertySpecificationGridAdapter(Context context, List<PropertySpecificationData> list) {

        this.context = context;
        this.list = list;
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
        try {
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.row_grid_property_secification, null);
                holder = new ViewHolder();

                holder.lnrCheckSpecification = (LinearLayout) v.findViewById(R.id.lnr_specificationcheckbox);
                holder.lnrNameSpecification = (LinearLayout) v.findViewById(R.id.lnr_specificationedittext);
                holder.lnrNumberSpecification = (LinearLayout) v.findViewById(R.id.lnr_specificationnumbertext);
                holder.chkSpecification = (CheckBox) v.findViewById(R.id.chk_specification);
                holder.lblSpecificatioName = (TextView) v.findViewById(R.id.lbl_specification);
                holder.txtSpecificationName = (EditText) v.findViewById(R.id.txt_specification);
                holder.lblSpecificationNumber = (TextView) v.findViewById(R.id.lbl_specificationnumber);
                holder.txtSpecificationNumber = (EditText) v.findViewById(R.id.txt_specificationnumber);
                v.setTag(holder);

            } else {
                holder = (ViewHolder) v.getTag();
            }
            final int position = i;

            if (list.get(i).InputDataType == 4) {
                holder.lnrCheckSpecification.setVisibility(View.VISIBLE);
                holder.chkSpecification.setText(list.get(i).SpecificationName);
                if (list.get(i).IsChecked) {
                    holder.chkSpecification.setChecked(true);
                    list.get(i).IsChecked = true;
                } else {
                    holder.chkSpecification.setChecked(false);
                    list.get(i).IsChecked = false;
                }
                holder.chkSpecification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.chkSpecification.isChecked()) {
                            list.get(position).IsChecked = true;
                            list.get(position).Data = "true";
                        } else {
                            list.get(position).IsChecked = false;
                            list.get(position).Data = "";
                        }
                        Log.v("esty","Position "+String.valueOf(position)+" Value "+list.get(position).Data);
                    }
                });
            } else {
                holder.lnrCheckSpecification.setVisibility(View.GONE);
            }
            if (list.get(i).InputDataType == 1) {
                holder.lnrNameSpecification.setVisibility(View.VISIBLE);
                holder.lblSpecificatioName.setText(list.get(i).SpecificationName);
                holder.txtSpecificationName.setText(TextBoxHandler.IsNullOrEmpty(list.get(i).Data));
                holder.txtSpecificationName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (holder.txtSpecificationName.getText().toString().length() > 0) {
                            list.get(position).Data = holder.txtSpecificationName.getText().toString();
                        } else {
                            list.get(position).Data = "";
                        }
                        Log.v("esty","Position "+String.valueOf(position)+" Value "+list.get(position).Data);
                    }
                });

            } else {
                holder.lnrNameSpecification.setVisibility(View.GONE);
            }
            if (list.get(i).InputDataType == 2) {
                holder.lnrNumberSpecification.setVisibility(View.VISIBLE);
                holder.lblSpecificationNumber.setText(list.get(i).SpecificationName);
                holder.txtSpecificationNumber.setText(TextBoxHandler.IsNullOrEmpty(list.get(i).Data));
                holder.txtSpecificationNumber.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (holder.txtSpecificationNumber.getText().toString().length() > 0) {
                            list.get(position).Data = holder.txtSpecificationNumber.getText().toString();
                        } else {
                            list.get(position).Data = "";
                        }
                        Log.v("esty","Position "+String.valueOf(position)+" Value "+list.get(position).Data);
                    }
                });

            } else {
                holder.lnrNumberSpecification.setVisibility(View.GONE);
            }


            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //   listener.onClick(list.get(index));

                }
            });


        } catch (Exception ex) {
            Log.v("error", ex.getMessage());
        }

        return v;
    }

    static class ViewHolder {
        public CheckBox chkSpecification;
        public TextView lblSpecificatioName;
        public EditText txtSpecificationName;
        public TextView lblSpecificationNumber;
        public EditText txtSpecificationNumber;
        public LinearLayout lnrCheckSpecification, lnrNameSpecification, lnrNumberSpecification;
    }
}
