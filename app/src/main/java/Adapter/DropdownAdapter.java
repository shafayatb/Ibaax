package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ibaax.com.ibaax.R;

import java.util.List;

import Entity.Dictunary;

/**
 * Created by iBaax on 2/8/16.
 */
public class DropdownAdapter extends ArrayAdapter<Dictunary> {
    Context context;
    List<Dictunary> list;
    public DropdownAdapter(Context context, int textViewResourceId,
                                 List<Dictunary> list) {
        super(context, textViewResourceId, list);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.list=list;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        // TODO Auto-generated method stub
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        //return super.getView(position, convertView, parent);

        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row=inflater.inflate(R.layout.row_postproperty_dropdown, parent, false);
        TextView label=(TextView)row.findViewById(R.id.lbl_searchitem_text);
        label.setText(list.get(position).Title);
        return row;
    }
}

