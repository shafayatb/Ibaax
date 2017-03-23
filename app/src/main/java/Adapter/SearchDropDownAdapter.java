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
 * Created by iBaax on 6/14/16.
 */
public class SearchDropDownAdapter extends ArrayAdapter<Dictunary> {

    Context context;
    List<Dictunary> list;

    public SearchDropDownAdapter(Context context, int resource, List<Dictunary> objects) {
        super(context, resource, objects);
        this.context = context;
        this.list = objects;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, android.R.layout.simple_spinner_dropdown_item, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, android.R.layout.simple_list_item_1, parent);
    }

    public View getCustomView(int position, int layout, ViewGroup parent) {
        // TODO Auto-generated method stub
        //return super.getView(position, convertView, parent);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(layout, parent, false);
        TextView label = (TextView) row.findViewById(android.R.id.text1);
        label.setText(list.get(position).Title + " " + list.get(position).Name);
        return row;
    }


}
