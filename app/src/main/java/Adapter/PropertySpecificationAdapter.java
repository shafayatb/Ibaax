package Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibaax.com.ibaax.R;

import java.util.List;

import Entity.Agent;
import Entity.PropertySpecification;
import Event.IEvent;

/**
 * Created by S.R Rain on 1/19/2016.
 */
public class PropertySpecificationAdapter extends BaseAdapter {

    List<PropertySpecification> list;
    Context context;
    IEvent listener;

    public PropertySpecificationAdapter(Context context, List<PropertySpecification>list){

        this.context=context;
        this.list=list;



    }


    static class ViewHolder{

        public TextView lblName;

        ImageView profile;

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
            if (v == null)
            {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.row_property_specification, null);
                holder = new ViewHolder();

                holder.lblName = (TextView) v.findViewById(R.id.lbl_row_property_spification_name);

                v.setTag(holder);

            } else
            {
                holder = (ViewHolder) v.getTag();
            }


            final  int position=i;
            if(list.get(i).InputDataType==4) {
                holder.lblName.setText(list.get(position).SpecificationName);
            }else {
                holder.lblName.setText(list.get(position).Data);
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
}
