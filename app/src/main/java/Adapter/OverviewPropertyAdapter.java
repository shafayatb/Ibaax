package Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibaax.com.ibaax.R;

import java.util.List;

import Entity.OverViewProperty;
import Event.IEvent;

/**
 * Created by iBaax on 3/16/16.
 */
public class OverviewPropertyAdapter extends BaseAdapter {

    List<OverViewProperty> list;
    Context context;
    IEvent listener;

    public OverviewPropertyAdapter(Context context, List<OverViewProperty> list, IEvent listener) {
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
                v = inflater.inflate(R.layout.row_propertyoverview, null);
                holder = new ViewHolder();
                holder.imgIcon = (ImageView) v.findViewById(R.id.img_overview_icon);
                holder.lblTitle = (TextView) v.findViewById(R.id.lbl_overview_titile);
                holder.lblValue = (TextView) v.findViewById(R.id.lbl_overview_value);
                v.setTag(holder);

            } else {
                holder = (ViewHolder) v.getTag();
            }

            if (i == 8) {
                holder.imgIcon.setImageResource(list.get(i).PropertyIcon);
                holder.lblTitle.setText(list.get(i).OverViewTitle);
                holder.lblValue.setText(list.get(i).OverViewValue);
                if (list.get(i).OverViewValue.equals("Published")) {
                    holder.lblValue.setTextColor(Color.parseColor("#00CC00"));
                } else {
                    holder.lblValue.setTextColor(Color.parseColor("#CC0000"));
                }
            } else {
                holder.imgIcon.setImageResource(list.get(i).PropertyIcon);
                holder.lblTitle.setText(list.get(i).OverViewTitle);
                holder.lblValue.setText(list.get(i).OverViewValue);
            }

        } catch (Exception ex) {
            Log.v("error", ex.getMessage());
        }

        return v;
    }

    static class ViewHolder {
        public ImageView imgIcon;
        public TextView lblTitle;
        public TextView lblValue;

    }
}
