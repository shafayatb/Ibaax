package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import Entity.Dictunary;

/**
 * Created by iBaax on 4/12/16.
 */
public class DropdownSettingsAdapter extends ArrayAdapter<Dictunary> {
    Context context;
    List<Dictunary> list;

    public DropdownSettingsAdapter(Context context, int textViewResourceId,
                                   List<Dictunary> list) {
        super(context, textViewResourceId, list);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.list = list;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        TextView label = (TextView) row.findViewById(android.R.id.text1);
        label.setText(list.get(position).Name);
        return row;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        //return super.getView(position, convertView, parent);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        TextView label = (TextView) row.findViewById(android.R.id.text1);
        label.setText(list.get(position).Name);
        return row;
    }
}
