package Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.ibaax.com.ibaax.R;

import java.util.List;

import Entity.NoBedBath;
import Event.IEvent;

/**
 * Created by iBaax on 2/18/16.
 */
public class NumberofBedBathAdapter extends BaseAdapter {

    Context context;
    List<NoBedBath> NumberList;
    IEvent listener;

    public NumberofBedBathAdapter(Context context, List<NoBedBath> NumberList, IEvent listener) {
        this.context = context;
        this.NumberList = NumberList;
        this.listener = listener;
    }


    @Override
    public int getCount() {
        return NumberList.size();
    }

    @Override
    public Object getItem(int i) {
        return NumberList.get(i);
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
                v = inflater.inflate(R.layout.row_numberbuttons, null);
                holder = new ViewHolder();
                holder.btnNumber = (Button) v
                        .findViewById(R.id.btn_numberof_bedbath);


                v.setTag(holder);

            } else {
                holder = (ViewHolder) v.getTag();
            }

            if (NumberList.get(i).IsSelected) {

                holder.btnNumber.setBackgroundColor(Color.parseColor("#56698F"));
                holder.btnNumber.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                holder.btnNumber.setBackgroundResource(R.drawable.btn_white_blackborder);
                holder.btnNumber.setTextColor(Color.parseColor("#646464"));
            }

            holder.btnNumber.setText(NumberList.get(i).Numbers);

            holder.btnNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(NumberList.get(index));
                }
            });


        } catch (Exception e) {

        }

        return v;
    }


    static class ViewHolder {
        public Button btnNumber;


    }

}