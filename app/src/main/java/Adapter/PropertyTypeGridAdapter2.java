package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.ibaax.com.ibaax.R;

import java.util.List;

import Entity.Dictunary;
import Event.IPropertyType;

/**
 * Created by iBaax on 4/25/16.
 */
public class PropertyTypeGridAdapter2 extends BaseAdapter {

    Context context;
    List<Dictunary> PropertyList;
    IPropertyType listener;

    public PropertyTypeGridAdapter2(Context context, List<Dictunary> PropertyList, IPropertyType listener) {
        this.context = context;
        this.PropertyList = PropertyList;
        this.listener = listener;
    }


    @Override
    public int getCount() {
        return PropertyList.size();
    }

    @Override
    public Object getItem(int i) {
        return PropertyList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        final ViewHolder holder;
        final int index = i;

        try {
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.row_checkbox, null);
                holder = new ViewHolder();
                holder.chkbox = (CheckBox) v
                        .findViewById(R.id.chk_box);


                v.setTag(holder);

            } else {
                holder = (ViewHolder) v.getTag();
            }


            holder.chkbox.setText(PropertyList.get(i).Title);
            if (PropertyList.get(i).IsSelected) {
                holder.chkbox.setChecked(true);
            } else {
                holder.chkbox.setChecked(false);
            }
            holder.chkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!PropertyList.get(index).IsSelected) {
                        PropertyList.get(index).IsSelected = true;
                    } else {
                        PropertyList.get(index).IsSelected = false;
                    }
                    listener.OnClick(PropertyList.get(index));
                }
            });


        } catch (Exception e) {

        }

        return v;
    }


    static class ViewHolder {
        public CheckBox chkbox;


    }

}
