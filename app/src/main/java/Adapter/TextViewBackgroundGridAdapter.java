package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ibaax.com.ibaax.R;

import java.util.List;

/**
 * Created by iBaax on 4/4/16.
 */
public class TextViewBackgroundGridAdapter extends BaseAdapter {

    List<String> list;
    Context context;

    public TextViewBackgroundGridAdapter(Context context, List<String> list) {
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
        Viewholder holder;
        final int index = i;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.row_textview_background, null);
            holder = new Viewholder();
            holder.lblText = (TextView) v.findViewById(R.id.lbl_textView_background);
            v.setTag(holder);
        } else {
            holder = (Viewholder) v.getTag();
        }

        holder.lblText.setText(list.get(i));


        return v;
    }

    class Viewholder {
        public TextView lblText;
    }
}
